package com.airbnb.epoxy

import com.google.common.truth.Truth.assert_
import com.google.testing.compile.JavaFileObjects
import com.google.testing.compile.JavaSourceSubjectFactory
import com.google.testing.compile.JavaSourceSubjectFactory.javaSource
import com.google.testing.compile.JavaSourcesSubject
import com.google.testing.compile.JavaSourcesSubjectFactory
import com.google.testing.compile.JavaSourcesSubjectFactory.javaSources
import java.util.*
import javax.annotation.processing.Processor
import javax.tools.JavaFileObject

internal object ProcessorTestUtils {
    @JvmStatic
    fun assertGenerationError(
        inputFile: String,
        errorMessage: String
    ) {
        val model = JavaFileObjects
            .forResource(inputFile)

        assert_().about<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject, JavaSourceSubjectFactory>(
            javaSource()
        )
            .that(model)
            .processedWith(EpoxyProcessor())
            .failsToCompile()
            .withErrorContaining(errorMessage)
    }

    @JvmStatic
    fun checkFileCompiles(inputFile: String) {
        val model = JavaFileObjects
            .forResource(inputFile)

        assert_().about<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject, JavaSourceSubjectFactory>(
            javaSource()
        )
            .that(model)
            .processedWith(EpoxyProcessor())
            .compilesWithoutError()
    }

    @JvmOverloads
    @JvmStatic
    fun assertGeneration(
        inputFile: String,
        generatedFile: String,
        useParis: Boolean = false
    ) {
        val model = JavaFileObjects
            .forResource(inputFile)

        val generatedModel = JavaFileObjects.forResource(generatedFile)

        val processors = mutableListOf<Processor>().apply {
            add(EpoxyProcessor())
//            if (useParis) add(ParisPro)
        }


        assert_().about<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject, JavaSourceSubjectFactory>(
            javaSource()
        )
            .that(model)
            .processedWith(processors)
            .compilesWithoutError()
            .and()
            .generatesSources(generatedModel)
    }

    @JvmStatic
    fun assertGeneration(
        inputFiles: List<String>,
        fileNames: List<String>
    ) {
        val sources = ArrayList<JavaFileObject>()

        for (inputFile in inputFiles) {
            sources.add(
                JavaFileObjects
                    .forResource(inputFile)
            )
        }

        val generatedFiles = ArrayList<JavaFileObject>()
        for (i in fileNames.indices) {
            generatedFiles.add(JavaFileObjects.forResource(fileNames[i]))
        }

        assert_().about<JavaSourcesSubject, Iterable<JavaFileObject>, JavaSourcesSubjectFactory>(
            javaSources()
        )
            .that(sources)
            .processedWith(EpoxyProcessor())
            .compilesWithoutError()
            .and()
            .generatesSources(
                generatedFiles[0],
                *generatedFiles.subList(1, generatedFiles.size)
                    .toTypedArray()
            )
    }
}
