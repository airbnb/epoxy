package com.airbnb.epoxy;

import com.google.testing.compile.JavaFileObjects;

import org.junit.Test;

import javax.tools.JavaFileObject;

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
    checkGeneration("BasicModelWithAttribute.java", "BasicModelWithAttribute_.java");
  }

  @Test
  public void testModelWithAllFieldTypes() {
    checkGeneration("ModelWithAllFieldTypes.java", "ModelWithAllFieldTypes_.java");
  }

  @Test
  public void testModelWithConstructors() {
    checkGeneration("ModelWithConstructors.java", "ModelWithConstructors_.java");
  }

  @Test
  public void testModelWithSuper() {
    checkGeneration("ModelWithSuper.java", "ModelWithSuper_.java");
  }

  @Test
  public void testModelWithFieldAnnotation() {
    checkGeneration("ModelWithFieldAnnotation.java", "ModelWithFieldAnnotation_.java");
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
    checkGeneration("ModelWithType.java", "ModelWithType_.java");
  }

  @Test
  public void testModelWithoutHash() {
    checkGeneration("ModelWithoutHash.java", "ModelWithoutHash_.java");
  }

  @Test
  public void testDoNotHashModel() {
    checkGeneration("ModelDoNotHash.java", "ModelDoNotHash_.java");
  }

  @Test
  public void testModelWithFinalAttribute() {
    checkGeneration("ModelWithFinalField.java", "ModelWithFinalField_.java");
  }

  @Test
  public void testModelWithStaticAttributeFails() {
    checkFailure("ModelWithStaticField.java", "static");
  }

  @Test
  public void testModelWithPrivateClassFails() {
    checkFailure("ModelWithPrivateInnerClass.java", "private classes");
  }

  @Test
  public void testModelWithFinalClassFails() {
    checkFailure("ModelWithFinalClass.java", "");
  }

  @Test
  public void testModelThatDoesNotExtendEpoxyModelFails() {
    checkFailure("ModelWithoutEpoxyExtension.java", "must extend");
  }

  @Test
  public void testModelAsInnerClassFails() {
    checkFailure("ModelAsInnerClass.java", "Nested classes");
  }

  @Test
  public void testModelWithIntDefAnnotation() {
    checkGeneration("ModelWithIntDef.java", "ModelWithIntDef_.java");
  }

  @Test
  public void testModelWithAnnotatedClass() {
    checkGeneration("ModelWithAnnotatedClass.java", "ModelWithAnnotatedClass_.java");
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
    checkGeneration("ModelWithAbstractClassAndAnnotation.java",
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
    checkGeneration("ModelWithoutSetter.java", "ModelWithoutSetter_.java");
  }

  @Test
  public void testModelReturningClassType() {
    checkGeneration("ModelReturningClassType.java", "ModelReturningClassType_.java");
  }

  @Test
  public void testModelReturningClassTypeWithVarargs() {
    checkGeneration("ModelReturningClassTypeWithVarargs.java",
        "ModelReturningClassTypeWithVarargs_.java");
  }

  @Test
  public void testModelWithVarargsConstructors() {
    checkGeneration("ModelWithVarargsConstructors.java", "ModelWithVarargsConstructors_.java");
  }

  @Test
  public void testModelWithHolderGeneratesNewHolderMethod() {
    checkGeneration("AbstractModelWithHolder.java", "AbstractModelWithHolder_.java");
  }

  @Test
  public void testGenerateDefaultLayoutMethod() {
    checkGeneration("GenerateDefaultLayoutMethod.java", "GenerateDefaultLayoutMethod_.java");
  }

  @Test
  public void testGenerateDefaultLayoutMethodFailsIfLayoutNotSpecified() {
    checkFailure("GenerateDefaultLayoutMethodNoLayout.java",
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
    checkFailure("GenerateDefaultLayoutMethodParentStillNoLayout.java",
        "Model must specify a valid layout resource");
  }

  @Test
  public void modelWithViewClickListener() {
    checkGeneration("ModelWithViewClickListener.java", "ModelWithViewClickListener_.java");
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
    checkFailure("ModelWithPrivateFieldWithoutGetterAndSetter.java",
        "private fields without proper getter and setter");
  }

  @Test
  public void testModelWithPrivateAttributeWithoutSetterFails() {
    checkFailure("ModelWithPrivateFieldWithoutSetter.java",
        "private fields without proper getter and setter");
  }

  @Test
  public void testModelWithPrivateAttributeWithoutGetterFails() {
    checkFailure("ModelWithPrivateFieldWithoutGetter.java",
        "private fields without proper getter and setter");
  }

  @Test
  public void testModelWithPrivateAttributeWithIsPrefixGetter() {
    checkFileCompiles("ModelWithPrivateFieldWithIsPrefixGetter.java");
  }

  @Test
  public void testModelWithPrivateAttributeWithPrivateGetterFails() {
    checkFailure("ModelWithPrivateFieldWithPrivateGetter.java",
        "private fields without proper getter and setter");
  }

  @Test
  public void testModelWithPrivateAttributeWithStaticGetterFails() {
    checkFailure("ModelWithPrivateFieldWithStaticGetter.java",
        "private fields without proper getter and setter");
  }

  @Test
  public void testModelWithPrivateAttributeWithGetterWithParamsFails() {
    checkFailure("ModelWithPrivateFieldWithGetterWithParams.java",
        "private fields without proper getter and setter");
  }

  @Test
  public void testModelWithPrivateAttributeWithGetterWithWrongReturnTypeFails() {
    checkFailure("ModelWithPrivateFieldWithGetterWithWrongReturnType.java",
        "private fields without proper getter and setter");
  }

  @Test
  public void testModelWithPrivateAttributeWithPrivateSetterFails() {
    checkFailure("ModelWithPrivateFieldWithPrivateSetter.java",
        "private fields without proper getter and setter");
  }

  @Test
  public void testModelWithPrivateAttributeWithStaticSetterFails() {
    checkFailure("ModelWithPrivateFieldWithStaticSetter.java",
        "private fields without proper getter and setter");
  }

  @Test
  public void testModelWithPrivateAttributeWithGetterWithoutParamsFails() {
    checkFailure("ModelWithPrivateFieldWithSettterWithoutParams.java",
        "private fields without proper getter and setter");
  }

  @Test
  public void testModelWithPrivateAttributeWithSetterWithWrongReturnTypeFails() {
    checkFailure("ModelWithPrivateFieldWithSetterWithWrongParamType.java",
        "private fields without proper getter and setter");
  }

  @Test
  public void testModelWithAllPrivateFieldTypes() {
    checkGeneration("ModelWithAllPrivateFieldTypes.java", "ModelWithAllPrivateFieldTypes_.java");
  }

  @Test
  public void modelWithViewPrivateClickListener() {
    checkGeneration("ModelWithPrivateViewClickListener.java",
        "ModelWithPrivateViewClickListener_.java");
  }

  @Test
  public void modelWithPrivateFieldWithSameAsFieldGetterAndSetterName() {
    checkGeneration("ModelWithPrivateFieldWithSameAsFieldGetterAndSetterName.java",
        "ModelWithPrivateFieldWithSameAsFieldGetterAndSetterName_.java");
  }

  @Test
  public void testDoNotUseInToStringModel() {
    checkGeneration("ModelDoNotUseInToString.java", "ModelDoNotUseInToString_.java");
  }

  @Test
  public void modelWithAnnotation() {
    checkGeneration("ModelWithAnnotation.java", "ModelWithAnnotation_.java");
  }

  @Test
  public void testModelBuilderInterface() {
    checkGeneration("ModelWithAllFieldTypes.java", "ModelWithAllFieldTypesBuilder.java");
  }

  private void checkFailure(String inputFile, String errorMessage) {
    JavaFileObject model = JavaFileObjects
        .forResource(inputFile);

    assert_().about(javaSource())
        .that(model)
        .processedWith(new EpoxyProcessor())
        .failsToCompile()
        .withErrorContaining(errorMessage);
  }

  private void checkFileCompiles(String inputFile) {
    JavaFileObject model = JavaFileObjects
        .forResource(inputFile);

    assert_().about(javaSource())
        .that(model)
        .processedWith(new EpoxyProcessor())
        .compilesWithoutError();
  }

  private void checkGeneration(String inputFile, String generatedFile) {
    JavaFileObject model = JavaFileObjects
        .forResource(inputFile);

    JavaFileObject generatedModel = JavaFileObjects.forResource(generatedFile);

    assert_().about(javaSource())
        .that(model)
        .processedWith(new EpoxyProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(generatedModel);
  }
}
