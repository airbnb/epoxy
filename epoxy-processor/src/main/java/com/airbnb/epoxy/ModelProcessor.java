package com.airbnb.epoxy;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import static com.airbnb.epoxy.Utils.EPOXY_MODEL_TYPE;
import static com.airbnb.epoxy.Utils.belongToTheSamePackage;
import static com.airbnb.epoxy.Utils.isEpoxyModel;
import static com.airbnb.epoxy.Utils.isSubtype;
import static com.airbnb.epoxy.Utils.validateFieldAccessibleViaGeneratedCode;
import static javax.lang.model.element.Modifier.ABSTRACT;
import static javax.lang.model.element.Modifier.STATIC;

class ModelProcessor {

  private final Elements elementUtils;
  private final Types typeUtils;
  private final ConfigManager configManager;
  private final ErrorLogger errorLogger;
  private final GeneratedModelWriter modelWriter;

  ModelProcessor(Elements elementUtils, Types typeUtils,
      ConfigManager configManager, ErrorLogger errorLogger, GeneratedModelWriter modelWriter) {
    this.elementUtils = elementUtils;
    this.typeUtils = typeUtils;
    this.configManager = configManager;
    this.errorLogger = errorLogger;
    this.modelWriter = modelWriter;
  }

  Collection<GeneratedModelInfo> processModels(RoundEnvironment roundEnv) {
    LinkedHashMap<TypeElement, GeneratedModelInfo> modelClassMap = new LinkedHashMap<>();

    for (Element attribute : roundEnv.getElementsAnnotatedWith(EpoxyAttribute.class)) {
      try {
        addAttributeToGeneratedClass(attribute, modelClassMap);
      } catch (Exception e) {
        errorLogger.logError(e);
      }
    }

    for (Element clazz : roundEnv.getElementsAnnotatedWith(EpoxyModelClass.class)) {
      try {
        getOrCreateTargetClass(modelClassMap, (TypeElement) clazz);
      } catch (Exception e) {
        errorLogger.logError(e);
      }
    }

    try {
      addAttributesFromOtherModules(modelClassMap);
    } catch (Exception e) {
      errorLogger.logError(e);
    }

    try {
      updateClassesForInheritance(modelClassMap);
    } catch (Exception e) {
      errorLogger.logError(e);
    }

    for (Entry<TypeElement, GeneratedModelInfo> modelEntry : modelClassMap.entrySet()) {
      try {
        modelWriter.generateClassForModel(modelEntry.getValue());
      } catch (Exception e) {
        errorLogger.logError(e, "Error generating model classes");
      }
    }

    return modelClassMap.values();
  }

  private void addAttributeToGeneratedClass(Element attribute,
      Map<TypeElement, GeneratedModelInfo> modelClassMap) {
    TypeElement classElement = (TypeElement) attribute.getEnclosingElement();
    GeneratedModelInfo helperClass = getOrCreateTargetClass(modelClassMap, classElement);
    helperClass.addAttribute(buildAttributeInfo(attribute));
  }

  private AttributeInfo buildAttributeInfo(Element attribute) {
    validateFieldAccessibleViaGeneratedCode(attribute, EpoxyAttribute.class, errorLogger, true);
    return new BaseModelAttributeInfo(attribute, typeUtils, elementUtils, errorLogger);
  }

  private GeneratedModelInfo getOrCreateTargetClass(
      Map<TypeElement, GeneratedModelInfo> modelClassMap, TypeElement classElement) {

    GeneratedModelInfo generatedModelInfo = modelClassMap.get(classElement);

    boolean isFinal = classElement.getModifiers().contains(Modifier.FINAL);
    if (isFinal) {
      errorLogger.logError("Class with %s annotations cannot be final: %s",
          EpoxyAttribute.class.getSimpleName(), classElement.getSimpleName());
    }

    // Nested classes must be static
    if (classElement.getNestingKind().isNested()) {
      if (!classElement.getModifiers().contains(STATIC)) {
        errorLogger.logError(
            "Nested model classes must be static. (class: %s)",
            classElement.getSimpleName());
      }
    }

    if (!isEpoxyModel(classElement.asType())) {
      errorLogger.logError("Class with %s annotations must extend %s (%s)",
          EpoxyAttribute.class.getSimpleName(), EPOXY_MODEL_TYPE,
          classElement.getSimpleName());
    }

    if (configManager.requiresAbstractModels(classElement)
        && !classElement.getModifiers().contains(ABSTRACT)) {
      errorLogger
          .logError("Epoxy model class must be abstract (%s)", classElement.getSimpleName());
    }

    if (generatedModelInfo == null) {
      generatedModelInfo = new BasicGeneratedModelInfo(typeUtils, elementUtils, classElement,
          errorLogger);
      modelClassMap.put(classElement, generatedModelInfo);
    }

    return generatedModelInfo;
  }

  /**
   * Looks for attributes on super classes that weren't included in this processor's coverage. Super
   * classes are already found if they are in the same module since the processor will pick them up
   * with the rest of the annotations.
   */
  private void addAttributesFromOtherModules(
      Map<TypeElement, GeneratedModelInfo> modelClassMap) {
    // Copy the entries in the original map so we can add new entries to the map while we iterate
    // through the old entries
    Set<Entry<TypeElement, GeneratedModelInfo>> originalEntries =
        new HashSet<>(modelClassMap.entrySet());

    for (Entry<TypeElement, GeneratedModelInfo> entry : originalEntries) {
      TypeElement currentEpoxyModel = entry.getKey();
      TypeMirror superclassType = currentEpoxyModel.getSuperclass();
      GeneratedModelInfo generatedModelInfo = entry.getValue();

      while (isEpoxyModel(superclassType)) {
        TypeElement superclassEpoxyModel = (TypeElement) typeUtils.asElement(superclassType);

        if (!modelClassMap.keySet().contains(superclassEpoxyModel)) {
          for (Element element : superclassEpoxyModel.getEnclosedElements()) {
            if (element.getAnnotation(EpoxyAttribute.class) != null) {
              AttributeInfo attributeInfo = buildAttributeInfo(element);
              if (!belongToTheSamePackage(currentEpoxyModel, superclassEpoxyModel, elementUtils)
                  && attributeInfo.isPackagePrivate()) {
                // We can't inherit a package private attribute if we're not in the same package
                continue;
              }

              // We add just the attribute info to the class in our module. We do NOT want to
              // generate a class for the super class EpoxyModel in the other module since one
              // will be created when that module is processed. If we make one as well there will
              // be a duplicate (causes proguard errors and is just wrong).
              generatedModelInfo.addAttribute(attributeInfo);
            }
          }
        }

        superclassType = superclassEpoxyModel.getSuperclass();
      }
    }
  }

  /**
   * Check each model for super classes that also have attributes. For each super class with
   * attributes we add those attributes to the attributes of the generated class, so that a
   * generated class contains all the attributes of its super classes combined.
   * <p>
   * One caveat is that if a sub class is in a different package than its super class we can't
   * include attributes that are package private, otherwise the generated class won't compile.
   */
  private void updateClassesForInheritance(
      Map<TypeElement, GeneratedModelInfo> helperClassMap) {
    for (Entry<TypeElement, GeneratedModelInfo> entry : helperClassMap.entrySet()) {
      TypeElement thisClass = entry.getKey();

      Map<TypeElement, GeneratedModelInfo> otherClasses = new LinkedHashMap<>(helperClassMap);
      otherClasses.remove(thisClass);

      for (Entry<TypeElement, GeneratedModelInfo> otherEntry : otherClasses.entrySet()) {
        TypeElement otherClass = otherEntry.getKey();

        if (!isSubtype(thisClass, otherClass, typeUtils)) {
          continue;
        }

        List<AttributeInfo> otherAttributes = otherEntry.getValue().getAttributeInfo();

        if (belongToTheSamePackage(thisClass, otherClass, elementUtils)) {
          entry.getValue().addAttributes(otherAttributes);
        } else {
          for (AttributeInfo attribute : otherAttributes) {
            if (!attribute.isPackagePrivate()) {
              entry.getValue().addAttribute(attribute);
            }
          }
        }
      }
    }
  }
}
