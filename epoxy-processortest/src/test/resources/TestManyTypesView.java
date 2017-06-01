package com.airbnb.epoxy;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.View;

import com.airbnb.epoxy.ModelProp.Option;

import java.util.List;

@ModelView(defaultLayout = 1)
public class TestManyTypesView extends View {

  public TestManyTypesView(Context context) {
    super(context);
  }

  @ModelProp
  public void setStringValue(String value) {

  }

  @ModelProp
  public void setIntValue(int value) {

  }

  @ModelProp
  public void setIntegerValue(Integer value) {

  }

  @ModelProp
  public void setBoolValue(boolean value) {

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