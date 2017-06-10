package com.airbnb.epoxy;

import android.support.annotation.Nullable;

import com.squareup.javapoet.ClassName;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;

class PackageModelViewSettings {
  final ClassName rClass;
  final String layoutName;
  @Nullable final TypeMirror defaultBaseModel;
  final boolean includeAlternateLayouts;

  PackageModelViewSettings(ClassName rClassName, PackageModelViewConfig annotation) {
    // The R class may be R or R2. We create the class name again to make sure we don't use R2.
    this.rClass = ClassName.get(rClassName.packageName(), "R", "layout");

    this.layoutName = annotation.defaultLayoutPattern();
    this.defaultBaseModel = getDefaultBaseModel(annotation);
    includeAlternateLayouts = annotation.useLayoutOverloads();
  }

  @Nullable
  private TypeMirror getDefaultBaseModel(PackageModelViewConfig annotation) {
    TypeMirror defaultBaseModel = null;
    try {
      annotation.defaultBaseModelClass(); // this should throw
    } catch (MirroredTypeException mte) {
      defaultBaseModel = mte.getTypeMirror();
    }

    if (defaultBaseModel != null
        && defaultBaseModel.toString().equals(Void.class.getCanonicalName())) {
      // The default value of the annotation parameter is Void.class to signal that the user
      // does not want to provide a custom base class
      defaultBaseModel = null;
    }
    return defaultBaseModel;
  }

  LayoutResource getNameForView(TypeElement viewElement) {
    String viewName = Utils.toSnakeCase(viewElement.getSimpleName().toString());
    String resourceName = layoutName.replace("%s", viewName);
    return new LayoutResource(rClass, resourceName, 0);
  }
}
