package com.airbnb.epoxy;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.widget.FrameLayout;

import com.airbnb.epoxy.ModelView.Size;

import java.util.List;

@ModelView(autoLayout = Size.MATCH_WIDTH_WRAP_HEIGHT)
public class TestModelPropertiesView extends FrameLayout {

  public TestModelPropertiesView(Context context) {
    super(context);
  }

  @ModelProp
  public void setBooleanValue(boolean value) {

  }

  @ModelProp
  public void setDoubleValue(double value) {

  }

  @ModelProp
  public void setDrawableRes(@DrawableRes int value) {

  }

  @ModelProp
  public void setIntValue(int value) {

  }

  @CallbackProp
  public void setOnClickListener(@Nullable OnClickListener value) {

  }

  @ModelProp
  public void setStringValue(CharSequence value) {

  }

  @ModelProp
  public void setStringList(List<String> value) {

  }
}