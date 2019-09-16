package com.airbnb.epoxy;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
public class UpdateOpHelperTest {

  private final UpdateOpHelper helper = new UpdateOpHelper();

  @Test
  public void insertionBatch() {
    helper.add(0); // New batch
    helper.add(1); // Add at the end
    helper.add(0); // Add at the start
    helper.add(1); // Add in the middle

    assertEquals(1, helper.getNumInsertionBatches());
    assertEquals(4, helper.getNumInsertions());

    List<UpdateOp> opList = helper.opList;
    assertEquals(1, opList.size());
    assertEquals(0, opList.get(0).positionStart);
    assertEquals(4, opList.get(0).itemCount);

    assertEquals(0, helper.getNumRemovalBatches());
    assertEquals(0, helper.getNumRemovals());
    assertEquals(0, helper.getNumMoves());
  }

  @Test
  public void insertionMultipleBatches() {
    helper.add(1); // New batch
    helper.add(3); // New batch
    helper.add(1); // New batch
    helper.add(0); // New batch

    assertEquals(4, helper.getNumInsertionBatches());
    assertEquals(4, helper.getNumInsertions());

    List<UpdateOp> opList = helper.opList;
    assertEquals(4, opList.size());

    assertEquals(1, opList.get(0).positionStart);
    assertEquals(1, opList.get(0).itemCount);

    assertEquals(3, opList.get(1).positionStart);
    assertEquals(1, opList.get(1).itemCount);

    assertEquals(1, opList.get(2).positionStart);
    assertEquals(1, opList.get(2).itemCount);

    assertEquals(0, opList.get(3).positionStart);
    assertEquals(1, opList.get(3).itemCount);
  }

  @Test
  public void insertionBatchRanges() {
    helper.add(1, 2);
    helper.add(1, 1);
    helper.add(4, 1);

    assertEquals(1, helper.getNumInsertionBatches());
    assertEquals(4, helper.getNumInsertions());

    List<UpdateOp> opList = helper.opList;
    assertEquals(1, opList.size());
    assertEquals(1, opList.get(0).positionStart);
    assertEquals(4, opList.get(0).itemCount);
  }

  @Test
  public void removeBatch() {
    helper.remove(3); // New batch
    helper.remove(3); // Remove at the end
    helper.remove(2); // Remove at the start

    assertEquals(1, helper.getNumRemovalBatches());
    assertEquals(3, helper.getNumRemovals());

    List<UpdateOp> opList = helper.opList;
    assertEquals(1, opList.size());
    assertEquals(2, opList.get(0).positionStart);
    assertEquals(3, opList.get(0).itemCount);

    assertEquals(0, helper.getNumInsertionBatches());
    assertEquals(0, helper.getNumInsertions());
    assertEquals(0, helper.getNumMoves());
  }

  @Test
  public void removeMultipleBatches() {
    helper.remove(3);
    helper.remove(4);
    helper.remove(2);

    assertEquals(3, helper.getNumRemovalBatches());
    assertEquals(3, helper.getNumRemovals());

    List<UpdateOp> opList = helper.opList;
    assertEquals(3, opList.size());

    assertEquals(3, opList.get(0).positionStart);
    assertEquals(1, opList.get(0).itemCount);

    assertEquals(4, opList.get(1).positionStart);
    assertEquals(1, opList.get(1).itemCount);

    assertEquals(2, opList.get(2).positionStart);
    assertEquals(1, opList.get(2).itemCount);
  }

  @Test
  public void removeBatchRange() {
    helper.remove(3, 2);
    helper.remove(3, 2);
    helper.remove(0, 3);

    assertEquals(1, helper.getNumRemovalBatches());
    assertEquals(7, helper.getNumRemovals());

    List<UpdateOp> opList = helper.opList;
    assertEquals(1, opList.size());
    assertEquals(0, opList.get(0).positionStart);
    assertEquals(7, opList.get(0).itemCount);
  }

  @Test
  public void update() {
    helper.update(1); // New Batch
    helper.update(0); // Update at start of batch
    helper.update(2); // Update at end of batch
    helper.update(0); // Update same item as before (shouldn't be added to batch length)

    List<UpdateOp> opList = helper.opList;
    assertEquals(1, opList.size());

    assertEquals(0, opList.get(0).positionStart);
    assertEquals(3, opList.get(0).itemCount);

    assertEquals(0, helper.getNumInsertionBatches());
    assertEquals(0, helper.getNumInsertions());
    assertEquals(0, helper.getNumRemovalBatches());
    assertEquals(0, helper.getNumRemovals());
    assertEquals(0, helper.getNumMoves());
  }

  @Test
  public void updateMultipleBatches() {
    helper.update(3);
    helper.update(5);
    helper.update(3);
    helper.update(0);

    List<UpdateOp> opList = helper.opList;
    assertEquals(4, opList.size());

    assertEquals(3, opList.get(0).positionStart);
    assertEquals(1, opList.get(0).itemCount);

    assertEquals(5, opList.get(1).positionStart);
    assertEquals(1, opList.get(1).itemCount);

    assertEquals(3, opList.get(2).positionStart);
    assertEquals(1, opList.get(2).itemCount);

    assertEquals(0, opList.get(3).positionStart);
    assertEquals(1, opList.get(3).itemCount);

    assertEquals(0, helper.getNumInsertionBatches());
    assertEquals(0, helper.getNumInsertions());
    assertEquals(0, helper.getNumRemovalBatches());
    assertEquals(0, helper.getNumRemovals());
    assertEquals(0, helper.getNumMoves());
  }

  @Test
  public void moves() {
    helper.move(0, 3);
    helper.move(0, 4);

    assertEquals(2, helper.getNumMoves());

    assertEquals(0, helper.getNumInsertionBatches());
    assertEquals(0, helper.getNumInsertions());
    assertEquals(0, helper.getNumRemovalBatches());
    assertEquals(0, helper.getNumRemovals());

    List<UpdateOp> opList = helper.opList;
    assertEquals(2, opList.size());

    assertEquals(0, opList.get(0).positionStart);
    assertEquals(3, opList.get(0).itemCount);

    assertEquals(0, opList.get(0).positionStart);
    assertEquals(3, opList.get(0).itemCount);
  }
}