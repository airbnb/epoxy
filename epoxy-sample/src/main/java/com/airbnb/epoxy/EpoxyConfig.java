package com.airbnb.epoxy;

@ModuleEpoxyConfig(
    requireAbstractModels = true,
    requireHashCode = true
)
public final class EpoxyConfig {
  private EpoxyConfig() {
  }
}
