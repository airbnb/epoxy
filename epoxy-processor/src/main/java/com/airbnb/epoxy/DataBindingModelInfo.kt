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

    private val dataBindingClassElement: TypeElement?
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
     * @return True if the DataBinding class exists, false otherwise.
     */
    fun parseDataBindingClass(): Boolean {
        // This databinding class won't exist until the second round of annotation processing since
        // it is generated in the first round.
        val dataBindingClassElement = this.dataBindingClassElement ?: return false

        val hashCodeValidator = HashCodeValidator(typeUtils, elementUtils)
        dataBindingClassElement.executableElements()
                .filter { Utils.isSetterMethod(it) }
                .map {
                    DataBindingAttributeInfo(this, it, hashCodeValidator)
                }
                .filter { it.fieldName !in FIELD_NAME_BLACKLIST }
                .let {
                    addAttributes(it)
                }
        return true
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

        val FIELD_NAME_BLACKLIST = listOf(
                // Starting with Android plugin 3.1.0 nested DataBinding classes have a
                // "setLifecycleOwner" method
                "lifecycleOwner"
        )
    }
}