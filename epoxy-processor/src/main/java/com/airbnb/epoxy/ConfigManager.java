package com.airbnb.epoxy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

import static com.airbnb.epoxy.Utils.buildEpoxyException;

/** Manages configuration settings for different packages. */
class ConfigManager {
  static final String PROCESSOR_OPTION_VALIDATE_MODEL_USAGE = "validateEpoxyModelUsage";
  static final String PROCESSOR_OPTION_REQUIRE_HASHCODE = "requireHashCodeInEpoxyModels";
  static final String PROCESSOR_OPTION_REQUIRE_ABSTRACT_MODELS = "requireAbstractEpoxyModels";
  static final String PROCESSOR_IMPLICITLY_ADD_AUTO_MODELS = "implicitlyAddAutoModels";

  private static final PackageConfigSettings
      DEFAULT_PACKAGE_CONFIG_SETTINGS = PackageConfigSettings.forDefaults();
  private final Map<String, PackageConfigSettings> configurationMap = new HashMap<>();
  private final Elements elementUtils;
  private final boolean validateModelUsage;
  private final boolean globalRequireHashCode;
  private final boolean globalRequireAbstractModels;
  private final boolean globalImplicitlyAddAutoModels;

  ConfigManager(Map<String, String> options, Elements elementUtils) {
    this.elementUtils = elementUtils;
    validateModelUsage = getBooleanOption(options, PROCESSOR_OPTION_VALIDATE_MODEL_USAGE, true);

    globalRequireHashCode = getBooleanOption(options, PROCESSOR_OPTION_REQUIRE_HASHCODE,
        PackageEpoxyConfig.REQUIRE_HASHCODE_DEFAULT);

    globalRequireAbstractModels =
        getBooleanOption(options, PROCESSOR_OPTION_REQUIRE_ABSTRACT_MODELS,
            PackageEpoxyConfig.REQUIRE_ABSTRACT_MODELS_DEFAULT);

    globalImplicitlyAddAutoModels =
        getBooleanOption(options, PROCESSOR_IMPLICITLY_ADD_AUTO_MODELS,
            PackageEpoxyConfig.IMPLICITLY_ADD_AUTO_MODELS_DEFAULT);
  }

  private static boolean getBooleanOption(Map<String, String> options, String option,
      boolean defaultValue) {
    String value = options.get(option);
    if (value == null) {
      return defaultValue;
    }

    return Boolean.valueOf(value);
  }

  /**
   * If true, Epoxy models added to an EpoxyController will be
   * validated at run time to make sure they are properly used.
   * <p>
   * By default this is true, and it is highly recommended to enable it to prevent accidental misuse
   * of your models. However, you may want to disable this for production builds to avoid the
   * overhead of the runtime validation code.
   * <p>
   * Using a debug build flag is a great way to do this.
   */
  List<Exception> processConfigurations(RoundEnvironment roundEnv) {

    Set<? extends Element> annotatedElements =
        roundEnv.getElementsAnnotatedWith(PackageEpoxyConfig.class);

    List<Exception> errors = new ArrayList<>();

    for (Element element : annotatedElements) {
      String packageName = elementUtils.getPackageOf(element).getQualifiedName().toString();

      if (configurationMap.containsKey(packageName)) {
        errors.add(buildEpoxyException(
            "Only one Epoxy configuration annotation is allowed per package (%s)",
            packageName));
      }

      PackageEpoxyConfig annotation = element.getAnnotation(PackageEpoxyConfig.class);
      configurationMap.put(packageName, PackageConfigSettings.create(annotation));
    }

    return errors;
  }

  boolean requiresHashCode(AttributeInfo attributeInfo) {
    return globalRequireHashCode
        || getConfigurationForPackage(attributeInfo.getPackageName()).requireHashCode;
  }

  boolean requiresAbstractModels(TypeElement classElement) {
    return globalRequireAbstractModels
        || getConfigurationForElement(classElement).requireAbstractModels;
  }

  boolean implicitlyAddAutoModels(ControllerClassInfo controller) {
    return globalImplicitlyAddAutoModels
        || getConfigurationForElement(controller.controllerClassElement).implicitlyAddAutoModels;
  }

  boolean shouldValidateModelUsage() {
    return validateModelUsage;
  }

  private PackageConfigSettings getConfigurationForElement(Element element) {
    return getConfigurationForPackage(elementUtils.getPackageOf(element));
  }

  private PackageConfigSettings getConfigurationForPackage(PackageElement packageElement) {
    String packageName = packageElement.getQualifiedName().toString();
    return getConfigurationForPackage(packageName);
  }

  private PackageConfigSettings getConfigurationForPackage(String packageName) {
    if (configurationMap.containsKey(packageName)) {
      return configurationMap.get(packageName);
    }

    // If there isn't a configuration for that exact package then we look for configurations for
    // parent packages which include the target package. If multiple parent packages declare
    // configurations we take the configuration from the more nested parent.
    Entry<String, PackageConfigSettings> bestMatch = null;
    for (Entry<String, PackageConfigSettings> configEntry : configurationMap.entrySet()) {
      String entryPackage = configEntry.getKey();
      if (!packageName.startsWith(entryPackage + ".")) {
        continue;
      }

      if (bestMatch == null || bestMatch.getKey().length() < entryPackage.length()) {
        bestMatch = configEntry;
      }
    }

    return bestMatch != null ? bestMatch.getValue() : DEFAULT_PACKAGE_CONFIG_SETTINGS;
  }
}
