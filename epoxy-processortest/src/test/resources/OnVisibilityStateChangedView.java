package com.airbnb.epoxy;

import android.content.Context;
import android.view.View;

@ModelView(defaultLayout = 1)
public class OnVisibilityStateChangedView extends View {

  public OnVisibilityStateChangedView(Context context) {
    super(context);
  }

  @ModelProp
  public void setTitle(CharSequence title) {

  }

  @OnVisibilityStateChanged
  void onVisibilityStateChanged1(int s) {
    // also testing package private works
  }

  @OnVisibilityStateChanged
  public void onVisibilityStateChanged2(int s) {

  }
}