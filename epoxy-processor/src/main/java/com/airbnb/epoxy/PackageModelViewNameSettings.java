package com.airbnb.epoxy;

import com.squareup.javapoet.ClassName;

import javax.lang.model.element.TypeElement;

class PackageModelViewNameSettings {
  final ClassName rLayoutClass;
  final String layoutName;

  PackageModelViewNameSettings(ClassName rLayoutClass, String layoutName) {
    // The R class may be R or R2. We create the class name again to make sure we don't use R2.
    this.rLayoutClass = ClassName.get(rLayoutClass.packageName(), "R", "layout");
    this.layoutName = layoutName;
  }

  LayoutResource getNameForView(TypeElement viewElement) {
    String viewName = Utils.toSnakeCase(viewElement.getSimpleName().toString());
    String resourceName = layoutName.replace("%s", viewName);
    return new LayoutResource(rLayoutClass, resourceName, 0);
  }
}
