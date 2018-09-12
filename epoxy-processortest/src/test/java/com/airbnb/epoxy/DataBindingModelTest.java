package com.airbnb.epoxy;

import com.google.testing.compile.JavaFileObjects;

import org.junit.Test;

import javax.tools.JavaFileObject;

import static com.google.common.truth.Truth.assert_;
import static com.google.testing.compile.JavaSourcesSubjectFactory.javaSources;
import static java.util.Arrays.asList;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class DataBindingModelTest {

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
      + "    public static final int model_with_data_binding=0x7f040009;\n"
      + "    public static final int model_with_data_binding_without_donothash=0x7f0f002b;\n"
      + "  }\n"
      + "  public static final class integer {\n"
      + "    public static final int res = 0x7f040004;\n"
      + "  }\n"
      + "  public static final class styleable {\n"
      + "    public static final int[] ActionBar = { 0x7f010001, 0x7f010003 };\n"
      + "  }\n"
      + "}"
  );

  private static final JavaFileObject BR_CLASS = JavaFileObjects
      .forSourceString("com.airbnb.epoxy.BR",
          "package com.airbnb.epoxy;\n"
              + "\n"
              + "public class BR {\n"
              + "  public static final int _all = 0;\n"
              + "  public static final int valueInt = 1;\n"
              + "  public static final int valueInteger = 2;\n"
              + "  public static final int valueShort = 3;\n"
              + "  public static final int valueShortWrapper = 4;\n"
              + "  public static final int valueChar = 5;\n"
              + "  public static final int valueCharacter = 6;\n"
              + "  public static final int valuebByte = 7;\n"
              + "  public static final int valueByteWrapper = 8;\n"
              + "  public static final int valueLong = 9;\n"
              + "  public static final int valueLongWrapper = 10;\n"
              + "  public static final int valueDouble = 11;\n"
              + "  public static final int valueDoubleWrapper = 12;\n"
              + "  public static final int valueFloat = 13;\n"
              + "  public static final int valueFloatWrapper = 14;\n"
              + "  public static final int valueBoolean = 15;\n"
              + "  public static final int valueBooleanWrapper = 16;\n"
              + "  public static final int valueIntArray = 17;\n"
              + "  public static final int valueObjectArray = 18;\n"
              + "  public static final int valueString = 19;\n"
              + "  public static final int valueObject = 20;\n"
              + "  public static final int valueList = 21;\n"
              + "  public static final int stringValue = 22;\n"
              + "  public static final int clickListener = 23;\n"
              + "}");

  @Test
  public void testSimpleModel() {
    JavaFileObject model = JavaFileObjects
        .forResource("DataBindingModelWithAllFieldTypes.java");

    JavaFileObject generatedModel =
        JavaFileObjects.forResource("DataBindingModelWithAllFieldTypes_.java");

    assert_().about(javaSources())
        .that(asList(model, BR_CLASS, R))
        .processedWith(new EpoxyProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(generatedModel);
  }

  @Test
  public void testSimpleModelNoValidation() {
    JavaFileObject model = JavaFileObjects
        .forResource("DataBindingModelWithAllFieldTypesNoValidation.java");

    JavaFileObject generatedModel =
        JavaFileObjects.forResource("DataBindingModelWithAllFieldTypesNoValidation_.java");

    assert_().about(javaSources())
        .that(asList(model, BR_CLASS, R))
        .processedWith(EpoxyProcessor.withNoValidation())
        .compilesWithoutError()
        .and()
        .generatesSources(generatedModel);
  }

  @Test
  public void testFullyGeneratedModel() {
    JavaFileObject packageInfo = JavaFileObjects
        .forResource("package-info.java");

    JavaFileObject binding = JavaFileObjects
        .forResource("ModelWithDataBindingBinding.java");

    JavaFileObject generatedModel =
        JavaFileObjects.forResource("ModelWithDataBindingBindingModel_.java");

    assert_().about(javaSources())
        .that(asList(packageInfo, binding, BR_CLASS, R))
        .processedWith(new EpoxyProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(generatedModel);
  }

  @Test
  public void testFullyGeneratedModelWithoutDoNotHash() {
    JavaFileObject packageInfo = JavaFileObjects
        .forSourceString("com.airbnb.epoxy.package-info",
            "@EpoxyDataBindingLayouts(value = {R.layout"
                + ".model_with_data_binding_without_donothash}, enableDoNotHash = false)\n"
                + "package com.airbnb.epoxy;\n"
        );

    JavaFileObject binding = JavaFileObjects
        .forResource("ModelWithDataBindingWithoutDonothashBinding.java");

    JavaFileObject generatedModel =
        JavaFileObjects.forResource("ModelWithDataBindingWithoutDonothashBindingModel_.java");

    assert_().about(javaSources())
        .that(asList(packageInfo, binding, BR_CLASS, R))
        .processedWith(new EpoxyProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(generatedModel);
  }
}
