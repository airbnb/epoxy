package com.airbnb.epoxy.processor

import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.processor.Utils.getElementByName
import com.airbnb.epoxy.processor.Utils.getEpoxyObjectType
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.ParameterizedTypeName
import com.squareup.javapoet.TypeVariableName
import javax.lang.model.element.Element
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

internal class BasicGeneratedModelInfo(
    private val elements: Elements,
    types: Types,
    superClassElement: TypeElement,
    logger: Logger,
    memoizer: Memoizer
) : GeneratedModelInfo(memoizer) {

    val boundObjectTypeElement: TypeElement?

    init {
        this.superClassName = superClassElement.asType().typeNameSynchronized()
        this.superClassElement = superClassElement
        generatedName = buildGeneratedModelName(superClassElement)

        for (typeParameterElement in superClassElement.typeParametersThreadSafe) {
            typeVariableNames.add(TypeVariableName.get(typeParameterElement))
        }

        constructors.addAll(getClassConstructors(superClassElement))
        collectMethodsReturningClassType(superClassElement)

        if (typeVariableNames.isNotEmpty()) {
            this.parameterizedGeneratedName = ParameterizedTypeName.get(
                generatedName,
                *typeVariableNames.toTypedArray()
            )
        } else {
            this.parameterizedGeneratedName = generatedName
        }

        var boundObjectTypeMirror = getEpoxyObjectType(superClassElement, types)
        if (boundObjectTypeMirror == null) {
            logger
                .logError(
                    "Epoxy model type could not be found. (class: %s)",
                    superClassElement.simpleName
                )
            // Return a basic view type so the code can be generated
            boundObjectTypeMirror = getElementByName(
                Utils.ANDROID_VIEW_TYPE, elements,
                types
            ).asType()
        }
        modelType = boundObjectTypeMirror!!.typeNameSynchronized()
        this.boundObjectTypeElement = (modelType as? ClassName)?.asTypeElement(elements)

        val annotation = superClassElement.getAnnotation<EpoxyModelClass>()

        // By default we don't extend classes that are abstract; if they don't contain all required
        // methods then our generated class won't compile. If there is a EpoxyModelClass annotation
        // though we will always generate the subclass
        shouldGenerateModel =
            annotation != null || Modifier.ABSTRACT !in superClassElement.modifiersThreadSafe
        includeOtherLayoutOptions = annotation?.useLayoutOverloads ?: false

        annotations.addAll(
            superClassElement.buildAnnotationSpecs {
                it != memoizer.epoxyModelClassAnnotation
            }
        )
    }

    private fun buildGeneratedModelName(classElement: TypeElement): ClassName {
        val packageName = elements.getPackageOf(classElement).qualifiedName.toString()

        val packageLen = packageName.length + 1
        val className = classElement.qualifiedName.toString().substring(packageLen).replace(
            '.',
            '$'
        )

        return ClassName.get(
            packageName,
            className + GENERATED_CLASS_NAME_SUFFIX
        )
    }

    override fun additionalOriginatingElements(): List<Element> = listOf(superClassElement)
}
