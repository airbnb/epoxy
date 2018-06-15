package com.airbnb.epoxy;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.FrameLayout;

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
  public void setDoubleValue(double value) {

  }

  @ModelProp({ Option.DoNotHash })
  public void setDrawable(Drawable value) {

  }

  @ModelProp
  public void setIntValue(int value) {

  }

  @ModelProp({ Option.DoNotHash })
  public void setOnClickListener(OnClickListener value) {

  }

  @ModelProp
  public void setStringValue(String value) {

  }

  @ModelProp
  public void setStringList(List<String> value) {

  }
}