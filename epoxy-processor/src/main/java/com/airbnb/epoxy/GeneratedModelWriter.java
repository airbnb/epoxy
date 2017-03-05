package com.airbnb.epoxy;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;

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
import java.util.List;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;

import static com.airbnb.epoxy.ProcessorUtils.EPOXY_VIEW_HOLDER_TYPE;
import static com.airbnb.epoxy.ProcessorUtils.GENERATED_MODEL_INTERFACE;
import static com.airbnb.epoxy.ProcessorUtils.MODEL_CLICK_LISTENER_TYPE;
import static com.airbnb.epoxy.ProcessorUtils.ON_BIND_MODEL_LISTENER_TYPE;
import static com.airbnb.epoxy.ProcessorUtils.ON_UNBIND_MODEL_LISTENER_TYPE;
import static com.airbnb.epoxy.ProcessorUtils.VIEW_CLICK_LISTENER_TYPE;
import static com.airbnb.epoxy.ProcessorUtils.getClassName;
import static com.airbnb.epoxy.ProcessorUtils.getEpoxyObjectType;
import static com.airbnb.epoxy.ProcessorUtils.implementsMethod;
import static com.airbnb.epoxy.ProcessorUtils.isEpoxyModel;
import static com.airbnb.epoxy.ProcessorUtils.isEpoxyModelWithHolder;
import static com.squareup.javapoet.TypeName.BOOLEAN;
import static com.squareup.javapoet.TypeName.BYTE;
import static com.squareup.javapoet.TypeName.CHAR;
import static com.squareup.javapoet.TypeName.DOUBLE;
import static com.squareup.javapoet.TypeName.FLOAT;
import static com.squareup.javapoet.TypeName.INT;
import static com.squareup.javapoet.TypeName.LONG;
import static com.squareup.javapoet.TypeName.SHORT;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;

class GeneratedModelWriter {
  /**
   * Use this suffix on helper fields added to the generated class so that we don't clash with
   * fields on the original model.
   */
  static final String GENERATED_FIELD_SUFFIX = "_epoxyGeneratedModel";
  private static final String CREATE_NEW_HOLDER_METHOD_NAME = "createNewHolder";
  private static final String GET_DEFAULT_LAYOUT_METHOD_NAME = "getDefaultLayout";

  private final Filer filer;
  private final Types typeUtils;
  private final ErrorLogger errorLogger;
  private final ResourceProcessor resourceProcessor;

  GeneratedModelWriter(Filer filer, Types typeUtils, ErrorLogger errorLogger,
      ResourceProcessor resourceProcessor) {
    this.filer = filer;
    this.typeUtils = typeUtils;
    this.errorLogger = errorLogger;
    this.resourceProcessor = resourceProcessor;
  }

  void generateClassForModel(ClassToGenerateInfo info)
      throws IOException {
    if (!info.shouldGenerateSubClass()) {
      return;
    }

    TypeSpec generatedClass = TypeSpec.classBuilder(info.getGeneratedName())
        .addJavadoc("Generated file. Do not modify!")
        .addModifiers(PUBLIC)
        .superclass(info.getOriginalClassName())
        .addSuperinterface(getGeneratedModelInterface(info))
        .addTypeVariables(info.getTypeVariables())
        .addFields(generateFields(info))
        .addMethods(generateConstructors(info))
        .addMethods(generateBindMethods(info))
        .addMethods(generateSettersAndGetters(info))
        .addMethods(generateMethodsReturningClassType(info))
        .addMethods(generateDefaultMethodImplementations(info))
        .addMethod(generateReset(info))
        .addMethod(generateEquals(info))
        .addMethod(generateHashCode(info))
        .addMethod(generateToString(info))
        .build();

    JavaFile.builder(info.getGeneratedName().packageName(), generatedClass)
        .build()
        .writeTo(filer);
  }

  @NonNull
  private ParameterizedTypeName getGeneratedModelInterface(ClassToGenerateInfo info) {
    return ParameterizedTypeName.get(
        getClassName(GENERATED_MODEL_INTERFACE),
        TypeName.get(info.getModelType())
    );
  }

  private Iterable<FieldSpec> generateFields(ClassToGenerateInfo classInfo) {
    List<FieldSpec> fields = new ArrayList<>();

    // Add fields for the bind/unbind listeners
    ParameterizedTypeName onBindListenerType = ParameterizedTypeName.get(
        getClassName(ON_BIND_MODEL_LISTENER_TYPE),
        classInfo.getParameterizedGeneratedName(),
        TypeName.get(classInfo.getModelType())
    );
    fields
        .add(FieldSpec.builder(onBindListenerType, modelBindListenerFieldName(), PRIVATE).build());

    ParameterizedTypeName onUnbindListenerType = ParameterizedTypeName.get(
        getClassName(ON_UNBIND_MODEL_LISTENER_TYPE),
        classInfo.getParameterizedGeneratedName(),
        TypeName.get(classInfo.getModelType())
    );
    fields.add(
        FieldSpec.builder(onUnbindListenerType, modelUnbindListenerFieldName(), PRIVATE).build());

    for (AttributeInfo attributeInfo : classInfo.getAttributeInfo()) {
      if (!attributeInfo.isViewClickListener()) {
        continue;
      }

      // Create our own field to store a model click listener. We will later wrap the model click
      // listener in a view click listener to set on the original model's View.OnClickListener field
      fields.add(FieldSpec.builder(
          getModelClickListenerType(classInfo),
          attributeInfo.getModelClickListenerName(),
          PRIVATE
          ).build()
      );
    }

    return fields;
  }

  @NonNull
  private String modelUnbindListenerFieldName() {
    return "onModelUnboundListener" + GENERATED_FIELD_SUFFIX;
  }

  @NonNull
  private String modelBindListenerFieldName() {
    return "onModelBoundListener" + GENERATED_FIELD_SUFFIX;
  }

  private static ParameterizedTypeName getModelClickListenerType(ClassToGenerateInfo classInfo) {
    return ParameterizedTypeName.get(
        getClassName(MODEL_CLICK_LISTENER_TYPE),
        classInfo.getParameterizedGeneratedName(),
        TypeName.get(classInfo.getModelType()));
  }

  /** Include any constructors that are in the super class. */
  private Iterable<MethodSpec> generateConstructors(ClassToGenerateInfo info) {
    List<MethodSpec> constructors = new ArrayList<>(info.getConstructors().size());

    for (ClassToGenerateInfo.ConstructorInfo constructorInfo : info.getConstructors()) {
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

  private Iterable<MethodSpec> generateBindMethods(ClassToGenerateInfo classInfo) {
    List<MethodSpec> methods = new ArrayList<>();

    // Add bind/unbind methods so the class can set the epoxyModelBoundObject and
    // boundEpoxyViewHolder fields for the model click listener to access

    TypeName viewHolderType = getClassName(EPOXY_VIEW_HOLDER_TYPE);
    ParameterSpec viewHolderParam = ParameterSpec.builder(viewHolderType, "holder", FINAL).build();

    TypeName boundObjectType = TypeName.get(classInfo.getModelType());
    ParameterSpec boundObjectParam =
        ParameterSpec.builder(boundObjectType, "object", FINAL).build();

    Builder preBindBuilder = MethodSpec.methodBuilder("handlePreBind")
        .addModifiers(PUBLIC)
        .addAnnotation(Override.class)
        .addParameter(viewHolderParam)
        .addParameter(boundObjectParam);

    ClassName viewClickListenerType = getClassName(VIEW_CLICK_LISTENER_TYPE);
    ClassName viewType = getClassName("android.view.View");
    for (AttributeInfo attribute : classInfo.getAttributeInfo()) {
      if (!attribute.isViewClickListener()) {
        continue;
      }
      // Generate a View.OnClickListener that wraps the model click listener and set it as the
      // view click listener of the original model.

      String modelClickListenerField = attribute.getModelClickListenerName();
      preBindBuilder.beginControlFlow("if ($L != null)", modelClickListenerField);
      if (attribute.isPrivate()) {
        preBindBuilder.addCode(CodeBlock.of(
            "super.$L(new $T() {\n"
                + "    // Save the original click listener so if it gets changed on\n"
                + "    // the generated model this click listener won't be affected\n"
                + "    // if it is still bound to a view.\n"
                + "    private final $T $L = $T.this.$L;\n"
                + "    public void onClick($T v) {\n"
                + "    $L.onClick($T.this, object,\n"
                + "        holder.getAdapterPosition());\n"
                + "    }\n"
                + "    public int hashCode() {\n"
                + "       // Use the hash of the original click listener so we don't change the\n"
                + "       // value by wrapping it with this anonymous click listener\n"
                + "       return $L.hashCode();\n"
                + "    }\n"
                + "  });\n", attribute.setter(), viewClickListenerType,
            getModelClickListenerType(classInfo),
            modelClickListenerField, classInfo.getGeneratedName(),
            modelClickListenerField, viewType, modelClickListenerField,
            classInfo.getGeneratedName(), modelClickListenerField));
      } else {
        preBindBuilder.addCode(CodeBlock.of(
            "super.$L = new $T() {\n"
                + "    // Save the original click listener so if it gets changed on\n"
                + "    // the generated model this click listener won't be affected\n"
                + "    // if it is still bound to a view.\n"
                + "    private final $T $L = $T.this.$L;\n"
                + "    public void onClick($T v) {\n"
                + "    $L.onClick($T.this, object,\n"
                + "        holder.getAdapterPosition());\n"
                + "    }\n"
                + "    public int hashCode() {\n"
                + "       // Use the hash of the original click listener so we don't change the\n"
                + "       // value by wrapping it with this anonymous click listener\n"
                + "       return $L.hashCode();\n"
                + "    }\n"
                + "  };\n", attribute.getName(), viewClickListenerType,
            getModelClickListenerType(classInfo),
            modelClickListenerField, classInfo.getGeneratedName(),
            modelClickListenerField, viewType, modelClickListenerField,
            classInfo.getGeneratedName(), modelClickListenerField));
      }
      preBindBuilder.endControlFlow();
    }
    methods.add(preBindBuilder.build());

    methods.add(MethodSpec.methodBuilder("handlePostBind")
        .addModifiers(PUBLIC)
        .addAnnotation(Override.class)
        .addParameter(viewHolderParam)
        .addParameter(boundObjectParam)
        .beginControlFlow("if ($L != null)", modelBindListenerFieldName())
        .addStatement("$L.onModelBound(this, object)", modelBindListenerFieldName())
        .endControlFlow()
        .build());

    ParameterizedTypeName onBindListenerType = ParameterizedTypeName.get(
        getClassName(ON_BIND_MODEL_LISTENER_TYPE),
        classInfo.getParameterizedGeneratedName(),
        TypeName.get(classInfo.getModelType())
    );
    ParameterSpec bindListenerParam = ParameterSpec.builder(onBindListenerType, "listener").build();

    methods.add(MethodSpec.methodBuilder("onBind")
        .addJavadoc(CodeBlock
            .of("Register a listener that will be called when this model is bound to a view.\n"
                + "<p>\n"
                + "The listener will contribute to this model's hashCode state per the {@link\n"
                + "com.airbnb.epoxy.EpoxyAttribute.Option#DoNotHash} rules.\n"
                + "<p>\n"
                + "You may clear the listener by setting a null value, or by calling "
                + "{@link #reset()}"))
        .addModifiers(PUBLIC)
        .returns(classInfo.getParameterizedGeneratedName())
        .addParameter(bindListenerParam)
        .addStatement("this.$L = listener", modelBindListenerFieldName())
        .addStatement("return this")
        .build());

    ParameterSpec unbindObjectParam =
        ParameterSpec.builder(boundObjectType, "object").build();

    methods.add(MethodSpec.methodBuilder("unbind")
        .addAnnotation(Override.class)
        .addModifiers(PUBLIC)
        .addParameter(unbindObjectParam)
        .addStatement("super.unbind(object)")
        .beginControlFlow("if ($L != null)", modelUnbindListenerFieldName())
        .addStatement("$L.onModelUnbound(this, object)", modelUnbindListenerFieldName())
        .endControlFlow()
        .build());

    ParameterizedTypeName onUnbindListenerType = ParameterizedTypeName.get(
        getClassName(ON_UNBIND_MODEL_LISTENER_TYPE),
        classInfo.getParameterizedGeneratedName(),
        TypeName.get(classInfo.getModelType())
    );
    ParameterSpec unbindListenerParam =
        ParameterSpec.builder(onUnbindListenerType, "listener").build();

    methods.add(MethodSpec.methodBuilder("onUnbind")
        .addJavadoc(CodeBlock
            .of("Register a listener that will be called when this model is unbound from a "
                + "view.\n"
                + "<p>\n"
                + "The listener will contribute to this model's hashCode state per the {@link\n"
                + "com.airbnb.epoxy.EpoxyAttribute.Option#DoNotHash} rules.\n"
                + "<p>\n"
                + "You may clear the listener by setting a null value, or by calling "
                + "{@link #reset()}"))
        .addModifiers(PUBLIC)
        .returns(classInfo.getParameterizedGeneratedName())
        .addParameter(unbindListenerParam)
        .addStatement("this.$L = listener", modelUnbindListenerFieldName())
        .addStatement("return this")
        .build());

    return methods;
  }

  private Iterable<MethodSpec> generateMethodsReturningClassType(ClassToGenerateInfo info) {
    List<MethodSpec> methods = new ArrayList<>(info.getMethodsReturningClassType().size());

    for (ClassToGenerateInfo.MethodInfo methodInfo : info.getMethodsReturningClassType()) {
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

    ParameterSpec param =
        ParameterSpec.builder(getModelClickListenerType(helperClass), attributeName, FINAL).build();

    Builder builder = MethodSpec.methodBuilder(attributeName)
        .addJavadoc(CodeBlock
            .of("Set a click listener that will provide the parent view, model, and adapter "
                + "position of the clicked view. This will clear the normal View.OnClickListener "
                + "if one has been set"))
        .addModifiers(PUBLIC)
        .returns(helperClass.getParameterizedGeneratedName())
        .addParameter(param)
        .addAnnotations(attribute.getSetterAnnotations());

    if (attribute.isPrivate()) {
      builder.addStatement("super.$L(null)", attribute.setter());
    } else {
      builder.addStatement("super.$L = null", attributeName);
    }

    builder.addStatement("this.$L = $L", attribute.getModelClickListenerName(), attributeName);

    return builder
        .addStatement("return this")
        .build();
  }

  private MethodSpec generateEquals(ClassToGenerateInfo helperClass) {
    Builder builder = MethodSpec.methodBuilder("equals")
        .addAnnotation(Override.class)
        .addModifiers(PUBLIC)
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

    addEqualsLineForType(
        builder,
        false,
        getClassName(ON_BIND_MODEL_LISTENER_TYPE),
        modelBindListenerFieldName()
    );

    addEqualsLineForType(
        builder,
        false,
        getClassName(ON_UNBIND_MODEL_LISTENER_TYPE),
        modelUnbindListenerFieldName()
    );

    for (AttributeInfo attributeInfo : helperClass.getAttributeInfo()) {
      TypeName type = attributeInfo.getType();

      if (!attributeInfo.useInHash() && type.isPrimitive()) {
        continue;
      }

      if (attributeInfo.isPrivate()) {
        addEqualsLineForTypeUsingGetter(builder, attributeInfo.useInHash(), type,
            attributeInfo.getter());
      } else {
        addEqualsLineForType(builder, attributeInfo.useInHash(), type, attributeInfo.getName());
      }

      if (attributeInfo.isViewClickListener()) {
        // Add the model click listener as well
        addEqualsLineForType(builder, attributeInfo.useInHash(), type,
            attributeInfo.getModelClickListenerName());
      }
    }

    return builder
        .addStatement("return true")
        .build();
  }

  private void addEqualsLineForType(Builder builder, boolean useObjectHashCode, TypeName type,
      String name) {
    if (useObjectHashCode) {
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
      builder.beginControlFlow("if (($L == null) != (that.$L == null))", name, name)
          .addStatement("return false")
          .endControlFlow();
    }
  }

  private void addEqualsLineForTypeUsingGetter(Builder builder, boolean useObjectHashCode,
      TypeName type, String getter) {
    if (useObjectHashCode) {
      if (type == FLOAT) {
        builder.beginControlFlow("if (Float.compare(that.$L(), $L()) != 0)", getter, getter)
            .addStatement("return false")
            .endControlFlow();
      } else if (type == DOUBLE) {
        builder.beginControlFlow("if (Double.compare(that.$L(), $L()) != 0)", getter, getter)
            .addStatement("return false")
            .endControlFlow();
      } else if (type.isPrimitive()) {
        builder.beginControlFlow("if ($L() != that.$L())", getter, getter)
            .addStatement("return false")
            .endControlFlow();
      } else if (type instanceof ArrayTypeName) {
        builder
            .beginControlFlow("if (!$T.equals($L(), that.$L()))", TypeName.get(Arrays.class),
                getter, getter)
            .addStatement("return false")
            .endControlFlow();
      } else {
        builder
            .beginControlFlow("if ($L() != null ? !$L().equals(that.$L()) : that.$L() != null)",
                getter, getter, getter, getter)
            .addStatement("return false")
            .endControlFlow();
      }
    } else {
      builder.beginControlFlow("if (($L() == null) != (that.$L() == null))", getter, getter)
          .addStatement("return false")
          .endControlFlow();
    }
  }

  private MethodSpec generateHashCode(ClassToGenerateInfo helperClass) {
    Builder builder = MethodSpec.methodBuilder("hashCode")
        .addAnnotation(Override.class)
        .addModifiers(PUBLIC)
        .returns(int.class)
        .addStatement("int result = super.hashCode()");

    addHashCodeLineForType(
        builder,
        false,
        getClassName(ON_BIND_MODEL_LISTENER_TYPE),
        modelBindListenerFieldName()
    );

    addHashCodeLineForType(
        builder,
        false,
        getClassName(ON_UNBIND_MODEL_LISTENER_TYPE),
        modelUnbindListenerFieldName()
    );

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

      if (attributeInfo.isPrivate()) {
        addHashCodeLineForTypeUsingGetter(builder, attributeInfo.useInHash(), type,
            attributeInfo.getter());
      } else {
        addHashCodeLineForType(builder, attributeInfo.useInHash(), type, attributeInfo.getName());
      }

      if (attributeInfo.isViewClickListener()) {
        // Add the model click listener as well
        addHashCodeLineForType(builder, attributeInfo.useInHash(), type,
            attributeInfo.getModelClickListenerName());
      }
    }

    return builder
        .addStatement("return result")
        .build();
  }

  private static void addHashCodeLineForType(Builder builder, boolean useObjectHashCode,
      TypeName type, String name) {
    if (useObjectHashCode) {
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

  private static void addHashCodeLineForTypeUsingGetter(Builder builder, boolean useObjectHashCode,
      TypeName type, String getter) {
    if (useObjectHashCode) {
      if ((type == BYTE) || (type == CHAR) || (type == SHORT) || (type == INT)) {
        builder.addStatement("result = 31 * result + $L()", getter);
      } else if (type == LONG) {
        builder.addStatement("result = 31 * result + (int) ($L() ^ ($L() >>> 32))", getter, getter);
      } else if (type == FLOAT) {
        builder.addStatement("result = 31 * result + ($L() != +0.0f "
            + "? Float.floatToIntBits($L()) : 0)", getter, getter);
      } else if (type == DOUBLE) {
        builder.addStatement("temp = Double.doubleToLongBits($L())", getter)
            .addStatement("result = 31 * result + (int) (temp ^ (temp >>> 32))");
      } else if (type == BOOLEAN) {
        builder.addStatement("result = 31 * result + ($L() ? 1 : 0)", getter);
      } else if (type instanceof ArrayTypeName) {
        builder.addStatement("result = 31 * result + Arrays.hashCode($L())", getter);
      } else {
        builder.addStatement("result = 31 * result + ($L() != null ? $L().hashCode() : 0)", getter,
            getter);
      }
    } else {
      builder.addStatement("result = 31 * result + ($L() != null ? 1 : 0)", getter);
    }
  }

  private MethodSpec generateToString(ClassToGenerateInfo helperClass) {
    Builder builder = MethodSpec.methodBuilder("toString")
        .addAnnotation(Override.class)
        .addModifiers(PUBLIC)
        .returns(String.class);

    StringBuilder sb = new StringBuilder();
    sb.append(String.format("\"%s{\" +\n", helperClass.getGeneratedName().simpleName()));

    boolean first = true;
    for (AttributeInfo attributeInfo : helperClass.getAttributeInfo()) {
      String attributeName = attributeInfo.getName();
      if (first) {
        if (attributeInfo.isPrivate()) {
          sb.append(String.format("\"%s=\" + %s() +\n", attributeName, attributeInfo.getter()));
        } else {
          sb.append(String.format("\"%s=\" + %s +\n", attributeName, attributeName));
        }
        first = false;
      } else {
        if (attributeInfo.isPrivate()) {
          sb.append(String.format("\", %s=\" + %s() +\n", attributeName, attributeInfo.getter()));
        } else {
          sb.append(String.format("\", %s=\" + %s +\n", attributeName, attributeName));
        }
      }
    }

    sb.append("\"}\" + super.toString()");

    return builder
        .addStatement("return $L", sb.toString())
        .build();
  }

  private MethodSpec generateGetter(AttributeInfo data) {
    MethodSpec.Builder builder = MethodSpec.methodBuilder(data.getName())
        .addModifiers(PUBLIC)
        .returns(data.getType())
        .addAnnotations(data.getGetterAnnotations());

    if (data.isPrivate()) {
      builder.addStatement("return $L()", data.getter());
    } else {
      builder.addStatement("return $L", data.getName());
    }

    return builder.build();
  }

  private MethodSpec generateSetter(ClassToGenerateInfo helperClass, AttributeInfo attribute) {
    String attributeName = attribute.getName();
    Builder builder = MethodSpec.methodBuilder(attributeName)
        .addModifiers(PUBLIC)
        .returns(helperClass.getParameterizedGeneratedName())
        .addParameter(ParameterSpec.builder(attribute.getType(), attributeName)
            .addAnnotations(attribute.getSetterAnnotations()).build());

    if (attribute.isPrivate()) {
      builder.addStatement("this.$L($L)", attribute.setter(), attributeName);
    } else {
      builder.addStatement("this.$L = $L", attributeName, attributeName);
    }

    if (attribute.isViewClickListener()) {
      // Null out the model click listener since this view click listener should replace it
      builder.addStatement("this.$L = null", attribute.getModelClickListenerName());
    }

    if (attribute.hasSuperSetterMethod()) {
      builder.addStatement("super.$L($L)", attributeName, attributeName);
    }

    return builder
        .addStatement("return this")
        .build();
  }

  private MethodSpec generateReset(ClassToGenerateInfo helperClass) {
    Builder builder = MethodSpec.methodBuilder("reset")
        .addAnnotation(Override.class)
        .addModifiers(PUBLIC)
        .returns(helperClass.getParameterizedGeneratedName())
        .addStatement("$L = null", modelBindListenerFieldName())
        .addStatement("$L = null", modelUnbindListenerFieldName());

    for (AttributeInfo attributeInfo : helperClass.getAttributeInfo()) {
      if (!attributeInfo.hasFinalModifier()) {
        if (attributeInfo.isPrivate()) {
          builder.addStatement("this.$L($L)", attributeInfo.setter(),
              getDefaultValue(attributeInfo.getType()));
        } else {
          builder.addStatement("this.$L = $L", attributeInfo.getName(),
              getDefaultValue(attributeInfo.getType()));
        }
      }

      if (attributeInfo.isViewClickListener()) {
        builder.addStatement("$L = null", attributeInfo.getModelClickListenerName());
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
    } else if (attributeType == INT) {
      return "0";
    } else if (attributeType == BYTE) {
      return "(byte) 0";
    } else if (attributeType == CHAR) {
      return "(char) 0";
    } else if (attributeType == SHORT) {
      return "(short) 0";
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
