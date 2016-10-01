package com.airbnb.epoxy;

import com.airbnb.epoxy.UpdateOp.Type;

import java.util.ArrayList;
import java.util.List;

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

    if (!batchWithLast) {
      numInsertionBatches++;
    }

    addOperation(ADD, startPosition, itemCount, batchWithLast);
  }

  void update(int indexToChange) {
    update(indexToChange, 1);
  }

  void update(int startPosition, int itemCount) {
    boolean batchWithLast = false;

    if (isLastOp(UPDATE)) {
      int lastIndexInRange = startPosition + itemCount - 1;

      if (lastOp.contains(startPosition) && lastOp.contains(lastIndexInRange)) {
        // These items have already been included in the existing batch range
        return;
      }

      if (lastOp.isAfter(startPosition) && lastOp.isBefore(lastIndexInRange)) {
        // Include more items to update at both the start and end of the batch range
        itemCount -= lastOp.itemCount;
        lastOp.positionStart = startPosition;
        batchWithLast = true;
      } else if (lastOp.isAfter(startPosition) && lastIndexInRange >= lastOp.positionStart - 1) {
        // Include more items to update at the start of the batch range
        itemCount = lastOp.positionStart - startPosition;
        lastOp.positionStart = startPosition;
        batchWithLast = true;
      } else if (startPosition <= lastOp.positionEnd() && lastOp.isBefore(lastIndexInRange)) {
        // Include more items at the end of the batch range
        itemCount -= lastOp.positionEnd() - startPosition;
        batchWithLast = true;
      }
    }

    addOperation(UPDATE, startPosition, itemCount, batchWithLast);
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

    if (!batchWithLast) {
      numRemovalBatches++;
    }

    addOperation(REMOVE, startPosition, itemCount, batchWithLast);
  }

  private boolean isLastOp(@UpdateOp.Type int updateType) {
    return lastOp != null && lastOp.type == updateType;
  }

  private void addOperation(@Type int type, int position, int itemCount, boolean batchWithLast) {
    if (batchWithLast) {
      lastOp.itemCount += itemCount;
    } else {
      lastOp = UpdateOp.instance(type, position, itemCount);
      opList.add(lastOp);
    }
  }

  void move(int from, int to) {
    // We can't batch moves
    lastOp = null;
    UpdateOp op = UpdateOp.instance(MOVE, from, to);
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
