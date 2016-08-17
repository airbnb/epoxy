package com.airbnb.viewmodeladapter;

import com.google.testing.compile.JavaFileObjects;

import org.junit.Test;

import javax.tools.JavaFileObject;

import static com.google.common.truth.Truth.assert_;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

public class ViewModelAdapterProcessorTest {
  @Test
  public void testProcessor() {
    JavaFileObject sampleActivity = JavaFileObjects
        .forResource("BasicModelWithAttribute.java");
    

    assert_().about(javaSource())
        .that(sampleActivity)
        .processedWith(new ViewModelAdapterProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(JavaFileObjects.forResource("BasicModelWithAttribute_.java"));
  }
}
