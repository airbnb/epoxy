package com.airbnb.epoxy

import com.airbnb.epoxy.ClassNames.*
import com.airbnb.epoxy.GeneratedModelWriter.*
import com.airbnb.epoxy.Utils.*
import com.squareup.javapoet.*
import com.squareup.javapoet.TypeSpec.*
import java.util.*
import javax.annotation.processing.*
import javax.lang.model.element.*
import javax.lang.model.util.*

internal class LithoSpecProcessor(
        private val elementUtils: Elements,
        private val typeUtils: Types,
        private val configManager: ConfigManager,
        private val errorLogger: ErrorLogger,
        private val modelWriter: GeneratedModelWriter
) {
    private var layoutSpecAnnotationClass: Class<out Annotation>? = null

    fun processSpecs(roundEnv: RoundEnvironment): Collection<LithoModelInfo> {

        if (!hasLithoEpoxyDependency()) {
            // If the epoxy-litho module has not been included then we don't have access to the Epoxy
            // litho model and can't build a model that extends it
            return emptyList()
        }

        layoutSpecAnnotationClass = getAnnotationClass(ClassNames.LITHO_ANNOTATION_LAYOUT_SPEC)
        if (layoutSpecAnnotationClass == null) {
            // There is no dependency on Litho so there aren't any litho components to check for
            return emptyList()
        }

        val modelInfoMap = LinkedHashMap<TypeElement, LithoModelInfo>()
        roundEnv.getElementsAnnotatedWith(layoutSpecAnnotationClass)
                .filterIsInstance<TypeElement>()
                .forEach { modelInfoMap.put(it, LithoModelInfo(typeUtils, elementUtils, it)) }

        val propClass = getAnnotationClass(ClassNames.LITHO_ANNOTATION_PROP)
        val hashCodeValidator = HashCodeValidator(typeUtils, elementUtils)
        for (propElement in roundEnv.getElementsAnnotatedWith(propClass)) {
            val lithoModelInfo = getModelInfoForProp(modelInfoMap, propElement)
            lithoModelInfo?.addProp(propElement, hashCodeValidator)
        }

        for ((_, modelInfo) in modelInfoMap) {
            try {
                modelWriter.generateClassForModel(modelInfo, object : BuilderHooks() {
                    override fun beforeFinalBuild(builder: Builder) {
                        updateGeneratedClassForLithoComponent(modelInfo, builder)
                    }
                })
            } catch (e: Exception) {
                errorLogger.logError(e, "Error generating model classes")
            }

        }

        return modelInfoMap.values
    }

    // Only true if the epoxy-litho module is included in dependencies
    private fun hasLithoEpoxyDependency(): Boolean =
            getTypeMirror(EPOXY_LITHO_MODEL.reflectionName(), elementUtils) != null

    private fun updateGeneratedClassForLithoComponent(
            modelInfo: LithoModelInfo,
            classBuilder: Builder
    ) {
        // Adding the "buildComponent" method
        val methodBuilder = MethodSpec.methodBuilder("buildComponent")
                .addAnnotation(Override::class.java)
                .addModifiers(Modifier.PROTECTED)
                .returns(
                        ParameterizedTypeName.get(ClassNames.LITHO_COMPONENT,
                                                  modelInfo.lithoComponentName)
                )
                .addParameter(ClassNames.LITHO_COMPONENT_CONTEXT, "context")
                .addCode("return \$T.create(context)", modelInfo.lithoComponentName)

        for (attributeInfo in modelInfo.attributeInfo) {
            methodBuilder.addCode(".\$L(\$L)", attributeInfo.getFieldName(),
                                  attributeInfo.getFieldName())
        }

        methodBuilder.addStatement(".build()")

        classBuilder.addMethod(methodBuilder.build())
    }

    private fun getModelInfoForProp(
            modelInfoMap: Map<TypeElement, LithoModelInfo>,
            propElement: Element
    ): LithoModelInfo? {
        val methodElement = propElement.enclosingElement ?: return null

        val classElement = methodElement.enclosingElement

        return if (classElement.getAnnotation(layoutSpecAnnotationClass) == null) {
            null
        } else {
            modelInfoMap[classElement]
        }

    }
}
