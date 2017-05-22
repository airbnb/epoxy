package com.airbnb.epoxy;

import com.squareup.javapoet.ClassName;

import javax.lang.model.element.TypeElement;

class PackageModelViewNameSettings {
  final ClassName rLayoutClass;
  final String layoutName;

  PackageModelViewNameSettings(ClassName rLayoutClass, String layoutName) {
    this.rLayoutClass = rLayoutClass;
    this.layoutName = layoutName;
  }

  LayoutResource getNameForView(TypeElement viewElement) {
    String viewName = Utils.toSnakeCase(viewElement.getSimpleName().toString());
    String resourceName = layoutName.replace("%s", viewName);
    return new LayoutResource(rLayoutClass, resourceName, 0);
  }
}
