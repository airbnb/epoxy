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
import java.util.Collections;
import java.util.List;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Types;

import static com.airbnb.epoxy.Utils.EPOXY_CONTROLLER_TYPE;
import static com.airbnb.epoxy.Utils.EPOXY_VIEW_HOLDER_TYPE;
import static com.airbnb.epoxy.Utils.GENERATED_MODEL_INTERFACE;
import static com.airbnb.epoxy.Utils.MODEL_CLICK_LISTENER_TYPE;
import static com.airbnb.epoxy.Utils.ON_BIND_MODEL_LISTENER_TYPE;
import static com.airbnb.epoxy.Utils.ON_UNBIND_MODEL_LISTENER_TYPE;
import static com.airbnb.epoxy.Utils.UNTYPED_EPOXY_MODEL_TYPE;
import static com.airbnb.epoxy.Utils.WRAPPED_LISTENER_TYPE;
import static com.airbnb.epoxy.Utils.getClassName;
import static com.airbnb.epoxy.Utils.implementsMethod;
import static com.airbnb.epoxy.Utils.isDataBindingModel;
import static com.airbnb.epoxy.Utils.isEpoxyModel;
import static com.airbnb.epoxy.Utils.isEpoxyModelWithHolder;
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
  private final LayoutResourceProcessor layoutResourceProcessor;
  private final ConfigManager configManager;
  private final DataBindingModuleLookup dataBindingModuleLookup;

  interface BeforeBuildCallback {
    void modifyBuilder(TypeSpec.Builder builder);
  }

  GeneratedModelWriter(Filer filer, Types typeUtils, ErrorLogger errorLogger,
      LayoutResourceProcessor layoutResourceProcessor, ConfigManager configManager,
      DataBindingModuleLookup dataBindingModuleLookup) {
    this.filer = filer;
    this.typeUtils = typeUtils;
    this.errorLogger = errorLogger;
    this.layoutResourceProcessor = layoutResourceProcessor;
    this.configManager = configManager;
    this.dataBindingModuleLookup = dataBindingModuleLookup;
  }

  void generateClassForModel(GeneratedModelInfo info) throws IOException {
    generateClassForModel(info, null);
  }

  void generateClassForModel(GeneratedModelInfo info, BeforeBuildCallback beforeBuildCallback)
      throws IOException {
    if (!info.shouldGenerateModel()) {
      return;
    }

    TypeSpec.Builder builder = TypeSpec.classBuilder(info.getGeneratedName())
        .addJavadoc("Generated file. Do not modify!")
        .addModifiers(PUBLIC)
        .superclass(info.getSuperClassName())
        .addSuperinterface(getGeneratedModelInterface(info))
        .addTypeVariables(info.getTypeVariables())
        .addFields(generateFields(info))
        .addMethods(generateConstructors(info));

    generateDebugAddToMethodIfNeeded(builder);

    builder
        .addMethods(generateBindMethods(info))
        .addMethods(generateSettersAndGetters(info))
        .addMethods(generateMethodsReturningClassType(info))
        .addMethods(generateDefaultMethodImplementations(info))
        .addMethods(generateDataBindingMethodsIfNeeded(info))
        .addMethod(generateReset(info))
        .addMethod(generateEquals(info))
        .addMethod(generateHashCode(info))
        .addMethod(generateToString(info));

    if (beforeBuildCallback != null) {
      beforeBuildCallback.modifyBuilder(builder);
    }

    JavaFile.builder(info.getGeneratedName().packageName(), builder.build())
        .build()
        .writeTo(filer);
  }

  @NonNull
  private ParameterizedTypeName getGeneratedModelInterface(GeneratedModelInfo info) {
    return ParameterizedTypeName.get(
        getClassName(GENERATED_MODEL_INTERFACE),
        info.getModelType()
    );
  }

  private Iterable<FieldSpec> generateFields(GeneratedModelInfo classInfo) {
    List<FieldSpec> fields = new ArrayList<>();

    // Add fields for the bind/unbind listeners
    ParameterizedTypeName onBindListenerType = ParameterizedTypeName.get(
        getClassName(ON_BIND_MODEL_LISTENER_TYPE),
        classInfo.getParameterizedGeneratedName(),
        classInfo.getModelType()
    );
    fields
        .add(FieldSpec.builder(onBindListenerType, modelBindListenerFieldName(), PRIVATE).build());

    ParameterizedTypeName onUnbindListenerType = ParameterizedTypeName.get(
        getClassName(ON_UNBIND_MODEL_LISTENER_TYPE),
        classInfo.getParameterizedGeneratedName(),
        classInfo.getModelType()
    );
    fields.add(
        FieldSpec.builder(onUnbindListenerType, modelUnbindListenerFieldName(), PRIVATE).build());

    for (AttributeInfo attributeInfo : classInfo.getAttributeInfo()) {
      if (attributeInfo.isGenerated) {
        fields.add(FieldSpec.builder(
            attributeInfo.getTypeName(),
            attributeInfo.name,
            PRIVATE
            ).build()
        );
      }

      if (attributeInfo.isViewClickListener()) {
        // Create our own field to store a model click listener. We will later wrap the model click
        // listener in a view click listener to set on the original model's View.OnClickListener
        // field
        fields.add(FieldSpec.builder(
            getModelClickListenerType(classInfo),
            attributeInfo.getModelClickListenerName(),
            PRIVATE
            ).build()
        );
      }
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

  private ParameterizedTypeName getModelClickListenerType(GeneratedModelInfo classInfo) {
    return ParameterizedTypeName.get(
        getClassName(MODEL_CLICK_LISTENER_TYPE),
        classInfo.getParameterizedGeneratedName(),
        classInfo.getModelType());
  }

  /** Include any constructors that are in the super class. */
  private Iterable<MethodSpec> generateConstructors(GeneratedModelInfo info) {
    List<MethodSpec> constructors = new ArrayList<>(info.getConstructors().size());

    for (GeneratedModelInfo.ConstructorInfo constructorInfo : info.getConstructors()) {
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

  private void generateDebugAddToMethodIfNeeded(TypeSpec.Builder classBuilder) {
    if (!configManager.shouldValidateModelUsage()) {
      return;
    }

    MethodSpec addToMethod = MethodSpec.methodBuilder("addTo")
        .addParameter(getClassName(EPOXY_CONTROLLER_TYPE), "controller")
        .addAnnotation(Override.class)
        .addModifiers(PUBLIC)
        .addStatement("super.addTo(controller)")
        .addStatement("addWithDebugValidation(controller)")
        .build();

    classBuilder.addMethod(addToMethod);
  }

  private Iterable<MethodSpec> generateBindMethods(GeneratedModelInfo classInfo) {
    List<MethodSpec> methods = new ArrayList<>();

    // Add bind/unbind methods so the class can set the epoxyModelBoundObject and
    // boundEpoxyViewHolder fields for the model click listener to access

    TypeName viewHolderType = getClassName(EPOXY_VIEW_HOLDER_TYPE);
    ParameterSpec viewHolderParam = ParameterSpec.builder(viewHolderType, "holder", FINAL).build();

    ParameterSpec boundObjectParam =
        ParameterSpec.builder(classInfo.getModelType(), "object", FINAL).build();

    Builder preBindBuilder = MethodSpec.methodBuilder("handlePreBind")
        .addModifiers(PUBLIC)
        .addAnnotation(Override.class)
        .addParameter(viewHolderParam)
        .addParameter(boundObjectParam)
        .addParameter(TypeName.INT, "position");

    addHashCodeValidationIfNecessary(preBindBuilder,
        "The model was changed between being added to the controller and being bound.");

    ClassName viewType = getClassName("android.view.View");
    ClassName clickWrapperType = getClassName(WRAPPED_LISTENER_TYPE);
    ClassName modelClickListenerType = getClassName(MODEL_CLICK_LISTENER_TYPE);
    for (AttributeInfo attribute : classInfo.getAttributeInfo()) {
      if (!attribute.isViewClickListener()) {
        continue;
      }
      // Generate a View.OnClickListener that wraps the model click listener and set it as the
      // view click listener of the original model.

      String modelClickListenerField = attribute.getModelClickListenerName();
      preBindBuilder.beginControlFlow("if ($L != null)", modelClickListenerField);

      CodeBlock clickListenerCodeBlock = CodeBlock.of(
          "new $T($L) {\n"
              + "    @Override\n"
              + "    protected void wrappedOnClick($T v, $T originalClickListener) {\n"
              + "       originalClickListener.onClick($L.this, object, v,\n"
              + "              holder.getAdapterPosition());\n"
              + "       }\n"
              + "    }",
          clickWrapperType, modelClickListenerField, viewType, modelClickListenerType,
          classInfo.getGeneratedName());

      preBindBuilder
          .addStatement(attribute.setterCode(), clickListenerCodeBlock)
          .endControlFlow();
    }

    methods.add(preBindBuilder.build());

    Builder postBindBuilder = MethodSpec.methodBuilder("handlePostBind")
        .addModifiers(PUBLIC)
        .addAnnotation(Override.class)
        .addParameter(boundObjectParam)
        .addParameter(TypeName.INT, "position")
        .beginControlFlow("if ($L != null)", modelBindListenerFieldName())
        .addStatement("$L.onModelBound(this, object, position)", modelBindListenerFieldName())
        .endControlFlow();

    addHashCodeValidationIfNecessary(postBindBuilder,
        "The model was changed during the bind call.");

    methods.add(postBindBuilder
        .build());

    ParameterizedTypeName onBindListenerType = ParameterizedTypeName.get(
        getClassName(ON_BIND_MODEL_LISTENER_TYPE),
        classInfo.getParameterizedGeneratedName(),
        classInfo.getModelType()
    );
    ParameterSpec bindListenerParam = ParameterSpec.builder(onBindListenerType, "listener").build();

    MethodSpec.Builder onBind = MethodSpec.methodBuilder("onBind")
        .addJavadoc("Register a listener that will be called when this model is bound to a view.\n"
            + "<p>\n"
            + "The listener will contribute to this model's hashCode state per the {@link\n"
            + "com.airbnb.epoxy.EpoxyAttribute.Option#DoNotHash} rules.\n"
            + "<p>\n"
            + "You may clear the listener by setting a null value, or by calling "
            + "{@link #reset()}")
        .addModifiers(PUBLIC)
        .returns(classInfo.getParameterizedGeneratedName())
        .addParameter(bindListenerParam);

    addOnMutationCall(onBind)
        .addStatement("this.$L = listener", modelBindListenerFieldName())
        .addStatement("return this")
        .build();

    methods.add(onBind.build());

    ParameterSpec unbindObjectParam =
        ParameterSpec.builder(classInfo.getModelType(), "object").build();

    Builder unbindBuilder = MethodSpec.methodBuilder("unbind")
        .addAnnotation(Override.class)
        .addModifiers(PUBLIC)
        .addParameter(unbindObjectParam);

    unbindBuilder
        .addStatement("super.unbind(object)")
        .beginControlFlow("if ($L != null)", modelUnbindListenerFieldName())
        .addStatement("$L.onModelUnbound(this, object)", modelUnbindListenerFieldName())
        .endControlFlow();

    methods.add(unbindBuilder
        .build());

    ParameterizedTypeName onUnbindListenerType = ParameterizedTypeName.get(
        getClassName(ON_UNBIND_MODEL_LISTENER_TYPE),
        classInfo.getParameterizedGeneratedName(),
        classInfo.getModelType()
    );
    ParameterSpec unbindListenerParam =
        ParameterSpec.builder(onUnbindListenerType, "listener").build();

    Builder onUnbind = MethodSpec.methodBuilder("onUnbind")
        .addJavadoc("Register a listener that will be called when this model is unbound from a "
            + "view.\n"
            + "<p>\n"
            + "The listener will contribute to this model's hashCode state per the {@link\n"
            + "com.airbnb.epoxy.EpoxyAttribute.Option#DoNotHash} rules.\n"
            + "<p>\n"
            + "You may clear the listener by setting a null value, or by calling "
            + "{@link #reset()}")
        .addModifiers(PUBLIC)
        .returns(classInfo.getParameterizedGeneratedName());

    addOnMutationCall(onUnbind)
        .addParameter(unbindListenerParam)
        .addStatement("this.$L = listener", modelUnbindListenerFieldName())
        .addStatement("return this");

    methods.add(onUnbind.build());

    return methods;
  }

  private Iterable<MethodSpec> generateMethodsReturningClassType(GeneratedModelInfo info) {
    List<MethodSpec> methods = new ArrayList<>(info.getMethodsReturningClassType().size());

    for (GeneratedModelInfo.MethodInfo methodInfo : info.getMethodsReturningClassType()) {
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
  private Iterable<MethodSpec> generateDefaultMethodImplementations(GeneratedModelInfo info) {
    List<MethodSpec> methods = new ArrayList<>();

    addCreateHolderMethodIfNeeded(info, methods);
    addDefaultLayoutMethodIfNeeded(info, methods);

    return methods;
  }

  /**
   * If the model is a holder and doesn't implement the "createNewHolder" method we can generate a
   * default implementation by getting the class type and creating a new instance of it.
   */
  private void addCreateHolderMethodIfNeeded(GeneratedModelInfo modelClassInfo,
      List<MethodSpec> methods) {

    TypeElement originalClassElement = modelClassInfo.getSuperClassElement();
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

    createHolderMethod = createHolderMethod.toBuilder()
        .returns(modelClassInfo.getModelType())
        .addStatement("return new $T()", modelClassInfo.getModelType())
        .build();

    methods.add(createHolderMethod);
  }

  /**
   * If there is no existing implementation of getDefaultLayout we can generate an implementation.
   * This relies on a layout res being set in the @EpoxyModelClass annotation.
   */
  private void addDefaultLayoutMethodIfNeeded(GeneratedModelInfo modelInfo,
      List<MethodSpec> methods) {

    MethodSpec getDefaultLayoutMethod = MethodSpec.methodBuilder(
        GET_DEFAULT_LAYOUT_METHOD_NAME)
        .addAnnotation(Override.class)
        .addAnnotation(LayoutRes.class)
        .addModifiers(Modifier.PROTECTED)
        .returns(TypeName.INT)
        .build();

    // TODO: This is pretty ugly and could be abstracted/decomposed better. We could probably
    // make a small class to contain this logic, or build it into the model info classes
    LayoutResource layout;
    if (modelInfo instanceof DataBindingModelInfo) {
      layout = ((DataBindingModelInfo) modelInfo).getLayoutResource();
    } else {

      TypeElement superClassElement = modelInfo.getSuperClassElement();
      if (implementsMethod(superClassElement, getDefaultLayoutMethod, typeUtils)) {
        return;
      }

      TypeElement modelClassWithAnnotation = findSuperClassWithClassAnnotation(superClassElement);
      if (modelClassWithAnnotation == null) {
        errorLogger
            .logError("Model must use %s annotation if it does not implement %s. (class: %s)",
                EpoxyModelClass.class,
                GET_DEFAULT_LAYOUT_METHOD_NAME,
                modelInfo.getSuperClassName());
        return;
      }

      layout = layoutResourceProcessor
          .getLayoutInAnnotation(modelClassWithAnnotation, EpoxyModelClass.class);
    }

    getDefaultLayoutMethod = getDefaultLayoutMethod.toBuilder()
        .addStatement("return $L", layout.code)
        .build();

    methods.add(getDefaultLayoutMethod);
  }

  /**
   * Add `setDataBindingVariables` for DataBinding models if they haven't implemented it. This adds
   * the basic method and a method that checks for payload changes and only sets the variables that
   * changed.
   */
  private Iterable<MethodSpec> generateDataBindingMethodsIfNeeded(GeneratedModelInfo info) {
    if (!isDataBindingModel(info.getSuperClassElement())) {
      return Collections.emptyList();
    }

    MethodSpec bindVariablesMethod = MethodSpec.methodBuilder("setDataBindingVariables")
        .addAnnotation(Override.class)
        .addParameter(ClassName.get("android.databinding", "ViewDataBinding"), "binding")
        .addModifiers(Modifier.PROTECTED)
        .returns(TypeName.VOID)
        .build();

    // If the base method is already implemented don't bother checking for the payload method
    if (implementsMethod(info.getSuperClassElement(), bindVariablesMethod, typeUtils)) {
      return Collections.emptyList();
    }

    ClassName generatedModelClass = info.getGeneratedName();

    String moduleName = dataBindingModuleLookup.getModuleName(info.getSuperClassElement());

    Builder baseMethodBuilder = bindVariablesMethod.toBuilder();

    Builder payloadMethodBuilder = bindVariablesMethod
        .toBuilder()
        .addParameter(getClassName(UNTYPED_EPOXY_MODEL_TYPE), "previousModel")
        .beginControlFlow("if (!(previousModel instanceof $T))", generatedModelClass)
        .addStatement("setDataBindingVariables(binding)")
        .addStatement("return")
        .endControlFlow()
        .addStatement("$T that = ($T) previousModel", generatedModelClass, generatedModelClass);

    ClassName brClass = ClassName.get(moduleName, "BR");
    boolean validateAttributes = configManager.shouldValidateModelUsage();
    for (AttributeInfo attribute : info.getAttributeInfo()) {
      String attrName = attribute.getName();
      CodeBlock setVariableBlock =
          CodeBlock.of("binding.setVariable($T.$L, $L)", brClass, attrName, attribute.getterCode());

      if (validateAttributes) {
        // The setVariable method returns false if the variable id was not found in the layout.
        // We can warn the user about this if they have model validations turned on, otherwise
        // it fails silently.
        baseMethodBuilder
            .beginControlFlow("if (!$L)", setVariableBlock)
            .addStatement(
                "throw new $T(\"The attribute $L was defined in your data binding model ($L) but "
                    + "a data variable of that name was not found in the layout.\")",
                IllegalStateException.class, attrName, info.getSuperClassName())
            .endControlFlow();
      } else {
        baseMethodBuilder.addStatement("$L", setVariableBlock);
      }

      // Handle binding variables only if they changed
      startNotEqualsControlFlow(payloadMethodBuilder, attribute)
          .addStatement("$L", setVariableBlock)
          .endControlFlow();
    }

    ArrayList<MethodSpec> methods = new ArrayList<>();
    methods.add(baseMethodBuilder.build());
    methods.add(payloadMethodBuilder.build());
    return methods;
  }

  /**
   * Looks for {@link EpoxyModelClass} annotation in the original class and his parents.
   */
  private TypeElement findSuperClassWithClassAnnotation(TypeElement classElement) {
    if (!isEpoxyModel(classElement)) {
      return null;
    }

    EpoxyModelClass annotation = classElement.getAnnotation(EpoxyModelClass.class);
    if (annotation == null) {
      // This is an error. The model must have an EpoxyModelClass annotation
      // since getDefaultLayout is not implemented
      return null;
    }

    int layoutRes;
    try {
      layoutRes = annotation.layout();
    } catch (AnnotationTypeMismatchException e) {
      errorLogger.logError("Invalid layout value in %s annotation. (class: %s). %s: %s",
          EpoxyModelClass.class,
          classElement.getSimpleName(),
          e.getClass().getSimpleName(),
          e.getMessage());
      return null;
    }

    if (layoutRes != 0) {
      return classElement;
    }

    // This model did not specify a layout in its EpoxyModelClass annotation,
    // but its superclass might
    TypeElement superClass = (TypeElement) typeUtils.asElement(classElement.getSuperclass());
    TypeElement superClassWithAnnotation = findSuperClassWithClassAnnotation(superClass);

    if (superClassWithAnnotation != null) {
      return superClassWithAnnotation;
    }

    errorLogger
        .logError(
            "Model must specify a valid layout resource in the %s annotation. (class: %s)",
            EpoxyModelClass.class,
            classElement.getSimpleName());

    return null;
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

  private List<MethodSpec> generateSettersAndGetters(GeneratedModelInfo helperClass) {
    List<MethodSpec> methods = new ArrayList<>();

    for (AttributeInfo attributeInfo : helperClass.getAttributeInfo()) {
      if (attributeInfo.isViewClickListener()) {
        methods.add(generateSetClickModelListener(helperClass, attributeInfo));
      }
      if (attributeInfo.generateSetter() && !attributeInfo.hasFinalModifier()) {
        methods.add(generateSetter(helperClass, attributeInfo));
      }

      if (attributeInfo.generateGetter()) {
        methods.add(generateGetter(attributeInfo));
      }
    }

    return methods;
  }

  private MethodSpec generateSetClickModelListener(GeneratedModelInfo classInfo,
      AttributeInfo attribute) {
    String attributeName = attribute.getName();

    ParameterSpec param =
        ParameterSpec.builder(getModelClickListenerType(classInfo), attributeName, FINAL).build();

    Builder builder = MethodSpec.methodBuilder(attributeName)
        .addJavadoc("Set a click listener that will provide the parent view, model, and adapter "
            + "position of the clicked view. This will clear the normal View.OnClickListener "
            + "if one has been set")
        .addModifiers(PUBLIC)
        .returns(classInfo.getParameterizedGeneratedName())
        .addParameter(param)
        .addAnnotations(attribute.getSetterAnnotations());

    ClassName viewType = getClassName("android.view.View");
    ClassName clickWrapperType = getClassName(WRAPPED_LISTENER_TYPE);
    ClassName modelClickListenerType = getClassName(MODEL_CLICK_LISTENER_TYPE);

    // This creates a View.OnClickListener and sets it on the original model's click listener field.
    // This click listener has an empty onClick implementation, and will be replaced in
    // `handlePreBind`
    // when we can create a functional click listener with the view holder we bind to.
    // However, we use this stub version for now since it has the same hashCode implementation
    // as the future click listener, so when we create the real click listener in `handlePreBind`
    // it won't change the hashCode of the model.
    CodeBlock clickListenerCodeBlock = CodeBlock.of(
        "new $T($L)  {\n"
            + "        @Override\n"
            + "        protected void wrappedOnClick($T v, $T "
            + "originalClickListener) {\n"
            + "          \n"
            + "        }\n"
            + "      }",
        clickWrapperType, attributeName, viewType, modelClickListenerType);

    addOnMutationCall(builder)
        .addStatement("this.$L = $L", attribute.getModelClickListenerName(), attributeName)
        .beginControlFlow("if ($L == null)", attributeName)
        .addStatement(attribute.setterCode(), "null")
        .endControlFlow()
        .beginControlFlow("else")
        .addStatement(attribute.setterCode(), clickListenerCodeBlock)
        .endControlFlow()
        .addStatement("return this");

    return builder.build();
  }

  private MethodSpec generateEquals(GeneratedModelInfo helperClass) {
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

    startNotEqualsControlFlow(
        builder,
        false,
        getClassName(ON_BIND_MODEL_LISTENER_TYPE),
        modelBindListenerFieldName())
        .addStatement("return false")
        .endControlFlow();

    startNotEqualsControlFlow(
        builder,
        false,
        getClassName(ON_UNBIND_MODEL_LISTENER_TYPE),
        modelUnbindListenerFieldName())
        .addStatement("return false")
        .endControlFlow();

    for (AttributeInfo attributeInfo : helperClass.getAttributeInfo()) {
      TypeName type = attributeInfo.getTypeName();

      if (!attributeInfo.useInHash() && type.isPrimitive()) {
        continue;
      }

      startNotEqualsControlFlow(builder, attributeInfo)
          .addStatement("return false")
          .endControlFlow();
    }

    return builder
        .addStatement("return true")
        .build();
  }

  private static MethodSpec.Builder startNotEqualsControlFlow(MethodSpec.Builder methodBuilder,
      AttributeInfo attribute) {
    TypeName attributeType = attribute.getTypeName();
    boolean useHash = attributeType.isPrimitive() || attribute.useInHash();
    return startNotEqualsControlFlow(methodBuilder, useHash, attributeType, attribute.getterCode());
  }

  private static MethodSpec.Builder startNotEqualsControlFlow(Builder builder,
      boolean useObjectHashCode, TypeName type, String accessorCode) {

    if (useObjectHashCode) {
      if (type == FLOAT) {
        builder
            .beginControlFlow("if (Float.compare(that.$L, $L) != 0)", accessorCode, accessorCode);
      } else if (type == DOUBLE) {
        builder
            .beginControlFlow("if (Double.compare(that.$L, $L) != 0)", accessorCode, accessorCode);
      } else if (type.isPrimitive()) {
        builder.beginControlFlow("if ($L != that.$L)", accessorCode, accessorCode);
      } else if (type instanceof ArrayTypeName) {
        builder
            .beginControlFlow("if (!$T.equals($L, that.$L))", TypeName.get(Arrays.class),
                accessorCode, accessorCode);
      } else {
        builder
            .beginControlFlow("if ($L != null ? !$L.equals(that.$L) : that.$L != null)",
                accessorCode, accessorCode, accessorCode, accessorCode);
      }
    } else {
      builder
          .beginControlFlow("if (($L == null) != (that.$L == null))", accessorCode, accessorCode);
    }

    return builder;
  }

  private MethodSpec generateHashCode(GeneratedModelInfo helperClass) {
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
      if (attributeInfo.getTypeName() == DOUBLE) {
        builder.addStatement("long temp");
        break;
      }
    }

    for (AttributeInfo attributeInfo : helperClass.getAttributeInfo()) {
      TypeName type = attributeInfo.getTypeName();

      if (!attributeInfo.useInHash() && type.isPrimitive()) {
        continue;
      }

      addHashCodeLineForType(builder, attributeInfo.useInHash(), type, attributeInfo.getterCode());
    }

    return builder
        .addStatement("return result")
        .build();
  }

  private static void addHashCodeLineForType(Builder builder, boolean useObjectHashCode,
      TypeName type, String accessorCode) {
    if (useObjectHashCode) {
      if ((type == BYTE) || (type == CHAR) || (type == SHORT) || (type == INT)) {
        builder.addStatement("result = 31 * result + $L", accessorCode);
      } else if (type == LONG) {
        builder.addStatement("result = 31 * result + (int) ($L ^ ($L >>> 32))", accessorCode,
            accessorCode);
      } else if (type == FLOAT) {
        builder.addStatement("result = 31 * result + ($L != +0.0f "
            + "? Float.floatToIntBits($L) : 0)", accessorCode, accessorCode);
      } else if (type == DOUBLE) {
        builder.addStatement("temp = Double.doubleToLongBits($L)", accessorCode)
            .addStatement("result = 31 * result + (int) (temp ^ (temp >>> 32))");
      } else if (type == BOOLEAN) {
        builder.addStatement("result = 31 * result + ($L ? 1 : 0)", accessorCode);
      } else if (type instanceof ArrayTypeName) {
        builder.addStatement("result = 31 * result + Arrays.hashCode($L)", accessorCode);
      } else {
        builder
            .addStatement("result = 31 * result + ($L != null ? $L.hashCode() : 0)", accessorCode,
                accessorCode);
      }
    } else {
      builder.addStatement("result = 31 * result + ($L != null ? 1 : 0)", accessorCode);
    }
  }

  private MethodSpec generateToString(GeneratedModelInfo helperClass) {
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
        sb.append(String.format("\"%s=\" + %s +\n", attributeName, attributeInfo.getterCode()));
        first = false;
      } else {
        sb.append(String.format("\", %s=\" + %s +\n", attributeName, attributeInfo.getterCode()));
      }
    }

    sb.append("\"}\" + super.toString()");

    return builder
        .addStatement("return $L", sb.toString())
        .build();
  }

  private MethodSpec generateGetter(AttributeInfo data) {
    return MethodSpec.methodBuilder(data.getName())
        .addModifiers(PUBLIC)
        .returns(data.getTypeName())
        .addAnnotations(data.getGetterAnnotations())
        .addStatement("return $L", data.getterCode())
        .build();
  }

  private MethodSpec generateSetter(GeneratedModelInfo helperClass, AttributeInfo attribute) {
    String attributeName = attribute.getName();
    Builder builder = MethodSpec.methodBuilder(attributeName)
        .addModifiers(PUBLIC)
        .returns(helperClass.getParameterizedGeneratedName())
        .addParameter(ParameterSpec.builder(attribute.getTypeName(), attributeName)
            .addAnnotations(attribute.getSetterAnnotations()).build());

    addOnMutationCall(builder)
        .addStatement(attribute.setterCode(), attributeName);

    if (attribute.isViewClickListener()) {
      // Null out the model click listener since this view click listener should replace it
      builder.addStatement("this.$L = null", attribute.getModelClickListenerName());
    }

    // Call the super setter if it exists.
    // No need to do this if the attribute is private since we already called the super setter to
    // set it
    if (!attribute.isPrivate && attribute.hasSuperSetterMethod()) {
      builder.addStatement("super.$L($L)", attributeName, attributeName);
    }

    return builder
        .addStatement("return this")
        .build();
  }

  private MethodSpec generateReset(GeneratedModelInfo helperClass) {
    Builder builder = MethodSpec.methodBuilder("reset")
        .addAnnotation(Override.class)
        .addModifiers(PUBLIC)
        .returns(helperClass.getParameterizedGeneratedName())
        .addStatement("$L = null", modelBindListenerFieldName())
        .addStatement("$L = null", modelUnbindListenerFieldName());

    for (AttributeInfo attributeInfo : helperClass.getAttributeInfo()) {
      if (!attributeInfo.hasFinalModifier()) {
        builder.addStatement(attributeInfo.setterCode(),
            getDefaultValue(attributeInfo.getTypeName()));
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

  private MethodSpec.Builder addOnMutationCall(MethodSpec.Builder method) {
    return method.addStatement("onMutation()");
  }

  private MethodSpec.Builder addHashCodeValidationIfNecessary(MethodSpec.Builder method,
      String message) {
    if (configManager.shouldValidateModelUsage()) {
      method.addStatement("validateStateHasNotChangedSinceAdded($S, position)", message);
    }

    return method;
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
