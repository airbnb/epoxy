package com.airbnb.epoxy;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.List;

import static com.airbnb.epoxy.ModelTestUtils.addModels;
import static com.airbnb.epoxy.ModelTestUtils.changeValue;
import static com.airbnb.epoxy.ModelTestUtils.remove;
import static junit.framework.Assert.assertEquals;

/**
 * Tests that changes to the models via notify calls besides
 * {@link EpoxyAdapter#notifyModelsChanged()}
 * will properly update the model state maintained by the differ.
 */
@RunWith(RobolectricTestRunner.class)
public class DifferNotifyTest {
  private static final int INITIAL_MODEL_COUNT = 20;

  private static final boolean SHOW_LOGS = false;
  private final TestObserver testObserver = new TestObserver(SHOW_LOGS);
  private final TestAdapter adapter = new TestAdapter();
  private final List<EpoxyModel<?>> models = adapter.models;

  @Test(expected = UnsupportedOperationException.class)
  public void notifyChange() {
    adapter.notifyDataSetChanged();
  }

  @Test
  public void notifyAddedToEmpty() {
    addModels(models);
    adapter.notifyItemRangeInserted(0, models.size());
    assertCorrectness();
  }

  @Test
  public void notifyAddedToStart() {
    addInitialModels();

    addModels(models, 0);
    adapter.notifyItemRangeInserted(0, models.size() - INITIAL_MODEL_COUNT);
    assertCorrectness();
  }

  @Test
  public void notifyAddedToEnd() {
    addInitialModels();

    addModels(models, INITIAL_MODEL_COUNT);
    adapter.notifyItemRangeInserted(INITIAL_MODEL_COUNT, models.size() - INITIAL_MODEL_COUNT);
    assertCorrectness();
  }

  @Test
  public void notifyAddedToMiddle() {
    addInitialModels();

    addModels(models, INITIAL_MODEL_COUNT / 2);
    adapter.notifyItemRangeInserted(INITIAL_MODEL_COUNT / 2, models.size() - INITIAL_MODEL_COUNT);
    assertCorrectness();
  }

  @Test
  public void notifyRemoveAll() {
    addInitialModels();

    models.clear();
    adapter.notifyItemRangeRemoved(0, INITIAL_MODEL_COUNT);
    assertCorrectness();
  }

  @Test
  public void notifyRemoveStart() {
    addInitialModels();

    remove(models, 0, INITIAL_MODEL_COUNT / 2);
    adapter.notifyItemRangeRemoved(0, INITIAL_MODEL_COUNT / 2);
    assertCorrectness();
  }

  @Test
  public void notifyRemoveMiddle() {
    addInitialModels();

    remove(models, INITIAL_MODEL_COUNT / 3, INITIAL_MODEL_COUNT / 3);
    adapter.notifyItemRangeRemoved(INITIAL_MODEL_COUNT / 3, INITIAL_MODEL_COUNT / 3);
    assertCorrectness();
  }

  @Test
  public void notifyRemoveEnd() {
    addInitialModels();

    remove(models, INITIAL_MODEL_COUNT / 2, INITIAL_MODEL_COUNT / 2);
    adapter.notifyItemRangeRemoved(INITIAL_MODEL_COUNT / 2, INITIAL_MODEL_COUNT / 2);
    assertCorrectness();
  }

  @Test
  public void notifyFrontMovedToEnd() {
    addInitialModels();

    EpoxyModel<?> modelToMove = models.remove(0);
    models.add(modelToMove);
    adapter.notifyItemMoved(0, INITIAL_MODEL_COUNT - 1);
    assertCorrectness();
  }

  @Test
  public void notifyEndMovedToFront() {
    addInitialModels();

    EpoxyModel<?> modelToMove = models.remove(INITIAL_MODEL_COUNT - 1);
    models.add(0, modelToMove);
    adapter.notifyItemMoved(INITIAL_MODEL_COUNT - 1, 0);
    assertCorrectness();
  }

  @Test
  public void notifyMiddleMovedToEnd() {
    addInitialModels();

    EpoxyModel<?> modelToMove = models.remove(INITIAL_MODEL_COUNT / 2);
    models.add(modelToMove);
    adapter.notifyItemMoved(INITIAL_MODEL_COUNT / 2, INITIAL_MODEL_COUNT - 1);
    assertCorrectness();
  }

  @Test
  public void notifyMiddleMovedToFront() {
    addInitialModels();

    EpoxyModel<?> modelToMove = models.remove(INITIAL_MODEL_COUNT / 2);
    models.add(0, modelToMove);
    adapter.notifyItemMoved(INITIAL_MODEL_COUNT / 2, 0);
    assertCorrectness();
  }

  @Test
  public void notifyValuesUpdated() {
    addInitialModels();

    int numModelsUpdated = 0;
    for (int i = INITIAL_MODEL_COUNT / 3; i < INITIAL_MODEL_COUNT * 2 / 3; i++) {
      changeValue(models.get(i));
      numModelsUpdated++;
    }

    adapter.notifyItemRangeChanged(INITIAL_MODEL_COUNT / 3, numModelsUpdated);
    assertCorrectness();
  }

  private void addInitialModels() {
    addModels(INITIAL_MODEL_COUNT, models);
    adapter.notifyModelsChanged();
  }

  private void assertCorrectness() {
    testObserver.operationCount = 0;

    adapter.registerAdapterDataObserver(testObserver);
    adapter.notifyModelsChanged();
    adapter.unregisterAdapterDataObserver(testObserver);

    assertEquals("Should not have any operations", 0, testObserver.operationCount);
  }
}