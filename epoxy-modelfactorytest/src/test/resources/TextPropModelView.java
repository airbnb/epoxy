package com.airbnb.epoxy;

import android.content.Context;
import android.widget.FrameLayout;

@ModelView(defaultLayout = 1)
public class TextPropModelView extends FrameLayout {

  public TextPropModelView(Context context) {
    super(context);
  }

  @TextProp
  public void setTitle(CharSequence title) {

  }
}