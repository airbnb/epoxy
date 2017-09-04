package com.airbnb.epoxy;

import com.google.testing.compile.JavaFileObjects;

import org.junit.Test;

import javax.tools.JavaFileObject;

import static com.airbnb.epoxy.ProcessorTestUtils.assertGenerationError;
import static com.airbnb.epoxy.ProcessorTestUtils.checkFileCompiles;
import static com.airbnb.epoxy.ProcessorTestUtils.assertGeneration;
import static com.google.common.truth.Truth.assert_;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;
import static junit.framework.Assert.assertTrue;

/**
 * These processor tests are in their own module since the processor module can't depend on the
 * android EpoxyAdapter library that contains the EpoxyModel.
 */

@SuppressWarnings("ResultOfMethodCallIgnored")
public class ModelProcessorTest {
  @Test
  public void testSimpleModel() {
    assertGeneration("BasicModelWithAttribute.java", "BasicModelWithAttribute_.java");
  }

  @Test
  public void testModelWithAllFieldTypes() {
    assertGeneration("ModelWithAllFieldTypes.java", "ModelWithAllFieldTypes_.java");
  }

  @Test
  public void testModelWithConstructors() {
    assertGeneration("ModelWithConstructors.java", "ModelWithConstructors_.java");
  }

  @Test
  public void testModelWithSuper() {
    assertGeneration("ModelWithSuper.java", "ModelWithSuper_.java");
  }

  @Test
  public void testModelWithFieldAnnotation() {
    assertGeneration("ModelWithFieldAnnotation.java", "ModelWithFieldAnnotation_.java");
  }

  @Test
  public void testModelWithSuperClass() {
    JavaFileObject model = JavaFileObjects
        .forResource("ModelWithSuperAttributes.java");

    JavaFileObject generatedModel = JavaFileObjects.forResource("ModelWithSuperAttributes_.java");
    JavaFileObject generatedSubClassModel =
        JavaFileObjects.forResource("ModelWithSuperAttributes$SubModelWithSuperAttributes_.java");

    assert_().about(javaSource())
        .that(model)
        .processedWith(new EpoxyProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(generatedModel, generatedSubClassModel);
  }

  @Test
  public void testModelWithType() {
    assertGeneration("ModelWithType.java", "ModelWithType_.java");
  }

  @Test
  public void testModelWithoutHash() {
    assertGeneration("ModelWithoutHash.java", "ModelWithoutHash_.java");
  }

  @Test
  public void testDoNotHashModel() {
    assertGeneration("ModelDoNotHash.java", "ModelDoNotHash_.java");
  }

  @Test
  public void testModelWithFinalAttribute() {
    assertGeneration("ModelWithFinalField.java", "ModelWithFinalField_.java");
  }

  @Test
  public void testModelWithStaticAttributeFails() {
    assertGenerationError("ModelWithStaticField.java", "static");
  }

  @Test
  public void testModelWithPrivateClassFails() {
    assertGenerationError("ModelWithPrivateInnerClass.java", "private classes");
  }

  @Test
  public void testModelWithFinalClassFails() {
    assertGenerationError("ModelWithFinalClass.java", "");
  }

  @Test
  public void testModelThatDoesNotExtendEpoxyModelFails() {
    assertGenerationError("ModelWithoutEpoxyExtension.java", "must extend");
  }

  @Test
  public void testModelAsInnerClassFails() {
    assertGenerationError("ModelAsInnerClass.java", "Nested classes");
  }

  @Test
  public void testModelWithIntDefAnnotation() {
    assertGeneration("ModelWithIntDef.java", "ModelWithIntDef_.java");
  }

  @Test
  public void testModelWithAnnotatedClass() {
    assertGeneration("ModelWithAnnotatedClass.java", "ModelWithAnnotatedClass_.java");
  }

  @Test
  public void testModelWithAbstractClass() {
    JavaFileObject model = JavaFileObjects
        .forResource("ModelWithAbstractClass.java");

    assert_().about(javaSource())
        .that(model)
        .processedWith(new EpoxyProcessor())
        .compilesWithoutError();

    // We don't generate subclasses if the model is abstract unless it has a class annotation.
    boolean modelNotGenerated;
    try {
      JavaFileObjects.forResource("ModelWithAbstractClass_.java");
      modelNotGenerated = false;
    } catch (IllegalArgumentException e) {
      modelNotGenerated = true;
    }

    assertTrue(modelNotGenerated);
  }

  @Test
  public void testModelWithAbstractClassAndAnnotation() {
    assertGeneration("ModelWithAbstractClassAndAnnotation.java",
        "ModelWithAbstractClassAndAnnotation_.java");
  }

  @Test
  public void testModelWithAnnotatedClassAndSuperClass() {
    JavaFileObject model = JavaFileObjects
        .forResource("ModelWithAnnotatedClassAndSuperAttributes.java");

    JavaFileObject generatedModel = JavaFileObjects
        .forResource("ModelWithAnnotatedClassAndSuperAttributes_.java");
    JavaFileObject generatedSubClassModel =
        JavaFileObjects.forResource("ModelWithAnnotatedClassAndSuperAttributes$SubModel"
            + "WithAnnotatedClassAndSuperAttributes_.java");

    assert_().about(javaSource())
        .that(model)
        .processedWith(new EpoxyProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(generatedModel, generatedSubClassModel);
  }

  @Test
  public void testModelWithoutSetter() {
    assertGeneration("ModelWithoutSetter.java", "ModelWithoutSetter_.java");
  }

  @Test
  public void testModelReturningClassType() {
    assertGeneration("ModelReturningClassType.java", "ModelReturningClassType_.java");
  }

  @Test
  public void testModelReturningClassTypeWithVarargs() {
    assertGeneration("ModelReturningClassTypeWithVarargs.java",
        "ModelReturningClassTypeWithVarargs_.java");
  }

  @Test
  public void testModelWithVarargsConstructors() {
    assertGeneration("ModelWithVarargsConstructors.java", "ModelWithVarargsConstructors_.java");
  }

  @Test
  public void testModelWithHolderGeneratesNewHolderMethod() {
    assertGeneration("AbstractModelWithHolder.java", "AbstractModelWithHolder_.java");
  }

  @Test
  public void testGenerateDefaultLayoutMethod() {
    assertGeneration("GenerateDefaultLayoutMethod.java", "GenerateDefaultLayoutMethod_.java");
  }

  @Test
  public void testGenerateDefaultLayoutMethodFailsIfLayoutNotSpecified() {
    assertGenerationError("GenerateDefaultLayoutMethodNoLayout.java",
        "Model must specify a valid layout resource");
  }

  @Test
  public void testGeneratedDefaultMethodWithLayoutSpecifiedInParent() {
    JavaFileObject model = JavaFileObjects
        .forResource("GenerateDefaultLayoutMethodParentLayout.java");

    JavaFileObject generatedNoLayoutModel = JavaFileObjects
        .forResource("GenerateDefaultLayoutMethodParentLayout$NoLayout_.java");
    JavaFileObject generatedWithLayoutModel =
        JavaFileObjects.forResource("GenerateDefaultLayoutMethodParentLayout$WithLayout_.java");

    assert_().about(javaSource())
        .that(model)
        .processedWith(new EpoxyProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(generatedNoLayoutModel, generatedWithLayoutModel);
  }

  @Test
  public void testGeneratedDefaultMethodWithLayoutSpecifiedInNextParent() {
    JavaFileObject model = JavaFileObjects
        .forResource("GenerateDefaultLayoutMethodNextParentLayout.java");

    JavaFileObject generatedNoLayoutModel = JavaFileObjects
        .forResource("GenerateDefaultLayoutMethodNextParentLayout$NoLayout_.java");
    JavaFileObject generatedStillNoLayoutModel = JavaFileObjects
        .forResource("GenerateDefaultLayoutMethodNextParentLayout$StillNoLayout_.java");
    JavaFileObject generatedWithLayoutModel =
        JavaFileObjects.forResource("GenerateDefaultLayoutMethodNextParentLayout$WithLayout_.java");

    assert_().about(javaSource())
        .that(model)
        .processedWith(new EpoxyProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(generatedNoLayoutModel, generatedStillNoLayoutModel,
            generatedWithLayoutModel);
  }

  @Test
  public void testGeneratedDefaultMethodWithLayoutFailsIfNotSpecifiedInParent() {
    assertGenerationError("GenerateDefaultLayoutMethodParentStillNoLayout.java",
        "Model must specify a valid layout resource");
  }

  @Test
  public void modelWithViewClickListener() {
    assertGeneration("ModelWithViewClickListener.java", "ModelWithViewClickListener_.java");
  }

  @Test
  public void modelWithViewClickLongListener() {
    JavaFileObject model = JavaFileObjects
        .forResource("ModelWithViewLongClickListener.java");

    JavaFileObject generatedNoLayoutModel = JavaFileObjects
        .forResource("ModelWithViewLongClickListener_.java");

    assert_().about(javaSource())
        .that(model)
        .processedWith(new EpoxyProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(generatedNoLayoutModel);
  }

  @Test
  public void testModelWithPrivateAttributeWithoutGetterAndSetterFails() {
    assertGenerationError("ModelWithPrivateFieldWithoutGetterAndSetter.java",
        "private fields without proper getter and setter");
  }

  @Test
  public void testModelWithPrivateAttributeWithoutSetterFails() {
    assertGenerationError("ModelWithPrivateFieldWithoutSetter.java",
        "private fields without proper getter and setter");
  }

  @Test
  public void testModelWithPrivateAttributeWithoutGetterFails() {
    assertGenerationError("ModelWithPrivateFieldWithoutGetter.java",
        "private fields without proper getter and setter");
  }

  @Test
  public void testModelWithPrivateAttributeWithIsPrefixGetter() {
    checkFileCompiles("ModelWithPrivateFieldWithIsPrefixGetter.java");
  }

  @Test
  public void testModelWithPrivateAttributeWithPrivateGetterFails() {
    assertGenerationError("ModelWithPrivateFieldWithPrivateGetter.java",
        "private fields without proper getter and setter");
  }

  @Test
  public void testModelWithPrivateAttributeWithStaticGetterFails() {
    assertGenerationError("ModelWithPrivateFieldWithStaticGetter.java",
        "private fields without proper getter and setter");
  }

  @Test
  public void testModelWithPrivateAttributeWithGetterWithParamsFails() {
    assertGenerationError("ModelWithPrivateFieldWithGetterWithParams.java",
        "private fields without proper getter and setter");
  }

  @Test
  public void testModelWithPrivateAttributeWithGetterWithWrongReturnTypeFails() {
    assertGenerationError("ModelWithPrivateFieldWithGetterWithWrongReturnType.java",
        "private fields without proper getter and setter");
  }

  @Test
  public void testModelWithPrivateAttributeWithPrivateSetterFails() {
    assertGenerationError("ModelWithPrivateFieldWithPrivateSetter.java",
        "private fields without proper getter and setter");
  }

  @Test
  public void testModelWithPrivateAttributeWithStaticSetterFails() {
    assertGenerationError("ModelWithPrivateFieldWithStaticSetter.java",
        "private fields without proper getter and setter");
  }

  @Test
  public void testModelWithPrivateAttributeWithGetterWithoutParamsFails() {
    assertGenerationError("ModelWithPrivateFieldWithSettterWithoutParams.java",
        "private fields without proper getter and setter");
  }

  @Test
  public void testModelWithPrivateAttributeWithSetterWithWrongReturnTypeFails() {
    assertGenerationError("ModelWithPrivateFieldWithSetterWithWrongParamType.java",
        "private fields without proper getter and setter");
  }

  @Test
  public void testModelWithAllPrivateFieldTypes() {
    assertGeneration("ModelWithAllPrivateFieldTypes.java", "ModelWithAllPrivateFieldTypes_.java");
  }

  @Test
  public void modelWithViewPrivateClickListener() {
    assertGeneration("ModelWithPrivateViewClickListener.java",
        "ModelWithPrivateViewClickListener_.java");
  }

  @Test
  public void modelWithPrivateFieldWithSameAsFieldGetterAndSetterName() {
    assertGeneration("ModelWithPrivateFieldWithSameAsFieldGetterAndSetterName.java",
        "ModelWithPrivateFieldWithSameAsFieldGetterAndSetterName_.java");
  }

  @Test
  public void testDoNotUseInToStringModel() {
    assertGeneration("ModelDoNotUseInToString.java", "ModelDoNotUseInToString_.java");
  }

  @Test
  public void modelWithAnnotation() {
    assertGeneration("ModelWithAnnotation.java", "ModelWithAnnotation_.java");
  }

  @Test
  public void testModelBuilderInterface() {
    assertGeneration("ModelWithAllFieldTypes.java", "ModelWithAllFieldTypesBuilder.java");
  }
}
