package com.airbnb.epoxy;

import android.content.Context;
import android.view.View;

@ModelView(defaultLayout = 1)
public class ObjectWithoutEqualsThrowsView extends View {

  public ObjectWithoutEqualsThrowsView(Context context) {
    super(context);
  }

  @ModelProp
  public void setClickListener(View.OnClickListener title) {

  }
}