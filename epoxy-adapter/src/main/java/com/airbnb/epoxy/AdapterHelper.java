package com.airbnb.epoxy;

/**
 * A helper class for {@link AutoEpoxyAdapter} to handle {@link
 * com.airbnb.epoxy.AutoModel} models. This is only implemented by the generated classes created the
 * annotation processor.
 */
public abstract class AdapterHelper<T extends AutoEpoxyAdapter> {
  public abstract void resetAutoModels();
}
