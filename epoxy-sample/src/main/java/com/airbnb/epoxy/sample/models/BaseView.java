package com.airbnb.epoxy.sample.models;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;

import com.airbnb.epoxy.ModelProp;
import com.airbnb.epoxy.ModelView;

@ModelView
public abstract class BaseView extends AppCompatTextView {

  public BaseView(Context context) {
    super(context);
  }

  @ModelProp
  public void setVerticalPadding(CharSequence tex) {
  }
}
