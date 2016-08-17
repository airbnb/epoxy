/*
 * Copyright (C) 2016 Airbnb, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.airbnb.epoxy;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Helper to track changes in the models list.
 */
class DiffHelper {

  private ArrayList<ModelState> oldStateList = new ArrayList<>();
  // Using a HashMap instead of a LongSparseArray to have faster look up times at the expense of memory
  private Map<Long, ModelState> oldStateMap = new HashMap<>();
  private ArrayList<ModelState> currentStateList = new ArrayList<>();
  private Map<Long, ModelState> currentStateMap = new HashMap<>();
  private final EpoxyAdapter adapter;

  DiffHelper(EpoxyAdapter adapter) {
    this.adapter = adapter;
    adapter.registerAdapterDataObserver(observer);
  }

  private final RecyclerView.AdapterDataObserver observer = new RecyclerView.AdapterDataObserver() {
    @Override
    public void onChanged() {
      buildCurrentState();
    }

    @Override
    public void onItemRangeChanged(int positionStart, int itemCount) {
      for (int i = positionStart; i < positionStart + itemCount; i++) {
        currentStateList.get(i).hashCode = adapter.models.get(i).hashCode();
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

      List<ModelState> modelsToRemove = currentStateList.subList(positionStart, positionStart + itemCount);
      for (ModelState model : modelsToRemove) {
        currentStateMap.remove(model.id);
        ModelState.release(model);
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
        throw new IllegalArgumentException("Moving more than 1 item at a time is not " +
            "supported. Number of items moved: " + itemCount);
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
   * Set the current list of models. The diff callbacks will be notified of the changes between the current list and the last list that was set.
   */
  public void notifyModelChanges() {
    // We use a list of the models as well as a map by their id, so we can easily find them by both position and id
    swapCurrentStateToOld();
    buildCurrentState();
    List<UpdateOp> diff = buildDiff();

    // Send out the proper notify calls for the diff. We remove our observer first so that we don't react to our own notify calls
    adapter.unregisterAdapterDataObserver(observer);
    notifyChanges(diff);
    adapter.registerAdapterDataObserver(observer);

    UpdateOp.release(diff);
  }

  private void notifyChanges(List<UpdateOp> diff) {
    for (UpdateOp op : diff) {
      switch (op.type) {
        case UpdateOp.Add:
          adapter.notifyItemRangeInserted(op.positionStart, op.itemCount);
          break;
        case UpdateOp.Move:
          adapter.notifyItemMoved(op.positionStart, op.itemCount);
          break;
        case UpdateOp.Remove:
          adapter.notifyItemRangeRemoved(op.positionStart, op.itemCount);
          break;
        case UpdateOp.Update:
          adapter.notifyItemRangeChanged(op.positionStart, op.itemCount);
          break;
        default:
          throw new IllegalArgumentException("Unknown type: " + op.type);
      }
    }
  }

  private void buildCurrentState() {
    int size = adapter.models.size();

    ModelState.release(currentStateList);
    currentStateList.clear();
    currentStateList.ensureCapacity(size);
    currentStateMap.clear();

    for (int i = 0; i < size; i++) {
      currentStateList.add(createStateForPosition(i));
    }
  }

  private ModelState createStateForPosition(int position) {
    ModelState state = ModelState.build(adapter.models.get(position), position);

    ModelState previousValue = currentStateMap.put(state.id, state);
    if (previousValue != null) {
      throw new IllegalStateException("Duplicate ID for model: " + state + " Original: " + previousValue);
    }

    return state;
  }

  private void swapCurrentStateToOld() {
    // Swap the two lists so that we have a copy of the current state to calculate the next diff
    ArrayList<ModelState> tempList = oldStateList;
    oldStateList = currentStateList;
    currentStateList = tempList;

    Map<Long, ModelState> tempMap = oldStateMap;
    oldStateMap = currentStateMap;
    currentStateMap = tempMap;
  }

  /**
   * Create a list of operations that define the difference between {@link #oldStateList} and {@link #currentStateList}.
   */
  private List<UpdateOp> buildDiff() {
    List<UpdateOp> result = new ArrayList<>();

    // The general approach is to first search for removals, then additions, and lastly changes.
    // Focusing on one type of operation at a time makes it easy to coalesce batch changes.
    // When we identify an operation and add it to the result list we update the postions of items in the oldStateList to reflect
    // the change, this way subsequent operations will use the correct, updated positions.
    collectRemovals(result);
    collectInsertions(result);
    collectChanges(result);
    collectMoves(result);

    return result;
  }

  /**
   * Find all removal operations and add them to the result list. The general strategy here is to walk through the {@link #oldStateList} and check for
   * items that don't exist in the new list. Walking through it in order makes it easy to batch adjacent removals.
   */
  private void collectRemovals(List<UpdateOp> result) {
    int removalCount = 0;
    UpdateOp lastRemoval = null;
    for (ModelState state : oldStateList) {
      // Update the position of the item to take into account previous removals,
      // so that future operations will reference the correct position
      state.position -= removalCount;

      // This is our first time going through the list, so we look up the item with the matching id in the new
      // list and hold a reference to it so that we can access it quickly in the future
      state.pair = currentStateMap.get(state.id);
      if (state.pair != null) {
        state.pair.pair = state;
        continue;
      }

      int indexToRemove = state.position;
      if (lastRemoval != null && lastRemoval.positionStart == indexToRemove) {
        lastRemoval.itemCount++;
      } else {
        if (lastRemoval != null) {
          result.add(lastRemoval);
        }
        lastRemoval = UpdateOp.instance(UpdateOp.Remove, indexToRemove);
      }
      removalCount++;
    }

    if (lastRemoval != null) {
      result.add(lastRemoval);
    }
  }

  /**
   * Find all insertion operations and add them to the result list. The general strategy here is to walk through the {@link #currentStateList} and
   * check for items that don't exist in the old list. Walking through it in order makes it easy to batch adjacent insertions.
   */
  private void collectInsertions(List<UpdateOp> result) {
    UpdateOp lastInsertion = null;
    int insertionCount = 0;
    Iterator<ModelState> oldItemIterator = oldStateList.iterator();

    for (ModelState itemToInsert : currentStateList) {
      if (itemToInsert.pair != null) {
        // Update the position of the next item in the old list to take any insertions into account
        ModelState nextOldItem = getNextItemWithPair(oldItemIterator);
        if (nextOldItem != null) {
          nextOldItem.position += insertionCount;
        }
        continue;
      }

      // If the item to insert is adjacent to our last insertion operation we can batch them
      if (lastInsertion != null && (lastInsertion.positionStart + lastInsertion.itemCount) == itemToInsert.position) {
        lastInsertion.itemCount++;
      } else {
        if (lastInsertion != null) {
          result.add(lastInsertion);
        }
        lastInsertion = UpdateOp.instance(UpdateOp.Add, itemToInsert.position);
      }

      insertionCount++;
    }

    if (lastInsertion != null) {
      result.add(lastInsertion);
    }
  }

  /**
   * Check if any items have had their values changed, batching if possible.
   */
  private void collectChanges(List<UpdateOp> result) {
    UpdateOp lastUpdateOp = null;
    for (ModelState newItem : currentStateList) {
      if (newItem.pair == null) {
        continue;
      }

      if (newItem.pair.hashCode != newItem.hashCode) {
        if (lastUpdateOp != null && (lastUpdateOp.positionStart + lastUpdateOp.itemCount) == newItem.position) {
          lastUpdateOp.itemCount++;
        } else {
          if (lastUpdateOp != null) {
            result.add(lastUpdateOp);
          }
          lastUpdateOp = UpdateOp.instance(UpdateOp.Update, newItem.position);
        }
      }
    }

    if (lastUpdateOp != null) {
      result.add(lastUpdateOp);
    }
  }

  /**
   * Check which items have had a position changed. Recyclerview does not support batching these.
   */
  private void collectMoves(List<UpdateOp> result) {
    // This walks through both the new and old list simultaneous and checks for position changes.
    Iterator<ModelState> oldItemIterator = oldStateList.iterator();
    ModelState nextOldItem = null;

    // We have to be careful to update all item positions in the list when we do a MOVE. This adds some complexity.
    // To do this we keep track of all moves and apply them to an item when we need the up to date position
    List<UpdateOp> moveOps = new ArrayList<>();

    for (ModelState newItem : currentStateList) {
      if (newItem.pair == null) {
        continue;
      }

      // We could iterate through only the new list and move each item that is out of place, however in cases such as moving the first item
      // to the end that strategy would do many moves to move all items up one, instead of doing one move to move the first item to the end.
      // To avoid this we compare the old item to the new item at each index and move the one that is farthest from its correct position.
      // We only move on from a new item once its pair is placed in the correct spot. Since we move from start to end, all new items we've
      // already iterated through are guaranteed to have their pair be already in the righ spot, which won't be affected by future MOVEs.
      if (nextOldItem == null) {
        nextOldItem = getNextItemWithPair(oldItemIterator);

        // We've already iterated through all old items and moved each item once. However, subsequent moves may have shifted an item out of
        // its correct space once it was already moved. We finish iterating through all the new items to ensure everything is still correct
        if (nextOldItem == null) {
          nextOldItem = newItem.pair;
        }
      }

      while (nextOldItem != null) {
        // Make sure the positions are updated to the latest move operations before we calculate the next move
        updateItemPosition(newItem.pair, moveOps);
        updateItemPosition(nextOldItem, moveOps);

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
          UpdateOp moveOp = UpdateOp.instance(UpdateOp.Move, nextOldItem.position, nextOldItem.pair.position);
          result.add(moveOp);
          moveOps.add(moveOp);

          nextOldItem.position = nextOldItem.pair.position;
          nextOldItem.lastMoveOp = moveOps.size();

          nextOldItem = getNextItemWithPair(oldItemIterator);
        } else {
          UpdateOp moveOp = UpdateOp.instance(UpdateOp.Move, newItem.pair.position, newItem.position);
          result.add(moveOp);
          moveOps.add(moveOp);

          newItem.pair.position = newItem.position;
          newItem.pair.lastMoveOp = moveOps.size();
          break;
        }
      }
    }
  }

  /**
   * Apply the movement operations to the given item to update its position. Only applies the operations that have not been applied yet, and stores
   * how many operations have been applied so we know which ones to apply next time.
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