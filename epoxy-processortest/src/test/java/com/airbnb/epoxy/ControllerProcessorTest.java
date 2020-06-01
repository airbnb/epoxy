package com.airbnb.epoxy;

import com.google.testing.compile.JavaFileObjects;

import org.junit.Test;

import javax.tools.JavaFileObject;

import static com.airbnb.epoxy.ProcessorTestUtils.processors;
import static com.google.common.truth.Truth.assert_;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;
import static com.google.testing.compile.JavaSourcesSubjectFactory.javaSources;
import static java.util.Arrays.asList;

public class ControllerProcessorTest {

  @Test
  public void controllerWithAutoModel() {
    JavaFileObject model = JavaFileObjects
        .forResource(GuavaPatch.patchResource("BasicModelWithAttribute.java"));

    JavaFileObject controller = JavaFileObjects
        .forResource(GuavaPatch.patchResource("ControllerWithAutoModel.java"));

    JavaFileObject generatedHelper = JavaFileObjects
        .forResource(GuavaPatch.patchResource("ControllerWithAutoModel_EpoxyHelper.java"));

    assert_().about(javaSources())
        .that(asList(model, controller))
        .processedWith(processors())
        .compilesWithoutError()
        .and()
        .generatesSources(generatedHelper);
  }

  @Test
  public void controllerWithAutoModelWithoutValidation() {
    JavaFileObject model = JavaFileObjects
        .forResource(GuavaPatch.patchResource("BasicModelWithAttribute.java"));

    JavaFileObject controller = JavaFileObjects
        .forResource(GuavaPatch.patchResource("ControllerWithAutoModelWithoutValidation.java"));

    JavaFileObject generatedHelper = JavaFileObjects
        .forResource(GuavaPatch.patchResource("ControllerWithAutoModelWithoutValidation_EpoxyHelper.java"));

    assert_().about(javaSources())
        .that(asList(model, controller))
        .withCompilerOptions(ProcessorTestUtils.options(true, false))
        .processedWith(processors())
        .compilesWithoutError()
        .and()
        .generatesSources(generatedHelper);
  }

  @Test
  public void controllerWithSuperClassWithAutoModel() {
    JavaFileObject model = JavaFileObjects
        .forResource(GuavaPatch.patchResource("BasicModelWithAttribute.java"));

    JavaFileObject controller = JavaFileObjects
        .forResource(GuavaPatch.patchResource("ControllerWithAutoModelWithSuperClass.java"));

    JavaFileObject generatedHelper = JavaFileObjects
        .forResource(GuavaPatch.patchResource("ControllerWithAutoModelWithSuperClass_EpoxyHelper.java"));

    JavaFileObject generatedSubHelper = JavaFileObjects
        .forResource(GuavaPatch.patchResource("ControllerWithAutoModelWithSuperClass$SubControllerWithAutoModelWithSuperClass_EpoxyHelper.java"));

    assert_().about(javaSources())
        .that(asList(model, controller))
        .processedWith(processors())
        .compilesWithoutError()
        .and()
        .generatesSources(generatedHelper, generatedSubHelper);
  }

  @Test
  public void autoModelNotInAutoAdapterFails() {
    JavaFileObject badClass = JavaFileObjects
        .forResource(GuavaPatch.patchResource("AutoModelNotInAutoAdapter.java"));

    assert_().about(javaSource())
        .that(badClass)
        .processedWith(processors())
        .failsToCompile();
  }

  @Test
  public void autoModelAnnotationNotOnModelFails() {
    JavaFileObject badClass = JavaFileObjects
        .forResource(GuavaPatch.patchResource("AutoModelNotOnModelField.java"));

    assert_().about(javaSource())
        .that(badClass)
        .processedWith(processors())
        .failsToCompile();
  }

  @Test
  public void setsStagingControllerWhenImplicitlyAddingModels() {
    JavaFileObject model = JavaFileObjects
        .forResource(GuavaPatch.patchResource("BasicModelWithAttribute.java"));

    JavaFileObject controller = JavaFileObjects
        .forResource(GuavaPatch.patchResource("ControllerWithAutoModelAndImplicitAdding.java"));

    JavaFileObject generatedHelper = JavaFileObjects
        .forResource(GuavaPatch.patchResource("ControllerWithAutoModelAndImplicitAdding_EpoxyHelper.java"));

    assert_().about(javaSources())
        .that(asList(model, controller))
        .withCompilerOptions(ProcessorTestUtils.options(false, true))
        .processedWith(processors())
        .compilesWithoutError()
        .and()
        .generatesSources(generatedHelper);

  }
}
