package com.airbnb.epoxy;

import android.support.annotation.StringRes;

import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.util.Elements;

class ViewAttributeStringResWithArgumentsOverload extends ViewAttributeInfo
    implements MultiParamAttribute {

  private static final String STRING_RES_PARAM = "stringRes";
  private static final String ARGS_PARAM = "formatArgs";

  ViewAttributeStringResWithArgumentsOverload(ModelViewInfo viewInfo, ViewAttributeInfo info,
      Elements elements) {
    super(viewInfo, Utils.getTypeMirror(ClassNames.EPOXY_STRING_RES_ATTRIBUTE, elements),
        info.viewSetterMethodName);
    useInHash = info.useInHash;
    ignoreRequireHashCode = info.ignoreRequireHashCode;
    javaDoc = info.javaDoc;
    isNullable = null; // N/A for a StringRes int value
    defaultValue = null;
    constantFieldNameForDefaultValue = info.constantFieldNameForDefaultValue;
    groupKey = info.groupKey;
  }

  @Override
  public List<ParameterSpec> getParams() {
    List<ParameterSpec> params = new ArrayList<>();

    params.add(
        ParameterSpec.builder(TypeName.INT, STRING_RES_PARAM).addAnnotation(StringRes.class)
            .build());

    params.add(ParameterSpec.builder(ArrayTypeName.of(TypeName.OBJECT), ARGS_PARAM).build());

    return params;
  }

  @Override
  public CodeBlock getValueToSetOnAttribute() {
    return CodeBlock
        .of("new $T($L, $L)", ClassNames.EPOXY_STRING_RES_ATTRIBUTE, STRING_RES_PARAM, ARGS_PARAM);
  }

  @Override
  public boolean varargs() {
    return true;
  }
}
