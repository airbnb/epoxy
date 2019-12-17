package com.airbnb.epoxy;

import android.content.Context;
import android.widget.FrameLayout;
import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.annotation.RawRes;

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
  public void setBoxedBooleanValue(Boolean value) {

  }

  @ModelProp
  public void setDoubleValue(double value) {

  }

  @ModelProp
  public void setBoxedDoubleValue(Double value) {

  }

  @ModelProp
  public void setDrawableRes(@DrawableRes int value) {

  }

  @ModelProp
  public void setEpoxyModelList(List<? extends EpoxyModel<?>> value) {

  }

  @ModelProp
  public void setIntValue(int value) {

  }

  @ModelProp
  public void setBoxedIntValue(Integer value) {

  }

  @ModelProp
  public void setLongValue(long value) {

  }

  @ModelProp
  public void setBoxedLongValue(Long value) {

  }

  @CallbackProp
  public void setOnClickListener(@Nullable OnClickListener value) {

  }

  @ModelProp
  public void setRawRes(@RawRes int value) {

  }

  @ModelProp
  public void setStringValue(CharSequence value) {

  }

  @ModelProp
  public void setStringList(List<String> value) {

  }
}
