package com.airbnb.epoxy;

import com.sun.source.util.Trees;

import net.ltgt.gradle.incap.IncrementalAnnotationProcessor;
import net.ltgt.gradle.incap.IncrementalAnnotationProcessorType;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import static com.airbnb.epoxy.ConfigManager.PROCESSOR_OPTION_DISABLE_KOTLIN_EXTENSION_GENERATION;
import static com.airbnb.epoxy.ConfigManager.PROCESSOR_OPTION_IMPLICITLY_ADD_AUTO_MODELS;
import static com.airbnb.epoxy.ConfigManager.PROCESSOR_OPTION_REQUIRE_ABSTRACT_MODELS;
import static com.airbnb.epoxy.ConfigManager.PROCESSOR_OPTION_REQUIRE_HASHCODE;
import static com.airbnb.epoxy.ConfigManager.PROCESSOR_OPTION_VALIDATE_MODEL_USAGE;
import static java.util.Collections.unmodifiableSet;

/**
 * Looks for {@link EpoxyAttribute} annotations and generates a subclass for all classes that have
 * those attributes. The generated subclass includes setters, getters, equals, and hashcode for the
 * given field. Any constructors on the original class are duplicated. Abstract classes are ignored
 * since generated classes would have to be abstract in order to guarantee they compile, and that
 * reduces their usefulness and doesn't make as much sense to support.
 */
@IncrementalAnnotationProcessor(IncrementalAnnotationProcessorType.DYNAMIC)
public class EpoxyProcessor extends AbstractProcessor {

  private final Map<String, String> testOptions;
  private Messager messager;
  private Elements elementUtils;
  private Types typeUtils;
  private Trees trees;

  private ConfigManager configManager;
  private final ErrorLogger errorLogger = new ErrorLogger();
  private ControllerProcessor controllerProcessor;
  private DataBindingProcessor dataBindingProcessor;
  private final List<GeneratedModelInfo> generatedModels = new ArrayList<>();
  private ModelProcessor modelProcessor;
  private LithoSpecProcessor lithoSpecProcessor;
  private KotlinModelBuilderExtensionWriter kotlinExtensionWriter;
  private ModelViewProcessor modelViewProcessor;

  public EpoxyProcessor() {
    this(Collections.emptyMap());
  }

  /**
   * Constructor to use for tests to pass annotation processor options since we can't get them from
   * the build.gradle
   */
  public EpoxyProcessor(Map<String, String> options) {
    testOptions = options;
  }

  /** For testing. */
  public static EpoxyProcessor withNoValidation() {
    HashMap<String, String> options = new HashMap<>();
    options.put(PROCESSOR_OPTION_VALIDATE_MODEL_USAGE, "false");
    return new EpoxyProcessor(options);
  }

  /** For testing. */
  public static EpoxyProcessor withImplicitAdding() {
    HashMap<String, String> options = new HashMap<>();
    options.put(PROCESSOR_OPTION_IMPLICITLY_ADD_AUTO_MODELS, "true");
    return new EpoxyProcessor(options);
  }

  @Override
  public synchronized void init(ProcessingEnvironment processingEnv) {
    super.init(processingEnv);
    Filer filer = processingEnv.getFiler();
    messager = processingEnv.getMessager();
    elementUtils = processingEnv.getElementUtils();
    typeUtils = processingEnv.getTypeUtils();

    try {
      trees = Trees.instance(processingEnv);
    } catch (IllegalArgumentException ignored) {
      try {
        // Get original ProcessingEnvironment from Gradle-wrapped one or KAPT-wrapped one.
        // In Kapt, its field is called "delegate". In Gradle's, it's called "processingEnv"
        for (Field field : processingEnv.getClass().getDeclaredFields()) {
          if (field.getName().equals("delegate") || field.getName().equals("processingEnv")) {
            field.setAccessible(true);
            ProcessingEnvironment javacEnv = (ProcessingEnvironment) field.get(processingEnv);
            trees = Trees.instance(javacEnv);
            break;
          }
        }
      } catch (Throwable ignored2) {
      }
    }

    ResourceProcessor resourceProcessor =
        new ResourceProcessor(trees, errorLogger, elementUtils, typeUtils);

    configManager =
        new ConfigManager(!testOptions.isEmpty() ? testOptions : processingEnv.getOptions(),
            elementUtils, typeUtils);

    DataBindingModuleLookup dataBindingModuleLookup =
        new DataBindingModuleLookup(elementUtils, typeUtils, errorLogger, resourceProcessor);

    GeneratedModelWriter modelWriter = new GeneratedModelWriter(filer, typeUtils, errorLogger,
        resourceProcessor,
        configManager, dataBindingModuleLookup, elementUtils);

    controllerProcessor = new ControllerProcessor(filer, elementUtils, typeUtils, errorLogger,
        configManager);

    dataBindingProcessor =
        new DataBindingProcessor(elementUtils, typeUtils, errorLogger, configManager,
            resourceProcessor, dataBindingModuleLookup, modelWriter);

    modelProcessor = new ModelProcessor(
        elementUtils, typeUtils, configManager, errorLogger,
        modelWriter);

    modelViewProcessor = new ModelViewProcessor(
        elementUtils, typeUtils, configManager, errorLogger,
        modelWriter, resourceProcessor);

    lithoSpecProcessor = new LithoSpecProcessor(
        elementUtils, typeUtils, errorLogger, modelWriter);

    kotlinExtensionWriter = new KotlinModelBuilderExtensionWriter(processingEnv);
  }

  @Override
  public Set<String> getSupportedAnnotationTypes() {
    Set<String> types = new LinkedHashSet<>();

    types.add(EpoxyModelClass.class.getCanonicalName());
    types.add(EpoxyAttribute.class.getCanonicalName());
    types.add(PackageEpoxyConfig.class.getCanonicalName());
    types.add(AutoModel.class.getCanonicalName());
    types.add(EpoxyDataBindingLayouts.class.getCanonicalName());
    types.add(EpoxyDataBindingPattern.class.getCanonicalName());
    types.add(ModelView.class.getCanonicalName());
    types.add(PackageModelViewConfig.class.getCanonicalName());
    types.add(TextProp.class.getCanonicalName());
    types.add(CallbackProp.class.getCanonicalName());

    types.add(ClassNames.LITHO_ANNOTATION_LAYOUT_SPEC.reflectionName());

    return types;
  }

  @Override
  public Set<String> getSupportedOptions() {
    Set<String> options = new LinkedHashSet<>();
    options.add(PROCESSOR_OPTION_IMPLICITLY_ADD_AUTO_MODELS);
    options.add(PROCESSOR_OPTION_VALIDATE_MODEL_USAGE);
    options.add(PROCESSOR_OPTION_REQUIRE_ABSTRACT_MODELS);
    options.add(PROCESSOR_OPTION_REQUIRE_HASHCODE);
    options.add(PROCESSOR_OPTION_DISABLE_KOTLIN_EXTENSION_GENERATION);
    if (trees != null) {
      IncrementalAnnotationProcessorType incapType = IncrementalAnnotationProcessorType.ISOLATING;
      if (!configManager.disableKotlinExtensionGeneration()) {
        incapType = IncrementalAnnotationProcessorType.AGGREGATING;
      }
      options.add(incapType.getProcessorOption());
    }
    return unmodifiableSet(options);
  }

  @Override
  public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latestSupported();
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    try {
      processRound(roundEnv);
    } catch (Exception e) {
      errorLogger.logError(e);
    }

    if (roundEnv.processingOver()) {
      // We wait until the very end to log errors so that all the generated classes are still
      // created.
      // Otherwise the compiler error output is clogged with lots of errors from the generated
      // classes  not existing, which makes it hard to see the actual errors.

      validateAttributesImplementHashCode(generatedModels);
      errorLogger.writeExceptions(messager);
    }

    // Let any other annotation processors use our annotations if they want to
    return false;
  }

  private void processRound(RoundEnvironment roundEnv) {
    errorLogger.logErrors(configManager.processConfigurations(roundEnv));

    generatedModels.addAll(modelProcessor.processModels(roundEnv));

    generatedModels.addAll(dataBindingProcessor.process(roundEnv));

    generatedModels.addAll(lithoSpecProcessor.processSpecs(roundEnv));

    generatedModels.addAll(modelViewProcessor.process(roundEnv, generatedModels));

    controllerProcessor.process(roundEnv);

    // TODO: (eli_hart 8/23/17) don't wait until round over?
    if (roundEnv.processingOver() && !configManager.disableKotlinExtensionGeneration()) {
      kotlinExtensionWriter.generateExtensionsForModels(generatedModels);
    }

    if (controllerProcessor.hasControllersToGenerate()
        && (!areModelsWaitingToWrite() || roundEnv.processingOver())) {
      // This must be done after all generated model info is collected so we must wait until
      // databinding is resolved.
      // However, if there was an error with the databinding resolution we can at least try to
      // finish writing the controllers before processing ends
      controllerProcessor.resolveGeneratedModelsAndWriteJava(generatedModels);
    }
  }

  private boolean areModelsWaitingToWrite() {
    return dataBindingProcessor.hasModelsToWrite()
        || modelProcessor.hasModelsToWrite()
        || modelViewProcessor.hasModelsToWrite();
  }

  private void validateAttributesImplementHashCode(
      Collection<GeneratedModelInfo> generatedClasses) {
    HashCodeValidator hashCodeValidator = new HashCodeValidator(typeUtils, elementUtils);

    for (GeneratedModelInfo generatedClass : generatedClasses) {
      for (AttributeInfo attributeInfo : generatedClass.getAttributeInfo()) {
        if (configManager.requiresHashCode(attributeInfo)
            && attributeInfo.getUseInHash()
            && !attributeInfo.getIgnoreRequireHashCode()) {

          try {
            hashCodeValidator.validate(attributeInfo);
          } catch (EpoxyProcessorException e) {
            errorLogger.logError(e);
          }
        }
      }
    }
  }
}
