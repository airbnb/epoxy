package com.airbnb.epoxy;

import android.content.Context;
import android.widget.FrameLayout;

@ModelView(defaultLayout = 1, baseModelClass = TestBaseModel.class)
public class BaseModelView extends FrameLayout {

  public BaseModelView(Context context) {
    super(context);
  }

  @ModelProp
  public void setClickListener(String title) {

  }
}