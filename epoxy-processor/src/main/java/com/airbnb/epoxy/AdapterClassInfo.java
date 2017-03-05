package com.airbnb.epoxy;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

class AdapterClassInfo {
  private static final String GENERATED_HELPER_CLASS_SUFFIX = "_EpoxyHelper";
  private final Elements elementUtils;
  final List<AdapterModelField> models = new ArrayList<>();
  final ClassName generatedClassName;
  final TypeName adapterClassType;
  final TypeElement adapterClassElement;

  AdapterClassInfo(Elements elementUtils, TypeElement adapterClassElement) {
    this.elementUtils = elementUtils;
    generatedClassName = getGeneratedClassName(adapterClassElement);
    adapterClassType = TypeName.get(adapterClassElement.asType());
    this.adapterClassElement = adapterClassElement;
  }

  void addModel(AdapterModelField adapterModelField) {
    models.add(adapterModelField);
  }

  private ClassName getGeneratedClassName(TypeElement adapterClass) {
    String packageName = elementUtils.getPackageOf(adapterClass).getQualifiedName().toString();

    int packageLen = packageName.length() + 1;
    String className =
        adapterClass.getQualifiedName().toString().substring(packageLen).replace('.', '$');

    return ClassName.get(packageName, className + GENERATED_HELPER_CLASS_SUFFIX);
  }

  @Override
  public String toString() {
    return "AdapterClassInfo{"
        + "models=" + models
        + ", generatedClassName=" + generatedClassName
        + ", adapterClassType=" + adapterClassType
        + '}';
  }
}
