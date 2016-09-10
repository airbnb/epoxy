package com.airbnb.epoxy;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static junit.framework.Assert.assertEquals;

/**
 * Tests that changes to the models via notify calls besides {@link EpoxyAdapter#notifyModelsChanged()}
 * will properly update the model state maintained by the differ.
 */
@Config(sdk = 21, manifest = TestRunner.MANIFEST_PATH)
@RunWith(TestRunner.class)
public class DifferNotifyTest {
  private static final int MODEL_COUNT = 20;

  private static final boolean SHOW_LOGS = false;
  private final List<TestModel> testModels = new ArrayList<>();
  private final TestObserver testObserver = new TestObserver(SHOW_LOGS);
  private final TestAdapter testAdapter = new TestAdapter();

  @Test
  public void notifyChange() {
    addTestModels();
    assertCorrectness();

    Collections.reverse(testAdapter.models);
    testAdapter.notifyDataSetChanged();
    assertCorrectness();
  }

  @Test
  public void notifyAddedToEmpty() {
    // Add to empty
    ModelTestUtils.addModels(testModels);
    setModelsOnAdapter();
    testAdapter.notifyItemRangeInserted(0, testModels.size());
    assertCorrectness();
  }

  @Test
  public void notifyAddedToStart() {
    addTestModels();

    ModelTestUtils.addModels(testModels, 0);
    setModelsOnAdapter();
    testAdapter.notifyItemRangeInserted(0, testModels.size() - MODEL_COUNT);
    assertCorrectness();
  }

  @Test
  public void notifyAddedToEnd() {
    addTestModels();

    ModelTestUtils.addModels(testModels, MODEL_COUNT);
    setModelsOnAdapter();
    testAdapter.notifyItemRangeInserted(MODEL_COUNT, testModels.size() - MODEL_COUNT);
    assertCorrectness();
  }

  @Test
  public void notifyAddedToMiddle() {
    addTestModels();

    ModelTestUtils.addModels(testModels, MODEL_COUNT / 2);
    setModelsOnAdapter();
    testAdapter.notifyItemRangeInserted(MODEL_COUNT / 2, testModels.size() - MODEL_COUNT);
    assertCorrectness();
  }

  @Test
  public void notifyRemoveAll() {
    addTestModels();
    testModels.clear();
    setModelsOnAdapter();
    testAdapter.notifyItemRangeRemoved(0, MODEL_COUNT);
    assertCorrectness();
  }

  @Test
  public void notifyRemoveStart() {
    addTestModels();
    ModelTestUtils.remove(testModels, 0, MODEL_COUNT / 2);
    setModelsOnAdapter();
    testAdapter.notifyItemRangeRemoved(0, MODEL_COUNT / 2);
    assertCorrectness();
  }

  @Test
  public void notifyRemoveMiddle() {
    addTestModels();
    ModelTestUtils.remove(testModels, MODEL_COUNT / 3, MODEL_COUNT / 3);
    setModelsOnAdapter();
    testAdapter.notifyItemRangeRemoved(MODEL_COUNT / 3, MODEL_COUNT / 3);
    assertCorrectness();
  }

  @Test
  public void notifyRemoveEnd() {
    addTestModels();
    ModelTestUtils.remove(testModels, MODEL_COUNT / 2, MODEL_COUNT / 2);
    setModelsOnAdapter();
    testAdapter.notifyItemRangeRemoved(MODEL_COUNT / 2, MODEL_COUNT / 2);
    assertCorrectness();
  }

  @Test
  public void notifyFrontMovedToEnd() {
    addTestModels();
    TestModel modelToMove = testModels.remove(0);
    testModels.add(modelToMove);
    setModelsOnAdapter();
    testAdapter.notifyItemMoved(0, MODEL_COUNT - 1);
    assertCorrectness();
  }

  @Test
  public void notifyEndMovedToFront() {
    addTestModels();
    TestModel modelToMove = testModels.remove(MODEL_COUNT - 1);
    testModels.add(0, modelToMove);
    setModelsOnAdapter();
    testAdapter.notifyItemMoved(MODEL_COUNT - 1, 0);
    assertCorrectness();
  }

  @Test
  public void notifyMiddleMovedToEnd() {
    addTestModels();
    TestModel modelToMove = testModels.remove(MODEL_COUNT / 2);
    testModels.add(modelToMove);
    setModelsOnAdapter();
    testAdapter.notifyItemMoved(MODEL_COUNT / 2, MODEL_COUNT - 1);
    assertCorrectness();
  }

  @Test
  public void notifyMiddleMovedToFront() {
    addTestModels();
    TestModel modelToMove = testModels.remove(MODEL_COUNT / 2);
    testModels.add(0, modelToMove);
    setModelsOnAdapter();
    testAdapter.notifyItemMoved(MODEL_COUNT / 2, 0);
    assertCorrectness();
  }

  @Test
  public void notifyValuesUpdated() {
    addTestModels();
    int numModelsUpdated = 0;
    for (int i = MODEL_COUNT / 3; i < MODEL_COUNT * 2 / 3; i++) {
      testModels.get(i).randomizeValue();
      numModelsUpdated++;
    }

    setModelsOnAdapter();
    testAdapter.notifyItemRangeChanged(MODEL_COUNT / 3, numModelsUpdated);
    assertCorrectness();
  }

  private void addTestModels() {
    ModelTestUtils.addModels(MODEL_COUNT, testModels);
    setModelsOnAdapter();
    testAdapter.notifyDataSetChanged();
  }

  private void setModelsOnAdapter() {
    testAdapter.models.clear();
    testAdapter.models.addAll(ModelTestUtils.convertToGenericModels(testModels));
  }

  private void assertCorrectness() {
    testAdapter.notifyModelsChanged();
    assertEquals("Should not have any operations", 0, testObserver.operationCount);
  }
}