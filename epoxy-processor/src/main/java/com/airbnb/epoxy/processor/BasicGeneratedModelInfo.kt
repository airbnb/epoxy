package com.airbnb.epoxy.processor

import androidx.room.compiler.processing.XElement
import androidx.room.compiler.processing.XTypeElement
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.processor.Utils.getEpoxyObjectType
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.ParameterizedTypeName
import com.squareup.javapoet.TypeVariableName

internal class BasicGeneratedModelInfo(
    superClassElement: XTypeElement,
    logger: Logger,
    memoizer: Memoizer
) : GeneratedModelInfo(memoizer) {

    val boundObjectTypeElement: XTypeElement?

    init {
        this.superClassName = superClassElement.type.typeNameWithWorkaround(memoizer)
        this.superClassElement = superClassElement
        generatedName = buildGeneratedModelName(superClassElement)

        for (typeParam in superClassElement.type.typeArguments) {
            val defaultTypeName = typeParam.typeNameWithWorkaround(memoizer)

            if (defaultTypeName is TypeVariableName) {
                typeVariableNames.add(defaultTypeName)
            } else {
                logger.logError(
                    superClassElement,
                    "Unable to get type variable name for $superClassElement. Found $defaultTypeName"
                )
            }
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

        var boundObjectType = getEpoxyObjectType(superClassElement, memoizer)
        if (boundObjectType == null) {
            logger
                .logError(
                    "Epoxy model type could not be found. (class: %s)",
                    superClassElement.name
                )
            // Return a basic view type so the code can be generated
            boundObjectType = memoizer.androidViewType
        }
        modelType = boundObjectType.typeName
        this.boundObjectTypeElement = boundObjectType.typeElement

        val annotation = superClassElement.getAnnotation(EpoxyModelClass::class)

        // By default we don't extend classes that are abstract; if they don't contain all required
        // methods then our generated class won't compile. If there is a EpoxyModelClass annotation
        // though we will always generate the subclass
        shouldGenerateModel = annotation != null || !superClassElement.isAbstract()
        includeOtherLayoutOptions = annotation?.value?.useLayoutOverloads ?: false

        annotations.addAll(
            superClassElement.buildAnnotationSpecs({
                it != memoizer.epoxyModelClassAnnotation
            }, memoizer)
        )
    }

    private fun buildGeneratedModelName(classElement: XTypeElement): ClassName {
        val packageName = classElement.packageName

        val packageLen = packageName.length + 1
        val className = classElement.qualifiedName.substring(packageLen).replace(
            '.',
            '$'
        )

        return ClassName.get(
            packageName,
            className + GENERATED_CLASS_NAME_SUFFIX
        )
    }

    override fun additionalOriginatingElements(): List<XElement> = listOf(superClassElement)
}
