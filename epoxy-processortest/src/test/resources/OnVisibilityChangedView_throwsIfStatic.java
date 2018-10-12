package com.airbnb.epoxy;

import android.content.Context;
import android.view.View;

@ModelView(defaultLayout = 1)
public class OnVisibilityChangedView_throwsIfStatic extends View {

  public OnVisibilityChangedView_throwsIfStatic(Context context) {
    super(context);
  }

  @ModelProp
  public void setTitle(CharSequence title) {

  }

  @OnVisibilityChanged
  static void onVisibilityChanged(float ph, float pw, int vh, int vw) {

  }
}