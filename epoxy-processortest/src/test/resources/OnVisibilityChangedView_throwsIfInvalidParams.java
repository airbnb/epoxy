package com.airbnb.epoxy;

import android.content.Context;
import android.view.View;

@ModelView(defaultLayout = 1)
public class OnVisibilityChangedView_throwsIfInvalidParams extends View {

  public OnVisibilityChangedView_throwsIfInvalidParams(Context context) {
    super(context);
  }

  @ModelProp
  public void setTitle(CharSequence title) {

  }

  @OnVisibilityChanged
  public void onVisibilityChanged(boolean ph, boolean pw, int vh, int vw) {

  }
}