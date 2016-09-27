package com.airbnb.epoxy;

import com.google.common.truth.Truth;
import com.google.testing.compile.JavaFileObjects;
import com.google.testing.compile.JavaSourceSubjectFactory;

import org.junit.Test;

import javax.tools.JavaFileObject;

/**
 * These processor tests are in their own module since the processor module can't depend on the
 * android EpoxyAdapter library that contains the EpoxyModel.
 */

public class EpoxyProcessorTest {
  @Test
  public void testSimpleModel() {
    JavaFileObject model = JavaFileObjects
        .forResource("BasicModelWithAttribute.java");

    JavaFileObject generatedModel = JavaFileObjects.forResource("BasicModelWithAttribute_.java");

    Truth.assert_().about(JavaSourceSubjectFactory.javaSource())
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

    Truth.assert_().about(JavaSourceSubjectFactory.javaSource())
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

    Truth.assert_().about(JavaSourceSubjectFactory.javaSource())
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

    Truth.assert_().about(JavaSourceSubjectFactory.javaSource())
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

    Truth.assert_().about(JavaSourceSubjectFactory.javaSource())
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

    Truth.assert_().about(JavaSourceSubjectFactory.javaSource())
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

    Truth.assert_().about(JavaSourceSubjectFactory.javaSource())
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

    Truth.assert_().about(JavaSourceSubjectFactory.javaSource())
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

    Truth.assert_().about(JavaSourceSubjectFactory.javaSource())
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

    Truth.assert_().about(JavaSourceSubjectFactory.javaSource())
        .that(model)
        .processedWith(new EpoxyProcessor())
        .failsToCompile()
        .withErrorContaining("private");
  }

  @Test
  public void testModelWithStaticAttributeFails() {
    JavaFileObject model = JavaFileObjects
        .forResource("ModelWithStaticField.java");

    Truth.assert_().about(JavaSourceSubjectFactory.javaSource())
        .that(model)
        .processedWith(new EpoxyProcessor())
        .failsToCompile()
        .withErrorContaining("static");
  }

  @Test
  public void testModelWithPrivateClassFails() {
    JavaFileObject model = JavaFileObjects
        .forResource("ModelWithPrivateInnerClass.java");

    Truth.assert_().about(JavaSourceSubjectFactory.javaSource())
        .that(model)
        .processedWith(new EpoxyProcessor())
        .failsToCompile()
        .withErrorContaining("private classes");
  }

  @Test
  public void testModelWithFinalClassFails() {
    JavaFileObject model = JavaFileObjects
        .forResource("ModelWithFinalClass.java");

    Truth.assert_().about(JavaSourceSubjectFactory.javaSource())
        .that(model)
        .processedWith(new EpoxyProcessor())
        .failsToCompile();
  }

  @Test
  public void testModelThatDoesNotExtendEpoxyModelFails() {
    JavaFileObject model = JavaFileObjects
        .forResource("ModelWithoutEpoxyExtension.java");

    Truth.assert_().about(JavaSourceSubjectFactory.javaSource())
        .that(model)
        .processedWith(new EpoxyProcessor())
        .failsToCompile()
        .withErrorContaining("must extend");
  }

  @Test
  public void testModelAsInnerClassFails() {
    JavaFileObject model = JavaFileObjects
        .forResource("ModelAsInnerClass.java");

    Truth.assert_().about(JavaSourceSubjectFactory.javaSource())
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

    Truth.assert_().about(JavaSourceSubjectFactory.javaSource())
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

    Truth.assert_().about(JavaSourceSubjectFactory.javaSource())
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

    Truth.assert_().about(JavaSourceSubjectFactory.javaSource())
        .that(model)
        .processedWith(new EpoxyProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(generatedModel, generatedSubClassModel);
  }
}
