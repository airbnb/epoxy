
package com.airbnb.epoxy;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.airbnb.epoxy.ViewHolderState.ViewState;

import java.util.List;

@SuppressWarnings("WeakerAccess")
public class EpoxyViewHolder extends RecyclerView.ViewHolder {
  @SuppressWarnings("rawtypes") private EpoxyModel epoxyModel;
  private List<Object> payloads;
  private EpoxyHolder epoxyHolder;
  @Nullable ViewHolderState.ViewState initialViewState;

  public EpoxyViewHolder(View view, boolean saveInitialState) {
    super(view);

    if (saveInitialState) {
      // We save the initial state of the view when it is created so that we can reset this initial
      // state before a model is bound for the first time. Otherwise the view may carry over
      // state from a previously bound view.
      initialViewState = new ViewState();
      initialViewState.save(itemView);
    }
  }

  void restoreInitialViewState() {
    if (initialViewState != null) {
      initialViewState.restore(itemView);
    }
  }

  public void bind(@SuppressWarnings("rawtypes") EpoxyModel model,
      @Nullable EpoxyModel<?> previouslyBoundModel, List<Object> payloads, int position) {
    this.payloads = payloads;

    if (epoxyHolder == null && model instanceof EpoxyModelWithHolder) {
      epoxyHolder = ((EpoxyModelWithHolder) model).createNewHolder();
      epoxyHolder.bindView(itemView);
    }

    if (model instanceof GeneratedModel) {
      // The generated method will enforce that only a properly typed listener can be set
      //noinspection unchecked
      ((GeneratedModel) model).handlePreBind(this, objectToBind(), position);
    }

    if (previouslyBoundModel != null) {
      // noinspection unchecked
      model.bind(objectToBind(), previouslyBoundModel);
    } else if (payloads.isEmpty()) {
      // noinspection unchecked
      model.bind(objectToBind());
    } else {
      // noinspection unchecked
      model.bind(objectToBind(), payloads);
    }

    if (model instanceof GeneratedModel) {
      // The generated method will enforce that only a properly typed listener can be set
      //noinspection unchecked
      ((GeneratedModel) model).handlePostBind(objectToBind(), position);
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
