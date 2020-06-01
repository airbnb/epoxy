package com.airbnb.epoxy;

import android.content.Context;
import android.widget.FrameLayout;

@ModelView(defaultLayout = 1)
public class ModelFactoryBaseModelView extends FrameLayout {

  public ModelFactoryBaseModelView(Context context) {
    super(context);
  }

  @ModelProp
  public void setTitle(String title) {

  }
}