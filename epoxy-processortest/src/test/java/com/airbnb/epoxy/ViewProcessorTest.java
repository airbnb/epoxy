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

    JavaFileObject generatedModel =
        JavaFileObjects.forResource("TestStringOverloadsViewModel_.java");

    assert_().about(javaSource())
        .that(model)
        .processedWith(new EpoxyProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(generatedModel);
  }

  @Test
  public void stringOverloads_throwsIfNotCharSequence() {
    JavaFileObject model = JavaFileObjects
        .forResource("StringOverloads_throwsIfNotCharSequence.java");

    assert_().about(javaSource())
        .that(model)
        .processedWith(new EpoxyProcessor())
        .failsToCompile()
        .withErrorContaining("must be a CharSequence");
  }

  @Test
  public void nullStringOverloads() {
    JavaFileObject model = JavaFileObjects
        .forResource("TestNullStringOverloadsView.java");

    JavaFileObject generatedModel =
        JavaFileObjects.forResource("TestNullStringOverloadsViewModel_.java");

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
  public void prop_throwsIfPrivate() {
    JavaFileObject model = JavaFileObjects
        .forResource("Prop_throwsIfPrivate.java");

    assert_().about(javaSource())
        .that(model)
        .processedWith(new EpoxyProcessor())
        .failsToCompile()
        .withErrorContaining("private");
  }

  @Test
  public void prop_throwsIfStatic() {
    JavaFileObject model = JavaFileObjects
        .forResource("Prop_throwsIfStatic.java");

    assert_().about(javaSource())
        .that(model)
        .processedWith(new EpoxyProcessor())
        .failsToCompile()
        .withErrorContaining("static");
  }

  @Test
  public void prop_throwsIfNoParams() {
    JavaFileObject model = JavaFileObjects
        .forResource("Prop_throwsIfNoParams.java");

    assert_().about(javaSource())
        .that(model)
        .processedWith(new EpoxyProcessor())
        .failsToCompile()
        .withErrorContaining("must have exactly 1 parameter");
  }

  @Test
  public void prop_throwsIfMultipleParams() {
    JavaFileObject model = JavaFileObjects
        .forResource("Prop_throwsIfMultipleParams.java");

    assert_().about(javaSource())
        .that(model)
        .processedWith(new EpoxyProcessor())
        .failsToCompile()
        .withErrorContaining("must have exactly 1 parameter");
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

  @Test
  public void defaults() {
    JavaFileObject model = JavaFileObjects
        .forResource("PropDefaultsView.java");

    JavaFileObject generatedModel = JavaFileObjects.forResource("PropDefaultsViewModel_.java");

    assert_().about(javaSource())
        .that(model)
        .processedWith(new EpoxyProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(generatedModel);
  }

  @Test
  public void defaults_throwsForNonStaticValue() {
    JavaFileObject model = JavaFileObjects
        .forResource("PropDefaultsView_throwsForNonStaticValue.java");

    assert_().about(javaSource())
        .that(model)
        .processedWith(new EpoxyProcessor())
        .failsToCompile()
        .withErrorContaining("static");
  }

  @Test
  public void defaults_throwsForNonFinalValue() {
    JavaFileObject model = JavaFileObjects
        .forResource("PropDefaultsView_throwsForNonFinalValue.java");

    assert_().about(javaSource())
        .that(model)
        .processedWith(new EpoxyProcessor())
        .failsToCompile()
        .withErrorContaining("final");
  }

  @Test
  public void defaults_throwsForPrivateValue() {
    JavaFileObject model = JavaFileObjects
        .forResource("PropDefaultsView_throwsForPrivateValue.java");

    assert_().about(javaSource())
        .that(model)
        .processedWith(new EpoxyProcessor())
        .failsToCompile()
        .withErrorContaining("final");
  }

  @Test
  public void defaults_throwsForWrongType() {
    JavaFileObject model = JavaFileObjects
        .forResource("PropDefaultsView_throwsForWrongType.java");

    assert_().about(javaSource())
        .that(model)
        .processedWith(new EpoxyProcessor())
        .failsToCompile()
        .withErrorContaining("must be a int");
  }

  @Test
  public void onViewRecycled() {
    JavaFileObject model = JavaFileObjects
        .forResource("OnViewRecycledView.java");

    JavaFileObject generatedModel = JavaFileObjects.forResource("OnViewRecycledViewModel_.java");

    assert_().about(javaSource())
        .that(model)
        .processedWith(new EpoxyProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(generatedModel);
  }

  @Test
  public void onViewRecycled_throwsIfPrivate() {
    JavaFileObject model = JavaFileObjects
        .forResource("OnViewRecycledView_throwsIfPrivate.java");

    assert_().about(javaSource())
        .that(model)
        .processedWith(new EpoxyProcessor())
        .failsToCompile()
        .withErrorContaining("private");
  }

  @Test
  public void onViewRecycled_throwsIfStatic() {
    JavaFileObject model = JavaFileObjects
        .forResource("OnViewRecycledView_throwsIfStatic.java");

    assert_().about(javaSource())
        .that(model)
        .processedWith(new EpoxyProcessor())
        .failsToCompile()
        .withErrorContaining("static");
  }

  @Test
  public void onViewRecycled_throwsIfHasParams() {
    JavaFileObject model = JavaFileObjects
        .forResource("OnViewRecycledView_throwsIfHasParams.java");

    assert_().about(javaSource())
        .that(model)
        .processedWith(new EpoxyProcessor())
        .failsToCompile()
        .withErrorContaining("must have exactly 0 parameter");
  }

  @Test
  public void nullOnRecycle() {
    JavaFileObject model = JavaFileObjects
        .forResource("NullOnRecycleView.java");

    JavaFileObject generatedModel = JavaFileObjects.forResource("NullOnRecycleViewModel_.java");

    assert_().about(javaSource())
        .that(model)
        .processedWith(new EpoxyProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(generatedModel);
  }

  @Test
  public void nullOnRecycle_throwsIfNotNullable() {
    JavaFileObject model = JavaFileObjects
        .forResource("NullOnRecycleView_throwsIfNotNullable.java");

    assert_().about(javaSource())
        .that(model)
        .processedWith(new EpoxyProcessor())
        .failsToCompile()
        .withErrorContaining("@Nullable");
  }

  @Test
  public void doNotHash() {
    JavaFileObject model = JavaFileObjects
        .forResource("DoNotHashView.java");

    JavaFileObject generatedModel = JavaFileObjects.forResource("DoNotHashViewModel_.java");

    assert_().about(javaSource())
        .that(model)
        .processedWith(new EpoxyProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(generatedModel);
  }

  @Test
  public void objectWithoutEqualsThrows() {
    JavaFileObject model = JavaFileObjects
        .forResource("ObjectWithoutEqualsThrowsView.java");


    assert_().about(javaSource())
        .that(model)
        .processedWith(new EpoxyProcessor())
        .failsToCompile()
        .withErrorContaining("Attribute does not implement hashCode");
  }

  @Test
  public void ignoreRequireHashCode() {
    JavaFileObject model = JavaFileObjects
        .forResource("IgnoreRequireHashCodeView.java");

    JavaFileObject generatedModel = JavaFileObjects.forResource("IgnoreRequireHashCodeViewModel_.java");

    assert_().about(javaSource())
        .that(model)
        .processedWith(new EpoxyProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(generatedModel);
  }


}
