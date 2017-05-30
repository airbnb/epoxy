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
    int stringWithNoArgs = R.string.string_with_no_args;

    ViewWithAnnotationsForIntegrationTestModel_ model =
        new ViewWithAnnotationsForIntegrationTestModel_()
            .requiredText(stringWithNoArgs);

    ViewWithAnnotationsForIntegrationTest view = bind(model);

    assertEquals(view.getContext().getText(stringWithNoArgs), view.requiredText);
  }

  @Test
  public void stringResWithArgsIsSet() {
    int stringWithNoArgs = R.string.string_with_args;

    ViewWithAnnotationsForIntegrationTestModel_ model =
        new ViewWithAnnotationsForIntegrationTestModel_()
            .requiredText(stringWithNoArgs, 3);

    ViewWithAnnotationsForIntegrationTest view = bind(model);

    assertEquals(view.getContext().getString(stringWithNoArgs, 3), view.requiredText);
  }

  @Test
  public void quantityStringIsSet() {
    int pluralString = R.plurals.plural_test_string;

    ViewWithAnnotationsForIntegrationTestModel_ model =
        new ViewWithAnnotationsForIntegrationTestModel_()
            .requiredTextQuantityRes(pluralString, 1);

    ViewWithAnnotationsForIntegrationTest view = bind(model);

    assertEquals(view.getContext().getResources().getQuantityString(pluralString, 1),
        view.requiredText);
  }

  @Test
  public void quantityStringWithArgsIsSet() {
    int pluralString = R.plurals.plural_test_string_with_args;

    ViewWithAnnotationsForIntegrationTestModel_ model =
        new ViewWithAnnotationsForIntegrationTestModel_()
            .requiredTextQuantityRes(pluralString, 1, 3);

    ViewWithAnnotationsForIntegrationTest view = bind(model);

    assertEquals(view.getContext().getResources().getQuantityString(pluralString, 1, 3),
        view.requiredText);
  }

  @Test(expected = IllegalArgumentException.class)
  public void requiredTextThrowsWhenSetWithNull() {
    new ViewWithAnnotationsForIntegrationTestModel_()
        .requiredText(null);
  }

  @Test(expected = IllegalStateException.class)
  public void requiredTextThrowsWhenNotSet() {

    ViewWithAnnotationsForIntegrationTestModel_ model =
        new ViewWithAnnotationsForIntegrationTestModel_();

    bind(model);
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
