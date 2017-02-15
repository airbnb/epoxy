package com.airbnb.epoxy;

/**
 * Stores configuration settings for a package.
 */
class PackageConfigSettings {

  final boolean requireHashCode;
  final boolean requireAbstractModels;

  private PackageConfigSettings(boolean requireHashCode, boolean requireAbstractModels) {
    this.requireHashCode = requireHashCode;
    this.requireAbstractModels = requireAbstractModels;
  }

  static PackageConfigSettings forDefaults() {
    return new PackageConfigSettings(
        PackageEpoxyConfig.REQUIRE_HASHCODE_DEFAULT,
        PackageEpoxyConfig.REQUIRE_ABSTRACT_MODELS
    );
  }

  static PackageConfigSettings create(PackageEpoxyConfig configAnnotation) {
    return new PackageConfigSettings(
        configAnnotation.requireHashCode(),
        configAnnotation.requireAbstractModels()
    );
  }
}
