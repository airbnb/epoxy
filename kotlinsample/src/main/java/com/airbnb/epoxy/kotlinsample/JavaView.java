package com.airbnb.epoxy.kotlinsample;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.FrameLayout;

import com.airbnb.epoxy.ModelProp;
import com.airbnb.epoxy.ModelView;

@ModelView(defaultLayout = R.layout.java_view)
public class JavaView extends FrameLayout {
  public JavaView(@NonNull Context context) {
    super(context);
  }

  @ModelProp
  public void setSomething(int something) {

  }
}
