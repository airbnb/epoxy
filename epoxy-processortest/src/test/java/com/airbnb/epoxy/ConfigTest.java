package com.airbnb.epoxy;

import com.google.testing.compile.JavaFileObjects;

import org.junit.Test;

import javax.tools.JavaFileObject;

import static com.google.common.truth.Truth.assert_;
import static com.google.testing.compile.JavaSourcesSubjectFactory.javaSources;
import static java.util.Arrays.asList;

public class ConfigTest {

  private static final JavaFileObject CONFIG_CLASS_REQUIRE_HASH =
      JavaFileObjects
          .forSourceString("com.airbnb.epoxy.configtest.package-info", "@PackageEpoxyConfig(\n"
              + "    requireHashCode = true\n"
              + ")\n"
              + "package com.airbnb.epoxy.configtest;\n"
              + "\n"
              + "import com.airbnb.epoxy.PackageEpoxyConfig;");

  private static final JavaFileObject CONFIG_CLASS_REQUIRE_ABSTRACT =
      JavaFileObjects
          .forSourceString("com.airbnb.epoxy.configtest.package-info", "@PackageEpoxyConfig(\n"
              + "    requireAbstractModels = true\n"
              + ")\n"
              + "package com.airbnb.epoxy.configtest;\n"
              + "\n"
              + "import com.airbnb.epoxy.PackageEpoxyConfig;");

  @Test
  public void testSubPackageOverridesParent() {
    JavaFileObject subPackageConfig =
        JavaFileObjects.forSourceString("com.airbnb.epoxy.configtest.sub.package-info",
            "@PackageEpoxyConfig(\n"
                + "    requireHashCode = false\n"
                + ")\n"
                + "package com.airbnb.epoxy.configtest.sub;\n"
                + "\n"
                + "import com.airbnb.epoxy.PackageEpoxyConfig;");

    JavaFileObject model = JavaFileObjects
        .forResource("ModelConfigSubPackageOverridesParent.java");

    assert_().about(javaSources())
        .that(asList(CONFIG_CLASS_REQUIRE_HASH, model, subPackageConfig))
        .processedWith(new EpoxyProcessor())
        .compilesWithoutError();
  }

  @Test
  public void testPackageWithNoConfigInheritsNearestParentConfig() {
    JavaFileObject topLevelParentConfig = JavaFileObjects
        .forSourceString("com.airbnb.epoxy.configtest.package-info", "@PackageEpoxyConfig(\n"
            + "    requireHashCode = false\n"
            + ")\n"
            + "package com.airbnb.epoxy.configtest;\n"
            + "\n"
            + "import com.airbnb.epoxy.PackageEpoxyConfig;");

    JavaFileObject secondLevelParentConfig =
        JavaFileObjects.forSourceString("com.airbnb.epoxy.configtest.sub.package-info",
            "@PackageEpoxyConfig(\n"
                + "    requireHashCode = true\n"
                + ")\n"
                + "package com.airbnb.epoxy.configtest.sub;\n"
                + "\n"
                + "import com.airbnb.epoxy.PackageEpoxyConfig;");

    JavaFileObject model = JavaFileObjects
        .forResource("ModelPackageWithNoConfigInheritsNearestParentConfig.java");

    assert_().about(javaSources())
        .that(asList(topLevelParentConfig, secondLevelParentConfig, model))
        .processedWith(new EpoxyProcessor())
        .failsToCompile()
        .withErrorContaining("Attribute does not implement hashCode");
  }

  @Test
  public void testConfigRequireHashCode() {
    JavaFileObject model = JavaFileObjects
        .forResource("ModelRequiresHashCodeFailsBasicObject.java");

    assert_().about(javaSources())
        .that(asList(CONFIG_CLASS_REQUIRE_HASH, model))
        .processedWith(new EpoxyProcessor())
        .failsToCompile()
        .withErrorContaining("Attribute does not implement hashCode");
  }

  @Test
  public void testConfigRequireHashCodeIterableFails() {
    JavaFileObject model = JavaFileObjects
        .forResource("ModelRequiresHashCodeIterableFails.java");

    assert_().about(javaSources())
        .that(asList(CONFIG_CLASS_REQUIRE_HASH, model))
        .processedWith(new EpoxyProcessor())
        .failsToCompile()
        .withErrorContaining("Type in Iterable does not implement hashCode");
  }

  @Test
  public void testConfigRequireHashCodeIterablePasses() {
    JavaFileObject model = JavaFileObjects
        .forResource("ModelRequiresHashCodeIterableSucceeds.java");

    assert_().about(javaSources())
        .that(asList(CONFIG_CLASS_REQUIRE_HASH, model))
        .processedWith(new EpoxyProcessor())
        .compilesWithoutError();
  }

  @Test
  public void testConfigRequireHashCodeArrayFails() {
    JavaFileObject model = JavaFileObjects
        .forResource("ModelRequiresHashCodeArrayFails.java");

    assert_().about(javaSources())
        .that(asList(CONFIG_CLASS_REQUIRE_HASH, model))
        .processedWith(new EpoxyProcessor())
        .failsToCompile()
        .withErrorContaining("Type in array does not implement hashCode");
  }

  @Test
  public void testConfigRequireHashCodeArrayPasses() {
    JavaFileObject model = JavaFileObjects
        .forResource("ModelRequiresHashCodeArraySucceeds.java");

    assert_().about(javaSources())
        .that(asList(CONFIG_CLASS_REQUIRE_HASH, model))
        .processedWith(new EpoxyProcessor())
        .compilesWithoutError();
  }

  @Test
  public void testConfigRequireHashCodeEnumAttributePasses() {
    // Verify that enum attributes pass the hashcode requirement
    JavaFileObject model = JavaFileObjects
        .forResource("ModelRequiresHashCodeEnumPasses.java");

    assert_().about(javaSources())
        .that(asList(CONFIG_CLASS_REQUIRE_HASH, model))
        .processedWith(new EpoxyProcessor())
        .compilesWithoutError();
  }

  @Test
  public void testConfigRequireHashCodeCharSequencePasses() {
    // Verify that AutoValue class attributes pass the hashcode requirement
    JavaFileObject model = JavaFileObjects
        .forResource("ModelConfigRequireHashCodeCharSequencePasses.java");

    assert_().about(javaSources())
        .that(asList(CONFIG_CLASS_REQUIRE_HASH, model))
        .processedWith(new EpoxyProcessor())
        .compilesWithoutError();
  }

  @Test
  public void testConfigRequireHashCodeInterfaceWithHashCodePasses() {
    // Verify that AutoValue class attributes pass the hashcode requirement
    JavaFileObject model = JavaFileObjects
        .forResource("ModelConfigRequireHashCodeInterfaceWithHashCodePasses.java");

    assert_().about(javaSources())
        .that(asList(CONFIG_CLASS_REQUIRE_HASH, model))
        .processedWith(new EpoxyProcessor())
        .compilesWithoutError();
  }

  @Test
  public void testConfigRequireAbstractModelPassesClassWithAttribute() {
    // Verify that AutoValue class attributes pass the hashcode requirement
    JavaFileObject model = JavaFileObjects
        .forResource("RequireAbstractModelPassesClassWithAttribute.java");

    assert_().about(javaSources())
        .that(asList(CONFIG_CLASS_REQUIRE_ABSTRACT, model))
        .processedWith(new EpoxyProcessor())
        .compilesWithoutError();
  }

  @Test
  public void testConfigRequireAbstractModelFailsClassWithAttribute() {
    // Verify that AutoValue class attributes pass the hashcode requirement
    JavaFileObject model = JavaFileObjects
        .forResource("RequireAbstractModelFailsClassWithAttribute.java");

    assert_().about(javaSources())
        .that(asList(CONFIG_CLASS_REQUIRE_ABSTRACT, model))
        .processedWith(new EpoxyProcessor())
        .failsToCompile()
        .withErrorContaining(
            "Epoxy model class must be abstract (RequireAbstractModelFailsClassWithAttribute)");
  }

  @Test
  public void testConfigRequireAbstractModelPassesEpoxyModelClass() {
    // Verify that AutoValue class attributes pass the hashcode requirement
    JavaFileObject model = JavaFileObjects
        .forResource("RequireAbstractModelPassesEpoxyModelClass.java");

    assert_().about(javaSources())
        .that(asList(CONFIG_CLASS_REQUIRE_ABSTRACT, model))
        .processedWith(new EpoxyProcessor())
        .compilesWithoutError();
  }

  @Test
  public void testConfigRequireAbstractModelFailsEpoxyModelClass() {
    // Verify that AutoValue class attributes pass the hashcode requirement
    JavaFileObject model = JavaFileObjects
        .forResource("RequireAbstractModelFailsEpoxyModelClass.java");

    assert_().about(javaSources())
        .that(asList(CONFIG_CLASS_REQUIRE_ABSTRACT, model))
        .processedWith(new EpoxyProcessor())
        .failsToCompile()
        .withErrorContaining(
            "Epoxy model class must be abstract (RequireAbstractModelFailsEpoxyModelClass)");
  }
}
