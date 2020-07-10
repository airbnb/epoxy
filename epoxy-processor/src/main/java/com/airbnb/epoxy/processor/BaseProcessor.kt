package com.airbnb.epoxy.processor

import com.airbnb.epoxy.processor.ConfigManager.Companion.PROCESSOR_OPTION_DISABLE_GENERATE_BUILDER_OVERLOADS
import com.airbnb.epoxy.processor.ConfigManager.Companion.PROCESSOR_OPTION_DISABLE_GENERATE_GETTERS
import com.airbnb.epoxy.processor.ConfigManager.Companion.PROCESSOR_OPTION_DISABLE_GENERATE_RESET
import com.airbnb.epoxy.processor.ConfigManager.Companion.PROCESSOR_OPTION_DISABLE_KOTLIN_EXTENSION_GENERATION
import com.airbnb.epoxy.processor.ConfigManager.Companion.PROCESSOR_OPTION_ENABLE_PARALLEL
import com.airbnb.epoxy.processor.ConfigManager.Companion.PROCESSOR_OPTION_IMPLICITLY_ADD_AUTO_MODELS
import com.airbnb.epoxy.processor.ConfigManager.Companion.PROCESSOR_OPTION_LOG_TIMINGS
import com.airbnb.epoxy.processor.ConfigManager.Companion.PROCESSOR_OPTION_REQUIRE_ABSTRACT_MODELS
import com.airbnb.epoxy.processor.ConfigManager.Companion.PROCESSOR_OPTION_REQUIRE_HASHCODE
import com.airbnb.epoxy.processor.ConfigManager.Companion.PROCESSOR_OPTION_VALIDATE_MODEL_USAGE
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Filer
import javax.annotation.processing.Messager
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements
import javax.lang.model.util.Types
import kotlin.reflect.KClass

abstract class BaseProcessor : AbstractProcessor(), Asyncable {

    lateinit var messager: Messager
    lateinit var elementUtils: Elements
    lateinit var typeUtils: Types
    lateinit var filer: Filer
    lateinit var configManager: ConfigManager
    lateinit var resourceProcessor: ResourceProcessor

    val dataBindingModuleLookup by lazy {
        DataBindingModuleLookup(
            elementUtils,
            typeUtils,
            logger,
            resourceProcessor
        )
    }

    val modelWriter by lazy {
        GeneratedModelWriter(
            filer,
            typeUtils,
            logger,
            resourceProcessor,
            configManager,
            dataBindingModuleLookup,
            elementUtils,
            this
        )
    }

    val memoizer by lazy { Memoizer(typeUtils, elementUtils, logger) }

    private val kotlinExtensionWriter: KotlinModelBuilderExtensionWriter by lazy {
        KotlinModelBuilderExtensionWriter(filer, this)
    }

    override val logger by lazy { Logger(messager, configManager.logTimings) }

    val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
        logger.logError(exception)
    }

    override val coroutineScope: CoroutineScope =
        CoroutineScope(Dispatchers.Default + coroutineExceptionHandler + SupervisorJob())

    override val coroutinesEnabled: Boolean
        get() = configManager.enableCoroutines

    val generatedModels: MutableList<GeneratedModelInfo> = mutableListOf()

    override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latestSupported()

    override fun getSupportedAnnotationTypes(): Set<String> =
        supportedAnnotations().map { it.java.canonicalName }.toSet()

    abstract fun supportedAnnotations(): List<KClass<*>>

    override fun getSupportedOptions(): Set<String> = setOf(
        PROCESSOR_OPTION_IMPLICITLY_ADD_AUTO_MODELS,
        PROCESSOR_OPTION_VALIDATE_MODEL_USAGE,
        PROCESSOR_OPTION_REQUIRE_ABSTRACT_MODELS,
        PROCESSOR_OPTION_REQUIRE_HASHCODE,
        PROCESSOR_OPTION_DISABLE_KOTLIN_EXTENSION_GENERATION,
        PROCESSOR_OPTION_LOG_TIMINGS,
        PROCESSOR_OPTION_ENABLE_PARALLEL,
        PROCESSOR_OPTION_DISABLE_GENERATE_RESET,
        PROCESSOR_OPTION_DISABLE_GENERATE_GETTERS,
        PROCESSOR_OPTION_DISABLE_GENERATE_BUILDER_OVERLOADS
    )

    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)

        filer = processingEnv.filer
        messager = processingEnv.messager
        elementUtils = processingEnv.elementUtils
        typeUtils = processingEnv.typeUtils
        configManager = ConfigManager(processingEnv.options, elementUtils, typeUtils)
        synchronizationEnabled = configManager.enableCoroutines
        resourceProcessor = ResourceProcessor(processingEnv, logger, elementUtils, typeUtils)
    }

    private var roundNumber = 1

    final override fun process(
        annotations: Set<TypeElement?>,
        roundEnv: RoundEnvironment
    ): Boolean = runBlocking(Dispatchers.Default) {
        try {
            logger.measure("Process Round: $roundNumber") {
                processRound(roundEnv, roundNumber++)
            }
        } catch (e: Exception) {
            logger.logError(e)
        }

        if (roundEnv.processingOver()) {
            val processorName = this@BaseProcessor::class.java.simpleName
            // We wait until the very end to log errors so that all the generated classes are still
            // created.
            // Otherwise the compiler error output is clogged with lots of errors from the generated
            // classes  not existing, which makes it hard to see the actual errors.
            logger.measure("validateAttributesImplementHashCode") {
                validateAttributesImplementHashCode(generatedModels)
            }

            logger.writeExceptions()

            if (!configManager.disableKotlinExtensionGeneration()) {
                logger.measure("generateKotlinExtensions") {
                    // TODO: Potentially generate a single file per model to allow for an isolating processor
                    kotlinExtensionWriter.generateExtensionsForModels(
                        generatedModels,
                        processorName
                    )
                }
            }

            logger.printTimings(processorName)
        }

        // Let any other annotation processors use our annotations if they want to
        false
    }

    protected abstract suspend fun processRound(roundEnv: RoundEnvironment, roundNumber: Int)

    private suspend fun validateAttributesImplementHashCode(
        generatedClasses: Collection<GeneratedModelInfo>
    ) = coroutineScope {
        val hashCodeValidator = HashCodeValidator(typeUtils, elementUtils)

        generatedClasses
            .flatMap { it.attributeInfo }
            .map("validateAttributeInfo") { attributeInfo ->
                if (configManager.requiresHashCode(attributeInfo) &&
                    attributeInfo.useInHash &&
                    !attributeInfo.ignoreRequireHashCode
                ) {
                    hashCodeValidator.validate(attributeInfo)
                }
            }
    }

    suspend fun RoundEnvironment.getElementsAnnotatedWith(annotation: KClass<out Annotation>): Set<Element> {
        return getElementsAnnotatedWith(logger, annotation)
    }
}
