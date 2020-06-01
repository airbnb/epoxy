package com.airbnb.epoxy;

import com.google.testing.compile.JavaFileObjects;

import org.junit.Test;

import java.util.Arrays;

import javax.tools.JavaFileObject;

import static com.airbnb.epoxy.ProcessorTestUtils.processors;
import static com.google.common.truth.Truth.assert_;
import static com.google.testing.compile.JavaSourcesSubjectFactory.javaSources;

public class EpoxyResourceProcessorTest {

  private static final JavaFileObject R = JavaFileObjects.forSourceString("com.airbnb.epoxy.R", ""
      + "package com.airbnb.epoxy;\n"
      + "public final class R {\n"
      + "  public static final class array {\n"
      + "    public static final int res = 0x7f040001;\n"
      + "  }\n"
      + "  public static final class bool {\n"
      + "    public static final int res = 0x7f040002;\n"
      + "  }\n"
      + "  public static final class color {\n"
      + "    public static final int res = 0x7f040003;\n"
      + "  }\n"
      + "  public static final class layout {\n"
      + "    public static final int res = 0x7f040008;\n"
      + "  }\n"
      + "  public static final class integer {\n"
      + "    public static final int res = 0x7f040004;\n"
      + "  }\n"
      + "  public static final class styleable {\n"
      + "    public static final int[] ActionBar = { 0x7f010001, 0x7f010003 };\n"
      + "  }\n"
      + "}"
  );

  private static final JavaFileObject R_FROM_DIFFERENT_PACKAGE_WITH_SAME_VALUE =
      JavaFileObjects.forSourceString("com.airbnb.epoxy.othermodule.R", ""
          + "package com.airbnb.epoxy.othermodule;\n"
          + "public final class R {\n"

          + "  public static final class layout {\n"
          + "    public static final int res_in_other_module = 0x7f040008;\n"
          + "  }\n"
          + "}"
      );

  @Test
  public void testGenerateDefaultLayoutMethod() {
    JavaFileObject model = JavaFileObjects
        .forResource(GuavaPatch.patchResource("ModelForRProcessingTest.java"));

    JavaFileObject generatedModel = JavaFileObjects
        .forResource(GuavaPatch.patchResource("ModelForRProcessingTest_.java"));

    assert_().about(javaSources())
        .that(Arrays.asList(model, R))
        .processedWith(processors())
        .compilesWithoutError()
        .and()
        .generatesSources(generatedModel);
  }

  @Test
  public void testRFilesWithSameValue() {
    // These two models use different R classes, but their layout value within each R class is
    // the same. This tests that the resource processor namespaces the R classes correctly to
    // avoid collisions between the two identical layout values.
    
    JavaFileObject model = JavaFileObjects
        .forResource(GuavaPatch.patchResource("ModelForRProcessingTest.java"));

    JavaFileObject modelWithDifferentRClass = JavaFileObjects
        .forResource(GuavaPatch.patchResource("ModelForTestingDuplicateRValues.java"));

    JavaFileObject generatedModel = JavaFileObjects
        .forResource(GuavaPatch.patchResource("ModelForRProcessingTest_.java"));

    JavaFileObject generatedModelWithDifferentRClass = JavaFileObjects
        .forResource(GuavaPatch.patchResource("ModelForTestingDuplicateRValues_.java"));

    assert_().about(javaSources())
        .that(Arrays
            .asList(model, modelWithDifferentRClass, R, R_FROM_DIFFERENT_PACKAGE_WITH_SAME_VALUE))
        .processedWith(processors())
        .compilesWithoutError()
        .and()
        .generatesSources(generatedModel, generatedModelWithDifferentRClass);
  }
}
