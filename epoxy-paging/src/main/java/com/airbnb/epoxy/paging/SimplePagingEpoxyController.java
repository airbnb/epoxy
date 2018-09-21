package com.airbnb.epoxy.paging;

import com.airbnb.epoxy.EpoxyModel;

import java.util.List;

/**
 * Use this version of {@link PagingEpoxyController} if you don't have any other model types to
 * build (eg header, footer, etc). This will build one model for each item in the list by calling
 * {@link #buildModel(Object)}
 *
 * @param <T> The list type
 *
 * @deprecated Use {@link PagedListEpoxyController} instead.
 */
public abstract class SimplePagingEpoxyController<T> extends PagingEpoxyController<T> {
  @Override
  protected final void buildModels(List<T> list) {
    for (T item : list) {
      add(buildModel(item));
    }
  }

  /** Create and return a new model instance representing the given item. */
  protected abstract EpoxyModel<?> buildModel(T item);
}
