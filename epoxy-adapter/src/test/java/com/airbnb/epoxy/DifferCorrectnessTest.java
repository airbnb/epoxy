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
import java.util.Random;

import static com.airbnb.epoxy.ModelTestUtils.addModels;
import static com.airbnb.epoxy.ModelTestUtils.changeValues;
import static com.airbnb.epoxy.ModelTestUtils.remove;
import static junit.framework.Assert.assertEquals;

@Config(sdk = 21, manifest = TestRunner.MANIFEST_PATH)
@RunWith(TestRunner.class)
public class DifferCorrectnessTest {
  private static final boolean SHOW_LOGS = false;
  /**
   * If true, will log the time taken on the diff and skip the validation since that takes a long
   * time for big change sets.
   */
  private static final boolean SPEED_RUN = false;
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
    addModels(oldModels);

    newModels.addAll(oldModels);
    changeValues(newModels);

    validateWithOpCount(1);
  }

  @Test
  public void updateStart() {
    addModels(oldModels);

    newModels.addAll(oldModels);
    changeValues(newModels, 0, newModels.size() / 2);

    validateWithOpCount(1);
  }

  @Test
  public void updateMiddle() {
    addModels(oldModels);

    newModels.addAll(oldModels);
    changeValues(newModels, newModels.size() / 3, newModels.size() * 2 / 3);

    validateWithOpCount(1);
  }

  @Test
  public void updateEnd() {
    addModels(oldModels);

    newModels.addAll(oldModels);
    changeValues(newModels, newModels.size() / 2, newModels.size());

    validateWithOpCount(1);
  }

  @Test
  public void shuffle() {
    // Tries all permutations of item shuffles, with various list sizes. Also randomizes
    // item values so that the diff must deal with both item updates and movements
    for (int i = 0; i < 9; i++) {
      List<TestModel> startingModels = new ArrayList<>();
      addModels(i, startingModels);
      oldModels.clear();
      oldModels.addAll(startingModels);
      List<TestModel> modelsWithValuesChanged = new ArrayList<>(startingModels);
      changeValues(modelsWithValuesChanged);

      int permutationNumber = 0;
      for (List<TestModel> permutedModels : Collections2.permutations(modelsWithValuesChanged)) {
        permutationNumber++;
        newModels.clear();
        newModels.addAll(permutedModels);

        log("\n\n***** Permutation " + permutationNumber + " - List Size: " + i + " ****** \n");
        log("old models:\n" + oldModels);
        log("\n");
        log("new models:\n" + newModels);
        log("\n");

        validate();
      }
    }
  }

  @Test
  public void swapEnds() {
    addModels(oldModels);

    newModels.addAll(oldModels);
    TestModel firstModel = newModels.remove(0);
    TestModel lastModel = newModels.remove(newModels.size() - 1);
    newModels.add(0, lastModel);
    newModels.add(firstModel);

    validateWithOpCount(2);
  }

  @Test
  public void moveFrontToEnd() {
    addModels(oldModels);

    newModels.addAll(oldModels);
    TestModel firstModel = newModels.remove(0);
    newModels.add(firstModel);

    validateWithOpCount(1);
  }

  @Test
  public void moveEndToFront() {
    addModels(oldModels);

    newModels.addAll(oldModels);
    TestModel lastModel = newModels.remove(newModels.size() - 1);
    newModels.add(0, lastModel);

    validateWithOpCount(1);
  }

  @Test
  public void moveEndToFrontAndChangeValues() {
    addModels(oldModels);

    newModels.addAll(oldModels);
    TestModel lastModel = newModels.remove(newModels.size() - 1);
    newModels.add(0, lastModel);
    changeValues(newModels);

    validateWithOpCount(2);
  }

  @Test
  public void swapHalf() {
    addModels(oldModels);

    newModels.addAll(oldModels);
    List<TestModel> firstHalf = newModels.subList(0, newModels.size() / 2);
    ArrayList<TestModel> firstHalfCopy = new ArrayList<>(firstHalf);
    firstHalf.clear();
    newModels.addAll(firstHalfCopy);

    validateWithOpCount(firstHalfCopy.size());
  }

  @Test
  public void reverse() {
    addModels(oldModels);

    newModels.addAll(oldModels);
    Collections.reverse(newModels);

    validate();
  }

  @Test
  public void removeAll() {
    addModels(oldModels);
    validateWithOpCount(1);
  }

  @Test
  public void removeEnd() {
    addModels(oldModels);

    newModels.addAll(oldModels);
    int half = newModels.size() / 2;
    ModelTestUtils.remove(newModels, half, half);

    validateWithOpCount(1);
  }

  @Test
  public void removeMiddle() {
    addModels(oldModels);

    newModels.addAll(oldModels);
    int third = newModels.size() / 3;
    ModelTestUtils.remove(newModels, third, third);
    validateWithOpCount(1);
  }

  @Test
  public void removeStart() {
    addModels(oldModels);

    newModels.addAll(oldModels);
    int half = newModels.size() / 2;
    ModelTestUtils.remove(newModels, 0, half);
    validateWithOpCount(1);
  }

  @Test
  public void multipleRemovals() {
    addModels(oldModels);

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
    addModels(newModels);
    validateWithOpCount(1);
  }

  @Test
  public void addToStart() {
    addModels(oldModels);

    newModels.addAll(oldModels);
    addModels(newModels, 0);

    validateWithOpCount(1);
  }

  @Test
  public void addToMiddle() {
    addModels(oldModels);

    newModels.addAll(oldModels);
    addModels(newModels, newModels.size() / 2);

    validateWithOpCount(1);
  }

  @Test
  public void addToEnd() {
    addModels(oldModels);

    newModels.addAll(oldModels);
    addModels(newModels);

    validateWithOpCount(1);
  }

  @Test
  public void multipleInsertions() {
    addModels(oldModels);

    newModels.addAll(oldModels);
    addModels(newModels, 0);
    addModels(newModels, newModels.size() * 2 / 3);
    addModels(newModels);

    validateWithOpCount(3);
  }

  @Test
  public void moveTwoInFrontOfInsertion() {
    addModels(4, oldModels);

    newModels.addAll(oldModels);
    addModels(1, newModels, 0);

    TestModel lastModel = newModels.remove(newModels.size() - 1);
    newModels.add(0, lastModel);

    lastModel = newModels.remove(newModels.size() - 1);
    newModels.add(0, lastModel);

    validate();
  }

  @Test
  public void randomCombinations() {
    int maxBatchSize = 3;
    int maxModelCount = 10;
    int maxSeed = 10000;

    // This modifies the models list in a random way many times, with different size lists.
    for (int modelCount = 1; modelCount < maxModelCount; modelCount++) {
      for (int randomSeed = 0; randomSeed < maxSeed; randomSeed++) {
        log("\n\n*** Combination seed " + randomSeed + " Model Count: " + modelCount + " *** \n");

        oldModels.clear();
        newModels.clear();
        addModels(modelCount, oldModels);
        newModels.addAll(oldModels);

        Random random = new Random(randomSeed);
        modifyModelsRandomly(newModels, maxBatchSize, random);

        log("\nResulting diff: \n");
        validate();
      }
    }
  }

  private void modifyModelsRandomly(List<TestModel> models, int maxBatchSize, Random random) {
    for (int i = 0; i < models.size(); i++) {
      int batchSize = randInt(1, maxBatchSize, random);
      switch (random.nextInt(4)) {
        case 0:
          // insert
          log("Inserting " + batchSize + " at " + i);
          addModels(batchSize, models, i);
          i += batchSize;
          break;
        case 1:
          // remove
          int numAvailableToRemove = models.size() - i;
          batchSize = numAvailableToRemove < batchSize ? numAvailableToRemove : batchSize;

          log("Removing " + batchSize + " at " + i);
          remove(models, i, batchSize);
          break;
        case 2:
          // change
          int numAvailableToChange = models.size() - i;
          batchSize = numAvailableToChange < batchSize ? numAvailableToChange : batchSize;

          log("Changing " + batchSize + " at " + i);
          changeValues(models, i, batchSize);
          break;
        case 3:
          // move
          int targetPosition = random.nextInt(models.size());
          TestModel currentItem = models.remove(i);

          models.add(targetPosition, currentItem);
          log("Moving " + i + " to " + targetPosition);
          break;
        default:
          throw new IllegalStateException("unhandled)");
      }
    }
  }

  private void validate() {
    validateWithOpCount(-1);
  }

  private void validateWithOpCount(int expectedOperationCount) {
    setModelsOnAdapter(oldModels);

    log("\nSetting old models\n");

    testAdapter.notifyModelsChanged();

    log("\nRunning diff on new models\n");

    testObserver.diffedModels = new ArrayList<>(oldModels);
    testObserver.operationCount = 0;
    setModelsOnAdapter(newModels);

    long start = System.currentTimeMillis();
    testAdapter.notifyModelsChanged();
    long end = System.currentTimeMillis();

    if (SPEED_RUN) {
      System.out.println("Time for diff (ms): " + (end - start));
    } else {
      if (expectedOperationCount != -1) {
        assertEquals("Operation count is incorrect", expectedOperationCount,
            testObserver.operationCount);
      }

      checkDiff(testObserver.diffedModels, newModels);
    }
  }

  private static int randInt(int min, int max, Random rand) {
    // nextInt is normally exclusive of the top value,
    // so add 1 to make it inclusive
    return rand.nextInt((max - min) + 1) + min;
  }

  private void log(String text) {
    log(text, false);
  }

  private void log(String text, boolean forceShow) {
    if (forceShow || SHOW_LOGS) {
      System.out.println(text);
    }
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