package com.airbnb.epoxy;

import android.support.annotation.StringRes;

import com.squareup.javapoet.AnnotationSpec;

import javax.lang.model.type.TypeKind;
import javax.lang.model.util.Types;

class ViewAttributeStringResOverload extends ViewAttributeInfo {

  ViewAttributeStringResOverload(ModelViewInfo viewInfo, ViewAttributeInfo info, Types types) {
    super(viewInfo, types.getPrimitiveType(TypeKind.INT), info.viewSetterMethodName);
    useInHash = info.useInHash;
    ignoreRequireHashCode = info.ignoreRequireHashCode;
    javaDoc = info.javaDoc;
    isNullable = null; // N/A for a StringRes int value
    defaultValue = null;
    constantFieldNameForDefaultValue = info.constantFieldNameForDefaultValue;
    groupKey = info.groupKey;

    AnnotationSpec stringResAnnotation = AnnotationSpec.builder(StringRes.class).build();
    getterAnnotations.add(stringResAnnotation);
    setterAnnotations.add(stringResAnnotation);
  }
}
