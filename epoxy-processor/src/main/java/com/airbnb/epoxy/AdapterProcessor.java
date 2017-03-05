package com.airbnb.epoxy;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.MethodSpec.Builder;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.processing.Filer;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;

import static com.airbnb.epoxy.ProcessorUtils.EPOXY_AUTO_ADAPTER_TYPE;
import static com.airbnb.epoxy.ProcessorUtils.EPOXY_MODEL_TYPE;
import static com.airbnb.epoxy.ProcessorUtils.isDiffAdapter;
import static com.airbnb.epoxy.ProcessorUtils.isEpoxyModel;
import static com.airbnb.epoxy.ProcessorUtils.validateFieldAccessibleViaGeneratedCode;

class AdapterProcessor {
  private static final String ADAPTER_HELPER_INTERFACE = "com.airbnb.epoxy.AdapterHelper";
  private Filer filer;
  private Elements elementUtils;
  private ErrorLogger errorLogger;
  private final ConfigManager configManager;

  AdapterProcessor(Filer filer, Elements elementUtils,
      ErrorLogger errorLogger, ConfigManager configManager) {
    this.filer = filer;
    this.elementUtils = elementUtils;
    this.errorLogger = errorLogger;
    this.configManager = configManager;
  }

  void process(RoundEnvironment roundEnv, List<ClassToGenerateInfo> generatedModels) {
    LinkedHashMap<TypeElement, AdapterClassInfo> adapterClassMap = new LinkedHashMap<>();

    for (Element modelFieldElement : roundEnv.getElementsAnnotatedWith(AutoModel.class)) {
      try {
        addFieldToAdapterClass(modelFieldElement, adapterClassMap);
      } catch (Exception e) {
        errorLogger.logError(e);
      }
    }

    resolveGeneratedModelNames(adapterClassMap, generatedModels);

    generateJava(adapterClassMap);
  }

  /**
   * Models in the same module as the adapter they are used in will be processed at the same time,
   * so the generated class won't yet exist. This means that we don't have any type information for
   * the generated model and can't correctly import it in the generated helper class. We can resolve
   * the FQN by looking at what models were already generated and finding matching names.
   *
   * @param generatedModels Information about the already generated models. Relies on the model
   *                        processor running first and passing us this information.
   */
  private void resolveGeneratedModelNames(
      LinkedHashMap<TypeElement, AdapterClassInfo> adapterClassMap,
      List<ClassToGenerateInfo> generatedModels) {

    for (AdapterClassInfo adapterClassInfo : adapterClassMap.values()) {
      for (AdapterModelField model : adapterClassInfo.models) {
        if (!hasFullyQualifiedName(model)) {
          model.typeName = getFullyQualifiedModelTypeName(model, generatedModels);
        }
      }
    }
  }

  /**
   * It will have a FQN if it is from a separate library and was already compiled, otherwise if it
   * is from this module we will just have the simple name.
   */
  private boolean hasFullyQualifiedName(AdapterModelField model) {
    return model.typeName.toString().contains(".");
  }

  /**
   * Returns the ClassType of the given model by finding a match in the list of generated models. If
   * no match is found the original model type is returned as a fallback.
   */
  private TypeName getFullyQualifiedModelTypeName(AdapterModelField model,
      List<ClassToGenerateInfo> generatedModels) {
    String modelName = model.typeName.toString();
    for (ClassToGenerateInfo generatedModel : generatedModels) {
      String generatedName = generatedModel.getGeneratedName().toString();
      if (generatedName.endsWith("." + modelName)) {
        return generatedModel.getGeneratedName();
      }
    }

    // Fallback to using the same name
    return model.typeName;
  }

  private void addFieldToAdapterClass(Element modelField,
      LinkedHashMap<TypeElement, AdapterClassInfo> adapterClassMap) {
    TypeElement adapterClassElement = (TypeElement) modelField.getEnclosingElement();
    AdapterClassInfo adapterClass = getOrCreateTargetClass(adapterClassMap, adapterClassElement);
    adapterClass.addModel(buildFieldInfo(modelField));
  }

  private AdapterClassInfo getOrCreateTargetClass(
      Map<TypeElement, AdapterClassInfo> adapterClassMap, TypeElement adapterClassElement) {

    if (!isDiffAdapter(adapterClassElement)) {
      errorLogger.logError("Class with %s annotations must extend %s (%s)",
          AutoModel.class.getSimpleName(), EPOXY_AUTO_ADAPTER_TYPE,
          adapterClassElement.getSimpleName());
    }

    AdapterClassInfo adapterClassInfo = adapterClassMap.get(adapterClassElement);

    if (adapterClassInfo == null) {
      adapterClassInfo = new AdapterClassInfo(elementUtils, adapterClassElement);
      adapterClassMap.put(adapterClassElement, adapterClassInfo);
    }

    return adapterClassInfo;
  }

  private AdapterModelField buildFieldInfo(Element modelFieldElement) {
    validateFieldAccessibleViaGeneratedCode(modelFieldElement, AutoModel.class, errorLogger);

    TypeMirror fieldType = modelFieldElement.asType();
    if (fieldType.getKind() != TypeKind.ERROR) {
      // If the field is a generated Epoxy model then the class won't have been generated
      // yet and it won't have type info. If the type can't be found that we assume it is
      // a generated model and is ok.
      if (!isEpoxyModel(fieldType)) {
        errorLogger.logError("Fields with %s annotations must be of type %s (%s#%s)",
            AutoModel.class.getSimpleName(), EPOXY_MODEL_TYPE,
            modelFieldElement.getEnclosingElement().getSimpleName(),
            modelFieldElement.getSimpleName());
      }

      TypeElement typeElement = (TypeElement) ((DeclaredType) fieldType).asElement();
      if (typeElement.getNestingKind().isNested()) {
        if (!typeElement.getModifiers().contains(Modifier.STATIC)) {
          errorLogger
              .logError(
                  "Types with %s annotations must be static if they are nested classes (%s#%s)",
                  AutoModel.class.getSimpleName(),
                  modelFieldElement.getEnclosingElement().getSimpleName(),
                  modelFieldElement.getSimpleName());
        }
      }
    }

    return new AdapterModelField(modelFieldElement);
  }

  private void generateJava(LinkedHashMap<TypeElement, AdapterClassInfo> adapterClassMap) {
    for (Entry<TypeElement, AdapterClassInfo> adapterInfo : adapterClassMap.entrySet()) {
      try {
        generateHelperClassForAdapter(adapterInfo.getValue());
      } catch (Exception e) {
        errorLogger.logError(e);
      }
    }
  }

  private void generateHelperClassForAdapter(AdapterClassInfo adapterInfo) throws IOException {
    ClassName superclass = ClassName.get(elementUtils.getTypeElement(ADAPTER_HELPER_INTERFACE));
    ParameterizedTypeName parameterizedSuperClass =
        ParameterizedTypeName.get(superclass, adapterInfo.adapterClassType);

    TypeSpec.Builder builder = TypeSpec.classBuilder(adapterInfo.generatedClassName)
        .addJavadoc("Generated file. Do not modify!")
        .addModifiers(Modifier.PUBLIC)
        .superclass(parameterizedSuperClass)
        .addField(adapterInfo.adapterClassType, "adapter", Modifier.FINAL, Modifier.PRIVATE)
        .addMethod(buildConstructor(adapterInfo))
        .addMethod(buildModelsMethod(adapterInfo));

    if (configManager.validateAutoAdapterUsage(adapterInfo)) {
      builder.addFields(buildFieldsToSaveModelsForValidation(adapterInfo))
          .addMethod(buildValidateModelsHaveNotChangedMethod(adapterInfo))
          .addMethod(buildValidateSameValueMethod(adapterInfo))
          .addMethod(buildSaveModelsForNextValidationMethod(adapterInfo));
    }

    JavaFile.builder(adapterInfo.generatedClassName.packageName(), builder.build())
        .build()
        .writeTo(filer);
  }

  private MethodSpec buildConstructor(AdapterClassInfo adapterInfo) {
    ParameterSpec adapterParam = ParameterSpec
        .builder(adapterInfo.adapterClassType, "adapter")
        .build();

    return MethodSpec.constructorBuilder()
        .addParameter(adapterParam)
        .addModifiers(Modifier.PUBLIC)
        .addStatement("this.adapter = adapter")
        .build();
  }

  /**
   * A field is created to save a reference to the model we create. Before the new buildModels phase
   * we check that it is the same object as on the adapter, validating that the user has not
   * manually assigned a new model to the AutoModel field.
   */
  private Iterable<FieldSpec> buildFieldsToSaveModelsForValidation(AdapterClassInfo adapterInfo) {
    List<FieldSpec> fields = new ArrayList<>();

    for (AdapterModelField model : adapterInfo.models) {
      fields.add(FieldSpec.builder(Object.class, model.fieldName, Modifier.PRIVATE).build());
    }

    return fields;
  }

  private MethodSpec buildValidateModelsHaveNotChangedMethod(AdapterClassInfo adapterInfo) {
    Builder builder = MethodSpec.methodBuilder("validateModelsHaveNotChanged")
        .addModifiers(Modifier.PRIVATE);

    // Validate that annotated fields are null
    for (AdapterModelField model : adapterInfo.models) {
      builder.addStatement("validateSameModel($L, adapter.$L, $S)",
          model.fieldName, model.fieldName, model.fieldName);
    }

    return builder.build();
  }

  private MethodSpec buildValidateSameValueMethod(AdapterClassInfo adapterInfo) {
    return MethodSpec.methodBuilder("validateSameModel")
        .addModifiers(Modifier.PRIVATE)
        .addParameter(Object.class, "expectedObject")
        .addParameter(Object.class, "actualObject")
        .addParameter(String.class, "fieldName")
        .beginControlFlow("if (expectedObject != actualObject)")
        .addStatement(
            "throw new $T(\"Fields annotated with $L cannot be directly assigned. The adapter "
                + "manages these fields for you. (\" + adapter.getClass().getSimpleName() + \"#\""
                + " + "
                + "fieldName + \")\")",
            IllegalStateException.class,
            AutoModel.class.getSimpleName())
        .endControlFlow()
        .build();
  }

  private MethodSpec buildSaveModelsForNextValidationMethod(AdapterClassInfo adapterInfo) {
    Builder builder = MethodSpec.methodBuilder("saveModelsForNextValidation")
        .addModifiers(Modifier.PRIVATE);

    for (AdapterModelField model : adapterInfo.models) {
      builder.addStatement("$L = adapter.$L", model.fieldName, model.fieldName);
    }

    return builder.build();
  }

  private MethodSpec buildModelsMethod(AdapterClassInfo adapterInfo) {
    Builder builder = MethodSpec.methodBuilder("resetAutoModels")
        .addAnnotation(Override.class)
        .addModifiers(Modifier.PUBLIC);

    if (configManager.validateAutoAdapterUsage(adapterInfo)) {
      builder.addStatement("validateModelsHaveNotChanged()");
    }

    long id = -1;
    for (AdapterModelField model : adapterInfo.models) {
      builder.addStatement("adapter.$L = new $T()", model.fieldName, model.typeName)
          .addStatement("adapter.$L.id($L)", model.fieldName, id--);
    }

    if (configManager.validateAutoAdapterUsage(adapterInfo)) {
      builder.addStatement("saveModelsForNextValidation()");
    }

    return builder.build();
  }
}
