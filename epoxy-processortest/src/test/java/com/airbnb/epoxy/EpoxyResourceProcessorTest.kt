package com.airbnb.epoxy

import com.airbnb.epoxy.ProcessorTestUtils.assertGeneration
import com.google.testing.compile.JavaFileObjects
import org.junit.Test

class EpoxyResourceProcessorTest {

    @Test
    fun testGenerateDefaultLayoutMethod() {
        val model = JavaFileObjects
            .forResource("ModelForRProcessingTest.java".patchResource())
        val generatedModel = JavaFileObjects
            .forResource("ModelForRProcessingTest_.java".patchResource())

        assertGeneration(
            sources = listOf(model, R),
            generatedFileObjects = listOf(generatedModel)
        )
    }

    @Test
    fun testRFilesWithSameValue() {
        // These two models use different R classes, but their layout value within each R class is
        // the same. This tests that the resource processor namespaces the R classes correctly to
        // avoid collisions between the two identical layout values.
        val model = JavaFileObjects
            .forResource("ModelForRProcessingTest.java".patchResource())

        val modelWithDifferentRClass = JavaFileObjects
            .forResource("ModelForTestingDuplicateRValues.java".patchResource())

        val generatedModel = JavaFileObjects
            .forResource("ModelForRProcessingTest_.java".patchResource())

        val generatedModelWithDifferentRClass = JavaFileObjects
            .forResource("ModelForTestingDuplicateRValues_.java".patchResource())

        assertGeneration(
            sources = listOf(
                model,
                modelWithDifferentRClass,
                R,
                R_FROM_DIFFERENT_PACKAGE_WITH_SAME_VALUE
            ),
            generatedFileObjects = listOf(generatedModel, generatedModelWithDifferentRClass),
            // Issue with kotlin compile testing prevents us from having two files with the same name
            // (the R class)
            compilationMode = CompilationMode.JavaAP
        )
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
                          }
                          public static final class integer {
                            public static final int res = 0x7f040004;
                          }
                          public static final class styleable {
                            public static final int[] ActionBar = { 0x7f010001, 0x7f010003 };
                          }
                        }"""
        )
        private val R_FROM_DIFFERENT_PACKAGE_WITH_SAME_VALUE = JavaFileObjects.forSourceString(
            "com.airbnb.epoxy.othermodule.R",
            """package com.airbnb.epoxy.othermodule;
                    public final class R {
                      public static final class layout {
                        public static final int res_in_other_module = 0x7f040008;
                      }
                    }"""
        )
    }
}
