package com.airbnb.epoxy;

import com.google.auto.service.AutoService;

import java.lang.annotation.Annotation;
import java.util.LinkedHashSet;
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

/**
 * Looks for {@link EpoxyAttribute} annotations and generates a subclass for all classes that have
 * those attributes. The generated subclass includes setters, getters, equals, and hashcode for the
 * given field. Any constructors on the original class are duplicated. Abstract classes are ignored
 * since generated classes would have to be abstract in order to guarantee they compile, and that
 * reduces their usefulness and doesn't make as much sense to support.
 */
@AutoService(Processor.class)
public class EpoxyProcessor extends AbstractProcessor {

  private Filer filer;
  private Messager messager;
  private Elements elementUtils;
  private Types typeUtils;

  private ResourceProcessor resourceProcessor;
  private ConfigManager configManager;
  private final ErrorLogger errorLogger = new ErrorLogger();

  @Override
  public synchronized void init(ProcessingEnvironment processingEnv) {
    super.init(processingEnv);
    filer = processingEnv.getFiler();
    messager = processingEnv.getMessager();
    elementUtils = processingEnv.getElementUtils();
    typeUtils = processingEnv.getTypeUtils();

    resourceProcessor = new ResourceProcessor(processingEnv, elementUtils, typeUtils);
    configManager = new ConfigManager(elementUtils);
  }

  @Override
  public Set<String> getSupportedAnnotationTypes() {
    Set<String> types = new LinkedHashSet<>();
    for (Class<? extends Annotation> annotation : getSupportedAnnotations()) {
      types.add(annotation.getCanonicalName());
    }
    return types;
  }

  static Set<Class<? extends Annotation>> getSupportedAnnotations() {
    Set<Class<? extends Annotation>> annotations = new LinkedHashSet<>();

    annotations.add(EpoxyModelClass.class);
    annotations.add(EpoxyAttribute.class);
    annotations.add(PackageEpoxyConfig.class);
    annotations.add(AutoModel.class);

    return annotations;
  }

  @Override
  public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latestSupported();
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    errorLogger.logErrors(configManager.processConfigurations(roundEnv));
    resourceProcessor.processorResources(roundEnv);

    ModelProcessor modelProcessor = new ModelProcessor(filer, messager,
        elementUtils, typeUtils, resourceProcessor, configManager, errorLogger);
    modelProcessor.processModels(roundEnv);

    new AdapterProcessor(filer, elementUtils, errorLogger)
        .process(roundEnv, modelProcessor.getGeneratedModels());

    if (roundEnv.processingOver()) {

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
