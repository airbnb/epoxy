package com.airbnb.epoxy;

import com.airbnb.epoxy.GeneratedModelInfo.AttributeGroup;
import com.airbnb.epoxy.GeneratedModelWriter.BuilderHooks;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.MethodSpec.Builder;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import static com.airbnb.epoxy.Utils.UNTYPED_EPOXY_MODEL_TYPE;
import static com.airbnb.epoxy.Utils.belongToTheSamePackage;
import static com.airbnb.epoxy.Utils.getMethodOnClass;
import static com.airbnb.epoxy.Utils.isSubtype;
import static com.airbnb.epoxy.Utils.isSubtypeOfType;
import static com.airbnb.epoxy.Utils.notNull;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;

// TODO: (eli_hart 6/6/17) consider binding base model after view so the model can override view
// behavior (like changing width dynamically or styles)
// TODO: (eli_hart 5/30/17) allow param counts > 0 in setters
// TODO: (eli_hart 5/23/17) Allow default values to be methods
class ModelViewProcessor {
  private final Elements elements;
  private final Types types;
  private final ConfigManager configManager;
  private final ErrorLogger errorLogger;
  private final GeneratedModelWriter modelWriter;
  private final Map<Element, ModelViewInfo> modelClassMap = new LinkedHashMap<>();

  ModelViewProcessor(Elements elements, Types types, ConfigManager configManager,
      ErrorLogger errorLogger, GeneratedModelWriter modelWriter) {

    this.elements = elements;
    this.types = types;
    this.configManager = configManager;
    this.errorLogger = errorLogger;
    this.modelWriter = modelWriter;
  }

  Collection<? extends GeneratedModelInfo> process(RoundEnvironment roundEnv,
      List<GeneratedModelInfo> otherGeneratedModels) {
    modelClassMap.clear();

    processViewAnnotations(roundEnv);

    processSetterAnnotations(roundEnv);
    processResetAnnotations(roundEnv);

    updateViewsForInheritedViewAnnotations();

    // Group overloads after inheriting methods from super classes so those can be included in
    // the groups as well.
    groupOverloads();

    // Our code generation assumes that all attributes in a group are view attributes (and not
    // attributes inherited from a base model class), so this should be done after grouping
    // attributes, and these attributes should not be grouped.
    updatesViewsForInheritedBaseModelAttributes(otherGeneratedModels);

    writeJava();

    return modelClassMap.values();
  }

  private void processViewAnnotations(RoundEnvironment roundEnv) {
    for (Element viewElement : roundEnv.getElementsAnnotatedWith(ModelView.class)) {
      try {
        if (!validateViewElement(viewElement)) {
          continue;
        }

        modelClassMap.put(viewElement,
            new ModelViewInfo((TypeElement) viewElement, types, elements, errorLogger,
                configManager));
      } catch (Exception e) {
        errorLogger.logError(e, "Error creating model view info classes.");
      }
    }
  }

  private boolean validateViewElement(Element viewElement) {
    if (viewElement.getKind() != ElementKind.CLASS || !(viewElement instanceof TypeElement)) {
      errorLogger.logError("%s annotations can only be on a class (element: %s)", ModelView.class,
          viewElement.getSimpleName());
      return false;
    }

    Set<Modifier> modifiers = viewElement.getModifiers();
    if (modifiers.contains(PRIVATE)) {
      errorLogger.logError(
          "%s annotations must not be on private classes. (class: %s)",
          ModelView.class, viewElement.getSimpleName());
      return false;
    }

    // Nested classes must be static
    if (((TypeElement) viewElement).getNestingKind().isNested()) {
      errorLogger.logError(
          "Classes with %s annotations cannot be nested. (class: %s)",
          ModelView.class, viewElement.getSimpleName());
      return false;
    }

    if (!isSubtypeOfType(viewElement.asType(), Utils.ANDROID_VIEW_TYPE)) {
      errorLogger.logError(
          "Classes with %s annotations must extend android.view.View. (class: %s)",
          ModelView.class, viewElement.getSimpleName());
      return false;
    }

    return true;
  }

  private void processSetterAnnotations(RoundEnvironment roundEnv) {
    for (Element propMethod : roundEnv.getElementsAnnotatedWith(ModelProp.class)) {
      if (!validatePropElement(propMethod)) {
        continue;
      }

      ModelViewInfo info = getModelInfoForMethodElement(propMethod);
      if (info == null) {
        errorLogger.logError("%s annotation can only be used in classes annotated with %s (%s#%s)",
            ModelProp.class.getSimpleName(), ModelView.class.getSimpleName(),
            propMethod.getEnclosingElement().getSimpleName(), propMethod.getSimpleName());
        continue;
      }

      info.addProp((ExecutableElement) propMethod);
    }
  }

  private void groupOverloads() {
    for (ModelViewInfo viewInfo : modelClassMap.values()) {
      Map<String, List<AttributeInfo>> attributeGroups = new HashMap<>();

      // Track which groups are created manually by the user via a group annotation param.
      // We use this to check that more than one setter is in the group, since otherwise it doesn't
      // make sense to have a group and there is likely a typo we can catch for them
      Set<String> customGroups = new HashSet<>();

      for (AttributeInfo attributeInfo : viewInfo.attributeInfo) {
        ViewAttributeInfo setterInfo = (ViewAttributeInfo) attributeInfo;

        String groupKey = notNull(setterInfo.groupKey);
        if (groupKey.isEmpty()) {
          // Default to using the method name as the group name, so method overloads are grouped
          // together by default
          groupKey = setterInfo.viewSetterMethodName;
        } else {
          customGroups.add(groupKey);
        }

        List<AttributeInfo> group = attributeGroups.get(groupKey);
        if (group == null) {
          group = new ArrayList<>();
          attributeGroups.put(groupKey, group);
        }

        group.add(attributeInfo);
      }

      for (String customGroup : customGroups) {
        List<AttributeInfo> attributes = attributeGroups.get(customGroup);
        if (attributes.size() == 1) {
          ViewAttributeInfo attribute = (ViewAttributeInfo) attributes.get(0);
          errorLogger.logError(
              "Only one setter was included in the custom group '%s' at %s#%s. Groups should have "
                  + "at least 2 setters.", customGroup, viewInfo.viewElement.getSimpleName(),
              attribute.viewSetterMethodName);
        }
      }

      for (Entry<String, List<AttributeInfo>> entry : attributeGroups.entrySet()) {
        try {
          viewInfo.addAttributeGroup(entry.getKey(), entry.getValue());
        } catch (EpoxyProcessorException e) {
          errorLogger.logError(e);
        }
      }
    }
  }

  private boolean validatePropElement(Element methodElement) {
    return validateExecutableElement(methodElement, ModelProp.class, 1);
  }

  private boolean validateExecutableElement(Element element, Class<?> annotationClass,
      int paramCount) {
    if (!(element instanceof ExecutableElement)) {
      errorLogger.logError("%s annotations can only be on a method (element: %s)", annotationClass,
          element.getSimpleName());
      return false;
    }

    ExecutableElement executableElement = (ExecutableElement) element;
    if (executableElement.getParameters().size() != paramCount) {
      errorLogger.logError("Methods annotated with %s must have exactly %s parameter (method: %s)",
          annotationClass, paramCount, element.getSimpleName());
      return false;
    }

    Set<Modifier> modifiers = element.getModifiers();
    if (modifiers.contains(STATIC) || modifiers.contains(PRIVATE)) {
      errorLogger.logError("Methods annotated with %s cannot be private or static (method: %s)",
          annotationClass, element.getSimpleName());
      return false;
    }

    return true;
  }

  private void processResetAnnotations(RoundEnvironment roundEnv) {
    for (Element recycleMethod : roundEnv.getElementsAnnotatedWith(OnViewRecycled.class)) {
      if (!validateResetElement(recycleMethod)) {
        continue;
      }

      ModelViewInfo info = getModelInfoForMethodElement(recycleMethod);
      if (info == null) {
        errorLogger.logError("%s annotation can only be used in classes annotated with %s",
            ModelProp.class, ModelView.class);
        continue;
      }

      info.addOnRecycleMethod((ExecutableElement) recycleMethod);
    }
  }

  /** Include props and reset methods from super class views. */
  private void updateViewsForInheritedViewAnnotations() {
    for (ModelViewInfo view : modelClassMap.values()) {
      Set<ModelViewInfo> otherViews = new HashSet<>(modelClassMap.values());
      otherViews.remove(view);

      for (ModelViewInfo otherView : otherViews) {
        if (!isSubtype(view.viewElement, otherView.viewElement, types)) {
          continue;
        }

        view.resetMethodNames.addAll(otherView.resetMethodNames);

        boolean samePackage =
            belongToTheSamePackage(view.viewElement, otherView.viewElement, elements);
        for (AttributeInfo otherAttribute : otherView.attributeInfo) {
          if (otherAttribute.packagePrivate && !samePackage) {
            ViewAttributeInfo otherSetterInfo = (ViewAttributeInfo) otherAttribute;
            // It would be an unlikely case for someone to not want views to inherit superclass
            // setters, so if they are package private and can't be inherited it is probably
            // just accidental and we can point it out instead of silently excluding it
            errorLogger.logError(
                "View %s is in a different package then %s and cannot inherit its package "
                    + "private setter %s",
                view.viewElement.getSimpleName(), otherView.viewElement.getSimpleName(),
                otherSetterInfo.viewSetterMethodName);
          } else {
            // We don't want the attribute from the super class replacing an attribute in the
            // subclass if the subclass overrides it, since the subclass definition could include
            // different annotation parameter settings.
            view.addAttributeIfNotExists(otherAttribute);
          }
        }
      }
    }
  }

  /**
   * If a view defines a base model that it's generated model should extend we need to check if that
   * base model has {@link com.airbnb.epoxy.EpoxyAttribute} fields and include those in our model if
   * so.
   */
  private void updatesViewsForInheritedBaseModelAttributes(
      List<GeneratedModelInfo> otherGeneratedModels) {

    for (ModelViewInfo modelViewInfo : modelClassMap.values()) {
      for (GeneratedModelInfo otherGeneratedModel : otherGeneratedModels) {
        if (isSubtype(modelViewInfo.superClassElement, otherGeneratedModel.superClassElement,
            types)) {
          modelViewInfo.addAttributes(otherGeneratedModel.attributeInfo);
        }
      }
    }
  }

  private boolean validateResetElement(Element resetMethod) {
    return validateExecutableElement(resetMethod, OnViewRecycled.class, 0);
  }

  private void writeJava() {
    for (final ModelViewInfo modelInfo : modelClassMap.values()) {
      try {
        modelWriter.generateClassForModel(modelInfo, new BuilderHooks() {
          @Override
          boolean addToBindMethod(Builder methodBuilder, ParameterSpec boundObjectParam) {

            for (AttributeGroup attributeGroup : modelInfo.attributeGroups) {
              int attrCount = attributeGroup.attributes.size();
              if (attrCount == 1) {
                ViewAttributeInfo viewAttribute =
                    (ViewAttributeInfo) attributeGroup.attributes.get(0);
                methodBuilder
                    .addCode(buildCodeBlockToSetAttribute(boundObjectParam, viewAttribute));
              } else {
                for (int i = 0; i < attrCount; i++) {
                  ViewAttributeInfo viewAttribute =
                      (ViewAttributeInfo) attributeGroup.attributes.get(i);

                  if (i == 0) {
                    methodBuilder.beginControlFlow("if ($L)",
                        GeneratedModelWriter.isAttributeSetCode(modelInfo, viewAttribute));
                  } else if (i == attrCount - 1 && attributeGroup.isRequired) {
                    methodBuilder.beginControlFlow("else");
                  } else {
                    methodBuilder.beginControlFlow("else if ($L)",
                        GeneratedModelWriter.isAttributeSetCode(modelInfo, viewAttribute));
                  }

                  methodBuilder
                      .addCode(buildCodeBlockToSetAttribute(boundObjectParam, viewAttribute))
                      .endControlFlow();
                }

                if (!attributeGroup.isRequired) {
                  ViewAttributeInfo defaultAttribute =
                      (ViewAttributeInfo) attributeGroup.defaultAttribute;

                  methodBuilder.beginControlFlow("else")
                      .addStatement("$L.$L($L)", boundObjectParam.name,
                          defaultAttribute.viewSetterMethodName,
                          defaultAttribute.codeToSetDefault.value())
                      .endControlFlow();
                }
              }
            }

            return true;
          }

          @Override
          boolean addToBindWithDiffMethod(Builder methodBuilder, ParameterSpec boundObjectParam,
              ParameterSpec previousModelParam) {

            ClassName generatedModelClass = modelInfo.generatedClassName;
            methodBuilder
                .beginControlFlow("if (!($L instanceof $T))", previousModelParam.name,
                    generatedModelClass)
                .addStatement("bind($L)", boundObjectParam.name)
                .addStatement("return")
                .endControlFlow()
                .addStatement("$T that = ($T) previousModel", generatedModelClass,
                    generatedModelClass);

            // We want to make sure the base model has its bind method called as well. Since the
            // user can provide a custom base class we aren't sure if it implements diff binding.
            // If so we should call it, but if not, calling it would invoke the default
            // EpoxyModel implementation which calls normal "bind". Doing that would force a full
            // bind!!! So we mustn't do that. So, we only call the super diff binding if we think
            // it's a custom implementation.
            if (modelImplementsBindWithDiff(modelInfo.superClassElement, methodBuilder.build())) {
              methodBuilder.addStatement("super.bind($L, $L)", boundObjectParam.name,
                  previousModelParam.name);
            } else {
              methodBuilder.addStatement("super.bind($L)", boundObjectParam.name);
            }

            for (AttributeGroup attributeGroup : modelInfo.attributeGroups) {
              methodBuilder.addCode("\n");

              if (attributeGroup.attributes.size() == 1) {
                AttributeInfo attributeInfo = attributeGroup.attributes.get(0);

                if (attributeInfo instanceof ViewAttributeInfo
                    && ((ViewAttributeInfo) attributeInfo).generateStringOverloads) {
                  methodBuilder
                      .beginControlFlow("if (!$L.equals(that.$L))", attributeInfo.getterCode(),
                          attributeInfo.getterCode());
                } else {
                  GeneratedModelWriter.startNotEqualsControlFlow(methodBuilder, attributeInfo);
                }

                methodBuilder.addCode(buildCodeBlockToSetAttribute(boundObjectParam,
                    (ViewAttributeInfo) attributeInfo))
                    .endControlFlow();
              } else {
                methodBuilder.beginControlFlow("if ($L.equals(that.$L))",
                    GeneratedModelWriter.ATTRIBUTES_BITSET_FIELD_NAME,
                    GeneratedModelWriter.ATTRIBUTES_BITSET_FIELD_NAME);

                boolean firstAttribute = true;
                for (AttributeInfo attribute : attributeGroup.attributes) {
                  if (!firstAttribute) {
                    methodBuilder.addCode(" else ");
                  }
                  firstAttribute = false;

                  methodBuilder.beginControlFlow("if ($L)",
                      GeneratedModelWriter.isAttributeSetCode(modelInfo, attribute));

                  GeneratedModelWriter.startNotEqualsControlFlow(methodBuilder, attribute)
                      .addCode(buildCodeBlockToSetAttribute(boundObjectParam,
                          (ViewAttributeInfo) attribute))
                      .endControlFlow()
                      .endControlFlow();
                }

                methodBuilder.endControlFlow()
                    .beginControlFlow("else");

                firstAttribute = true;
                for (AttributeInfo attribute : attributeGroup.attributes) {
                  if (!firstAttribute) {
                    methodBuilder.addCode(" else ");
                  }
                  firstAttribute = false;

                  methodBuilder.beginControlFlow("if ($L && !that.$L)",
                      GeneratedModelWriter.isAttributeSetCode(modelInfo, attribute),
                      GeneratedModelWriter.isAttributeSetCode(modelInfo, attribute))
                      .addCode(buildCodeBlockToSetAttribute(boundObjectParam,
                          (ViewAttributeInfo) attribute))
                      .endControlFlow();
                }

                if (!attributeGroup.isRequired) {
                  ViewAttributeInfo defaultAttribute =
                      (ViewAttributeInfo) attributeGroup.defaultAttribute;

                  methodBuilder.beginControlFlow("else")
                      .addStatement("$L.$L($L)", boundObjectParam.name,
                          defaultAttribute.viewSetterMethodName,
                          defaultAttribute.codeToSetDefault.value())
                      .endControlFlow();
                }

                methodBuilder.endControlFlow();
              }
            }

            return true;
          }

          @Override
          void addToUnbindMethod(MethodSpec.Builder unbindBuilder, String unbindParamName) {
            for (ViewAttributeInfo viewAttribute : modelInfo.getViewAttributes()) {
              if (viewAttribute.resetWithNull) {
                unbindBuilder.addStatement("$L.$L(null)", unbindParamName,
                    viewAttribute.viewSetterMethodName);
              }
            }

            addResetMethodsToBuilder(unbindBuilder, modelInfo, unbindParamName);
          }

          @Override
          void beforeFinalBuild(TypeSpec.Builder builder) {
            if (modelInfo.saveViewState) {
              builder.addMethod(buildSaveStateMethod());
            }

            if (modelInfo.fullSpanSize) {
              builder.addMethod(buildFullSpanSizeMethod());
            }
          }
        });
      } catch (Exception e) {
        errorLogger.logError(new EpoxyProcessorException(e, "Error generating model view classes"));
      }
    }
  }

  boolean modelImplementsBindWithDiff(TypeElement clazz, MethodSpec bindWithDiffMethod) {
    ExecutableElement methodOnClass = getMethodOnClass(clazz, bindWithDiffMethod, types, elements);
    if (methodOnClass == null) {
      return false;
    }

    Set<Modifier> modifiers = methodOnClass.getModifiers();
    if (modifiers.contains(Modifier.ABSTRACT)) {
      return false;
    }

    TypeElement enclosingElement = (TypeElement) methodOnClass.getEnclosingElement();
    if (enclosingElement == null) {
      return false;
    }

    // As long as the implementation is not on the base EpoxyModel we consider it a custom
    // implementation
    return !enclosingElement.getQualifiedName().toString().equals(UNTYPED_EPOXY_MODEL_TYPE);
  }

  private static CodeBlock buildCodeBlockToSetAttribute(ParameterSpec boundObjectParam,
      ViewAttributeInfo viewAttribute) {
    return CodeBlock.of("$L.$L($L);\n", boundObjectParam.name,
        viewAttribute.viewSetterMethodName,
        getValueToSetOnView(viewAttribute, boundObjectParam));
  }

  private static String getValueToSetOnView(ViewAttributeInfo viewAttribute,
      ParameterSpec boundObjectParam) {
    String fieldName = viewAttribute.getFieldName();

    if (viewAttribute.generateStringOverloads) {
      return fieldName + ".toString(" + boundObjectParam.name + ".getContext())";
    } else {
      return fieldName;
    }
  }

  private MethodSpec buildSaveStateMethod() {
    return MethodSpec.methodBuilder("shouldSaveViewState")
        .addAnnotation(Override.class)
        .returns(TypeName.BOOLEAN)
        .addModifiers(PUBLIC)
        .addStatement("return true")
        .build();
  }

  private MethodSpec buildFullSpanSizeMethod() {
    return MethodSpec.methodBuilder("getSpanSize")
        .addAnnotation(Override.class)
        .returns(TypeName.INT)
        .addModifiers(PUBLIC)
        .addParameter(TypeName.INT, "totalSpanCount")
        .addParameter(TypeName.INT, "position")
        .addParameter(TypeName.INT, "itemCount")
        .addStatement("return totalSpanCount")
        .build();
  }

  private void addResetMethodsToBuilder(Builder builder, ModelViewInfo modelViewInfo,
      String unbindParamName) {
    for (String methodName : modelViewInfo.getResetMethodNames()) {
      builder.addStatement(unbindParamName + "." + methodName + "()");
    }
  }

  private ModelViewInfo getModelInfoForMethodElement(Element element) {
    Element enclosingElement = element.getEnclosingElement();
    if (enclosingElement == null) {
      return null;
    }

    return modelClassMap.get(enclosingElement);
  }
}
