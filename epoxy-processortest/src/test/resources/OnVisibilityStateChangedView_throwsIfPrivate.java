package com.airbnb.epoxy;

import android.content.Context;
import android.view.View;

@ModelView(defaultLayout = 1)
public class OnVisibilityStateChangedView_throwsIfPrivate extends View {

  public OnVisibilityStateChangedView_throwsIfPrivate(Context context) {
    super(context);
  }

  @ModelProp
  public void setTitle(CharSequence title) {

  }

  @OnVisibilityStateChanged
  private void onVisibilityStateChanged(int s) {

  }
}