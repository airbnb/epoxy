package com.airbnb.epoxy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

import static com.airbnb.epoxy.ProcessorUtils.buildEpoxyException;

/** Manages configuration settings for different packages. */
class ConfigManager {

  private static final PackageConfigSettings
      DEFAULT_PACKAGE_CONFIG_SETTINGS = PackageConfigSettings.forDefaults();
  private final Map<String, PackageConfigSettings> configurationMap = new HashMap<>();
  private final Elements elementUtils;

  ConfigManager(Elements elementUtils) {
    this.elementUtils = elementUtils;
  }

  List<Exception> processConfigurations(RoundEnvironment roundEnv) {
    configurationMap.clear();

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
    return getConfigurationForElement(attributeInfo.getClassElement()).requireHashCode;
  }

  boolean requiresAbstractModels(TypeElement classElement) {
    return getConfigurationForElement(classElement).requireAbstractModels;
  }

  boolean validateModelUsage(ControllerClassInfo classElement) {
    return getConfigurationForElement(classElement.controllerClassElement).validateModelUsage;
  }

  boolean validateModelUsage(ClassToGenerateInfo epoxyClass) {
    return getConfigurationForElement(epoxyClass.getOriginalClassElement()).validateModelUsage;
  }

  private PackageConfigSettings getConfigurationForElement(Element element) {
    String targetPackage = elementUtils.getPackageOf(element).getQualifiedName().toString();

    if (configurationMap.containsKey(targetPackage)) {
      return configurationMap.get(targetPackage);
    }

    // If there isn't a configuration for that exact package then we look for configurations for
    // parent packages which include the target package. If multiple parent packages declare
    // configurations we take the configuration from the more nested parent.
    Entry<String, PackageConfigSettings> bestMatch = null;
    for (Entry<String, PackageConfigSettings> configEntry : configurationMap.entrySet()) {
      String entryPackage = configEntry.getKey();
      if (!targetPackage.startsWith(entryPackage + ".")) {
        continue;
      }

      if (bestMatch == null || bestMatch.getKey().length() < entryPackage.length()) {
        bestMatch = configEntry;
      }
    }

    return bestMatch != null ? bestMatch.getValue() : DEFAULT_PACKAGE_CONFIG_SETTINGS;
  }
}
