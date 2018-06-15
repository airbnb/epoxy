package com.airbnb.epoxy;

import android.content.Context;
import android.widget.FrameLayout;

@ModelView(defaultLayout = 1)
public class BaseModelView extends FrameLayout {

  public BaseModelView(Context context) {
    super(context);
  }

  @ModelProp
  public void setTitle(String title) {

  }
}