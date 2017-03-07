package com.airbnb.epoxy;

import com.squareup.javapoet.TypeName;

import javax.lang.model.element.Element;

class ControllerModelField {

  String fieldName;
  TypeName typeName;

  ControllerModelField(Element element) {
    fieldName = element.getSimpleName().toString();
    typeName = TypeName.get(element.asType());
  }

  @Override
  public String toString() {
    return "AdapterModelField{"
        + "name='" + fieldName + '\''
        + ", typeName=" + typeName
        + '}';
  }
}
