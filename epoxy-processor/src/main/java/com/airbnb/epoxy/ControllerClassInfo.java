package com.airbnb.epoxy;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

class ControllerClassInfo {
  private static final String GENERATED_HELPER_CLASS_SUFFIX = "_EpoxyHelper";
  private final Elements elementUtils;
  final Set<ControllerModelField> models = new HashSet<>();
  final ClassName generatedClassName;
  final TypeName controllerClassType;
  final TypeElement controllerClassElement;

  ControllerClassInfo(Elements elementUtils, TypeElement controllerClassElement) {
    this.elementUtils = elementUtils;
    generatedClassName = getGeneratedClassName(controllerClassElement);
    controllerClassType = TypeName.get(controllerClassElement.asType());
    this.controllerClassElement = controllerClassElement;
  }

  void addModel(ControllerModelField controllerModelField) {
    models.add(controllerModelField);
  }

  void addModels(Collection<ControllerModelField> controllerModelFields) {
    models.addAll(controllerModelFields);
  }

  private ClassName getGeneratedClassName(TypeElement controllerClass) {
    String packageName = elementUtils.getPackageOf(controllerClass).getQualifiedName().toString();

    int packageLen = packageName.length() + 1;
    String className =
        controllerClass.getQualifiedName().toString().substring(packageLen).replace('.', '$');

    return ClassName.get(packageName, className + GENERATED_HELPER_CLASS_SUFFIX);
  }

  @Override
  public String toString() {
    return "ControllerClassInfo{"
        + "models=" + models
        + ", generatedClassName=" + generatedClassName
        + ", controllerClassType=" + controllerClassType
        + '}';
  }
}
