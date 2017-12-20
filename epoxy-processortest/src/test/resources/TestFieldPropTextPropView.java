package com.airbnb.epoxy;


import android.content.Context;
import android.view.View;

import com.airbnb.epoxy.AfterPropsSet;
import com.airbnb.epoxy.ModelView;
import com.airbnb.epoxy.TextProp;

@ModelView(autoLayout = ModelView.Size.WRAP_WIDTH_WRAP_HEIGHT)
public class TestFieldPropTextPropView extends View {
  @TextProp CharSequence value;

  public TestFieldPropTextPropView(Context context) {
    super(context);
  }

  @AfterPropsSet
  public void call() { }
}
