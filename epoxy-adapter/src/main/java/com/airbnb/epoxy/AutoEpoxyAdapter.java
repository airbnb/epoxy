package com.airbnb.epoxy;

import android.os.Handler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import static com.airbnb.epoxy.AdapterHelperLookup.getHelperForAdapter;

public abstract class AutoEpoxyAdapter extends BaseEpoxyAdapter {
  private final DiffHelper diffHelper = new DiffHelper(this);
  private final AdapterHelper helper = getHelperForAdapter(this);
  private final NotifyBlocker notifyBlocker = new NotifyBlocker();
  private final Handler handler = new Handler();
  private List<EpoxyModel<?>> currentModels = Collections.emptyList();
  private ArrayList<EpoxyModel<?>> modelsBeingBuilt;
  private boolean isFirstModelBuild = true;
  private boolean filterDuplicates;

  public AutoEpoxyAdapter() {
    registerAdapterDataObserver(notifyBlocker);
  }

  @Override
  List<EpoxyModel<?>> getCurrentModels() {
    return currentModels;
  }

  /**
   * Call this to schedule a model update. The adapter will call {@link #buildModels()} so that
   * models can be rebuilt for the current data.
   */
  public void requestModelUpdate() {
    handler.removeCallbacks(updateModelsRunnable);
    handler.post(updateModelsRunnable);
  }

  private final Runnable updateModelsRunnable = new Runnable() {
    @Override
    public void run() {
      doModelUpdate();
    }
  };

  private void doModelUpdate() {
    if (isFirstModelBuild) {
      helper.validateFieldsAreNull();
    }

    helper.resetAutoModels();

    modelsBeingBuilt = new ArrayList<>(getExpectedModelCount());
    buildModels();
    currentModels = modelsBeingBuilt;
    modelsBeingBuilt = null;

    filterDuplicatesIfNeeded(currentModels);

    notifyBlocker.allowChanges();
    diffHelper.notifyModelChanges();
    notifyBlocker.blockChanges();

    isFirstModelBuild = false;
  }

  private void filterDuplicatesIfNeeded(List<EpoxyModel<?>> models) {
    if (!filterDuplicates) {
      return;
    }

    Set<Long> modelIds = new HashSet<>(models.size());

    ListIterator<EpoxyModel<?>> modelIterator = models.listIterator();
    while (modelIterator.hasNext()) {
      EpoxyModel<?> model = modelIterator.next();
      if (!modelIds.add(model.id())) {
        int indexOfDuplicate = modelIterator.previousIndex();
        modelIterator.remove();

        int indexOfOriginal = findPositionOfDuplicate(models, model);
        EpoxyModel<?> originalModel = models.get(indexOfOriginal);
        if (indexOfDuplicate <= indexOfOriginal) {
          // Adjust for the original positions of the models before the duplicate was removed
          indexOfOriginal++;
        }

        onModelFiltered(originalModel, indexOfOriginal, model, indexOfDuplicate);
      }
    }
  }

  private int findPositionOfDuplicate(List<EpoxyModel<?>> models, EpoxyModel<?> duplicateModel) {
    int size = models.size();
    for (int i = 0; i < size; i++) {
      EpoxyModel<?> model = models.get(i);
      if (model.id() == duplicateModel.id()) {
        return i;
      }
    }

    throw new IllegalArgumentException("No duplicates in list");
  }

  protected void onModelFiltered(EpoxyModel<?> originalModel, int indexOfOriginal,
      EpoxyModel<?> duplicateModel, int indexOfDuplicate) {

  }

  private int getExpectedModelCount() {
    if (isFirstModelBuild) {
      return 25;
    }

    return currentModels.size();
  }

  /**
   * Subclasses should implement this to describe what models should be shown for the current state.
   * Implementations should call either {@link #add(EpoxyModel)} or {@link
   * EpoxyModel#addTo(AutoEpoxyAdapter)} with the models that should be shown, in the order that is
   * desired.
   */
  protected abstract void buildModels();

  protected void add(EpoxyModel<?> model) {
    validateAddedModel(model);
    modelsBeingBuilt.add(model);
  }

  protected void add(EpoxyModel<?>... modelsToAdd) {
    for (EpoxyModel<?> model : modelsToAdd) {
      validateAddedModel(model);
    }
    modelsBeingBuilt.ensureCapacity(modelsBeingBuilt.size() + modelsToAdd.length);
    Collections.addAll(modelsBeingBuilt, modelsToAdd);
  }

  protected void add(Collection<EpoxyModel<?>> modelsToAdd) {
    for (EpoxyModel<?> model : modelsToAdd) {
      validateAddedModel(model);
    }
    modelsBeingBuilt.addAll(modelsToAdd);
  }

  /**
   * Throw if adding a model is not currently allowed.
   */
  private void validateAddedModel(EpoxyModel<?> model) {
    if (modelsBeingBuilt == null) {
      throw new IllegalStateException(
          "You can only add models inside the `buildModels` methods, and you cannot call "
              + "`buildModels` directly. Call `requestModelUpdate` instead");
    }

    if (model == null) {
      throw new IllegalArgumentException("You cannot add a null model");
    }

    if (model.hasDefaultId()) {
      throw new IllegalStateException("You must set an id on a model before adding it.");
    }
  }

  public void setFilterDuplicates(boolean filterDuplicates) {
    this.filterDuplicates = filterDuplicates;
  }

  public boolean isFilteringDuplicates() {
    return filterDuplicates;
  }
}
