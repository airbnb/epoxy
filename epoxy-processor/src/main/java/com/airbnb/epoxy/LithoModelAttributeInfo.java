package com.airbnb.epoxy;

import javax.lang.model.element.Element;

class LithoModelAttributeInfo extends AttributeInfo {
  LithoModelAttributeInfo(LithoModelInfo lithoModelInfo,
      Element propElement, HashCodeValidator hashCodeValidator) {
    fieldName = propElement.getSimpleName().toString();
    typeMirror = propElement.asType();
    modelName = lithoModelInfo.getGeneratedName().simpleName();
    modelPackageName = lithoModelInfo.generatedClassName.packageName();
    useInHash = hashCodeValidator.implementsHashCodeAndEquals(typeMirror);
    ignoreRequireHashCode = false;
    generateSetter = true;
    generateGetter = true;
    hasFinalModifier = false;
    packagePrivate = false;
    isGenerated = true;
  }
}
