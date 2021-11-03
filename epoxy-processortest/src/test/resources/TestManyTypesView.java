package com.airbnb.epoxy;

import android.content.Context;
import androidx.annotation.Dimension;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import kotlin.jvm.functions.Function3;

import android.view.View;

import com.airbnb.epoxy.ModelProp.Option;
import com.airbnb.epoxy.EpoxyModel;

import java.util.List;
import java.util.Map;

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

  @ModelProp(Option.DoNotHash)
  void setFunction(Function3<Integer, Integer, Integer, Integer> funct) {

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
  void setModels(List<? extends EpoxyModel<?>> models) {

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

  @ModelProp
  public void setMapValue(Map<Integer, ?> value) {

  }

  @ModelProp(options = Option.DoNotHash)
  public void setClickListener(View.OnClickListener value) {

  }

  @ModelProp(options = ModelProp.Option.GenerateStringOverloads)
  public void setTitle(@Nullable CharSequence title) {

  }
}
