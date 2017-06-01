package com.airbnb.epoxy;

import android.support.annotation.Nullable;
import android.support.annotation.PluralsRes;
import android.support.annotation.StringRes;

import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.MethodSpec.Builder;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;

import java.util.ArrayList;
import java.util.List;

import static com.airbnb.epoxy.GeneratedModelWriter.addOnMutationCall;
import static com.airbnb.epoxy.GeneratedModelWriter.addParameterNullCheckIfNeeded;
import static com.airbnb.epoxy.GeneratedModelWriter.setBitSetIfNeeded;
import static com.airbnb.epoxy.GeneratedModelWriter.shouldUseBitSet;
import static javax.lang.model.element.Modifier.PUBLIC;

class StringOverloadWriter {
  private static final String PLURAL_RES_PARAM = "pluralRes";
  private static final String STRING_RES_PARAM = "stringRes";
  private static final String ARGS_PARAM = "formatArgs";
  private static final String QUANTITY_PARAM = "quantity";

  private final GeneratedModelInfo modelInfo;
  private final AttributeInfo attr;
  private ConfigManager configManager;
  private final String fieldName;
  private final boolean nullable;

  StringOverloadWriter(GeneratedModelInfo modelInfo, AttributeInfo attr,
      ConfigManager configManager) {

    this.modelInfo = modelInfo;
    this.attr = attr;
    this.configManager = configManager;
    fieldName = attr.getFieldName();
    nullable = attr.isNullable != null && attr.isNullable;
  }

  List<MethodSpec> buildMethods() {
    List<MethodSpec> methods = new ArrayList<>();
    methods.add(buildGetter());

    methods.add(finishSetter(buildCharSequenceSetter(baseSetter())));
    methods.add(finishSetter(buildStringResSetter(baseSetter())));
    methods.add(finishSetter(buildStringResWithArgsSetter(baseSetter())));
    methods.add(finishSetter(buildQuantityStringResSetter(baseSetter("QuantityRes"))));

    // If a string res is set then make sure it isn't 0
//    if ((attr instanceof StringAttributeOverload)) {
//      ParameterSpec stringParam = stringSetter.build().parameters.get(0);
//      addStringResCheckForInvalidId(modelInfo, attr, stringParam.name, stringSetter);
//    }

    return methods;
  }

  private Builder buildCharSequenceSetter(Builder builder) {
    String paramName = attr.generatedSetterName();
    ParameterSpec.Builder paramBuilder = ParameterSpec.builder(CharSequence.class, paramName);

    if (nullable) {
      paramBuilder.addAnnotation(Nullable.class);
    }

    builder.addParameter(paramBuilder.build());

    addParameterNullCheckIfNeeded(configManager, attr, paramName, builder);
    builder.addStatement("$L.setValue($L)", fieldName, paramName);

    return builder;
  }

  private Builder buildStringResSetter(Builder builder) {
    builder.addParameter(ParameterSpec.builder(TypeName.INT, STRING_RES_PARAM)
        .addAnnotation(StringRes.class)
        .build());

    builder.addStatement("$L.setValue($L)", fieldName, STRING_RES_PARAM);

    return builder;
  }

  private Builder buildStringResWithArgsSetter(Builder builder) {
    builder.addParameter(ParameterSpec.builder(TypeName.INT, STRING_RES_PARAM)
        .addAnnotation(StringRes.class)
        .build());

    builder
        .addParameter(ParameterSpec.builder(ArrayTypeName.of(TypeName.OBJECT), ARGS_PARAM).build());

    builder.addStatement("$L.setValue($L, $L)", fieldName, STRING_RES_PARAM, ARGS_PARAM)
        .varargs();

    return builder;
  }

  private Builder buildQuantityStringResSetter(Builder builder) {

    builder.addParameter(ParameterSpec.builder(TypeName.INT, PLURAL_RES_PARAM)
        .addAnnotation(PluralsRes.class)
        .build());

    builder.addParameter(ParameterSpec.builder(TypeName.INT, QUANTITY_PARAM).build());

    builder
        .addParameter(ParameterSpec.builder(ArrayTypeName.of(TypeName.OBJECT), ARGS_PARAM).build());

    builder.addStatement("$L.setValue($L, $L, $L)", fieldName, PLURAL_RES_PARAM, QUANTITY_PARAM,
        ARGS_PARAM)
        .varargs();

    return builder;
  }

  private void addStringResCheckForInvalidId(GeneratedModelInfo modelInfo, AttributeInfo attribute,
      String stringResParamName, Builder builder) {
    builder.beginControlFlow("if ($L == 0)", stringResParamName);

    if (attribute.isRequired()) {
      builder.addStatement(
          "\tthrow new $T(\"A string resource value of 0 was set for the required attribute $L\")",
          IllegalArgumentException.class, attribute);
    } else if (shouldUseBitSet(modelInfo)) {
      // If 0 was set then we assume they want to clear and use the default.
      // We simply clear the bitset value so the model thinks this attribute was never set and
      // it will use the default instead
      builder.addComment("Since this is an optional attribute we'll revert to the default value");
      GeneratedModelWriter.clearBitSetIfNeeded(modelInfo, attribute, builder);
    }

    builder.endControlFlow();
  }

  private static MethodSpec finishSetter(MethodSpec.Builder builder) {
    return builder
        .addStatement("return this")
        .build();
  }

  private Builder baseSetter() {
    return baseSetter("");
  }

  private Builder baseSetter(String suffix) {
    String name = attr.generatedSetterName() + suffix;

    Builder builder = MethodSpec.methodBuilder(name)
        .addModifiers(PUBLIC)
        .returns(modelInfo.getParameterizedGeneratedName());

    if (attr.javaDoc != null) {
      builder.addJavadoc(attr.javaDoc);
    }

    addOnMutationCall(builder);
    setBitSetIfNeeded(modelInfo, attr, builder);
    return builder;
  }

  private MethodSpec buildGetter() {
    Builder builder = MethodSpec.methodBuilder(attr.generatedGetterName())
        .addModifiers(PUBLIC)
        .returns(CharSequence.class);

    if (nullable) {
      builder.addAnnotation(Nullable.class);
    }

    return builder
        .addAnnotations(attr.getGetterAnnotations())
        .addParameter(ClassNames.ANDROID_CONTEXT, "context")
        .addStatement("return $L", fieldName + ".toString(context)")
        .build();
  }
}
