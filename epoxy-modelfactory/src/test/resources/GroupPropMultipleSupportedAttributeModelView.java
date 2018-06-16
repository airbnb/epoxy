package com.airbnb.epoxy;

import android.content.Context;
import android.widget.FrameLayout;

import com.airbnb.epoxy.ModelProp.Option;

@ModelView(defaultLayout = 1)
public class GroupPropMultipleSupportedAttributeModelView extends FrameLayout {

  public GroupPropMultipleSupportedAttributeModelView(Context context) {
    super(context);
  }

  @ModelProp
  public void setTitle(String title) {

  }

  @ModelProp
  public void setTitle(int title) {

  }
}