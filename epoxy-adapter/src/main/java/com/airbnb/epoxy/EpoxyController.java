package com.airbnb.epoxy;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager.SpanSizeLookup;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import static com.airbnb.epoxy.ControllerHelperLookup.getHelperForController;

/**
 * A controller for easily combining {@link EpoxyModel} instances in a {@link RecyclerView.Adapter}.
 * Simply implement {@link #buildModels()} to declare which models should be used, and in which
 * order. Call {@link #requestModelBuild()} whenever your data changes, and the controller will call
 * {@link #buildModels()}, update the adapter with the new models, and notify any changes between
 * the new and old models.
 * <p>
 * The controller maintains a {@link android.support.v7.widget.RecyclerView.Adapter} with the latest
 * models, which you can get via {@link #getAdapter()} to set on your RecyclerView.
 * <p>
 * All data change notifications are applied automatically via Epoxy's diffing algorithm. All of
 * your models must have a unique id set on them for diffing to work. You may choose to use {@link
 * AutoModel} annotations to have the controller create models with unique ids for you
 * automatically.
 * <p>
 * Once a model is created and added to the controller in {@link #buildModels()} it should be
 * treated as immutable and never modified again. This is necessary for adapter updates to be
 * accurate.
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
  private List<ModelInterceptorCallback> modelInterceptorCallbacks;
  private int recyclerViewAttachCount = 0;
  private EpoxyModel<?> stagedModel;

  /**
   * Call this to request a model update. The controller will schedule a call to {@link
   * #buildModels()} so that models can be rebuilt for the current data. All calls after the first
   * are posted and debounced so that the calling code need not worry about calling this multiple
   * times in a row.
   */
  public void requestModelBuild() {
    if (isBuildingModels()) {
      throw new IllegalEpoxyUsage("Cannot call `requestModelBuild` from inside `buildModels`");
    }

    // If it is the first time building models then we do it right away, otherwise we post the call.
    // We want to do it right away the first time so that scroll position can be restored correctly,
    // shared element transitions aren't delayed, and content is shown asap. We post later calls
    // so that they are debounced, and so any updates to data can be completely finished before
    // the models are built.
    if (hasBuiltModelsEver) {
      requestDelayedModelBuild(0);
    } else {
      cancelPendingModelBuild();
      dispatchModelBuild();
    }
  }

  /**
   * Call this to request a delayed model update. The controller will schedule a call to {@link
   * #buildModels()} so that models can be rebuilt for the current data.
   * <p>
   * Using this to delay a model update may be helpful in cases where user input is causing many
   * rapid changes in the models, such as typing. In that case, the view is already updated on
   * screen and constantly rebuilding models is potentially slow and unnecessary. The downside to
   * delaying the model build too long is that models will not be in sync with the data or view, and
   * scrolling the view offscreen and back onscreen will cause the model to bind old data.
   * <p>
   * This will cancel any currently queued request to build models.
   * <p>
   * In most cases you should use {@link #requestModelBuild()} instead of this.
   *
   * @param delayMs The time in milliseconds to delay the model build by. Should be greater than or
   *                equal to 0. Even if a delay of 0 is given the model build will be posted to the
   *                next frame.
   */
  public void requestDelayedModelBuild(int delayMs) {
    if (isBuildingModels()) {
      throw new IllegalEpoxyUsage(
          "Cannot call `requestDelayedModelBuild` from inside `buildModels`");
    }

    cancelPendingModelBuild();
    handler.postDelayed(buildModelsRunnable, delayMs);
  }

  /**
   * Cancels a pending call to {@link #buildModels()} if one has been queued by {@link
   * #requestModelBuild()}.
   */
  public void cancelPendingModelBuild() {
    handler.removeCallbacks(buildModelsRunnable);
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
    addCurrentlyStagedModelIfExists();
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
    int currentModelCount = adapter.getItemCount();
    return currentModelCount != 0 ? currentModelCount : 25;
  }

  /**
   * Subclasses should implement this to describe what models should be shown for the current state.
   * Implementations should call either {@link #add(EpoxyModel)}, {@link
   * EpoxyModel#addTo(EpoxyController)}, or {@link EpoxyModel#addIf(boolean, EpoxyController)} with
   * the models that should be shown, in the order that is desired.
   * <p>
   * Once a model is added to the controller it should be treated as immutable and never modified
   * again. This is necessary for adapter updates to be accurate. If "validateEpoxyModelUsage" is
   * enabled then runtime validations will be done to make sure models are not changed.
   * <p>
   * You CANNOT call this method directly. Instead, call {@link #requestModelBuild()} to have the
   * controller schedule an update.
   */
  protected abstract void buildModels();

  int getFirstIndexOfModelInBuildingList(EpoxyModel<?> model) {
    int size = modelsBeingBuilt.size();
    for (int i = 0; i < size; i++) {
      if (modelsBeingBuilt.get(i) == model) {
        return i;
      }
    }

    return -1;
  }

  boolean isModelAddedMultipleTimes(EpoxyModel<?> model) {
    int modelCount = 0;
    int size = modelsBeingBuilt.size();
    for (int i = 0; i < size; i++) {
      if (modelsBeingBuilt.get(i) == model) {
        modelCount++;
      }
    }

    return modelCount > 1;
  }

  void addAfterInterceptorCallback(ModelInterceptorCallback callback) {
    if (!isBuildingModels()) {
      throw new IllegalEpoxyUsage("Can only call when building models");
    }

    if (modelInterceptorCallbacks == null) {
      modelInterceptorCallbacks = new ArrayList<>();
    }

    modelInterceptorCallbacks.add(callback);
  }

  /**
   * Callbacks to each model for when interceptors are started and stopped, so the models know when
   * to allow changes.
   */
  interface ModelInterceptorCallback {
    void onInterceptorsStarted(EpoxyController controller);
    void onInterceptorsFinished(EpoxyController controller);
  }

  private void runInterceptors() {
    if (!interceptors.isEmpty()) {
      if (modelInterceptorCallbacks != null) {
        for (ModelInterceptorCallback callback : modelInterceptorCallbacks) {
          callback.onInterceptorsStarted(this);
        }
      }

      timer.start();

      for (Interceptor interceptor : interceptors) {
        interceptor.intercept(modelsBeingBuilt);
      }

      timer.stop("Interceptors executed");

      if (modelInterceptorCallbacks != null) {
        for (ModelInterceptorCallback callback : modelInterceptorCallbacks) {
          callback.onInterceptorsFinished(this);
        }

        // Interceptors are cleared so that future model builds don't notify past models
        modelInterceptorCallbacks = null;
      }
    }
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
      throw new IllegalEpoxyUsage("Can only all this when inside the `buildModels` method");
    }

    return modelsBeingBuilt.size();
  }

  /**
   * Add the model to this controller. Can only be called from inside {@link
   * EpoxyController#buildModels()}.
   */
  protected void add(EpoxyModel<?> model) {
    model.addTo(this);
  }

  /**
   * Add the models to this controller. Can only be called from inside {@link
   * EpoxyController#buildModels()}.
   */
  protected void add(EpoxyModel<?>... modelsToAdd) {
    modelsBeingBuilt.ensureCapacity(modelsBeingBuilt.size() + modelsToAdd.length);

    for (EpoxyModel<?> model : modelsToAdd) {
      model.addTo(this);
    }
  }

  /**
   * Add the models to this controller. Can only be called from inside {@link
   * EpoxyController#buildModels()}.
   */
  protected void add(List<? extends EpoxyModel<?>> modelsToAdd) {
    modelsBeingBuilt.ensureCapacity(modelsBeingBuilt.size() + modelsToAdd.size());

    for (EpoxyModel<?> model : modelsToAdd) {
      model.addTo(this);
    }
  }

  /**
   * Method to actually add the model to the list being built. Should be called after all
   * validations are done.
   */
  void addInternal(EpoxyModel<?> modelToAdd) {
    if (!isBuildingModels()) {
      throw new IllegalEpoxyUsage(
          "You can only add models inside the `buildModels` methods, and you cannot call "
              + "`buildModels` directly. Call `requestModelBuild` instead");
    }

    if (modelToAdd.hasDefaultId()) {
      throw new IllegalEpoxyUsage(
          "You must set an id on a model before adding it. Use the @AutoModel annotation if you "
              + "want an id to be automatically generated for you.");
    }

    if (!modelToAdd.isShown()) {
      throw new IllegalEpoxyUsage(
          "You cannot hide a model in an EpoxyController. Use `addIf` to conditionally add a "
              + "model instead.");
    }

    // The model being added may not have been staged if it wasn't mutated before it was added.
    // In that case we may have a previously staged model that still needs to be added.
    clearModelFromStaging(modelToAdd);
    modelToAdd.controllerToStageTo = null;
    modelsBeingBuilt.add(modelToAdd);
  }

  /**
   * Staging models allows them to be implicitly added after the user finishes modifying them. This
   * means that if a user has modified a model, and then moves on to modifying a different model,
   * the first model is automatically added as soon as the second model is modified.
   * <p>
   * There are some edge cases for handling models that are added without modification, or models
   * that are modified but then fail an `addIf` check.
   * <p>
   * This only works for AutoModels, and only if implicity adding is enabled in configuration.
   */
  void setStagedModel(EpoxyModel<?> model) {
    if (model != stagedModel) {
      addCurrentlyStagedModelIfExists();
    }

    stagedModel = model;
  }

  void addCurrentlyStagedModelIfExists() {
    if (stagedModel != null) {
      stagedModel.addTo(this);
    }
    stagedModel = null;
  }

  void clearModelFromStaging(EpoxyModel<?> model) {
    if (stagedModel != model) {
      addCurrentlyStagedModelIfExists();
    }
    stagedModel = null;
  }

  boolean isBuildingModels() {
    return modelsBeingBuilt != null;
  }

  private void filterDuplicatesIfNeeded(List<EpoxyModel<?>> models) {
    if (!filterDuplicates) {
      return;
    }

    timer.start();
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
            new IllegalEpoxyUsage("Two models have the same ID. ID's must be unique!"
                + "\nOriginal has position " + indexOfOriginal + ":\n" + originalModel
                + "\nDuplicate has position " + indexOfDuplicate + ":\n" + model)
        );
      }
    }

    timer.stop("Duplicates filtered");
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
   * model hashCode/equals implementations (which can often slow down diffing).
   * <p>
   * This should only be used in debug builds to avoid a performance hit in prod.
   */
  public void setDebugLoggingEnabled(boolean enabled) {
    if (isBuildingModels()) {
      throw new IllegalEpoxyUsage("Debug logging should be enabled before models are built");
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

  void onAttachedToRecyclerViewInternal(RecyclerView recyclerView) {
    recyclerViewAttachCount++;
    if (recyclerViewAttachCount > 1) {
      onExceptionSwallowed(new IllegalStateException(
          "Epoxy does not support attaching an adapter to more than one RecyclerView because "
              + "saved state will not work properly. If you did not intend to attach your adapter "
              + "to multiple RecyclerViews you may be leaking a "
              + "reference to a previous RecyclerView. Make sure to remove the adapter from any "
              + "previous RecyclerViews (eg if the adapter is reused in a Fragment across "
              + "multiple onCreateView/onDestroyView cycles). See https://github"
              + ".com/airbnb/epoxy/wiki/Avoiding-Memory-Leaks for more information."));
    }
    onAttachedToRecyclerView(recyclerView);
  }

  void onDetachedFromRecyclerViewInternal(RecyclerView recyclerView) {
    recyclerViewAttachCount--;
    onDetachedFromRecyclerView(recyclerView);
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
   *
   * @param previouslyBoundModel If non null, this is a model with the same id as the newly bound
   *                             model, and was previously bound to a view. This means that {@link
   *                             #buildModels()} returned a model that is different from the
   *                             previouslyBoundModel and the view is being rebound to incorporate
   *                             the change. You can compare this previous model with the new one to
   *                             see exactly what changed.
   *                             <p>
   *                             The newly bound model and the previously bound model are guaranteed
   *                             to have the same id, but will not necessarily be of the same type
   *                             depending on your implementation of {@link #buildModels()}. With
   *                             common usage patterns of Epoxy they should be the same type, and
   *                             will only differ if you are using different model classes with the
   *                             same id.
   *                             <p>
   *                             Comparing the newly bound model with the previous model allows you
   *                             to be more intelligent when updating your view. This may help you
   *                             optimize, or make it easier to work with animations.
   *                             <p>
   *                             If the new model and the previous model have the same view type
   *                             (given by {@link EpoxyModel#getViewType()}), and if you are using
   *                             the default ReyclerView item animator, the same view will be kept.
   *                             If you are using a custom item animator then the view will be the
   *                             same if the animator returns true in canReuseUpdatedViewHolder.
   *                             <p>
   *                             This previously bound model is taken as a payload from the diffing
   *                             process, and follows the same general conditions for all
   *                             recyclerview change payloads.
   */
  protected void onModelBound(EpoxyViewHolder holder, EpoxyModel<?> boundModel, int position,
      @Nullable EpoxyModel<?> previouslyBoundModel) {
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
