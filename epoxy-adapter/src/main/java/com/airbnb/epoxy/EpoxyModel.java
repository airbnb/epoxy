package com.airbnb.epoxy;

import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.airbnb.epoxy.EpoxyController.AfterInterceptorCallback;

import java.util.List;

/**
 * Helper to bind data to a view using a builder style. The parameterized type should extend
 * Android's View.
 */
public abstract class EpoxyModel<T> {

  /**
   * Counts how many of these objects are created, so that each new object can have a unique id .
   * Uses negative values so that these autogenerated ids don't clash with database ids that may be
   * set with {@link #id(long)}
   */
  private static long idCounter = -1;

  /**
   * An id that can be used to uniquely identify this {@link EpoxyModel} for use in RecyclerView
   * stable ids. It defaults to a unique id for this object instance, if you want to maintain the
   * same id across instances use {@link #id(long)}
   */
  private long id;
  @LayoutRes private int layout;
  private boolean shown = true;
  /**
   * Set to true once this model is diffed in an adapter. Used to ensure that this model's id
   * doesn't change after being diffed.
   */
  boolean addedToAdapter;
  /**
   * The controller this model was added to. A reference is kept in debug mode in order to run
   * validations.
   */
  private EpoxyController attachedController;
  private int hashCodeWhenAdded;
  private boolean hasDefaultId;

  protected EpoxyModel(long id) {
    id(id);
  }

  public EpoxyModel() {
    this(idCounter--);
    hasDefaultId = true;
  }

  boolean hasDefaultId() {
    return hasDefaultId;
  }

  /**
   * Get the view type to associate with this model in the recyclerview. For models that use a
   * layout resource, the view type is simply the layout resource value.
   *
   * @see android.support.v7.widget.RecyclerView.Adapter#getItemViewType(int)
   */
  int getViewType() {
    return getLayout();
  }

  View buildView(ViewGroup parent) {
    return LayoutInflater.from(parent.getContext()).inflate(getLayout(), parent, false);
  }

  /**
   * Binds the current data to the given view. You should bind all fields including unset/empty
   * fields to ensure proper recycling.
   */
  public void bind(T view) {

  }

  /**
   * Similar to {@link #bind(Object)}, but provides a non null, non empty list of payloads
   * describing what changed. This is the payloads list specified in the adapter's notifyItemChanged
   * method. This is a useful optimization to allow you to only change part of a view instead of
   * updating the whole thing, which may prevent unnecessary layout calls. If there are no payloads
   * then {@link #bind(Object)} is called instead. This will only be used if the model is used with
   * an {@link EpoxyAdapter}
   */
  public void bind(T view, List<Object> payloads) {
    bind(view);
  }

  /**
   * Similar to {@link #bind(Object)}, but provides a non null model which was previously bound to
   * this view. This will only be called if the model is used with an {@link EpoxyController}.
   *
   * @param previouslyBoundModel This is a model with the same id that was previously bound. You can
   *                             compare this previous model with the current one to see exactly
   *                             what changed.
   *                             <p>
   *                             This model and the previously bound model are guaranteed to have
   *                             the same id, but will not necessarily be of the same type depending
   *                             on your implementation of {@link EpoxyController#buildModels()}.
   *                             With common usage patterns of Epoxy they should be the same type,
   *                             and will only differ if you are using different model classes with
   *                             the same id.
   *                             <p>
   *                             Comparing the newly bound model with the previous model allows you
   *                             to be more intelligent when binding your view. This may help you
   *                             optimize view binding, or make it easier to work with animations.
   *                             <p>
   *                             If the new model and the previous model have the same view type
   *                             (given by {@link EpoxyModel#getViewType()}), and if you are using
   *                             the default ReyclerView item animator, the same view will be
   *                             reused. This means that you only need to update the view to reflect
   *                             the data that changed. If you are using a custom item animator then
   *                             the view will be the same if the animator returns true in
   *                             canReuseUpdatedViewHolder.
   *                             <p>
   *                             This previously bound model is taken as a payload from the diffing
   *                             process, and follows the same general conditions for all
   *                             recyclerview change payloads.
   */
  public void bind(T view, EpoxyModel<?> previouslyBoundModel) {
    bind(view);
  }

  /**
   * Called when the view bound to this model is recycled. Subclasses can override this if their
   * view should release resources when it's recycled.
   * <p>
   * Note that {@link #bind(Object)} can be called multiple times without an unbind call in between
   * if the view has remained on screen to be reused across item changes. This means that you should
   * not rely on unbind to clear a view or model's state before bind is called again.
   *
   * @see EpoxyAdapter#onViewRecycled(EpoxyViewHolder)
   */
  public void unbind(T view) {
  }

  public long id() {
    return id;
  }

  /**
   * Override the default id in cases where the data subject naturally has an id, like an object
   * from a database. This id can only be set before the model is added to the adapter, it is an
   * error to change the id after that.
   */
  public EpoxyModel<T> id(long id) {
    if ((addedToAdapter || attachedController != null) && id != this.id) {
      throw new IllegalEpoxyUsage(
          "Cannot change a model's id after it has been added to the adapter.");
    }

    hasDefaultId = false;
    this.id = id;
    return this;
  }

  /**
   * Use a string as the model id. Useful for models that don't clearly map to a numerical id. This
   * is preferable to using {@link String#hashCode()} because that is a 32 bit hash and this is a 64
   * bit hash, giving better spread and less chance of collision with other ids.
   * <p>
   * Since this uses a hashcode method to convert the String to a long there is a very small chance
   * that you may have a collision with another id. Assuming an even spread of hashcodes, and
   * several hundred models in the adapter, there would be roughly 1 in 100 trillion chance of a
   * collision. (http://preshing.com/20110504/hash-collision-probabilities/)
   *
   * @see EpoxyModel#hashString64Bit(CharSequence)
   */
  public EpoxyModel<T> id(CharSequence key) {
    id(hashString64Bit(key));
    return this;
  }

  /**
   * Set an id that is namespaced with a string. This is useful when you need to show models of
   * multiple types, side by side and don't want to risk id collisions.
   * <p>
   * Since this uses a hashcode method to convert the String to a long there is a very small chance
   * that you may have a collision with another id. Assuming an even spread of hashcodes, and
   * several hundred models in the adapter, there would be roughly 1 in 100 trillion chance of a
   * collision. (http://preshing.com/20110504/hash-collision-probabilities/)
   *
   * @see EpoxyModel#hashString64Bit(CharSequence)
   * @see EpoxyModel#hashLong64Bit(long)
   */
  public EpoxyModel<T> id(CharSequence key, long id) {
    long result = hashString64Bit(key);
    result = 31 * result + hashLong64Bit(id);
    id(result);
    return this;
  }

  /**
   * Hash a long into 64 bits instead of the normal 32. This uses a xor shift implementation to
   * attempt psuedo randomness so object ids have an even spread for less chance of collisions.
   * <p>
   * From http://stackoverflow.com/a/11554034
   * <p>
   * http://www.javamex.com/tutorials/random_numbers/xorshift.shtml
   */
  private static long hashLong64Bit(long value) {
    value ^= (value << 21);
    value ^= (value >>> 35);
    value ^= (value << 4);
    return value;
  }

  /**
   * Hash a string into 64 bits instead of the normal 32. This allows us to better use strings as a
   * model id with less chance of collisions. This uses the FNV-1a algorithm for a good mix of speed
   * and distribution.
   * <p>
   * Performance comparisons found at http://stackoverflow.com/a/1660613
   * <p>
   * Hash implementation from http://www.isthe.com/chongo/tech/comp/fnv/index.html#FNV-1a
   */
  private static long hashString64Bit(CharSequence str) {
    long result = 0xcbf29ce484222325L;
    final int len = str.length();
    for (int i = 0; i < len; i++) {
      result ^= str.charAt(i);
      result *= 0x100000001b3L;
    }
    return result;
  }

  @LayoutRes
  protected abstract int getDefaultLayout();

  public EpoxyModel<T> layout(@LayoutRes int layoutRes) {
    validateMutability();
    layout = layoutRes;
    return this;
  }

  @LayoutRes
  public final int getLayout() {
    if (layout == 0) {
      return getDefaultLayout();
    }

    return layout;
  }

  /**
   * Sets fields of the model to default ones.
   */
  public EpoxyModel<T> reset() {
    validateMutability();

    layout = 0;
    shown = true;

    return this;
  }

  /**
   * Add this model to the given controller. Can only be called from inside {@link
   * EpoxyController#buildModels()}.
   */
  public void addTo(EpoxyController controller) {
    controller.addInternal(this);
  }

  /**
   * Add this model to the given controller if the condition is true. Can only be called from inside
   * {@link EpoxyController#buildModels()}.
   */
  public void addIf(boolean condition, EpoxyController controller) {
    if (condition) {
      addTo(controller);
    }
  }

  /**
   * Add this model to the given controller if the {@link AddPredicate} return true. Can only be
   * called from inside {@link EpoxyController#buildModels()}.
   */
  public void addIf(AddPredicate predicate, EpoxyController controller) {
    if (predicate.addIf()) {
      addTo(controller);
    }
  }

  /**
   * @see #addIf(AddPredicate, EpoxyController)
   */
  public interface AddPredicate {
    boolean addIf();
  }

  /**
   * This is used internally by generated models to turn on validation checking when
   * "validateEpoxyModelUsage" is enabled and the model is used with an {@link EpoxyController}.
   */
  protected final void addWithDebugValidation(EpoxyController controller) {
    if (controller == null) {
      throw new IllegalArgumentException("Controller cannot be null");
    }

    if (attachedController != null) {
      throw new IllegalEpoxyUsage(
          "This model was already added to the controller at position "
              + controller.getIndexOfModelInBuildingList(this));
    }

    attachedController = controller;
    // We save the current hashCode so we can compare it to the hashCode at later points in time
    // in order to validate that it doesn't change and enforce mutability.
    hashCodeWhenAdded = hashCode();

    // The one time it is valid to change the model is during an interceptor callback. To support
    // that we need to update the hashCode after interceptors have been run.
    controller.addAfterInterceptorCallback(new AfterInterceptorCallback() {
      @Override
      public void afterInterceptorsRun() {
        hashCodeWhenAdded = EpoxyModel.this.hashCode();
      }
    });
  }

  /**
   * This is used internally by generated models to do validation checking when
   * "validateEpoxyModelUsage" is enabled and the model is used with an {@link EpoxyController}.
   * This method validates that it is ok to change this model. It is only valid if the model hasn't
   * yet been added, or the change is being done from an {@link EpoxyController.Interceptor}
   * callback.
   */
  protected final void validateMutability() {
    if (attachedController != null && !attachedController.isRunningInterceptors()) {
      throw new ImmutableModelException(this,
          getPosition(attachedController, this));
    }
  }

  private static int getPosition(EpoxyController controller, EpoxyModel<?> model) {
    if (controller.isBuildingModels()) {
      return controller.getIndexOfModelInBuildingList(model);
    }

    return controller.getAdapter().getModelPosition(model);
  }

  /**
   * This is used internally by generated models to do validation checking when
   * "validateEpoxyModelUsage" is enabled and the model is used with a {@link EpoxyController}. This
   * method validates that the model's hashCode hasn't been changed since it was added to the
   * controller. This is similar to {@link #validateMutability()}, but that method is only used for
   * specific model changes such as calling a setter. By checking the hashCode, this method allows
   * us to catch more subtle changes, such as through setting a field directly or through changing
   * an object that is set on the model.
   */
  protected final void validateStateHasNotChangedSinceAdded(String descriptionOfChange,
      int modelPosition) {
    if (attachedController != null && hashCodeWhenAdded != hashCode()) {
      throw new ImmutableModelException(this, descriptionOfChange, modelPosition);
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof EpoxyModel)) {
      return false;
    }

    EpoxyModel<?> that = (EpoxyModel<?>) o;

    if (id != that.id) {
      return false;
    }
    if (getViewType() != that.getViewType()) {
      return false;
    }
    return shown == that.shown;
  }

  @Override
  public int hashCode() {
    int result = (int) (id ^ (id >>> 32));
    result = 31 * result + getViewType();
    result = 31 * result + (shown ? 1 : 0);
    return result;
  }

  /**
   * Subclasses can override this if they want their view to take up more than one span in a grid
   * layout.
   *
   * @param totalSpanCount The number of spans in the grid
   * @param position       The position of the model
   * @param itemCount      The total number of items in the adapter
   */
  public int getSpanSize(int totalSpanCount, int position, int itemCount) {
    return 1;
  }

  public EpoxyModel<T> show() {
    return show(true);
  }

  public EpoxyModel<T> show(boolean show) {
    validateMutability();
    shown = show;
    return this;
  }

  public EpoxyModel<T> hide() {
    return show(false);
  }

  /**
   * Whether the model's view should be shown on screen. If false it won't be inflated and drawn,
   * and will be like it was never added to the recycler view.
   */
  public boolean isShown() {
    return shown;
  }

  /**
   * Whether the adapter should save the state of the view bound to this model.
   */
  public boolean shouldSaveViewState() {
    return false;
  }

  /**
   * Called if the RecyclerView failed to recycle this model's view. You can take this opportunity
   * to clear the animation(s) that affect the View's transient state and return <code>true</code>
   * so that the View can be recycled. Keep in mind that the View in question is already removed
   * from the RecyclerView.
   *
   * @return True if the View should be recycled, false otherwise
   * @see EpoxyAdapter#onFailedToRecycleView(android.support.v7.widget.RecyclerView.ViewHolder)
   */
  public boolean onFailedToRecycleView(T view) {
    return false;
  }

  /**
   * Called when this model's view is attached to the window.
   *
   * @see EpoxyAdapter#onViewAttachedToWindow(android.support.v7.widget.RecyclerView.ViewHolder)
   */
  public void onViewAttachedToWindow(T view) {

  }

  /**
   * Called when this model's view is detached from the the window.
   *
   * @see EpoxyAdapter#onViewDetachedFromWindow(android.support.v7.widget.RecyclerView.ViewHolder)
   */
  public void onViewDetachedFromWindow(T view) {

  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + "{"
        + "id=" + id
        + ", viewType=" + getViewType()
        + ", shown=" + shown
        + ", addedToAdapter=" + addedToAdapter
        + '}';
  }
}
