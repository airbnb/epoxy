package com.airbnb.epoxy;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Parameterizable;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import static com.airbnb.epoxy.Utils.isEpoxyModel;

class ModelViewInfo extends GeneratedModelInfo {
  final List<String> resetMethodNames = new ArrayList<>();
  final TypeElement viewElement;
  final Types typeUtils;
  final Elements elements;
  final ErrorLogger errorLogger;
  final ConfigManager configManager;
  final boolean saveViewState;
  final ModelView viewAnnotation;
  final boolean fullSpanSize;

  ModelViewInfo(TypeElement viewElement, Types typeUtils, Elements elements,
      ErrorLogger errorLogger, ConfigManager configManager) {

    viewAnnotation = viewElement.getAnnotation(ModelView.class);
    this.viewElement = viewElement;
    this.typeUtils = typeUtils;
    this.elements = elements;
    this.errorLogger = errorLogger;
    this.configManager = configManager;

    superClassElement = lookUpSuperClassElement();
    this.superClassName = ParameterizedTypeName
        .get(ClassName.get(superClassElement), TypeName.get(viewElement.asType()));

    generatedClassName = buildGeneratedModelName(viewElement, elements);
    // We don't have any type parameters on our generated model
    this.parametrizedClassName = generatedClassName;
    shouldGenerateModel = !viewElement.getModifiers().contains(Modifier.ABSTRACT);

    collectMethodsReturningClassType(superClassElement, typeUtils);

    // The bound type is the type of this view
    boundObjectTypeName = ClassName.get(viewElement.asType());

    saveViewState = viewAnnotation.saveViewState();
    fullSpanSize = viewAnnotation.fullSpan();
    includeOtherLayoutOptions = configManager.includeAlternateLayoutsForViews(viewElement);
  }

  private TypeElement lookUpSuperClassElement() {
    TypeElement defaultSuper = (TypeElement) Utils.getElementByName(ClassNames.EPOXY_MODEL_UNTYPED,
        elements, typeUtils);

    // Unfortunately we have to do this weird try/catch to get the class type
    TypeMirror classToExtend = null;
    try {
      viewAnnotation.baseModelClass(); // this should throw
    } catch (MirroredTypeException mte) {
      classToExtend = mte.getTypeMirror();
    }

    if (classToExtend == null
        || classToExtend.toString().equals(Void.class.getCanonicalName())) {

      TypeMirror defaultBaseModel = configManager.getDefaultBaseModel(viewElement);
      if (defaultBaseModel != null) {
        classToExtend = defaultBaseModel;
      } else {
        return defaultSuper;
      }
    }

    if (!isEpoxyModel(classToExtend)) {
      errorLogger
          .logError("The base model provided to an %s must extend EpoxyModel, but was %s (%s).",
              ModelView.class.getSimpleName(), classToExtend, viewElement.getSimpleName());
      return defaultSuper;
    }

    if (!validateSuperClassIsTypedCorrectly(classToExtend)) {
      errorLogger.logError("The base model provided to an %s must have View as its type (%s).",
          ModelView.class.getSimpleName(), viewElement.getSimpleName());
      return defaultSuper;
    }

    return (TypeElement) typeUtils.asElement(classToExtend);
  }

  /** The super class that our generated model extends from must have View as its only type. */
  private boolean validateSuperClassIsTypedCorrectly(TypeMirror classType) {
    Element classElement = typeUtils.asElement(classType);
    if (!(classElement instanceof Parameterizable)) {
      return false;
    }

    Parameterizable parameterizable = (Parameterizable) classElement;
    List<? extends TypeParameterElement> typeParameters = parameterizable.getTypeParameters();
    if (typeParameters.size() != 1) {
      // TODO: (eli_hart 6/15/17) It should be valid to have multiple or no types as long as they
      // are correct, but that should be a rare case
      return false;
    }

    TypeParameterElement typeParam = typeParameters.get(0);
    List<? extends TypeMirror> bounds = typeParam.getBounds();
    if (bounds.isEmpty()) {
      // Any type is allowed, so View wil work
      return true;
    }

    TypeMirror typeMirror = bounds.get(0);
    TypeMirror viewType = Utils.getTypeMirror(ClassNames.ANDROID_VIEW, elements, typeUtils);
    return typeUtils.isAssignable(viewType, typeMirror)
        || typeUtils.isSubtype(typeMirror, viewType);
  }

  private ClassName buildGeneratedModelName(TypeElement viewElement, Elements elementUtils) {
    String packageName = elementUtils.getPackageOf(viewElement).getQualifiedName().toString();

    String className = viewElement.getSimpleName().toString();
    className += "Model" + GENERATED_CLASS_NAME_SUFFIX;

    return ClassName.get(packageName, className);
  }

  void addProp(ExecutableElement propMethod) {
    addAttribute(new ViewAttributeInfo(this, propMethod, typeUtils, elements, errorLogger));
  }

  void addOnRecycleMethod(ExecutableElement resetMethod) {
    resetMethodNames.add(resetMethod.getSimpleName().toString());
  }

  LayoutResource getLayoutResource(LayoutResourceProcessor layoutResourceProcessor) {
    ModelView annotation = viewElement.getAnnotation(ModelView.class);
    int layoutValue = annotation.defaultLayout();
    if (layoutValue != 0) {
      return layoutResourceProcessor.getLayoutInAnnotation(viewElement, ModelView.class);
    }

    PackageModelViewSettings modelViewConfig =
        configManager.getModelViewConfig(viewElement);

    if (modelViewConfig != null) {
      return modelViewConfig.getNameForView(viewElement);
    }

    errorLogger.logError("Unable to get layout resource for view %s", viewElement.getSimpleName());
    return new LayoutResource(0);
  }

  List<String> getResetMethodNames() {
    return resetMethodNames;
  }

  List<ViewAttributeInfo> getViewAttributes() {
    List<ViewAttributeInfo> result = new ArrayList<>(attributeInfo.size());
    for (AttributeInfo info : attributeInfo) {
      if (info instanceof ViewAttributeInfo) {
        result.add((ViewAttributeInfo) info);
      }
    }

    return result;
  }
}
