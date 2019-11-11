
package com.airbnb.epoxy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Helper to track changes in the models list.
 */
class DiffHelper {
  private ArrayList<ModelState> oldStateList = new ArrayList<>();
  // Using a HashMap instead of a LongSparseArray to
  // have faster look up times at the expense of memory
  private Map<Long, ModelState> oldStateMap = new HashMap<>();
  private ArrayList<ModelState> currentStateList = new ArrayList<>();
  private Map<Long, ModelState> currentStateMap = new HashMap<>();
  private final BaseEpoxyAdapter adapter;
  private final boolean immutableModels;


  DiffHelper(BaseEpoxyAdapter adapter, boolean immutableModels) {
    this.adapter = adapter;
    this.immutableModels = immutableModels;
    adapter.registerAdapterDataObserver(observer);
  }

  private final RecyclerView.AdapterDataObserver observer = new RecyclerView.AdapterDataObserver() {
    @Override
    public void onChanged() {
      throw new UnsupportedOperationException(
          "Diffing is enabled. You should use notifyModelsChanged instead of notifyDataSetChanged");
    }

    @Override
    public void onItemRangeChanged(int positionStart, int itemCount) {
      for (int i = positionStart; i < positionStart + itemCount; i++) {
        currentStateList.get(i).hashCode = adapter.getCurrentModels().get(i).hashCode();
      }
    }

    @Override
    public void onItemRangeInserted(int positionStart, int itemCount) {
      if (itemCount == 0) {
        // no-op
        return;
      }

      if (itemCount == 1 || positionStart == currentStateList.size()) {
        for (int i = positionStart; i < positionStart + itemCount; i++) {
          currentStateList.add(i, createStateForPosition(i));
        }
      } else {
        // Add in a batch since multiple insertions to the middle of the list are slow
        List<ModelState> newModels = new ArrayList<>(itemCount);
        for (int i = positionStart; i < positionStart + itemCount; i++) {
          newModels.add(createStateForPosition(i));
        }

        currentStateList.addAll(positionStart, newModels);
      }

      // Update positions of affected items
      int size = currentStateList.size();
      for (int i = positionStart + itemCount; i < size; i++) {
        currentStateList.get(i).position += itemCount;
      }
    }

    @Override
    public void onItemRangeRemoved(int positionStart, int itemCount) {
      if (itemCount == 0) {
        // no-op
        return;
      }

      List<ModelState> modelsToRemove =
          currentStateList.subList(positionStart, positionStart + itemCount);
      for (ModelState model : modelsToRemove) {
        currentStateMap.remove(model.id);
      }
      modelsToRemove.clear();

      // Update positions of affected items
      int size = currentStateList.size();
      for (int i = positionStart; i < size; i++) {
        currentStateList.get(i).position -= itemCount;
      }
    }

    @Override
    public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
      if (fromPosition == toPosition) {
        // no-op
        return;
      }

      if (itemCount != 1) {
        throw new IllegalArgumentException("Moving more than 1 item at a time is not "
            + "supported. Number of items moved: " + itemCount);
      }

      ModelState model = currentStateList.remove(fromPosition);
      model.position = toPosition;
      currentStateList.add(toPosition, model);

      if (fromPosition < toPosition) {
        // shift the affected items left
        for (int i = fromPosition; i < toPosition; i++) {
          currentStateList.get(i).position--;
        }
      } else {
        // shift the affected items right
        for (int i = toPosition + 1; i <= fromPosition; i++) {
          currentStateList.get(i).position++;
        }
      }
    }
  };

  /**
   * Set the current list of models. The diff callbacks will be notified of the changes between the
   * current list and the last list that was set.
   */
  void notifyModelChanges() {
    UpdateOpHelper updateOpHelper = new UpdateOpHelper();

    buildDiff(updateOpHelper);

    // Send out the proper notify calls for the diff. We remove our
    // observer first so that we don't react to our own notify calls
    adapter.unregisterAdapterDataObserver(observer);
    notifyChanges(updateOpHelper);
    adapter.registerAdapterDataObserver(observer);
  }

  private void notifyChanges(UpdateOpHelper opHelper) {
    for (UpdateOp op : opHelper.opList) {
      switch (op.type) {
        case UpdateOp.ADD:
          adapter.notifyItemRangeInserted(op.positionStart, op.itemCount);
          break;
        case UpdateOp.MOVE:
          adapter.notifyItemMoved(op.positionStart, op.itemCount);
          break;
        case UpdateOp.REMOVE:
          adapter.notifyItemRangeRemoved(op.positionStart, op.itemCount);
          break;
        case UpdateOp.UPDATE:
          if (immutableModels && op.payloads != null) {
            adapter.notifyItemRangeChanged(op.positionStart, op.itemCount,
                new DiffPayload(op.payloads));
          } else {
            adapter.notifyItemRangeChanged(op.positionStart, op.itemCount);
          }
          break;
        default:
          throw new IllegalArgumentException("Unknown type: " + op.type);
      }
    }
  }

  /**
   * Create a list of operations that define the difference between {@link #oldStateList} and {@link
   * #currentStateList}.
   */
  private UpdateOpHelper buildDiff(UpdateOpHelper updateOpHelper) {
    prepareStateForDiff();

    // The general approach is to first search for removals, then additions, and lastly changes.
    // Focusing on one type of operation at a time makes it easy to coalesce batch changes.
    // When we identify an operation and add it to the
    // result list we update the positions of items in the oldStateList to reflect
    // the change, this way subsequent operations will use the correct, updated positions.
    collectRemovals(updateOpHelper);

    // Only need to check for insertions if new list is bigger
    boolean hasInsertions =
        oldStateList.size() - updateOpHelper.getNumRemovals() != currentStateList.size();
    if (hasInsertions) {
      collectInsertions(updateOpHelper);
    }

    collectMoves(updateOpHelper);
    collectChanges(updateOpHelper);

    resetOldState();

    return updateOpHelper;
  }

  private void resetOldState() {
    oldStateList.clear();
    oldStateMap.clear();
  }

  private void prepareStateForDiff() {
    // We use a list of the models as well as a map by their id,
    // so we can easily find them by both position and id

    oldStateList.clear();
    oldStateMap.clear();

    // Swap the two lists so that we have a copy of the current state to calculate the next diff
    ArrayList<ModelState> tempList = oldStateList;
    oldStateList = currentStateList;
    currentStateList = tempList;

    Map<Long, ModelState> tempMap = oldStateMap;
    oldStateMap = currentStateMap;
    currentStateMap = tempMap;

    // Remove all pairings in the old states so we can tell which of them were removed. The items
    // that still exist in the new list will be paired when we build the current list state below
    for (ModelState modelState : oldStateList) {
      modelState.pair = null;
    }

    int modelCount = adapter.getCurrentModels().size();
    currentStateList.ensureCapacity(modelCount);

    for (int i = 0; i < modelCount; i++) {
      currentStateList.add(createStateForPosition(i));
    }
  }

  private ModelState createStateForPosition(int position) {
    EpoxyModel<?> model = adapter.getCurrentModels().get(position);
    model.addedToAdapter = true;
    ModelState state = ModelState.build(model, position, immutableModels);

    ModelState previousValue = currentStateMap.put(state.id, state);
    if (previousValue != null) {
      int previousPosition = previousValue.position;
      EpoxyModel<?> previousModel = adapter.getCurrentModels().get(previousPosition);
      throw new IllegalStateException("Two models have the same ID. ID's must be unique!"
          + " Model at position " + position + ": " + model
          + " Model at position " + previousPosition + ": " + previousModel);
    }

    return state;
  }

  /**
   * Find all removal operations and add them to the result list. The general strategy here is to
   * walk through the {@link #oldStateList} and check for items that don't exist in the new list.
   * Walking through it in order makes it easy to batch adjacent removals.
   */
  private void collectRemovals(UpdateOpHelper helper) {
    for (ModelState state : oldStateList) {
      // Update the position of the item to take into account previous removals,
      // so that future operations will reference the correct position
      state.position -= helper.getNumRemovals();

      // This is our first time going through the list, so we
      // look up the item with the matching id in the new
      // list and hold a reference to it so that we can access it quickly in the future
      state.pair = currentStateMap.get(state.id);
      if (state.pair != null) {
        state.pair.pair = state;
        continue;
      }

      helper.remove(state.position);
    }
  }

  /**
   * Find all insertion operations and add them to the result list. The general strategy here is to
   * walk through the {@link #currentStateList} and check for items that don't exist in the old
   * list. Walking through it in order makes it easy to batch adjacent insertions.
   */
  private void collectInsertions(UpdateOpHelper helper) {
    Iterator<ModelState> oldItemIterator = oldStateList.iterator();

    for (ModelState itemToInsert : currentStateList) {
      if (itemToInsert.pair != null) {
        // Update the position of the next item in the old list to take any insertions into account
        ModelState nextOldItem = getNextItemWithPair(oldItemIterator);
        if (nextOldItem != null) {
          nextOldItem.position += helper.getNumInsertions();
        }
        continue;
      }

      helper.add(itemToInsert.position);
    }
  }

  /**
   * Check if any items have had their values changed, batching if possible.
   */
  private void collectChanges(UpdateOpHelper helper) {
    for (ModelState newItem : currentStateList) {
      ModelState previousItem = newItem.pair;
      if (previousItem == null) {
        continue;
      }

      // We use equals when we know the models are immutable and available, otherwise we have to
      // rely on the stored hashCode
      boolean modelChanged;
      if (immutableModels) {
        // Make sure that the old model hasn't changed, otherwise comparing it with the new one
        // won't be accurate.
        if (previousItem.model.isDebugValidationEnabled()) {
          previousItem.model
              .validateStateHasNotChangedSinceAdded("Model was changed before it could be diffed.",
                  previousItem.position);
        }

        modelChanged = !previousItem.model.equals(newItem.model);
      } else {
        modelChanged = previousItem.hashCode != newItem.hashCode;
      }

      if (modelChanged) {
        helper.update(newItem.position, previousItem.model);
      }
    }
  }

  /**
   * Check which items have had a position changed. Recyclerview does not support batching these.
   */
  private void collectMoves(UpdateOpHelper helper) {
    // This walks through both the new and old list simultaneous and checks for position changes.
    Iterator<ModelState> oldItemIterator = oldStateList.iterator();
    ModelState nextOldItem = null;

    for (ModelState newItem : currentStateList) {
      if (newItem.pair == null) {
        // This item was inserted. However, insertions are done at the item's final position, and
        // aren't smart about inserting at a different position to take future moves into account.
        // As the old state list is updated to reflect moves, it needs to also consider insertions
        // affected by those moves in order for the final change set to be correct
        if (helper.moves.isEmpty()) {
          // There have been no moves, so the item is still at it's correct position
          continue;
        } else {
          // There have been moves, so the old list needs to take this inserted item
          // into account. The old list doesn't have this item inserted into it
          // (for optimization purposes), but we can create a pair for this item to
          // track its position in the old list and move it back to its final position if necessary
          newItem.pairWithSelf();
        }
      }

      // We could iterate through only the new list and move each
      // item that is out of place, however in cases such as moving the first item
      // to the end, that strategy would do many moves to move all
      // items up one instead of doing one move to move the first item to the end.
      // To avoid this we compare the old item to the new item at
      // each index and move the one that is farthest from its correct position.
      // We only move on from a new item once its pair is placed in
      // the correct spot. Since we move from start to end, all new items we've
      // already iterated through are guaranteed to have their pair
      // be already in the right spot, which won't be affected by future MOVEs.
      if (nextOldItem == null) {
        nextOldItem = getNextItemWithPair(oldItemIterator);

        // We've already iterated through all old items and moved each
        // item once. However, subsequent moves may have shifted an item out of
        // its correct space once it was already moved. We finish
        // iterating through all the new items to ensure everything is still correct
        if (nextOldItem == null) {
          nextOldItem = newItem.pair;
        }
      }

      while (nextOldItem != null) {
        // Make sure the positions are updated to the latest
        // move operations before we calculate the next move
        updateItemPosition(newItem.pair, helper.moves);
        updateItemPosition(nextOldItem, helper.moves);

        // The item is the same and its already in the correct place
        if (newItem.id == nextOldItem.id && newItem.position == nextOldItem.position) {
          nextOldItem = null;
          break;
        }

        int newItemDistance = newItem.pair.position - newItem.position;
        int oldItemDistance = nextOldItem.pair.position - nextOldItem.position;

        // Both items are already in the correct position
        if (newItemDistance == 0 && oldItemDistance == 0) {
          nextOldItem = null;
          break;
        }

        if (oldItemDistance > newItemDistance) {
          helper.move(nextOldItem.position, nextOldItem.pair.position);

          nextOldItem.position = nextOldItem.pair.position;
          nextOldItem.lastMoveOp = helper.getNumMoves();

          nextOldItem = getNextItemWithPair(oldItemIterator);
        } else {
          helper.move(newItem.pair.position, newItem.position);

          newItem.pair.position = newItem.position;
          newItem.pair.lastMoveOp = helper.getNumMoves();
          break;
        }
      }
    }
  }

  /**
   * Apply the movement operations to the given item to update its position. Only applies the
   * operations that have not been applied yet, and stores how many operations have been applied so
   * we know which ones to apply next time.
   */
  private void updateItemPosition(ModelState item, List<UpdateOp> moveOps) {
    int size = moveOps.size();

    for (int i = item.lastMoveOp; i < size; i++) {
      UpdateOp moveOp = moveOps.get(i);
      int fromPosition = moveOp.positionStart;
      int toPosition = moveOp.itemCount;

      if (item.position > fromPosition && item.position <= toPosition) {
        item.position--;
      } else if (item.position < fromPosition && item.position >= toPosition) {
        item.position++;
      }
    }

    item.lastMoveOp = size;
  }

  /**
   * Gets the next item in the list that has a pair, meaning it wasn't inserted or removed.
   */
  @Nullable
  private ModelState getNextItemWithPair(Iterator<ModelState> iterator) {
    ModelState nextItem = null;
    while (nextItem == null && iterator.hasNext()) {
      nextItem = iterator.next();

      if (nextItem.pair == null) {
        // Skip this one and go on to the next
        nextItem = null;
      }
    }

    return nextItem;
  }
}
