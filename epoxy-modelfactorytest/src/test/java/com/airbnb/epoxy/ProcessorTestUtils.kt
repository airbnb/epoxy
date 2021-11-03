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
import strikt.assertions.doesNotContain
import strikt.assertions.isEmpty
import strikt.assertions.isNotNull
import java.io.File
import javax.annotation.processing.Processor
import javax.tools.JavaFileObject

internal object ProcessorTestUtils {

    fun assertGeneration(
        inputFile: String,
        generatedFile: String,
        useParis: Boolean = false,
        helperObjects: List<JavaFileObject> = emptyList()
    ) {
        val inputFileUrl = inputFile.patchResource()
        val model = JavaFileObjects.forResource(inputFileUrl)

        val generatedFileUrl = generatedFile.patchResource()
        val generatedModel = JavaFileObjects.forResource(generatedFileUrl)

        assert_().about(javaSources())
            .that(helperObjects + listOf(model))
            .withAnnotationProcessorOptions(
                "logEpoxyTimings" to true,
                "disableEpoxyKotlinExtensionGeneration" to true,
            )
            .processedWith(processors(useParis))
            .compilesWithoutError()
            .and()
            .generatesSources(generatedModel)

        val generatedFile = File(generatedFileUrl.file)
        testCodeGeneration(
            sourceFiles = listOf(SourceFile.java(inputFile, inputFileUrl.readText())),
            expectedOutput = listOf(generatedFile),
            useKsp = false,
            useParis = useParis
        )

        // KSP can't capture the original parameter names in java sources so it uses "p0"/"p1"/etc
        // placeholders, which differs from kapt behavior. Due to this we can't directly compare them
        // and instead maintain separate ksp expected sources.
        val generatedKspFile = File(generatedFile.parent, "/ksp/${generatedFile.name}")
        generatedKspFile.unpatchResource().let {
            if (!it.exists()) {
                it.parentFile?.mkdirs()
                it.createNewFile()
            }
        }

        testCodeGeneration(
            sourceFiles = listOf(SourceFile.java(inputFile, inputFileUrl.readText())),
            expectedOutput = listOf(generatedKspFile),
            useKsp = true,
            useParis = useParis
        )
    }

    fun processors(useParis: Boolean = false): MutableList<Processor> {
        return mutableListOf<Processor>().apply {
            add(EpoxyProcessor())
            add(ControllerProcessor())
            add(DataBindingProcessor())
            add(ModelViewProcessor())
            if (useParis) add(ParisProcessor())
        }
    }

    fun processorProviders(): List<SymbolProcessorProvider> {
        return mutableListOf<SymbolProcessorProvider>().apply {
            add(EpoxyProcessorProvider())
            add(ControllerProcessorProvider())
            add(DataBindingProcessorProvider())
            add(ModelViewProcessorProvider())
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
        args: MutableMap<String, String> = mutableMapOf()
    ) {
        println("Using ksp: $useKsp")
        val compilation = KotlinCompilation().apply {
            if (useKsp) {
                symbolProcessorProviders = processorProviders()
                kspArgs = args
            } else {
                annotationProcessors = processors(useParis)
                kaptArgs = args
            }
            sources = sourceFiles
            inheritClassPath = true
            messageOutputStream = System.out
        }
        val result = compilation.compile()

        val generatedSources = if (useKsp) {
            compilation.kspSourcesDir.walk().filter { it.isFile }.toList()
        } else {
            result.sourcesGeneratedByAnnotationProcessor
        }

        if (result.exitCode != KotlinCompilation.ExitCode.OK) {
            println("Generated:")
            generatedSources.forEach { println(it.readText()) }
            error("Compilation failed with ${result.exitCode}.")
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

                            println("Expected source is at: ${expectedOutputFile.unpatchResource()}")
                            val actualFile = File(
                                expectedOutputFile.parent,
                                "actual/${expectedOutputFile.name}"
                            ).apply {
                                parentFile?.mkdirs()
                                writeText(generated.readText())
                            }
                            println("Actual source is at: $actualFile")
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

    // See epoxy-processortest/src/test/java/com/airbnb/epoxy/GuavaPatch.kt
    private fun String.patchResource() =
        File("build/intermediates/sourceFolderJavaResources/debug/$this").toURI().toURL()

    fun File.unpatchResource(): File = File(
        canonicalPath.replace(
            "build/intermediates/sourceFolderJavaResources/debug/",
            "src/test/resources/"
        )
    )

    fun JavaSourcesSubject.withAnnotationProcessorOptions(vararg option: Pair<String, Any>): JavaSourcesSubject {
        return withCompilerOptions(option.map { it.first setTo it.second })
    }

    infix fun String.setTo(value: Any) = "-A$this=$value"
}

enum class CompilationMode(val testKapt: Boolean, val testKSP: Boolean) {
    KSP(testKapt = false, testKSP = true),
    KAPT(testKapt = true, testKSP = false),
    ALL(testKapt = true, testKSP = true)
}

/**
 * Change to true to have tests auto update the expected sources files for easy updating of tests.
 */
const val UPDATE_TEST_SOURCES_ON_DIFF = true
