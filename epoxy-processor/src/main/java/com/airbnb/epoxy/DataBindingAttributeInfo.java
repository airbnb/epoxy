package com.airbnb.epoxy;

import com.squareup.javapoet.TypeName;

import javax.lang.model.element.Element;

class DataBindingAttributeInfo extends AttributeInfo {

  public DataBindingAttributeInfo(DataBindingModelInfo modelInfo, Element dataBindingElement) {
    name = dataBindingElement.getSimpleName().toString();
    typeName = TypeName.get(dataBindingElement.asType());
    typeMirror = dataBindingElement.asType();
    modelName = modelInfo.getGeneratedName().simpleName();
    modelPackageName = modelInfo.getGeneratedName().packageName();
    useInHash = true;
    ignoreRequireHashCode = false;
    generateSetter = true;
    generateGetter = true;
    hasFinalModifier = false;
    packagePrivate = false;
  }
}
