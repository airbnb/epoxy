package com.airbnb.epoxy;

import android.support.v7.widget.RecyclerView.AdapterDataObserver;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Config(sdk = 21, manifest = TestRunner.MANIFEST_PATH)
@RunWith(TestRunner.class)
public class EpoxyAdapterTest {

  private final TestAdapter testAdapter = new TestAdapter();
  @Mock AdapterDataObserver observer;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
    testAdapter.registerAdapterDataObserver(observer);
  }

  @Test
  public void testAddModels() {
    testAdapter.addModels(new TestModel(), new TestModel());
    verify(observer).onItemRangeInserted(0, 2);
    assertEquals(2, testAdapter.models.size());

    testAdapter.addModels(new TestModel(), new TestModel());
    verify(observer).onItemRangeInserted(2, 2);
    assertEquals(4, testAdapter.models.size());
  }

  @Test
  public void testNotifyModelChanged() {
    TestModel testModel = new TestModel();
    testAdapter.addModels(testModel);
    testAdapter.notifyModelChanged(testModel);
    verify(observer).onItemRangeChanged(0, 1, null);
  }

  @Test(expected = IllegalStateException.class)
  public void testInsertModelBeforeThrowsForInvalidModel() {
    testAdapter.insertModelBefore(new TestModel(), new TestModel());
  }

  @Test()
  public void testInsertModelBefore() {
    TestModel firstModel = new TestModel();
    testAdapter.addModels(firstModel);
    testAdapter.insertModelBefore(new TestModel(), firstModel);

    verify(observer, times(2)).onItemRangeInserted(0, 1);
    assertEquals(2, testAdapter.models.size());
    assertEquals(firstModel, testAdapter.models.get(1));
  }

  @Test(expected = IllegalStateException.class)
  public void testInsertModelAfterThrowsForInvalidModel() {
    testAdapter.insertModelAfter(new TestModel(), new TestModel());
  }

  @Test()
  public void testInsertModelAfter() {
    TestModel firstModel = new TestModel();
    testAdapter.addModels(firstModel);
    testAdapter.insertModelAfter(new TestModel(), firstModel);

    verify(observer).onItemRangeInserted(1, 1);
    assertEquals(2, testAdapter.models.size());
    assertEquals(firstModel, testAdapter.models.get(0));
  }

  @Test
  public void testRemoveModels() {
    TestModel testModel = new TestModel();
    testAdapter.addModels(testModel);

    testAdapter.removeModel(testModel);
    verify(observer).onItemRangeRemoved(0, 1);
    assertEquals(0, testAdapter.models.size());
  }

  @Test
  public void testRemoveAllAfterModels() {
    List<TestModel> models = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      TestModel model = new TestModel();
      models.add(model);
      testAdapter.addModels(model);
    }

    testAdapter.removeAllAfterModel(models.get(5));
    verify(observer).onItemRangeRemoved(6, 4);
    assertEquals(models.subList(0, 6), testAdapter.models);
  }

  @Test
  public void testShowModel() {
    TestModel testModel = new TestModel();
    testModel.hide();
    testAdapter.addModels(testModel);

    testAdapter.showModel(testModel);
    verify(observer).onItemRangeChanged(0, 1, null);
    assertTrue(testModel.isShown());
  }

  @Test
  public void testShowModelNoopIfAlreadyShown() {
    TestModel testModel = new TestModel();
    testAdapter.addModels(testModel);

    testAdapter.showModel(testModel);
    verify(observer, times(0)).onItemRangeChanged(0, 1, null);
    assertTrue(testModel.isShown());
  }

  @Test
  public void testHideModel() {
    TestModel testModel = new TestModel();
    testAdapter.addModels(testModel);

    testAdapter.hideModel(testModel);
    verify(observer).onItemRangeChanged(0, 1, null);
    assertFalse(testModel.isShown());
  }

  @Test
  public void testHideAllAfterModel() {
    List<TestModel> models = new ArrayList<>();
    int modelCount = 10;
    for (int i = 0; i < modelCount; i++) {
      TestModel model = new TestModel();
      models.add(model);
      testAdapter.addModels(model);
    }

    int hideIndex = 5;
    testAdapter.hideAllAfterModel(models.get(hideIndex));
    for (int i = hideIndex + 1; i < modelCount; i++) {
      verify(observer).onItemRangeChanged(i, 1, null);
    }

    for (int i = 0; i < modelCount; i++) {
      assertEquals(i <= hideIndex, models.get(i).isShown());
    }
  }
}