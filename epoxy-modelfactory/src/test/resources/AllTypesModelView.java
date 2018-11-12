package com.airbnb.epoxy;

import android.content.Context;
import android.widget.FrameLayout;
import androidx.annotation.DrawableRes;
import androidx.annotation.RawRes;

import com.airbnb.epoxy.ModelProp.Option;

import java.util.List;

@ModelView(defaultLayout = 1)
public class AllTypesModelView extends FrameLayout {

  public AllTypesModelView(Context context) {
    super(context);
  }

  @ModelProp
  public void setBooleanValue(boolean value) {

  }

  @ModelProp
  public void setBoxedBooleanValue(Boolean value) {

  }

  @ModelProp
  public void setCharSequenceValue(CharSequence value) {

  }

  @ModelProp
  public void setBoxedDoubleValue(Double value) {

  }

  @ModelProp
  public void setDoubleValue(double value) {

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

  @ModelProp({ Option.DoNotHash })
  public void setOnClickListener(OnClickListener value) {

  }

  @ModelProp
  public void setRawRes(@RawRes int value) {

  }

  @ModelProp
  public void setStringValue(String value) {

  }

  @ModelProp
  public void setStringList(List<String> value) {

  }
}
