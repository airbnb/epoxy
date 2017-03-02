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

public class ModelProcessorTest {
  @Test
  public void testSimpleModel() {
    JavaFileObject model = JavaFileObjects
        .forResource("BasicModelWithAttribute.java");

    JavaFileObject generatedModel = JavaFileObjects.forResource("BasicModelWithAttribute_.java");

    assert_().about(javaSource())
        .that(model)
        .processedWith(new EpoxyProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(generatedModel);
  }

  @Test
  public void testModelWithAllFieldTypes() {
    JavaFileObject model = JavaFileObjects
        .forResource("ModelWithAllFieldTypes.java");

    JavaFileObject generatedModel = JavaFileObjects.forResource("ModelWithAllFieldTypes_.java");

    assert_().about(javaSource())
        .that(model)
        .processedWith(new EpoxyProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(generatedModel);
  }

  @Test
  public void testModelWithConstructors() {
    JavaFileObject model = JavaFileObjects
        .forResource("ModelWithConstructors.java");

    JavaFileObject generatedModel = JavaFileObjects.forResource("ModelWithConstructors_.java");

    assert_().about(javaSource())
        .that(model)
        .processedWith(new EpoxyProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(generatedModel);
  }

  @Test
  public void testModelWithSuper() {
    JavaFileObject model = JavaFileObjects
        .forResource("ModelWithSuper.java");

    JavaFileObject generatedModel = JavaFileObjects.forResource("ModelWithSuper_.java");

    assert_().about(javaSource())
        .that(model)
        .processedWith(new EpoxyProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(generatedModel);
  }

  @Test
  public void testModelWithFieldAnnotation() {
    JavaFileObject model = JavaFileObjects
        .forResource("ModelWithFieldAnnotation.java");

    JavaFileObject generatedModel = JavaFileObjects.forResource("ModelWithFieldAnnotation_.java");

    assert_().about(javaSource())
        .that(model)
        .processedWith(new EpoxyProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(generatedModel);
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
    JavaFileObject model = JavaFileObjects
        .forResource("ModelWithType.java");

    JavaFileObject generatedModel = JavaFileObjects.forResource("ModelWithType_.java");

    assert_().about(javaSource())
        .that(model)
        .processedWith(new EpoxyProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(generatedModel);
  }

  @Test
  public void testModelWithoutHash() {
    JavaFileObject model = JavaFileObjects
        .forResource("ModelWithoutHash.java");

    JavaFileObject generatedModel = JavaFileObjects.forResource("ModelWithoutHash_.java");

    assert_().about(javaSource())
        .that(model)
        .processedWith(new EpoxyProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(generatedModel);
  }

  @Test
  public void testDoNotHashModel() {
    JavaFileObject model = JavaFileObjects
        .forResource("ModelDoNotHash.java");

    JavaFileObject generatedModel = JavaFileObjects.forResource("ModelDoNotHash_.java");

    assert_().about(javaSource())
        .that(model)
        .processedWith(new EpoxyProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(generatedModel);
  }

  @Test
  public void testModelWithFinalAttribute() {
    JavaFileObject model = JavaFileObjects
        .forResource("ModelWithFinalField.java");

    JavaFileObject generatedModel = JavaFileObjects.forResource("ModelWithFinalField_.java");

    assert_().about(javaSource())
        .that(model)
        .processedWith(new EpoxyProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(generatedModel);
  }

  @Test
  public void testModelWithPrivateAttributeFails() {
    JavaFileObject model = JavaFileObjects
        .forResource("ModelWithPrivateField.java");

    assert_().about(javaSource())
        .that(model)
        .processedWith(new EpoxyProcessor())
        .failsToCompile()
        .withErrorContaining("private");
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
    JavaFileObject model = JavaFileObjects
        .forResource("ModelWithIntDef.java");

    JavaFileObject generatedModel = JavaFileObjects.forResource("ModelWithIntDef_.java");

    assert_().about(javaSource())
        .that(model)
        .processedWith(new EpoxyProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(generatedModel);
  }

  @Test
  public void testModelWithAnnotatedClass() {
    JavaFileObject model = JavaFileObjects
        .forResource("ModelWithAnnotatedClass.java");

    JavaFileObject generatedModel = JavaFileObjects.forResource("ModelWithAnnotatedClass_.java");

    assert_().about(javaSource())
        .that(model)
        .processedWith(new EpoxyProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(generatedModel);
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
      JavaFileObject generatedModel = JavaFileObjects.forResource("ModelWithAbstractClass_.java");
      modelNotGenerated = false;
    } catch (IllegalArgumentException e) {
      modelNotGenerated = true;
    }

    assertTrue(modelNotGenerated);
  }

  @Test
  public void testModelWithAbstractClassAndAnnotation() {
    JavaFileObject model = JavaFileObjects
        .forResource("ModelWithAbstractClassAndAnnotation.java");

    JavaFileObject generatedModel =
        JavaFileObjects.forResource("ModelWithAbstractClassAndAnnotation_.java");

    assert_().about(javaSource())
        .that(model)
        .processedWith(new EpoxyProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(generatedModel);
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
    JavaFileObject model = JavaFileObjects
        .forResource("ModelWithoutSetter.java");

    JavaFileObject generatedModel = JavaFileObjects.forResource("ModelWithoutSetter_.java");

    assert_().about(javaSource())
        .that(model)
        .processedWith(new EpoxyProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(generatedModel);
  }

  @Test
  public void testModelReturningClassType() {
    JavaFileObject model = JavaFileObjects
        .forResource("ModelReturningClassType.java");

    JavaFileObject generatedModel = JavaFileObjects.forResource("ModelReturningClassType_.java");

    assert_().about(javaSource())
        .that(model)
        .processedWith(new EpoxyProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(generatedModel);
  }

  @Test
  public void testModelReturningClassTypeWithVarargs() {
    JavaFileObject model = JavaFileObjects
        .forResource("ModelReturningClassTypeWithVarargs.java");

    JavaFileObject generatedModel = JavaFileObjects
        .forResource("ModelReturningClassTypeWithVarargs_.java");

    assert_().about(javaSource())
        .that(model)
        .processedWith(new EpoxyProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(generatedModel);
  }

  @Test
  public void testModelWithVarargsConstructors() {
    JavaFileObject model = JavaFileObjects
        .forResource("ModelWithVarargsConstructors.java");

    JavaFileObject generatedModel = JavaFileObjects
        .forResource("ModelWithVarargsConstructors_.java");

    assert_().about(javaSource())
        .that(model)
        .processedWith(new EpoxyProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(generatedModel);
  }

  @Test
  public void testModelWithHolderGeneratesNewHolderMethod() {
    JavaFileObject model = JavaFileObjects
        .forResource("AbstractModelWithHolder.java");

    JavaFileObject generatedModel = JavaFileObjects
        .forResource("AbstractModelWithHolder_.java");

    assert_().about(javaSource())
        .that(model)
        .processedWith(new EpoxyProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(generatedModel);
  }

  @Test
  public void testGenerateDefaultLayoutMethod() {
    JavaFileObject model = JavaFileObjects
        .forResource("GenerateDefaultLayoutMethod.java");

    JavaFileObject generatedModel = JavaFileObjects
        .forResource("GenerateDefaultLayoutMethod_.java");

    assert_().about(javaSource())
        .that(model)
        .processedWith(new EpoxyProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(generatedModel);
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
        .forResource("GenerateDefaultLayoutMethodNextParentLayout$NoLayout.java");
    JavaFileObject generatedStillNoLayoutModel = JavaFileObjects
        .forResource("GenerateDefaultLayoutMethodNextParentLayout$StillNoLayout.java");
    JavaFileObject generatedWithLayoutModel =
        JavaFileObjects.forResource("GenerateDefaultLayoutMethodNextParentLayout$WithLayout.java");

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
}
