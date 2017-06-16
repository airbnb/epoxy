package com.airbnb.epoxy;

import android.content.Context;
import android.view.View;

import com.airbnb.epoxy.ModelProp.Option;

@ModelView(defaultLayout = 1)
public class DoNotHashView extends View {

  public DoNotHashView(Context context) {
    super(context);
  }

  @ModelProp(options = Option.DoNotHash)
  public void setTitle(CharSequence title) {

  }

  @ModelProp(options = Option.DoNotHash)
  public void setClickListener(View.OnClickListener title) {

  }

  @ModelProp
  public void normalProp(CharSequence title) {

  }
}