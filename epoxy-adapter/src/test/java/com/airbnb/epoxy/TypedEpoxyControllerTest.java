package com.airbnb.epoxy;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import static junit.framework.Assert.assertEquals;

@Config(sdk = 21, manifest = TestRunner.MANIFEST_PATH)
@RunWith(TestRunner.class)
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