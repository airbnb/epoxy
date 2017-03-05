package com.airbnb.epoxy;

/**
 * Stores configuration settings for a package.
 */
class PackageConfigSettings {

  final boolean requireHashCode;
  final boolean requireAbstractModels;
  final boolean validateAutoModelUsage;

  private PackageConfigSettings(boolean requireHashCode, boolean requireAbstractModels,
      boolean validateAutoModelUsage) {
    this.requireHashCode = requireHashCode;
    this.requireAbstractModels = requireAbstractModels;
    this.validateAutoModelUsage = validateAutoModelUsage;
  }

  static PackageConfigSettings forDefaults() {
    return new PackageConfigSettings(
        PackageEpoxyConfig.REQUIRE_HASHCODE_DEFAULT,
        PackageEpoxyConfig.REQUIRE_ABSTRACT_MODELS,
        PackageEpoxyConfig.VALIDATE_AUTO_MODEL_USAGE
    );
  }

  static PackageConfigSettings create(PackageEpoxyConfig configAnnotation) {
    return new PackageConfigSettings(
        configAnnotation.requireHashCode(),
        configAnnotation.requireAbstractModels(),
        configAnnotation.validateAutoModelUsage()
    );
  }
}
