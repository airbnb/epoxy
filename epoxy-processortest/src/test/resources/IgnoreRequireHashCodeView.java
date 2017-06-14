package com.airbnb.epoxy;

import android.content.Context;
import android.view.View;

import com.airbnb.epoxy.ModelProp.Option;

@ModelView(defaultLayout = 1)
public class IgnoreRequireHashCodeView extends View {

  public IgnoreRequireHashCodeView(Context context) {
    super(context);
  }

  @ModelProp(options = Option.IgnoreRequireHashCode)
  public void setClickListener(View.OnClickListener title) {

  }
}