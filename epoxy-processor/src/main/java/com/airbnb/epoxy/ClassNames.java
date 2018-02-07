package com.airbnb.epoxy;

import com.squareup.javapoet.ClassName;

import static com.squareup.javapoet.ClassName.get;

final class ClassNames {
  private ClassNames() {
  }

  private static final String PKG_EPOXY = "com.airbnb.epoxy";
  private static final String PKG_PARIS = "com.airbnb.paris";
  private static final String PKG_LITHO = "com.facebook.litho";
  private static final String PKG_LITHO_ANNOTATIONS = "com.facebook.litho.annotations";
  private static final String PKG_ANDROID = "android";
  private static final String PKG_ANDROID_CONTENT = "android.content";
  private static final String PKG_ANDROID_VIEW = "android.view";
  private static final String PKG_ANDROID_OS = "android.os";

  static final ClassName ANDROID_CONTEXT = get(PKG_ANDROID_CONTENT, "Context");
  static final ClassName ANDROID_VIEW = get(PKG_ANDROID_VIEW, "View");
  static final ClassName ANDROID_VIEW_GROUP = get(PKG_ANDROID_VIEW, "ViewGroup");
  static final ClassName ANDROID_ASYNC_TASK = get(PKG_ANDROID_OS, "AsyncTask");
  static final ClassName ANDROID_MARGIN_LAYOUT_PARAMS =
      get(PKG_ANDROID_VIEW, "ViewGroup", "MarginLayoutParams");
  static final ClassName ANDROID_R = get(PKG_ANDROID, "R");

  static final ClassName LITHO_COMPONENT = get(PKG_LITHO, "Component");
  static final ClassName LITHO_COMPONENT_CONTEXT = get(PKG_LITHO, "ComponentContext");
  static final ClassName LITHO_ANNOTATION_LAYOUT_SPEC = get(PKG_LITHO_ANNOTATIONS, "LayoutSpec");
  static final ClassName LITHO_ANNOTATION_PROP = get(PKG_LITHO_ANNOTATIONS, "Prop");
  static final ClassName EPOXY_LITHO_MODEL = get(PKG_EPOXY, "EpoxyLithoModel");
  static final ClassName LITHO_VIEW = get(PKG_LITHO, "LithoView");

  static final ClassName EPOXY_MODEL_UNTYPED = get(PKG_EPOXY, "EpoxyModel");
  // TODO: (eli_hart 9/8/17) Fix this package name
  static final ClassName EPOXY_R = get("com.airbnb.viewmodeladapter", "R");
  static final ClassName EPOXY_DATA_BINDING_MODEL = get(PKG_EPOXY, "DataBindingEpoxyModel");
  static final ClassName EPOXY_DATA_BINDING_HOLDER =
      get(PKG_EPOXY, "DataBindingEpoxyModel", "DataBindingHolder");
  static final ClassName EPOXY_STRING_ATTRIBUTE_DATA = get(PKG_EPOXY, "StringAttributeData");
  static final ClassName EPOXY_CONTROLLER = get(PKG_EPOXY, "EpoxyController");
  static final ClassName EPOXY_STYLE_BUILDER_CALLBACK = get(PKG_EPOXY, "StyleBuilderCallback");
  static final ClassName EPOXY_CONTROLLER_HELPER = get(PKG_EPOXY, "ControllerHelper");

  static final ClassName PARIS_STYLE_UTILS = get(PKG_PARIS, "StyleApplierUtils", "Companion");
  static final ClassName PARIS_STYLE = get(PKG_PARIS + ".styles", "Style");
}
