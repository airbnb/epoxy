package com.airbnb.epoxy;

import android.content.Context;
import android.view.View;

@ModelView(defaultLayout = 1)
public class Prop_throwsIfStatic extends View {

  public Prop_throwsIfStatic(Context context) {
    super(context);
  }

  @ModelProp
  public static void setTitle(CharSequence title) {

  }
}