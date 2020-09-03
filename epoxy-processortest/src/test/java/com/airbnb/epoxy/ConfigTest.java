package com.airbnb.epoxy;

import com.google.testing.compile.JavaFileObjects;

import org.junit.Test;

import javax.tools.JavaFileObject;

import static com.airbnb.epoxy.ProcessorTestUtils.processors;
import static com.google.common.truth.Truth.assert_;
import static com.google.testing.compile.JavaFileObjects.forResource;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;
import static com.google.testing.compile.JavaSourcesSubjectFactory.javaSources;
import static java.util.Arrays.asList;

public class ConfigTest {

  private static final JavaFileObject CONFIG_CLASS_REQUIRE_HASH =
      JavaFileObjects
          .forSourceString("com.airbnb.epoxy.configtest.EpoxyConfig",
              "package com.airbnb.epoxy.configtest;\n" +
                  "import com.airbnb.epoxy.PackageEpoxyConfig;\n" +
                  "@PackageEpoxyConfig(\n"
                  + "    requireHashCode = true\n"
                  + ")\n"
                  + "interface EpoxyConfig {}"
                  + "\n");

  private static final JavaFileObject CONFIG_CLASS_REQUIRE_ABSTRACT =
      JavaFileObjects
          .forSourceString("com.airbnb.epoxy.configtest.EpoxyConfig",
              "package com.airbnb.epoxy.configtest;\n" +
                  "import com.airbnb.epoxy.PackageEpoxyConfig;\n" +
                  "@PackageEpoxyConfig(\n"
                  + "    requireAbstractModels = true\n"
                  + ")\n"
                  + "interface EpoxyConfig {}"
                  + "\n");

  @Test
  public void testSubPackageOverridesParent() {
    JavaFileObject subPackageConfig =
        JavaFileObjects.forSourceString("com.airbnb.epoxy.configtest.sub.EpoxyConfig",
            "package com.airbnb.epoxy.configtest.sub;\n" +
                "import com.airbnb.epoxy.PackageEpoxyConfig;\n" +
                "@PackageEpoxyConfig(\n"
                + "    requireHashCode = false\n"
                + ")\n"
                + "interface EpoxyConfig {}"
                + "\n");

    JavaFileObject model =
        forResource(GuavaPatch.patchResource("ModelConfigSubPackageOverridesParent.java"));

    assert_().about(javaSources())
        .that(asList(CONFIG_CLASS_REQUIRE_HASH, model, subPackageConfig))
        .processedWith(processors())
        .compilesWithoutError();
  }

  @Test
  public void testPackageWithNoConfigInheritsNearestParentConfig() {
    JavaFileObject topLevelParentConfig =
        JavaFileObjects.forSourceString("com.airbnb.epoxy.configtest.EpoxyConfig",
            "package com.airbnb.epoxy.configtest;\n" +
                "import com.airbnb.epoxy.PackageEpoxyConfig;\n" +
                "@PackageEpoxyConfig(\n"
                + "    requireHashCode = false\n"
                + ")\n"
                + "interface EpoxyConfig {}"
                + "\n");

    JavaFileObject secondLevelParentConfig =
        JavaFileObjects.forSourceString("com.airbnb.epoxy.configtest.sub.EpoxyConfig",
            "package com.airbnb.epoxy.configtest.sub;\n" +
                "import com.airbnb.epoxy.PackageEpoxyConfig;\n" +
                "@PackageEpoxyConfig(\n"
                + "    requireHashCode = true\n"
                + ")\n"
                + "interface EpoxyConfig {}"
                + "\n");

    JavaFileObject model =
        forResource(
            GuavaPatch.patchResource("ModelPackageWithNoConfigInheritsNearestParentConfig.java"));

    assert_().about(javaSources())
        .that(asList(topLevelParentConfig, secondLevelParentConfig, model))
        .processedWith(processors())
        .failsToCompile()
        .withErrorContaining("Attribute does not implement hashCode");
  }

  @Test
  public void testConfigRequireHashCode() {
    JavaFileObject model =
        forResource(GuavaPatch.patchResource("ModelRequiresHashCodeFailsBasicObject.java"));

    assert_().about(javaSources())
        .that(asList(CONFIG_CLASS_REQUIRE_HASH, model))
        .processedWith(processors())
        .failsToCompile()
        .withErrorContaining("Attribute does not implement hashCode");
  }

  @Test
  public void testConfigRequireEquals() {
    JavaFileObject model =
        forResource(GuavaPatch.patchResource("ModelRequiresEqualsFailsBasicObject.java"));

    assert_().about(javaSources())
        .that(asList(CONFIG_CLASS_REQUIRE_HASH, model))
        .processedWith(processors())
        .failsToCompile()
        .withErrorContaining("Attribute does not implement equals");
  }

  @Test
  public void testConfigRequireHashCodeIterableFails() {
    JavaFileObject model =
        forResource(GuavaPatch.patchResource("ModelRequiresHashCodeIterableFails.java"));

    assert_().about(javaSources())
        .that(asList(CONFIG_CLASS_REQUIRE_HASH, model))
        .processedWith(processors())
        .failsToCompile()
        .withErrorContaining("Type in Iterable does not implement hashCode");
  }

  @Test
  public void testConfigRequireHashCodeIterablePasses() {
    JavaFileObject model =
        forResource(GuavaPatch.patchResource("ModelRequiresHashCodeIterableSucceeds.java"));

    assert_().about(javaSources())
        .that(asList(CONFIG_CLASS_REQUIRE_HASH, model))
        .processedWith(processors())
        .compilesWithoutError();
  }

  @Test
  public void testConfigRequireHashCodeArrayFails() {
    JavaFileObject model =
        forResource(GuavaPatch.patchResource("ModelRequiresHashCodeArrayFails.java"));

    assert_().about(javaSources())
        .that(asList(CONFIG_CLASS_REQUIRE_HASH, model))
        .processedWith(processors())
        .failsToCompile()
        .withErrorContaining("Type in array does not implement hashCode");
  }

  @Test
  public void testConfigRequireHashCodeArrayPasses() {
    JavaFileObject model =
        forResource(GuavaPatch.patchResource("ModelRequiresHashCodeArraySucceeds.java"));

    assert_().about(javaSources())
        .that(asList(CONFIG_CLASS_REQUIRE_HASH, model))
        .processedWith(processors())
        .compilesWithoutError();
  }

  @Test
  public void testConfigRequireHashCodeEnumAttributePasses() {
    // Verify that enum attributes pass the hashcode requirement
    JavaFileObject model =
        forResource(GuavaPatch.patchResource("ModelRequiresHashCodeEnumPasses.java"));

    assert_().about(javaSources())
        .that(asList(CONFIG_CLASS_REQUIRE_HASH, model))
        .processedWith(processors())
        .compilesWithoutError();
  }

  @Test
  public void testConfigRequireHashCodeCharSequencePasses() {
    // Verify that AutoValue class attributes pass the hashcode requirement
    JavaFileObject model =
        forResource(GuavaPatch.patchResource("ModelConfigRequireHashCodeCharSequencePasses.java"));

    assert_().about(javaSources())
        .that(asList(CONFIG_CLASS_REQUIRE_HASH, model))
        .processedWith(processors())
        .compilesWithoutError();
  }

  @Test
  public void testConfigRequireHashCodeInterfaceWithHashCodePasses() {
    // Verify that AutoValue class attributes pass the hashcode requirement
    JavaFileObject model =
        forResource(
            GuavaPatch.patchResource("ModelConfigRequireHashCodeInterfaceWithHashCodePasses.java"));

    assert_().about(javaSources())
        .that(asList(CONFIG_CLASS_REQUIRE_HASH, model))
        .processedWith(processors())
        .compilesWithoutError();
  }

  @Test
  public void testConfigRequireHashCodeAutoValueAttributePasses() {
    // Verify that AutoValue class attributes pass the hashcode requirement
    JavaFileObject model = JavaFileObjects
        .forResource(GuavaPatch.patchResource("ModelRequiresHashCodeAutoValueClassPasses.java"));

    assert_().about(javaSources())
        .that(asList(CONFIG_CLASS_REQUIRE_HASH, model))
        .processedWith(processors())
        .compilesWithoutError();
  }

  @Test
  public void testConfigRequireHashCodeAllowsMarkedAttributes() {
    // Verify that AutoValue class attributes pass the hashcode requirement
    JavaFileObject model = JavaFileObjects
        .forResource(
            GuavaPatch.patchResource("ModelConfigRequireHashCodeAllowsMarkedAttributes.java"));

    assert_().about(javaSources())
        .that(asList(CONFIG_CLASS_REQUIRE_HASH, model))
        .processedWith(processors())
        .compilesWithoutError();
  }

  @Test
  public void testConfigRequireAbstractModelPassesClassWithAttribute() {
    // Verify that AutoValue class attributes pass the hashcode requirement. Only works for
    // classes in the module since AutoValue has a retention of Source so it is discarded after
    // compilation
    JavaFileObject model =
        forResource(GuavaPatch.patchResource("RequireAbstractModelPassesClassWithAttribute.java"));

    assert_().about(javaSources())
        .that(asList(CONFIG_CLASS_REQUIRE_ABSTRACT, model))
        .processedWith(processors())
        .compilesWithoutError();
  }

  @Test
  public void testConfigRequireAbstractModelFailsClassWithAttribute() {
    // Verify that AutoValue class attributes pass the hashcode requirement
    JavaFileObject model =
        forResource(GuavaPatch.patchResource("RequireAbstractModelFailsClassWithAttribute.java"));

    assert_().about(javaSources())
        .that(asList(CONFIG_CLASS_REQUIRE_ABSTRACT, model))
        .processedWith(processors())
        .failsToCompile()
        .withErrorContaining(
            "Epoxy model class must be abstract (RequireAbstractModelFailsClassWithAttribute)");
  }

  @Test
  public void testConfigRequireAbstractModelPassesEpoxyModelClass() {
    // Verify that AutoValue class attributes pass the hashcode requirement
    JavaFileObject model =
        forResource(GuavaPatch.patchResource("RequireAbstractModelPassesEpoxyModelClass.java"));

    assert_().about(javaSources())
        .that(asList(CONFIG_CLASS_REQUIRE_ABSTRACT, model))
        .processedWith(processors())
        .compilesWithoutError();
  }

  @Test
  public void testConfigRequireAbstractModelFailsEpoxyModelClass() {
    // Verify that AutoValue class attributes pass the hashcode requirement
    JavaFileObject model =
        forResource(GuavaPatch.patchResource("RequireAbstractModelFailsEpoxyModelClass.java"));

    assert_().about(javaSources())
        .that(asList(CONFIG_CLASS_REQUIRE_ABSTRACT, model))
        .processedWith(processors())
        .failsToCompile()
        .withErrorContaining(
            "Epoxy model class must be abstract (RequireAbstractModelFailsEpoxyModelClass)");
  }

  @Test
  public void testConfigNoModelValidation() {
    JavaFileObject model =
        forResource(GuavaPatch.patchResource("ModelNoValidation.java"));

    JavaFileObject generatedModel =
        JavaFileObjects.forResource(GuavaPatch.patchResource("ModelNoValidation_.java"));

    assert_().about(javaSource())
        .that(model)
        .withCompilerOptions(ProcessorTestUtils.options(true, false))
        .processedWith(processors())
        .compilesWithoutError()
        .and()
        .generatesSources(generatedModel);
  }
}
