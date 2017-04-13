package com.airbnb.epoxy;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class AutoModelIntegrationTest {

  @Test
  public void basicAutoModels() {
    BasicAutoModelsAdapter controller = new BasicAutoModelsAdapter();
    controller.requestModelBuild();

    List<EpoxyModel<?>> models = controller.getAdapter().getCopyOfModels();

    assertEquals("Models size", 2, models.size());
    assertEquals("First model", Model_.class, models.get(0).getClass());
    assertEquals("Second model", Model_.class, models.get(1).getClass());
  }

  @Test(expected = IllegalStateException.class)
  public void assigningValueToFieldFails() {
    AdapterWithFieldAssigned testAdapter = new AdapterWithFieldAssigned();
    testAdapter.requestModelBuild();
  }

  @Test(expected = IllegalStateException.class)
  public void assigningIdToAutoModelFails() {
    AdapterWithIdChanged testAdapter = new AdapterWithIdChanged();
    testAdapter.requestModelBuild();
  }
}