package com.airbnb.epoxy

import com.airbnb.epoxy.ProcessorTestUtils.assertGeneration
import com.google.testing.compile.JavaFileObjects
import org.junit.Test
import javax.tools.JavaFileObject

class ModelFactoryViewProcessorTest {

    @Test
    fun baseModel() {
        assertGeneration(
            "BaseModelView.java",
            "BaseModelViewModel_.java"
        )
    }

    @Test
    fun callbackPropModel() {
        assertGeneration(
            "CallbackPropModelView.java",
            "CallbackPropModelViewModel_.java"
        )
    }

    @Test
    fun textPropModel() {
        assertGeneration(
            "TextPropModelView.java",
            "TextPropModelViewModel_.java"
        )
    }

    @Test
    fun allTypesModel() {
        assertGeneration(
            "AllTypesModelView.java",
            "AllTypesModelViewModel_.java"
        )
    }

    @Test
    fun styleableModel() {
        // If the view is styleable then the generated "from" method supports setting a style

        val configClass: JavaFileObject = JavaFileObjects
            .forSourceLines("com.airbnb.epoxy.package-info",
                            "@ParisConfig(rClass = R.class)\n"
                                    + "package com.airbnb.epoxy;\n"
                                    + "\n"
                                    + "import com.airbnb.paris.annotations.ParisConfig;\n"
                                    + "import com.airbnb.epoxymodelfactory.R;\n")

        assertGeneration(
            "StyleableModelView.java",
            "StyleableModelViewModel_.java",
            useParis = true,
            helperObjects = listOf(configClass)
        )
    }
}
