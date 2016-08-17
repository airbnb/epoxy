package com.airbnb.epoxy;

import com.google.common.collect.Collections2;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static junit.framework.Assert.assertEquals;

@Config(sdk = 21, manifest = TestRunner.MANIFEST_PATH)
@RunWith(TestRunner.class)
public class DifferCorrectnessTest {
  private static final boolean SHOW_LOGS = false;
  private final List<TestModel> oldModels = new ArrayList<>();
  private final List<TestModel> newModels = new ArrayList<>();
  private final TestObserver testObserver = new TestObserver(SHOW_LOGS);
  private final TestAdapter testAdapter = new TestAdapter();

  @Before
  public void setUp() {
    testAdapter.registerAdapterDataObserver(testObserver);
  }

  @Test
  public void noChange() {
    validateWithOpCount(0);
  }

  @Test
  public void simpleUpdate() {
    ModelTestUtils.addModels(oldModels);

    newModels.addAll(oldModels);
    ModelTestUtils.changeValues(newModels);

    validateWithOpCount(1);
  }

  @Test
  public void updateStart() {
    ModelTestUtils.addModels(oldModels);

    newModels.addAll(oldModels);
    ModelTestUtils.changeValues(newModels, 0, newModels.size() / 2);

    validateWithOpCount(1);
  }

  @Test
  public void updateMiddle() {
    ModelTestUtils.addModels(oldModels);

    newModels.addAll(oldModels);
    ModelTestUtils.changeValues(newModels, newModels.size() / 3, newModels.size() * 2 / 3);

    validateWithOpCount(1);
  }

  @Test
  public void updateEnd() {
    ModelTestUtils.addModels(oldModels);

    newModels.addAll(oldModels);
    ModelTestUtils.changeValues(newModels, newModels.size() / 2, newModels.size());

    validateWithOpCount(1);
  }

  @Test
  public void shuffle() {
    // Tries all permutations of item shuffles, with various list sizes. Also randomizes
    // item values so that the diff must deal with both item updates and movements
    for (int i = 0; i < 10; i++) {
      List<TestModel> startingModels = new ArrayList<>();
      ModelTestUtils.addModels(i, startingModels);
      oldModels.clear();
      oldModels.addAll(startingModels);
      List<TestModel> modelsWithValuesChanged = new ArrayList<>(startingModels);
      ModelTestUtils.changeValues(modelsWithValuesChanged);

      int permutationNumber = 0;
      for (List<TestModel> permutedModels : Collections2.permutations(modelsWithValuesChanged)) {
        permutationNumber++;
        newModels.clear();
        newModels.addAll(permutedModels);

        if (SHOW_LOGS) {
          System.out.println(
              "\n\n***** Permutation " + permutationNumber + " - List Size: " + i + " ****** \n");
          System.out.println("old models:\n" + oldModels);
          System.out.println("\n");
          System.out.println("new models:\n" + newModels);
          System.out.println("\n");
        }
        validate();
      }
    }
  }

  @Test
  public void swapEnds() {
    ModelTestUtils.addModels(oldModels);

    newModels.addAll(oldModels);
    TestModel firstModel = newModels.remove(0);
    TestModel lastModel = newModels.remove(newModels.size() - 1);
    newModels.add(0, lastModel);
    newModels.add(firstModel);

    validateWithOpCount(2);
  }

  @Test
  public void moveFrontToEnd() {
    ModelTestUtils.addModels(oldModels);

    newModels.addAll(oldModels);
    TestModel firstModel = newModels.remove(0);
    newModels.add(firstModel);

    validateWithOpCount(1);
  }

  @Test
  public void moveEndToFront() {
    ModelTestUtils.addModels(oldModels);

    newModels.addAll(oldModels);
    TestModel lastModel = newModels.remove(newModels.size() - 1);
    newModels.add(0, lastModel);

    validateWithOpCount(1);
  }

  @Test
  public void moveEndToFrontAndChangeValues() {
    ModelTestUtils.addModels(oldModels);

    newModels.addAll(oldModels);
    TestModel lastModel = newModels.remove(newModels.size() - 1);
    newModels.add(0, lastModel);
    ModelTestUtils.changeValues(newModels);

    validateWithOpCount(2);
  }

  @Test
  public void swapHalf() {
    ModelTestUtils.addModels(oldModels);

    newModels.addAll(oldModels);
    List<TestModel> firstHalf = new ArrayList<>(newModels.subList(0, newModels.size() / 2));
    firstHalf.clear();
    newModels.addAll(firstHalf);

    validateWithOpCount(firstHalf.size());
  }

  @Test
  public void reverse() {
    ModelTestUtils.addModels(oldModels);

    newModels.addAll(oldModels);
    Collections.reverse(newModels);

    validate();
  }

  @Test
  public void removeAll() {
    ModelTestUtils.addModels(oldModels);
    validateWithOpCount(1);
  }

  @Test
  public void removeEnd() {
    ModelTestUtils.addModels(oldModels);

    newModels.addAll(oldModels);
    int half = newModels.size() / 2;
    ModelTestUtils.remove(newModels, half, half);

    validateWithOpCount(1);
  }

  @Test
  public void removeMiddle() {
    ModelTestUtils.addModels(oldModels);

    newModels.addAll(oldModels);
    int third = newModels.size() / 3;
    ModelTestUtils.remove(newModels, third, third);
    validateWithOpCount(1);
  }

  @Test
  public void removeStart() {
    ModelTestUtils.addModels(oldModels);

    newModels.addAll(oldModels);
    int half = newModels.size() / 2;
    ModelTestUtils.remove(newModels, 0, half);
    validateWithOpCount(1);
  }

  @Test
  public void multipleRemovals() {
    ModelTestUtils.addModels(oldModels);

    newModels.addAll(oldModels);
    int size = newModels.size();
    int tenth = size / 10;
    // Remove a tenth of the models at the end, middle, and start
    ModelTestUtils.removeAfter(newModels, size - tenth);
    ModelTestUtils.remove(newModels, size / 2, tenth);
    ModelTestUtils.remove(newModels, 0, tenth);

    validateWithOpCount(3);
  }

  @Test
  public void simpleAdd() {
    ModelTestUtils.addModels(newModels);
    validateWithOpCount(1);
  }

  @Test
  public void addToStart() {
    ModelTestUtils.addModels(oldModels);

    newModels.addAll(oldModels);
    ModelTestUtils.addModels(newModels, 0);

    validateWithOpCount(1);
  }

  @Test
  public void addToMiddle() {
    ModelTestUtils.addModels(oldModels);

    newModels.addAll(oldModels);
    ModelTestUtils.addModels(newModels, newModels.size() / 2);

    validateWithOpCount(1);
  }

  @Test
  public void addToEnd() {
    ModelTestUtils.addModels(oldModels);

    newModels.addAll(oldModels);
    ModelTestUtils.addModels(newModels);

    validateWithOpCount(1);
  }

  @Test
  public void multipleInsertions() {
    ModelTestUtils.addModels(oldModels);

    newModels.addAll(oldModels);
    ModelTestUtils.addModels(newModels, 0);
    ModelTestUtils.addModels(newModels, newModels.size() * 2 / 3);
    ModelTestUtils.addModels(newModels);

    validateWithOpCount(3);
  }

  private void validate() {
    validateWithOpCount(-1);
  }

  private void validateWithOpCount(int expectedOperationCount) {
    setModelsOnAdapter(oldModels);
    testAdapter.notifyModelsChanged();

    testObserver.diffedModels = new ArrayList<>(oldModels);
    testObserver.operationCount = 0;
    setModelsOnAdapter(newModels);
    testAdapter.notifyModelsChanged();

    if (expectedOperationCount != -1) {
      assertEquals("Operation count is incorrect", expectedOperationCount,
          testObserver.operationCount);
    }

    checkDiff(testObserver.diffedModels, newModels);
  }

  private void setModelsOnAdapter(List<TestModel> models) {
    testAdapter.models.clear();
    testAdapter.models.addAll(ModelTestUtils.convertList(models));
  }

  private void checkDiff(List<TestModel> diffedModels, List<TestModel> newModels) {
    assertEquals("Diff produces list of different size.", newModels.size(),
        diffedModels.size());

    for (int i = 0; i < diffedModels.size(); i++) {
      TestModel model = diffedModels.get(i);
      final TestModel expected = newModels.get(i);

      if (model == InsertedModel.INSTANCE) {
        // If the item at this index is indeed new then it shouldn't exist in the old list
        for (TestModel oldModel : diffedModels) {
          Assert.assertNotSame("The inserted model should not exist in the original list",
              oldModel.id(), expected.id());
        }
      } else {
        assertEquals("Models at same index should have same id", expected.id(), model.id());

        if (model.updated) {
          // If there was a change operation then the item hashcodes should be different
          Assert
              .assertNotSame("Incorrectly updated an item.", model.hashCode(), expected.hashCode());
        } else {
          assertEquals("Models should have same hashcode when not updated",
              expected.hashCode(), model.hashCode());
        }
      }
    }
  }
}