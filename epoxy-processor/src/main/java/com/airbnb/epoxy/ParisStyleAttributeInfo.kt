package com.airbnb.epoxy

import com.squareup.javapoet.*
import javax.lang.model.element.*
import javax.lang.model.type.*
import javax.lang.model.util.*

const val BUILDER_STYLE_METHOD_PREFIX = "add"
const val PARIS_DEFAULT_STYLE_CONSTANT_NAME = "DEFAULT_PARIS_STYLE"
const val PARIS_STYLE_ATTR_NAME = "style"

fun weakReferenceFieldForStyle(styleName: String) = "parisStyleReference_$styleName"

/**
 * Represents a Paris Style option for a model.
 * This is added automatically for models generated with [ModelView] that also have Paris's Stylable annotation.
 */
internal class ParisStyleAttributeInfo(
        modelInfo: GeneratedModelInfo,
        val elements: Elements,
        val types: Types,
        packageName: String,
        styleBuilderClassName: ClassName,
        styleBuilderElement: TypeMirror
) : AttributeInfo() {

    val styles: List<ParisStyle>
    val styleApplierClass: ClassName
    val styleBuilderClass: ClassName

    init {
        fieldName = PARIS_STYLE_ATTR_NAME
        modelName = modelInfo.generatedName.simpleName()
        modelPackageName = packageName
        typeMirror = getTypeMirror(ClassNames.PARIS_STYLE, elements, types)
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

    private fun findStyleNames(typeMirror: TypeMirror): List<ParisStyle> {
        return types.asElement(typeMirror)
                .enclosedElements
                .filter {
                    it.kind == ElementKind.METHOD
                            && it.simpleName.startsWith(BUILDER_STYLE_METHOD_PREFIX)
                }
                .map {
                    val name = it.simpleName
                            .toString()
                            .removePrefix(BUILDER_STYLE_METHOD_PREFIX)
                            .lowerCaseFirstLetter()

                    ParisStyle(name, elements.getDocComment(it))
                }
    }

}

data class ParisStyle(
        val name: String,
        val javadoc: String?
)