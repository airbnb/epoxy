package com.airbnb.epoxy;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;

/**
 * Used to process {@link com.airbnb.epoxy.EpoxyConfig} annotations and specify configuration
 * details to the annotation processor
 */
class Configuration {

  final boolean requireHashCode;
  final boolean requireAbstractModels;

  private Configuration(boolean requireHashCode, boolean requireAbstractModels) {
    this.requireHashCode = requireHashCode;
    this.requireAbstractModels = requireAbstractModels;
  }

  static Configuration forDefaults() {
    return new Configuration(
        EpoxyConfig.REQUIRE_HASHCODE_DEFAULT,
        EpoxyConfig.REQUIRE_ABSTRACT_MODELS
    );
  }

  static Configuration create(RoundEnvironment roundEnv) throws EpoxyProcessorException {
    Set<? extends Element> configElements =
        roundEnv.getElementsAnnotatedWith(EpoxyConfig.class);

    if (configElements.isEmpty()) {
      return forDefaults();
    }

    validateOnlyOneConfig(configElements);

    EpoxyConfig configAnnotation =
        configElements.iterator().next().getAnnotation(EpoxyConfig.class);

    return new Configuration(
        configAnnotation.requireHashCode(),
        configAnnotation.requireAbstractModels()
    );
  }

  private static void validateOnlyOneConfig(Set<? extends Element> configElements)
      throws EpoxyProcessorException {
    if (configElements.size() > 1) {
      List<String> classNamesWithConfigs = new ArrayList<>();
      for (Element configElement : configElements) {
        classNamesWithConfigs.add(configElement.getSimpleName().toString());
      }

      throw new EpoxyProcessorException(
          "Epoxy config can only be used once per project. Exists in: " + classNamesWithConfigs);
    }
  }
}
