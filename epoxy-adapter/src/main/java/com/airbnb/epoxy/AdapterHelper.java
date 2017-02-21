package com.airbnb.epoxy;

/**
 * A helper class for {@link com.airbnb.epoxy.DiffAdapter} to handle {@link
 * com.airbnb.epoxy.AutoModel} models. This is only implemented by the generated classes created the
 * annotation processor.
 */
public abstract class AdapterHelper<T extends DiffAdapter> {
  public abstract void buildAutoModels(T adapter);
}
