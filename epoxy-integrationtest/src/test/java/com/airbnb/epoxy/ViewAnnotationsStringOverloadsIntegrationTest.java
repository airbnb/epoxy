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
        new ViewWithAnnotationsForIntegrationTestModel_().requiredText(text);

    ViewWithAnnotationsForIntegrationTest view = bind(model);

    assertEquals(text, view.requiredText);
  }

  @Test
  public void stringResIsSet() {
    String text = "hello world";

    ViewWithAnnotationsForIntegrationTestModel_ model =
        new ViewWithAnnotationsForIntegrationTestModel_().requiredText(text);

    ViewWithAnnotationsForIntegrationTest view = bind(model);

    assertEquals(text, view.requiredText);
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

  @Test
  public void requiredTextThrowsOnNull() {

  }

  @Test
  public void requiredTextThrowsOnBadStringRes() {

  }

  @Test
  public void requiredTextThrowsOnBadStringResWithArgs() {

  }

  @Test
  public void requiredTextThrowsOnBadQuantityString() {

  }

  @Test
  public void nullableTextSetsNullWhenNotSet() {

  }

  @Test
  public void nullableTextAllowsNull() {

  }

  @Test
  public void nullableTextAllowsZeroStringRes() {

  }

  @Test
  public void defaultStringValueSetIfNothingElseIsSet() {

  }

  @Test
  public void nullableStringOverridesDefaultWithNull() {

  }

  @Test
  public void zeroStringResSetsDefault() {

  }

  @Test
  public void zeroQuantityStringResSetsDefault() {

  }

  @Test
  public void stringOverloadsResetEachOther() {

  }
}
