package com.airbnb.epoxy

import com.airbnb.epoxy.processor.ControllerProcessor
import com.airbnb.epoxy.processor.ControllerProcessorProvider
import com.airbnb.epoxy.processor.DataBindingProcessor
import com.airbnb.epoxy.processor.DataBindingProcessorProvider
import com.airbnb.epoxy.processor.EpoxyProcessor
import com.airbnb.epoxy.processor.EpoxyProcessorProvider
import com.airbnb.epoxy.processor.ModelViewProcessor
import com.airbnb.epoxy.processor.ModelViewProcessorProvider
import com.airbnb.paris.processor.ParisProcessor
import com.airbnb.paris.processor.ParisProcessorProvider
import com.github.difflib.DiffUtils
import com.google.common.truth.Truth.assert_
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.testing.compile.JavaFileObjects
import com.google.testing.compile.JavaSourcesSubject
import com.google.testing.compile.JavaSourcesSubjectFactory.javaSources
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.kspArgs
import com.tschuchort.compiletesting.kspSourcesDir
import com.tschuchort.compiletesting.symbolProcessorProviders
import strikt.api.expect
import strikt.api.expectThat
import strikt.assertions.contains
import strikt.assertions.doesNotContain
import strikt.assertions.isEmpty
import strikt.assertions.isNotNull
import java.io.File
import javax.annotation.processing.Processor
import javax.tools.JavaFileObject

internal object ProcessorTestUtils {
    @JvmStatic
    fun assertGenerationError(
        inputFile: String,
        errorMessage: String,
        compilationMode: CompilationMode = CompilationMode.ALL
    ) {
        val model = JavaFileObjects
            .forResource(inputFile.patchResource())

        assertGenerationError(listOf(model), errorMessage, compilationMode)
    }

    fun assertGenerationError(
        sources: List<JavaFileObject>,
        errorMessage: String,
        compilationMode: CompilationMode = CompilationMode.ALL
    ) {
        if (compilationMode.testJavaAP) {
            googleCompileJava(sources)
                .processedWith(processors())
                .failsToCompile()
                .withErrorContaining(errorMessage)
        }

        expectCompilationFailure(
            errorMessage,
            sourceFiles = toKotlinCompilationSourceFiles(sources),
            compilationMode = compilationMode
        )
    }

    @JvmStatic
    fun checkFileCompiles(inputFile: String) {
        val model = JavaFileObjects
            .forResource(inputFile.patchResource())

        googleCompileJava(listOf(model))
            .processedWith(processors())
            .compilesWithoutError()
    }

    @JvmStatic
    @JvmOverloads
    fun processors(useParis: Boolean = false): MutableList<Processor> {
        return mutableListOf<Processor>().apply {
            add(EpoxyProcessor())
            add(ControllerProcessor())
            add(DataBindingProcessor())
            add(ModelViewProcessor())
            if (useParis) add(ParisProcessor())
        }
    }

    fun processorProviders(useParis: Boolean = false): List<SymbolProcessorProvider> {
        return mutableListOf<SymbolProcessorProvider>().apply {
            add(EpoxyProcessorProvider())
            add(ControllerProcessorProvider())
            add(DataBindingProcessorProvider())
            add(ModelViewProcessorProvider())
            if (useParis) add(ParisProcessorProvider())
        }
    }

    @JvmStatic
    fun options(
        withNoValidation: Boolean = false,
        withImplicitAdding: Boolean = false
    ): List<String> {
        return mutableListOf<String>().apply {
            if (withNoValidation) add("validateEpoxyModelUsage" setTo false)
            if (withImplicitAdding) add("implicitlyAddAutoModels" setTo true)
        }
    }

    @JvmOverloads
    @JvmStatic
    fun assertGeneration(
        inputFile: String,
        generatedFile: String,
        useParis: Boolean = false,
        helperObjects: List<JavaFileObject> = emptyList(),
        compilationMode: CompilationMode = CompilationMode.ALL,
        ignoreCompilationError: Boolean = false
    ) {
        assertGeneration(
            sourceFileNames = listOf(inputFile),
            sourceObjects = helperObjects,
            generatedFileNames = listOf(generatedFile),
            useParis = useParis,
            compilationMode = compilationMode,
            ignoreCompilationError = ignoreCompilationError,
        )
    }

    @JvmStatic
    fun assertGeneration(
        inputFiles: List<String>,
        generatedFileNames: List<String>,
        useParis: Boolean = false,
        compilationMode: CompilationMode = CompilationMode.ALL,
        ignoreCompilationError: Boolean = false
    ) {
        assertGeneration(
            sourceFileNames = inputFiles,
            generatedFileNames = generatedFileNames,
            useParis = useParis,
            compilationMode = compilationMode,
            ignoreCompilationError = ignoreCompilationError,
        )
    }

    @JvmName("assertGenerationWithFileObjects")
    fun assertGeneration(
        sourceFileNames: List<String> = emptyList(),
        sourceObjects: List<JavaFileObject> = emptyList(),
        generatedFileNames: List<String> = emptyList(),
        generatedFileObjects: List<JavaFileObject> = emptyList(),
        useParis: Boolean = false,
        compilationMode: CompilationMode = CompilationMode.ALL,
        ignoreCompilationError: Boolean = false
    ) {
        assertGeneration(
            sources = sourceObjects + sourceFileNames.toJavaFileObjects(),
            generatedFileObjects = generatedFileObjects + generatedFileNames.toJavaFileObjects(),
            useParis = useParis,
            compilationMode = compilationMode,
            ignoreCompilationError = ignoreCompilationError,
        )
    }

    @JvmName("assertGenerationWithFileObjects")
    fun assertGeneration(
        sources: List<JavaFileObject>,
        generatedFileObjects: List<JavaFileObject> = emptyList(),
        useParis: Boolean = false,
        compilationMode: CompilationMode = CompilationMode.ALL,
        /**
         * As long as the generated files match properly then ignore any errors during compilation.
         * This can be used to workaround kinks in kotlin compile testing library.
         */
        ignoreCompilationError: Boolean = false
    ) {
        if (compilationMode.testJavaAP) {

            googleCompileJava(sources)
                .processedWith(processors(useParis))
                .compilesWithoutError().apply {
                    if (generatedFileObjects.isNotEmpty()) {
                        and()
                            .generatesSources(
                                generatedFileObjects[0],
                                *generatedFileObjects.drop(1).toTypedArray()
                            )
                    }
                }

            googleCompileJava(sources)
                // Also compile using these flags, since they run different code and could help
                // catch concurrency issues, as well as indeterminate ways that the order of generated
                // code may change due to concurrent processing. Generated code output must be stable
                // to provide stable build cache keys
                .withAnnotationProcessorOptions(
                    "logEpoxyTimings" to true,
                )
                .processedWith(processors(useParis))
                .compilesWithoutError().apply {
                    if (generatedFileObjects.isNotEmpty()) {
                        and()
                            .generatesSources(
                                generatedFileObjects[0],
                                *generatedFileObjects.drop(1).toTypedArray()
                            )
                    }
                }
        }

        // Convert from the java file objects that google compile testing uses to source files
        // that kotlin compile testing can use.
        val generatedFiles = generatedFileObjects.map { generatedFileObject ->
            File(generatedFileObject.toUri()).also {
                check(it.exists()) { "Don't have a file for $generatedFileObject" }
            }
        }

        val sourcesForKotlinCompilation = toKotlinCompilationSourceFiles(sources)

        if (compilationMode.testKapt) {
            testCodeGeneration(
                sourceFiles = sourcesForKotlinCompilation,
                expectedOutput = generatedFiles,
                useKsp = false,
                useParis = useParis,
                ignoreCompilationError = ignoreCompilationError,
            )
        }

        if (compilationMode.testKSP) {

            // KSP can't capture the original parameter names in java sources so it uses "p0"/"p1"/etc
            // placeholders, which differs from kapt behavior. Due to this we can't directly compare them
            // and instead maintain separate ksp expected sources.
            val generatedKspFiles = generatedFiles.map { generatedFile ->
                generatedFile
                File(generatedFile.parent, "/ksp/${generatedFile.name}")
                    .unpatchResource()
                    .also {
                        if (!it.exists()) {
                            it.parentFile?.mkdirs()
                            it.createNewFile()
                        }
                    }
            }

            testCodeGeneration(
                sourceFiles = sourcesForKotlinCompilation,
                expectedOutput = generatedKspFiles,
                useKsp = true,
                useParis = useParis,
                ignoreCompilationError = ignoreCompilationError
            )
        }
    }

    private fun toKotlinCompilationSourceFiles(sources: List<JavaFileObject>): List<SourceFile> {
        return sources.map { javaFileObject ->
            SourceFile.new(
                name = javaFileObject.name.substringAfterLast("/"),
                contents = javaFileObject.openInputStream().bufferedReader().readText()
            )
        }
    }

    /**
     * Test that [sourceFiles] generate [expectedOutput].
     * @param useKsp - If true ksp will be used as the annotation processing backend, if false, kapt will be used.
     *
     * You can set [UPDATE_TEST_SOURCES_ON_DIFF] to true to have the original sources file updated for the actual generated code.
     */
    fun testCodeGeneration(
        sourceFiles: List<SourceFile>,
        expectedOutput: List<File> = emptyList(),
        unexpectedOutputFileName: List<String> = emptyList(),
        useKsp: Boolean = true,
        useParis: Boolean = false,
        args: MutableMap<String, String> = mutableMapOf(),
        ignoreCompilationError: Boolean = false
    ) {
        println("Using ksp: $useKsp")
        val compilation = getCompilation(useKsp, args, sourceFiles, useParis)
        val result = compilation.compile()

        val generatedSources = if (useKsp) {
            compilation.kspSourcesDir.walk().filter { it.isFile }.toList()
        } else {
            result.sourcesGeneratedByAnnotationProcessor
        }

        if (result.exitCode != KotlinCompilation.ExitCode.OK) {
            println("Generated:")
            generatedSources.forEach { println(it.readText()) }
            if (!ignoreCompilationError) {
                error("Compilation failed with ${result.exitCode}.")
            }
        }

        println("Generated files:")
        generatedSources.forEach { println(it.name) }

        expect {
            expectedOutput.forEach { expectedOutputFile ->
                val actualOutputFileName = expectedOutputFile.name
                // Since we may encode output files as txt resources, we need to remove the suffix when comparing
                // generated filename to expected filename.
                val expectedOutputFilename = actualOutputFileName.removeSuffix(".txt")
                val generated = generatedSources.find { it.name == expectedOutputFilename }
                that(generated) {
                    isNotNull().and {
                        val patch =
                            DiffUtils.diff(generated!!.readLines(), expectedOutputFile.readLines())
                        if (patch.deltas.isNotEmpty()) {
                            println("Found differences for $expectedOutputFilename!")
                            println("Actual filename in filesystem is $actualOutputFileName")
                            println("Expected:\n")
                            println(expectedOutputFile.readText())
                            println("Generated:\n")
                            println(generated.readText())

                            if (UPDATE_TEST_SOURCES_ON_DIFF) {
                                println("UPDATE_TEST_SOURCES_ON_DIFF is enabled; updating expected sources with actual sources.")
                                expectedOutputFile.unpatchResource().apply {
                                    parentFile?.mkdirs()
                                    writeText(generated.readText())
                                }
                            }
                        }
                        that(patch.deltas).isEmpty()
                    }
                }.describedAs(expectedOutputFilename)
            }
        }
        val generatedFileNames = generatedSources.map { it.name }
        if (unexpectedOutputFileName.isNotEmpty()) {
            expectThat(generatedFileNames).doesNotContain(unexpectedOutputFileName)
        }
    }

    /**
     * Allows writing compilation test name with backticks that specifies a resource folder. If there folder is nested `/` should be
     * encoded as ` `. Compilation is expceted to fail with [failureMessage].
     */
    fun expectCompilationFailure(
        failureMessage: String,
        sourceFiles: List<SourceFile>,
        args: MutableMap<String, String> = mutableMapOf(),
        compilationMode: CompilationMode = CompilationMode.ALL
    ) {
        fun testCodeGenerationFailure(useKsp: Boolean) {
            val compilation = getCompilation(useKsp, args, sourceFiles)

            val result = compilation.compile()

            if (result.exitCode == KotlinCompilation.ExitCode.OK) {
                error("Compilation succeed.")
            }
            expectThat(result.messages).contains(failureMessage)
        }

        if (compilationMode.testKSP) {
            testCodeGenerationFailure(useKsp = true)
        }
        if (compilationMode.testKapt) {
            testCodeGenerationFailure(useKsp = false)
        }
    }

    private fun getCompilation(
        useKsp: Boolean,
        args: MutableMap<String, String>,
        sourceFiles: List<SourceFile>,
        useParis: Boolean = false
    ): KotlinCompilation {
        return KotlinCompilation().apply {
            if (useKsp) {
                symbolProcessorProviders = processorProviders(useParis)
                kspArgs = args
            } else {
                annotationProcessors = processors(useParis)
                kaptArgs = args
            }
            sources = sourceFiles
            inheritClassPath = true
            messageOutputStream = System.out
        }
    }
}

infix fun String.setTo(value: Any) = "-A$this=$value"

fun JavaSourcesSubject.withAnnotationProcessorOptions(vararg option: Pair<String, Any>): JavaSourcesSubject {
    return withCompilerOptions(option.map { it.first setTo it.second })
}

fun googleCompileJava(sources: List<JavaFileObject>): JavaSourcesSubject {
    return assert_().about(javaSources())
        .that(sources)
        .withAnnotationProcessorOptions(
            // java ap cannot generate kotlin sources
            "disableEpoxyKotlinExtensionGeneration" to true,
        )
}

fun List<String>.toJavaFileObjects() = map { JavaFileObjects.forResource(it.patchResource()) }

fun javaFile(name: String) = JavaFileObjects.forResource(name.patchResource())

enum class CompilationMode(
    val testKapt: Boolean = false,
    val testKSP: Boolean = false,
    val testJavaAP: Boolean = false
) {
    KSP(testKSP = true),
    JavaAP(testJavaAP = true),
    KAPT(testKapt = true),
    ALL(testKSP = true, testKapt = true, testJavaAP = true)
}

/**
 * Change to true to have tests auto update the expected sources files for easy updating of tests.
 */
const val UPDATE_TEST_SOURCES_ON_DIFF = true
