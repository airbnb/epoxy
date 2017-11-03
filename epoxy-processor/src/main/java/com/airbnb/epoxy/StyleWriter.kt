package com.airbnb.epoxy

import com.squareup.javapoet.*
import javax.lang.model.*
import javax.lang.model.util.*

internal fun addStyleApplierCode(
        methodBuilder: MethodSpec.Builder,
        styleInfo: ParisStyleAttributeInfo,
        viewVariableName: String
) {

    methodBuilder.apply {

        addStatement("\$T styleApplier = new \$T(\$L)",
                     styleInfo.styleApplierClass, styleInfo.styleApplierClass, viewVariableName)

        addStatement("styleApplier.apply(\$L)", PARIS_STYLE_ATTR_NAME)

        // By saving the style as a tag we can prevent applying it
        // again when the view is recycled and the same style is used
        addStatement("\$L.setTag(\$T.id.epoxy_saved_view_style, \$L)",
                     viewVariableName, ClassNames.EPOXY_R, PARIS_STYLE_ATTR_NAME)
    }
}

internal fun addBindStyleCodeIfNeeded(
        modelInfo: GeneratedModelInfo,
        methodBuilder: MethodSpec.Builder,
        boundObjectParam: ParameterSpec,
        hasPreviousModel: Boolean
) {
    val styleInfo = modelInfo.styleBuilderInfo ?: return

    methodBuilder.apply {
        // Compare against the style on the previous model if it exists,
        // otherwise we look up the saved style from the view tag
        if (hasPreviousModel) {
            beginControlFlow("\nif (\$L != that.\$L)",
                             PARIS_STYLE_ATTR_NAME, PARIS_STYLE_ATTR_NAME)
        } else {
            beginControlFlow("\nif (\$L != \$L.getTag(\$T.id.epoxy_saved_view_style))",
                             PARIS_STYLE_ATTR_NAME, boundObjectParam.name, ClassNames.EPOXY_R)
        }

        addStyleApplierCode(this, styleInfo, boundObjectParam.name)

        endControlFlow()
    }
}

internal fun AnnotatedConstruct.hasStyleableAnnotation(elements: Elements)
        = annotationMirrors
        .map { it.annotationType.asElement() }
        .any {
            it.simpleName.toString() == "Styleable"
                    && elements.getPackageOf(it).qualifiedName.contains("paris")
        }

internal fun tryAddStyleBuilderAttribute(
        styleableModel: GeneratedModelInfo,
        elements: Elements,
        types: Types
): Boolean {
    // if style applier is generated
    val viewClass = (styleableModel.boundObjectTypeName as? ClassName) ?: return false
    val styleBuilderClassName = ClassName.get(viewClass.packageName(),
                                              "${viewClass.simpleName()}StyleApplier",
                                              "StyleBuilder")

    val styleBuilderElement = try {
        getTypeMirror(styleBuilderClassName, elements, types)
    } catch (e: IllegalArgumentException) {
        return false
    }

    styleableModel.setStyleable(ParisStyleAttributeInfo(
            styleableModel,
            elements,
            types,
            viewClass.packageName(),
            styleBuilderClassName,
            styleBuilderElement
    ))
    return true
}