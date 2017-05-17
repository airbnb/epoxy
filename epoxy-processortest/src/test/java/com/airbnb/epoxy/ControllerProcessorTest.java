package com.airbnb.epoxy;

import com.google.testing.compile.JavaFileObjects;

import org.junit.Test;

import javax.tools.JavaFileObject;

import static com.google.common.truth.Truth.assert_;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;
import static com.google.testing.compile.JavaSourcesSubjectFactory.javaSources;
import static java.util.Arrays.asList;

public class ControllerProcessorTest {

  @Test
  public void controllerWithAutoModel() {
    JavaFileObject model = JavaFileObjects
        .forResource("BasicModelWithAttribute.java");

    JavaFileObject controller = JavaFileObjects
        .forResource("ControllerWithAutoModel.java");

    JavaFileObject generatedHelper = JavaFileObjects
        .forResource("ControllerWithAutoModel_EpoxyHelper.java");

    assert_().about(javaSources())
        .that(asList(model, controller))
        .processedWith(new EpoxyProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(generatedHelper);
  }

  @Test
  public void controllerWithAutoModelWithoutValidation() {
    JavaFileObject model = JavaFileObjects
        .forResource("BasicModelWithAttribute.java");

    JavaFileObject controller = JavaFileObjects
        .forResource("ControllerWithAutoModelWithoutValidation.java");

    JavaFileObject generatedHelper = JavaFileObjects
        .forResource("ControllerWithAutoModelWithoutValidation_EpoxyHelper.java");

    assert_().about(javaSources())
        .that(asList(model, controller))
        .processedWith(EpoxyProcessor.withNoValidation())
        .compilesWithoutError()
        .and()
        .generatesSources(generatedHelper);
  }

  @Test
  public void controllerWithSuperClassWithAutoModel() {
    JavaFileObject model = JavaFileObjects
        .forResource("BasicModelWithAttribute.java");

    JavaFileObject controller = JavaFileObjects
        .forResource("ControllerWithAutoModelWithSuperClass.java");

    JavaFileObject generatedHelper = JavaFileObjects
        .forResource("ControllerWithAutoModelWithSuperClass_EpoxyHelper.java");

    JavaFileObject generatedSubHelper = JavaFileObjects
        .forResource("ControllerWithAutoModelWithSuperClass$SubControllerWithAutoModelWithSuperClass_EpoxyHelper.java");

    assert_().about(javaSources())
        .that(asList(model, controller))
        .processedWith(new EpoxyProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(generatedHelper, generatedSubHelper);
  }

  @Test
  public void autoModelNotInAutoAdapterFails() {
    JavaFileObject badClass = JavaFileObjects
        .forResource("AutoModelNotInAutoAdapter.java");

    assert_().about(javaSource())
        .that(badClass)
        .processedWith(new EpoxyProcessor())
        .failsToCompile();
  }

  @Test
  public void autoModelAnnotationNotOnModelFails() {
    JavaFileObject badClass = JavaFileObjects
        .forResource("AutoModelNotOnModelField.java");

    assert_().about(javaSource())
        .that(badClass)
        .processedWith(new EpoxyProcessor())
        .failsToCompile();
  }

  @Test
  public void setsStagingControllerWhenImplicitlyAddingModels() {
    JavaFileObject model = JavaFileObjects
        .forResource("BasicModelWithAttribute.java");

    JavaFileObject controller = JavaFileObjects
        .forResource("ControllerWithAutoModelAndImplicitAdding.java");

    JavaFileObject generatedHelper = JavaFileObjects
        .forResource("ControllerWithAutoModelAndImplicitAdding_EpoxyHelper.java");

    assert_().about(javaSources())
        .that(asList(model, controller))
        .processedWith(EpoxyProcessor.withImplicitAdding())
        .compilesWithoutError()
        .and()
        .generatesSources(generatedHelper);

  }
}
