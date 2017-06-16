package com.airbnb.epoxy;

import android.content.Context;
import android.view.View;

@ModelView(defaultLayout = 1, saveViewState = true)
public class SavedStateView extends View {

  public SavedStateView(Context context) {
    super(context);
  }

  @ModelProp
  public void setClickListener(String title) {

  }
}