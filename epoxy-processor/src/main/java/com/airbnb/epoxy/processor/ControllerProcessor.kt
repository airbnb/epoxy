package com.airbnb.epoxy.processor

import androidx.room.compiler.processing.XElement
import androidx.room.compiler.processing.XFieldElement
import androidx.room.compiler.processing.XFiler
import androidx.room.compiler.processing.XProcessingEnv
import androidx.room.compiler.processing.XRoundEnv
import androidx.room.compiler.processing.XTypeElement
import androidx.room.compiler.processing.addOriginatingElement
import androidx.room.compiler.processing.writeTo
import com.airbnb.epoxy.AutoModel
import com.airbnb.epoxy.processor.ClassNames.EPOXY_MODEL_UNTYPED
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
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
import javax.lang.model.element.Modifier
import kotlin.reflect.KClass

class ControllerProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return ControllerProcessor(environment)
    }
}

// TODO: This could be an isolating processor except that the PackageEpoxyConfig annotation
// can change the `implicitlyAddAutoModels` setting.
@IncrementalAnnotationProcessor(IncrementalAnnotationProcessorType.AGGREGATING)
class ControllerProcessor @JvmOverloads constructor(
    kspEnvironment: SymbolProcessorEnvironment? = null
) : BaseProcessorWithPackageConfigs(kspEnvironment) {
    override val usesPackageEpoxyConfig: Boolean = true
    override val usesModelViewConfig: Boolean = false

    override fun additionalSupportedAnnotations(): List<KClass<*>> = listOf(
        AutoModel::class
    )

    private val classNameToInfo = mutableMapOf<ClassName, ControllerClassInfo>()

    override fun processRound(
        environment: XProcessingEnv,
        round: XRoundEnv,
        memoizer: Memoizer,
        timer: Timer,
        roundNumber: Int
    ): List<XElement> {
        super.processRound(environment, round, memoizer, timer, roundNumber)

        // JavaAP and KAPT can correct error types and still figure out when the type is a generated
        // model that doesn't exist yet. KSP needs to defer those symbols though, and only process
        // them once the class is available.
        val (validFields, invalidFields) = round.getElementsAnnotatedWith(AutoModel::class)
            .filterIsInstance<XFieldElement>()
            .partition { !isKsp() || it.validate() }

        timer.markStepCompleted("get automodel fields")

        validFields.forEach { field ->
            val classElement =
                field.enclosingTypeElement ?: error("Field $field should be used inside a class")
            val targetClassInfo = getOrCreateTargetClass(classElement, memoizer)
            try {
                targetClassInfo.addModel(buildFieldInfo(targetClassInfo, field, memoizer))
            } catch (e: Exception) {
                logger.logError(e)
            }
        }

        timer.markStepCompleted("parse field info")

        // Need to wait until all fields are valid until we can write files, because:
        // 1. multiple fields in the same class are aggregated
        // 2. across classes we need to handle inheritance
        if (invalidFields.isEmpty()) {
            try {
                updateClassesForInheritance(environment, classNameToInfo)
            } catch (e: Exception) {
                logger.logError(e)
            }
            timer.markStepCompleted("lookup inheritance details")

            generateJava(classNameToInfo)
            classNameToInfo.clear()
            timer.markStepCompleted("write automodel helpers")
        }

        return invalidFields
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
        environment: XProcessingEnv,
        controllerClassMap: MutableMap<ClassName, ControllerClassInfo>
    ) {
        for ((thisClassName, thisClassInfo) in controllerClassMap) {
            // Need to look up the types now instead of storing them because if we processed the
            // fields across multiple rounds the stored types cannot be compared.
            val thisClassType = environment.requireType(thisClassName)
            val otherClasses: MutableMap<ClassName, ControllerClassInfo> =
                LinkedHashMap(controllerClassMap)

            otherClasses.remove(thisClassName)
            for ((otherClassName, otherClassInfo) in otherClasses) {
                val otherClassType = environment.requireType(otherClassName)
                if (!thisClassType.isSubTypeOf(otherClassType)) {
                    continue
                }
                val otherControllerModelFields: Set<ControllerModelField> =
                    otherClassInfo.modelsImmutable
                if (thisClassInfo.classPackage == thisClassInfo.classPackage) {
                    thisClassInfo.addModels(otherControllerModelFields)
                } else {
                    for (controllerModelField in otherControllerModelFields) {
                        if (!controllerModelField.packagePrivate) {
                            thisClassInfo.addModel(controllerModelField)
                        }
                    }
                }
            }
        }
    }

    private fun getOrCreateTargetClass(
        controllerClassElement: XTypeElement,
        memoizer: Memoizer
    ): ControllerClassInfo = classNameToInfo.getOrPut(controllerClassElement.className) {
        if (!controllerClassElement.isEpoxyController(memoizer)) {
            logger.logError(
                controllerClassElement,
                "Class with %s annotations must extend %s (%s)",
                AutoModel::class.java.simpleName,
                Utils.EPOXY_CONTROLLER_TYPE,
                controllerClassElement.name
            )
        }

        ControllerClassInfo(controllerClassElement, resourceProcessor, memoizer)
    }

    private fun buildFieldInfo(
        classElement: ControllerClassInfo,
        modelFieldElement: XFieldElement,
        memoizer: Memoizer
    ): ControllerModelField {
        Utils.validateFieldAccessibleViaGeneratedCode(
            fieldElement = modelFieldElement,
            annotationClass = AutoModel::class.java,
            logger = logger,
        )
        val fieldName = modelFieldElement.name
        val fieldType = modelFieldElement.type

        val modelTypeName = if (!fieldType.isError()) {
            // If the field is a generated Epoxy model then the class won't have been generated
            // yet and it won't have type info. If the type can't be found that we assume it is
            // a generated model and is ok.
            if (!fieldType.isEpoxyModel(memoizer)) {
                logger.logError(
                    modelFieldElement,
                    "Fields with %s annotations must be of type %s (%s#%s)",
                    AutoModel::class.java.simpleName,
                    Utils.EPOXY_MODEL_TYPE,
                    modelFieldElement.enclosingElement.expectName,
                    modelFieldElement.name
                )
            }

            fieldType.typeNameWithWorkaround(memoizer)
        } else {
            // We only have the simple name of the model, since it isn't generated yet.
            // We can find the FQN by looking in imports. Imports aren't actually directly accessible
            // in the AST, so we have a hacky workaround by accessing the compiler tree

            val simpleName = fieldType.toString()

            val packageName = classElement.imports
                .firstOrNull { it.endsWith(simpleName) }
                ?.substringBeforeLast(".$simpleName")
                // With no import we assume the model is in the same package as the controller
                ?: classElement.classPackage

            ClassName.get(packageName, simpleName)
        }

        return ControllerModelField(
            fieldName = fieldName,
            typeName = modelTypeName,
            packagePrivate = Utils.isFieldPackagePrivate(modelFieldElement)
        )
    }

    private fun generateJava(controllerClassMap: MutableMap<ClassName, ControllerClassInfo>) {
        for ((_, classInfo) in controllerClassMap) {
            try {
                generateHelperClassForController(classInfo)
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

            addOriginatingElement(controllerInfo.originatingElement)

            // Package configs can be used to change the implicit auto add option.
            originatingConfigElements().forEach { configElement ->
                addOriginatingElement(configElement)
            }
        }.build()

        JavaFile.builder(controllerInfo.generatedClassName.packageName(), classSpec)
            .build()
            .writeTo(filer, mode = XFiler.Mode.Aggregating)
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
                    ClassNames.EPOXY_MODEL_UNTYPED,
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
                EPOXY_MODEL_UNTYPED,
                "expectedObject"
            )
            .addParameter(
                EPOXY_MODEL_UNTYPED,
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
