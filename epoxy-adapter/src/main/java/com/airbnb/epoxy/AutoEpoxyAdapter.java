package com.airbnb.epoxy;

import android.support.v7.widget.RecyclerView.AdapterDataObserver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static com.airbnb.epoxy.AdapterHelperLookup.getHelperForAdapter;

public abstract class AutoEpoxyAdapter extends BaseEpoxyAdapter {
  private final DiffHelper diffHelper = new DiffHelper(this);
  private final AdapterHelper helper = getHelperForAdapter(this);
  private final NotifyWatcher notifyWatcher = new NotifyWatcher();
  private List<EpoxyModel<?>> models = Collections.emptyList();
  private ArrayList<EpoxyModel<?>> modelsBeingBuilt;
  private boolean isFirstModelBuild = true;

  public AutoEpoxyAdapter() {
    registerAdapterDataObserver(notifyWatcher);
  }

  @Override
  List<EpoxyModel<?>> getCurrentModels() {
    return models;
  }

  /**
   * Call this to schedule a model update. The adapter will call {@link #buildModels()} so that
   * models can be rebuilt for the current data.
   */
  protected void requestModelUpdate() {
    doModelUpdate();
  }

  private void doModelUpdate() {
    if (isFirstModelBuild) {
      helper.validateFieldsAreNull();
      isFirstModelBuild = false;
    }

    // The helper should be the correct type because we looked it up based on the adapter's class
    //noinspection unchecked
    helper.resetAutoModels();

    modelsBeingBuilt = new ArrayList<>(getExpectedModelCount());
    buildModels();
    models = modelsBeingBuilt;
    modelsBeingBuilt = null;

    notifyWatcher.allowChanges();
    diffHelper.notifyModelChanges();
    notifyWatcher.blockChanges();
  }

  private int getExpectedModelCount() {
    // TODO: (eli_hart 2/21/17) Be more intelligent about this
    return models.size();
  }

  /**
   * Subclasses should implement this to describe what models should be shown for the current state.
   * Implementations should call either {@link #add(EpoxyModel)} or {@link
   * EpoxyModel#addTo(AutoEpoxyAdapter)} with the models that should be shown, in the order that is
   * desired.
   */
  protected abstract void buildModels();

  protected void add(EpoxyModel<?> model) {
    validateAddAllowed();
    modelsBeingBuilt.add(model);
  }

  protected void add(EpoxyModel<?>... modelsToAdd) {
    validateAddAllowed();
    modelsBeingBuilt.ensureCapacity(modelsBeingBuilt.size() + modelsToAdd.length);
    Collections.addAll(modelsBeingBuilt, modelsToAdd);
  }

  protected void add(Collection<EpoxyModel<?>> modelsToAdd) {
    validateAddAllowed();
    modelsBeingBuilt.addAll(modelsToAdd);
  }

  /** Throw if adding a model is not currently allowed. */
  private void validateAddAllowed() {
    if (modelsBeingBuilt == null) {
      throw new IllegalStateException(
          "You can only add models inside the `buildModels` methods, and you cannot call "
              + "`buildModels` directly. Call `requestModelUpdate` instead");
    }
  }

  /**
   * We don't allow any data change notifications except the ones done though diffing. Forcing
   * changes to happen through diffing reduces the chance for developer error when implementing an
   * adapter.
   * <p>
   * This observer throws upon any changes done outside of diffing.
   */
  private static class NotifyWatcher extends AdapterDataObserver {
    private boolean changesAllowed;

    void allowChanges() {
      changesAllowed = true;
    }

    void blockChanges() {
      changesAllowed = false;
    }

    @Override
    public void onChanged() {
      if (!changesAllowed) {
        throw new IllegalStateException(
            "You cannot notify item changes directly. Call `requestModelUpdate` instead.");
      }
    }

    @Override
    public void onItemRangeChanged(int positionStart, int itemCount) {
      onChanged();
    }

    @Override
    public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
      onChanged();
    }

    @Override
    public void onItemRangeInserted(int positionStart, int itemCount) {
      onChanged();
    }

    @Override
    public void onItemRangeRemoved(int positionStart, int itemCount) {
      onChanged();
    }

    @Override
    public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
      onChanged();
    }
  }
}
