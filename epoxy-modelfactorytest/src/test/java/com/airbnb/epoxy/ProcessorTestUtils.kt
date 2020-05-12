package com.airbnb.epoxy

import com.airbnb.paris.processor.ParisProcessor
import com.google.common.truth.Truth.assert_
import com.google.testing.compile.JavaFileObjects
import com.google.testing.compile.JavaSourcesSubjectFactory.javaSources
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
        val model = JavaFileObjects
            .forResource(inputFile.patchResource())

        val generatedModel = JavaFileObjects.forResource(generatedFile.patchResource())

        val processors = mutableListOf<Processor>().apply {
            add(EpoxyProcessor())
            if (useParis) add(ParisProcessor())
        }

        assert_().about(javaSources())
            .that(helperObjects + listOf(model))
            .processedWith(processors)
            .compilesWithoutError()
            .and()
            .generatesSources(generatedModel)
    }

    // See epoxy-processortest/src/test/java/com/airbnb/epoxy/GuavaPatch.kt
    private fun String.patchResource() =
        File("build/intermediates/sourceFolderJavaResources/debug/$this").toURI().toURL()
}
