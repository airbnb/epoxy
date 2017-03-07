package com.airbnb.epoxy;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager.SpanSizeLookup;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.AdapterDataObserver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import static com.airbnb.epoxy.ControllerHelperLookup.getHelperForController;

/**
 * A controller for easily combining {@link EpoxyModel} objects. Simply implement {@link
 * #buildModels()} to declare which models should be used, and in which order. Call {@link
 * #requestModelBuild()} whenever your data changes and the models need to be recreated.
 * <p>
 * The controller creates a {@link android.support.v7.widget.RecyclerView.Adapter} with the latest
 * models, which you can get via {@link #getAdapter()} to set on your RecyclerView.
 * <p>
 * All data change notifications are applied automatically via Epoxy's diffing algorithm. All of
 * your models must have a unique id set on them for diffing to work. You may choose to use {@link
 * AutoModel} annotations to have the controller create models with unique ids for you
 * automatically.
 */
public abstract class EpoxyController {
  private static final Timer NO_OP_TIMER = new NoOpTimer();

  private final ControllerAdapter adapter = new ControllerAdapter(this);
  private final ControllerHelper helper = getHelperForController(this);
  private final Handler handler = new Handler();
  private List<EpoxyModel<?>> copyOfCurrentModels;
  private ArrayList<EpoxyModel<?>> modelsBeingBuilt;
  private boolean filterDuplicates;
  /** Used to time operations and log their duration when in debug mode. */
  private Timer timer = NO_OP_TIMER;

  // Readme items:
  // hidden models breaking for pull to refresh or multiple items in a row on grid
  // Model  group
  // debug logs
  // Config setting to validate auto models
  // Note that it doesn't work to attach the adapter to multiple recyclerviews because of saved
  // state. Multiple recyclerviews could be supported if needed.

  /**
   * Call this to schedule a model update. The adapter will schedule a call to {@link
   * #buildModels()} so that models can be rebuilt for the current data.
   */
  public void requestModelBuild() {
    handler.removeCallbacks(buildModelsRunnable);
    handler.post(buildModelsRunnable);
  }

  private final Runnable buildModelsRunnable = new Runnable() {
    @Override
    public void run() {
      doModelBuild();
    }
  };

  private void doModelBuild() {
    helper.resetAutoModels();

    modelsBeingBuilt = new ArrayList<>(getExpectedModelCount());

    timer.start();
    buildModels();
    timer.stop("Models built");

    filterDuplicatesIfNeeded(modelsBeingBuilt);

    timer.start();
    adapter.setModels(modelsBeingBuilt);
    timer.stop("Models diffed");

    modelsBeingBuilt = null;
    copyOfCurrentModels = null;
  }

  private int getExpectedModelCount() {
    if (adapter.currentModels == Collections.EMPTY_LIST) {
      return 25;
    }

    return adapter.getItemCount();
  }

  /**
   * Subclasses should implement this to describe what models should be shown for the current state.
   * Implementations should call either {@link #add(EpoxyModel)}, {@link
   * EpoxyModel#addTo(EpoxyController)}, or {@link EpoxyModel#addIf(boolean, EpoxyController)}
   * with the models that should be shown, in the order that is desired.
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

  boolean isBuildingModels() {
    return modelsBeingBuilt != null;
  }

  /**
   * Throw if adding a model is not currently allowed.
   */
  private void validateAddedModel(EpoxyModel<?> model) {
    if (!isBuildingModels()) {
      throw new IllegalStateException(
          "You can only add models inside the `buildModels` methods, and you cannot call "
              + "`buildModels` directly. Call `requestModelBuild` instead");
    }

    if (model == null) {
      throw new IllegalArgumentException("You cannot add a null model");
    }

    if (model.hasDefaultId()) {
      throw new IllegalStateException("You must set an id on a model before adding it.");
    }

    if (!model.isShown()) {
      throw new IllegalStateException(
          "You cannot hide a model in an AutoEpoxyAdapter. Use `addIf` to conditionally add a "
              + "model instead.");
    }
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

  /**
   * Called if a duplicate model is detected and filtered out.
   *
   * @see #setFilterDuplicates(boolean)
   */
  protected void onModelFiltered(EpoxyModel<?> originalModel, int indexOfOriginal,
      EpoxyModel<?> duplicateModel, int indexOfDuplicate) {

  }

  /**
   * If set to true, Epoxy will search for models with duplicate ids added during {@link
   * #buildModels()} and remove any duplicates found. If models with the same id are found, the
   * first one is left in the adapter and any subsequent models are removed. {@link
   * #onModelFiltered(EpoxyModel, int, EpoxyModel, int)} will be called for each duplicate removed.
   * <p>
   * This may be useful if your models are created via server supplied data, in which case the
   * server may erroneously send duplicate items. Duplicate items break Epoxy's diffing and would
   * normally cause a crash, so filtering them out can make a production application more robust to
   * server inconsistencies.
   */
  public void setFilterDuplicates(boolean filterDuplicates) {
    this.filterDuplicates = filterDuplicates;
  }

  /**
   * If enabled, DEBUG logcat messages will be printed to show when models are rebuilt, the time
   * taken to build them, the time taken to diff them, and the item change outcomes from the
   * differ. The tag of the logcat message is your adapter name.
   * <p>
   * This is useful to verify that models are being diffed as expected, as well as to watch for
   * slowdowns in model building or diffing to indicate when you should optimize model building or
   * model hashCode implementations (which can often slow down diffing).
   * <p>
   * This should only be used in debug builds to avoid a performance hit in prod.
   */
  public void enableDebugLogging() {
    if (isBuildingModels()) {
      throw new IllegalStateException("Debug logging should be enabled before models are built");
    }

    if (timer == NO_OP_TIMER) {
      timer = new DebugTimer(getClass().getSimpleName());
      registerAdapterDataObserver(new EpoxyDiffLogger(getClass().getSimpleName()));
    }
  }

  /**
   * Get the underlying adapter built by this controller. Use this to get the adapter to set on a
   * RecyclerView.
   */
  public RecyclerView.Adapter getAdapter() {
    return adapter;
  }

  public int getModelCount() {
    return adapter.getItemCount();
  }

  public boolean isEmpty() {
    return adapter.isEmpty();
  }

  /** Get an unmodifiable copy of the current models set on the adapter. */
  public List<EpoxyModel<?>> getCopyOfModels() {
    if (copyOfCurrentModels == null) {
      copyOfCurrentModels = new UnmodifiableList<>(adapter.currentModels);
    }

    return copyOfCurrentModels;
  }

  public EpoxyModel<?> getModelAtPosition(int position) {
    return adapter.currentModels.get(position);
  }

  /**
   * Searches the current model list for the model with the given id. Returns the matching model if
   * one is found, otherwise null is returned.
   */
  @Nullable
  public EpoxyModel<?> getModelById(long id) {
    for (EpoxyModel<?> model : adapter.currentModels) {
      if (model.id() == id) {
        return model;
      }
    }

    return null;
  }

  protected int getModelPosition(EpoxyModel<?> targetModel) {
    int size = adapter.currentModels.size();
    for (int i = 0; i < size; i++) {
      EpoxyModel<?> model = adapter.currentModels.get(i);
      if (model.id() == targetModel.id()) {
        return i;
      }
    }

    return -1;
  }

  public void onSaveInstanceState(Bundle outState) {
    adapter.onSaveInstanceState(outState);
  }

  public void onRestoreInstanceState(@Nullable Bundle inState) {
    adapter.onRestoreInstanceState(inState);
  }

  /**
   * For use with a grid layout manager - use this to get the {@link SpanSizeLookup} for models in
   * this controller. This will delegate span look up calls to each model's {@link
   * EpoxyModel#getSpanSize(int, int, int)}. Make sure to also call {@link #setSpanCount(int)} so
   * the span count is correct.
   */
  public SpanSizeLookup getSpanSizeLookup() {
    return adapter.getSpanSizeLookup();
  }

  /**
   * If you are using a grid layout manager you must call this to set the span count of the grid.
   * This span count will be passed on to the models so models can choose which span count to be.
   *
   * @see #getSpanSizeLookup()
   * @see EpoxyModel#getSpanSize(int, int, int)
   */
  public void setSpanCount(int spanCount) {
    adapter.setSpanCount(spanCount);
  }

  public int getSpanCount() {
    return adapter.getSpanCount();
  }

  public boolean isMultiSpan() {
    return adapter.isMultiSpan();
  }

  public void registerAdapterDataObserver(AdapterDataObserver observer) {
    adapter.registerAdapterDataObserver(observer);
  }

  public void unregisterAdapterDataObserver(AdapterDataObserver observer) {
    adapter.unregisterAdapterDataObserver(observer);
  }

  /** Called when the controller's adapter is attach to a recyclerview. */
  protected void onAttachedToRecyclerView(RecyclerView recyclerView) {

  }

  /** Called when the controller's adapter is detached from a recyclerview. */
  protected void onDetachedFromRecyclerView(RecyclerView recyclerView) {

  }

  /**
   * Returns an object that manages the view holders currently bound to the RecyclerView. This
   * object is mainly used by the base Epoxy adapter to save view states, but you may find it useful
   * to help access views or models currently shown in the RecyclerView.
   */
  public BoundViewHolders getBoundViewHolders() {
    return adapter.getBoundViewHolders();
  }
}
