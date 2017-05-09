package com.airbnb.epoxy;

import com.squareup.javapoet.TypeName;

import javax.lang.model.element.Element;

class LithoModelAttributeInfo extends AttributeInfo {
  LithoModelAttributeInfo(LithoModelInfo lithoModelInfo,
      Element propElement) {
    name = propElement.getSimpleName().toString();
    typeName = TypeName.get(propElement.asType());
    typeMirror = propElement.asType();
    modelName = lithoModelInfo.getGeneratedName().simpleName();
    modelPackageName = lithoModelInfo.generatedClassName.packageName();
    useInHash = true; // TODO: (eli_hart 4/26/17) We should come up with a way to exclude things
    // from the hash (like click listeners). One option is to exclude it if it the type doesn't
    // implement hashCode
    ignoreRequireHashCode = false;
    generateSetter = true;
    generateGetter = true;
    hasFinalModifier = false;
    packagePrivate = false;
    isGenerated = true;
  }
}
