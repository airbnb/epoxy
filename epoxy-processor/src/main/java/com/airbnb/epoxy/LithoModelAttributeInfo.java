package com.airbnb.epoxy;

import com.squareup.javapoet.TypeName;

import javax.lang.model.element.Element;

class LithoModelAttributeInfo extends AttributeInfo {
  LithoModelAttributeInfo(LithoModelInfo lithoModelInfo,
      Element propElement, HashCodeValidator hashCodeValidator) {
    fieldName = propElement.getSimpleName().toString();
    typeName = TypeName.get(propElement.asType());
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
