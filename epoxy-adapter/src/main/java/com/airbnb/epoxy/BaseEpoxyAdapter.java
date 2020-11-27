package com.airbnb.epoxy;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.airbnb.epoxy.stickyheader.StickyHeaderCallbacks;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup;
import androidx.recyclerview.widget.RecyclerView;

public abstract class BaseEpoxyAdapter
    extends RecyclerView.Adapter<EpoxyViewHolder>
    implements StickyHeaderCallbacks {

  private static final String SAVED_STATE_ARG_VIEW_HOLDERS = "saved_state_view_holders";

  private int spanCount = 1;

  private final ViewTypeManager viewTypeManager = new ViewTypeManager();
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
        return getModelForPosition(position)
            .spanSize(spanCount, position, getItemCount());
      } catch (IndexOutOfBoundsException e) {
        // There seems to be a GridLayoutManager bug where when the user is in accessibility mode
        // it incorrectly uses an outdated view position
        // when calling this method. This crashes when a view is animating out, when it is
        // removed from the adapter but technically still added
        // to the layout. We've posted a bug report and hopefully can update when the support
        // library fixes this
        // TODO: (eli_hart 8/23/16) Figure out if this has been fixed in new support library
        onExceptionSwallowed(e);
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

  /**
   * This is called when recoverable exceptions happen at runtime. They can be ignored and Epoxy
   * will recover, but you can override this to be aware of when they happen.
   */
  protected void onExceptionSwallowed(RuntimeException exception) {

  }

  @Override
  public int getItemCount() {
    return getCurrentModels().size();
  }

  /** Return the models currently being used by the adapter to populate the recyclerview. */
  abstract List<? extends EpoxyModel<?>> getCurrentModels();

  public boolean isEmpty() {
    return getCurrentModels().isEmpty();
  }

  @Override
  public long getItemId(int position) {
    // This does not call getModelForPosition so that we don't use the id of the empty model when
    // hidden,
    // so that the id stays constant when gone vs shown
    return getCurrentModels().get(position).id();
  }

  @Override
  public int getItemViewType(int position) {
    return viewTypeManager.getViewTypeAndRememberModel(getModelForPosition(position));
  }

  @Override
  public EpoxyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    EpoxyModel<?> model = viewTypeManager.getModelForViewType(this, viewType);
    View view = model.buildView(parent);
    return new EpoxyViewHolder(parent, view, model.shouldSaveViewState());
  }

  @Override
  public void onBindViewHolder(EpoxyViewHolder holder, int position) {
    onBindViewHolder(holder, position, Collections.emptyList());
  }

  @Override
  public void onBindViewHolder(EpoxyViewHolder holder, int position, List<Object> payloads) {
    EpoxyModel<?> modelToShow = getModelForPosition(position);

    EpoxyModel<?> previouslyBoundModel = null;
    if (diffPayloadsEnabled()) {
      previouslyBoundModel = DiffPayload.getModelFromPayload(payloads, getItemId(position));
    }

    holder.bind(modelToShow, previouslyBoundModel, payloads, position);

    if (payloads.isEmpty()) {
      // We only apply saved state to the view on initial bind, not on model updates.
      // Since view state should be independent of model props, we should not need to apply state
      // again in this case. This simplifies a rebind on update
      viewHolderState.restore(holder);
    }

    boundViewHolders.put(holder);

    if (diffPayloadsEnabled()) {
      onModelBound(holder, modelToShow, position, previouslyBoundModel);
    } else {
      onModelBound(holder, modelToShow, position, payloads);
    }
  }

  boolean diffPayloadsEnabled() {
    return false;
  }

  /**
   * Called immediately after a model is bound to a view holder. Subclasses can override this if
   * they want alerts on when a model is bound.
   */
  protected void onModelBound(EpoxyViewHolder holder, EpoxyModel<?> model, int position,
      @Nullable List<Object> payloads) {
    onModelBound(holder, model, position);
  }

  void onModelBound(EpoxyViewHolder holder, EpoxyModel<?> model, int position,
      @Nullable EpoxyModel<?> previouslyBoundModel) {
    onModelBound(holder, model, position);
  }

  /**
   * Called immediately after a model is bound to a view holder. Subclasses can override this if
   * they want alerts on when a model is bound.
   */
  protected void onModelBound(EpoxyViewHolder holder, EpoxyModel<?> model, int position) {

  }

  /**
   * Returns an object that manages the view holders currently bound to the RecyclerView. This
   * object is mainly used by the base Epoxy adapter to save view states, but you may find it useful
   * to help access views or models currently shown in the RecyclerView.
   */
  protected BoundViewHolders getBoundViewHolders() {
    return boundViewHolders;
  }

  EpoxyModel<?> getModelForPosition(int position) {
    return getCurrentModels().get(position);
  }

  @Override
  public void onViewRecycled(EpoxyViewHolder holder) {
    viewHolderState.save(holder);
    boundViewHolders.remove(holder);

    EpoxyModel<?> model = holder.getModel();
    holder.unbind();
    onModelUnbound(holder, model);
  }

  @CallSuper
  @Override
  public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
    // The last model is saved for optimization, but holding onto it can leak anything saved inside
    // the model (like a click listener that references a Fragment). This is only needed during
    // the viewholder creation phase, so it is safe to clear now.
    viewTypeManager.lastModelForViewTypeLookup = null;
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
   *
   * @return The position of the given model in the current models list, or -1 if the model can't be
   * found.
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

  //region Sticky header

  /**
   * Optional callback to setup the sticky view,
   * by default it doesn't do anything.
   * <p>
   * The sub-classes should override the function if they are
   * using sticky header feature.
   */
  @Override
  public void setupStickyHeaderView(@NotNull View stickyHeader) {
    // no-op
  }

  /**
   * Optional callback to perform tear down operation on the
   * sticky view, by default it doesn't do anything.
   * <p>
   * The sub-classes should override the function if they are
   * using sticky header feature.
   */
  @Override
  public void teardownStickyHeaderView(@NotNull View stickyHeader) {
    // no-op
  }

  /**
   * Called to check if the item at the position is a sticky item,
   * by default returns false.
   * <p>
   * The sub-classes should override the function if they are
   * using sticky header feature.
   */
  @Override
  public boolean isStickyHeader(int position) {
    return false;
  }

  //endregion
}
