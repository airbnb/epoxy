package com.airbnb.epoxy;

import android.view.View;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class AutoModelIntegrationTest {

  static class TestModel extends EpoxyModel<View> {

    @Override
    protected int getDefaultLayout() {
      return 0;
    }
  }

  static class BasicAutoModelsAdapter extends EpoxyController {

    @AutoModel TestModel model1;
    @AutoModel TestModel model2;

    @Override
    protected void buildModels() {
      add(model1.id(1));
      add(model2.id(2));
    }
  }

  @Test
  public void basicAutoModels() {
    BasicAutoModelsAdapter controller = new BasicAutoModelsAdapter();
    controller.requestModelBuild();

    List<EpoxyModel<?>> models = controller.getCopyOfModels();

    assertEquals("Models size", 2, models.size());
    assertEquals("First model", TestModel.class, models.get(0).getClass());
    assertEquals("Second model", TestModel.class, models.get(1).getClass());
  }

  static class AdapterWithFieldAssigned extends EpoxyController {

    @AutoModel TestModel model1 = new TestModel();

    @Override
    protected void buildModels() {
      add(model1);
    }
  }

  @Test(expected = IllegalStateException.class)
  public void assigningValueToFieldFails() {
    AdapterWithFieldAssigned testAdapter = new AdapterWithFieldAssigned();
    testAdapter.requestModelBuild();
  }

  static class AdapterWithIdChanged extends EpoxyController {

    @AutoModel TestModel model1 = new TestModel();

    @Override
    protected void buildModels() {
      add(model1.id(23));
    }
  }

  @Test(expected = IllegalStateException.class)
  public void assigningIdToAutoModelFails() {
    AdapterWithIdChanged testAdapter = new AdapterWithIdChanged();
    testAdapter.requestModelBuild();
  }
}