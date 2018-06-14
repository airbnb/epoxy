package com.airbnb.epoxy;

import android.content.Context;
import android.view.View;

@ModelView(defaultLayout = 1)
public class PropDefaultsView_throwsForNotFound extends View {

  public PropDefaultsView_throwsForNotFound(Context context) {
    super(context);
  }

  @ModelProp(defaultValue = "PRIMITIVE_DEFAULT")
  public void primitiveWithExplicitDefault(int title) {

  }
}