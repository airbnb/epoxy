package com.airbnb.epoxy;

import com.google.common.collect.Collections2;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static com.airbnb.epoxy.ModelTestUtils.addModels;
import static com.airbnb.epoxy.ModelTestUtils.changeValues;
import static com.airbnb.epoxy.ModelTestUtils.convertToTestModels;
import static com.airbnb.epoxy.ModelTestUtils.remove;
import static com.airbnb.epoxy.ModelTestUtils.removeModelsAfterPosition;
import static junit.framework.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
public class DifferCorrectnessTest {
  private static final boolean SHOW_LOGS = false;
  /**
   * If true, will log the time taken on the diff and skip the validation since that takes a long
   * time for big change sets.
   */
  private static final boolean SPEED_RUN = false;
  private final TestObserver testObserver = new TestObserver(SHOW_LOGS);
  private final TestAdapter testAdapter = new TestAdapter();
  private final List<EpoxyModel<?>> models = testAdapter.models;
  private static long totalDiffMillis = 0;
  private static long totalDiffOperations = 0;
  private static long totalDiffs = 0;

  @BeforeClass
  public static void beforeClass() {
    totalDiffMillis = 0;
    totalDiffOperations = 0;
    totalDiffs = 0;
  }

  @AfterClass
  public static void afterClass() {
    if (SPEED_RUN) {
      System.out.println("Total time for all diffs (ms): " + totalDiffMillis);
    } else {
      System.out.println("Total operations for diffs: " + totalDiffOperations);

      double avgOperations = ((double) totalDiffOperations / totalDiffs);
      System.out.println("Average operations per diff: " + avgOperations);
    }
  }

  @Before
  public void setUp() {
    if (!SPEED_RUN) {
      testAdapter.registerAdapterDataObserver(testObserver);
    }
  }

  @Test
  public void noChange() {
    diffAndValidateWithOpCount(0);
  }

  @Test
  public void simpleUpdate() {
    addModels(models);
    diffAndValidate();

    changeValues(models);
    diffAndValidateWithOpCount(1);
  }

  @Test
  public void updateStart() {
    addModels(models);
    diffAndValidate();

    changeValues(models, 0, models.size() / 2);
    diffAndValidateWithOpCount(1);
  }

  @Test
  public void updateMiddle() {
    addModels(models);
    diffAndValidate();

    changeValues(models, models.size() / 3, models.size() * 2 / 3);
    diffAndValidateWithOpCount(1);
  }

  @Test
  public void updateEnd() {
    addModels(models);
    diffAndValidate();

    changeValues(models, models.size() / 2, models.size());
    diffAndValidateWithOpCount(1);
  }

  @Test
  public void shuffle() {
    // Tries all permutations of item shuffles, with various list sizes. Also randomizes
    // item values so that the diff must deal with both item updates and movements
    for (int i = 0; i < 9; i++) {
      List<EpoxyModel<?>> originalModels = new ArrayList<>();
      addModels(i, originalModels);
      int permutationNumber = 0;
      for (List<EpoxyModel<?>> permutedModels : Collections2.permutations(originalModels)) {
        permutationNumber++;

        // Resetting to the original models each time, otherwise each subsequent permutation is
        // only a small difference
        models.clear();
        models.addAll(originalModels);
        diffAndValidate();

        models.clear();
        models.addAll(permutedModels);
        changeValues(models);

        log("\n\n***** Permutation " + permutationNumber + " - List Size: " + i + " ****** \n");
        log("old models:\n" + models);
        log("\n");
        log("new models:\n" + models);
        log("\n");

        diffAndValidate();
      }
    }
  }

  @Test
  public void swapEnds() {
    addModels(models);
    diffAndValidate();

    EpoxyModel<?> firstModel = models.remove(0);
    EpoxyModel<?> lastModel = models.remove(models.size() - 1);
    models.add(0, lastModel);
    models.add(firstModel);

    diffAndValidateWithOpCount(2);
  }

  @Test
  public void moveFrontToEnd() {
    addModels(models);
    diffAndValidate();

    EpoxyModel<?> firstModel = models.remove(0);
    models.add(firstModel);

    diffAndValidateWithOpCount(1);
  }

  @Test
  public void moveEndToFront() {
    addModels(models);
    diffAndValidate();

    EpoxyModel<?> lastModel = models.remove(models.size() - 1);
    models.add(0, lastModel);

    diffAndValidateWithOpCount(1);
  }

  @Test
  public void moveEndToFrontAndChangeValues() {
    addModels(models);
    diffAndValidate();

    EpoxyModel<?> lastModel = models.remove(models.size() - 1);
    models.add(0, lastModel);
    changeValues(models);

    diffAndValidateWithOpCount(2);
  }

  @Test
  public void swapHalf() {
    addModels(models);
    diffAndValidate();

    List<EpoxyModel<?>> firstHalf = models.subList(0, models.size() / 2);
    ArrayList<EpoxyModel<?>> firstHalfCopy = new ArrayList<>(firstHalf);
    firstHalf.clear();
    models.addAll(firstHalfCopy);

    diffAndValidateWithOpCount(firstHalfCopy.size());
  }

  @Test
  public void reverse() {
    addModels(models);
    diffAndValidate();

    Collections.reverse(models);
    diffAndValidate();
  }

  @Test
  public void removeAll() {
    addModels(models);
    diffAndValidate();

    models.clear();
    diffAndValidateWithOpCount(1);
  }

  @Test
  public void removeEnd() {
    addModels(models);
    diffAndValidate();

    int half = models.size() / 2;
    ModelTestUtils.remove(models, half, half);

    diffAndValidateWithOpCount(1);
  }

  @Test
  public void removeMiddle() {
    addModels(models);
    diffAndValidate();

    int third = models.size() / 3;
    ModelTestUtils.remove(models, third, third);
    diffAndValidateWithOpCount(1);
  }

  @Test
  public void removeStart() {
    addModels(models);
    diffAndValidate();

    int half = models.size() / 2;
    ModelTestUtils.remove(models, 0, half);
    diffAndValidateWithOpCount(1);
  }

  @Test
  public void multipleRemovals() {
    addModels(models);
    diffAndValidate();

    int size = models.size();
    int tenth = size / 10;
    // Remove a tenth of the models at the end, middle, and start
    ModelTestUtils.removeModelsAfterPosition(models, size - tenth);
    ModelTestUtils.remove(models, size / 2, tenth);
    ModelTestUtils.remove(models, 0, tenth);

    diffAndValidateWithOpCount(3);
  }

  @Test
  public void simpleAdd() {
    addModels(models);
    diffAndValidateWithOpCount(1);
  }

  @Test
  public void addToStart() {
    addModels(models);
    diffAndValidate();

    addModels(models, 0);
    diffAndValidateWithOpCount(1);
  }

  @Test
  public void addToMiddle() {
    addModels(models);
    diffAndValidate();

    addModels(models, models.size() / 2);
    diffAndValidateWithOpCount(1);
  }

  @Test
  public void addToEnd() {
    addModels(models);
    diffAndValidate();

    addModels(models);
    diffAndValidateWithOpCount(1);
  }

  @Test
  public void multipleInsertions() {
    addModels(models);
    diffAndValidate();

    addModels(models, 0);
    addModels(models, models.size() * 2 / 3);
    addModels(models);

    diffAndValidateWithOpCount(3);
  }

  @Test
  public void moveTwoInFrontOfInsertion() {
    addModels(4, models);
    diffAndValidate();

    addModels(1, models, 0);

    EpoxyModel<?> lastModel = models.remove(models.size() - 1);
    models.add(0, lastModel);

    lastModel = models.remove(models.size() - 1);
    models.add(0, lastModel);

    diffAndValidate();
  }

  @Test
  public void randomCombinations() {
    int maxBatchSize = 3;
    int maxModelCount = 10;
    int maxSeed = 100000;

    // This modifies the models list in a random way many times, with different size lists.
    for (int modelCount = 1; modelCount < maxModelCount; modelCount++) {
      for (int randomSeed = 0; randomSeed < maxSeed; randomSeed++) {
        log("\n\n*** Combination seed " + randomSeed + " Model Count: " + modelCount + " *** \n");

        // We keep the list from the previous loop and keep modifying it. This allows us to test
        // that state is maintained properly between diffs. We just make sure the list size
        // says the same by adding or removing if necessary
        int currentModelCount = models.size();
        if (currentModelCount < modelCount) {
          addModels(modelCount - currentModelCount, models);
        } else if (currentModelCount > modelCount) {
          removeModelsAfterPosition(models, modelCount);
        }
        diffAndValidate();

        modifyModelsRandomly(models, maxBatchSize, new Random(randomSeed));
        log("\nResulting diff: \n");
        diffAndValidate();
      }
    }
  }

  private void modifyModelsRandomly(List<EpoxyModel<?>> models, int maxBatchSize, Random random) {
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
          EpoxyModel<?> currentItem = models.remove(i);

          models.add(targetPosition, currentItem);
          log("Moving " + i + " to " + targetPosition);
          break;
        default:
          throw new IllegalStateException("unhandled)");
      }
    }
  }

  private void diffAndValidate() {
    diffAndValidateWithOpCount(-1);
  }

  private void diffAndValidateWithOpCount(int expectedOperationCount) {
    testObserver.operationCount = 0;

    long start = System.currentTimeMillis();
    testAdapter.notifyModelsChanged();
    long end = System.currentTimeMillis();
    totalDiffMillis += (end - start);
    totalDiffOperations += testObserver.operationCount;
    totalDiffs++;

    if (!SPEED_RUN) {
      if (expectedOperationCount != -1) {
        assertEquals("Operation count is incorrect", expectedOperationCount,
            testObserver.operationCount);
      }

      List<TestModel> newModels = convertToTestModels(models);
      checkDiff(testObserver.initialModels, testObserver.modelsAfterDiffing, newModels);
      testObserver.setUpForNextDiff(newModels);
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

  private void checkDiff(List<TestModel> modelsBeforeDiff, List<TestModel> modelsAfterDiff,
      List<TestModel> actualModels) {
    assertEquals("Diff produces list of different size.", actualModels.size(),
        modelsAfterDiff.size());

    for (int i = 0; i < modelsAfterDiff.size(); i++) {
      TestModel model = modelsAfterDiff.get(i);
      final TestModel expected = actualModels.get(i);

      if (model == InsertedModel.INSTANCE) {
        // If the item at this index is new then it shouldn't exist in the original list
        for (TestModel oldModel : modelsBeforeDiff) {
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

        // Clear state so the model can be used again in another diff
        model.updated = false;
      }
    }
  }
}