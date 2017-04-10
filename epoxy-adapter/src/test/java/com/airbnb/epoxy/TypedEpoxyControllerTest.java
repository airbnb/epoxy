package com.airbnb.epoxy;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@Config(sdk = 21, manifest = TestRunner.MANIFEST_PATH)
@RunWith(TestRunner.class)
public class TypedEpoxyControllerTest {

  @Test
  public void setData() {
    TypedEpoxyController<String> controller = spy(new TypedEpoxyController<String>() {
      @Override
      protected void buildModels(String data) {

      }
    });

    controller.setData("data");

    verify(controller).buildModels("data");
  }
}