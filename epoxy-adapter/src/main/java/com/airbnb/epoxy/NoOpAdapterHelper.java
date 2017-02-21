package com.airbnb.epoxy;

/**
 * A {@link com.airbnb.epoxy.AdapterHelper} implementation for adapters with no {@link
 * com.airbnb.epoxy.AutoModel} usage.
 */
class NoOpAdapterHelper extends AdapterHelper<DiffAdapter> {
  @Override
  public void buildAutoModels(DiffAdapter adapter) {
    // No - Op
  }
}
