package com.airbnb.epoxy;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static junit.framework.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class ViewAnnotationsStringOverloadsIntegrationTest {

  private ControllerLifecycleHelper lifecycleHelper;

  ViewWithAnnotationsForIntegrationTest bind(ViewWithAnnotationsForIntegrationTestModel_ model) {
    lifecycleHelper = new ControllerLifecycleHelper();
    model.id(1);
    return (ViewWithAnnotationsForIntegrationTest) lifecycleHelper.bindModel(model);
  }

  @Test
  public void normalCharSequenceIsSet() {
    String text = "hello world";

    ViewWithAnnotationsForIntegrationTestModel_ model =
        new ViewWithAnnotationsForIntegrationTestModel_().text(text);

    ViewWithAnnotationsForIntegrationTest view = bind(model);


    assertEquals(text, view.text);
  }

  @Test
  public void stringResIsSet() {

  }

  @Test
  public void stringResWithArgsIsSet() {

  }

  @Test
  public void quantityStringIsSet() {

  }

  @Test
  public void quantityStringWithArgsIsSet() {

  }
}
