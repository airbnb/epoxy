package com.airbnb.epoxy;

import android.content.Context;
import android.view.View;

@ModelView(defaultLayout = 1)
public abstract class TestAfterBindPropsSuperView extends View {

  public TestAfterBindPropsSuperView(Context context) {
    super(context);
  }

  @ModelProp
  public void setFlagSuper(boolean flag) {

  }

  @AfterPropsSet
  public void afterFlagSetSuper() {

  }
}