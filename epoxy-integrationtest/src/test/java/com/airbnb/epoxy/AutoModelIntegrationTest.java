package com.airbnb.epoxy;

import android.view.View;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import java.util.List;

import static org.junit.Assert.assertEquals;

@Config(sdk = 21, manifest = TestRunner.MANIFEST_PATH)
@RunWith(TestRunner.class)
public class AutoModelIntegrationTest {

  static class TestModel extends EpoxyModel<View> {

    @Override
    protected int getDefaultLayout() {
      return 0;
    }
  }

  static class BasicAutoModelsAdapter extends AutoEpoxyAdapter {

    @AutoModel TestModel model1;
    @AutoModel TestModel model2;

    @Override
    protected void buildModels() {
      add(model1);
      add(model2);
    }
  }

  @Test
  public void basicAutoModels() {
    BasicAutoModelsAdapter testAdapter = new BasicAutoModelsAdapter();
    testAdapter.requestModelUpdate();

    List<EpoxyModel<?>> models = testAdapter.getCurrentModels();

    assertEquals("Models size", 2, models.size());
    assertEquals("First model", TestModel.class, models.get(0).getClass());
    assertEquals("Second model", TestModel.class, models.get(1).getClass());
  }



  @Test
  public void assigningValueToFieldFails() {

  }
}