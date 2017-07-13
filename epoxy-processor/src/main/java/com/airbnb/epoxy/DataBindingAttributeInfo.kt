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
        modelPackageName = modelInfo.generatedName.packageName()
        useInHash = hashCodeValidator.implementsHashCodeAndEquals(typeMirror)
        ignoreRequireHashCode = false
        generateSetter = true
        generateGetter = true
        hasFinalModifier = false
        packagePrivate = false
        isGenerated = true
    }
}
