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
import java.util.Set;

import javax.annotation.processing.Filer;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import static com.airbnb.epoxy.Utils.EPOXY_CONTROLLER_TYPE;
import static com.airbnb.epoxy.Utils.EPOXY_MODEL_TYPE;
import static com.airbnb.epoxy.Utils.UNTYPED_EPOXY_MODEL_TYPE;
import static com.airbnb.epoxy.Utils.belongToTheSamePackage;
import static com.airbnb.epoxy.Utils.getClassName;
import static com.airbnb.epoxy.Utils.isController;
import static com.airbnb.epoxy.Utils.isEpoxyModel;
import static com.airbnb.epoxy.Utils.isSubtype;
import static com.airbnb.epoxy.Utils.validateFieldAccessibleViaGeneratedCode;

class ControllerProcessor {
  private static final String CONTROLLER_HELPER_INTERFACE = "com.airbnb.epoxy.ControllerHelper";
  private Filer filer;
  private Elements elementUtils;
  private Types typeUtils;
  private ErrorLogger errorLogger;
  private final ConfigManager configManager;
  private final Map<TypeElement, ControllerClassInfo> controllerClassMap = new LinkedHashMap<>();

  ControllerProcessor(Filer filer, Elements elementUtils, Types typeUtils,
      ErrorLogger errorLogger, ConfigManager configManager) {
    this.filer = filer;
    this.elementUtils = elementUtils;
    this.typeUtils = typeUtils;
    this.errorLogger = errorLogger;
    this.configManager = configManager;
  }

  void process(RoundEnvironment roundEnv) {
    for (Element modelFieldElement : roundEnv.getElementsAnnotatedWith(AutoModel.class)) {
      try {
        addFieldToControllerClass(modelFieldElement, controllerClassMap);
      } catch (Exception e) {
        errorLogger.logError(e);
      }
    }

    try {
      updateClassesForInheritance(controllerClassMap);
    } catch (Exception e) {
      errorLogger.logError(e);
    }
  }

  /**
   * True if controller classes have been parsed and their java classes need to be written. We need
   * to wait for other models to finish being generated first so we can resolve generated model
   * references.
   *
   * @see #resolveGeneratedModelsAndWriteJava(List)
   */
  boolean hasControllersToGenerate() {
    return !controllerClassMap.isEmpty();
  }

  void resolveGeneratedModelsAndWriteJava(List<GeneratedModelInfo> generatedModels) {
    resolveGeneratedModelNames(controllerClassMap, generatedModels);
    generateJava(controllerClassMap);
    controllerClassMap.clear();
  }

  /**
   * Models in the same module as the controller they are used in will be processed at the same
   * time, so the generated class won't yet exist. This means that we don't have any type
   * information for the generated model and can't correctly import it in the generated helper
   * class. We can resolve the FQN by looking at what models were already generated and finding
   * matching names.
   *
   * @param generatedModels Information about the already generated models. Relies on the model
   *                        processor running first and passing us this information.
   */
  private void resolveGeneratedModelNames(Map<TypeElement, ControllerClassInfo> controllerClassMap,
      List<GeneratedModelInfo> generatedModels) {

    for (ControllerClassInfo controllerClassInfo : controllerClassMap.values()) {
      for (ControllerModelField model : controllerClassInfo.models) {
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
  private boolean hasFullyQualifiedName(ControllerModelField model) {
    return model.typeName.toString().contains(".");
  }

  /**
   * Returns the ClassType of the given model by finding a match in the list of generated models. If
   * no match is found the original model type is returned as a fallback.
   */
  private TypeName getFullyQualifiedModelTypeName(ControllerModelField model,
      List<GeneratedModelInfo> generatedModels) {
    String modelName = model.typeName.toString();
    for (GeneratedModelInfo generatedModel : generatedModels) {
      String generatedName = generatedModel.getGeneratedName().toString();
      if (generatedName.endsWith("." + modelName)) {
        return generatedModel.getGeneratedName();
      }
    }

    // Fallback to using the same name
    return model.typeName;
  }

  private void addFieldToControllerClass(Element modelField,
      Map<TypeElement, ControllerClassInfo> controllerClassMap) {

    TypeElement controllerClassElement = (TypeElement) modelField.getEnclosingElement();

    ControllerClassInfo controllerClass =
        getOrCreateTargetClass(controllerClassMap, controllerClassElement);

    controllerClass.addModel(buildFieldInfo(modelField));
  }

  /**
   * Check each controller for super classes that also have auto models. For each super class with
   * auto model we add those models to the auto models of the generated class, so that a
   * generated class contains all the models of its super classes combined.
   * <p>
   * One caveat is that if a sub class is in a different package than its super class we can't
   * include auto models that are package private, otherwise the generated class won't compile.
   */
  private void updateClassesForInheritance(
      Map<TypeElement, ControllerClassInfo> controllerClassMap) {
    for (Entry<TypeElement, ControllerClassInfo> entry : controllerClassMap.entrySet()) {
      TypeElement thisClass = entry.getKey();

      Map<TypeElement, ControllerClassInfo> otherClasses = new LinkedHashMap<>(controllerClassMap);
      otherClasses.remove(thisClass);

      for (Entry<TypeElement, ControllerClassInfo> otherEntry : otherClasses.entrySet()) {
        TypeElement otherClass = otherEntry.getKey();

        if (!isSubtype(thisClass, otherClass, typeUtils)) {
          continue;
        }

        Set<ControllerModelField> otherControllerModelFields = otherEntry.getValue().models;

        if (belongToTheSamePackage(thisClass, otherClass, elementUtils)) {
          entry.getValue().addModels(otherControllerModelFields);
        } else {
          for (ControllerModelField controllerModelField : otherControllerModelFields) {
            if (!controllerModelField.packagePrivate) {
              entry.getValue().addModel(controllerModelField);
            }
          }
        }
      }
    }
  }

  private ControllerClassInfo getOrCreateTargetClass(
      Map<TypeElement, ControllerClassInfo> controllerClassMap,
      TypeElement controllerClassElement) {

    if (!isController(controllerClassElement)) {
      errorLogger.logError("Class with %s annotations must extend %s (%s)",
          AutoModel.class.getSimpleName(), EPOXY_CONTROLLER_TYPE,
          controllerClassElement.getSimpleName());
    }

    ControllerClassInfo controllerClassInfo = controllerClassMap.get(controllerClassElement);

    if (controllerClassInfo == null) {
      controllerClassInfo = new ControllerClassInfo(elementUtils, controllerClassElement);
      controllerClassMap.put(controllerClassElement, controllerClassInfo);
    }

    return controllerClassInfo;
  }

  private ControllerModelField buildFieldInfo(Element modelFieldElement) {
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
    }

    return new ControllerModelField(modelFieldElement);
  }

  private void generateJava(Map<TypeElement, ControllerClassInfo> controllerClassMap) {
    for (Entry<TypeElement, ControllerClassInfo> controllerInfo : controllerClassMap.entrySet()) {
      try {
        generateHelperClassForController(controllerInfo.getValue());
      } catch (Exception e) {
        errorLogger.logError(e);
      }
    }
  }

  private void generateHelperClassForController(ControllerClassInfo controllerInfo)
      throws IOException {
    ClassName superclass = ClassName.get(elementUtils.getTypeElement(CONTROLLER_HELPER_INTERFACE));
    ParameterizedTypeName parameterizeSuperClass =
        ParameterizedTypeName.get(superclass, controllerInfo.controllerClassType);

    TypeSpec.Builder builder = TypeSpec.classBuilder(controllerInfo.generatedClassName)
        .addJavadoc("Generated file. Do not modify!")
        .addModifiers(Modifier.PUBLIC)
        .superclass(parameterizeSuperClass)
        .addField(controllerInfo.controllerClassType, "controller", Modifier.FINAL,
            Modifier.PRIVATE)
        .addMethod(buildConstructor(controllerInfo))
        .addMethod(buildResetModelsMethod(controllerInfo));

    if (configManager.shouldValidateModelUsage()) {
      builder.addFields(buildFieldsToSaveModelsForValidation(controllerInfo))
          .addMethod(buildValidateModelsHaveNotChangedMethod(controllerInfo))
          .addMethod(buildValidateSameValueMethod())
          .addMethod(buildSaveModelsForNextValidationMethod(controllerInfo));
    }

    JavaFile.builder(controllerInfo.generatedClassName.packageName(), builder.build())
        .build()
        .writeTo(filer);
  }

  private MethodSpec buildConstructor(ControllerClassInfo controllerInfo) {
    ParameterSpec controllerParam = ParameterSpec
        .builder(controllerInfo.controllerClassType, "controller")
        .build();

    return MethodSpec.constructorBuilder()
        .addParameter(controllerParam)
        .addModifiers(Modifier.PUBLIC)
        .addStatement("this.controller = controller")
        .build();
  }

  /**
   * A field is created to save a reference to the model we create. Before the new buildModels phase
   * we check that it is the same object as on the controller, validating that the user has not
   * manually assigned a new model to the AutoModel field.
   */
  private Iterable<FieldSpec> buildFieldsToSaveModelsForValidation(
      ControllerClassInfo controllerInfo) {
    List<FieldSpec> fields = new ArrayList<>();

    for (ControllerModelField model : controllerInfo.models) {
      fields.add(FieldSpec.builder(getClassName(UNTYPED_EPOXY_MODEL_TYPE),
          model.fieldName, Modifier.PRIVATE).build());
    }

    return fields;
  }

  private MethodSpec buildValidateModelsHaveNotChangedMethod(ControllerClassInfo controllerInfo) {
    Builder builder = MethodSpec.methodBuilder("validateModelsHaveNotChanged")
        .addModifiers(Modifier.PRIVATE);

    // Validate that annotated fields have not been reassigned or had their id changed
    long id = -1;
    for (ControllerModelField model : controllerInfo.models) {
      builder.addStatement("validateSameModel($L, controller.$L, $S, $L)",
          model.fieldName, model.fieldName, model.fieldName, id--);
    }

    return builder
        .addStatement("validateModelHashCodesHaveNotChanged(controller)")
        .build();
  }

  private MethodSpec buildValidateSameValueMethod() {
    return MethodSpec.methodBuilder("validateSameModel")
        .addModifiers(Modifier.PRIVATE)
        .addParameter(getClassName(UNTYPED_EPOXY_MODEL_TYPE), "expectedObject")
        .addParameter(getClassName(UNTYPED_EPOXY_MODEL_TYPE), "actualObject")
        .addParameter(String.class, "fieldName")
        .addParameter(TypeName.INT, "id")
        .beginControlFlow("if (expectedObject != actualObject)")
        .addStatement(
            "throw new $T(\"Fields annotated with $L cannot be directly assigned. The controller "
                + "manages these fields for you. (\" + controller.getClass().getSimpleName() + "
                + "\"#\" + fieldName + \")\")",
            IllegalStateException.class,
            AutoModel.class.getSimpleName())
        .endControlFlow()
        .beginControlFlow("if (actualObject != null && actualObject.id() != id)")
        .addStatement(
            "throw new $T(\"Fields annotated with $L cannot have their id changed manually. The "
                + "controller manages the ids of these models for you. (\" + controller.getClass()"
                + ".getSimpleName() + \"#\" + fieldName + \")\")",
            IllegalStateException.class,
            AutoModel.class.getSimpleName())
        .endControlFlow()
        .build();
  }

  private MethodSpec buildSaveModelsForNextValidationMethod(ControllerClassInfo controllerInfo) {
    Builder builder = MethodSpec.methodBuilder("saveModelsForNextValidation")
        .addModifiers(Modifier.PRIVATE);

    for (ControllerModelField model : controllerInfo.models) {
      builder.addStatement("$L = controller.$L", model.fieldName, model.fieldName);
    }

    return builder.build();
  }

  private MethodSpec buildResetModelsMethod(ControllerClassInfo controllerInfo) {
    Builder builder = MethodSpec.methodBuilder("resetAutoModels")
        .addAnnotation(Override.class)
        .addModifiers(Modifier.PUBLIC);

    if (configManager.shouldValidateModelUsage()) {
      builder.addStatement("validateModelsHaveNotChanged()");
    }

    boolean implicitlyAddAutoModels = configManager.implicitlyAddAutoModels(controllerInfo);
    long id = -1;
    for (ControllerModelField model : controllerInfo.models) {
      builder.addStatement("controller.$L = new $T()", model.fieldName, model.typeName)
          .addStatement("controller.$L.id($L)", model.fieldName, id--);

      if (implicitlyAddAutoModels) {
        builder.addStatement("setControllerToStageTo(controller.$L, controller)", model.fieldName);
      }
    }

    if (configManager.shouldValidateModelUsage()) {
      builder.addStatement("saveModelsForNextValidation()");
    }

    return builder.build();
  }
}
