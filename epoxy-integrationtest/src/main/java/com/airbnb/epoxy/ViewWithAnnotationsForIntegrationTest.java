package com.airbnb.epoxy;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.airbnb.epoxy.ModelProp.Option;

@ModelView(defaultLayout = R.layout.view_with_annotations_for_integration_test)
public class ViewWithAnnotationsForIntegrationTest extends View {
  public static final String DEFAULT_STRING = "hello world";

  public CharSequence requiredText;
  public CharSequence nullableText;
  public CharSequence textWithDefault;
  public CharSequence nullableTextWithDefault;

  public ViewWithAnnotationsForIntegrationTest(Context context) {
    super(context);
  }

  public ViewWithAnnotationsForIntegrationTest(Context context,
      @Nullable AttributeSet attrs) {
    super(context, attrs);
  }

  public ViewWithAnnotationsForIntegrationTest(Context context,
      @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @ModelProp(options = Option.GenerateStringOverloads)
  public void setRequiredText(CharSequence text) {
    this.requiredText = text;
  }

  @ModelProp(options = Option.GenerateStringOverloads)
  public void setNullableText(@Nullable CharSequence text) {
    nullableText = text;
  }

  @ModelProp(options = Option.GenerateStringOverloads, defaultValue = "DEFAULT_STRING")
  public void setTextWithDefault(CharSequence text) {
    textWithDefault = text;
  }

  @ModelProp(options = Option.GenerateStringOverloads, defaultValue = "DEFAULT_STRING")
  public void setNullableTextWithDefault(@Nullable CharSequence text) {
    nullableTextWithDefault = text;
  }
}
