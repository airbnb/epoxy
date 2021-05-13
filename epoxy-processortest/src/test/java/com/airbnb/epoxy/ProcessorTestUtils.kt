package com.airbnb.epoxy

import com.airbnb.epoxy.processor.ControllerProcessor
import com.airbnb.epoxy.processor.DataBindingProcessor
import com.airbnb.epoxy.processor.EpoxyProcessor
import com.airbnb.epoxy.processor.ModelViewProcessor
import com.airbnb.paris.processor.ParisProcessor
import com.google.common.truth.Truth.assert_
import com.google.testing.compile.JavaFileObjects
import com.google.testing.compile.JavaSourceSubjectFactory.javaSource
import com.google.testing.compile.JavaSourcesSubject
import com.google.testing.compile.JavaSourcesSubjectFactory.javaSources
import javax.annotation.processing.Processor
import javax.tools.JavaFileObject

internal object ProcessorTestUtils {
    @JvmStatic
    fun assertGenerationError(
        inputFile: String,
        errorMessage: String
    ) {
        val model = JavaFileObjects
            .forResource(inputFile.patchResource())

        assert_().about(javaSource())
            .that(model)
            .processedWith(processors())
            .failsToCompile()
            .withErrorContaining(errorMessage)
    }

    @JvmStatic
    fun checkFileCompiles(inputFile: String) {
        val model = JavaFileObjects
            .forResource(inputFile.patchResource())

        assert_().about(javaSource())
            .that(model)
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
        helperObjects: List<JavaFileObject> = emptyList()
    ) {
        assertGeneration(
            sourceFileNames = listOf(inputFile),
            sourceObjects = helperObjects,
            generatedFileNames = listOf(generatedFile),
            useParis = useParis
        )
    }

    @JvmStatic
    fun assertGeneration(
        inputFiles: List<String>,
        fileNames: List<String>,
        useParis: Boolean = false
    ) {
        assertGeneration(
            sourceFileNames = inputFiles,
            generatedFileNames = fileNames,
            useParis = useParis
        )
    }

    @JvmName("assertGenerationWithFileObjects")
    fun assertGeneration(
        sourceFileNames: List<String> = emptyList(),
        sourceObjects: List<JavaFileObject> = emptyList(),
        generatedFileNames: List<String> = emptyList(),
        generatedFileObjects: List<JavaFileObject> = emptyList(),
        useParis: Boolean = false
    ) {
        val generatedFiles = generatedFileObjects + generatedFileNames.toJavaFileObjects()

        assert_().about(javaSources())
            .that(sourceObjects + sourceFileNames.toJavaFileObjects())
            .processedWith(processors(useParis))
            .compilesWithoutError()
            .and()
            .generatesSources(
                generatedFiles[0],
                *generatedFiles.drop(1).toTypedArray()
            )

        assert_().about(javaSources())
            .that(sourceObjects + sourceFileNames.toJavaFileObjects())
            // Also compile using these flags, since they run different code and could help
            // catch concurrency issues, as well as indeterminate ways that the order of generated
            // code may change due to concurrent processing. Generated code output must be stable
            // to provide stable build cache keys
            .withAnnotationProcessorOptions(
                // parallel testing seems flaky since java 11 with the agp 4.2 update.
                // Disabling until it can be looked into.
//                "enableParallelEpoxyProcessing" to true,
                "logEpoxyTimings" to true
            )
            .processedWith(processors(useParis))
            .compilesWithoutError()
            .and()
            .generatesSources(
                generatedFiles[0],
                *generatedFiles.drop(1).toTypedArray()
            )
    }
}

infix fun String.setTo(value: Any) = "-A$this=$value"

fun JavaSourcesSubject.withAnnotationProcessorOptions(vararg option: Pair<String, Any>): JavaSourcesSubject {
    return withCompilerOptions(option.map { it.first setTo it.second })
}

fun List<String>.toJavaFileObjects() = map { JavaFileObjects.forResource(it.patchResource()) }

fun javaFile(name: String) = JavaFileObjects.forResource(name.patchResource())
