package com.airbnb.epoxy;

import android.content.Context;
import android.widget.FrameLayout;

import com.airbnb.epoxy.ModelProp.Option;

@ModelView(defaultLayout = 1)
public class GroupPropMultipleSupportedAttributeSameNameModelView extends FrameLayout {

  public GroupPropMultipleSupportedAttributeSameNameModelView(Context context) {
    super(context);
  }

  @ModelProp(group = "title")
  public void setTitleString(String title) {

  }

  @ModelProp(group = "title")
  public void setTitleInt(int title) {

  }
}