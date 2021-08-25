package com.airbnb.epoxy;

import android.view.View;
import android.view.ViewParent;

import com.airbnb.epoxy.ViewHolderState.ViewState;
import com.airbnb.epoxy.VisibilityState.Visibility;

import java.util.List;

import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;
import androidx.recyclerview.widget.RecyclerView;

@SuppressWarnings("WeakerAccess")
public class EpoxyViewHolder extends RecyclerView.ViewHolder {
  @SuppressWarnings("rawtypes") private EpoxyModel epoxyModel;
  private List<Object> payloads;
  private EpoxyHolder epoxyHolder;
  @Nullable ViewHolderState.ViewState initialViewState;

  // Once the EpoxyHolder is created parent will be set to null.
  private ViewParent parent;

  public EpoxyViewHolder(ViewParent parent, View view, boolean saveInitialState) {
    super(view);

    this.parent = parent;
    if (saveInitialState) {
      // We save the initial state of the view when it is created so that we can reset this initial
      // state before a model is bound for the first time. Otherwise the view may carry over
      // state from a previously bound model.
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
      epoxyHolder = ((EpoxyModelWithHolder) model).createNewHolder(parent);
      epoxyHolder.bindView(itemView);
    }
    // Safe to set to null as it is only used for createNewHolder method
    parent = null;

    if (model instanceof GeneratedModel) {
      // The generated method will enforce that only a properly typed listener can be set
      //noinspection unchecked
      ((GeneratedModel) model).handlePreBind(this, objectToBind(), position);
    }

    // noinspection unchecked
    model.preBind(objectToBind(), previouslyBoundModel);

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

  @NonNull
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

  public void visibilityStateChanged(@Visibility int visibilityState) {
    assertBound();
    // noinspection unchecked
    epoxyModel.onVisibilityStateChanged(visibilityState, objectToBind());
  }

  public void visibilityChanged(
      @FloatRange(from = 0.0f, to = 100.0f) float percentVisibleHeight,
      @FloatRange(from = 0.0f, to = 100.0f) float percentVisibleWidth,
      @Px int visibleHeight,
      @Px int visibleWidth
  ) {
    assertBound();
    // noinspection unchecked
    epoxyModel.onVisibilityChanged(percentVisibleHeight, percentVisibleWidth, visibleHeight,
        visibleWidth, objectToBind());
  }

  public List<Object> getPayloads() {
    assertBound();
    return payloads;
  }

  public EpoxyModel<?> getModel() {
    assertBound();
    return epoxyModel;
  }

  public EpoxyHolder getHolder() {
    assertBound();
    return epoxyHolder;
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
