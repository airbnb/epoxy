package com.airbnb.epoxy;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import static com.airbnb.epoxy.ClassNames.EPOXY_LITHO_MODEL;

class LithoModelInfo extends GeneratedModelInfo {

  final ClassName lithoComponentName;

  LithoModelInfo(Types typeUtils, Elements elementUtils, TypeElement layoutSpecClassElement) {
    superClassElement =
        (TypeElement) Utils.getElementByName(EPOXY_LITHO_MODEL, elementUtils, typeUtils);

    lithoComponentName = getLithoComponentName(elementUtils, layoutSpecClassElement);
    this.superClassName = ParameterizedTypeName.get(EPOXY_LITHO_MODEL, lithoComponentName);

    generatedClassName = buildGeneratedModelName(lithoComponentName);
    // We don't have any type parameters on our generated litho model
    this.parametrizedClassName = generatedClassName;
    shouldGenerateModel = true;

    collectMethodsReturningClassType(superClassElement, typeUtils);

    // The bound type is simply a LithoView
    boundObjectTypeName = ClassName.get("com.facebook.litho", "LithoView");
  }

  /**
   * The name of the component that is generated for the layout spec. It will be in the same
   * package, and with the "Spec" term removed from the name.
   */
  private ClassName getLithoComponentName(Elements elementUtils,
      TypeElement layoutSpecClassElement) {
    String packageName =
        elementUtils.getPackageOf(layoutSpecClassElement).getQualifiedName().toString();

    // Litho doesn't appear to allow specs as nested classes, so we don't check for nested
    // class naming here
    String className = layoutSpecClassElement.getSimpleName().toString();

    if (className.endsWith("Spec")) {
      className = className.substring(0, className.lastIndexOf("Spec"));
    }

    return ClassName.get(packageName, className);
  }

  private ClassName buildGeneratedModelName(ClassName componentName) {
    String simpleName = componentName.simpleName() + "Model" + GENERATED_CLASS_NAME_SUFFIX;
    return ClassName.get(componentName.packageName(), simpleName);
  }

  void addProp(Element propElement, HashCodeValidator hashCodeValidator) {
    attributeInfo.add(new LithoModelAttributeInfo(this, propElement, hashCodeValidator));
  }
}
