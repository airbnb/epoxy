package com.airbnb.epoxy;

import android.content.Context;

@ModelView(defaultLayout = 1)
public class TestAfterBindPropsView extends TestAfterBindPropsSuperView {

  public TestAfterBindPropsView(Context context) {
    super(context);
  }

  @ModelProp
  public void setFlag(boolean flag) {

  }

  @AfterPropsSet
  public void afterFlagSet() {

  }
}