package com.airbnb.epoxy;

import android.content.Context;
import android.view.View;

@ModelView(autoLayout = ModelView.Size.WRAP_WIDTH_WRAP_HEIGHT)
public class AutoLayoutModelView extends View {

  public AutoLayoutModelView(Context context) {
    super(context);
  }

  @ModelProp
  void setValue(int value) {

  }
}