package com.airbnb.epoxy;

import com.squareup.javapoet.ClassName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import static com.airbnb.epoxy.Utils.buildEpoxyException;
import static com.airbnb.epoxy.Utils.getClassParamFromAnnotation;

/** Manages configuration settings for different packages. */
class ConfigManager {
  static final String PROCESSOR_OPTION_VALIDATE_MODEL_USAGE = "validateEpoxyModelUsage";
  static final String PROCESSOR_OPTION_REQUIRE_HASHCODE = "requireHashCodeInEpoxyModels";
  static final String PROCESSOR_OPTION_REQUIRE_ABSTRACT_MODELS = "requireAbstractEpoxyModels";
  static final String PROCESSOR_IMPLICITLY_ADD_AUTO_MODELS = "implicitlyAddAutoModels";

  private static final PackageConfigSettings
      DEFAULT_PACKAGE_CONFIG_SETTINGS = PackageConfigSettings.forDefaults();
  private final Map<String, PackageConfigSettings> configurationMap = new HashMap<>();
  private final Map<String, PackageModelViewNameSettings> modelViewNamingMap = new HashMap<>();
  private final Elements elementUtils;
  private final boolean validateModelUsage;
  private final boolean globalRequireHashCode;
  private final boolean globalRequireAbstractModels;
  private final boolean globalImplicitlyAddAutoModels;
  private final Types typeUtils;

  ConfigManager(Map<String, String> options, Elements elementUtils, Types typeUtils) {
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
    this.typeUtils = typeUtils;
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

    List<Exception> errors = new ArrayList<>();

    for (Element element : roundEnv.getElementsAnnotatedWith(PackageEpoxyConfig.class)) {
      String packageName = elementUtils.getPackageOf(element).getQualifiedName().toString();

      if (configurationMap.containsKey(packageName)) {
        errors.add(buildEpoxyException(
            "Only one Epoxy configuration annotation is allowed per package (%s)", packageName));
        continue;
      }

      PackageEpoxyConfig annotation = element.getAnnotation(PackageEpoxyConfig.class);
      configurationMap.put(packageName, PackageConfigSettings.create(annotation));
    }

    for (Element element : roundEnv.getElementsAnnotatedWith(PackageModelViewLayoutNaming.class)) {
      String packageName = elementUtils.getPackageOf(element).getQualifiedName().toString();

      if (modelViewNamingMap.containsKey(packageName)) {
        errors.add(buildEpoxyException("Only one %s annotation is allowed per package (%s)",
            PackageModelViewLayoutNaming.class.getSimpleName(), packageName));
        continue;
      }

      TypeMirror rLayoutClassType =
          getClassParamFromAnnotation(element, PackageModelViewLayoutNaming.class, "rLayoutClass");
      if (rLayoutClassType == null) {
        errors.add(buildEpoxyException(
            "Unable to get R class details from annotation %s (package: %s)",
            PackageModelViewLayoutNaming.class.getSimpleName(), packageName));
        continue;
      }

      ClassName rLayoutClassName =
          ClassName.get((TypeElement) typeUtils.asElement(rLayoutClassType));

      String rLayoutClassString = rLayoutClassName.reflectionName();
      if (!rLayoutClassString.endsWith(".R$layout")
          && !rLayoutClassString.endsWith(".R2$layout")) {
        errors.add(buildEpoxyException(
            "Layout class in %s must be of the form 'R.layout', but was '%s' (package: %s)",
            PackageModelViewLayoutNaming.class.getSimpleName(), rLayoutClassString, packageName));
        continue;
      }

      PackageModelViewLayoutNaming annotation =
          element.getAnnotation(PackageModelViewLayoutNaming.class);
      modelViewNamingMap.put(packageName,
          new PackageModelViewNameSettings(rLayoutClassName, annotation.layoutName()));
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

  PackageModelViewNameSettings getModelViewNamingSettings(Element viewElement) {
    String packageName = elementUtils.getPackageOf(viewElement).getQualifiedName().toString();
    return getObjectFromPackageMap(modelViewNamingMap, packageName, null);
  }

  private PackageConfigSettings getConfigurationForElement(Element element) {
    return getConfigurationForPackage(elementUtils.getPackageOf(element));
  }

  private PackageConfigSettings getConfigurationForPackage(PackageElement packageElement) {
    String packageName = packageElement.getQualifiedName().toString();
    return getConfigurationForPackage(packageName);
  }

  private PackageConfigSettings getConfigurationForPackage(String packageName) {
    return getObjectFromPackageMap(configurationMap, packageName, DEFAULT_PACKAGE_CONFIG_SETTINGS);
  }

  private static <T> T getObjectFromPackageMap(Map<String, T> map, String packageName,
      T ifNotFound) {

    if (map.containsKey(packageName)) {
      return map.get(packageName);
    }

    // If there isn't a configuration for that exact package then we look for configurations for
    // parent packages which include the target package. If multiple parent packages declare
    // configurations we take the configuration from the more nested parent.
    Entry<String, T> bestMatch = null;
    for (Entry<String, T> configEntry : map.entrySet()) {
      String entryPackage = configEntry.getKey();

      if (!packageName.startsWith(entryPackage + ".")) {
        continue;
      }

      if (bestMatch == null || bestMatch.getKey().length() < entryPackage.length()) {
        bestMatch = configEntry;
      }
    }

    return bestMatch != null ? bestMatch.getValue() : ifNotFound;
  }
}
