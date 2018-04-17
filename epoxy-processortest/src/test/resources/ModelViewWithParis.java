package com.airbnb.epoxy;

import android.content.Context;
import android.view.View;

import com.airbnb.epoxy.AfterPropsSet;
import com.airbnb.epoxy.ModelProp;
import com.airbnb.epoxy.ModelProp.Option;
import com.airbnb.epoxy.ModelView;

import com.airbnb.paris.annotations.Style;
import com.airbnb.paris.annotations.Styleable;
@Styleable
@ModelView
public class ModelViewWithParis extends View {
  @ModelProp(options = Option.IgnoreRequireHashCode) OnClickListener value;

  public ModelViewWithParis(Context context) {
    super(context);
  }

  @AfterPropsSet
  public void call() {
  }
}
