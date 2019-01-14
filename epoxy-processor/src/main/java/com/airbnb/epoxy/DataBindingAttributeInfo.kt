package com.airbnb.epoxy

import com.airbnb.epoxy.Utils.removeSetPrefix
import javax.lang.model.element.ExecutableElement

internal class DataBindingAttributeInfo(
    modelInfo: DataBindingModelInfo,
    setterMethod: ExecutableElement,
    hashCodeValidator: HashCodeValidator
) : AttributeInfo() {

    init {
        fieldName = removeSetPrefix(setterMethod.simpleName.toString())
        typeMirror = setterMethod.parameters[0].asType()
        modelName = modelInfo.generatedName.simpleName()
        packageName = modelInfo.generatedName.packageName()
        useInHash = !modelInfo.enableDoNotHash ||
            hashCodeValidator.implementsHashCodeAndEquals(typeMirror)
        ignoreRequireHashCode = true
        generateSetter = true
        generateGetter = true
        hasFinalModifier = false
        isPackagePrivate = false
        isGenerated = true
    }
}
