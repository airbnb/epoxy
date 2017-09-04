package com.airbnb.epoxy;

import com.google.testing.compile.JavaFileObjects;

import javax.tools.JavaFileObject;

import static com.google.common.truth.Truth.assert_;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

class ProcessorTestUtils {
  static void assertGenerationError(String inputFile, String errorMessage) {
    JavaFileObject model = JavaFileObjects
        .forResource(inputFile);

    assert_().about(javaSource())
        .that(model)
        .processedWith(new EpoxyProcessor())
        .failsToCompile()
        .withErrorContaining(errorMessage);
  }

  static void checkFileCompiles(String inputFile) {
    JavaFileObject model = JavaFileObjects
        .forResource(inputFile);

    assert_().about(javaSource())
        .that(model)
        .processedWith(new EpoxyProcessor())
        .compilesWithoutError();
  }

  static void assertGeneration(String inputFile, String generatedFile) {
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
