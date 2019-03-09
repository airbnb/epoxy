package com.airbnb.epoxy;

import com.airbnb.epoxy.UpdateOp.Type;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;

import static com.airbnb.epoxy.UpdateOp.ADD;
import static com.airbnb.epoxy.UpdateOp.MOVE;
import static com.airbnb.epoxy.UpdateOp.REMOVE;
import static com.airbnb.epoxy.UpdateOp.UPDATE;

/** Helper class to collect changes in a diff, batching when possible. */
class UpdateOpHelper {
  final List<UpdateOp> opList = new ArrayList<>();
  // We have to be careful to update all item positions in the list when we
  // do a MOVE. This adds some complexity.
  // To do this we keep track of all moves and apply them to an item when we
  // need the up to date position
  final List<UpdateOp> moves = new ArrayList<>();
  private UpdateOp lastOp;
  private int numInsertions;
  private int numInsertionBatches;
  private int numRemovals;
  private int numRemovalBatches;

  void reset() {
    opList.clear();
    moves.clear();
    lastOp = null;
    numInsertions = 0;
    numInsertionBatches = 0;
    numRemovals = 0;
    numRemovalBatches = 0;
  }

  void add(int indexToInsert) {
    add(indexToInsert, 1);
  }

  void add(int startPosition, int itemCount) {
    numInsertions += itemCount;

    // We can append to a previously ADD batch if the new items are added anywhere in the
    // range of the previous batch batch
    boolean batchWithLast = isLastOp(ADD)
        && (lastOp.contains(startPosition) || lastOp.positionEnd() == startPosition);

    if (batchWithLast) {
      addItemsToLastOperation(itemCount, null);
    } else {
      numInsertionBatches++;
      addNewOperation(ADD, startPosition, itemCount);
    }
  }

  void update(int indexToChange) {
    update(indexToChange, null);
  }

  void update(final int indexToChange, EpoxyModel<?> payload) {
    if (isLastOp(UPDATE)) {
      if (lastOp.positionStart == indexToChange + 1) {
        // Change another item at the start of the batch range
        addItemsToLastOperation(1, payload);
        lastOp.positionStart = indexToChange;
      } else if (lastOp.positionEnd() == indexToChange) {
        // Add another item at the end of the batch range
        addItemsToLastOperation(1, payload);
      } else if (lastOp.contains(indexToChange)) {
        // This item is already included in the existing batch range, so we don't add any items
        // to the batch count, but we still need to add the new payload
        addItemsToLastOperation(0, payload);
      } else {
        // The item can't be batched with the previous update operation
        addNewOperation(UPDATE, indexToChange, 1, payload);
      }
    } else {
      addNewOperation(UPDATE, indexToChange, 1, payload);
    }
  }

  void remove(int indexToRemove) {
    remove(indexToRemove, 1);
  }

  void remove(int startPosition, int itemCount) {
    numRemovals += itemCount;

    boolean batchWithLast = false;
    if (isLastOp(REMOVE)) {
      if (lastOp.positionStart == startPosition) {
        // Remove additional items at the end of the batch range
        batchWithLast = true;
      } else if (lastOp.isAfter(startPosition)
          && startPosition + itemCount >= lastOp.positionStart) {
        // Removes additional items at the start and (possibly) end of the batch
        lastOp.positionStart = startPosition;
        batchWithLast = true;
      }
    }

    if (batchWithLast) {
      addItemsToLastOperation(itemCount, null);
    } else {
      numRemovalBatches++;
      addNewOperation(REMOVE, startPosition, itemCount);
    }
  }

  private boolean isLastOp(@UpdateOp.Type int updateType) {
    return lastOp != null && lastOp.type == updateType;
  }

  private void addNewOperation(@Type int type, int position, int itemCount) {
    addNewOperation(type, position, itemCount, null);
  }

  private void addNewOperation(@Type int type, int position, int itemCount,
      @Nullable EpoxyModel<?> payload) {
    lastOp = UpdateOp.instance(type, position, itemCount, payload);
    opList.add(lastOp);
  }

  private void addItemsToLastOperation(int numItemsToAdd, EpoxyModel<?> payload) {
    lastOp.itemCount += numItemsToAdd;
    lastOp.addPayload(payload);
  }

  void move(int from, int to) {
    // We can't batch moves
    lastOp = null;
    UpdateOp op = UpdateOp.instance(MOVE, from, to, null);
    opList.add(op);
    moves.add(op);
  }

  int getNumRemovals() {
    return numRemovals;
  }

  boolean hasRemovals() {
    return numRemovals > 0;
  }

  int getNumInsertions() {
    return numInsertions;
  }

  boolean hasInsertions() {
    return numInsertions > 0;
  }

  int getNumMoves() {
    return moves.size();
  }

  int getNumInsertionBatches() {
    return numInsertionBatches;
  }

  int getNumRemovalBatches() {
    return numRemovalBatches;
  }
}
