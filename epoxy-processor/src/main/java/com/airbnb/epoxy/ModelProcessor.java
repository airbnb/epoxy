package com.airbnb.epoxy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import static com.airbnb.epoxy.ProcessorUtils.EPOXY_MODEL_TYPE;
import static com.airbnb.epoxy.ProcessorUtils.isEpoxyModel;
import static com.airbnb.epoxy.ProcessorUtils.validateFieldAccessibleViaGeneratedCode;
import static javax.lang.model.element.Modifier.ABSTRACT;
import static javax.lang.model.element.Modifier.STATIC;

class ModelProcessor {

  private final Messager messager;
  private final Elements elementUtils;
  private final Types typeUtils;
  private final ConfigManager configManager;
  private final ErrorLogger errorLogger;
  private final GeneratedModelWriter modelWriter;
  private LinkedHashMap<TypeElement, ClassToGenerateInfo> modelClassMap;

  ModelProcessor(Filer filer, Messager messager, Elements elementUtils, Types typeUtils,
      ConfigManager configManager, ErrorLogger errorLogger,
      LayoutResourceProcessor layoutProcessor, DataBindingModuleLookup dataBindingModuleLookup) {
    this.messager = messager;
    this.elementUtils = elementUtils;
    this.typeUtils = typeUtils;
    this.configManager = configManager;
    this.errorLogger = errorLogger;
    modelWriter =
        new GeneratedModelWriter(filer, typeUtils, elementUtils, errorLogger, layoutProcessor,
            configManager, dataBindingModuleLookup);
  }

  List<ClassToGenerateInfo> getGeneratedModels() {
    return new ArrayList<>(modelClassMap.values());
  }

  void processModels(RoundEnvironment roundEnv) {
    modelClassMap = new LinkedHashMap<>();

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

    for (Entry<TypeElement, ClassToGenerateInfo> modelEntry : modelClassMap.entrySet()) {
      try {
        modelWriter.generateClassForModel(modelEntry.getValue());
      } catch (Exception e) {
        errorLogger.logError(e, "Error generating model classes");
      }
    }

    validateAttributesImplementHashCode(modelClassMap.values());
  }

  private void validateAttributesImplementHashCode(
      Collection<ClassToGenerateInfo> generatedClasses) {
    HashCodeValidator hashCodeValidator = new HashCodeValidator(typeUtils);

    for (ClassToGenerateInfo generatedClass : generatedClasses) {
      for (AttributeInfo attributeInfo : generatedClass.getAttributeInfo()) {
        if (configManager.requiresHashCode(attributeInfo)
            && attributeInfo.useInHash()
            && !attributeInfo.ignoreRequireHashCode()) {

          try {
            hashCodeValidator.validate(attributeInfo);
          } catch (EpoxyProcessorException e) {
            errorLogger.logError(e);
          }
        }
      }
    }
  }

  private void addAttributeToGeneratedClass(Element attribute,
      Map<TypeElement, ClassToGenerateInfo> modelClassMap) {
    TypeElement classElement = (TypeElement) attribute.getEnclosingElement();
    ClassToGenerateInfo helperClass = getOrCreateTargetClass(modelClassMap, classElement);
    helperClass.addAttribute(buildAttributeInfo(attribute));
  }

  private AttributeInfo buildAttributeInfo(Element attribute) {
    validateFieldAccessibleViaGeneratedCode(attribute, EpoxyAttribute.class, errorLogger, true);
    return new AttributeInfo(attribute, typeUtils, errorLogger);
  }

  private ClassToGenerateInfo getOrCreateTargetClass(
      Map<TypeElement, ClassToGenerateInfo> modelClassMap, TypeElement classElement) {

    ClassToGenerateInfo classToGenerateInfo = modelClassMap.get(classElement);

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

    if (classToGenerateInfo == null) {
      classToGenerateInfo = new ClassToGenerateInfo(typeUtils, elementUtils, classElement);
      modelClassMap.put(classElement, classToGenerateInfo);
    }

    return classToGenerateInfo;
  }

  /**
   * Looks for attributes on super classes that weren't included in this processor's coverage. Super
   * classes are already found if they are in the same module since the processor will pick them up
   * with the rest of the annotations.
   */
  private void addAttributesFromOtherModules(
      Map<TypeElement, ClassToGenerateInfo> modelClassMap) {
    // Copy the entries in the original map so we can add new entries to the map while we iterate
    // through the old entries
    Set<Entry<TypeElement, ClassToGenerateInfo>> originalEntries =
        new HashSet<>(modelClassMap.entrySet());

    for (Entry<TypeElement, ClassToGenerateInfo> entry : originalEntries) {
      TypeElement currentEpoxyModel = entry.getKey();
      TypeMirror superclassType = currentEpoxyModel.getSuperclass();
      ClassToGenerateInfo classToGenerateInfo = entry.getValue();

      while (isEpoxyModel(superclassType)) {
        TypeElement superclassEpoxyModel = (TypeElement) typeUtils.asElement(superclassType);

        if (!modelClassMap.keySet().contains(superclassEpoxyModel)) {
          for (Element element : superclassEpoxyModel.getEnclosedElements()) {
            if (element.getAnnotation(EpoxyAttribute.class) != null) {
              AttributeInfo attributeInfo = buildAttributeInfo(element);
              if (!belongToTheSamePackage(currentEpoxyModel, superclassEpoxyModel)
                  && attributeInfo.isPackagePrivate()) {
                // We can't inherit a package private attribute if we're not in the same package
                continue;
              }

              // We add just the attribute info to the class in our module. We do NOT want to
              // generate a class for the super class EpoxyModel in the other module since one
              // will be created when that module is processed. If we make one as well there will
              // be a duplicate (causes proguard errors and is just wrong).
              classToGenerateInfo.addAttribute(attributeInfo);
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
      Map<TypeElement, ClassToGenerateInfo> helperClassMap) {
    for (Entry<TypeElement, ClassToGenerateInfo> entry : helperClassMap.entrySet()) {
      TypeElement thisClass = entry.getKey();

      Map<TypeElement, ClassToGenerateInfo> otherClasses = new LinkedHashMap<>(helperClassMap);
      otherClasses.remove(thisClass);

      for (Entry<TypeElement, ClassToGenerateInfo> otherEntry : otherClasses.entrySet()) {
        TypeElement otherClass = otherEntry.getKey();

        if (!isSubtype(thisClass, otherClass)) {
          continue;
        }

        Set<AttributeInfo> otherAttributes = otherEntry.getValue().getAttributeInfo();

        if (belongToTheSamePackage(thisClass, otherClass)) {
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

  /**
   * Checks if two classes belong to the same package
   */
  private boolean belongToTheSamePackage(TypeElement class1, TypeElement class2) {
    Name package1 = elementUtils.getPackageOf(class1).getQualifiedName();
    Name package2 = elementUtils.getPackageOf(class2).getQualifiedName();
    return package1.equals(package2);
  }

  private boolean isSubtype(TypeElement e1, TypeElement e2) {
    return isSubtype(e1.asType(), e2.asType());
  }

  private boolean isSubtype(TypeMirror e1, TypeMirror e2) {
    // We use erasure so that EpoxyModelA is considered a subtype of EpoxyModel<T extends View>
    return typeUtils.isSubtype(e1, typeUtils.erasure(e2));
  }
}
