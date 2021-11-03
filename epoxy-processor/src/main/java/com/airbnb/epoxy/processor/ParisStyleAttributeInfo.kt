package com.airbnb.epoxy.processor

import androidx.room.compiler.processing.XTypeElement
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.CodeBlock

const val BUILDER_STYLE_METHOD_PREFIX = "add"
const val PARIS_DEFAULT_STYLE_CONSTANT_NAME = "DEFAULT_PARIS_STYLE"
const val PARIS_STYLE_ATTR_NAME = "style"

fun weakReferenceFieldForStyle(styleName: String) = "parisStyleReference_$styleName"

/**
 * Represents a Paris Style option for a model.
 * This is added automatically for models generated with [ModelView] that also have Paris's Stylable annotation.
 */
class ParisStyleAttributeInfo(
    modelInfo: GeneratedModelInfo,
    packageName: String,
    styleBuilderClassName: ClassName,
    val styleBuilderElement: XTypeElement,
    memoizer: Memoizer
) : AttributeInfo(memoizer) {

    val styles: List<ParisStyle>
    val styleApplierClass: ClassName
    val styleBuilderClass: ClassName

    init {
        fieldName = PARIS_STYLE_ATTR_NAME
        rootClass = modelInfo.generatedName.simpleName()
        this.packageName = packageName
        setXType(modelInfo.memoizer.parisStyleType, modelInfo.memoizer)
        styleBuilderClass = styleBuilderClassName
        ignoreRequireHashCode = true
        isGenerated = true
        useInHash = true
        isNullable = false
        styles = findStyleNames(styleBuilderElement)

        // the builder is nested in the style applier class
        styleApplierClass = styleBuilderClassName.topLevelClassName()

        codeToSetDefault.explicit = CodeBlock.of(PARIS_DEFAULT_STYLE_CONSTANT_NAME)
    }

    private fun findStyleNames(styleBuilderElement: XTypeElement): List<ParisStyle> {
        return styleBuilderElement
            .getDeclaredMethodsLight(memoizer)
            .filter {
                it.name.startsWith(BUILDER_STYLE_METHOD_PREFIX)
            }
            .map {
                val name = it.name
                    .removePrefix(BUILDER_STYLE_METHOD_PREFIX)
                    .lowerCaseFirstLetter()

                ParisStyle(name, it.docComment)
            }
    }
}

data class ParisStyle(
    val name: String,
    val javadoc: String?
)
