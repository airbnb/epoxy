package com.airbnb.epoxy;

import android.content.Context;
import android.view.View;

@ModelView(defaultLayout = 1)
public class OnVisibilityStateChangedView_throwsIfNoParams extends View {

  public OnVisibilityStateChangedView_throwsIfNoParams(Context context) {
    super(context);
  }

  @ModelProp
  public void setTitle(CharSequence title) {

  }

  @OnVisibilityStateChanged
  void onVisibilityStateChanged() {

  }
}