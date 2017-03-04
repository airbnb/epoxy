package com.airbnb.epoxy;

/**
 * A {@link com.airbnb.epoxy.AdapterHelper} implementation for adapters with no {@link
 * com.airbnb.epoxy.AutoModel} usage.
 */
class NoOpAdapterHelper extends AdapterHelper<AutoEpoxyAdapter> {

  @Override
  public void resetAutoModels() {
    // No - Op
  }
}
