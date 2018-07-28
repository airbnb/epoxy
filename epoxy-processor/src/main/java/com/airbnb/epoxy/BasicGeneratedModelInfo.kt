package com.airbnb.epoxy

import com.airbnb.epoxy.Utils.getElementByName
import com.airbnb.epoxy.Utils.getEpoxyObjectType
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.ParameterizedTypeName
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.TypeVariableName
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

internal class BasicGeneratedModelInfo(
    private val elements: Elements,
    types: Types,
    superClassElement: TypeElement,
    errorLogger: ErrorLogger
) : GeneratedModelInfo() {

    val boundObjectTypeElement: TypeElement?

    init {
        this.superClassName = ParameterizedTypeName.get(superClassElement.asType())
        this.superClassElement = superClassElement
        generatedClassName = buildGeneratedModelName(superClassElement)

        for (typeParameterElement in superClassElement.typeParameters) {
            typeVariableNames.add(TypeVariableName.get(typeParameterElement))
        }

        constructors.addAll(GeneratedModelInfo.getClassConstructors(superClassElement))
        collectMethodsReturningClassType(superClassElement, types)

        if (!typeVariableNames.isEmpty()) {
            this.parametrizedClassName = ParameterizedTypeName.get(
                generatedClassName,
                *typeVariableNames.toTypedArray()
            )
        } else {
            this.parametrizedClassName = generatedClassName
        }

        var boundObjectTypeMirror = getEpoxyObjectType(superClassElement, types)
        if (boundObjectTypeMirror == null) {
            errorLogger
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
        boundObjectTypeName = TypeName.get(boundObjectTypeMirror!!)
        this.boundObjectTypeElement = (boundObjectTypeName as? ClassName)?.let {
            elements.getTypeElement(it.reflectionName())
        }

        val annotation = superClassElement.getAnnotation(EpoxyModelClass::class.java)

        // By default we don't extend classes that are abstract; if they don't contain all required
        // methods then our generated class won't compile. If there is a EpoxyModelClass annotation
        // though we will always generate the subclass
        shouldGenerateModel = annotation != null || Modifier.ABSTRACT !in
                superClassElement.modifiers
        includeOtherLayoutOptions = annotation?.useLayoutOverloads ?: false

        val modelClassAnnotation = EpoxyModelClass::class.className()
        annotations.addAll(
            superClassElement.buildAnnotationSpecs {
                it != modelClassAnnotation
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
            className + GeneratedModelInfo.GENERATED_CLASS_NAME_SUFFIX
        )
    }

}
