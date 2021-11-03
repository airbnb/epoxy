package com.airbnb.epoxy

import com.airbnb.epoxy.ProcessorTestUtils.assertGeneration
import com.airbnb.epoxy.ProcessorTestUtils.assertGenerationError
import com.airbnb.epoxy.ProcessorTestUtils.checkFileCompiles
import com.google.testing.compile.JavaFileObjects
import junit.framework.Assert
import org.junit.Test

/**
 * These processor tests are in their own module since the processor module can't depend on the
 * android EpoxyAdapter library that contains the EpoxyModel.
 */
class ModelProcessorTest {
    @Test
    fun testSimpleModel() {
        assertGeneration("BasicModelWithAttribute.java", "BasicModelWithAttribute_.java")
    }

    @Test
    fun testModelWithAllFieldTypes() {
        assertGeneration("ModelWithAllFieldTypes.java", "ModelWithAllFieldTypes_.java")
    }

    @Test
    fun testModelWithConstructors() {
        assertGeneration("ModelWithConstructors.java", "ModelWithConstructors_.java")
    }

    @Test
    fun testModelWithSuper() {
        assertGeneration("ModelWithSuper.java", "ModelWithSuper_.java")
    }

    @Test
    fun testModelWithFieldAnnotation() {
        assertGeneration("ModelWithFieldAnnotation.java", "ModelWithFieldAnnotation_.java")
    }

    @Test
    fun testModelWithSuperClass() {
        val model = JavaFileObjects.forResource("ModelWithSuperAttributes.java".patchResource())

        val generatedModel =
            JavaFileObjects.forResource("ModelWithSuperAttributes_.java".patchResource())

        val generatedSubClassModel =
            JavaFileObjects.forResource("ModelWithSuperAttributes\$SubModelWithSuperAttributes_.java".patchResource())

        assertGeneration(
            sources = listOf(model),
            generatedFileObjects = listOf(generatedModel, generatedSubClassModel)
        )
    }

    @Test
    fun testModelWithType() {
        assertGeneration(
            "ModelWithType.java",
            "ModelWithType_.java",
            // The generics in the model don't seem to work with KSP/Kotlin. It isn't a real/good
            // use case anyway, so don't care about supporting it.
            compilationMode = CompilationMode.JavaAP
        )
    }

    @Test
    fun testModelWithoutHash() {
        assertGeneration("ModelWithoutHash.java", "ModelWithoutHash_.java")
    }

    @Test
    fun testDoNotHashModel() {
        assertGeneration("ModelDoNotHash.java", "ModelDoNotHash_.java")
    }

    @Test
    fun testModelWithFinalAttribute() {
        assertGeneration("ModelWithFinalField.java", "ModelWithFinalField_.java")
    }

    @Test
    fun testModelWithStaticAttributeFails() {
        assertGenerationError("ModelWithStaticField.java", "static")
    }

    @Test
    fun testModelWithPrivateClassFails() {
        assertGenerationError(
            "ModelWithPrivateInnerClass.java",
            "private classes",
            // KSP bug prevents us from testing this case https://github.com/google/ksp/issues/622
            compilationMode = CompilationMode.JavaAP
        )
    }

    @Test
    fun testModelWithFinalClassFails() {
        assertGenerationError("ModelWithFinalClass.java", "")
    }

    @Test
    fun testModelThatDoesNotExtendEpoxyModelFails() {
        assertGenerationError("ModelWithoutEpoxyExtension.java", "must extend")
    }

    @Test
    fun testModelAsInnerClassFails() {
        assertGenerationError("ModelAsInnerClass.java", "Nested model classes must be static")
    }

    @Test
    fun testModelWithIntDefAnnotation() {
        assertGeneration(
            "ModelWithIntDef.java",
            "ModelWithIntDef_.java",
            // Throws NPE in KSP due to https://github.com/google/ksp/issues/622
            compilationMode = CompilationMode.JavaAP
        )
    }

    @Test
    fun testModelWithAnnotatedClass() {
        assertGeneration("ModelWithAnnotatedClass.java", "ModelWithAnnotatedClass_.java")
    }

    @Test
    fun testModelWithAbstractClass() {
        val model = JavaFileObjects
            .forResource("ModelWithAbstractClass.java".patchResource())

        assertGeneration(
            sources = listOf(model)
        )

        // We don't generate subclasses if the model is abstract unless it has a class annotation.
        val modelNotGenerated: Boolean = try {
            JavaFileObjects.forResource("non-sense? Any wrong path will generate IAE")
            false
        } catch (e: IllegalArgumentException) {
            true
        }

        Assert.assertTrue(modelNotGenerated)
    }

    @Test
    fun testModelWithAbstractClassAndAnnotation() {
        assertGeneration(
            "ModelWithAbstractClassAndAnnotation.java",
            "ModelWithAbstractClassAndAnnotation_.java"
        )
    }

    @Test
    fun testKotlinModel_kapt() {
        assertGeneration(
            "ModelProcessorTest/testKotlinModel/Model.kt",
            "ModelProcessorTest/testKotlinModel/Model_.java",
            compilationMode = CompilationMode.KAPT
        )
    }

    @Test
    fun testKotlinModel_ksp() {
        assertGeneration(
            "ModelProcessorTest/testKotlinModel/Model.kt",
            "ModelProcessorTest/testKotlinModel/Model_.java",
            compilationMode = CompilationMode.KSP
        )
    }

    @Test
    fun testModelWithAnnotatedClassAndSuperClass() {
        val model = JavaFileObjects
            .forResource("ModelWithAnnotatedClassAndSuperAttributes.java".patchResource())

        val generatedModel = JavaFileObjects
            .forResource("ModelWithAnnotatedClassAndSuperAttributes_.java".patchResource())

        val generatedSubClassModel = JavaFileObjects.forResource(
            (
                "ModelWithAnnotatedClassAndSuperAttributes\$SubModel" +
                    "WithAnnotatedClassAndSuperAttributes_.java"
                ).patchResource()
        )
        assertGeneration(
            sources = listOf(model),
            generatedFileObjects = listOf(generatedModel, generatedSubClassModel),
            // Throws NPE in KSP due to https://github.com/google/ksp/issues/622
            compilationMode = CompilationMode.JavaAP
        )
    }

    @Test
    fun testModelWithoutSetter() {
        assertGeneration("ModelWithoutSetter.java", "ModelWithoutSetter_.java")
    }

    @Test
    fun testModelReturningClassType() {
        assertGeneration("ModelReturningClassType.java", "ModelReturningClassType_.java")
    }

    @Test
    fun testModelReturningClassTypeWithVarargs() {
        assertGeneration(
            "ModelReturningClassTypeWithVarargs.java",
            "ModelReturningClassTypeWithVarargs_.java"
        )
    }

    @Test
    fun testModelWithVarargsConstructors() {
        assertGeneration(
            "ModelWithVarargsConstructors.java",
            "ModelWithVarargsConstructors_.java",
            // The kotlin extension generation doesn't actually work with vararg constructors.
            // it's a bug we're not trying to support, so just make sure this works with the originally
            // intended legacy java.
            compilationMode = CompilationMode.JavaAP
        )
    }

    @Test
    fun testModelWithHolderGeneratesNewHolderMethod() {
        assertGeneration(
            "AbstractModelWithHolder.java",
            "AbstractModelWithHolder_.java",
            // The nested static holder class results in KSP seeing it as an "Error" type during processing
            // for some reason (seems like a bug?). There doesn't seem to be a way to defer the
            // symbol the way we access it via a type param does not give the underlying KSAnnotated
            // element. The separate test with kotlin sources works fine, so workaround is to write
            // the holder classes in kotlin if needed.
            compilationMode = CompilationMode.JavaAP
        )
    }

    @Test
    fun testModelWithHolderGeneratesNewHolderMethod_kotlin() {
        assertGeneration(
            "testModelWithHolderGeneratesNewHolderMethod/AbstractModelWithHolder.kt",
            "testModelWithHolderGeneratesNewHolderMethod/AbstractModelWithHolder_.java",
            compilationMode = CompilationMode.KSP
        )
    }

    @Test
    fun testGenerateDefaultLayoutMethod() {
        assertGeneration("GenerateDefaultLayoutMethod.java", "GenerateDefaultLayoutMethod_.java")
    }

    @Test
    fun testGenerateDefaultLayoutMethodFailsIfLayoutNotSpecified() {
        assertGenerationError(
            "GenerateDefaultLayoutMethodNoLayout.java",
            "Model must specify a valid layout resource"
        )
    }

    @Test
    fun testGeneratedDefaultMethodWithLayoutSpecifiedInParent() {
        val model = JavaFileObjects
            .forResource("GenerateDefaultLayoutMethodParentLayout.java".patchResource())

        val generatedNoLayoutModel = JavaFileObjects
            .forResource("GenerateDefaultLayoutMethodParentLayout\$NoLayout_.java".patchResource())

        val generatedWithLayoutModel =
            JavaFileObjects.forResource("GenerateDefaultLayoutMethodParentLayout\$WithLayout_.java".patchResource())

        assertGeneration(
            sources = listOf(model),
            generatedFileObjects = listOf(generatedNoLayoutModel, generatedWithLayoutModel)
        )
    }

    @Test
    fun testGeneratedDefaultMethodWithLayoutSpecifiedInNextParent() {
        val model = JavaFileObjects
            .forResource("GenerateDefaultLayoutMethodNextParentLayout.java".patchResource())

        val generatedNoLayoutModel = JavaFileObjects
            .forResource("GenerateDefaultLayoutMethodNextParentLayout\$NoLayout_.java".patchResource())

        val generatedStillNoLayoutModel = JavaFileObjects
            .forResource("GenerateDefaultLayoutMethodNextParentLayout\$StillNoLayout_.java".patchResource())

        val generatedWithLayoutModel =
            JavaFileObjects.forResource("GenerateDefaultLayoutMethodNextParentLayout\$WithLayout_.java".patchResource())

        assertGeneration(
            sources = listOf(model),
            generatedFileObjects = listOf(
                generatedNoLayoutModel,
                generatedStillNoLayoutModel,
                generatedWithLayoutModel
            )
        )
    }

    @Test
    fun testGeneratedDefaultMethodWithLayoutFailsIfNotSpecifiedInParent() {
        assertGenerationError(
            "GenerateDefaultLayoutMethodParentStillNoLayout.java",
            "Model must specify a valid layout resource"
        )
    }

    @Test
    fun modelWithViewClickListener() {
        assertGeneration("ModelWithViewClickListener.java", "ModelWithViewClickListener_.java")
    }

    @Test
    fun modelWithViewClickLongListener() {
        val model = JavaFileObjects
            .forResource("ModelWithViewLongClickListener.java".patchResource())

        val generatedNoLayoutModel = JavaFileObjects
            .forResource("ModelWithViewLongClickListener_.java".patchResource())

        assertGeneration(
            sources = listOf(model),
            generatedFileObjects = listOf(generatedNoLayoutModel)
        )
    }

    @Test
    fun modelWithCheckedChangeListener() {
        assertGeneration(
            "ModelWithCheckedChangeListener.java",
            "ModelWithCheckedChangeListener_.java"
        )
    }

    @Test
    fun testModelWithPrivateAttributeWithoutGetterAndSetterFails() {
        assertGenerationError(
            "ModelWithPrivateFieldWithoutGetterAndSetter.java",
            "private fields without proper getter and setter"
        )
    }

    @Test
    fun testModelWithPrivateAttributeWithoutSetterFails() {
        assertGenerationError(
            "ModelWithPrivateFieldWithoutSetter.java",
            "private fields without proper getter and setter"
        )
    }

    @Test
    fun testModelWithPrivateAttributeWithoutGetterFails() {
        assertGenerationError(
            "ModelWithPrivateFieldWithoutGetter.java",
            "private fields without proper getter and setter"
        )
    }

    @Test
    fun testModelWithPrivateAttributeWithIsPrefixGetter() {
        checkFileCompiles("ModelWithPrivateFieldWithIsPrefixGetter.java")
    }

    @Test
    fun testModelWithPrivateAttributeWithPrivateGetterFails() {
        assertGenerationError(
            "ModelWithPrivateFieldWithPrivateGetter.java",
            "private fields without proper getter and setter"
        )
    }

    @Test
    fun testModelWithPrivateAttributeWithStaticGetterFails() {
        assertGenerationError(
            "ModelWithPrivateFieldWithStaticGetter.java",
            "private fields without proper getter and setter"
        )
    }

    @Test
    fun testModelWithPrivateAttributeWithGetterWithParamsFails() {
        assertGenerationError(
            "ModelWithPrivateFieldWithGetterWithParams.java",
            "private fields without proper getter and setter"
        )
    }

    @Test
    fun testModelWithPrivateAttributeWithPrivateSetterFails() {
        assertGenerationError(
            "ModelWithPrivateFieldWithPrivateSetter.java",
            "private fields without proper getter and setter"
        )
    }

    @Test
    fun testModelWithPrivateAttributeWithStaticSetterFails() {
        assertGenerationError(
            "ModelWithPrivateFieldWithStaticSetter.java",
            "private fields without proper getter and setter"
        )
    }

    @Test
    fun testModelWithPrivateAttributeWithGetterWithoutParamsFails() {
        assertGenerationError(
            "ModelWithPrivateFieldWithSettterWithoutParams.java",
            "private fields without proper getter and setter"
        )
    }

    @Test
    fun testModelWithAllPrivateFieldTypes() {
        assertGeneration(
            "ModelWithAllPrivateFieldTypes.java",
            "ModelWithAllPrivateFieldTypes_.java"
        )
    }

    @Test
    fun modelWithViewPrivateClickListener() {
        assertGeneration(
            "ModelWithPrivateViewClickListener.java",
            "ModelWithPrivateViewClickListener_.java"
        )
    }

    @Test
    fun modelWithPrivateFieldWithSameAsFieldGetterAndSetterName() {
        assertGeneration(
            "ModelWithPrivateFieldWithSameAsFieldGetterAndSetterName.java",
            "ModelWithPrivateFieldWithSameAsFieldGetterAndSetterName_.java"
        )
    }

    @Test
    fun testDoNotUseInToStringModel() {
        assertGeneration("ModelDoNotUseInToString.java", "ModelDoNotUseInToString_.java")
    }

    @Test
    fun modelWithAnnotation() {
        assertGeneration("ModelWithAnnotation.java", "ModelWithAnnotation_.java")
    }

    @Test
    fun testModelBuilderInterface() {
        assertGeneration("ModelWithAllFieldTypes.java", "ModelWithAllFieldTypesBuilder.java")
    }

    @Test
    fun generatedEpoxyModelGroup() {
        assertGeneration(
            "EpoxyModelGroupWithAnnotations.java",
            "EpoxyModelGroupWithAnnotations_.java",
        )
    }

    @Test
    fun generatedEpoxyModelWithView() {
        assertGeneration(
            "AbstractEpoxyModelWithView.java",
            "AbstractEpoxyModelWithView_.java"
        )
    }
}
