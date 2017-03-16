package com.airbnb.epoxy;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager.SpanSizeLookup;
import android.support.v7.widget.RecyclerView;

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

  private final EpoxyControllerAdapter adapter = new EpoxyControllerAdapter(this);
  private final ControllerHelper helper = getHelperForController(this);
  private final Handler handler = new Handler();
  private final List<Interceptor> interceptors = new ArrayList<>();
  private ControllerModelList modelsBeingBuilt;
  private boolean filterDuplicates;
  /** Used to time operations and log their duration when in debug mode. */
  private Timer timer = NO_OP_TIMER;
  private EpoxyDiffLogger debugObserver;
  private boolean hasBuiltModelsEver;

  // TODO: (eli_hart 3/9/17) validate add is only ever called once per model instance
  // TODO: (eli_hart 3/14/17) change hash validation to ignore field if it is a generated model
  // since the class can't be looked up at annotation proccessing time
  // TODO: (eli_hart 3/14/17) validate hashcode never changes
  // TODO: (eli_hart 3/14/17) validate a setter is never called once model is added

  // Readme items:
  // hidden models breaking for pull to refresh or multiple items in a row on grid
  // Model  group
  // debug logs
  // Config setting to validate auto models
  // Note that it doesn't work to attach the adapter to multiple recyclerviews because of saved
  // state. Multiple recyclerviews could be supported if needed.

  // TODO: (eli_hart 3/7/17) Guide for updating to 2.0
  // Setting a null click listener is broken. Now needs to be cast.

  /**
   * Call this to schedule a model update. The adapter will schedule a call to {@link
   * #buildModels()} so that models can be rebuilt for the current data.
   */
  public void requestModelBuild() {
    if (isBuildingModels()) {
      throw new IllegalStateException("Cannot call `requestBuildModels` from inside `buildModels`");
    }

    handler.removeCallbacks(buildModelsRunnable);
    handler.post(buildModelsRunnable);
  }

  private final Runnable buildModelsRunnable = new Runnable() {
    @Override
    public void run() {
      dispatchModelBuild();
    }
  };

  private void dispatchModelBuild() {
    helper.resetAutoModels();

    modelsBeingBuilt = new ControllerModelList(getExpectedModelCount());

    timer.start();
    buildModels();
    timer.stop("Models built");

    runInterceptors();
    filterDuplicatesIfNeeded(modelsBeingBuilt);
    modelsBeingBuilt.freeze();

    timer.start();
    adapter.setModels(modelsBeingBuilt);
    timer.stop("Models diffed");

    modelsBeingBuilt = null;
    hasBuiltModelsEver = true;
  }

  /** An estimate for how many models will be built in the next {@link #buildModels()} phase. */
  private int getExpectedModelCount() {
    if (hasBuiltModelsEver) {
      return adapter.getItemCount();
    }

    return 25;
  }

  /**
   * Subclasses should implement this to describe what models should be shown for the current state.
   * Implementations should call either {@link #add(EpoxyModel)}, {@link
   * EpoxyModel#addTo(EpoxyController)}, or {@link EpoxyModel#addIf(boolean, EpoxyController)}
   * with the models that should be shown, in the order that is desired.
   */
  protected abstract void buildModels();

  private void runInterceptors() {
    if (interceptors.isEmpty()) {
      return;
    }

    timer.start();
    for (Interceptor interceptor : interceptors) {
      interceptor.intercept(modelsBeingBuilt);
    }
    timer.stop("Interceptors executed");
  }

  /** A callback that is run after {@link #buildModels()} completes and before diffing is run. */
  public interface Interceptor {
    /**
     * This is called immediately after {@link #buildModels()} and before diffing is run and the
     * models are set on the adapter. This is a final chance to make any changes to the the models
     * added in {@link #buildModels()}. This may be useful for actions that act on all models in
     * aggregate, such as toggling divider settings, or for cases such as rearranging models for an
     * experiment.
     * <p>
     * The models list must not be changed after this method returns. Doing so will throw an
     * exception.
     */
    void intercept(List<EpoxyModel<?>> models);
  }

  /**
   * Add an interceptor callback to be run after models are built, to make any last changes before
   * they are set on the adapter. Interceptors are run in the order they are added.
   *
   * @see Interceptor#intercept(List)
   */
  public void addInterceptor(Interceptor interceptor) {
    interceptors.add(interceptor);
  }

  /** Remove an interceptor that was added with {@link #addInterceptor(Interceptor)}. */
  public void removeInterceptor(Interceptor interceptor) {
    interceptors.remove(interceptor);
  }

  /**
   * Get the number of models added so far during the {@link #buildModels()} phase. It is only valid
   * to call this from within that method.
   * <p>
   * This is different from the number of models currently on the adapter, since models on the
   * adapter are not updated until after models are finished being built. To access current adapter
   * count call {@link #getAdapter()} and {@link EpoxyControllerAdapter#getItemCount()}
   */
  protected int getModelCountBuiltSoFar() {
    if (!isBuildingModels()) {
      throw new IllegalStateException("Can only all this when inside the `buildModels` method");
    }

    return modelsBeingBuilt.size();
  }

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

        onExceptionSwallowed(
            new IllegalStateException("Two models have the same ID. ID's must be unique!"
                + "\nOriginal has position " + indexOfOriginal + ":\n" + originalModel
                + "\nDuplicate has position " + indexOfDuplicate + ":\n" + model)
        );
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
   * If set to true, Epoxy will search for models with duplicate ids added during {@link
   * #buildModels()} and remove any duplicates found. If models with the same id are found, the
   * first one is left in the adapter and any subsequent models are removed. {@link
   * #onExceptionSwallowed(RuntimeException)} will be called for each duplicate removed.
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
  public void setDebugLoggingEnabled(boolean enabled) {
    if (isBuildingModels()) {
      throw new IllegalStateException("Debug logging should be enabled before models are built");
    }

    if (enabled) {
      timer = new DebugTimer(getClass().getSimpleName());
      debugObserver = new EpoxyDiffLogger(getClass().getSimpleName());
      adapter.registerAdapterDataObserver(debugObserver);
    } else {
      timer = NO_OP_TIMER;
      if (debugObserver != null) {
        adapter.unregisterAdapterDataObserver(debugObserver);
      }
    }
  }

  /**
   * Get the underlying adapter built by this controller. Use this to get the adapter to set on a
   * RecyclerView, or to get information about models currently in use.
   */
  public EpoxyControllerAdapter getAdapter() {
    return adapter;
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

  /**
   * This is called when recoverable exceptions happen at runtime. They can be ignored and Epoxy
   * will recover, but you can override this to be aware of when they happen.
   */
  protected void onExceptionSwallowed(RuntimeException exception) {
  }

  /** Called when the controller's adapter is attach to a recyclerview. */
  protected void onAttachedToRecyclerView(RecyclerView recyclerView) {

  }

  /** Called when the controller's adapter is detached from a recyclerview. */
  protected void onDetachedFromRecyclerView(RecyclerView recyclerView) {

  }

  /**
   * Called immediately after a model is bound to a view holder. Subclasses can override this if
   * they want alerts on when a model is bound. Alternatively you may attach a listener directly to
   * a generated model with model.onBind(...)
   */
  protected void onModelBound(EpoxyViewHolder holder, EpoxyModel<?> model, int position,
      @Nullable List<Object> payloads) {
  }

  /**
   * Called immediately after a model is unbound from a view holder. Subclasses can override this if
   * they want alerts on when a model is unbound. Alternatively you may attach a listener directly
   * to a generated model with model.onUnbind(...)
   */
  protected void onModelUnbound(EpoxyViewHolder holder, EpoxyModel<?> model) {

  }

  /**
   * Called when the given viewholder is attached to the window, along with the model it is bound
   * to.
   *
   * @see BaseEpoxyAdapter#onViewAttachedToWindow(EpoxyViewHolder)
   */
  protected void onViewAttachedToWindow(EpoxyViewHolder holder, EpoxyModel<?> model) {

  }

  /**
   * Called when the given viewholder is detechaed from the window, along with the model it is bound
   * to.
   *
   * @see BaseEpoxyAdapter#onViewDetachedFromWindow(EpoxyViewHolder)
   */
  protected void onViewDetachedFromWindow(EpoxyViewHolder holder, EpoxyModel<?> model) {

  }
}
