package com.airbnb.epoxy;

import com.squareup.javapoet.ClassName;

import static com.squareup.javapoet.ClassName.get;

final class ClassNames {
  private ClassNames() {
  }

  private static final String PKG_EPOXY = "com.airbnb.epoxy";
  private static final String PKG_LITHO = "com.facebook.litho";
  private static final String PKG_LITHO_ANNOTATIONS = "com.facebook.litho.annotations";

  static final ClassName LITHO_COMPONENT = get(PKG_LITHO, "Component");
  static final ClassName LITHO_COMPONENT_CONTEXT = get(PKG_LITHO, "ComponentContext");
  static final ClassName LITHO_ANNOTATION_LAYOUT_SPEC = get(PKG_LITHO_ANNOTATIONS, "LayoutSpec");
  static final ClassName LITHO_ANNOTATION_PROP = get(PKG_LITHO_ANNOTATIONS, "Prop");
  static final ClassName EPOXY_LITHO_MODEL = get(PKG_EPOXY, "EpoxyLithoModel");

  static final ClassName EPOXY_MODEL_UNTYPED = get(PKG_EPOXY, "EpoxyModel");
  static final ClassName EPOXY_DATA_BINDING_MODEL = get(PKG_EPOXY, "DataBindingEpoxyModel");
  static final ClassName EPOXY_DATA_BINDING_HOLDER = get(PKG_EPOXY,
      "DataBindingEpoxyModel.DataBindingHolder");
}
