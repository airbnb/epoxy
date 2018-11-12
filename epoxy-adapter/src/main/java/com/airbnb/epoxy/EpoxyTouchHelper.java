package com.airbnb.epoxy;

import android.graphics.Canvas;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import static androidx.recyclerview.widget.ItemTouchHelper.Callback.makeMovementFlags;

/**
 * A simple way to set up drag or swipe interactions with Epoxy.
 * <p>
 * Drag events work with the EpoxyController and automatically update the controller and
 * RecyclerView when an item is moved. You just need to implement a callback to update your data to
 * reflect the change.
 * <p>
 * Both swipe and drag events implement a small lifecycle to help you style the views as they are
 * moved. You can register callbacks for the lifecycle events you care about.
 * <p>
 * If you want to set up multiple drag and swipe rules for the same RecyclerView, you can use this
 * class multiple times to specify different targets or swipe and drag directions and callbacks.
 * <p>
 * If you want more control over configuration and handling, you can opt to not use this class and
 * instead you can implement {@link EpoxyModelTouchCallback} directly with your own {@link
 * ItemTouchHelper}. That class provides an interface that makes it easier to work with Epoxy models
 * and simplifies touch callbacks.
 * <p>
 * If you want even more control you can implement {@link EpoxyTouchHelperCallback}. This is just a
 * light layer over the normal RecyclerView touch callbacks, but it converts all view holders to
 * Epoxy view holders to remove some boilerplate for you.
 */
public abstract class EpoxyTouchHelper {

  /**
   * The entry point for setting up drag support.
   *
   * @param controller The EpoxyController with the models that will be dragged. The controller will
   *                   be updated for you when a model is dragged and moved by a user's touch
   *                   interaction.
   */
  public static DragBuilder initDragging(EpoxyController controller) {
    return new DragBuilder(controller);
  }

  public static class DragBuilder {

    private final EpoxyController controller;

    private DragBuilder(EpoxyController controller) {
      this.controller = controller;
    }

    /**
     * The recyclerview that the EpoxyController has its adapter added to. An {@link
     * androidx.recyclerview.widget.ItemTouchHelper} will be created and configured for you, and
     * attached to this RecyclerView.
     */
    public DragBuilder2 withRecyclerView(RecyclerView recyclerView) {
      return new DragBuilder2(controller, recyclerView);
    }
  }

  public static class DragBuilder2 {

    private final EpoxyController controller;
    private final RecyclerView recyclerView;

    private DragBuilder2(EpoxyController controller, RecyclerView recyclerView) {
      this.controller = controller;
      this.recyclerView = recyclerView;
    }

    /** Enable dragging vertically, up and down. */
    public DragBuilder3 forVerticalList() {
      return withDirections(ItemTouchHelper.UP | ItemTouchHelper.DOWN);
    }

    /** Enable dragging horizontally, left and right. */
    public DragBuilder3 forHorizontalList() {
      return withDirections(ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
    }

    /** Enable dragging in all directions. */
    public DragBuilder3 forGrid() {
      return withDirections(ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT
          | ItemTouchHelper.RIGHT);
    }

    /**
     * Set custom movement flags to dictate which drag directions should be allowed.
     * <p>
     * Can be any of {@link ItemTouchHelper#LEFT}, {@link ItemTouchHelper#RIGHT}, {@link
     * ItemTouchHelper#UP}, {@link ItemTouchHelper#DOWN}, {@link ItemTouchHelper#START}, {@link
     * ItemTouchHelper#END}
     * <p>
     * Flags can be OR'd together to allow multiple directions.
     */
    public DragBuilder3 withDirections(int directionFlags) {
      return new DragBuilder3(controller, recyclerView, makeMovementFlags(directionFlags, 0));
    }
  }

  public static class DragBuilder3 {

    private final EpoxyController controller;
    private final RecyclerView recyclerView;
    private final int movementFlags;

    private DragBuilder3(EpoxyController controller, RecyclerView recyclerView, int movementFlags) {
      this.controller = controller;
      this.recyclerView = recyclerView;
      this.movementFlags = movementFlags;
    }

    /**
     * Set the type of Epoxy model that is draggable. This approach works well if you only have one
     * draggable type.
     */
    public <U extends EpoxyModel> DragBuilder4<U> withTarget(Class<U> targetModelClass) {
      List<Class<? extends EpoxyModel>> targetClasses = new ArrayList<>(1);
      targetClasses.add(targetModelClass);

      return new DragBuilder4<>(controller, recyclerView, movementFlags, targetModelClass,
          targetClasses);
    }

    /**
     * Specify which Epoxy model types are draggable. Use this if you have more than one type that
     * is draggable.
     * <p>
     * If you only have one draggable type you should use {@link #withTarget(Class)}
     */
    public DragBuilder4<EpoxyModel> withTargets(Class<? extends EpoxyModel>... targetModelClasses) {
      return new DragBuilder4<>(controller, recyclerView, movementFlags, EpoxyModel.class,
          Arrays.asList(targetModelClasses));
    }

    /**
     * Use this if all models in the controller should be draggable, and if there are multiple types
     * of models in the controller.
     * <p>
     * If you only have one model type you should use {@link #withTarget(Class)}
     */
    public DragBuilder4<EpoxyModel> forAllModels() {
      return withTarget(EpoxyModel.class);
    }
  }

  public static class DragBuilder4<U extends EpoxyModel> {

    private final EpoxyController controller;
    private final RecyclerView recyclerView;
    private final int movementFlags;
    private final Class<U> targetModelClass;
    private final List<Class<? extends EpoxyModel>> targetModelClasses;

    private DragBuilder4(EpoxyController controller,
        RecyclerView recyclerView, int movementFlags,
        Class<U> targetModelClass, List<Class<? extends EpoxyModel>> targetModelClasses) {

      this.controller = controller;
      this.recyclerView = recyclerView;
      this.movementFlags = movementFlags;
      this.targetModelClass = targetModelClass;
      this.targetModelClasses = targetModelClasses;
    }

    /**
     * Set callbacks to handle drag actions and lifecycle events.
     * <p>
     * You MUST implement {@link DragCallbacks#onModelMoved(int, int, EpoxyModel,
     * View)} to update your data to reflect an item move.
     * <p>
     * You can optionally implement the other callbacks to modify the view being dragged. This is
     * useful if you want to change things like the view background, size, color, etc
     *
     * @return An {@link ItemTouchHelper} instance that has been initialized and attached to a
     * recyclerview. The touch helper has already been fully set up and can be ignored, but you may
     * want to hold a reference to it if you need to later detach the recyclerview to disable touch
     * events via setting null on {@link ItemTouchHelper#attachToRecyclerView(RecyclerView)}
     */
    public ItemTouchHelper andCallbacks(final DragCallbacks<U> callbacks) {
      ItemTouchHelper itemTouchHelper =
          new ItemTouchHelper(new EpoxyModelTouchCallback<U>(controller, targetModelClass) {

            @Override
            public int getMovementFlagsForModel(U model, int adapterPosition) {
              return movementFlags;
            }

            @Override
            protected boolean isTouchableModel(EpoxyModel<?> model) {
              boolean isTargetType = targetModelClasses.size() == 1
                  ? super.isTouchableModel(model)
                  : targetModelClasses.contains(model.getClass());

              //noinspection unchecked
              return isTargetType && callbacks.isDragEnabledForModel((U) model);
            }

            @Override
            public void onDragStarted(U model, View itemView, int adapterPosition) {
              callbacks.onDragStarted(model, itemView, adapterPosition);
            }

            @Override
            public void onDragReleased(U model, View itemView) {
              callbacks.onDragReleased(model, itemView);
            }

            @Override
            public void onModelMoved(int fromPosition, int toPosition, U modelBeingMoved,
                View itemView) {
              callbacks.onModelMoved(fromPosition, toPosition, modelBeingMoved, itemView);
            }

            @Override
            public void clearView(U model, View itemView) {
              callbacks.clearView(model, itemView);
            }
          });

      itemTouchHelper.attachToRecyclerView(recyclerView);

      return itemTouchHelper;
    }
  }

  public abstract static class DragCallbacks<T extends EpoxyModel>
      implements EpoxyDragCallback<T> {

    @Override
    public void onDragStarted(T model, View itemView, int adapterPosition) {

    }

    @Override
    public void onDragReleased(T model, View itemView) {

    }

    @Override
    public abstract void onModelMoved(int fromPosition, int toPosition, T modelBeingMoved,
        View itemView);

    @Override
    public void clearView(T model, View itemView) {

    }

    /**
     * Whether the given model should be draggable.
     * <p>
     * True by default. You may override this to toggle draggability for a model.
     */
    public boolean isDragEnabledForModel(T model) {
      return true;
    }

    @Override
    public final int getMovementFlagsForModel(T model, int adapterPosition) {
      // No-Op this is not used
      return 0;
    }
  }

  /**
   * The entry point for setting up swipe support for a RecyclerView. The RecyclerView must be set
   * with an Epoxy adapter or controller.
   */
  public static SwipeBuilder initSwiping(RecyclerView recyclerView) {
    return new SwipeBuilder(recyclerView);
  }

  public static class SwipeBuilder {

    private final RecyclerView recyclerView;

    private SwipeBuilder(RecyclerView recyclerView) {
      this.recyclerView = recyclerView;
    }

    /** Enable swiping right. */
    public SwipeBuilder2 right() {
      return withDirections(ItemTouchHelper.RIGHT);
    }

    /** Enable swiping left. */
    public SwipeBuilder2 left() {
      return withDirections(ItemTouchHelper.LEFT);
    }

    /** Enable swiping horizontally, left and right. */
    public SwipeBuilder2 leftAndRight() {
      return withDirections(ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
    }

    /**
     * Set custom movement flags to dictate which swipe directions should be allowed.
     * <p>
     * Can be any of {@link ItemTouchHelper#LEFT}, {@link ItemTouchHelper#RIGHT}, {@link
     * ItemTouchHelper#UP}, {@link ItemTouchHelper#DOWN}, {@link ItemTouchHelper#START}, {@link
     * ItemTouchHelper#END}
     * <p>
     * Flags can be OR'd together to allow multiple directions.
     */
    public SwipeBuilder2 withDirections(int directionFlags) {
      return new SwipeBuilder2(recyclerView, makeMovementFlags(0, directionFlags));
    }
  }

  public static class SwipeBuilder2 {

    private final RecyclerView recyclerView;
    private final int movementFlags;

    private SwipeBuilder2(RecyclerView recyclerView,
        int movementFlags) {
      this.recyclerView = recyclerView;
      this.movementFlags = movementFlags;
    }

    /**
     * Set the type of Epoxy model that is swipable. Use this if you only have one
     * swipable type.
     */
    public <U extends EpoxyModel> SwipeBuilder3<U> withTarget(Class<U> targetModelClass) {
      List<Class<? extends EpoxyModel>> targetClasses = new ArrayList<>(1);
      targetClasses.add(targetModelClass);

      return new SwipeBuilder3<>(recyclerView, movementFlags, targetModelClass,
          targetClasses);
    }

    /**
     * Specify which Epoxy model types are swipable. Use this if you have more than one type that
     * is swipable.
     * <p>
     * If you only have one swipable type you should use {@link #withTarget(Class)}
     */
    public SwipeBuilder3<EpoxyModel> withTargets(
        Class<? extends EpoxyModel>... targetModelClasses) {
      return new SwipeBuilder3<>(recyclerView, movementFlags, EpoxyModel.class,
          Arrays.asList(targetModelClasses));
    }

    /**
     * Use this if all models in the controller should be swipable, and if there are multiple types
     * of models in the controller.
     * <p>
     * If you only have one model type you should use {@link #withTarget(Class)}
     */
    public SwipeBuilder3<EpoxyModel> forAllModels() {
      return withTarget(EpoxyModel.class);
    }
  }

  public static class SwipeBuilder3<U extends EpoxyModel> {

    private final RecyclerView recyclerView;
    private final int movementFlags;
    private final Class<U> targetModelClass;
    private final List<Class<? extends EpoxyModel>> targetModelClasses;

    private SwipeBuilder3(
        RecyclerView recyclerView, int movementFlags,
        Class<U> targetModelClass, List<Class<? extends EpoxyModel>> targetModelClasses) {

      this.recyclerView = recyclerView;
      this.movementFlags = movementFlags;
      this.targetModelClass = targetModelClass;
      this.targetModelClasses = targetModelClasses;
    }

    /**
     * Set callbacks to handle swipe actions and lifecycle events.
     * <p>
     * You MUST implement {@link SwipeCallbacks#onSwipeCompleted(EpoxyModel, View, int, int)} to
     * remove the swiped item from your data and request a model build.
     * <p>
     * You can optionally implement the other callbacks to modify the view as it is being swiped.
     *
     * @return An {@link ItemTouchHelper} instance that has been initialized and attached to a
     * recyclerview. The touch helper has already been fully set up and can be ignored, but you may
     * want to hold a reference to it if you need to later detach the recyclerview to disable touch
     * events via setting null on {@link ItemTouchHelper#attachToRecyclerView(RecyclerView)}
     */
    public ItemTouchHelper andCallbacks(final SwipeCallbacks<U> callbacks) {
      ItemTouchHelper itemTouchHelper =
          new ItemTouchHelper(new EpoxyModelTouchCallback<U>(null, targetModelClass) {

            @Override
            public int getMovementFlagsForModel(U model, int adapterPosition) {
              return movementFlags;
            }

            @Override
            protected boolean isTouchableModel(EpoxyModel<?> model) {
              boolean isTargetType = targetModelClasses.size() == 1
                  ? super.isTouchableModel(model)
                  : targetModelClasses.contains(model.getClass());

              //noinspection unchecked
              return isTargetType && callbacks.isSwipeEnabledForModel((U) model);
            }

            @Override
            public void onSwipeStarted(U model, View itemView, int adapterPosition) {
              callbacks.onSwipeStarted(model, itemView, adapterPosition);
            }

            @Override
            public void onSwipeProgressChanged(U model, View itemView, float swipeProgress,
                Canvas canvas) {
              callbacks.onSwipeProgressChanged(model, itemView, swipeProgress, canvas);
            }

            @Override
            public void onSwipeCompleted(U model, View itemView, int position, int direction) {
              callbacks.onSwipeCompleted(model, itemView, position, direction);
            }

            @Override
            public void onSwipeReleased(U model, View itemView) {
              callbacks.onSwipeReleased(model, itemView);
            }

            @Override
            public void clearView(U model, View itemView) {
              callbacks.clearView(model, itemView);
            }
          });

      itemTouchHelper.attachToRecyclerView(recyclerView);

      return itemTouchHelper;
    }
  }

  public abstract static class SwipeCallbacks<T extends EpoxyModel>
      implements EpoxySwipeCallback<T> {

    @Override
    public void onSwipeStarted(T model, View itemView, int adapterPosition) {

    }

    @Override
    public void onSwipeProgressChanged(T model, View itemView, float swipeProgress,
        Canvas canvas) {

    }

    @Override
    public abstract void onSwipeCompleted(T model, View itemView, int position, int direction);

    @Override
    public void onSwipeReleased(T model, View itemView) {

    }

    @Override
    public void clearView(T model, View itemView) {

    }

    /**
     * Whether the given model should be swipable.
     * <p>
     * True by default. You may override this to toggle swipabaility for a model.
     */
    public boolean isSwipeEnabledForModel(T model) {
      return true;
    }

    @Override
    public final int getMovementFlagsForModel(T model, int adapterPosition) {
      // Not used
      return 0;
    }
  }
}
