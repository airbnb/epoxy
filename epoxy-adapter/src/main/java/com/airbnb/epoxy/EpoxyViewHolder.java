
package com.airbnb.epoxy;

import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

@SuppressWarnings("WeakerAccess")
public class EpoxyViewHolder extends RecyclerView.ViewHolder {
  @SuppressWarnings("rawtypes") private EpoxyModel epoxyModel;
  private List<Object> payloads;
  private EpoxyHolder epoxyHolder;

  public EpoxyViewHolder(ViewGroup parent, @LayoutRes int layoutId) {
    super(LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false));
  }

  public void bind(@SuppressWarnings("rawtypes") EpoxyModel model, List<Object> payloads) {
    this.payloads = payloads;

    if (epoxyHolder == null && model instanceof EpoxyModelWithHolder) {
      epoxyHolder = ((EpoxyModelWithHolder) model).createNewHolder();
      epoxyHolder.bindView(itemView);
    }

    if (payloads.isEmpty()) {
      // noinspection unchecked
      model.bind(objectToBind());
    } else {
      // noinspection unchecked
      model.bind(objectToBind(), payloads);
    }

    epoxyModel = model;
  }

  Object objectToBind() {
    return epoxyHolder != null ? epoxyHolder : itemView;
  }

  public void unbind() {
    assertBound();
    // noinspection unchecked
    epoxyModel.unbind(objectToBind());
    epoxyModel = null;
    payloads = null;
  }

  public List<Object> getPayloads() {
    assertBound();
    return payloads;
  }

  public EpoxyModel<?> getModel() {
    assertBound();
    return epoxyModel;
  }

  private void assertBound() {
    if (epoxyModel == null) {
      throw new IllegalStateException("This holder is not currently bound.");
    }
  }

  @Override
  public String toString() {
    return "EpoxyViewHolder{"
        + "epoxyModel=" + epoxyModel
        + ", view=" + itemView
        + ", super=" + super.toString()
        + '}';
  }
}
