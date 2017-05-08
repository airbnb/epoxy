package com.airbnb.epoxy;

import com.google.auto.service.AutoService;

import java.util.ArrayList;
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
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import static com.airbnb.epoxy.ConfigManager.PROCESSOR_IMPLICITLY_ADD_AUTO_MODELS;
import static com.airbnb.epoxy.ConfigManager.PROCESSOR_OPTION_VALIDATE_MODEL_USAGE;

/**
 * Looks for {@link EpoxyAttribute} annotations and generates a subclass for all classes that have
 * those attributes. The generated subclass includes setters, getters, equals, and hashcode for the
 * given field. Any constructors on the original class are duplicated. Abstract classes are ignored
 * since generated classes would have to be abstract in order to guarantee they compile, and that
 * reduces their usefulness and doesn't make as much sense to support.
 */
@AutoService(Processor.class)
public class EpoxyProcessor extends AbstractProcessor {

  private final Map<String, String> testOptions;
  private Filer filer;
  private Messager messager;
  private Elements elementUtils;
  private Types typeUtils;

  private LayoutResourceProcessor layoutResourceProcessor;
  private ConfigManager configManager;
  private DataBindingModuleLookup dataBindingModuleLookup;
  private final ErrorLogger errorLogger = new ErrorLogger();
  private GeneratedModelWriter modelWriter;
  private ControllerProcessor controllerProcessor;
  private DataBindingProcessor dataBindingProcessor;
  private final List<GeneratedModelInfo> generatedModels = new ArrayList<>();

  public EpoxyProcessor() {
    this(Collections.<String, String>emptyMap());
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
    options.put(PROCESSOR_IMPLICITLY_ADD_AUTO_MODELS, "true");
    return new EpoxyProcessor(options);
  }

  @Override
  public synchronized void init(ProcessingEnvironment processingEnv) {
    super.init(processingEnv);
    filer = processingEnv.getFiler();
    messager = processingEnv.getMessager();
    elementUtils = processingEnv.getElementUtils();
    typeUtils = processingEnv.getTypeUtils();

    layoutResourceProcessor =
        new LayoutResourceProcessor(processingEnv, errorLogger, elementUtils, typeUtils);
    configManager =
        new ConfigManager(!testOptions.isEmpty() ? testOptions : processingEnv.getOptions(),
            elementUtils);

    dataBindingModuleLookup =
        new DataBindingModuleLookup(elementUtils, typeUtils, errorLogger, layoutResourceProcessor);

    modelWriter =
        new GeneratedModelWriter(filer, typeUtils, errorLogger,
            layoutResourceProcessor,
            configManager, dataBindingModuleLookup);

    controllerProcessor = new ControllerProcessor(filer, elementUtils, errorLogger, configManager);

    dataBindingProcessor =
        new DataBindingProcessor(elementUtils, typeUtils, errorLogger, configManager,
            layoutResourceProcessor, dataBindingModuleLookup, modelWriter);
  }

  @Override
  public Set<String> getSupportedAnnotationTypes() {
    Set<String> types = new LinkedHashSet<>();

    types.add(EpoxyModelClass.class.getCanonicalName());
    types.add(EpoxyAttribute.class.getCanonicalName());
    types.add(PackageEpoxyConfig.class.getCanonicalName());
    types.add(AutoModel.class.getCanonicalName());
    types.add(EpoxyDataBindingLayouts.class.getCanonicalName());

    types.add(ClassNames.LITHO_ANNOTATION_LAYOUT_SPEC.reflectionName());

    return types;
  }

  @Override
  public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latestSupported();
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    errorLogger.logErrors(configManager.processConfigurations(roundEnv));

    ModelProcessor modelProcessor = new ModelProcessor(messager,
        elementUtils, typeUtils, configManager, errorLogger,
        modelWriter);

    generatedModels.addAll(modelProcessor.processModels(roundEnv));

    dataBindingProcessor.process(roundEnv);

    LithoSpecProcessor lithoSpecProcessor = new LithoSpecProcessor(
        elementUtils, typeUtils, configManager, errorLogger, modelWriter);

    generatedModels.addAll(lithoSpecProcessor.processSpecs(roundEnv));

    controllerProcessor.process(roundEnv);

    if (roundEnv.processingOver()) {
      generatedModels.addAll(dataBindingProcessor.resolveDataBindingClassesAndWriteJava());

      // This must be done after all generated model info is collected
      controllerProcessor.resolveGeneratedModelsAndWriteJava(generatedModels);

      // We wait until the very end to log errors so that all the generated classes are still
      // created.
      // Otherwise the compiler error output is clogged with lots of errors from the generated
      // classes  not existing, which makes it hard to see the actual errors.
      errorLogger.writeExceptions(messager);
    }

    // Let any other annotation processors use our annotations if they want to
    return false;
  }
}
