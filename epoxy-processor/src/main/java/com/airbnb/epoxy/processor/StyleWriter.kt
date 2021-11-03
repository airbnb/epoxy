package com.airbnb.epoxy.processor

import androidx.room.compiler.processing.XElement
import androidx.room.compiler.processing.XProcessingEnv
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.ParameterSpec
import java.util.Objects
import javax.lang.model.element.Element
import javax.lang.model.util.Elements

internal fun addStyleApplierCode(
    methodBuilder: MethodSpec.Builder,
    styleInfo: ParisStyleAttributeInfo,
    viewVariableName: String
) {

    methodBuilder.apply {

        addStatement(
            "\$T styleApplier = new \$T(\$L)",
            styleInfo.styleApplierClass, styleInfo.styleApplierClass, viewVariableName
        )

        addStatement("styleApplier.apply(\$L)", PARIS_STYLE_ATTR_NAME)

        // By saving the style as a tag we can prevent applying it
        // again when the view is recycled and the same style is used
        addStatement(
            "\$L.setTag(\$T.id.epoxy_saved_view_style, \$L)",
            viewVariableName, ClassNames.EPOXY_R, PARIS_STYLE_ATTR_NAME
        )
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
            beginControlFlow(
                "\nif (!\$T.equals(\$L, that.\$L))",
                Objects::class.java, PARIS_STYLE_ATTR_NAME, PARIS_STYLE_ATTR_NAME
            )
        } else {
            beginControlFlow(
                "\nif (!\$T.equals(\$L, \$L.getTag(\$T.id.epoxy_saved_view_style)))",
                Objects::class.java, PARIS_STYLE_ATTR_NAME, boundObjectParam.name,
                ClassNames.EPOXY_R
            )
        }

        addStyleApplierCode(this, styleInfo, boundObjectParam.name)

        endControlFlow()
    }
}

internal fun Element.hasStyleableAnnotation(elements: Elements) = annotationMirrorsThreadSafe
    .map { it.annotationType.asElement() }
    .any {
        it.simpleName.toString() == "Styleable" &&
            elements.getPackageOf(it).qualifiedName.contains("paris")
    }

internal fun XElement.hasStyleableAnnotation(): Boolean {
    return getAllAnnotations().any {
        it.name == "Styleable" && it.qualifiedName.contains("paris")
    }
}

internal fun tryAddStyleBuilderAttribute(
    styleableModel: GeneratedModelInfo,
    processingEnv: XProcessingEnv,
    memoizer: Memoizer
): Boolean {
    // if style applier is generated
    val viewClass = (styleableModel.modelType as? ClassName) ?: return false
    val styleBuilderClassName = ClassName.get(
        viewClass.packageName(),
        "${viewClass.simpleName()}StyleApplier",
        "StyleBuilder"
    )

    val styleBuilderElement = processingEnv.findTypeElement(styleBuilderClassName) ?: return false

    styleableModel.setStyleable(
        ParisStyleAttributeInfo(
            modelInfo = styleableModel,
            packageName = viewClass.packageName(),
            styleBuilderClassName = styleBuilderClassName,
            styleBuilderElement = styleBuilderElement,
            memoizer = memoizer
        )
    )
    return true
}
