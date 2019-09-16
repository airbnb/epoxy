package com.airbnb.epoxy;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver;

import static com.airbnb.epoxy.DiffPayload.getModelFromPayload;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@RunWith(RobolectricTestRunner.class)
public class DiffPayloadTest {

  private final List<EpoxyModel<?>> models = new ArrayList<>();
  private BaseEpoxyAdapter adapter;
  private AdapterDataObserver observer;

  @Before
  public void before() {
    adapter = new BaseEpoxyAdapter() {

      @Override
      List<EpoxyModel<?>> getCurrentModels() {
        return models;
      }
    };

    observer = spy(new AdapterDataObserver() {
      @Override
      public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {

      }
    });

    adapter.registerAdapterDataObserver(observer);
  }

  @Test
  public void payloadsDisabled() {
    DiffHelper diffHelper = new DiffHelper(adapter, false);

    TestModel firstModel = new TestModel();
    models.add(firstModel);
    diffHelper.notifyModelChanges();
    verify(observer).onItemRangeInserted(0, 1);

    TestModel updatedFirstModel = firstModel.clone().incrementValue();
    models.clear();
    models.add(updatedFirstModel);
    diffHelper.notifyModelChanges();
    verify(observer).onItemRangeChanged(0, 1, null);

    verifyNoMoreInteractions(observer);
  }

  @Test
  public void noPayloadsForNoChanges() {
    DiffHelper diffHelper = new DiffHelper(adapter, true);

    TestModel firstModel = new TestModel();
    models.add(firstModel);
    diffHelper.notifyModelChanges();
    verify(observer).onItemRangeInserted(0, 1);

    models.clear();
    diffHelper.notifyModelChanges();
    verify(observer).onItemRangeRemoved(0, 1);

    verifyNoMoreInteractions(observer);
  }

  @Test
  public void singlePayload() {
    DiffHelper diffHelper = new DiffHelper(adapter, true);

    TestModel firstModel = new TestModel();
    models.add(firstModel);
    diffHelper.notifyModelChanges();
    verify(observer).onItemRangeInserted(0, 1);

    models.clear();
    TestModel changedFirstModel = firstModel.clone().incrementValue();

    this.models.add(changedFirstModel);
    diffHelper.notifyModelChanges();
    verify(observer).onItemRangeChanged(eq(0), eq(1), argThat(new DiffPayloadMatcher(firstModel)));

    verifyNoMoreInteractions(observer);
  }

  @Test
  public void batchPayload() {
    DiffHelper diffHelper = new DiffHelper(adapter, true);

    TestModel firstModel = new TestModel();
    TestModel secondModel = new TestModel();
    models.add(firstModel);
    models.add(secondModel);

    diffHelper.notifyModelChanges();

    TestModel changedFirstModel = firstModel.clone().incrementValue();
    TestModel changedSecondModel = secondModel.clone().incrementValue();
    models.clear();
    models.add(changedFirstModel);
    models.add(changedSecondModel);

    diffHelper.notifyModelChanges();
    verify(observer)
        .onItemRangeChanged(eq(0), eq(2), argThat(new DiffPayloadMatcher(firstModel, secondModel)));
  }

  @Test
  public void multiplePayloads() {
    DiffHelper diffHelper = new DiffHelper(adapter, true);

    TestModel firstModel = new TestModel();
    TestModel secondModel = new TestModel();
    TestModel thirdModel = new TestModel();
    models.add(firstModel);
    models.add(thirdModel);

    diffHelper.notifyModelChanges();

    TestModel changedFirstModel = firstModel.clone().incrementValue();
    TestModel changedThirdModel = thirdModel.clone().incrementValue();
    models.clear();
    models.add(changedFirstModel);
    models.add(secondModel);
    models.add(changedThirdModel);

    diffHelper.notifyModelChanges();
    verify(observer).onItemRangeChanged(eq(0), eq(1), argThat(new DiffPayloadMatcher(firstModel)));
    verify(observer).onItemRangeChanged(eq(2), eq(1), argThat(new DiffPayloadMatcher(thirdModel)));
  }

  @Test
  public void getSingleModelFromPayload() {
    TestModel model = new TestModel();
    List<Object> payloads = payloadsWithChangedModels(model);
    EpoxyModel<?> modelFromPayload = getModelFromPayload(payloads, model.id());

    assertEquals(model, modelFromPayload);
  }

  @Test
  public void returnsNullWhenNoModelFoundInPayload() {
    TestModel model = new TestModel();
    List<Object> payloads = payloadsWithChangedModels(model);
    EpoxyModel<?> modelFromPayload = getModelFromPayload(payloads, model.id() - 1);

    assertNull(modelFromPayload);
  }

  @Test
  public void returnsNullForEmptyPayload() {
    List<Object> payloads = new ArrayList<>();
    EpoxyModel<?> modelFromPayload = getModelFromPayload(payloads, 2);

    assertNull(modelFromPayload);
  }

  @Test
  public void getMultipleModelsFromPayload() {
    TestModel model1 = new TestModel();
    TestModel model2 = new TestModel();
    List<Object> payloads = payloadsWithChangedModels(model1, model2);

    EpoxyModel<?> modelFromPayload1 = getModelFromPayload(payloads, model1.id());
    EpoxyModel<?> modelFromPayload2 = getModelFromPayload(payloads, model2.id());

    assertEquals(model1, modelFromPayload1);
    assertEquals(model2, modelFromPayload2);
  }

  @Test
  public void getSingleModelsFromMultipleDiffPayloads() {
    TestModel model1 = new TestModel();
    DiffPayload diffPayload1 = diffPayloadWithModels(model1);

    TestModel model2 = new TestModel();
    DiffPayload diffPayload2 = diffPayloadWithModels(model2);

    List<Object> payloads = payloadsWithDiffPayloads(diffPayload1, diffPayload2);

    EpoxyModel<?> modelFromPayload1 = getModelFromPayload(payloads, model1.id());
    EpoxyModel<?> modelFromPayload2 = getModelFromPayload(payloads, model2.id());

    assertEquals(model1, modelFromPayload1);
    assertEquals(model2, modelFromPayload2);
  }

  @Test
  public void getMultipleModelsFromMultipleDiffPayloads() {
    TestModel model1Payload1 = new TestModel(1);
    TestModel model2Payload1 = new TestModel(2);
    DiffPayload diffPayload1 = diffPayloadWithModels(model1Payload1, model2Payload1);

    TestModel model1Payload2 = new TestModel(3);
    TestModel model2Payload2 = new TestModel(4);
    DiffPayload diffPayload2 = diffPayloadWithModels(model1Payload2, model2Payload2);

    List<Object> payloads = payloadsWithDiffPayloads(diffPayload1, diffPayload2);

    EpoxyModel<?> model1FromPayload1 = getModelFromPayload(payloads, model1Payload1.id());
    EpoxyModel<?> model2FromPayload1 = getModelFromPayload(payloads, model2Payload1.id());
    EpoxyModel<?> model1FromPayload2 = getModelFromPayload(payloads, model1Payload2.id());
    EpoxyModel<?> model2FromPayload2 = getModelFromPayload(payloads, model2Payload2.id());

    assertEquals(model1Payload1, model1FromPayload1);
    assertEquals(model2Payload1, model2FromPayload1);
    assertEquals(model1Payload2, model1FromPayload2);
    assertEquals(model2Payload2, model2FromPayload2);
  }

  static class DiffPayloadMatcher implements ArgumentMatcher<DiffPayload> {

    private final DiffPayload expectedPayload;

    DiffPayloadMatcher(EpoxyModel<?>... changedModels) {
      List<EpoxyModel<?>> epoxyModels = Arrays.asList(changedModels);
      expectedPayload = new DiffPayload(epoxyModels);
    }

    @Override
    public boolean matches(DiffPayload argument) {
      return expectedPayload.equalsForTesting(argument);
    }
  }

  static DiffPayload diffPayloadWithModels(EpoxyModel<?>... models) {
    List<EpoxyModel<?>> epoxyModels = Arrays.asList(models);
    return new DiffPayload(epoxyModels);
  }

  static List<Object> payloadsWithDiffPayloads(DiffPayload... diffPayloads) {
    List<DiffPayload> payloads = Arrays.asList(diffPayloads);
    return new ArrayList<Object>(payloads);
  }

  static List<Object> payloadsWithChangedModels(EpoxyModel<?>... models) {
    return payloadsWithDiffPayloads(diffPayloadWithModels(models));
  }
}
