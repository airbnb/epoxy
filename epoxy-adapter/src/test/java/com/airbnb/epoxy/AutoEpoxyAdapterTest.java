package com.airbnb.epoxy;

import android.view.View;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import java.util.List;

import static junit.framework.Assert.assertEquals;

@Config(sdk = 21, manifest = TestRunner.MANIFEST_PATH)
@RunWith(TestRunner.class)
public class AutoEpoxyAdapterTest {

  class TestModel extends EpoxyModel<View> {

    @Override
    protected int getDefaultLayout() {
      return 0;
    }
  }

  class TestModel2 extends EpoxyModel<View> {

    @Override
    protected int getDefaultLayout() {
      return 0;
    }
  }

  class TestModel3 extends EpoxyModel<View> {

    @Override
    protected int getDefaultLayout() {
      return 0;
    }
  }

  class BasicModelBuildingAdapter extends AutoEpoxyAdapter {

    @Override
    protected void buildModels() {
      add(new TestModel());
      new TestModel().addTo(this);

      new TestModel2().addIf(true, this);
      new TestModel3().addIf(false, this);
    }
  }

  @Test
  public void basicModelBuilding() {
    BasicModelBuildingAdapter testAdapter = new BasicModelBuildingAdapter();
    testAdapter.requestModelUpdate();

    List<EpoxyModel<?>> models = testAdapter.getCurrentModels();

    assertEquals("Models size", 3, models.size());
    assertEquals("First model", TestModel.class, models.get(0).getClass());
    assertEquals("Second model", TestModel.class, models.get(1).getClass());
    assertEquals("Third model", TestModel2.class, models.get(2).getClass());
  }

  @Test(expected = IllegalStateException.class)
  public void callingBuildModelsDirectlyFails() {
    BasicModelBuildingAdapter testAdapter = new BasicModelBuildingAdapter();
    testAdapter.buildModels();
  }

  @Test(expected = IllegalStateException.class)
  public void callingNotifyDirectlyFails() {
    BasicModelBuildingAdapter testAdapter = new BasicModelBuildingAdapter();
    testAdapter.notifyItemChanged(0);
  }

  @Test(expected = IllegalStateException.class)
  public void callingAddOutsideBuildModelsFails() {
    BasicModelBuildingAdapter testAdapter = new BasicModelBuildingAdapter();
    testAdapter.add(new TestModel());
  }

  @Test
  public void notSettingModelIdFails() {
    // TODO: (eli_hart 2/25/17)
  }
}