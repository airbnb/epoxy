package com.airbnb.epoxy;

import android.view.View;

import com.airbnb.epoxy.EpoxyModel.AddPredicate;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Config(sdk = 21, manifest = TestRunner.MANIFEST_PATH)
@RunWith(TestRunner.class)
public class AutoEpoxyAdapterTest {

  static class TestModel extends EpoxyModel<View> {

    @Override
    protected int getDefaultLayout() {
      return 0;
    }
  }

  static class TestModel2 extends EpoxyModel<View> {

    @Override
    protected int getDefaultLayout() {
      return 0;
    }
  }

  static class TestModel3 extends EpoxyModel<View> {

    @Override
    protected int getDefaultLayout() {
      return 0;
    }
  }

  static class BasicModelBuildingAdapter extends AutoEpoxyAdapter {

    @Override
    protected void buildModels() {
      add(new TestModel().id(0));
      new TestModel().id(1).addTo(this);

      new TestModel2().id(2).addIf(true, this);
      new TestModel3().id(3).addIf(false, this);

      new TestModel().id(4).addIf(new AddPredicate() {
        @Override
        public boolean addIf() {
          return false;
        }
      }, this);

      new TestModel().id(5).addIf(new AddPredicate() {
        @Override
        public boolean addIf() {
          return true;
        }
      }, this);
    }
  }

  @Test
  public void basicModelBuilding() {
    BasicModelBuildingAdapter testAdapter = new BasicModelBuildingAdapter();
    testAdapter.requestModelUpdate();

    List<EpoxyModel<?>> models = testAdapter.getCurrentModels();

    assertEquals("Models size", 4, models.size());
    assertEquals("First model", TestModel.class, models.get(0).getClass());
    assertEquals("Second model", TestModel.class, models.get(1).getClass());
    assertEquals("Third model", TestModel2.class, models.get(2).getClass());
    assertEquals("Fourth model", TestModel.class, models.get(3).getClass());
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

  static class AdapterThatSetsModelWithoutIdFails extends AutoEpoxyAdapter {

    @Override
    protected void buildModels() {
      add(new TestModel());
    }
  }

  @Test(expected = IllegalStateException.class)
  public void notSettingModelIdFails() {
    new AdapterThatSetsModelWithoutIdFails().requestModelUpdate();
  }

  static class AdapterThatHidesModelFails extends AutoEpoxyAdapter {

    @Override
    protected void buildModels() {
      add(new TestModel().id(1).hide());
    }
  }

  @Test(expected = IllegalStateException.class)
  public void hidingModelFails() {
    new AdapterThatHidesModelFails().requestModelUpdate();
  }
}