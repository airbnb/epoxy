package com.airbnb.epoxy;

import android.content.Context;
import android.view.View;

@ModelView(defaultLayout = 1)
public class TestTextPropView extends View {

  public TestTextPropView(Context context) {
    super(context);
  }

  @TextProp
  public void setTitle(CharSequence title) {

  }
}