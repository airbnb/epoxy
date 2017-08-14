package com.airbnb.epoxy;

import android.content.Context;
import android.view.View;

@ModelView(defaultLayout = 1)
public class TestCallbackPropMustBeNullableView extends View {

  public TestCallbackPropMustBeNullableView(Context context) {
    super(context);
  }

  @CallbackProp
  public void setListener(View.OnClickListener clickListener) {

  }
}