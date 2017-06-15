package com.airbnb.epoxy;

import android.content.Context;
import android.view.View;

@ModelView(defaultLayout = 1, fullSpan = false)
public class GridSpanCountView extends View {

  public GridSpanCountView(Context context) {
    super(context);
  }

  @ModelProp
  public void setClickListener(String title) {

  }
}