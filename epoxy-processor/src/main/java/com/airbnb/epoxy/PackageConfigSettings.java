package com.airbnb.epoxy;

/**
 * Stores configuration settings for a package.
 */
class PackageConfigSettings {

  final boolean requireHashCode;
  final boolean requireAbstractModels;
  final boolean validateModelUsage;

  private PackageConfigSettings(boolean requireHashCode, boolean requireAbstractModels,
      boolean validateModelUsage) {
    this.requireHashCode = requireHashCode;
    this.requireAbstractModels = requireAbstractModels;
    this.validateModelUsage = validateModelUsage;
  }

  static PackageConfigSettings forDefaults() {
    return new PackageConfigSettings(
        PackageEpoxyConfig.REQUIRE_HASHCODE_DEFAULT,
        PackageEpoxyConfig.REQUIRE_ABSTRACT_MODELS,
        PackageEpoxyConfig.VALIDATE_MODEL_USAGE
    );
  }

  static PackageConfigSettings create(PackageEpoxyConfig configAnnotation) {
    return new PackageConfigSettings(
        configAnnotation.requireHashCode(),
        configAnnotation.requireAbstractModels(),
        configAnnotation.validateModelUsage()
    );
  }
}
