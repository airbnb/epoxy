package com.airbnb.epoxy;

import android.content.Context;
import android.support.annotation.Dimension;
import android.support.annotation.IntRange;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.View;

import com.airbnb.epoxy.ModelProp.Option;

import java.util.List;

@ModelView(defaultLayout = 1)
public class TestManyTypesView extends View {
  public static final int TO_RANGE = 200;

  public TestManyTypesView(Context context) {
    super(context);
  }

  // Also testing package private setters
  @ModelProp
  void setStringValue(String value) {

  }

  @ModelProp
  void setNullableStringValue(@Nullable String value) {

  }

  @ModelProp
  void setIntValue(int value) {

  }

  @ModelProp
  void setIntValueWithAnnotation(@StringRes int value) {

  }

  @ModelProp
  void setIntValueWithRangeAnnotation(@IntRange(from = 0, to = TO_RANGE) int value) {

  }

  @ModelProp
  void setIntValueWithDimenTypeAnnotation(@Dimension(unit = Dimension.DP) int value) {

  }

  @ModelProp
  void setIntWithMultipleAnnotations(@IntRange(from = 0, to = TO_RANGE) @Dimension(unit = Dimension.DP) int value) {

  }

  @ModelProp
  void setIntegerValue(Integer value) {

  }

  @ModelProp
  void setBoolValue(boolean value) {

  }

  @ModelProp
  public void setBooleanValue(Boolean value) {

  }

  @ModelProp
  public void setArrayValue(String[] value) {

  }

  @ModelProp
  public void setListValue(List<String> value) {

  }

  @ModelProp(options = Option.DoNotHash)
  public void setClickListener(View.OnClickListener value) {

  }

  @ModelProp(options = ModelProp.Option.GenerateStringOverloads)
  public void setTitle(@Nullable CharSequence title) {

  }
}