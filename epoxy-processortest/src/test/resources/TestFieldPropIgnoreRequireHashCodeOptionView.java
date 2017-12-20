package com.airbnb.epoxy;

import android.content.Context;
import android.view.View;

import com.airbnb.epoxy.AfterPropsSet;
import com.airbnb.epoxy.ModelProp;
import com.airbnb.epoxy.ModelProp.Option;
import com.airbnb.epoxy.ModelView;

@ModelView(autoLayout = ModelView.Size.WRAP_WIDTH_WRAP_HEIGHT)

public class TestFieldPropIgnoreRequireHashCodeOptionView extends View {
  @ModelProp(options = Option.IgnoreRequireHashCode) OnClickListener value;

  public TestFieldPropIgnoreRequireHashCodeOptionView(Context context) {
    super(context);
  }

  @AfterPropsSet
  public void call() {
  }
}
