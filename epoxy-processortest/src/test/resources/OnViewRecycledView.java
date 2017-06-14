package com.airbnb.epoxy;

import android.content.Context;
import android.view.View;

@ModelView(defaultLayout = 1)
public class OnViewRecycledView extends View {

  public OnViewRecycledView(Context context) {
    super(context);
  }

  @ModelProp
  public void setTitle(CharSequence title) {

  }

  @OnViewRecycled
  void onRecycled1() {
    // also testing package private works
  }

  @OnViewRecycled
  public void onRecycled2() {

  }
}