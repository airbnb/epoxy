package com.airbnb.epoxy;

import android.support.v4.util.Pools;

import java.util.Collection;

/** Helper to store relevant information about a model that we need to determine if it changed. */
class ModelState {
  /**
   * To have enough items in the pool it should be twice as big as the list size, since we track old
   * and new list state. It's hard to predict list size and some users may have long lists that this
   * won't be well suited to. It may be worth looking into dynamically resizing this based on list
   * size.
   */
  private static final Pools.Pool<ModelState> POOL = new Pools.SimplePool<>(200);

  long id;
  int hashCode;
  int position;

  /**
   * A link to the item with the same id in the other list when diffing two lists. This will be null
   * if the item doesn't exist, in the case of insertions or removals. This is an optimization to
   * prevent having to look up the matching pair in a hash map every time.
   */
  ModelState pair;

  /**
   * How many movement operations have been applied to this item in order to update its position. As
   * we find more item movements we need to update the position of affected items in the list in
   * order to correctly calculate the next movement. Instead of iterating through all items in the
   * list every time a movement operation happens we keep track of how many of these operations have
   * been applied to an item, and apply all new operations in order when we need to get this item's
   * up to date position.
   */
  int lastMoveOp;

  static ModelState build(EpoxyModel<?> model, int position) {
    ModelState state = get();

    state.lastMoveOp = 0;
    state.pair = null;
    state.id = model.id();
    state.hashCode = model.hashCode();
    state.position = position;

    return state;
  }

  private static ModelState get() {
    ModelState state = POOL.acquire();
    if (state == null) {
      state = new ModelState();
    }
    return state;
  }

  /**
   * Used for an item inserted into the new list when we need to track moves that effect the
   * inserted item in the old list.
   */
  public void pairWithSelf() {
    if (pair != null) {
      throw new IllegalStateException("Already paired.");
    }

    pair = get();
    pair.lastMoveOp = 0;
    pair.id = id;
    pair.position = position;
    pair.hashCode = hashCode;
    pair.pair = this;
  }

  static void release(Collection<ModelState> states) {
    for (ModelState state : states) {
      release(state);
    }
  }

  public static void release(ModelState state) {
    POOL.release(state);
  }

  @Override
  public String toString() {
    return "ModelState{"
        + "id=" + id
        + ", hashCode=" + hashCode
        + ", position=" + position
        + ", pair=" + pair
        + ", lastMoveOp=" + lastMoveOp
        + '}';
  }
}
