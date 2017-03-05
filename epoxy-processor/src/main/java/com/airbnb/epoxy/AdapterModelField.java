package com.airbnb.epoxy;

import com.squareup.javapoet.TypeName;

import javax.lang.model.element.Element;

class AdapterModelField {

  String fieldName;
  TypeName typeName;

  AdapterModelField(Element element) {
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
