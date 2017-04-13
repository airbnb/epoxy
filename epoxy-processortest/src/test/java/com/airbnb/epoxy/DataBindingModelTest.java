package com.airbnb.epoxy;

import com.google.testing.compile.JavaFileObjects;

import org.junit.Test;

import javax.tools.JavaFileObject;

import static com.google.common.truth.Truth.assert_;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

public class DataBindingModelTest {

//  @Test
//  public void testSimpleModel() {
//    JavaFileObject model = JavaFileObjects
//        .forResource("BasicModelWithAttribute.java");
//
//    JavaFileObject generatedModel = JavaFileObjects.forResource("BasicModelWithAttribute_.java");
//
////    EpoxyProcessor.withNoValidation()
//
//    assert_().about(javaSource())
//        .that(model)
//        .processedWith(new EpoxyProcessor())
//        .compilesWithoutError()
//        .and()
//        .generatesSources(generatedModel);
//  }
}
