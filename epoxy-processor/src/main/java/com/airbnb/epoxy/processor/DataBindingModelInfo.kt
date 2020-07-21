package com.airbnb.epoxy.processor

import com.airbnb.epoxy.processor.ClassNames.EPOXY_DATA_BINDING_HOLDER
import com.airbnb.epoxy.processor.ClassNames.EPOXY_DATA_BINDING_MODEL
import com.airbnb.epoxy.processor.Utils.getElementByNameNullable
import com.squareup.javapoet.ClassName
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

internal class DataBindingModelInfo(
    private val typeUtils: Types,
    private val elementUtils: Elements,
    val layoutResource: ResourceValue,
    val moduleName: String,
    private val layoutPrefix: String = "",
    val enableDoNotHash: Boolean,
    val annotatedElement: Element,
    memoizer: Memoizer
) : GeneratedModelInfo(memoizer) {
    private val dataBindingClassName: ClassName

    private var dataBindingClassElement: TypeElement? = null
        get() {
            if (field == null) {
                field = getElementByNameNullable(dataBindingClassName, elementUtils, typeUtils)
            }
            return field
        }

    init {
        dataBindingClassName = getDataBindingClassNameForResource(layoutResource, moduleName)

        superClassElement = memoizer.epoxyDataBindingModelBaseClass
        superClassName = EPOXY_DATA_BINDING_MODEL
        generatedName = buildGeneratedModelName()
        parameterizedGeneratedName = generatedName
        modelType = EPOXY_DATA_BINDING_HOLDER
        shouldGenerateModel = true

        collectMethodsReturningClassType(superClassElement)
    }

    /**
     * Look up the DataBinding class generated for this model's layout file and parse the attributes
     * for it.
     * @return the databinding element if it was successfully parsed, null otherwise.
     */
    fun parseDataBindingClass(): TypeElement? {
        // This databinding class won't exist until the second round of annotation processing since
        // it is generated in the first round.
        val dataBindingClassElement = this.dataBindingClassElement ?: return null
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

        return dataBindingClassElement
    }

    private fun getDataBindingClassNameForResource(
        layoutResource: ResourceValue,
        moduleName: String
    ): ClassName {
        val modelName = layoutResource.resourceName?.toUpperCamelCase()?.plus(BINDING_SUFFIX)
            ?: error("Resource name not found for layout: ${layoutResource.debugDetails()}")

        return ClassName.get("$moduleName.databinding", modelName)
    }

    private fun buildGeneratedModelName(): ClassName {
        val modelName = layoutResource.resourceName!!
            .removePrefix(layoutPrefix)
            .toUpperCamelCase()
            .plus(BINDING_SUFFIX)
            .plus(GENERATED_MODEL_SUFFIX)

        return ClassName.get(moduleName, modelName)
    }

    companion object {

        const val BINDING_SUFFIX = "Binding"

        val FIELD_NAME_BLACKLIST = listOf(
            // Starting with Android plugin 3.1.0 nested DataBinding classes have a
            // "setLifecycleOwner" method
            "lifecycleOwner"
        )
    }

    override fun additionalOriginatingElements() =
        listOfNotNull(annotatedElement, dataBindingClassElement)
}
