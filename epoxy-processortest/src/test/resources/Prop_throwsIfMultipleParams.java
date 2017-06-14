package com.airbnb.epoxy;

import android.content.Context;
import android.view.View;

@ModelView(defaultLayout = 1)
public class Prop_throwsIfMultipleParams extends View {

  public Prop_throwsIfMultipleParams(Context context) {
    super(context);
  }

  @ModelProp
  public void setTitle(CharSequence title, int prop) {

  }
}