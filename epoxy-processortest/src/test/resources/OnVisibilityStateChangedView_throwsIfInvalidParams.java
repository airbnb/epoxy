package com.airbnb.epoxy;

import android.content.Context;
import android.view.View;

@ModelView(defaultLayout = 1)
public class OnVisibilityStateChangedView_throwsIfInvalidParams extends View {

  public OnVisibilityStateChangedView_throwsIfInvalidParams(Context context) {
    super(context);
  }

  @ModelProp
  public void setTitle(CharSequence title) {

  }

  @OnVisibilityStateChanged
  public void onVisibilityStateChanged(boolean s) {

  }
}