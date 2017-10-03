package com.airbnb.epoxy;

import com.google.testing.compile.JavaFileObjects;

import java.util.ArrayList;
import java.util.List;

import javax.tools.JavaFileObject;

import static com.google.common.truth.Truth.assert_;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;
import static com.google.testing.compile.JavaSourcesSubjectFactory.javaSources;

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

  static void assertGeneration(List<String> inputFiles, List<String> fileNames) {
    List<JavaFileObject> sources = new ArrayList<>();

    for (String inputFile : inputFiles) {
      sources.add(JavaFileObjects
          .forResource(inputFile));
    }

    List<JavaFileObject> generatedFiles = new ArrayList<>();
    for (int i = 0; i < fileNames.size(); i++) {
      generatedFiles.add(JavaFileObjects.forResource(fileNames.get(i)));
    }

    assert_().about(javaSources())
        .that(sources)
        .processedWith(new EpoxyProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(generatedFiles.get(0),
            generatedFiles.subList(1, generatedFiles.size())
                .toArray(new JavaFileObject[fileNames.size() - 1]));
  }
}
