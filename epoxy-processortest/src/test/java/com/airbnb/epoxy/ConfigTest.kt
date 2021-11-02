package com.airbnb.epoxy

import com.airbnb.epoxy.ProcessorTestUtils.assertGeneration
import com.airbnb.epoxy.ProcessorTestUtils.assertGenerationError
import com.airbnb.epoxy.ProcessorTestUtils.options
import com.airbnb.epoxy.ProcessorTestUtils.processors
import com.google.testing.compile.JavaFileObjects
import org.junit.Test

class ConfigTest {

    @Test
    fun testSubPackageOverridesParent() {
        val subPackageConfig = JavaFileObjects.forSourceString(
            "com.airbnb.epoxy.configtest.sub.EpoxyNestedConfig",
            """package com.airbnb.epoxy.configtest.sub;
                    import com.airbnb.epoxy.PackageEpoxyConfig;
                    @PackageEpoxyConfig(
                        requireHashCode = false
                    )
                    interface EpoxyNestedConfig {}
                    """
        )
        val model =
            JavaFileObjects.forResource("ModelConfigSubPackageOverridesParent.java".patchResource())

        assertGeneration(
            sources = listOf(CONFIG_CLASS_REQUIRE_HASH, model, subPackageConfig),
            generatedFileObjects = emptyList(),
        )
    }

    @Test
    fun testPackageWithNoConfigInheritsNearestParentConfig() {
        val topLevelParentConfig = JavaFileObjects.forSourceString(
            "com.airbnb.epoxy.configtest.EpoxyConfig1",
            """package com.airbnb.epoxy.configtest;
                        import com.airbnb.epoxy.PackageEpoxyConfig;
                        @PackageEpoxyConfig(
                            requireHashCode = false
                        )
                        interface EpoxyConfig1 {}
                        """
        )
        val secondLevelParentConfig = JavaFileObjects.forSourceString(
            "com.airbnb.epoxy.configtest.sub.EpoxyConfig2",
            """package com.airbnb.epoxy.configtest.sub;
                    import com.airbnb.epoxy.PackageEpoxyConfig;
                    @PackageEpoxyConfig(
                        requireHashCode = true
                    )
                    interface EpoxyConfig2 {}
                    """
        )
        val model = JavaFileObjects.forResource(
            "ModelPackageWithNoConfigInheritsNearestParentConfig.java".patchResource()
        )

        assertGenerationError(
            sources = listOf(topLevelParentConfig, secondLevelParentConfig, model),
            errorMessage = "Attribute does not implement hashCode",
            compilationMode = CompilationMode.JavaAP
        )
    }

    @Test
    fun testConfigRequireHashCode() {
        val model =
            JavaFileObjects.forResource("ModelRequiresHashCodeFailsBasicObject.java".patchResource())

        assertGenerationError(
            sources = listOf(CONFIG_CLASS_REQUIRE_HASH, model),
            errorMessage = "Attribute does not implement hashCode",
            compilationMode = CompilationMode.JavaAP
        )
    }

    @Test
    fun testConfigRequireEquals() {
        val model =
            JavaFileObjects.forResource("ModelRequiresEqualsFailsBasicObject.java".patchResource())

        assertGenerationError(
            sources = listOf(CONFIG_CLASS_REQUIRE_HASH, model),
            errorMessage = "Attribute does not implement equals",
            compilationMode = CompilationMode.JavaAP
        )
    }

    @Test
    fun testConfigRequireHashCodeIterableFails() {
        val model =
            JavaFileObjects.forResource("ModelRequiresHashCodeIterableFails.java".patchResource())

        assertGenerationError(
            sources = listOf(CONFIG_CLASS_REQUIRE_HASH, model),
            errorMessage = "Type in Iterable does not implement hashCode"
        )
    }

    @Test
    fun testConfigRequireHashCodeIterablePasses() {
        val model =
            JavaFileObjects.forResource("ModelRequiresHashCodeIterableSucceeds.java".patchResource())

        assertGeneration(
            sources = listOf(CONFIG_CLASS_REQUIRE_HASH, model),
            generatedFileObjects = listOf()
        )
    }

    @Test
    fun testConfigRequireHashCodeArrayFails() {
        val model =
            JavaFileObjects.forResource("ModelRequiresHashCodeArrayFails.java".patchResource())

        assertGenerationError(
            sources = listOf(CONFIG_CLASS_REQUIRE_HASH, model),
            errorMessage = "Type in array does not implement hashCode"
        )
    }

    @Test
    fun testConfigRequireHashCodeArrayPasses() {
        val model =
            JavaFileObjects.forResource("ModelRequiresHashCodeArraySucceeds.java".patchResource())

        assertGeneration(
            sources = listOf(CONFIG_CLASS_REQUIRE_HASH, model),
            generatedFileObjects = listOf()
        )
    }

    @Test
    fun testConfigRequireHashCodeEnumAttributePasses() {
        // Verify that enum attributes pass the hashcode requirement
        val model =
            JavaFileObjects.forResource("ModelRequiresHashCodeEnumPasses.java".patchResource())

        assertGeneration(
            sources = listOf(CONFIG_CLASS_REQUIRE_HASH, model),
            generatedFileObjects = listOf()
        )
    }

    @Test
    fun testConfigRequireHashCodeCharSequencePasses() {
        // Verify that AutoValue class attributes pass the hashcode requirement
        val model =
            JavaFileObjects.forResource("ModelConfigRequireHashCodeCharSequencePasses.java".patchResource())

        assertGeneration(
            sources = listOf(CONFIG_CLASS_REQUIRE_HASH, model),
            generatedFileObjects = listOf()
        )
    }

    @Test
    fun testConfigRequireHashCodeInterfaceWithHashCodePasses() {
        // Verify that AutoValue class attributes pass the hashcode requirement
        val model = JavaFileObjects.forResource(
            "ModelConfigRequireHashCodeInterfaceWithHashCodePasses.java".patchResource()
        )
        assertGeneration(
            sources = listOf(CONFIG_CLASS_REQUIRE_HASH, model),
            generatedFileObjects = listOf()
        )
    }

    @Test
    fun testConfigRequireHashCodeAutoValueAttributePasses() {
        // Verify that AutoValue class attributes pass the hashcode requirement
        val model = JavaFileObjects
            .forResource("ModelRequiresHashCodeAutoValueClassPasses.java".patchResource())

        assertGeneration(
            sources = listOf(CONFIG_CLASS_REQUIRE_HASH, model),
            generatedFileObjects = listOf()
        )
    }

    @Test
    fun testConfigRequireHashCodeAllowsMarkedAttributes() {
        // Verify that AutoValue class attributes pass the hashcode requirement
        val model = JavaFileObjects
            .forResource(
                "ModelConfigRequireHashCodeAllowsMarkedAttributes.java".patchResource()
            )

        assertGeneration(
            sources = listOf(CONFIG_CLASS_REQUIRE_HASH, model),
            generatedFileObjects = listOf()
        )
    }

    @Test
    fun testConfigRequireAbstractModelPassesClassWithAttribute() {
        // Verify that AutoValue class attributes pass the hashcode requirement. Only works for
        // classes in the module since AutoValue has a retention of Source so it is discarded after
        // compilation
        val model =
            JavaFileObjects.forResource("RequireAbstractModelPassesClassWithAttribute.java".patchResource())

        assertGeneration(
            sources = listOf(CONFIG_CLASS_REQUIRE_HASH, model),
            generatedFileObjects = listOf()
        )
    }

    @Test
    fun testConfigRequireAbstractModelFailsClassWithAttribute() {
        // Verify that AutoValue class attributes pass the hashcode requirement
        val model =
            JavaFileObjects.forResource("RequireAbstractModelFailsClassWithAttribute.java".patchResource())

        assertGenerationError(
            sources = listOf(CONFIG_CLASS_REQUIRE_ABSTRACT, model),
            errorMessage = "Epoxy model class must be abstract (RequireAbstractModelFailsClassWithAttribute)"
        )
    }

    @Test
    fun testConfigRequireAbstractModelPassesEpoxyModelClass() {
        // Verify that AutoValue class attributes pass the hashcode requirement
        val model =
            JavaFileObjects.forResource("RequireAbstractModelPassesEpoxyModelClass.java".patchResource())

        assertGeneration(
            sources = listOf(CONFIG_CLASS_REQUIRE_ABSTRACT, model),
            generatedFileObjects = listOf()
        )
    }

    @Test
    fun testConfigRequireAbstractModelFailsEpoxyModelClass() {
        // Verify that AutoValue class attributes pass the hashcode requirement
        val model =
            JavaFileObjects.forResource("RequireAbstractModelFailsEpoxyModelClass.java".patchResource())

        assertGenerationError(
            sources = listOf(CONFIG_CLASS_REQUIRE_ABSTRACT, model),
            errorMessage = "Epoxy model class must be abstract (RequireAbstractModelFailsEpoxyModelClass)"
        )
    }

    @Test
    fun testConfigNoModelValidation() {
        val model = JavaFileObjects.forResource("ModelNoValidation.java".patchResource())
        val generatedModel = JavaFileObjects.forResource("ModelNoValidation_.java".patchResource())

        googleCompileJava(listOf(model))
            .withCompilerOptions(options(true, false))
            .processedWith(processors())
            .compilesWithoutError()
            .and()
            .generatesSources(generatedModel)
    }

    companion object {
        private val CONFIG_CLASS_REQUIRE_HASH = JavaFileObjects
            .forSourceString(
                "com.airbnb.epoxy.configtest.EpoxyConfig",
                """package com.airbnb.epoxy.configtest;
                        import com.airbnb.epoxy.PackageEpoxyConfig;
                        @PackageEpoxyConfig(
                            requireHashCode = true
                        )
                        interface EpoxyConfig {}
                        """
            )
        private val CONFIG_CLASS_REQUIRE_ABSTRACT = JavaFileObjects
            .forSourceString(
                "com.airbnb.epoxy.configtest.EpoxyConfig",
                """package com.airbnb.epoxy.configtest;
                        import com.airbnb.epoxy.PackageEpoxyConfig;
                        @PackageEpoxyConfig(
                            requireAbstractModels = true
                        )
                        interface EpoxyConfig {}
                        """
            )
    }
}
