package com.airbnb.epoxy;

import android.content.Context;
import android.view.View;

@ModelView(defaultLayout = 1)
public class TestTextPropMustBeCharSequenceView extends View {

  public TestTextPropMustBeCharSequenceView(Context context) {
    super(context);
  }

  @TextProp
  public void setTitle(String title) {

  }
}