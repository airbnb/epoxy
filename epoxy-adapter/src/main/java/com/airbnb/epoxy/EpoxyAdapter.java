
package com.airbnb.epoxy;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import androidx.annotation.Nullable;

/**
 * Allows you to easily combine different view types in the same adapter, and handles view holder
 * creation, binding, and ids for you. Subclasses just need to add their desired {@link EpoxyModel}
 * objects and the rest is done automatically.
 * <p/>
 * {@link androidx.recyclerview.widget.RecyclerView.Adapter#setHasStableIds(boolean)} is set to true
 * by default, since {@link EpoxyModel} makes it easy to support unique ids. If you don't want to
 * support this then disable it in your base class (not recommended).
 */
@SuppressWarnings("WeakerAccess")
public abstract class EpoxyAdapter extends BaseEpoxyAdapter {
  private final HiddenEpoxyModel hiddenModel = new HiddenEpoxyModel();

  /**
   * Subclasses should modify this list as necessary with the models they want to show. Subclasses
   * are responsible for notifying data changes whenever this list is changed.
   */
  protected final List<EpoxyModel<?>> models = new ModelList();
  private DiffHelper diffHelper;

  @Override
  List<EpoxyModel<?>> getCurrentModels() {
    return models;
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

    diffHelper = new DiffHelper(this, false);
  }

  @Override
  EpoxyModel<?> getModelForPosition(int position) {
    EpoxyModel<?> model = models.get(position);
    return model.isShown() ? model : hiddenModel;
  }

  /**
   * Intelligently notify item changes by comparing the current {@link #models} list against the
   * previous so you don't have to micromanage notification calls yourself. This may be
   * prohibitively slow for large model lists (in the hundreds), in which case consider doing
   * notification calls yourself. If you use this, all your view models must implement {@link
   * EpoxyModel#hashCode()} and {@link EpoxyModel#equals(Object)} to completely identify their
   * state, so that changes to a model's content can be detected. Before using this you must enable
   * it with {@link #enableDiffing()}, since keeping track of the model state adds extra computation
   * time to all other data change notifications.
   *
   * @see #enableDiffing()
   */

  protected void notifyModelsChanged() {
    if (diffHelper == null) {
      throw new IllegalStateException("You must enable diffing before notifying models changed");
    }

    diffHelper.notifyModelChanges();
  }

  /**
   * Notify that the given model has had its data changed. It should only be called if the model
   * retained the same position.
   */
  protected void notifyModelChanged(EpoxyModel<?> model) {
    notifyModelChanged(model, null);
  }

  /**
   * Notify that the given model has had its data changed. It should only be called if the model
   * retained the same position.
   */
  protected void notifyModelChanged(EpoxyModel<?> model, @Nullable Object payload) {
    int index = getModelPosition(model);
    if (index != -1) {
      notifyItemChanged(index, payload);
    }
  }

  /**
   * Adds the model to the end of the {@link #models} list and notifies that the item was inserted.
   */
  protected void addModel(EpoxyModel<?> modelToAdd) {
    int initialSize = models.size();

    pauseModelListNotifications();
    models.add(modelToAdd);
    resumeModelListNotifications();

    notifyItemRangeInserted(initialSize, 1);
  }

  /**
   * Adds the models to the end of the {@link #models} list and notifies that the items were
   * inserted.
   */
  protected void addModels(EpoxyModel<?>... modelsToAdd) {
    int initialSize = models.size();
    int numModelsToAdd = modelsToAdd.length;

    ((ModelList) models).ensureCapacity(initialSize + numModelsToAdd);

    pauseModelListNotifications();
    Collections.addAll(models, modelsToAdd);
    resumeModelListNotifications();

    notifyItemRangeInserted(initialSize, numModelsToAdd);
  }

  /**
   * Adds the models to the end of the {@link #models} list and notifies that the items were
   * inserted.
   */
  protected void addModels(Collection<? extends EpoxyModel<?>> modelsToAdd) {
    int initialSize = models.size();

    pauseModelListNotifications();
    models.addAll(modelsToAdd);
    resumeModelListNotifications();

    notifyItemRangeInserted(initialSize, modelsToAdd.size());
  }

  /**
   * Inserts the given model before the other in the {@link #models} list, and notifies that the
   * item was inserted.
   */
  protected void insertModelBefore(EpoxyModel<?> modelToInsert, EpoxyModel<?> modelToInsertBefore) {
    int targetIndex = getModelPosition(modelToInsertBefore);
    if (targetIndex == -1) {
      throw new IllegalStateException("Model is not added: " + modelToInsertBefore);
    }

    pauseModelListNotifications();
    models.add(targetIndex, modelToInsert);
    resumeModelListNotifications();

    notifyItemInserted(targetIndex);
  }

  /**
   * Inserts the given model after the other in the {@link #models} list, and notifies that the item
   * was inserted.
   */
  protected void insertModelAfter(EpoxyModel<?> modelToInsert, EpoxyModel<?> modelToInsertAfter) {
    int modelIndex = getModelPosition(modelToInsertAfter);
    if (modelIndex == -1) {
      throw new IllegalStateException("Model is not added: " + modelToInsertAfter);
    }

    int targetIndex = modelIndex + 1;
    pauseModelListNotifications();
    models.add(targetIndex, modelToInsert);
    resumeModelListNotifications();

    notifyItemInserted(targetIndex);
  }

  /**
   * If the given model exists it is removed and an item removal is notified. Otherwise this does
   * nothing.
   */
  protected void removeModel(EpoxyModel<?> model) {
    int index = getModelPosition(model);
    if (index != -1) {
      pauseModelListNotifications();
      models.remove(index);
      resumeModelListNotifications();

      notifyItemRemoved(index);
    }
  }

  /**
   * Removes all models
   */
  protected void removeAllModels() {
    int numModelsRemoved = models.size();

    pauseModelListNotifications();
    models.clear();
    resumeModelListNotifications();

    notifyItemRangeRemoved(0, numModelsRemoved);
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
    pauseModelListNotifications();
    modelsToRemove.clear();
    resumeModelListNotifications();

    notifyItemRangeRemoved(initialModelCount - numModelsRemoved, numModelsRemoved);
  }

  /**
   * Sets the visibility of the given model, and notifies that the item changed if the new
   * visibility is different from the previous.
   *
   * @param model The model to show. It should already be added to the {@link #models} list.
   * @param show  True to show the model, false to hide it.
   */
  protected void showModel(EpoxyModel<?> model, boolean show) {
    if (model.isShown() == show) {
      return;
    }

    model.show(show);
    notifyModelChanged(model);
  }

  /**
   * Shows the given model, and notifies that the item changed if the item wasn't already shown.
   *
   * @param model The model to show. It should already be added to the {@link #models} list.
   */
  protected void showModel(EpoxyModel<?> model) {
    showModel(model, true);
  }

  /**
   * Shows the given models, and notifies that each item changed if the item wasn't already shown.
   *
   * @param models The models to show. They should already be added to the {@link #models} list.
   */
  protected void showModels(EpoxyModel<?>... models) {
    showModels(Arrays.asList(models));
  }

  /**
   * Sets the visibility of the given models, and notifies that the items changed if the new
   * visibility is different from the previous.
   *
   * @param models The models to show. They should already be added to the {@link #models} list.
   * @param show   True to show the models, false to hide them.
   */
  protected void showModels(boolean show, EpoxyModel<?>... models) {
    showModels(Arrays.asList(models), show);
  }

  /**
   * Shows the given models, and notifies that each item changed if the item wasn't already shown.
   *
   * @param models The models to show. They should already be added to the {@link #models} list.
   */
  protected void showModels(Iterable<EpoxyModel<?>> models) {
    showModels(models, true);
  }

  /**
   * Sets the visibility of the given models, and notifies that the items changed if the new
   * visibility is different from the previous.
   *
   * @param models The models to show. They should already be added to the {@link #models} list.
   * @param show   True to show the models, false to hide them.
   */
  protected void showModels(Iterable<EpoxyModel<?>> models, boolean show) {
    for (EpoxyModel<?> model : models) {
      showModel(model, show);
    }
  }

  /**
   * Hides the given model, and notifies that the item changed if the item wasn't already hidden.
   *
   * @param model The model to hide. This should already be added to the {@link #models} list.
   */
  protected void hideModel(EpoxyModel<?> model) {
    showModel(model, false);
  }

  /**
   * Hides the given models, and notifies that each item changed if the item wasn't already hidden.
   *
   * @param models The models to hide. They should already be added to the {@link #models} list.
   */
  protected void hideModels(Iterable<EpoxyModel<?>> models) {
    showModels(models, false);
  }

  /**
   * Hides the given models, and notifies that each item changed if the item wasn't already hidden.
   *
   * @param models The models to hide. They should already be added to the {@link #models} list.
   */
  protected void hideModels(EpoxyModel<?>... models) {
    hideModels(Arrays.asList(models));
  }

  /**
   * Hides all models currently located after the given model in the {@link #models} list.
   *
   * @param model The model after which to hide. It must exist in the {@link #models} list.
   */
  protected void hideAllAfterModel(EpoxyModel<?> model) {
    hideModels(getAllModelsAfter(model));
  }

  /**
   * Returns a sub list of all items in {@link #models} that occur after the given model. This list
   * is backed by the original models list, any changes to the returned list will be reflected in
   * the original {@link #models} list.
   *
   * @param model Must exist in {@link #models}.
   */
  protected List<EpoxyModel<?>> getAllModelsAfter(EpoxyModel<?> model) {
    int index = getModelPosition(model);
    if (index == -1) {
      throw new IllegalStateException("Model is not added: " + model);
    }
    return models.subList(index + 1, models.size());
  }

  /**
   * We pause the list's notifications when we modify models internally, since we already do the
   * proper adapter notifications for those modifications. By pausing these list notifications we
   * prevent the differ having to do work to track them.
   */
  private void pauseModelListNotifications() {
    ((ModelList) models).pauseNotifications();
  }

  private void resumeModelListNotifications() {
    ((ModelList) models).resumeNotifications();
  }
}
