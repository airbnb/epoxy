package com.airbnb.epoxy

import com.airbnb.paris.processor.ParisProcessor
import com.google.common.truth.Truth.assert_
import com.google.testing.compile.JavaFileObjects
import com.google.testing.compile.JavaSourcesSubjectFactory.javaSources
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
            .forResource(inputFile)

        val generatedModel = JavaFileObjects.forResource(generatedFile)

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
}
