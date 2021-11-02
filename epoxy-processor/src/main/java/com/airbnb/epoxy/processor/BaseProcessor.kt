package com.airbnb.epoxy.processor

import androidx.room.compiler.processing.XElement
import androidx.room.compiler.processing.XFiler
import androidx.room.compiler.processing.XMessager
import androidx.room.compiler.processing.XProcessingEnv
import androidx.room.compiler.processing.XRoundEnv
import com.airbnb.epoxy.processor.ConfigManager.Companion.PROCESSOR_OPTION_DISABLE_GENERATE_BUILDER_OVERLOADS
import com.airbnb.epoxy.processor.ConfigManager.Companion.PROCESSOR_OPTION_DISABLE_GENERATE_GETTERS
import com.airbnb.epoxy.processor.ConfigManager.Companion.PROCESSOR_OPTION_DISABLE_GENERATE_RESET
import com.airbnb.epoxy.processor.ConfigManager.Companion.PROCESSOR_OPTION_DISABLE_KOTLIN_EXTENSION_GENERATION
import com.airbnb.epoxy.processor.ConfigManager.Companion.PROCESSOR_OPTION_IMPLICITLY_ADD_AUTO_MODELS
import com.airbnb.epoxy.processor.ConfigManager.Companion.PROCESSOR_OPTION_LOG_TIMINGS
import com.airbnb.epoxy.processor.ConfigManager.Companion.PROCESSOR_OPTION_REQUIRE_ABSTRACT_MODELS
import com.airbnb.epoxy.processor.ConfigManager.Companion.PROCESSOR_OPTION_REQUIRE_HASHCODE
import com.airbnb.epoxy.processor.ConfigManager.Companion.PROCESSOR_OPTION_VALIDATE_MODEL_USAGE
import com.airbnb.epoxy.processor.resourcescanning.JavacResourceScanner
import com.airbnb.epoxy.processor.resourcescanning.KspResourceScanner
import com.airbnb.epoxy.processor.resourcescanning.ResourceScanner
import com.airbnb.epoxy.processor.resourcescanning.getFieldWithReflectionOrNull
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic
import kotlin.reflect.KClass

abstract class BaseProcessor(val kspEnvironment: SymbolProcessorEnvironment? = null) :
    AbstractProcessor(),
    Asyncable,
    SymbolProcessor {

    val processorName = this@BaseProcessor::class.java.simpleName

    lateinit var environment: XProcessingEnv
        private set

    val messager: XMessager
        get() = environment.messager

    val filer: XFiler
        get() = environment.filer

    private lateinit var options: Map<String, String>

    private var roundNumber = 1
    fun isKsp(): Boolean = kspEnvironment != null

    init {
        if (kspEnvironment != null) {
            options = kspEnvironment.options
            initOptions(kspEnvironment.options)
        }
    }

    val configManager: ConfigManager by lazy {
        ConfigManager(options, environment)
    }
    val resourceProcessor: ResourceScanner by lazy {
        if (kspEnvironment != null) {
            KspResourceScanner(environmentProvider = { environment })
        } else {
            JavacResourceScanner(
                processingEnv = processingEnv,
                environmentProvider = { environment }
            )
        }
    }

    /**
     * Unified place to handle any compiler processor options that are passed to either javac processor or KSP processor,
     * before any rounds are processed.
     */
    open fun initOptions(options: Map<String, String>) {}

    val dataBindingModuleLookup by lazy {
        DataBindingModuleLookup(
            environment,
            logger,
            resourceProcessor
        )
    }

    fun createModelWriter(memoizer: Memoizer): GeneratedModelWriter {
        return GeneratedModelWriter(
            filer,
            environment,
            logger,
            resourceProcessor,
            configManager,
            dataBindingModuleLookup,
            this,
            memoizer
        )
    }

    private val kotlinExtensionWriter: KotlinModelBuilderExtensionWriter by lazy {
        KotlinModelBuilderExtensionWriter(filer, this)
    }

    override val logger by lazy { Logger(messager, configManager.logTimings) }

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
        PROCESSOR_OPTION_DISABLE_GENERATE_RESET,
        PROCESSOR_OPTION_DISABLE_GENERATE_GETTERS,
        PROCESSOR_OPTION_DISABLE_GENERATE_BUILDER_OVERLOADS
    )

    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)

        environment = XProcessingEnv.create(processingEnv)
        options = processingEnv.options
        initOptions(processingEnv.options)
    }

    final override fun process(
        resolver: Resolver
    ): List<KSAnnotated> {
        val roundNumber = roundNumber++
        val timer = Timer("$processorName round $roundNumber")
        timer.start()

        val kspEnvironment = requireNotNull(kspEnvironment)
        environment = XProcessingEnv.create(
            kspEnvironment.options,
            resolver,
            kspEnvironment.codeGenerator,
            kspEnvironment.logger
        )
        return processRoundInternal(
            environment,
            XRoundEnv.create(environment),
            timer,
            roundNumber
        )
            .mapNotNull { xElement ->
                xElement.run {
                    // All xprocessing implementations are internal so we need to use reflection :(
                    // KspElement class uses the "declaration property for its original element.
                    getFieldWithReflectionOrNull<KSAnnotated>("declaration")
                } ?: run {
                    messager.printMessage(
                        Diagnostic.Kind.WARNING,
                        "Unable to get symbol for deferred element $xElement"
                    )
                    null
                }
            }.also {
                if (configManager.logTimings) {
                    timer.finishAndPrint(messager)
                }
            }
    }

    final override fun process(
        annotations: Set<TypeElement?>,
        roundEnv: RoundEnvironment
    ): Boolean {
        val roundNumber = roundNumber++
        val timer = Timer("$processorName round $roundNumber")
        timer.start()

        processRoundInternal(
            environment,
            XRoundEnv.create(environment, roundEnv),
            timer,
            roundNumber
        )

        if (roundEnv.processingOver()) {
            finish()
            timer.markStepCompleted("finish")
        }

        if (configManager.logTimings) {
            timer.finishAndPrint(messager)
        }

        // Let any other annotation processors use our annotations if they want to
        return false
    }

    final override fun finish() {
        // We wait until the very end to log errors so that all the generated classes are still
        // created.
        // Otherwise the compiler error output is clogged with lots of errors from the generated
        // classes  not existing, which makes it hard to see the actual errors.
        logger.writeExceptions()
    }

    private fun processRoundInternal(
        environment: XProcessingEnv,
        round: XRoundEnv,
        timer: Timer,
        roundNumber: Int
    ): List<XElement> {
        // Memoizer should not be used across rounds because KSP symbols are not valid
        // for reuse.
        val memoizer = Memoizer(environment, logger)

        val deferredElements: List<XElement> = try {
            tryOrPrintError<List<XElement>?> {
                timer.markStepCompleted("round initialization")
                processRound(environment, round, memoizer, timer, roundNumber)
            } ?: emptyList()
        } catch (e: Exception) {
            logger.logError(e)
            emptyList()
        }

        // Validate items after, so if any fail we've generated as much of the models
        // as possible to avoid weird errors.
        // Note that we have to be VERY careful referencing symbols across rounds
        // as they types can rely on === checks and instances may not be the same,
        // so behavior may break in strange ways.
        // So we do this check now, instead of waiting for "finish", and then clear
        // the models.
        validateAttributesImplementHashCode(memoizer, generatedModels)
        timer.markStepCompleted("validateAttributesImplementHashCode")

        if (!configManager.disableKotlinExtensionGeneration()) {
            // TODO: Potentially generate a single file per model to allow for an isolating processor
            kotlinExtensionWriter.generateExtensionsForModels(
                generatedModels,
                processorName
            )
            timer.markStepCompleted("generateKotlinExtensions")
        }

        generatedModels.clear()

        return deferredElements
    }

    private inline fun <T> tryOrPrintError(block: () -> T): T? {
        @Suppress("Detekt.TooGenericExceptionCaught")
        return try {
            block()
        } catch (e: Throwable) {
            // Errors thrown from within KSP can get lost, making the root cause of an issue hidden.
            // This helps to surface all thrown errors.
            messager.printMessage(Diagnostic.Kind.ERROR, e.stackTraceToString())
            null
        }
    }

    protected abstract fun processRound(
        environment: XProcessingEnv,
        round: XRoundEnv,
        /**
         * A memoizer to help cache types looked up in this round. Note that KSP must NOT use
         * symbols across rounds, so this memoizer should only be used during this round.
         */
        memoizer: Memoizer,
        timer: Timer,
        roundNumber: Int,
    ): List<XElement>

    private fun validateAttributesImplementHashCode(
        memoizer: Memoizer,
        generatedClasses: Collection<GeneratedModelInfo>
    ) {
        if (generatedClasses.isEmpty()) return

        val hashCodeValidator = HashCodeValidator(environment, memoizer, logger)

        generatedClasses
            .flatMap { it.attributeInfo }
            .mapNotNull { attributeInfo ->
                if (configManager.requiresHashCode(attributeInfo) &&
                    attributeInfo.useInHash &&
                    !attributeInfo.ignoreRequireHashCode
                ) {
                    hashCodeValidator.validate(attributeInfo)
                }
            }
    }
}
