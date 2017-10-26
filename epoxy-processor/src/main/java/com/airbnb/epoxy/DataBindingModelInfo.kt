package com.airbnb.epoxy

import com.airbnb.epoxy.ClassNames.*
import com.airbnb.epoxy.Utils.*
import com.squareup.javapoet.*
import javax.lang.model.element.*
import javax.lang.model.util.*

internal class DataBindingModelInfo(
        private val typeUtils: Types,
        private val elementUtils: Elements,
        val layoutResource: ResourceValue,
         val moduleName: String,
        private val layoutPrefix: String = ""
) : GeneratedModelInfo() {
    private val dataBindingClassName: ClassName

    val dataBindingClassElement: Element?
        get() = getElementByName(dataBindingClassName, elementUtils, typeUtils)

    init {

        dataBindingClassName = getDataBindingClassNameForResource(layoutResource, moduleName)

        superClassElement = Utils.getElementByName(EPOXY_DATA_BINDING_MODEL,
                                                   elementUtils, typeUtils) as TypeElement
        superClassName = EPOXY_DATA_BINDING_MODEL
        generatedClassName = buildGeneratedModelName()
        parametrizedClassName = generatedClassName
        boundObjectTypeName = EPOXY_DATA_BINDING_HOLDER
        shouldGenerateModel = true

        collectMethodsReturningClassType(superClassElement, typeUtils)
    }

    /**
     * Look up the DataBinding class generated for this model's layout file and parse the attributes
     * for it.
     */
    fun parseDataBindingClass() {
        // This databinding class won't exist until the second round of annotation processing since
        // it is generated in the first round.

        val hashCodeValidator = HashCodeValidator(typeUtils, elementUtils)
        dataBindingClassElement!!.enclosedElements
                .filter { Utils.isSetterMethod(it) }
                .forEach {
                    addAttribute(
                            DataBindingAttributeInfo(this, it as ExecutableElement,
                                                     hashCodeValidator))
                }
    }

    private fun getDataBindingClassNameForResource(
            layoutResource: ResourceValue,
            moduleName: String
    ): ClassName {
        val modelName = layoutResource.resourceName!!.toUpperCamelCase().plus(BINDING_SUFFIX)

        return ClassName.get(moduleName + ".databinding", modelName)
    }

    private fun buildGeneratedModelName(): ClassName {
        val modelName = layoutResource.resourceName!!
                .removePrefix(layoutPrefix)
                .toUpperCamelCase()
                .plus(BINDING_SUFFIX)
                .plus(GeneratedModelInfo.GENERATED_MODEL_SUFFIX)

        return ClassName.get(moduleName, modelName)
    }

    companion object {

        val BINDING_SUFFIX = "Binding"
    }
}