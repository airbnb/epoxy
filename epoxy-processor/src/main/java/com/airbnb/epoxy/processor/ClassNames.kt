package com.airbnb.epoxy.processor

import com.squareup.javapoet.ClassName
import com.squareup.javapoet.ClassName.get
import com.squareup.kotlinpoet.javapoet.toJClassName

object ClassNames {

    private const val PKG_EPOXY = "com.airbnb.epoxy"
    private const val PKG_PARIS = "com.airbnb.paris"
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
    val EPOXY_MODEL_UNTYPED = get(PKG_EPOXY, "EpoxyModel")!!

    val EPOXY_MODEL_WITH_HOLDER_UNTYPED = get(PKG_EPOXY, "EpoxyModelWithHolder")!!

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
    val EPOXY_STRING_ATTRIBUTE_DATA_REFLECTION_NAME = EPOXY_STRING_ATTRIBUTE_DATA.reflectionName()

    @JvmField
    val EPOXY_CONTROLLER = get(PKG_EPOXY, "EpoxyController")!!

    @JvmField
    val MODEL_COLLECTOR = get(PKG_EPOXY, "ModelCollector")!!

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

    val EPOXY_VIEW_HOLDER = ClassName.bestGuess(Utils.EPOXY_VIEW_HOLDER_TYPE)

    val EPOXY_GENERATED_MODEL_INTERFACE = ClassName.bestGuess(Utils.GENERATED_MODEL_INTERFACE)

    val EPOXY_ON_BIND_MODEL_LISTENER = ClassName.bestGuess(Utils.ON_BIND_MODEL_LISTENER_TYPE)
    val EPOXY_ON_UNBIND_MODEL_LISTENER = ClassName.bestGuess(Utils.ON_UNBIND_MODEL_LISTENER_TYPE)
    val EPOXY_ON_VISIBILITY_STATE_MODEL_LISTENER =
        ClassName.bestGuess(Utils.ON_VISIBILITY_STATE_MODEL_LISTENER_TYPE)
    val EPOXY_ON_VISIBILITY_MODEL_LISTENER =
        ClassName.bestGuess(Utils.ON_VISIBILITY_MODEL_LISTENER_TYPE)
    val EPOXY_WRAPPED_CHECKED_LISTENER = ClassName.bestGuess(Utils.WRAPPED_CHECKED_LISTENER_TYPE)
    val EPOXY_WRAPPED_LISTENER = ClassName.bestGuess(Utils.WRAPPED_LISTENER_TYPE)
    val EPOXY_MODEL_CLICK_LISTENER = ClassName.bestGuess(Utils.MODEL_CLICK_LISTENER_TYPE)
    val EPOXY_MODEL_LONG_CLICK_LISTENER = ClassName.bestGuess(Utils.MODEL_LONG_CLICK_LISTENER_TYPE)
    val EPOXY_MODEL_CHECKED_CHANGE_LISTENER =
        ClassName.bestGuess(Utils.MODEL_CHECKED_CHANGE_LISTENER_TYPE)

    val VIEW_PARENT = get("android.view", "ViewParent")
    val KOTLIN_ANY = ClassName.get("kotlin", "Any")
    val ITERABLE = ClassName.get("java.lang", "Iterable")
    val LIST = ClassName.get("java.util", "List")
    val SET = ClassName.get("java.util", "Set")
    val COLLECTION = ClassName.get("java.util", "Collection")
    val UNIT = KClassNames.KOTLIN_UNIT.toJClassName()
}

const val DEPRECATED = "Deprecated"
