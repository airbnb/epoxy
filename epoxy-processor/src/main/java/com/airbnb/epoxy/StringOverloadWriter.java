package com.airbnb.epoxy;

import android.support.annotation.Nullable;
import android.support.annotation.PluralsRes;
import android.support.annotation.StringRes;

import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.MethodSpec.Builder;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;

import java.util.ArrayList;
import java.util.List;

import static com.airbnb.epoxy.GeneratedModelWriter.addOnMutationCall;
import static com.airbnb.epoxy.GeneratedModelWriter.addParameterNullCheckIfNeeded;
import static com.airbnb.epoxy.GeneratedModelWriter.setBitSetIfNeeded;
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
    nullable = attr.hasSetNullability() && attr.isNullable();
  }

  List<MethodSpec> buildMethods() {
    List<MethodSpec> methods = new ArrayList<>();
    methods.add(buildGetter());

    methods.add(finishSetter(buildCharSequenceSetter(baseSetter())));
    methods.add(finishSetter(buildStringResSetter(baseSetter())));
    methods.add(finishSetter(buildStringResWithArgsSetter(baseSetter())));
    methods.add(finishSetter(buildQuantityStringResSetter(baseSetter("QuantityRes"))));

    return methods;
  }

  private Builder buildCharSequenceSetter(Builder builder) {
    String paramName = attr.generatedSetterName();
    ParameterSpec.Builder paramBuilder = ParameterSpec.builder(CharSequence.class, paramName);

    if (nullable) {
      paramBuilder.addAnnotation(Nullable.class);
    }

    addJavaDoc(builder, false);

    builder.addParameter(paramBuilder.build());

    addParameterNullCheckIfNeeded(configManager, attr, paramName, builder);
    builder.addStatement("$L.setValue($L)", fieldName, paramName);

    return builder;
  }

  private Builder buildStringResSetter(Builder builder) {
    builder.addParameter(ParameterSpec.builder(TypeName.INT, STRING_RES_PARAM)
        .addAnnotation(StringRes.class)
        .build());

    addJavaDoc(builder, true);
    builder.addStatement("$L.setValue($L)", fieldName, STRING_RES_PARAM);

    return builder;
  }

  private Builder buildStringResWithArgsSetter(Builder builder) {
    builder.addParameter(ParameterSpec.builder(TypeName.INT, STRING_RES_PARAM)
        .addAnnotation(StringRes.class)
        .build());

    addJavaDoc(builder, true);

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

    addJavaDoc(builder, true);

    builder.addParameter(ParameterSpec.builder(TypeName.INT, QUANTITY_PARAM).build());

    builder
        .addParameter(ParameterSpec.builder(ArrayTypeName.of(TypeName.OBJECT), ARGS_PARAM).build());

    builder.addStatement("$L.setValue($L, $L, $L)", fieldName, PLURAL_RES_PARAM, QUANTITY_PARAM,
        ARGS_PARAM)
        .varargs();

    return builder;
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

    addOnMutationCall(builder);
    setBitSetIfNeeded(modelInfo, attr, builder);
    return builder;
  }

  private void addJavaDoc(Builder builder, boolean forStringRes) {
    if (attr.javaDoc == null) {
      return;
    }

    CodeBlock.Builder javaDoc = CodeBlock.builder();

    if (forStringRes) {
      if (attr.isRequired()) {
        javaDoc.add("Throws if a value <= 0 is set.\n<p>\n");
      } else {
        javaDoc.add(
            "If a value of 0 is set then this attribute will revert to its default value.\n<p>\n");
      }
    }

    builder.addJavadoc(javaDoc.add(attr.javaDoc).build());
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
