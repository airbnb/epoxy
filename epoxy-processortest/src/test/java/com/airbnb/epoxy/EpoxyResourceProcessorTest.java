package com.airbnb.epoxy;

import com.google.testing.compile.JavaFileObjects;

import org.junit.Test;

import java.util.Arrays;

import javax.tools.JavaFileObject;

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

  @Test
  public void testGenerateDefaultLayoutMethod() {
    JavaFileObject model = JavaFileObjects
        .forResource("ModelForRProcessingTest.java");

    JavaFileObject generatedModel = JavaFileObjects
        .forResource("ModelForRProcessingTest_.java");

    assert_().about(javaSources())
        .that(Arrays.asList(model, R))
        .processedWith(new EpoxyProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(generatedModel);
  }
}
