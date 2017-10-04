package com.airbnb.epoxy.paging;

import com.airbnb.epoxy.EpoxyModel;

import java.util.List;

public abstract class SimplePagingEpoxyController<T> extends PagingEpoxyController<T> {
  @Override
  protected final void buildModels(List<T> list) {
    for (T item : list) {
      add(buildModel(item));
    }
  }

  protected abstract EpoxyModel<?> buildModel(T item);
}
