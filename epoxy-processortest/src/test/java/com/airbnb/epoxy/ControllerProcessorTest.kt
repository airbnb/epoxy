package com.airbnb.epoxy

import com.airbnb.epoxy.ProcessorTestUtils.assertGeneration
import com.airbnb.epoxy.ProcessorTestUtils.assertGenerationError
import com.airbnb.epoxy.ProcessorTestUtils.options
import com.airbnb.epoxy.ProcessorTestUtils.processors
import com.google.testing.compile.JavaFileObjects
import org.junit.Test

class ControllerProcessorTest {

    @Test
    fun controllerWithAutoModel() {
        val model = JavaFileObjects
            .forResource("BasicModelWithAttribute.java".patchResource())
        val controller = JavaFileObjects
            .forResource("ControllerWithAutoModel.java".patchResource())
        val generatedHelper = JavaFileObjects
            .forResource("ControllerWithAutoModel_EpoxyHelper.java".patchResource())

        assertGeneration(
            sources = listOf(model, controller),
            generatedFileObjects = listOf(generatedHelper),
            // Kotlin compile testing does not like that the source file has an unknown type initially
            // since its generated and logs an error, but the processing and generation completes fine, so we just
            // ignore that error.
            ignoreCompilationError = true,
        )
    }

    @Test
    fun controllerWithAutoModel_kotlin() {
        val model = JavaFileObjects
            .forResource("ControllerProcessorTest/controllerWithAutoModel/BasicModelWithAttribute.kt".patchResource())
        val controller = JavaFileObjects
            .forResource("ControllerProcessorTest/controllerWithAutoModel/ControllerWithAutoModel.kt".patchResource())
        val generatedHelper = JavaFileObjects
            .forResource("ControllerProcessorTest/controllerWithAutoModel/ControllerWithAutoModel_EpoxyHelper.java".patchResource())

        assertGeneration(
            sources = listOf(model, controller),
            generatedFileObjects = listOf(generatedHelper),
            compilationMode = CompilationMode.KSP,
        )
    }

    @Test
    fun controllerWithAutoModelWithoutValidation() {
        val model = JavaFileObjects
            .forResource("BasicModelWithAttribute.java".patchResource())
        val controller = JavaFileObjects
            .forResource("ControllerWithAutoModelWithoutValidation.java".patchResource())
        val generatedHelper = JavaFileObjects
            .forResource("ControllerWithAutoModelWithoutValidation_EpoxyHelper.java".patchResource())

        googleCompileJava(listOf(model, controller))
            .withCompilerOptions(options(true, false))
            .processedWith(processors())
            .compilesWithoutError()
            .and()
            .generatesSources(generatedHelper)
    }

    @Test
    fun controllerWithSuperClassWithAutoModel() {
        val model = JavaFileObjects
            .forResource("BasicModelWithAttribute.java".patchResource())
        val controller = JavaFileObjects
            .forResource("ControllerWithAutoModelWithSuperClass.java".patchResource())
        val generatedHelper = JavaFileObjects
            .forResource("ControllerWithAutoModelWithSuperClass_EpoxyHelper.java".patchResource())
        val generatedSubHelper = JavaFileObjects
            .forResource("ControllerWithAutoModelWithSuperClass\$SubControllerWithAutoModelWithSuperClass_EpoxyHelper.java".patchResource())

        assertGeneration(
            sources = listOf(model, controller),
            generatedFileObjects = listOf(generatedHelper, generatedSubHelper),
            // Kotlin compile testing does not like that the source file has an unknown type initially
            // since its generated and logs an error, but the processing and generation completes fine, so we just
            // ignore that error.
            ignoreCompilationError = true,
        )
    }

    @Test
    fun autoModelNotInAutoAdapterFails() {
        val badClass = JavaFileObjects
            .forResource("AutoModelNotInAutoAdapter.java".patchResource())

        assertGenerationError(
            sources = listOf(badClass),
            errorMessage = ""
        )
    }

    @Test
    fun autoModelAnnotationNotOnModelFails() {
        val badClass = JavaFileObjects
            .forResource("AutoModelNotOnModelField.java".patchResource())

        assertGenerationError(
            sources = listOf(badClass),
            errorMessage = ""
        )
    }

    @Test
    fun setsStagingControllerWhenImplicitlyAddingModels() {
        val model = JavaFileObjects
            .forResource("BasicModelWithAttribute.java".patchResource())
        val controller = JavaFileObjects
            .forResource("ControllerWithAutoModelAndImplicitAdding.java".patchResource())
        val generatedHelper = JavaFileObjects
            .forResource("ControllerWithAutoModelAndImplicitAdding_EpoxyHelper.java".patchResource())

        googleCompileJava(listOf(model, controller))
            .withCompilerOptions(options(false, true))
            .processedWith(processors())
            .compilesWithoutError()
            .and()
            .generatesSources(generatedHelper)
    }
}
