
package com.airbnb.epoxy;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager.SpanSizeLookup;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Allows you to easily combine different view types in the same adapter, and handles view holder
 * creation, binding, and ids for you. Subclasses just need to add their desired {@link EpoxyModel}
 * objects and the rest is done automatically.
 * <p/>
 * {@link android.support.v7.widget.RecyclerView.Adapter#setHasStableIds(boolean)} is set to true by
 * default, since {@link EpoxyModel} makes it easy to support unique ids. If you don't want to
 * support this then disable it in your base class (not recommended).
 */
@SuppressWarnings("WeakerAccess")
public abstract class EpoxyAdapter extends RecyclerView.Adapter<EpoxyViewHolder> {
  private static final String SAVED_STATE_ARG_VIEW_HOLDERS = "saved_state_view_holders";

  /**
   * Subclasses should modify this list as necessary with the models they want to show. Subclasses
   * are responsible for notifying data changes whenever this list is changed.
   */
  protected final List<EpoxyModel<?>> models = new ArrayList<>();
  private int spanCount = 1;
  private final HiddenEpoxyModel hiddenModel = new HiddenEpoxyModel();
  /**
   * Keeps track of view holders that are currently bound so we can save their state in {@link
   * #onSaveInstanceState(Bundle)}.
   */
  private final BoundViewHolders boundViewHolders = new BoundViewHolders();
  private ViewHolderState viewHolderState = new ViewHolderState();
  private DiffHelper diffHelper;

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
        // TODO: (eli_hart 7/1/16) Get link to bug report. I think sean created one
        return 1;
      }
    }
  };

  public EpoxyAdapter() {
    // Defaults to stable ids since view models generate unique ids. Set this to false in the
    // subclass if you don't want to support it
    setHasStableIds(true);
    spanSizeLookup.setSpanIndexCacheEnabled(true);
  }

  /**
   * Enables support for automatically notifying model changes via {@link #notifyModelsChanged()}.
   * If used, this should be called in the constructor, before any models are changed.
   *
   * @see #notifyModelsChanged()
   */
  protected void enableDiffing() {
    if (diffHelper != null) {
      throw new IllegalStateException("Diffing was already enabled");
    }

    if (!models.isEmpty()) {
      throw new IllegalStateException("You must enable diffing before modifying models");
    }

    if (!hasStableIds()) {
      throw new IllegalStateException("You must have stable ids to use diffing");
    }

    diffHelper = new DiffHelper(this);
  }

  /**
   * Intelligently notify item changes by comparing the current {@link #models} list against the
   * previous so you don't have to micromanage notification calls yourself. This may be
   * prohibitively slow for large model lists (in the hundreds), in which case consider doing
   * notification calls yourself. If you use this, all your view models must implement {@link
   * EpoxyModel#hashCode()} to completely identify their state, so that changes to a model's content
   * can be detected. Before using this you must enable it with {@link #enableDiffing()}, since
   * keeping track of the model state adds extra computation time to all other data change
   * notifications.
   *
   * @see #enableDiffing()
   */

  public void notifyModelsChanged() {
    if (diffHelper == null) {
      throw new IllegalStateException("You must enable diffing before notifying models changed");
    }

    diffHelper.notifyModelChanges();
  }

  @Override
  public int getItemCount() {
    return models.size();
  }

  public boolean isEmpty() {
    return models.isEmpty();
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

    onModelBound(holder, modelToShow);
  }

  /**
   * Called immediately after a model is bound to a view holder. Subclasses can override this if
   * they want alerts on when a model is bound.
   */
  protected void onModelBound(EpoxyViewHolder holder, EpoxyModel<?> model) {

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
    return models.get(position).id();
  }

  private EpoxyModel<?> getModelForPosition(int position) {
    EpoxyModel<?> epoxyModel = models.get(position);
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
    }
  }

  /**
   * Notify that the given model has had its data changed. It should only be called if the model
   * retained the same position.
   */
  protected void notifyModelChanged(EpoxyModel<?> model) {
    int index = getModelPosition(model);
    if (index != -1) {
      notifyItemChanged(index);
    }
  }

  protected void addModels(EpoxyModel<?>... modelsToAdd) {
    int initialSize = models.size();
    Collections.addAll(models, modelsToAdd);
    notifyItemRangeInserted(initialSize, modelsToAdd.length);
  }

  protected void insertModelBefore(EpoxyModel<?> modelToInsert, EpoxyModel<?> modelToInsertBefore) {
    int targetIndex = getModelPosition(modelToInsertBefore);
    if (targetIndex == -1) {
      throw new IllegalStateException("Model is not added: " + modelToInsertBefore);
    }

    models.add(targetIndex, modelToInsert);
    notifyItemInserted(targetIndex);
  }

  protected void insertModelAfter(EpoxyModel<?> modelToInsert, EpoxyModel<?> modelToInsertAfter) {
    int modelIndex = getModelPosition(modelToInsertAfter);
    if (modelIndex == -1) {
      throw new IllegalStateException("Model is not added: " + modelToInsertAfter);
    }

    int targetIndex = modelIndex + 1;
    models.add(targetIndex, modelToInsert);
    notifyItemInserted(targetIndex);
  }

  /**
   * If the given model exists it is removed and an item removal is notified. Otherwise this does
   * nothing.
   */
  protected void removeModel(EpoxyModel<?> model) {
    int index = getModelPosition(model);
    if (index != -1) {
      models.remove(index);
      notifyItemRemoved(index);
    }
  }

  /**
   * Removes all models after the given model, which must have already been added. An example use
   * case is you want to keep a header but clear everything else, like in the case of refreshing
   * data.
   */
  protected void removeAllAfterModel(EpoxyModel<?> model) {
    List<EpoxyModel<?>> modelsToRemove = getAllModelsAfter(model);
    int numModelsRemoved = modelsToRemove.size();
    int initialModelCount = models.size();

    // This is a sublist, so clearing it will clear the models in the original list
    modelsToRemove.clear();

    notifyItemRangeRemoved(initialModelCount - numModelsRemoved, numModelsRemoved);
  }

  protected void showModel(EpoxyModel<?> model, boolean show) {
    if (model.isShown() == show) {
      return;
    }

    model.show(show);
    notifyModelChanged(model);
  }

  protected void showModel(EpoxyModel<?> model) {
    showModel(model, true);
  }

  protected void showModels(List<EpoxyModel<?>> epoxyModels) {
    for (EpoxyModel<?> epoxyModel : epoxyModels) {
      showModel(epoxyModel);
    }
  }

  protected void showModels(List<EpoxyModel<?>> epoxyModels, boolean show) {
    for (EpoxyModel<?> epoxyModel : epoxyModels) {
      showModel(epoxyModel, show);
    }
  }

  protected void hideModel(EpoxyModel<?> model) {
    showModel(model, false);
  }

  protected void hideModels(List<EpoxyModel<?>> epoxyModels) {
    for (EpoxyModel<?> epoxyModel : epoxyModels) {
      hideModel(epoxyModel);
    }
  }

  /**
   * Hides all models currently located after the given model.
   *
   * @param model the model after which to hide, must exist
   */
  protected void hideAllAfterModel(EpoxyModel<?> model) {
    hideModels(getAllModelsAfter(model));
  }

  /**
   * Returns a list of all items in {@link #models} that occur after the given model. Any changes to
   * the returned sublist will be reflected in the original {@link #models} list.
   *
   * @param model Must exists in {@link #models}.
   */
  private List<EpoxyModel<?>> getAllModelsAfter(EpoxyModel<?> model) {
    int index = getModelPosition(model);
    if (index == -1) {
      throw new IllegalStateException("Model is not added: " + model);
    }
    return models.subList(index + 1, models.size());
  }

  /**
   * Finds the position of the given model in the list. Doesn't use indexOf to avoid unnecessary
   * equals() calls since we're looking for the same object instance.
   */
  private int getModelPosition(EpoxyModel<?> model) {
    int size = models.size();
    for (int i = 0; i < size; i++) {
      if (model == models.get(i)) {
        return i;
      }
    }

    return -1;
  }

  public SpanSizeLookup getSpanSizeLookup() {
    return spanSizeLookup;
  }

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
