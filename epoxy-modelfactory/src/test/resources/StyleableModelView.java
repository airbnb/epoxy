package com.airbnb.epoxy;

import android.content.Context;
import android.widget.FrameLayout;

import com.airbnb.paris.annotations.Styleable;

@Styleable
@ModelView
public class StyleableModelView extends FrameLayout {

  public StyleableModelView(Context context) {
    super(context);
  }

  @ModelProp
  public void setTitle(String title) {

  }
}