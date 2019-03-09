package com.airbnb.epoxy

import javax.lang.model.element.Element

internal class LithoModelAttributeInfo(
    lithoModelInfo: LithoModelInfo,
    propElement: Element,
    hashCodeValidator: HashCodeValidator
) : AttributeInfo() {

    init {
        fieldName = propElement.simpleName.toString()
        typeMirror = propElement.asType()
        modelName = lithoModelInfo.generatedName.simpleName()
        packageName = lithoModelInfo.generatedClassName.packageName()
        useInHash = hashCodeValidator.implementsHashCodeAndEquals(typeMirror)
        ignoreRequireHashCode = false
        generateSetter = true
        generateGetter = true
        hasFinalModifier = false
        isPackagePrivate = false
        isGenerated = true
    }
}
