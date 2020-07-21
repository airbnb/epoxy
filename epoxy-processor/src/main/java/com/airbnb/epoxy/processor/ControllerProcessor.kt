package com.airbnb.epoxy.processor

import com.airbnb.epoxy.AutoModel
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.FieldSpec
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.ParameterSpec
import com.squareup.javapoet.ParameterizedTypeName
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.TypeSpec
import net.ltgt.gradle.incap.IncrementalAnnotationProcessor
import net.ltgt.gradle.incap.IncrementalAnnotationProcessorType
import java.util.ArrayList
import java.util.LinkedHashMap
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.lang.model.type.TypeKind
import kotlin.reflect.KClass

// TODO: This could be an isolating processor except that the PackageEpoxyConfig annotation
// can change the `implicitlyAddAutoModels` setting.
@IncrementalAnnotationProcessor(IncrementalAnnotationProcessorType.AGGREGATING)
class ControllerProcessor : BaseProcessorWithPackageConfigs() {
    override val usesPackageEpoxyConfig: Boolean = true
    override val usesModelViewConfig: Boolean = false

    override fun additionalSupportedAnnotations(): List<KClass<*>> = listOf(
        AutoModel::class
    )

    override suspend fun processRound(roundEnv: RoundEnvironment, roundNumber: Int) {
        super.processRound(roundEnv, roundNumber)
        val controllerClassMap: MutableMap<TypeElement, ControllerClassInfo> = LinkedHashMap()

        for (modelFieldElement in roundEnv.getElementsAnnotatedWith(AutoModel::class)) {
            try {
                addFieldToControllerClass(modelFieldElement, controllerClassMap)
            } catch (e: Exception) {
                logger.logError(e)
            }
        }

        try {
            updateClassesForInheritance(controllerClassMap)
        } catch (e: Exception) {
            logger.logError(e)
        }

        generateJava(controllerClassMap)
    }

    private fun addFieldToControllerClass(
        modelField: Element,
        controllerClassMap: MutableMap<TypeElement, ControllerClassInfo>
    ) {
        val controllerClassElement = modelField.enclosingElement as TypeElement
        val controllerClass = getOrCreateTargetClass(controllerClassMap, controllerClassElement)
        controllerClass.addModel(buildFieldInfo(controllerClass, modelField))
    }

    /**
     * Check each controller for super classes that also have auto models. For each super class with
     * auto model we add those models to the auto models of the generated class, so that a
     * generated class contains all the models of its super classes combined.
     *
     * One caveat is that if a sub class is in a different package than its super class we can't
     * include auto models that are package private, otherwise the generated class won't compile.
     */
    private fun updateClassesForInheritance(
        controllerClassMap: Map<TypeElement, ControllerClassInfo>
    ) {
        for ((thisClass, value) in controllerClassMap) {
            val otherClasses: MutableMap<TypeElement, ControllerClassInfo> =
                LinkedHashMap(controllerClassMap)

            otherClasses.remove(thisClass)
            for ((otherClass, controllerInfo) in otherClasses) {
                if (!Utils.isSubtype(thisClass, otherClass, typeUtils)) {
                    continue
                }
                val otherControllerModelFields: Set<ControllerModelField> = controllerInfo.models
                if (Utils.belongToTheSamePackage(
                    thisClass,
                    otherClass,
                    elementUtils
                )
                ) {
                    value.addModels(otherControllerModelFields)
                } else {
                    for (controllerModelField in otherControllerModelFields) {
                        if (!controllerModelField.packagePrivate) {
                            value.addModel(controllerModelField)
                        }
                    }
                }
            }
        }
    }

    private fun getOrCreateTargetClass(
        controllerClassMap: MutableMap<TypeElement, ControllerClassInfo>,
        controllerClassElement: TypeElement
    ): ControllerClassInfo {
        if (!Utils.isController(controllerClassElement)) {
            logger.logError(
                "Class with %s annotations must extend %s (%s)",
                AutoModel::class.java.simpleName,
                Utils.EPOXY_CONTROLLER_TYPE,
                controllerClassElement.simpleName
            )
        }
        var controllerClassInfo = controllerClassMap[controllerClassElement]
        if (controllerClassInfo == null) {
            controllerClassInfo =
                ControllerClassInfo(elementUtils, controllerClassElement, resourceProcessor)
            controllerClassMap[controllerClassElement] = controllerClassInfo
        }
        return controllerClassInfo
    }

    private fun buildFieldInfo(
        controllerClass: ControllerClassInfo,
        modelFieldElement: Element
    ): ControllerModelField {
        Utils.validateFieldAccessibleViaGeneratedCode(
            modelFieldElement,
            AutoModel::class.java,
            logger
        )
        val fieldName = modelFieldElement.simpleName.toString()
        val fieldType = modelFieldElement.asType()

        val modelTypeName = if (fieldType.kind != TypeKind.ERROR) {
            // If the field is a generated Epoxy model then the class won't have been generated
            // yet and it won't have type info. If the type can't be found that we assume it is
            // a generated model and is ok.
            if (!Utils.isEpoxyModel(fieldType)) {
                logger.logError(
                    "Fields with %s annotations must be of type %s (%s#%s)",
                    AutoModel::class.java.simpleName,
                    Utils.EPOXY_MODEL_TYPE,
                    modelFieldElement.enclosingElement.simpleName,
                    modelFieldElement.simpleName
                )
            }

            modelFieldElement.asType().typeNameSynchronized()
        } else {
            // We only have the simple name of the model, since it isn't generated yet.
            // We can find the FQN by looking in imports. Imports aren't actually directly accessible
            // in the AST, so we have a hacky workaround by accessing the compiler tree

            val simpleName = fieldType.toString()

            val packageName = controllerClass
                .imports
                .firstOrNull { it.endsWith(simpleName) }
                ?.substringBeforeLast(".$simpleName")
                // With no import we assume the model is in the same package as the controller
                ?: controllerClass.generatedClassName.packageName()

            ClassName.get(packageName, simpleName)
        }

        return ControllerModelField(
            fieldName = fieldName,
            typeName = modelTypeName,
            packagePrivate = Utils.isFieldPackagePrivate(modelFieldElement)
        )
    }

    private fun generateJava(controllerClassMap: Map<TypeElement, ControllerClassInfo>) {
        for ((_, value) in controllerClassMap) {
            try {
                generateHelperClassForController(value)
            } catch (e: Exception) {
                logger.logError(e)
            }
        }
    }

    private fun generateHelperClassForController(controllerInfo: ControllerClassInfo) {
        val parameterizeSuperClass = ParameterizedTypeName.get(
            ClassNames.EPOXY_CONTROLLER_HELPER,
            controllerInfo.controllerClassType
        )

        val classSpec = TypeSpec.classBuilder(controllerInfo.generatedClassName).apply {

            addJavadoc("Generated file. Do not modify!")
            addModifiers(Modifier.PUBLIC)
            superclass(parameterizeSuperClass)
            addField(
                controllerInfo.controllerClassType,
                "controller",
                Modifier.FINAL,
                Modifier.PRIVATE
            )
            addMethod(buildConstructor(controllerInfo))
            addMethod(buildResetModelsMethod(controllerInfo))

            if (configManager.shouldValidateModelUsage()) {
                addFields(buildFieldsToSaveModelsForValidation(controllerInfo))
                addMethod(buildValidateModelsHaveNotChangedMethod(controllerInfo))
                addMethod(buildValidateSameValueMethod())
                addMethod(buildSaveModelsForNextValidationMethod(controllerInfo))
            }

            addOriginatingElement(controllerInfo.controllerClassElement)

            // Package configs can be used to change the implicit auto add option.
            originatingConfigElements().forEach { configElement ->
                addOriginatingElement(configElement)
            }
        }.build()

        JavaFile.builder(controllerInfo.generatedClassName.packageName(), classSpec)
            .build()
            .writeSynchronized(filer)
    }

    private fun buildConstructor(controllerInfo: ControllerClassInfo): MethodSpec {
        val controllerParam = ParameterSpec
            .builder(controllerInfo.controllerClassType, "controller")
            .build()
        return MethodSpec.constructorBuilder()
            .addParameter(controllerParam)
            .addModifiers(Modifier.PUBLIC)
            .addStatement("this.controller = controller")
            .build()
    }

    /**
     * A field is created to save a reference to the model we create. Before the new buildModels phase
     * we check that it is the same object as on the controller, validating that the user has not
     * manually assigned a new model to the AutoModel field.
     */
    private fun buildFieldsToSaveModelsForValidation(
        controllerInfo: ControllerClassInfo
    ): Iterable<FieldSpec> {
        val fields: MutableList<FieldSpec> = ArrayList()
        for (model in controllerInfo.models) {
            fields.add(
                FieldSpec.builder(
                    Utils.getClassName(Utils.UNTYPED_EPOXY_MODEL_TYPE),
                    model.fieldName, Modifier.PRIVATE
                ).build()
            )
        }
        return fields
    }

    private fun buildValidateModelsHaveNotChangedMethod(controllerInfo: ControllerClassInfo): MethodSpec {
        val builder = MethodSpec.methodBuilder("validateModelsHaveNotChanged")
            .addModifiers(Modifier.PRIVATE)

        // Validate that annotated fields have not been reassigned or had their id changed
        var id: Long = -1
        for (model in controllerInfo.models) {
            builder.addStatement(
                "validateSameModel(\$L, controller.\$L, \$S, \$L)",
                model.fieldName, model.fieldName, model.fieldName, id--
            )
        }
        return builder
            .addStatement("validateModelHashCodesHaveNotChanged(controller)")
            .build()
    }

    private fun buildValidateSameValueMethod(): MethodSpec {
        return MethodSpec.methodBuilder("validateSameModel")
            .addModifiers(Modifier.PRIVATE)
            .addParameter(
                Utils.getClassName(Utils.UNTYPED_EPOXY_MODEL_TYPE),
                "expectedObject"
            )
            .addParameter(
                Utils.getClassName(Utils.UNTYPED_EPOXY_MODEL_TYPE),
                "actualObject"
            )
            .addParameter(String::class.java, "fieldName")
            .addParameter(TypeName.INT, "id")
            .beginControlFlow("if (expectedObject != actualObject)")
            .addStatement(
                "throw new \$T(\"Fields annotated with \$L cannot be directly assigned. The controller " +
                    "manages these fields for you. (\" + controller.getClass().getSimpleName() + " +
                    "\"#\" + fieldName + \")\")",
                IllegalStateException::class.java,
                AutoModel::class.java.simpleName
            )
            .endControlFlow()
            .beginControlFlow("if (actualObject != null && actualObject.id() != id)")
            .addStatement(
                "throw new \$T(\"Fields annotated with \$L cannot have their id changed manually. The " +
                    "controller manages the ids of these models for you. (\" + controller.getClass()" +
                    ".getSimpleName() + \"#\" + fieldName + \")\")",
                IllegalStateException::class.java,
                AutoModel::class.java.simpleName
            )
            .endControlFlow()
            .build()
    }

    private fun buildSaveModelsForNextValidationMethod(controllerInfo: ControllerClassInfo): MethodSpec {
        val builder = MethodSpec.methodBuilder("saveModelsForNextValidation")
            .addModifiers(Modifier.PRIVATE)
        for (model in controllerInfo.models) {
            builder.addStatement("\$L = controller.\$L", model.fieldName, model.fieldName)
        }
        return builder.build()
    }

    private fun buildResetModelsMethod(controllerInfo: ControllerClassInfo): MethodSpec {
        val builder = MethodSpec.methodBuilder("resetAutoModels")
            .addAnnotation(Override::class.java)
            .addModifiers(Modifier.PUBLIC)
        if (configManager.shouldValidateModelUsage()) {
            builder.addStatement("validateModelsHaveNotChanged()")
        }
        val implicitlyAddAutoModels =
            configManager.implicitlyAddAutoModels(controllerInfo)
        var id: Long = -1
        for (model in controllerInfo.models) {
            builder.addStatement("controller.\$L = new \$T()", model.fieldName, model.typeName)
                .addStatement("controller.\$L.id(\$L)", model.fieldName, id--)
            if (implicitlyAddAutoModels) {
                builder.addStatement(
                    "setControllerToStageTo(controller.\$L, controller)",
                    model.fieldName
                )
            }
        }
        if (configManager.shouldValidateModelUsage()) {
            builder.addStatement("saveModelsForNextValidation()")
        }
        return builder.build()
    }
}
