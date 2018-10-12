package com.airbnb.epoxy;

import android.content.Context;
import android.view.View;

@ModelView(defaultLayout = 1)
public class OnVisibilityChangedView_throwsIfPrivate extends View {

  public OnVisibilityChangedView_throwsIfPrivate(Context context) {
    super(context);
  }

  @ModelProp
  public void setTitle(CharSequence title) {

  }

  @OnVisibilityChanged
  private void onVisibilityChanged(float ph, float pw, int vh, int vw) {

  }
}