package com.airbnb.epoxy;

import android.content.Context;
import android.view.View;

@ModelView(defaultLayout = 1)
public class OnViewRecycledView_throwsIfHasParams extends View {

  public OnViewRecycledView_throwsIfHasParams(Context context) {
    super(context);
  }

  @ModelProp
  public void setTitle(CharSequence title) {

  }

  @OnViewRecycled
  private void onRecycled1(int param) {
  }

}