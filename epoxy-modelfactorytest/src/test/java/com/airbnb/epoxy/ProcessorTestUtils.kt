package com.airbnb.epoxy

import com.airbnb.epoxy.processor.ControllerProcessor
import com.airbnb.epoxy.processor.DataBindingProcessor
import com.airbnb.epoxy.processor.EpoxyProcessor
import com.airbnb.epoxy.processor.ModelViewProcessor
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

        assert_().about(javaSources())
            .that(helperObjects + listOf(model))
            .processedWith(processors(useParis))
            .compilesWithoutError()
            .and()
            .generatesSources(generatedModel)
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

    // See epoxy-processortest/src/test/java/com/airbnb/epoxy/GuavaPatch.kt
    private fun String.patchResource() =
        File("build/intermediates/sourceFolderJavaResources/debug/$this").toURI().toURL()
}
