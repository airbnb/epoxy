package com.airbnb.epoxy;

import android.content.Context;
import android.view.View;

@ModelView(defaultLayout = 1)
public class Prop_throwsIfNoParams extends View {

  public Prop_throwsIfNoParams(Context context) {
    super(context);
  }

  @ModelProp
  public void setTitle() {

  }
}