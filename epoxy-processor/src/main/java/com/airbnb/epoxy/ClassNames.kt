package com.airbnb.epoxy

import com.squareup.javapoet.ClassName.get

object ClassNames {

    private const val PKG_EPOXY = "com.airbnb.epoxy"
    private const val PKG_PARIS = "com.airbnb.paris"
    private const val PKG_LITHO = "com.facebook.litho"
    private const val PKG_LITHO_ANNOTATIONS = "com.facebook.litho.annotations"
    private const val PKG_ANDROID = "android"
    private const val PKG_ANDROID_CONTENT = "android.content"
    private const val PKG_ANDROID_VIEW = "android.view"
    private const val PKG_ANDROID_OS = "android.os"

    @JvmField
    val ANDROID_CONTEXT = get(PKG_ANDROID_CONTENT, "Context")!!
    @JvmField
    val ANDROID_VIEW = get(PKG_ANDROID_VIEW, "View")!!
    @JvmField
    val ANDROID_VIEW_GROUP = get(PKG_ANDROID_VIEW, "ViewGroup")!!
    @JvmField
    val ANDROID_ASYNC_TASK = get(PKG_ANDROID_OS, "AsyncTask")!!
    @JvmField
    val ANDROID_MARGIN_LAYOUT_PARAMS =
        get(PKG_ANDROID_VIEW, "ViewGroup", "MarginLayoutParams")!!
    @JvmField
    val ANDROID_R = get(PKG_ANDROID, "R")!!

    @JvmField
    val LITHO_COMPONENT = get(PKG_LITHO, "Component")!!
    @JvmField
    val LITHO_COMPONENT_CONTEXT = get(PKG_LITHO, "ComponentContext")!!
    @JvmField
    val LITHO_ANNOTATION_LAYOUT_SPEC = get(PKG_LITHO_ANNOTATIONS, "LayoutSpec")!!
    @JvmField
    val LITHO_ANNOTATION_PROP = get(PKG_LITHO_ANNOTATIONS, "Prop")!!
    @JvmField
    val EPOXY_LITHO_MODEL = get(PKG_EPOXY, "EpoxyLithoModel")!!
    @JvmField
    val LITHO_VIEW = get(PKG_LITHO, "LithoView")!!

    @JvmField
    val EPOXY_MODEL_UNTYPED = get(PKG_EPOXY, "EpoxyModel")!!
    // TODO: (eli_hart 9/8/17) Fix this package name
    @JvmField
    val EPOXY_R = get("com.airbnb.viewmodeladapter", "R")!!
    @JvmField
    val EPOXY_DATA_BINDING_MODEL = get(PKG_EPOXY, "DataBindingEpoxyModel")!!
    @JvmField
    val EPOXY_DATA_BINDING_HOLDER =
        get(PKG_EPOXY, "DataBindingEpoxyModel", "DataBindingHolder")!!
    @JvmField
    val EPOXY_STRING_ATTRIBUTE_DATA = get(PKG_EPOXY, "StringAttributeData")!!
    @JvmField
    val EPOXY_CONTROLLER = get(PKG_EPOXY, "EpoxyController")!!
    @JvmField
    val EPOXY_STYLE_BUILDER_CALLBACK = get(PKG_EPOXY, "StyleBuilderCallback")!!
    @JvmField
    val EPOXY_CONTROLLER_HELPER = get(PKG_EPOXY, "ControllerHelper")!!
    @JvmField
    val EPOXY_MODEL_PROPERTIES = get(PKG_EPOXY, "ModelProperties")!!

    @JvmField
    val PARIS_STYLE_UTILS = get(PKG_PARIS, "StyleApplierUtils", "Companion")!!
    @JvmField
    val PARIS_STYLE = get("$PKG_PARIS.styles", "Style")!!
}

const val DEPRECATED = "Deprecated"
