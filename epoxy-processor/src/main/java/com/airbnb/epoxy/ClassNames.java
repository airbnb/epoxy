package com.airbnb.epoxy;

import com.squareup.javapoet.ClassName;

import static com.squareup.javapoet.ClassName.get;

interface ClassNames {
  String PKG_LITHO = "com.facebook.litho";
  String PKG_LITHO_ANNOTATIONS = "com.facebook.litho.annotations";

  ClassName LITHO_COMPONENT = get(PKG_LITHO, "Component");
  ClassName LITHO_COMPONENT_CONTEXT = get(PKG_LITHO, "ComponentContext");
  ClassName LITHO_ANNOTATION_LAYOUT_SPEC = get(PKG_LITHO_ANNOTATIONS, "LayoutSpec");
  ClassName LITHO_ANNOTATION_PROP = get(PKG_LITHO_ANNOTATIONS, "Prop");

  void makeLinterShutUpAboutNoMethodInInterface();
}
