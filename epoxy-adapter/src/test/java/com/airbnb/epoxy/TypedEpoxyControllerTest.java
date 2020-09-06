package com.airbnb.epoxy;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.LooperMode;
import org.robolectric.annotation.LooperMode.Mode;

import static junit.framework.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
@LooperMode(Mode.LEGACY)
public class TypedEpoxyControllerTest {

  static class TestTypedController extends TypedEpoxyController<String> {
    int numTimesBuiltModels = 0;

    @Override
    protected void buildModels(String data) {
      assertEquals("data", data);
      numTimesBuiltModels++;
    }
  }

  @Test
  public void setData() {
    TestTypedController controller = new TestTypedController();

    controller.setData("data");
    controller.setData("data");

    controller.cancelPendingModelBuild();

    controller.setData("data");
    controller.setData("data");

    assertEquals(4, controller.numTimesBuiltModels);
  }
}