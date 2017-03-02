package com.airbnb.epoxy;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager.SpanSizeLookup;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.Collections;
import java.util.List;

abstract class BaseEpoxyAdapter extends RecyclerView.Adapter<EpoxyViewHolder> {
  private static final String SAVED_STATE_ARG_VIEW_HOLDERS = "saved_state_view_holders";

  private int spanCount = 1;
  private final HiddenEpoxyModel hiddenModel = new HiddenEpoxyModel();
  /**
   * Keeps track of view holders that are currently bound so we can save their state in {@link
   * #onSaveInstanceState(Bundle)}.
   */
  private final BoundViewHolders boundViewHolders = new BoundViewHolders();
  private ViewHolderState viewHolderState = new ViewHolderState();

  private final SpanSizeLookup spanSizeLookup = new SpanSizeLookup() {

    @Override
    public int getSpanSize(int position) {
      try {
        return getModelForPosition(position).getSpanSize(spanCount, position, getItemCount());
      } catch (IndexOutOfBoundsException e) {
        // There seems to be a GridLayoutManager bug where when the user is in accessibility mode
        // it incorrectly uses an outdated view position
        // when calling this method. This crashes when a view is animating out, when it is
        // removed from the adapter but technically still added
        // to the layout. We've posted a bug report and hopefully can update when the support
        // library fixes this
        // TODO: (eli_hart 8/23/16) Figure out if this has been fixed in new support library
        return 1;
      }
    }
  };

  public BaseEpoxyAdapter() {
    // Defaults to stable ids since view models generate unique ids. Set this to false in the
    // subclass if you don't want to support it
    setHasStableIds(true);
    spanSizeLookup.setSpanIndexCacheEnabled(true);
  }

  @Override
  public int getItemCount() {
    return getCurrentModels().size();
  }

  /** Return the models currently being used by the adapter to populate the recyclerview. */
  abstract List<EpoxyModel<?>> getCurrentModels();

  public boolean isEmpty() {
    return getCurrentModels().isEmpty();
  }

  @Override
  public EpoxyViewHolder onCreateViewHolder(ViewGroup parent, int layoutRes) {
    return new EpoxyViewHolder(parent, layoutRes);
  }

  @Override
  public void onBindViewHolder(EpoxyViewHolder holder, int position) {
    onBindViewHolder(holder, position, Collections.emptyList());
  }

  @Override
  public void onBindViewHolder(EpoxyViewHolder holder, int position, List<Object> payloads) {
    // A ViewHolder can be bound again even it is already bound and showing, like when it is on
    // screen and is changed. In this case we need
    // to carry the state of the previous view over to the new view. This may not be necessary if
    // the viewholder is reused (see RecyclerView.ItemAnimator#canReuseUpdatedViewHolder)
    // but we don't rely on that to be safe and to simplify
    EpoxyViewHolder boundViewHolder = boundViewHolders.get(holder);
    if (boundViewHolder != null) {
      viewHolderState.save(boundViewHolder);
    }

    EpoxyModel<?> modelToShow = getModelForPosition(position);
    holder.bind(modelToShow, payloads);

    viewHolderState.restore(holder);
    boundViewHolders.put(holder);

    onModelBound(holder, modelToShow, position, payloads);
  }

  /**
   * Called immediately after a model is bound to a view holder. Subclasses can override this if
   * they want alerts on when a model is bound.
   */
  protected void onModelBound(EpoxyViewHolder holder, EpoxyModel<?> model, int position,
      @Nullable List<Object> payloads) {
    onModelBound(holder, model, position);
  }

  /**
   * Called immediately after a model is bound to a view holder. Subclasses can override this if
   * they want alerts on when a model is bound.
   */
  protected void onModelBound(EpoxyViewHolder holder, EpoxyModel<?> model, int position) {

  }

  protected BoundViewHolders getBoundViewHolders() {
    return boundViewHolders;
  }

  @Override
  public int getItemViewType(int position) {
    return getModelForPosition(position).getLayout();
  }

  @Override
  public long getItemId(int position) {
    // This does not call getModelForPosition so that we don't use the id of the empty model when
    // hidden,
    // so that the id stays constant when gone vs shown
    return getCurrentModels().get(position).id();
  }

  private EpoxyModel<?> getModelForPosition(int position) {
    EpoxyModel<?> epoxyModel = getCurrentModels().get(position);
    return epoxyModel.isShown() ? epoxyModel : hiddenModel;
  }

  @Override
  public void onViewRecycled(EpoxyViewHolder holder) {
    viewHolderState.save(holder);
    boundViewHolders.remove(holder);

    EpoxyModel<?> model = holder.getModel();
    holder.unbind();
    onModelUnbound(holder, model);
  }

  /**
   * Called immediately after a model is unbound from a view holder. Subclasses can override this if
   * they want alerts on when a model is unbound.
   */
  protected void onModelUnbound(EpoxyViewHolder holder, EpoxyModel<?> model) {

  }

  @CallSuper
  @Override
  public boolean onFailedToRecycleView(EpoxyViewHolder holder) {
    //noinspection unchecked,rawtypes
    return ((EpoxyModel) holder.getModel()).onFailedToRecycleView(holder.objectToBind());
  }

  @CallSuper
  @Override
  public void onViewAttachedToWindow(EpoxyViewHolder holder) {
    //noinspection unchecked,rawtypes
    ((EpoxyModel) holder.getModel()).onViewAttachedToWindow(holder.objectToBind());
  }

  @CallSuper
  @Override
  public void onViewDetachedFromWindow(EpoxyViewHolder holder) {
    //noinspection unchecked,rawtypes
    ((EpoxyModel) holder.getModel()).onViewDetachedFromWindow(holder.objectToBind());
  }

  public void onSaveInstanceState(Bundle outState) {
    // Save the state of currently bound views first so they are included. Views that were
    // scrolled off and unbound will already have had
    // their state saved.
    for (EpoxyViewHolder holder : boundViewHolders) {
      viewHolderState.save(holder);
    }

    if (viewHolderState.size() > 0 && !hasStableIds()) {
      throw new IllegalStateException("Must have stable ids when saving view holder state");
    }

    outState.putParcelable(SAVED_STATE_ARG_VIEW_HOLDERS, viewHolderState);
  }

  public void onRestoreInstanceState(@Nullable Bundle inState) {
    // To simplify things we enforce that state is restored before views are bound, otherwise it
    // is more difficult to update view state once they are bound
    if (boundViewHolders.size() > 0) {
      throw new IllegalStateException(
          "State cannot be restored once views have been bound. It should be done before adding "
              + "the adapter to the recycler view.");
    }

    if (inState != null) {
      viewHolderState = inState.getParcelable(SAVED_STATE_ARG_VIEW_HOLDERS);
      if (viewHolderState == null) {
        throw new IllegalStateException(
            "Tried to restore instance state, but onSaveInstanceState was never called.");
      }
    }
  }

  /**
   * Finds the position of the given model in the list. Doesn't use indexOf to avoid unnecessary
   * equals() calls since we're looking for the same object instance.
   */
  protected int getModelPosition(EpoxyModel<?> model) {
    int size = getCurrentModels().size();
    for (int i = 0; i < size; i++) {
      if (model == getCurrentModels().get(i)) {
        return i;
      }
    }

    return -1;
  }

  /**
   * For use with a grid layout manager - use this to get the {@link SpanSizeLookup} for models in
   * this adapter. This will delegate span look up calls to each model's {@link
   * EpoxyModel#getSpanSize(int, int, int)}. Make sure to also call {@link #setSpanCount(int)} so
   * the span count is correct.
   */
  public SpanSizeLookup getSpanSizeLookup() {
    return spanSizeLookup;
  }

  /**
   * If you are using a grid layout manager you must call this to set the span count of the grid.
   * This span count will be passed on to the models so models can choose what span count to be.
   *
   * @see #getSpanSizeLookup()
   * @see EpoxyModel#getSpanSize(int, int, int)
   */
  public void setSpanCount(int spanCount) {
    this.spanCount = spanCount;
  }

  public int getSpanCount() {
    return spanCount;
  }

  public boolean isMultiSpan() {
    return spanCount > 1;
  }
}
