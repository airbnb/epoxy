package com.airbnb.epoxy.sample.models;

import android.content.Context;

import com.airbnb.epoxy.ModelProp;
import com.airbnb.epoxy.ModelView;

import androidx.appcompat.widget.AppCompatTextView;

@ModelView
public abstract class BaseView extends AppCompatTextView {

  public BaseView(Context context) {
    super(context);
  }

  @ModelProp
  public void setVerticalPadding(CharSequence tex) {
  }
}
