package com.airbnb.epoxy;

/**
 * Stores configuration settings for a package.
 */
class PackageConfigSettings {

  final boolean requireHashCode;
  final boolean requireAbstractModels;
  final boolean implicitlyAddAutoModels;

  private PackageConfigSettings(boolean requireHashCode, boolean requireAbstractModels,
      boolean implicitlyAddAutoModels) {
    this.requireHashCode = requireHashCode;
    this.requireAbstractModels = requireAbstractModels;
    this.implicitlyAddAutoModels = implicitlyAddAutoModels;
  }

  static PackageConfigSettings forDefaults() {
    return new PackageConfigSettings(
        PackageEpoxyConfig.REQUIRE_HASHCODE_DEFAULT,
        PackageEpoxyConfig.REQUIRE_ABSTRACT_MODELS_DEFAULT,
        PackageEpoxyConfig.IMPLICITLY_ADD_AUTO_MODELS_DEFAULT
    );
  }

  static PackageConfigSettings create(PackageEpoxyConfig configAnnotation) {
    return new PackageConfigSettings(
        configAnnotation.requireHashCode(),
        configAnnotation.requireAbstractModels(),
        configAnnotation.implicitlyAddAutoModels());
  }
}
