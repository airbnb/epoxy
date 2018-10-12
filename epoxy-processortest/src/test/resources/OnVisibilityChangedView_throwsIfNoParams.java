package com.airbnb.epoxy;

import android.content.Context;
import android.view.View;

@ModelView(defaultLayout = 1)
public class OnVisibilityChangedView_throwsIfNoParams extends View {

  public OnVisibilityChangedView_throwsIfNoParams(Context context) {
    super(context);
  }

  @ModelProp
  public void setTitle(CharSequence title) {

  }

  @OnVisibilityChanged
  void onVisibilityChanged() {

  }
}