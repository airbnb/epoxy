package com.airbnb.epoxy;

import com.airbnb.epoxy.integrationtest.BuildConfig;
import com.airbnb.epoxy.integrationtest.Model;
import com.airbnb.epoxy.integrationtest.Model_;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
public class EpoxyAdapterIntegrationTest {

  private ControllerLifecycleHelper lifecycleHelper = new ControllerLifecycleHelper();
  private final SimpleEpoxyAdapter adapter = new SimpleEpoxyAdapter();

  @Test
  public void returnModelViewType() {
    Model testModel = new Model_();
    adapter.addModel(testModel);
    int itemViewType = adapter.getItemViewType(0);

    assertEquals(testModel.getViewType(), itemViewType);
  }

  @Test
  public void returnHiddenModelViewType() {
    Model testModel = new Model_();
    testModel.hide();
    adapter.addModel(testModel);
    int itemViewType = adapter.getItemViewType(0);

    assertEquals(new HiddenEpoxyModel().getViewType(), itemViewType);
  }

  @Test
  public void bindHiddenModel() {
    Model testModel = new Model_();
    testModel.hide();
    adapter.addModel(testModel);

    lifecycleHelper.bindModels(adapter);
  }

  @Test
  public void bindNormalModel() {
    Model testModel = new Model_();
    adapter.addModel(testModel);

    lifecycleHelper.bindModels(adapter);
  }
}