package com.airbnb.epoxy;

import com.google.testing.compile.JavaFileObjects;

import org.junit.Test;

import javax.tools.JavaFileObject;

import static com.google.common.truth.Truth.assert_;
import static com.google.testing.compile.JavaFileObjects.forResource;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;
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

    JavaFileObject model =
        forResource("ModelConfigSubPackageOverridesParent.java");

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

    JavaFileObject model =
        forResource("ModelPackageWithNoConfigInheritsNearestParentConfig.java");

    assert_().about(javaSources())
        .that(asList(topLevelParentConfig, secondLevelParentConfig, model))
        .processedWith(new EpoxyProcessor())
        .failsToCompile()
        .withErrorContaining("Attribute does not implement hashCode");
  }

  @Test
  public void testConfigRequireHashCode() {
    JavaFileObject model =
        forResource("ModelRequiresHashCodeFailsBasicObject.java");

    assert_().about(javaSources())
        .that(asList(CONFIG_CLASS_REQUIRE_HASH, model))
        .processedWith(new EpoxyProcessor())
        .failsToCompile()
        .withErrorContaining("Attribute does not implement hashCode");
  }

  @Test
  public void testConfigRequireEquals() {
    JavaFileObject model =
        forResource("ModelRequiresEqualsFailsBasicObject.java");

    assert_().about(javaSources())
        .that(asList(CONFIG_CLASS_REQUIRE_HASH, model))
        .processedWith(new EpoxyProcessor())
        .failsToCompile()
        .withErrorContaining("Attribute does not implement equals");
  }

  @Test
  public void testConfigRequireHashCodeIterableFails() {
    JavaFileObject model =
        forResource("ModelRequiresHashCodeIterableFails.java");

    assert_().about(javaSources())
        .that(asList(CONFIG_CLASS_REQUIRE_HASH, model))
        .processedWith(new EpoxyProcessor())
        .failsToCompile()
        .withErrorContaining("Type in Iterable does not implement hashCode");
  }

  @Test
  public void testConfigRequireHashCodeIterablePasses() {
    JavaFileObject model =
        forResource("ModelRequiresHashCodeIterableSucceeds.java");

    assert_().about(javaSources())
        .that(asList(CONFIG_CLASS_REQUIRE_HASH, model))
        .processedWith(new EpoxyProcessor())
        .compilesWithoutError();
  }

  @Test
  public void testConfigRequireHashCodeArrayFails() {
    JavaFileObject model =
        forResource("ModelRequiresHashCodeArrayFails.java");

    assert_().about(javaSources())
        .that(asList(CONFIG_CLASS_REQUIRE_HASH, model))
        .processedWith(new EpoxyProcessor())
        .failsToCompile()
        .withErrorContaining("Type in array does not implement hashCode");
  }

  @Test
  public void testConfigRequireHashCodeArrayPasses() {
    JavaFileObject model =
        forResource("ModelRequiresHashCodeArraySucceeds.java");

    assert_().about(javaSources())
        .that(asList(CONFIG_CLASS_REQUIRE_HASH, model))
        .processedWith(new EpoxyProcessor())
        .compilesWithoutError();
  }

  @Test
  public void testConfigRequireHashCodeEnumAttributePasses() {
    // Verify that enum attributes pass the hashcode requirement
    JavaFileObject model =
        forResource("ModelRequiresHashCodeEnumPasses.java");

    assert_().about(javaSources())
        .that(asList(CONFIG_CLASS_REQUIRE_HASH, model))
        .processedWith(new EpoxyProcessor())
        .compilesWithoutError();
  }

  @Test
  public void testConfigRequireHashCodeCharSequencePasses() {
    // Verify that AutoValue class attributes pass the hashcode requirement
    JavaFileObject model =
        forResource("ModelConfigRequireHashCodeCharSequencePasses.java");

    assert_().about(javaSources())
        .that(asList(CONFIG_CLASS_REQUIRE_HASH, model))
        .processedWith(new EpoxyProcessor())
        .compilesWithoutError();
  }

  @Test
  public void testConfigRequireHashCodeInterfaceWithHashCodePasses() {
    // Verify that AutoValue class attributes pass the hashcode requirement
    JavaFileObject model =
        forResource("ModelConfigRequireHashCodeInterfaceWithHashCodePasses.java");

    assert_().about(javaSources())
        .that(asList(CONFIG_CLASS_REQUIRE_HASH, model))
        .processedWith(new EpoxyProcessor())
        .compilesWithoutError();
  }

  @Test
  public void testConfigRequireHashCodeAutoValueAttributePasses() {
    // Verify that AutoValue class attributes pass the hashcode requirement
    JavaFileObject model = JavaFileObjects
        .forResource("ModelRequiresHashCodeAutoValueClassPasses.java");

    assert_().about(javaSources())
        .that(asList(CONFIG_CLASS_REQUIRE_HASH, model))
        .processedWith(new EpoxyProcessor())
        .compilesWithoutError();
  }

  @Test
  public void testConfigRequireHashCodeAllowsMarkedAttributes() {
    // Verify that AutoValue class attributes pass the hashcode requirement
    JavaFileObject model = JavaFileObjects
        .forResource("ModelConfigRequireHashCodeAllowsMarkedAttributes.java");

    assert_().about(javaSources())
        .that(asList(CONFIG_CLASS_REQUIRE_HASH, model))
        .processedWith(new EpoxyProcessor())
        .compilesWithoutError();
  }

  @Test
  public void testConfigRequireAbstractModelPassesClassWithAttribute() {
    // Verify that AutoValue class attributes pass the hashcode requirement. Only works for
    // classes in the module since AutoValue has a retention of Source so it is discarded after
    // compilation
    JavaFileObject model =
        forResource("RequireAbstractModelPassesClassWithAttribute.java");

    assert_().about(javaSources())
        .that(asList(CONFIG_CLASS_REQUIRE_ABSTRACT, model))
        .processedWith(new EpoxyProcessor())
        .compilesWithoutError();
  }

  @Test
  public void testConfigRequireAbstractModelFailsClassWithAttribute() {
    // Verify that AutoValue class attributes pass the hashcode requirement
    JavaFileObject model =
        forResource("RequireAbstractModelFailsClassWithAttribute.java");

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
    JavaFileObject model =
        forResource("RequireAbstractModelPassesEpoxyModelClass.java");

    assert_().about(javaSources())
        .that(asList(CONFIG_CLASS_REQUIRE_ABSTRACT, model))
        .processedWith(new EpoxyProcessor())
        .compilesWithoutError();
  }

  @Test
  public void testConfigRequireAbstractModelFailsEpoxyModelClass() {
    // Verify that AutoValue class attributes pass the hashcode requirement
    JavaFileObject model =
        forResource("RequireAbstractModelFailsEpoxyModelClass.java");

    assert_().about(javaSources())
        .that(asList(CONFIG_CLASS_REQUIRE_ABSTRACT, model))
        .processedWith(new EpoxyProcessor())
        .failsToCompile()
        .withErrorContaining(
            "Epoxy model class must be abstract (RequireAbstractModelFailsEpoxyModelClass)");
  }

  @Test
  public void testConfigNoModelValidation() {
    JavaFileObject model =
        forResource("ModelNoValidation.java");

    JavaFileObject generatedModel = JavaFileObjects.forResource("ModelNoValidation_.java");

    assert_().about(javaSource())
        .that(model)
        .processedWith(EpoxyProcessor.withNoValidation())
        .compilesWithoutError()
        .and()
        .generatesSources(generatedModel);
  }
}
