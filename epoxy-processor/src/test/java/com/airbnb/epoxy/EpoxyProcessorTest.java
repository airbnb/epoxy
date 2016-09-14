package com.airbnb.epoxy;

import com.google.testing.compile.JavaFileObjects;

import org.junit.Test;

import javax.tools.JavaFileObject;

import static com.google.common.truth.Truth.assert_;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

/**
 * These tests cannot be run from Android Studio as it fails to pick up the files in the resources
 * folder. Run them from the command line (eg ./gradlew test) instead.
 */
public class EpoxyProcessorTest {
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
  public void testModelWithFinalAttributeFails() {
    JavaFileObject model = JavaFileObjects
        .forResource("ModelWithFinalField.java");

    assert_().about(javaSource())
        .that(model)
        .processedWith(new EpoxyProcessor())
        .failsToCompile()
        .withErrorContaining("final");
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
        .failsToCompile()
        .withErrorContaining("final");
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
}
