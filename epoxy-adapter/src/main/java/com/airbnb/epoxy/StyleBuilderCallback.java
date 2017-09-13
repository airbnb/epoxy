package com.airbnb.epoxy;

/**
 * Used for specifying dynamic styling for a view when creating a model. This is only used if the
 * view is set up to be styled with the Paris library.
 */
public interface StyleBuilderCallback<T> {
  void buildStyle(T builder);
}
