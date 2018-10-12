package com.airbnb.epoxy;

import android.content.Context;
import android.view.View;

@ModelView(defaultLayout = 1)
public class OnVisibilityChangedView extends View {

  public OnVisibilityChangedView(Context context) {
    super(context);
  }

  @ModelProp
  public void setTitle(CharSequence title) {

  }

  @OnVisibilityChanged
  void onVisibilityChanged1(float ph, float pw, int vh, int vw) {
    // also testing package private works
  }

  @OnVisibilityChanged
  public void onVisibilityChanged2(float ph, float pw, int vh, int vw) {

  }
}