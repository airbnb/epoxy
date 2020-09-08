package com.airbnb.epoxy;

import android.content.res.Resources;

import com.airbnb.epoxy.integrationtest.R;
import com.airbnb.epoxy.integrationtest.ViewWithAnnotationsForIntegrationTest;
import com.airbnb.epoxy.integrationtest.ViewWithAnnotationsForIntegrationTestModel_;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.LooperMode;

import androidx.test.core.app.ApplicationProvider;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

@RunWith(RobolectricTestRunner.class)
@LooperMode(LooperMode.Mode.LEGACY)
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
  public void getStringOffModel() {
    String text = "hello world!";

    ViewWithAnnotationsForIntegrationTestModel_ model =
        new ViewWithAnnotationsForIntegrationTestModel_().requiredText(text);

    ViewWithAnnotationsForIntegrationTest view = bind(model);

    assertEquals(model.getRequiredText(view.getContext()), view.requiredText);
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

  @Test(expected = IllegalArgumentException.class)
  public void requiredTextThrowsOnBadStringRes() {
    new ViewWithAnnotationsForIntegrationTestModel_()
        .requiredText(0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void requiredTextThrowsOnBadStringResWithArgs() {
    new ViewWithAnnotationsForIntegrationTestModel_()
        .requiredText(0, "args");
  }

  @Test(expected = IllegalArgumentException.class)
  public void requiredTextThrowsOnBadQuantityString() {
    new ViewWithAnnotationsForIntegrationTestModel_()
        .requiredTextQuantityRes(0, 23, "args");
  }

  @Test
  public void nullableTextSetsNullWhenNotSet() {
    ViewWithAnnotationsForIntegrationTestModel_ model =
        new ViewWithAnnotationsForIntegrationTestModel_().requiredText("required");

    ViewWithAnnotationsForIntegrationTest view = bind(model);

    assertNull(view.nullableText);
  }

  @Test
  public void nullableTextAllowsNull() {
    ViewWithAnnotationsForIntegrationTestModel_ model =
        new ViewWithAnnotationsForIntegrationTestModel_()
            .requiredText("required")
            .nullableText(null);

    ViewWithAnnotationsForIntegrationTest view = bind(model);

    assertNull(view.nullableText);
  }

  @Test
  public void nullableTextAllowsZeroStringRes() {
    ViewWithAnnotationsForIntegrationTestModel_ model =
        new ViewWithAnnotationsForIntegrationTestModel_()
            .requiredText("required")
            .nullableText(0);

    ViewWithAnnotationsForIntegrationTest view = bind(model);

    assertNull(view.nullableText);
  }

  @Test
  public void nullableTextAllowsZeroQuantityRes() {
    ViewWithAnnotationsForIntegrationTestModel_ model =
        new ViewWithAnnotationsForIntegrationTestModel_()
            .requiredText("required")
            .nullableTextQuantityRes(0, 1);

    ViewWithAnnotationsForIntegrationTest view = bind(model);

    assertNull(view.nullableText);
  }

  @Test
  public void defaultStringValueSetIfNothingElseIsSet() {
    ViewWithAnnotationsForIntegrationTestModel_ model =
        new ViewWithAnnotationsForIntegrationTestModel_()
            .requiredText("required");

    ViewWithAnnotationsForIntegrationTest view = bind(model);

    assertEquals(ViewWithAnnotationsForIntegrationTest.DEFAULT_STRING, view.textWithDefault);
    assertEquals(ViewWithAnnotationsForIntegrationTest.DEFAULT_STRING,
        view.nullableTextWithDefault);
  }

  @Test
  public void stringOverridesDefault() {
    String text = "hello world";

    ViewWithAnnotationsForIntegrationTestModel_ model =
        new ViewWithAnnotationsForIntegrationTestModel_()
            .requiredText("required")
            .textWithDefault(text)
            .nullableTextWithDefault(text);

    ViewWithAnnotationsForIntegrationTest view = bind(model);

    assertEquals(text, view.nullableTextWithDefault);
    assertEquals(text, view.textWithDefault);
  }

  @Test
  public void nullableStringOverridesDefaultWithNull() {
    ViewWithAnnotationsForIntegrationTestModel_ model =
        new ViewWithAnnotationsForIntegrationTestModel_()
            .requiredText("required")
            .nullableTextWithDefault(null);

    ViewWithAnnotationsForIntegrationTest view = bind(model);

    assertEquals(null, view.nullableTextWithDefault);
  }

  @Test
  public void zeroStringResSetsDefault() {
    ViewWithAnnotationsForIntegrationTestModel_ model =
        new ViewWithAnnotationsForIntegrationTestModel_()
            .requiredText("required")
            .textWithDefault(0)
            .nullableTextWithDefault(0);

    ViewWithAnnotationsForIntegrationTest view = bind(model);

    assertEquals(ViewWithAnnotationsForIntegrationTest.DEFAULT_STRING, view.textWithDefault);
    assertEquals(ViewWithAnnotationsForIntegrationTest.DEFAULT_STRING,
        view.nullableTextWithDefault);
  }

  @Test
  public void zeroQuantityStringResSetsDefault() {
    ViewWithAnnotationsForIntegrationTestModel_ model =
        new ViewWithAnnotationsForIntegrationTestModel_()
            .requiredText("required")
            .textWithDefaultQuantityRes(0, 1)
            .nullableTextWithDefaultQuantityRes(0, 1);

    ViewWithAnnotationsForIntegrationTest view = bind(model);

    assertEquals(ViewWithAnnotationsForIntegrationTest.DEFAULT_STRING, view.textWithDefault);
    assertEquals(ViewWithAnnotationsForIntegrationTest.DEFAULT_STRING,
        view.nullableTextWithDefault);
  }

  @Test
  public void stringOverloadsResetEachOther() {
    Resources r = ApplicationProvider.getApplicationContext().getResources();

    ViewWithAnnotationsForIntegrationTest view =
        bind(new ViewWithAnnotationsForIntegrationTestModel_()
            .requiredText("required")
            .nullableText(R.string.string_with_no_args)
            .nullableText("test"));

    assertEquals("test", view.nullableText);

    view = bind(new ViewWithAnnotationsForIntegrationTestModel_()
        .requiredText("required")
        .nullableText("test")
        .nullableText(R.string.string_with_no_args));

    assertEquals(r.getString(R.string.string_with_no_args), view.nullableText);

    view = bind(new ViewWithAnnotationsForIntegrationTestModel_()
        .requiredText("required")
        .nullableText(R.string.string_with_no_args)
        .nullableTextQuantityRes(R.plurals.plural_test_string, 1));

    assertEquals(r.getQuantityString(R.plurals.plural_test_string, 1), view.nullableText);

    view = bind(new ViewWithAnnotationsForIntegrationTestModel_()
        .requiredText("required")
        .nullableText(R.string.string_with_no_args)
        .nullableTextQuantityRes(0, 1));

    assertNull(view.nullableText);

    view = bind(new ViewWithAnnotationsForIntegrationTestModel_()
        .requiredText("required")
        .nullableTextQuantityRes(R.plurals.plural_test_string, 1)
        .nullableText(R.string.string_with_args, 2));

    assertEquals(r.getString(R.string.string_with_args, 2), view.nullableText);

    view = bind(new ViewWithAnnotationsForIntegrationTestModel_()
        .requiredText("required")
        .nullableText(0)
        .nullableText(R.string.string_with_args, 2));

    assertEquals(r.getString(R.string.string_with_args, 2), view.nullableText);

    view = bind(new ViewWithAnnotationsForIntegrationTestModel_()
        .requiredText("required")
        .nullableText(0)
        .nullableText(R.string.string_with_args, 2));

    assertEquals(r.getString(R.string.string_with_args, 2), view.nullableText);

    view = bind(new ViewWithAnnotationsForIntegrationTestModel_()
        .requiredText("required")
        .nullableText(R.string.string_with_args, 2)
        .nullableText(0));

    assertNull(view.nullableText);
  }
}
