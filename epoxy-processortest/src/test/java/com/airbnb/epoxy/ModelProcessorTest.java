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
    testGeneration("BasicModelWithAttribute.java", "BasicModelWithAttribute_.java");
  }

  @Test
  public void testModelWithAllFieldTypes() {
    testGeneration("ModelWithAllFieldTypes.java", "ModelWithAllFieldTypes_.java");
  }

  @Test
  public void testModelWithConstructors() {
    testGeneration("ModelWithConstructors.java", "ModelWithConstructors_.java");
  }

  @Test
  public void testModelWithSuper() {
    testGeneration("ModelWithSuper.java", "ModelWithSuper_.java");
  }

  @Test
  public void testModelWithFieldAnnotation() {
    testGeneration("ModelWithFieldAnnotation.java", "ModelWithFieldAnnotation_.java");
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
    testGeneration("ModelWithType.java", "ModelWithType_.java");
  }

  @Test
  public void testModelWithoutHash() {
    testGeneration("ModelWithoutHash.java", "ModelWithoutHash_.java");
  }

  @Test
  public void testDoNotHashModel() {
    testGeneration("ModelDoNotHash.java", "ModelDoNotHash_.java");
  }

  @Test
  public void testModelWithFinalAttribute() {
    testGeneration("ModelWithFinalField.java", "ModelWithFinalField_.java");
  }

  @Test
  public void testModelWithStaticAttributeFails() {
    JavaFileObject model = JavaFileObjects
        .forResource("ModelWithStaticField.java");

    assert_().about(javaSource())
        .that(model)
        .processedWith(new EpoxyProcessor())
        .failsToCompile()
        .withErrorContaining("static");
  }

  @Test
  public void testModelWithPrivateClassFails() {
    JavaFileObject model = JavaFileObjects
        .forResource("ModelWithPrivateInnerClass.java");

    assert_().about(javaSource())
        .that(model)
        .processedWith(new EpoxyProcessor())
        .failsToCompile()
        .withErrorContaining("private classes");
  }

  @Test
  public void testModelWithFinalClassFails() {
    JavaFileObject model = JavaFileObjects
        .forResource("ModelWithFinalClass.java");

    assert_().about(javaSource())
        .that(model)
        .processedWith(new EpoxyProcessor())
        .failsToCompile();
  }

  @Test
  public void testModelThatDoesNotExtendEpoxyModelFails() {
    JavaFileObject model = JavaFileObjects
        .forResource("ModelWithoutEpoxyExtension.java");

    assert_().about(javaSource())
        .that(model)
        .processedWith(new EpoxyProcessor())
        .failsToCompile()
        .withErrorContaining("must extend");
  }

  @Test
  public void testModelAsInnerClassFails() {
    JavaFileObject model = JavaFileObjects
        .forResource("ModelAsInnerClass.java");

    assert_().about(javaSource())
        .that(model)
        .processedWith(new EpoxyProcessor())
        .failsToCompile()
        .withErrorContaining("Nested classes");
  }

  @Test
  public void testModelWithIntDefAnnotation() {
    testGeneration("ModelWithIntDef.java", "ModelWithIntDef_.java");
  }

  @Test
  public void testModelWithAnnotatedClass() {
    testGeneration("ModelWithAnnotatedClass.java", "ModelWithAnnotatedClass_.java");
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
    testGeneration("ModelWithAbstractClassAndAnnotation.java",
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
    testGeneration("ModelWithoutSetter.java", "ModelWithoutSetter_.java");
  }

  @Test
  public void testModelReturningClassType() {
    testGeneration("ModelReturningClassType.java", "ModelReturningClassType_.java");
  }

  @Test
  public void testModelReturningClassTypeWithVarargs() {
    testGeneration("ModelReturningClassTypeWithVarargs.java",
        "ModelReturningClassTypeWithVarargs_.java");
  }

  @Test
  public void testModelWithVarargsConstructors() {
    testGeneration("ModelWithVarargsConstructors.java", "ModelWithVarargsConstructors_.java");
  }

  @Test
  public void testModelWithHolderGeneratesNewHolderMethod() {
    testGeneration("AbstractModelWithHolder.java", "AbstractModelWithHolder_.java");
  }

  @Test
  public void testGenerateDefaultLayoutMethod() {
    testGeneration("GenerateDefaultLayoutMethod.java", "GenerateDefaultLayoutMethod_.java");
  }

  @Test
  public void testGenerateDefaultLayoutMethodFailsIfLayoutNotSpecified() {
    JavaFileObject model = JavaFileObjects
        .forResource("GenerateDefaultLayoutMethodNoLayout.java");

    assert_().about(javaSource())
        .that(model)
        .processedWith(new EpoxyProcessor())
        .failsToCompile()
        .withErrorContaining("Model must specify a valid layout resource");
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
    JavaFileObject model = JavaFileObjects
        .forResource("GenerateDefaultLayoutMethodParentStillNoLayout.java");

    assert_().about(javaSource())
        .that(model)
        .processedWith(new EpoxyProcessor())
        .failsToCompile()
        .withErrorContaining("Model must specify a valid layout resource");
  }

  @Test
  public void modelWithViewClickListener() {
    JavaFileObject model = JavaFileObjects
        .forResource("ModelWithViewClickListener.java");

    JavaFileObject generatedNoLayoutModel = JavaFileObjects
        .forResource("ModelWithViewClickListener_.java");

    assert_().about(javaSource())
        .that(model)
        .processedWith(new EpoxyProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(generatedNoLayoutModel);
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
    JavaFileObject model = JavaFileObjects
        .forResource("ModelWithPrivateFieldWithoutGetterAndSetter.java");

    assert_().about(javaSource())
        .that(model)
        .processedWith(new EpoxyProcessor())
        .failsToCompile()
        .withErrorContaining("private fields without proper getter and setter");
  }

  @Test
  public void testModelWithPrivateAttributeWithoutSetterFails() {
    JavaFileObject model = JavaFileObjects
        .forResource("ModelWithPrivateFieldWithoutSetter.java");

    assert_().about(javaSource())
        .that(model)
        .processedWith(new EpoxyProcessor())
        .failsToCompile()
        .withErrorContaining("private fields without proper getter and setter");
  }

  @Test
  public void testModelWithPrivateAttributeWithoutGetterFails() {
    JavaFileObject model = JavaFileObjects
        .forResource("ModelWithPrivateFieldWithoutGetter.java");

    assert_().about(javaSource())
        .that(model)
        .processedWith(new EpoxyProcessor())
        .failsToCompile()
        .withErrorContaining("private fields without proper getter and setter");
  }

  @Test
  public void testModelWithPrivateAttributeWithIsPrefixGetter() {
    JavaFileObject model = JavaFileObjects
        .forResource("ModelWithPrivateFieldWithIsPrefixGetter.java");

    assert_().about(javaSource())
        .that(model)
        .processedWith(new EpoxyProcessor())
        .compilesWithoutError();
  }

  @Test
  public void testModelWithPrivateAttributeWithPrivateGetterFails() {
    JavaFileObject model = JavaFileObjects
        .forResource("ModelWithPrivateFieldWithPrivateGetter.java");

    assert_().about(javaSource())
        .that(model)
        .processedWith(new EpoxyProcessor())
        .failsToCompile()
        .withErrorContaining("private fields without proper getter and setter");
  }

  @Test
  public void testModelWithPrivateAttributeWithStaticGetterFails() {
    JavaFileObject model = JavaFileObjects
        .forResource("ModelWithPrivateFieldWithStaticGetter.java");

    assert_().about(javaSource())
        .that(model)
        .processedWith(new EpoxyProcessor())
        .failsToCompile()
        .withErrorContaining("private fields without proper getter and setter");
  }

  @Test
  public void testModelWithPrivateAttributeWithGetterWithParamsFails() {
    JavaFileObject model = JavaFileObjects
        .forResource("ModelWithPrivateFieldWithGetterWithParams.java");

    assert_().about(javaSource())
        .that(model)
        .processedWith(new EpoxyProcessor())
        .failsToCompile()
        .withErrorContaining("private fields without proper getter and setter");
  }

  @Test
  public void testModelWithPrivateAttributeWithGetterWithWrongReturnTypeFails() {
    JavaFileObject model = JavaFileObjects
        .forResource("ModelWithPrivateFieldWithGetterWithWrongReturnType.java");

    assert_().about(javaSource())
        .that(model)
        .processedWith(new EpoxyProcessor())
        .failsToCompile()
        .withErrorContaining("private fields without proper getter and setter");
  }

  @Test
  public void testModelWithPrivateAttributeWithPrivateSetterFails() {
    JavaFileObject model = JavaFileObjects
        .forResource("ModelWithPrivateFieldWithPrivateSetter.java");

    assert_().about(javaSource())
        .that(model)
        .processedWith(new EpoxyProcessor())
        .failsToCompile()
        .withErrorContaining("private fields without proper getter and setter");
  }

  @Test
  public void testModelWithPrivateAttributeWithStaticSetterFails() {
    JavaFileObject model = JavaFileObjects
        .forResource("ModelWithPrivateFieldWithStaticSetter.java");

    assert_().about(javaSource())
        .that(model)
        .processedWith(new EpoxyProcessor())
        .failsToCompile()
        .withErrorContaining("private fields without proper getter and setter");
  }

  @Test
  public void testModelWithPrivateAttributeWithGetterWithoutParamsFails() {
    JavaFileObject model = JavaFileObjects
        .forResource("ModelWithPrivateFieldWithSettterWithoutParams.java");

    assert_().about(javaSource())
        .that(model)
        .processedWith(new EpoxyProcessor())
        .failsToCompile()
        .withErrorContaining("private fields without proper getter and setter");
  }

  @Test
  public void testModelWithPrivateAttributeWithSetterWithWrongReturnTypeFails() {
    JavaFileObject model = JavaFileObjects
        .forResource("ModelWithPrivateFieldWithSetterWithWrongParamType.java");

    assert_().about(javaSource())
        .that(model)
        .processedWith(new EpoxyProcessor())
        .failsToCompile()
        .withErrorContaining("private fields without proper getter and setter");
  }

  @Test
  public void testModelWithAllPrivateFieldTypes() {
    testGeneration("ModelWithAllPrivateFieldTypes.java", "ModelWithAllPrivateFieldTypes_.java");
  }

  @Test
  public void modelWithViewPrivateClickListener() {
    testGeneration("ModelWithPrivateViewClickListener.java",
        "ModelWithPrivateViewClickListener_.java");
  }

  @Test
  public void modelWithPrivateFieldWithSameAsFieldGetterAndSetterName() {
    testGeneration("ModelWithPrivateFieldWithSameAsFieldGetterAndSetterName.java",
        "ModelWithPrivateFieldWithSameAsFieldGetterAndSetterName_.java");
  }

  @Test
  public void testDoNotUseInToStringModel() {
    testGeneration("ModelDoNotUseInToString.java", "ModelDoNotUseInToString_.java");
  }

  @Test
  public void modelWithAnnotation() {
    testGeneration("ModelWithAnnotation.java", "ModelWithAnnotation_.java");


  }

  private void testGeneration(String inputFile, String generatedFile) {
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
