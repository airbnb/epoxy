package com.airbnb.epoxy;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@RunWith(RobolectricTestRunner.class)
public class ModelListTest {

  private final ModelList.ModelListObserver observer = mock(ModelList.ModelListObserver.class);
  private final ModelList modelList = new ModelList();

  @Before
  public void before() {
    modelList.add(new TestModel());
    modelList.add(new TestModel());
    modelList.add(new TestModel());

    modelList.setObserver(observer);
  }

  @Test
  public void testSet() {
    modelList.set(0, new TestModel());

    verify(observer).onItemRangeRemoved(0, 1);
    verify(observer).onItemRangeInserted(0, 1);
  }

  @Test
  public void testSetSameIdDoesntNotify() {
    EpoxyModel<?> newModelWithSameId = new TestModel();
    newModelWithSameId.id(modelList.get(0).id());

    modelList.set(0, newModelWithSameId);
    verifyNoMoreInteractions(observer);
    assertEquals(newModelWithSameId, modelList.get(0));
  }

  @Test
  public void testAdd() {
    modelList.add(new TestModel());
    modelList.add(new TestModel());

    verify(observer).onItemRangeInserted(3, 1);
    verify(observer).onItemRangeInserted(4, 1);
  }

  @Test
  public void testAddAtIndex() {
    modelList.add(0, new TestModel());
    modelList.add(2, new TestModel());

    verify(observer).onItemRangeInserted(0, 1);
    verify(observer).onItemRangeInserted(2, 1);
  }

  @Test
  public void testAddAll() {
    List<EpoxyModel<?>> newModels = new ArrayList<>();
    newModels.add(new TestModel());
    newModels.add(new TestModel());

    modelList.addAll(newModels);
    verify(observer).onItemRangeInserted(3, 2);
  }

  @Test
  public void testAddAllAtIndex() {
    List<EpoxyModel<?>> newModels = new ArrayList<>();
    newModels.add(new TestModel());
    newModels.add(new TestModel());

    modelList.addAll(0, newModels);
    verify(observer).onItemRangeInserted(0, 2);
  }

  @Test
  public void testRemoveIndex() {
    EpoxyModel<?> removedModel = modelList.remove(0);
    assertFalse(modelList.contains(removedModel));

    assertEquals(2, modelList.size());
    verify(observer).onItemRangeRemoved(0, 1);
  }

  @Test
  public void testRemoveObject() {
    EpoxyModel<?> model = modelList.get(0);
    boolean model1Removed = modelList.remove(model);

    assertEquals(2, modelList.size());
    assertTrue(model1Removed);
    assertFalse(modelList.contains(model));

    verify(observer).onItemRangeRemoved(0, 1);
  }

  @Test
  public void testRemoveObjectNotAdded() {
    boolean removed = modelList.remove(new TestModel());
    assertFalse(removed);
    verifyNoMoreInteractions(observer);
  }

  @Test
  public void testClear() {
    modelList.clear();
    verify(observer).onItemRangeRemoved(0, 3);
  }

  @Test
  public void testClearWhenAlreadyEmpty() {
    modelList.clear();
    modelList.clear();
    verify(observer).onItemRangeRemoved(0, 3);
    verifyNoMoreInteractions(observer);
  }

  @Test
  public void testSublistClear() {
    modelList.subList(0, 2).clear();
    verify(observer).onItemRangeRemoved(0, 2);
  }

  @Test
  public void testNoClearWhenEmpty() {
    modelList.clear();
    modelList.clear();
    verify(observer).onItemRangeRemoved(0, 3);
    verifyNoMoreInteractions(observer);
  }

  @Test
  public void testRemoveRange() {
    modelList.removeRange(0, 2);
    assertEquals(1, modelList.size());
    verify(observer).onItemRangeRemoved(0, 2);
  }

  @Test
  public void testRemoveEmptyRange() {
    modelList.removeRange(1, 1);
    verifyNoMoreInteractions(observer);
  }

  @Test
  public void testIteratorRemove() {
    Iterator<EpoxyModel<?>> iterator = modelList.iterator();
    iterator.next();
    iterator.remove();

    verify(observer).onItemRangeRemoved(0, 1);
  }

  @Test
  public void testRemoveAll() {
    List<EpoxyModel<?>> modelsToRemove = new ArrayList<>();
    modelsToRemove.add(modelList.get(0));
    modelsToRemove.add(modelList.get(1));

    modelList.removeAll(modelsToRemove);
    verify(observer, times(2)).onItemRangeRemoved(0, 1);
  }

  @Test
  public void testRetainAll() {
    List<EpoxyModel<?>> modelsToRetain = new ArrayList<>();
    modelsToRetain.add(modelList.get(0));

    modelList.retainAll(modelsToRetain);
    verify(observer, times(2)).onItemRangeRemoved(1, 1);
  }
}