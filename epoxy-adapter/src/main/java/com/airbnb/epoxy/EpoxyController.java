package com.airbnb.epoxy;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.airbnb.epoxy.stickyheader.StickyHeaderCallbacks;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup;

import static com.airbnb.epoxy.ControllerHelperLookup.getHelperForController;

/**
 * A controller for easily combining {@link EpoxyModel} instances in a {@link RecyclerView.Adapter}.
 * Simply implement {@link #buildModels()} to declare which models should be used, and in which
 * order. Call {@link #requestModelBuild()} whenever your data changes, and the controller will call
 * {@link #buildModels()}, update the adapter with the new models, and notify any changes between
 * the new and old models.
 * <p>
 * The controller maintains a {@link androidx.recyclerview.widget.RecyclerView.Adapter} with the
 * latest models, which you can get via {@link #getAdapter()} to set on your RecyclerView.
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
public abstract class EpoxyController implements ModelCollector, StickyHeaderCallbacks {

  /**
   * We check that the adapter is not connected to multiple recyclerviews, but when a fragment has
   * its view quickly destroyed and recreated it may temporarily attach the same adapter to the
   * previous view and the new view (eg because of fragment transitions) if the controller is reused
   * across views. We want to allow this case since it is a brief transient state. This should be
   * enough time for screen transitions to happen.
   */
  private static final int DELAY_TO_CHECK_ADAPTER_COUNT_MS = 3000;
  private static final Timer NO_OP_TIMER = new NoOpTimer();

  public static Handler defaultModelBuildingHandler = MainThreadExecutor.INSTANCE.handler;
  public static Handler defaultDiffingHandler = MainThreadExecutor.INSTANCE.handler;
  private static boolean filterDuplicatesDefault = false;
  private static boolean globalDebugLoggingEnabled = false;

  private final EpoxyControllerAdapter adapter;
  private EpoxyDiffLogger debugObserver;
  private int recyclerViewAttachCount = 0;
  private final Handler modelBuildHandler;

  /**
   * This is iterated over in the build models thread, but items can be inserted or removed from
   * other threads at any time.
   */
  private final List<Interceptor> interceptors = new CopyOnWriteArrayList<>();

  // Volatile because -> write only on main thread, read from builder thread
  private volatile boolean filterDuplicates = filterDuplicatesDefault;
  /**
   * This is used to track whether we are currently building models. If it is non null it means
   * a thread is in the building models method. We store the thread so we can know which one
   * is building models.
   * <p>
   * Volatile because -> write only on handler, read from any thread
   */
  private volatile Thread threadBuildingModels = null;
  /**
   * Used to know that we should build models synchronously the first time.
   * <p>
   * Volatile because -> written from the build models thread, read from the main thread.
   */
  private volatile boolean hasBuiltModelsEver;

  //////////////////////////////////////////////////////////////////////////////////////////

  /*
   * These fields are expected to only be used on the model building thread so they are not
   * synchronized.
   */

  /** Used to time operations and log their duration when in debug mode. */
  private Timer timer = NO_OP_TIMER;
  private final ControllerHelper helper = getHelperForController(this);
  private ControllerModelList modelsBeingBuilt;
  private List<ModelInterceptorCallback> modelInterceptorCallbacks;
  private EpoxyModel<?> stagedModel;

  //////////////////////////////////////////////////////////////////////////////////////////

  public EpoxyController() {
    this(defaultModelBuildingHandler, defaultDiffingHandler);
  }

  public EpoxyController(Handler modelBuildingHandler, Handler diffingHandler) {
    adapter = new EpoxyControllerAdapter(this, diffingHandler);
    modelBuildHandler = modelBuildingHandler;
    setDebugLoggingEnabled(globalDebugLoggingEnabled);
  }

  /**
   * Posting and canceling runnables is a bit expensive - it is synchronizes and iterates the
   * list of runnables. We want clients to be able to request model builds as often as they want and
   * have it act as a no-op if one is already requested, without being a performance hit. To do that
   * we track whether we have a call to build models posted already so we can avoid canceling a
   * current call and posting it again.
   */
  @RequestedModelBuildType private volatile int requestedModelBuildType =
      RequestedModelBuildType.NONE;

  @Retention(RetentionPolicy.SOURCE)
  @IntDef({RequestedModelBuildType.NONE,
      RequestedModelBuildType.NEXT_FRAME,
      RequestedModelBuildType.DELAYED})
  private @interface RequestedModelBuildType {
    int NONE = 0;
    /** A request has been made to build models immediately. It is posted. */
    int NEXT_FRAME = 1;
    /** A request has been made to build models after a delay. It is post delayed. */
    int DELAYED = 2;
  }

  /**
   * Call this to request a model update. The controller will schedule a call to {@link
   * #buildModels()} so that models can be rebuilt for the current data. Once a build is requested
   * all subsequent requests are ignored until the model build runs. Therefore, the calling code
   * need not worry about calling this multiple times in a row.
   * <p>
   * The exception is that the first time this is called on a new instance of {@link
   * EpoxyController} it is run synchronously. This allows state to be restored and the initial view
   * to be draw quicker.
   * <p>
   * If you would like to be alerted when models have finished building use
   * {@link #addModelBuildListener(OnModelBuildFinishedListener)}
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
      buildModelsRunnable.run();
    }
  }

  /**
   * Whether an update to models is currently pending. This can either be because
   * {@link #requestModelBuild()} was called, or because models are currently being built or diff
   * on a background thread.
   */
  public boolean hasPendingModelBuild() {
    return requestedModelBuildType != RequestedModelBuildType.NONE // model build is posted
        || threadBuildingModels != null // model build is in progress
        || adapter.isDiffInProgress(); // Diff in progress
  }

  /**
   * Add a listener that will be called every time {@link #buildModels()} has finished running
   * and changes have been dispatched to the RecyclerView.
   * <p>
   * Since buildModels can be called once for many calls to {@link #requestModelBuild()}, this is
   * called just once for each buildModels execution, not for every request.
   * <p>
   * Use this to react to changes in your models that need to happen after the RecyclerView has
   * been notified, such as scrolling.
   */
  public void addModelBuildListener(OnModelBuildFinishedListener listener) {
    adapter.addModelBuildListener(listener);
  }

  /**
   * Remove a listener added with {@link #addModelBuildListener(OnModelBuildFinishedListener)}.
   * This is safe to call from inside the callback
   * {@link OnModelBuildFinishedListener#onModelBuildFinished(DiffResult)}
   */
  public void removeModelBuildListener(OnModelBuildFinishedListener listener) {
    adapter.removeModelBuildListener(listener);
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
   * If a previous request is still pending it will be removed in favor of this new delay
   * <p>
   * Any call to {@link #requestModelBuild()} will override a delayed request.
   * <p>
   * In most cases you should use {@link #requestModelBuild()} instead of this.
   *
   * @param delayMs The time in milliseconds to delay the model build by. Should be greater than or
   *                equal to 0. A value of 0 is equivalent to calling {@link #requestModelBuild()}
   */
  public synchronized void requestDelayedModelBuild(int delayMs) {
    if (isBuildingModels()) {
      throw new IllegalEpoxyUsage(
          "Cannot call `requestDelayedModelBuild` from inside `buildModels`");
    }

    if (requestedModelBuildType == RequestedModelBuildType.DELAYED) {
      cancelPendingModelBuild();
    } else if (requestedModelBuildType == RequestedModelBuildType.NEXT_FRAME) {
      return;
    }

    requestedModelBuildType =
        delayMs == 0 ? RequestedModelBuildType.NEXT_FRAME : RequestedModelBuildType.DELAYED;

    modelBuildHandler.postDelayed(buildModelsRunnable, delayMs);
  }

  /**
   * Cancels a pending call to {@link #buildModels()} if one has been queued by {@link
   * #requestModelBuild()}.
   */
  public synchronized void cancelPendingModelBuild() {
    // Access to requestedModelBuildType is synchronized because the model building thread clears
    // it when model building starts, and the main thread needs to set it to indicate a build
    // request.
    // Additionally, it is crucial to guarantee that the state of requestedModelBuildType is in sync
    // with the modelBuildHandler, otherwise we could end up in a state where we think a model build
    // is queued, but it isn't, and model building never happens - stuck forever.
    if (requestedModelBuildType != RequestedModelBuildType.NONE) {
      requestedModelBuildType = RequestedModelBuildType.NONE;
      modelBuildHandler.removeCallbacks(buildModelsRunnable);
    }
  }

  private final Runnable buildModelsRunnable = new Runnable() {
    @Override
    public void run() {
      // Do this first to mark the controller as being in the model building process.
      threadBuildingModels = Thread.currentThread();

      // This is needed to reset the requestedModelBuildType back to NONE.
      // As soon as we do this another model build can be posted.
      cancelPendingModelBuild();

      helper.resetAutoModels();

      modelsBeingBuilt = new ControllerModelList(getExpectedModelCount());

      timer.start("Models built");

      // The user's implementation of buildModels is wrapped in a try/catch so that if it fails
      // we can reset the state of this controller. This is useful when model building is done
      // on a dedicated thread, which may have its own error handler, and a failure may not
      // crash the app - in which case this controller would be in an invalid state and crash later
      // with confusing errors because "threadBuildingModels" and other properties are not
      // correctly set. This can happen particularly with Espresso testing.
      try {
        buildModels();
      } catch (Throwable throwable) {
        timer.stop();
        modelsBeingBuilt = null;
        hasBuiltModelsEver = true;
        threadBuildingModels = null;
        stagedModel = null;
        throw throwable;
      }

      addCurrentlyStagedModelIfExists();
      timer.stop();

      runInterceptors();
      filterDuplicatesIfNeeded(modelsBeingBuilt);
      modelsBeingBuilt.freeze();

      timer.start("Models diffed");
      adapter.setModels(modelsBeingBuilt);
      // This timing is only right if diffing and model building are on the same thread
      timer.stop();

      modelsBeingBuilt = null;
      hasBuiltModelsEver = true;
      threadBuildingModels = null;
    }
  };

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
    assertIsBuildingModels();

    int size = modelsBeingBuilt.size();
    for (int i = 0; i < size; i++) {
      if (modelsBeingBuilt.get(i) == model) {
        return i;
      }
    }

    return -1;
  }

  boolean isModelAddedMultipleTimes(EpoxyModel<?> model) {
    assertIsBuildingModels();

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
    assertIsBuildingModels();

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

      timer.start("Interceptors executed");

      for (Interceptor interceptor : interceptors) {
        interceptor.intercept(modelsBeingBuilt);
      }

      timer.stop();

      if (modelInterceptorCallbacks != null) {
        for (ModelInterceptorCallback callback : modelInterceptorCallbacks) {
          callback.onInterceptorsFinished(this);
        }
      }
    }

    // Interceptors are cleared so that future model builds don't notify past models.
    // We need to make sure they are cleared even if there are no interceptors so that
    // we don't leak the models.
    modelInterceptorCallbacks = null;
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
    void intercept(@NonNull List<EpoxyModel<?>> models);
  }

  /**
   * Add an interceptor callback to be run after models are built, to make any last changes before
   * they are set on the adapter. Interceptors are run in the order they are added.
   * <p>
   * Interceptors are run on the same thread that models are built on.
   *
   * @see Interceptor#intercept(List)
   */
  public void addInterceptor(@NonNull Interceptor interceptor) {
    interceptors.add(interceptor);
  }

  /** Remove an interceptor that was added with {@link #addInterceptor(Interceptor)}. */
  public void removeInterceptor(@NonNull Interceptor interceptor) {
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
    assertIsBuildingModels();
    return modelsBeingBuilt.size();
  }

  private void assertIsBuildingModels() {
    if (!isBuildingModels()) {
      throw new IllegalEpoxyUsage("Can only call this when inside the `buildModels` method");
    }
  }

  private void assertNotBuildingModels() {
    if (isBuildingModels()) {
      throw new IllegalEpoxyUsage("Cannot call this from inside `buildModels`");
    }
  }

  /**
   * Add the model to this controller. Can only be called from inside {@link
   * EpoxyController#buildModels()}.
   */
  public void add(@NonNull EpoxyModel<?> model) {
    model.addTo(this);
  }

  /**
   * Add the models to this controller. Can only be called from inside {@link
   * EpoxyController#buildModels()}.
   */
  protected void add(@NonNull EpoxyModel<?>... modelsToAdd) {
    modelsBeingBuilt.ensureCapacity(modelsBeingBuilt.size() + modelsToAdd.length);

    for (EpoxyModel<?> model : modelsToAdd) {
      add(model);
    }
  }

  /**
   * Add the models to this controller. Can only be called from inside {@link
   * EpoxyController#buildModels()}.
   */
  protected void add(@NonNull List<? extends EpoxyModel<?>> modelsToAdd) {
    modelsBeingBuilt.ensureCapacity(modelsBeingBuilt.size() + modelsToAdd.size());

    for (EpoxyModel<?> model : modelsToAdd) {
      add(model);
    }
  }

  /**
   * Method to actually add the model to the list being built. Should be called after all
   * validations are done.
   */
  void addInternal(EpoxyModel<?> modelToAdd) {
    assertIsBuildingModels();

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
   * This only works for AutoModels, and only if implicitly adding is enabled in configuration.
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

  /** True if the current callstack originated from the buildModels call, on the same thread. */
  protected boolean isBuildingModels() {
    return threadBuildingModels == Thread.currentThread();
  }

  private void filterDuplicatesIfNeeded(List<EpoxyModel<?>> models) {
    if (!filterDuplicates) {
      return;
    }

    timer.start("Duplicates filtered");
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

    timer.stop();
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
   * server may erroneously send duplicate items. Duplicate items are otherwise left in and can
   * result in undefined behavior.
   */
  public void setFilterDuplicates(boolean filterDuplicates) {
    this.filterDuplicates = filterDuplicates;
  }

  public boolean isDuplicateFilteringEnabled() {
    return filterDuplicates;
  }

  /**
   * {@link #setFilterDuplicates(boolean)} is disabled in each EpoxyController by default. It can be
   * toggled individually in each controller, or alternatively you can use this to change the
   * default value for all EpoxyControllers.
   */
  public static void setGlobalDuplicateFilteringDefault(boolean filterDuplicatesByDefault) {
    EpoxyController.filterDuplicatesDefault = filterDuplicatesByDefault;
  }

  /**
   * If enabled, DEBUG logcat messages will be printed to show when models are rebuilt, the time
   * taken to build them, the time taken to diff them, and the item change outcomes from the
   * differ. The tag of the logcat message is the class name of your EpoxyController.
   * <p>
   * This is useful to verify that models are being diffed as expected, as well as to watch for
   * slowdowns in model building or diffing to indicate when you should optimize model building or
   * model hashCode/equals implementations (which can often slow down diffing).
   * <p>
   * This should only be used in debug builds to avoid a performance hit in prod.
   */
  public void setDebugLoggingEnabled(boolean enabled) {
    assertNotBuildingModels();

    if (enabled) {
      timer = new DebugTimer(getClass().getSimpleName());
      if (debugObserver == null) {
        debugObserver = new EpoxyDiffLogger(getClass().getSimpleName());
      }
      adapter.registerAdapterDataObserver(debugObserver);
    } else {
      timer = NO_OP_TIMER;
      if (debugObserver != null) {
        adapter.unregisterAdapterDataObserver(debugObserver);
      }
    }
  }

  public boolean isDebugLoggingEnabled() {
    return timer != NO_OP_TIMER;
  }

  /**
   * Similar to {@link #setDebugLoggingEnabled(boolean)}, but this changes the global default for
   * all EpoxyControllers.
   * <p>
   * The default is false.
   */
  public static void setGlobalDebugLoggingEnabled(boolean globalDebugLoggingEnabled) {
    EpoxyController.globalDebugLoggingEnabled = globalDebugLoggingEnabled;
  }

  /**
   * An optimized way to move a model from one position to another without rebuilding all models.
   * This is intended to be used with {@link androidx.recyclerview.widget.ItemTouchHelper} to
   * allow for efficient item dragging and rearranging. It cannot be
   * <p>
   * If you call this you MUST also update the data backing your models as necessary.
   * <p>
   * This will immediately change the model's position and notify the change to the RecyclerView.
   * However, a delayed request to rebuild models will be scheduled for the future to guarantee that
   * models are in sync with data.
   *
   * @param fromPosition Previous position of the item.
   * @param toPosition   New position of the item.
   */
  public void moveModel(int fromPosition, int toPosition) {
    assertNotBuildingModels();

    adapter.moveModel(fromPosition, toPosition);

    requestDelayedModelBuild(500);
  }


  /**
   * An way to notify the adapter that a model has changed. This is intended to be used with
   * {@link androidx.recyclerview.widget.ItemTouchHelper} to allow revert swiping a model.
   * <p>
   * This will immediately notify the change to the RecyclerView.
   *
   * @param position Position of the item.
   */
  public void notifyModelChanged(int position) {
    assertNotBuildingModels();

    adapter.notifyModelChanged(position);
  }


  /**
   * Get the underlying adapter built by this controller. Use this to get the adapter to set on a
   * RecyclerView, or to get information about models currently in use.
   */
  @NonNull
  public EpoxyControllerAdapter getAdapter() {
    return adapter;
  }

  public void onSaveInstanceState(@NonNull Bundle outState) {
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
  @NonNull
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
   * This is called when recoverable exceptions occur at runtime. By default they are ignored and
   * Epoxy will recover, but you can override this to be aware of when they happen.
   * <p>
   * A common use for this is being aware of duplicates when {@link #setFilterDuplicates(boolean)}
   * is enabled.
   * <p>
   * By default the global exception handler provided by
   * {@link #setGlobalExceptionHandler(ExceptionHandler)}
   * is called with the exception. Overriding this allows you to provide your own handling for a
   * controller.
   */
  protected void onExceptionSwallowed(@NonNull RuntimeException exception) {
    globalExceptionHandler.onException(this, exception);
  }

  /**
   * Default handler for exceptions in all EpoxyControllers. Set with {@link
   * #setGlobalExceptionHandler(ExceptionHandler)}
   */
  private static ExceptionHandler globalExceptionHandler =
      new ExceptionHandler() {

        @Override
        public void onException(@NonNull EpoxyController controller,
            @NonNull RuntimeException exception) {
          // Ignore exceptions as the default
        }
      };

  /**
   * Set a callback to be notified when a recoverable exception occurs at runtime.  By default these
   * are ignored and Epoxy will recover, but you can override this to be aware of when they happen.
   * <p>
   * For example, you could choose to rethrow the exception in development builds, or log them in
   * production.
   * <p>
   * A common use for this is being aware of duplicates when {@link #setFilterDuplicates(boolean)}
   * is enabled.
   * <p>
   * This callback will be used in all EpoxyController classes. If you would like specific handling
   * in a certain controller you can override {@link #onExceptionSwallowed(RuntimeException)} in
   * that controller.
   */
  public static void setGlobalExceptionHandler(
      @NonNull ExceptionHandler globalExceptionHandler) {
    EpoxyController.globalExceptionHandler = globalExceptionHandler;
  }

  public interface ExceptionHandler {
    /**
     * This is called when recoverable exceptions happen at runtime. They can be ignored and Epoxy
     * will recover, but you can override this to be aware of when they happen.
     * <p>
     * For example, you could choose to rethrow the exception in development builds, or log them in
     * production.
     *
     * @param controller The EpoxyController that the error occurred in.
     */
    void onException(@NonNull EpoxyController controller, @NonNull RuntimeException exception);
  }

  void onAttachedToRecyclerViewInternal(RecyclerView recyclerView) {
    recyclerViewAttachCount++;

    if (recyclerViewAttachCount > 1) {
      MainThreadExecutor.INSTANCE.handler.postDelayed(new Runnable() {
        @Override
        public void run() {
          // Only warn if there are still multiple adapters attached after a delay, to allow for
          // a grace period
          if (recyclerViewAttachCount > 1) {
            onExceptionSwallowed(new IllegalStateException(
                "This EpoxyController had its adapter added to more than one ReyclerView. Epoxy "
                    + "does not support attaching an adapter to multiple RecyclerViews because "
                    + "saved state will not work properly. If you did not intend to attach your "
                    + "adapter "
                    + "to multiple RecyclerViews you may be leaking a "
                    + "reference to a previous RecyclerView. Make sure to remove the adapter from "
                    + "any "
                    + "previous RecyclerViews (eg if the adapter is reused in a Fragment across "
                    + "multiple onCreateView/onDestroyView cycles). See https://github"
                    + ".com/airbnb/epoxy/wiki/Avoiding-Memory-Leaks for more information."));
          }
        }
      }, DELAY_TO_CHECK_ADAPTER_COUNT_MS);
    }

    onAttachedToRecyclerView(recyclerView);
  }

  void onDetachedFromRecyclerViewInternal(RecyclerView recyclerView) {
    recyclerViewAttachCount--;
    onDetachedFromRecyclerView(recyclerView);
  }

  /** Called when the controller's adapter is attach to a recyclerview. */
  protected void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {

  }

  /** Called when the controller's adapter is detached from a recyclerview. */
  protected void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {

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
  protected void onModelBound(@NonNull EpoxyViewHolder holder, @NonNull EpoxyModel<?> boundModel,
      int position,
      @Nullable EpoxyModel<?> previouslyBoundModel) {
  }

  /**
   * Called immediately after a model is unbound from a view holder. Subclasses can override this if
   * they want alerts on when a model is unbound. Alternatively you may attach a listener directly
   * to a generated model with model.onUnbind(...)
   */
  protected void onModelUnbound(@NonNull EpoxyViewHolder holder, @NonNull EpoxyModel<?> model) {

  }

  /**
   * Called when the given viewholder is attached to the window, along with the model it is bound
   * to.
   *
   * @see BaseEpoxyAdapter#onViewAttachedToWindow(EpoxyViewHolder)
   */
  protected void onViewAttachedToWindow(@NonNull EpoxyViewHolder holder,
      @NonNull EpoxyModel<?> model) {

  }

  /**
   * Called when the given viewholder is detechaed from the window, along with the model it is bound
   * to.
   *
   * @see BaseEpoxyAdapter#onViewDetachedFromWindow(EpoxyViewHolder)
   */
  protected void onViewDetachedFromWindow(@NonNull EpoxyViewHolder holder,
      @NonNull EpoxyModel<?> model) {

  }

  //region Sticky header

  /**
   * Optional callback to setup the sticky view,
   * by default it doesn't do anything.
   *
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
   *
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
   *
   * The sub-classes should override the function if they are
   * using sticky header feature.
   */
  @Override
  public boolean isStickyHeader(int position) {
    return false;
  }

  //endregion
}
