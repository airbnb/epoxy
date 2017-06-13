package com.airbnb.epoxy;

import com.google.testing.compile.JavaFileObjects;

import org.junit.Test;

import javax.tools.JavaFileObject;

import static com.google.common.truth.Truth.assert_;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class ViewProcessorTest {
  @Test
  public void stringOverloads() {
    JavaFileObject model = JavaFileObjects
        .forResource("TestStringOverloadsView.java");

    JavaFileObject generatedModel = JavaFileObjects.forResource("TestStringOverloadsViewModel_.java");

    assert_().about(javaSource())
        .that(model)
        .processedWith(new EpoxyProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(generatedModel);
  }

  @Test
  public void nullStringOverloads() {
    JavaFileObject model = JavaFileObjects
        .forResource("TestNullStringOverloadsView.java");

    JavaFileObject generatedModel = JavaFileObjects.forResource("TestNullStringOverloadsViewModel_.java");

    assert_().about(javaSource())
        .that(model)
        .processedWith(new EpoxyProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(generatedModel);
  }

  @Test
  public void manyTypes() {
    JavaFileObject model = JavaFileObjects
        .forResource("TestManyTypesView.java");

    JavaFileObject generatedModel = JavaFileObjects.forResource("TestManyTypesViewModel_.java");

    assert_().about(javaSource())
        .that(model)
        .processedWith(new EpoxyProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(generatedModel);
  }

  @Test
  public void groups() {
    JavaFileObject model = JavaFileObjects
        .forResource("PropGroupsView.java");

    JavaFileObject generatedModel = JavaFileObjects.forResource("PropGroupsViewModel_.java");

    assert_().about(javaSource())
        .that(model)
        .processedWith(new EpoxyProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(generatedModel);
  }
}
