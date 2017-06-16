package com.airbnb.epoxy;

import android.content.Context;
import android.view.View;

@ModelView(defaultLayout = 1)
public class OnViewRecycledView_throwsIfStatic extends View {

  public OnViewRecycledView_throwsIfStatic(Context context) {
    super(context);
  }

  @ModelProp
  public void setTitle(CharSequence title) {

  }

  @OnViewRecycled
  static void onRecycled1() {
  }

}