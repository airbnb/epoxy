package com.airbnb.epoxy

import com.airbnb.epoxy.ProcessorTestUtils.assertGeneration
import com.airbnb.epoxy.ProcessorTestUtils.options
import com.airbnb.epoxy.ProcessorTestUtils.processors
import com.google.testing.compile.JavaFileObjects
import org.junit.Test

class DataBindingModelTest {

    @Test
    fun testSimpleModel() {
        val model = JavaFileObjects
            .forResource("DataBindingModelWithAllFieldTypes.java".patchResource())
        val generatedModel =
            JavaFileObjects.forResource("DataBindingModelWithAllFieldTypes_.java".patchResource())

        assertGeneration(
            sources = listOf(model, BR_CLASS, R),
            generatedFileObjects = listOf(generatedModel),
        )
    }

    @Test
    fun testSimpleModelNoValidation() {
        val model = JavaFileObjects
            .forResource("DataBindingModelWithAllFieldTypesNoValidation.java".patchResource())
        val generatedModel =
            JavaFileObjects.forResource("DataBindingModelWithAllFieldTypesNoValidation_.java".patchResource())

        googleCompileJava(listOf(model, BR_CLASS, R))
            .withCompilerOptions(options(withNoValidation = true, withImplicitAdding = false))
            .processedWith(processors())
            .compilesWithoutError()
            .and()
            .generatesSources(generatedModel)
    }

    companion object {
        private val R = JavaFileObjects.forSourceString(
            "com.airbnb.epoxy.R",
            """package com.airbnb.epoxy;
public final class R {
  public static final class array {
    public static final int res = 0x7f040001;
  }
  public static final class bool {
    public static final int res = 0x7f040002;
  }
  public static final class color {
    public static final int res = 0x7f040003;
  }
  public static final class layout {
    public static final int res = 0x7f040008;
    public static final int model_with_data_binding=0x7f040009;
    public static final int model_with_data_binding_without_donothash=0x7f0f002b;
  }
  public static final class integer {
    public static final int res = 0x7f040004;
  }
  public static final class styleable {
    public static final int[] ActionBar = { 0x7f010001, 0x7f010003 };
  }
}"""
        )
        private val BR_CLASS = JavaFileObjects
            .forSourceString(
                "com.airbnb.epoxy.BR",
                """package com.airbnb.epoxy;

public class BR {
  public static final int _all = 0;
  public static final int valueInt = 1;
  public static final int valueInteger = 2;
  public static final int valueShort = 3;
  public static final int valueShortWrapper = 4;
  public static final int valueChar = 5;
  public static final int valueCharacter = 6;
  public static final int valuebByte = 7;
  public static final int valueByteWrapper = 8;
  public static final int valueLong = 9;
  public static final int valueLongWrapper = 10;
  public static final int valueDouble = 11;
  public static final int valueDoubleWrapper = 12;
  public static final int valueFloat = 13;
  public static final int valueFloatWrapper = 14;
  public static final int valueBoolean = 15;
  public static final int valueBooleanWrapper = 16;
  public static final int valueIntArray = 17;
  public static final int valueObjectArray = 18;
  public static final int valueString = 19;
  public static final int valueObject = 20;
  public static final int valueList = 21;
  public static final int stringValue = 22;
  public static final int clickListener = 23;
}"""
            )
    }
}
