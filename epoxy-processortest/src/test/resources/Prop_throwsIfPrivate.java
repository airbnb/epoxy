package com.airbnb.epoxy;

import android.content.Context;
import android.view.View;

@ModelView(defaultLayout = 1)
public class Prop_throwsIfPrivate extends View {

  public Prop_throwsIfPrivate(Context context) {
    super(context);
  }

  @ModelProp
  private void setTitle(CharSequence title) {

  }
}