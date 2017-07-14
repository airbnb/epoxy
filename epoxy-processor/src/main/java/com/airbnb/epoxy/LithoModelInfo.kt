package com.airbnb.epoxy

import com.airbnb.epoxy.ClassNames.EPOXY_LITHO_MODEL
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.ParameterizedTypeName
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

internal class LithoModelInfo(
        typeUtils: Types,
        elementUtils: Elements,
        layoutSpecClassElement: TypeElement
) : GeneratedModelInfo() {

    val lithoComponentName: ClassName

    init {
        superClassElement = Utils.getElementByName(EPOXY_LITHO_MODEL, elementUtils, typeUtils) as TypeElement

        lithoComponentName = getLithoComponentName(elementUtils, layoutSpecClassElement)
        superClassName = ParameterizedTypeName.get(EPOXY_LITHO_MODEL, lithoComponentName)

        generatedClassName = buildGeneratedModelName(lithoComponentName)
        // We don't have any type parameters on our generated litho model
        parametrizedClassName = generatedClassName
        shouldGenerateModel = true

        collectMethodsReturningClassType(superClassElement, typeUtils)

        // The bound type is simply a LithoView
        boundObjectTypeName = ClassNames.LITHO_VIEW
    }

    /**
     * The name of the component that is generated for the layout spec. It will be in the same
     * package, and with the "Spec" term removed from the name.
     */
    fun getLithoComponentName(elementUtils: Elements,
                              layoutSpecClassElement: TypeElement): ClassName {
        val packageName = elementUtils.getPackageOf(layoutSpecClassElement).qualifiedName.toString()

        // Litho doesn't appear to allow specs as nested classes, so we don't check for nested
        // class naming here
        var className = layoutSpecClassElement.simpleName.toString()

        if (className.endsWith("Spec")) {
            className = className.substring(0, className.lastIndexOf("Spec"))
        }

        return ClassName.get(packageName, className)
    }

    private fun buildGeneratedModelName(componentName: ClassName): ClassName {
        val simpleName = "${componentName.simpleName()}Model${GeneratedModelInfo.GENERATED_CLASS_NAME_SUFFIX}"
        return ClassName.get(componentName.packageName(), simpleName)
    }

    fun addProp(propElement: Element, hashCodeValidator: HashCodeValidator) {
        attributeInfo += LithoModelAttributeInfo(this, propElement, hashCodeValidator)
    }
}
