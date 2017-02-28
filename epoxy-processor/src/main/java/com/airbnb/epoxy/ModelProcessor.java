package com.airbnb.epoxy;

import android.support.annotation.LayoutRes;

import com.airbnb.epoxy.ClassToGenerateInfo.ConstructorInfo;
import com.airbnb.epoxy.ClassToGenerateInfo.MethodInfo;
import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.MethodSpec.Builder;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.lang.annotation.AnnotationTypeMismatchException;
import java.util.ArrayList;
import java.util.Arrays;
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
import javax.tools.Diagnostic.Kind;

import static com.airbnb.epoxy.ProcessorUtils.CLICKABLE_MODEL_INTERFACE;
import static com.airbnb.epoxy.ProcessorUtils.EPOXY_MODEL_TYPE;
import static com.airbnb.epoxy.ProcessorUtils.EPOXY_VIEW_HOLDER_TYPE;
import static com.airbnb.epoxy.ProcessorUtils.MODEL_CLICK_LISTENER_TYPE;
import static com.airbnb.epoxy.ProcessorUtils.VIEW_CLICK_LISTENER_TYPE;
import static com.airbnb.epoxy.ProcessorUtils.getClassName;
import static com.airbnb.epoxy.ProcessorUtils.getEpoxyObjectType;
import static com.airbnb.epoxy.ProcessorUtils.implementsMethod;
import static com.airbnb.epoxy.ProcessorUtils.isEpoxyModel;
import static com.airbnb.epoxy.ProcessorUtils.isEpoxyModelWithHolder;
import static com.airbnb.epoxy.ProcessorUtils.validateFieldAccessibleViaGeneratedCode;
import static com.squareup.javapoet.TypeName.BOOLEAN;
import static com.squareup.javapoet.TypeName.BYTE;
import static com.squareup.javapoet.TypeName.CHAR;
import static com.squareup.javapoet.TypeName.DOUBLE;
import static com.squareup.javapoet.TypeName.FLOAT;
import static com.squareup.javapoet.TypeName.INT;
import static com.squareup.javapoet.TypeName.LONG;
import static com.squareup.javapoet.TypeName.SHORT;
import static javax.lang.model.element.Modifier.ABSTRACT;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;

class ModelProcessor {
  private static final String CREATE_NEW_HOLDER_METHOD_NAME = "createNewHolder";
  private static final String GET_DEFAULT_LAYOUT_METHOD_NAME = "getDefaultLayout";

  private Filer filer;
  private Messager messager;
  private Elements elementUtils;
  private Types typeUtils;

  private ResourceProcessor resourceProcessor;
  private ConfigManager configManager;
  private ErrorLogger errorLogger;
  private LinkedHashMap<TypeElement, ClassToGenerateInfo> modelClassMap;

  ModelProcessor(Filer filer, Messager messager, Elements elementUtils, Types typeUtils,
      ResourceProcessor resourceProcessor, ConfigManager configManager, ErrorLogger errorLogger) {
    this.filer = filer;
    this.messager = messager;
    this.elementUtils = elementUtils;
    this.typeUtils = typeUtils;
    this.resourceProcessor = resourceProcessor;
    this.configManager = configManager;
    this.errorLogger = errorLogger;
  }

  List<ClassToGenerateInfo> getGeneratedModels() {
    return new ArrayList<>(modelClassMap.values());
  }

  void processModels(RoundEnvironment roundEnv) {
    modelClassMap = new LinkedHashMap<>();

    for (Element attribute : roundEnv.getElementsAnnotatedWith(EpoxyAttribute.class)) {
      messager.printMessage(Kind.NOTE, "field typename: " + TypeName.get(attribute.asType()));
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
        generateClassForModel(modelEntry.getValue());
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
            && !attributeInfo.allowMissingHash()) {

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
    validateFieldAccessibleViaGeneratedCode(attribute, EpoxyAttribute.class, errorLogger);
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

  private void generateClassForModel(ClassToGenerateInfo info)
      throws IOException {
    if (!info.shouldGenerateSubClass()) {
      return;
    }

    TypeSpec.Builder classBuilder = TypeSpec.classBuilder(info.getGeneratedName())
        .addJavadoc("Generated file. Do not modify!")
        .addModifiers(Modifier.PUBLIC)
        .superclass(info.getOriginalClassName());

    if (info.hasClickListenerAttributes()) {
      classBuilder.addSuperinterface(getClassName(CLICKABLE_MODEL_INTERFACE));
    }

    classBuilder.addTypeVariables(info.getTypeVariables())
        .addFields(generateFields(info))
        .addMethods(generateConstructors(info))
        .addMethods(generateBindMethodsIfNeeded(info))
        .addMethods(generateSettersAndGetters(info))
        .addMethods(generateMethodsReturningClassType(info))
        .addMethods(generateDefaultMethodImplementations(info))
        .addMethod(generateReset(info))
        .addMethod(generateEquals(info))
        .addMethod(generateHashCode(info))
        .addMethod(generateToString(info))
        .build();

    JavaFile.builder(info.getGeneratedName().packageName(), classBuilder.build())
        .build()
        .writeTo(filer);
  }

  private Iterable<FieldSpec> generateFields(ClassToGenerateInfo classInfo) {
    List<FieldSpec> fields = new ArrayList<>();

    if (classInfo.hasClickListenerAttributes()) {
      // Adds fields to store the view holder and bound object so the model click listener can
      // access them when clicked
      TypeName viewHolderType = getClassName(EPOXY_VIEW_HOLDER_TYPE);
      fields.add(FieldSpec.builder(viewHolderType, "boundEpoxyViewHolder", PRIVATE).build());

      TypeName modelType = TypeName.get(classInfo.getModelType());
      fields.add(FieldSpec.builder(modelType, "epoxyModelBoundObject", PRIVATE).build());
    }

    return fields;
  }

  /** Include any constructors that are in the super class. */
  private Iterable<MethodSpec> generateConstructors(ClassToGenerateInfo info) {
    List<MethodSpec> constructors = new ArrayList<>(info.getConstructors().size());

    for (ConstructorInfo constructorInfo : info.getConstructors()) {
      Builder builder = MethodSpec.constructorBuilder()
          .addModifiers(constructorInfo.modifiers)
          .addParameters(constructorInfo.params)
          .varargs(constructorInfo.varargs);

      StringBuilder statementBuilder = new StringBuilder("super(");
      generateParams(statementBuilder, constructorInfo.params);

      constructors.add(builder
          .addStatement(statementBuilder.toString())
          .build());
    }

    return constructors;
  }

  private Iterable<MethodSpec> generateBindMethodsIfNeeded(ClassToGenerateInfo info) {
    List<MethodSpec> methods = new ArrayList<>();

    if (info.hasClickListenerAttributes()) {
      // Add bind/unbind/setViewHolder methods so the class can set the epoxyModelBoundObject and
      // boundEpoxyViewHolder fields for the model click listener to access

      TypeName viewHolderType = getClassName(EPOXY_VIEW_HOLDER_TYPE);
      ParameterSpec viewHolderParam = ParameterSpec.builder(viewHolderType, "holder").build();

      methods.add(MethodSpec.methodBuilder("setViewHolder")
          .addAnnotation(Override.class)
          .addModifiers(PUBLIC)
          .addParameter(viewHolderParam)
          .addStatement("this.boundEpoxyViewHolder = holder")
          .build());

      TypeName boundObjectType = TypeName.get(info.getModelType());
      ParameterSpec boundObjectParam = ParameterSpec.builder(boundObjectType, "object").build();

      methods.add(MethodSpec.methodBuilder("bind")
          .addAnnotation(Override.class)
          .addModifiers(PUBLIC)
          .addParameter(boundObjectParam)
          .addStatement("super.bind(object)")
          .addStatement("this.epoxyModelBoundObject = object")
          .build());

      TypeName payloadsType = ParameterizedTypeName.get(List.class, Object.class);
      ParameterSpec payloadsParam = ParameterSpec.builder(payloadsType, "payloads").build();

      methods.add(MethodSpec.methodBuilder("bind")
          .addAnnotation(Override.class)
          .addModifiers(PUBLIC)
          .addParameter(boundObjectParam)
          .addParameter(payloadsParam)
          .addStatement("super.bind(object, payloads)")
          .addStatement("this.epoxyModelBoundObject = object")
          .build());

      methods.add(MethodSpec.methodBuilder("unbind")
          .addAnnotation(Override.class)
          .addModifiers(PUBLIC)
          .addParameter(boundObjectParam)
          .addStatement("super.unbind(object)")
          .addStatement("this.epoxyModelBoundObject = null")
          .addStatement("this.boundEpoxyViewHolder = null")
          .build());
    }

    return methods;
  }

  private Iterable<MethodSpec> generateMethodsReturningClassType(ClassToGenerateInfo info) {
    List<MethodSpec> methods = new ArrayList<>(info.getMethodsReturningClassType().size());

    for (MethodInfo methodInfo : info.getMethodsReturningClassType()) {
      Builder builder = MethodSpec.methodBuilder(methodInfo.name)
          .addModifiers(methodInfo.modifiers)
          .addParameters(methodInfo.params)
          .addAnnotation(Override.class)
          .varargs(methodInfo.varargs)
          .returns(info.getParameterizedGeneratedName());

      StringBuilder statementBuilder = new StringBuilder(String.format("super.%s(",
          methodInfo.name));
      generateParams(statementBuilder, methodInfo.params);

      methods.add(builder
          .addStatement(statementBuilder.toString())
          .addStatement("return this")
          .build());
    }

    return methods;
  }

  /**
   * Generates default implementations of certain model methods if the model is abstract and doesn't
   * implement them.
   */
  private Iterable<MethodSpec> generateDefaultMethodImplementations(ClassToGenerateInfo info) {

    List<MethodSpec> methods = new ArrayList<>();
    TypeElement originalClassElement = info.getOriginalClassElement();

    addCreateHolderMethodIfNeeded(originalClassElement, methods);
    addDefaultLayoutMethodIfNeeded(originalClassElement, methods);

    return methods;
  }

  /**
   * If the model is a holder and doesn't implement the "createNewHolder" method we can generate a
   * default implementation by getting the class type and creating a new instance of it.
   */
  private void addCreateHolderMethodIfNeeded(TypeElement originalClassElement,
      List<MethodSpec> methods) {

    if (!isEpoxyModelWithHolder(originalClassElement)) {
      return;
    }

    MethodSpec createHolderMethod = MethodSpec.methodBuilder(
        CREATE_NEW_HOLDER_METHOD_NAME)
        .addAnnotation(Override.class)
        .addModifiers(Modifier.PROTECTED)
        .build();

    if (implementsMethod(originalClassElement, createHolderMethod, typeUtils)) {
      return;
    }

    TypeMirror epoxyObjectType = getEpoxyObjectType(originalClassElement, typeUtils);
    if (epoxyObjectType == null) {
      errorLogger
          .logError("Return type for createNewHolder method could not be found. (class: %s)",
              originalClassElement.getSimpleName());
      return;
    }

    createHolderMethod = createHolderMethod.toBuilder()
        .returns(TypeName.get(epoxyObjectType))
        .addStatement("return new $T()", epoxyObjectType)
        .build();

    methods.add(createHolderMethod);
  }

  /**
   * If there is no existing implementation of getDefaultLayout we can generate an implementation.
   * This relies on a layout res being set in the @EpoxyModelClass annotation.
   */
  private void addDefaultLayoutMethodIfNeeded(TypeElement originalClassElement,
      List<MethodSpec> methods) {

    MethodSpec getDefaultLayoutMethod = MethodSpec.methodBuilder(
        GET_DEFAULT_LAYOUT_METHOD_NAME)
        .addAnnotation(Override.class)
        .addAnnotation(LayoutRes.class)
        .addModifiers(Modifier.PROTECTED)
        .returns(TypeName.INT)
        .build();

    if (implementsMethod(originalClassElement, getDefaultLayoutMethod, typeUtils)) {
      return;
    }

    EpoxyModelClass annotation = findClassAnnotationWithLayout(originalClassElement);
    if (annotation == null) {
      errorLogger
          .logError("Model must use %s annotation if it does not implement %s. (class: %s)",
              EpoxyModelClass.class,
              GET_DEFAULT_LAYOUT_METHOD_NAME,
              originalClassElement.getSimpleName());
      return;
    }

    int layoutRes;
    try {
      layoutRes = annotation.layout();
    } catch (AnnotationTypeMismatchException e) {
      errorLogger.logError("Invalid layout value in %s annotation. (class: %s). %s: %s",
          EpoxyModelClass.class,
          originalClassElement.getSimpleName(),
          e.getClass().getSimpleName(),
          e.getMessage());
      return;
    }

    if (layoutRes == 0) {
      errorLogger
          .logError(
              "Model must specify a valid layout resource in the %s annotation. (class: %s)",
              EpoxyModelClass.class,
              originalClassElement.getSimpleName());
      return;
    }

    AndroidResource layoutResource = resourceProcessor.getResourceForValue(layoutRes);
    getDefaultLayoutMethod = getDefaultLayoutMethod.toBuilder()
        .addStatement("return $L", layoutResource.code)
        .build();

    methods.add(getDefaultLayoutMethod);
  }

  /**
   * Looks for {@link EpoxyModelClass} annotation in the original class and his parents.
   */
  private EpoxyModelClass findClassAnnotationWithLayout(TypeElement classElement) {
    if (!isEpoxyModel(classElement)) {
      return null;
    }

    EpoxyModelClass annotation = classElement.getAnnotation(EpoxyModelClass.class);
    if (annotation == null) {
      return null;
    }

    try {
      int layoutRes = annotation.layout();
      if (layoutRes != 0) {
        return annotation;
      }
    } catch (AnnotationTypeMismatchException e) {
      errorLogger.logError("Invalid layout value in %s annotation. (class: %s). %s: %s",
          EpoxyModelClass.class,
          classElement.getSimpleName(),
          e.getClass().getSimpleName(),
          e.getMessage());
      return null;
    }

    TypeElement superclassElement =
        (TypeElement) typeUtils.asElement(classElement.getSuperclass());
    EpoxyModelClass annotationOnSuperClass = findClassAnnotationWithLayout(superclassElement);

    // Return the last annotation value we have so the proper error can be thrown if needed
    return annotationOnSuperClass != null ? annotationOnSuperClass : annotation;
  }

  private void generateParams(StringBuilder statementBuilder, List<ParameterSpec> params) {
    boolean first = true;
    for (ParameterSpec param : params) {
      if (!first) {
        statementBuilder.append(", ");
      }
      first = false;
      statementBuilder.append(param.name);
    }
    statementBuilder.append(")");
  }

  private List<MethodSpec> generateSettersAndGetters(ClassToGenerateInfo helperClass) {
    List<MethodSpec> methods = new ArrayList<>();

    for (AttributeInfo attributeInfo : helperClass.getAttributeInfo()) {
      if (attributeInfo.isViewClickListener()) {
        methods.add(generateSetClickModelListener(helperClass, attributeInfo));
      }
      if (attributeInfo.generateSetter() && !attributeInfo.hasFinalModifier()) {
        methods.add(generateSetter(helperClass, attributeInfo));
      }
      methods.add(generateGetter(attributeInfo));
    }

    return methods;
  }

  private MethodSpec generateSetClickModelListener(ClassToGenerateInfo helperClass,
      AttributeInfo attribute) {
    String attributeName = attribute.getName();

    ClassName viewClickListenerType = getClassName(VIEW_CLICK_LISTENER_TYPE);
    ClassName viewType = getClassName("android.view.View");

    ParameterizedTypeName modelClickListenerType = ParameterizedTypeName.get(
        getClassName(MODEL_CLICK_LISTENER_TYPE),
        helperClass.getParameterizedGeneratedName(),
        TypeName.get(helperClass.getModelType())
    );

    ParameterSpec param =
        ParameterSpec.builder(modelClickListenerType, attributeName, FINAL).build();

    Builder builder = MethodSpec.methodBuilder(attributeName)
        .addModifiers(Modifier.PUBLIC)
        .returns(helperClass.getParameterizedGeneratedName())
        .addParameter(param)
        .addAnnotations(attribute.getSetterAnnotations())
        .addCode(CodeBlock.of(
            "if ($L == null) {\n"
                + "  this.$L = null;\n"
                + "} else {\n"
                + "  this.$L = new $T() {\n"
                + "    public void onClick($T v) {\n"
                + "      // protect from being called when unbound\n"
                + "      if (boundEpoxyViewHolder != null) {\n"
                + "        $L.onClick($T.this, epoxyModelBoundObject,\n"
                + "            boundEpoxyViewHolder.getAdapterPosition());\n"
                + "      }\n"
                + "    }\n"
                + "    public int hashCode() {\n"
                + "      // Hash the original click listener to avoid changing model state\n"
                + "      return $L.hashCode();\n"
                + "    }\n"
                + "  };\n"
                + "}\n", attributeName, attributeName, attributeName,
            viewClickListenerType, viewType, attributeName, helperClass.getGeneratedName(),
            attributeName));

    return builder
        .addStatement("return this")
        .build();
  }

  private MethodSpec generateEquals(ClassToGenerateInfo helperClass) {
    Builder builder = MethodSpec.methodBuilder("equals")
        .addAnnotation(Override.class)
        .addModifiers(Modifier.PUBLIC)
        .returns(boolean.class)
        .addParameter(Object.class, "o")
        .beginControlFlow("if (o == this)")
        .addStatement("return true")
        .endControlFlow()
        .beginControlFlow("if (!(o instanceof $T))", helperClass.getGeneratedName())
        .addStatement("return false")
        .endControlFlow()
        .beginControlFlow("if (!super.equals(o))")
        .addStatement("return false")
        .endControlFlow()
        .addStatement("$T that = ($T) o", helperClass.getGeneratedName(),
            helperClass.getGeneratedName());

    for (AttributeInfo attributeInfo : helperClass.getAttributeInfo()) {
      TypeName type = attributeInfo.getType();

      if (!attributeInfo.useInHash() && type.isPrimitive()) {
        continue;
      }

      String name = attributeInfo.getName();

      if (attributeInfo.useInHash()) {
        if (type == FLOAT) {
          builder.beginControlFlow("if (Float.compare(that.$L, $L) != 0)", name, name)
              .addStatement("return false")
              .endControlFlow();
        } else if (type == DOUBLE) {
          builder.beginControlFlow("if (Double.compare(that.$L, $L) != 0)", name, name)
              .addStatement("return false")
              .endControlFlow();
        } else if (type.isPrimitive()) {
          builder.beginControlFlow("if ($L != that.$L)", name, name)
              .addStatement("return false")
              .endControlFlow();
        } else if (type instanceof ArrayTypeName) {
          builder
              .beginControlFlow("if (!$T.equals($L, that.$L))", TypeName.get(Arrays.class), name,
                  name)
              .addStatement("return false")
              .endControlFlow();
        } else {
          builder
              .beginControlFlow("if ($L != null ? !$L.equals(that.$L) : that.$L != null)",
                  name, name, name, name)
              .addStatement("return false")
              .endControlFlow();
        }
      } else {
        builder.beginControlFlow("if ($L != null && that.$L == null"
                + " || $L == null && that.$L != null)",
            name, name, name, name)
            .addStatement("return false")
            .endControlFlow();
      }
    }

    return builder
        .addStatement("return true")
        .build();
  }

  private MethodSpec generateHashCode(ClassToGenerateInfo helperClass) {
    Builder builder = MethodSpec.methodBuilder("hashCode")
        .addAnnotation(Override.class)
        .addModifiers(Modifier.PUBLIC)
        .returns(int.class)
        .addStatement("int result = super.hashCode()");

    for (AttributeInfo attributeInfo : helperClass.getAttributeInfo()) {
      if (!attributeInfo.useInHash()) {
        continue;
      }
      if (attributeInfo.getType() == DOUBLE) {
        builder.addStatement("long temp");
        break;
      }
    }

    for (AttributeInfo attributeInfo : helperClass.getAttributeInfo()) {
      TypeName type = attributeInfo.getType();

      if (!attributeInfo.useInHash() && type.isPrimitive()) {
        continue;
      }

      String name = attributeInfo.getName();

      if (attributeInfo.useInHash()) {
        if ((type == BYTE) || (type == CHAR) || (type == SHORT) || (type == INT)) {
          builder.addStatement("result = 31 * result + $L", name);
        } else if (type == LONG) {
          builder.addStatement("result = 31 * result + (int) ($L ^ ($L >>> 32))", name, name);
        } else if (type == FLOAT) {
          builder.addStatement("result = 31 * result + ($L != +0.0f "
              + "? Float.floatToIntBits($L) : 0)", name, name);
        } else if (type == DOUBLE) {
          builder.addStatement("temp = Double.doubleToLongBits($L)", name)
              .addStatement("result = 31 * result + (int) (temp ^ (temp >>> 32))");
        } else if (type == BOOLEAN) {
          builder.addStatement("result = 31 * result + ($L ? 1 : 0)", name);
        } else if (type instanceof ArrayTypeName) {
          builder.addStatement("result = 31 * result + Arrays.hashCode($L)", name);
        } else {
          builder.addStatement("result = 31 * result + ($L != null ? $L.hashCode() : 0)", name,
              name);
        }
      } else {
        builder.addStatement("result = 31 * result + ($L != null ? 1 : 0)", name);
      }
    }

    return builder
        .addStatement("return result")
        .build();
  }

  private MethodSpec generateToString(ClassToGenerateInfo helperClass) {
    Builder builder = MethodSpec.methodBuilder("toString")
        .addAnnotation(Override.class)
        .addModifiers(Modifier.PUBLIC)
        .returns(String.class);

    StringBuilder sb = new StringBuilder();
    sb.append(String.format("\"%s{\" +\n", helperClass.getGeneratedName().simpleName()));

    boolean first = true;
    for (AttributeInfo attributeInfo : helperClass.getAttributeInfo()) {
      String attributeName = attributeInfo.getName();
      if (first) {
        sb.append(String.format("\"%s=\" + %s +\n", attributeName, attributeName));
        first = false;
      } else {
        sb.append(String.format("\", %s=\" + %s +\n", attributeName, attributeName));
      }
    }

    sb.append("\"}\" + super.toString()");

    return builder
        .addStatement("return $L", sb.toString())
        .build();
  }

  private MethodSpec generateGetter(AttributeInfo data) {
    return MethodSpec.methodBuilder(data.getName())
        .addModifiers(Modifier.PUBLIC)
        .returns(data.getType())
        .addAnnotations(data.getGetterAnnotations())
        .addStatement("return $L", data.getName())
        .build();
  }

  private MethodSpec generateSetter(ClassToGenerateInfo helperClass, AttributeInfo data) {
    String attributeName = data.getName();
    Builder builder = MethodSpec.methodBuilder(attributeName)
        .addModifiers(Modifier.PUBLIC)
        .returns(helperClass.getParameterizedGeneratedName())
        .addParameter(ParameterSpec.builder(data.getType(), attributeName)
            .addAnnotations(data.getSetterAnnotations()).build())
        .addStatement("this.$L = $L", attributeName, attributeName);

    if (data.hasSuperSetterMethod()) {
      builder.addStatement("super.$L($L)", attributeName, attributeName);
    }

    return builder
        .addStatement("return this")
        .build();
  }

  private MethodSpec generateReset(ClassToGenerateInfo helperClass) {
    Builder builder = MethodSpec.methodBuilder("reset")
        .addAnnotation(Override.class)
        .addModifiers(Modifier.PUBLIC)
        .returns(helperClass.getParameterizedGeneratedName());

    for (AttributeInfo attributeInfo : helperClass.getAttributeInfo()) {
      if (!attributeInfo.hasFinalModifier()) {
        builder.addStatement("this.$L = $L", attributeInfo.getName(),
            getDefaultValue(attributeInfo.getType()));
      }
    }

    return builder
        .addStatement("super.reset()")
        .addStatement("return this")
        .build();
  }

  private static String getDefaultValue(TypeName attributeType) {
    if (attributeType == BOOLEAN) {
      return "false";
    } else if (attributeType == BYTE || attributeType == CHAR || attributeType == SHORT
        || attributeType == INT) {
      return "0";
    } else if (attributeType == LONG) {
      return "0L";
    } else if (attributeType == FLOAT) {
      return "0.0f";
    } else if (attributeType == DOUBLE) {
      return "0.0d";
    } else {
      return "null";
    }
  }
}
