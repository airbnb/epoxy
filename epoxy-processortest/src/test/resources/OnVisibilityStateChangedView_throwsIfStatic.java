package com.airbnb.epoxy;

import android.content.Context;
import android.view.View;

@ModelView(defaultLayout = 1)
public class OnVisibilityStateChangedView_throwsIfStatic extends View {

  public OnVisibilityStateChangedView_throwsIfStatic(Context context) {
    super(context);
  }

  @ModelProp
  public void setTitle(CharSequence title) {

  }

  @OnVisibilityStateChanged
  static void onVisibilityStateChanged(int s) {

  }
}