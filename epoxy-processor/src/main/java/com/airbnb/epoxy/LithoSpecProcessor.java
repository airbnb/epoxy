package com.airbnb.epoxy;

import com.airbnb.epoxy.GeneratedModelWriter.BeforeBuildCallback;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec.Builder;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import static com.airbnb.epoxy.ClassNames.EPOXY_LITHO_MODEL;
import static com.airbnb.epoxy.Utils.getAnnotationClass;

class LithoSpecProcessor {

  private final Elements elementUtils;
  private final Types typeUtils;
  private final ConfigManager configManager;
  private final ErrorLogger errorLogger;
  private final GeneratedModelWriter modelWriter;
  private Class<? extends Annotation> layoutSpecAnnotationClass;

  LithoSpecProcessor(Elements elementUtils, Types typeUtils,
      ConfigManager configManager, ErrorLogger errorLogger, GeneratedModelWriter modelWriter) {

    this.elementUtils = elementUtils;
    this.typeUtils = typeUtils;
    this.configManager = configManager;
    this.errorLogger = errorLogger;
    this.modelWriter = modelWriter;
  }

  Collection<LithoModelInfo> processSpecs(RoundEnvironment roundEnv) {
    Map<TypeElement, LithoModelInfo> modelInfoMap = new LinkedHashMap<>();

    if (!hasLithoEpoxyDependency()) {
      // If the epoxy-litho module has not been included then we don't have access to the Epoxy
      // litho model and can't build a model that extends it
      return new ArrayList<>();
    }

    layoutSpecAnnotationClass =
        getAnnotationClass(ClassNames.LITHO_ANNOTATION_LAYOUT_SPEC);
    if (layoutSpecAnnotationClass == null) {
      // There is no dependency on Litho so there aren't any litho components to check for
      return new ArrayList<>();
    }

    for (Element lithoLayout : roundEnv.getElementsAnnotatedWith(layoutSpecAnnotationClass)) {
      if (!(lithoLayout instanceof TypeElement)) {
        continue;
      }

      TypeElement typeElement = (TypeElement) lithoLayout;
      modelInfoMap.put(typeElement, new LithoModelInfo(typeUtils, elementUtils, typeElement));
    }

    Class<? extends Annotation> propClass = getAnnotationClass(ClassNames.LITHO_ANNOTATION_PROP);
    for (Element propElement : roundEnv.getElementsAnnotatedWith(propClass)) {
      LithoModelInfo lithoModelInfo = getModelInfoForProp(modelInfoMap, propElement);
      if (lithoModelInfo != null) {
        lithoModelInfo.addProp(propElement);
      }
    }

    for (Entry<TypeElement, LithoModelInfo> modelInfoEntry : modelInfoMap.entrySet()) {
      try {
        final LithoModelInfo modelInfo = modelInfoEntry.getValue();
        modelWriter.generateClassForModel(modelInfo, new BeforeBuildCallback() {
          @Override
          public void modifyBuilder(Builder builder) {
            updateGeneratedClassForLithoComponent(modelInfo, builder);
          }
        });
      } catch (Exception e) {
        errorLogger.logError(e, "Error generating model classes");
      }
    }

    return modelInfoMap.values();
  }

  private boolean hasLithoEpoxyDependency() {
    // Only true if the epoxy-litho module is included in dependencies
    return Utils.getClass(EPOXY_LITHO_MODEL) != null;
  }

  private void updateGeneratedClassForLithoComponent(LithoModelInfo modelInfo,
      Builder classBuilder) {
    // Adding the "buildComponent" method
    MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("buildComponent")
        .addAnnotation(Override.class)
        .addModifiers(Modifier.PROTECTED)
        .returns(
            ParameterizedTypeName.get(ClassNames.LITHO_COMPONENT, modelInfo.lithoComponentName)
        )
        .addParameter(ClassNames.LITHO_COMPONENT_CONTEXT, "context")
        .addCode("return $T.create(context)", modelInfo.lithoComponentName);

    for (AttributeInfo attributeInfo : modelInfo.attributeInfo) {
      methodBuilder.addCode(".$L($L)", attributeInfo.getName(), attributeInfo.getName());
    }

    methodBuilder.addStatement(".build()");

    classBuilder.addMethod(methodBuilder.build());
  }

  private LithoModelInfo getModelInfoForProp(Map<TypeElement, LithoModelInfo> modelInfoMap,
      Element propElement) {
    Element methodElement = propElement.getEnclosingElement();
    if (methodElement == null) {
      return null;
    }

    Element classElement = methodElement.getEnclosingElement();

    if (classElement.getAnnotation(layoutSpecAnnotationClass) == null) {
      return null;
    }

    return modelInfoMap.get(classElement);
  }
}
