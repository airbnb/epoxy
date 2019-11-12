package com.airbnb.epoxy;

/** Helper to store relevant information about a model that we need to determine if it changed. */
class ModelState {
  long id;
  int hashCode;
  int position;
  EpoxyModel<?> model;

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

  static ModelState build(EpoxyModel<?> model, int position, boolean immutableModel) {
    ModelState state = new ModelState();

    state.lastMoveOp = 0;
    state.pair = null;
    state.id = model.id();
    state.position = position;

    if (immutableModel) {
      state.model = model;
    } else {
      state.hashCode = model.hashCode();
    }

    return state;
  }

  /**
   * Used for an item inserted into the new list when we need to track moves that effect the
   * inserted item in the old list.
   */
  void pairWithSelf() {
    if (pair != null) {
      throw new IllegalStateException("Already paired.");
    }

    pair = new ModelState();
    pair.lastMoveOp = 0;
    pair.id = id;
    pair.position = position;
    pair.hashCode = hashCode;
    pair.pair = this;
    pair.model = model;
  }

  @Override
  public String toString() {
    return "ModelState{"
        + "id=" + id
        + ", model=" + model
        + ", hashCode=" + hashCode
        + ", position=" + position
        + ", pair=" + pair
        + ", lastMoveOp=" + lastMoveOp
        + '}';
  }
}
