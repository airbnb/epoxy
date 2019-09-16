package com.airbnb.epoxy;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
public class EpoxyAdapterTest {

  @Rule public ExpectedException thrown = ExpectedException.none();
  private final TestAdapter testAdapter = new TestAdapter();
  private final TestObserver differObserver = new TestObserver();
  @Mock RecyclerView.AdapterDataObserver observer;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
    testAdapter.registerAdapterDataObserver(observer);
  }

  @Test
  public void testAddModel() {
    testAdapter.addModel(new TestModel());
    verify(observer).onItemRangeInserted(0, 1);
    assertEquals(1, testAdapter.models.size());

    testAdapter.addModel(new TestModel());
    verify(observer).onItemRangeInserted(1, 1);
    assertEquals(2, testAdapter.models.size());
    checkDifferState();
  }

  @Test
  public void testAddModels() {
    List<TestModel> list = new ArrayList<>();
    list.add(new TestModel());
    list.add(new TestModel());

    testAdapter.addModels(list);
    verify(observer).onItemRangeInserted(0, 2);
    assertEquals(2, testAdapter.models.size());

    List<TestModel> list2 = new ArrayList<>();
    list2.add(new TestModel());
    list2.add(new TestModel());
    testAdapter.addModels(list2);
    verify(observer).onItemRangeInserted(2, 2);
    assertEquals(4, testAdapter.models.size());

    checkDifferState();
  }

  @Test
  public void testAddModelsVarArgs() {
    testAdapter.addModels(new TestModel(), new TestModel());
    verify(observer).onItemRangeInserted(0, 2);
    assertEquals(2, testAdapter.models.size());

    testAdapter.addModels(new TestModel(), new TestModel());
    verify(observer).onItemRangeInserted(2, 2);
    assertEquals(4, testAdapter.models.size());

    checkDifferState();
  }

  @Test
  public void testNotifyModelChanged() {
    TestModel testModel = new TestModel();
    testAdapter.addModels(testModel);
    testAdapter.notifyModelChanged(testModel);
    verify(observer).onItemRangeChanged(0, 1, null);

    checkDifferState();
  }

  @Test
  public void testNotifyModelChangedWithPayload() {
    Object payload = new Object();
    TestModel testModel = new TestModel();
    testAdapter.addModels(testModel);
    testAdapter.notifyModelChanged(testModel, payload);
    verify(observer).onItemRangeChanged(0, 1, payload);

    checkDifferState();
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

    checkDifferState();
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

    checkDifferState();
  }

  @Test
  public void testRemoveModels() {
    TestModel testModel = new TestModel();
    testAdapter.addModels(testModel);

    testAdapter.removeModel(testModel);
    verify(observer).onItemRangeRemoved(0, 1);
    assertEquals(0, testAdapter.models.size());

    checkDifferState();
  }

  @Test
  public void testRemoveAllModels() {
    for (int i = 0; i < 10; i++) {
      TestModel model = new TestModel();
      testAdapter.addModels(model);
    }

    testAdapter.removeAllModels();
    verify(observer).onItemRangeRemoved(0, 10);
    assertEquals(0, testAdapter.models.size());

    checkDifferState();
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

    checkDifferState();
  }

  @Test
  public void testShowModel() {
    TestModel testModel = new TestModel();
    testModel.hide();
    testAdapter.addModels(testModel);

    testAdapter.showModel(testModel);
    verify(observer).onItemRangeChanged(0, 1, null);
    assertTrue(testModel.isShown());

    checkDifferState();
  }

  @Test
  public void testShowModels() {
    TestModel testModel1 = new TestModel();
    testModel1.hide();

    TestModel testModel2 = new TestModel();
    testModel2.hide();

    testAdapter.addModels(testModel1, testModel2);

    testAdapter.showModels(testAdapter.models);
    verify(observer).onItemRangeChanged(0, 1, null);
    verify(observer).onItemRangeChanged(1, 1, null);
    assertTrue(testModel1.isShown());
    assertTrue(testModel2.isShown());

    checkDifferState();
  }

  @Test
  public void testShowModelsVarArgs() {
    TestModel testModel1 = new TestModel();
    testModel1.hide();

    TestModel testModel2 = new TestModel();
    testModel2.hide();

    testAdapter.addModels(testModel1, testModel2);

    testAdapter.showModels(testModel1, testModel2);
    verify(observer).onItemRangeChanged(0, 1, null);
    verify(observer).onItemRangeChanged(1, 1, null);
    assertTrue(testModel1.isShown());
    assertTrue(testModel2.isShown());

    checkDifferState();
  }

  @Test
  public void testShowModelsConditionalTrue() {
    TestModel testModel1 = new TestModel();
    testModel1.hide();

    TestModel testModel2 = new TestModel();
    testModel2.hide();

    testAdapter.addModels(testModel1, testModel2);

    testAdapter.showModels(testAdapter.models, true);
    verify(observer).onItemRangeChanged(0, 1, null);
    verify(observer).onItemRangeChanged(1, 1, null);
    assertTrue(testModel1.isShown());
    assertTrue(testModel2.isShown());

    checkDifferState();
  }

  @Test
  public void testShowModelsVarArgsConditionalTrue() {
    TestModel testModel1 = new TestModel();
    testModel1.hide();

    TestModel testModel2 = new TestModel();
    testModel2.hide();

    testAdapter.addModels(testModel1, testModel2);

    testAdapter.showModels(true, testModel1, testModel2);
    verify(observer).onItemRangeChanged(0, 1, null);
    verify(observer).onItemRangeChanged(1, 1, null);
    assertTrue(testModel1.isShown());
    assertTrue(testModel2.isShown());

    checkDifferState();
  }

  @Test
  public void testShowModelsConditionalFalse() {
    TestModel testModel1 = new TestModel();
    TestModel testModel2 = new TestModel();
    testAdapter.addModels(testModel1, testModel2);

    testAdapter.showModels(testAdapter.models, false);
    verify(observer).onItemRangeChanged(0, 1, null);
    verify(observer).onItemRangeChanged(1, 1, null);
    assertFalse(testModel1.isShown());
    assertFalse(testModel2.isShown());

    checkDifferState();
  }

  @Test
  public void testShowModelsVarArgsConditionalFalse() {
    TestModel testModel1 = new TestModel();
    TestModel testModel2 = new TestModel();
    testAdapter.addModels(testModel1, testModel2);

    testAdapter.showModels(false, testModel1, testModel2);
    verify(observer).onItemRangeChanged(0, 1, null);
    verify(observer).onItemRangeChanged(1, 1, null);
    assertFalse(testModel1.isShown());
    assertFalse(testModel2.isShown());

    checkDifferState();
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

    checkDifferState();
  }

  @Test
  public void testHideModels() {
    TestModel testModel1 = new TestModel();
    TestModel testModel2 = new TestModel();
    testAdapter.addModels(testModel1, testModel2);

    testAdapter.hideModels(testAdapter.models);
    verify(observer).onItemRangeChanged(0, 1, null);
    verify(observer).onItemRangeChanged(1, 1, null);
    assertFalse(testModel1.isShown());
    assertFalse(testModel2.isShown());

    checkDifferState();
  }

  @Test
  public void testHideModelsVarArgs() {
    TestModel testModel1 = new TestModel();
    TestModel testModel2 = new TestModel();
    testAdapter.addModels(testModel1, testModel2);

    testAdapter.hideModels(testModel1, testModel2);
    verify(observer).onItemRangeChanged(0, 1, null);
    verify(observer).onItemRangeChanged(1, 1, null);
    assertFalse(testModel1.isShown());
    assertFalse(testModel2.isShown());

    checkDifferState();
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

    checkDifferState();
  }

  @Test
  public void testThrowIfChangeModelIdAfterNotify() {
    TestModel testModel = new TestModel();
    testModel.id(100);

    testAdapter.addModel(testModel);

    thrown.expect(IllegalEpoxyUsage.class);
    thrown.expectMessage("Cannot change a model's id after it has been added to the adapter");
    testModel.id(200);
  }

  @Test
  public void testAllowSetSameModelIdAfterNotify() {
    TestModel testModel = new TestModel();
    testModel.id(100);

    testAdapter.addModel(testModel);
    testModel.id(100);
  }

  @Test
  public void testThrowIfChangeModelIdAfterDiff() {
    TestModel testModel = new TestModel();
    testModel.id(100);

    testAdapter.models.add(testModel);
    testAdapter.notifyModelsChanged();

    thrown.expect(IllegalEpoxyUsage.class);
    thrown.expectMessage("Cannot change a model's id after it has been added to the adapter");
    testModel.id(200);
  }

  /** Make sure that the differ is in a correct state, and then running it produces no changes. */
  private void checkDifferState() {
    differObserver.operationCount = 0;

    testAdapter.registerAdapterDataObserver(differObserver);
    testAdapter.notifyModelsChanged();
    testAdapter.unregisterAdapterDataObserver(differObserver);

    Assert.assertEquals("Should not have any operations", 0, differObserver.operationCount);
  }
}
