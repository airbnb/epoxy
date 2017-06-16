package com.airbnb.epoxy;

import android.content.Context;
import android.view.View;

@ModelView(defaultLayout = 1)
public class PropDefaultsView_throwsForPrivateValue extends View {
  private static final int PRIMITIVE_DEFAULT = 23;

  public PropDefaultsView_throwsForPrivateValue(Context context) {
    super(context);
  }

  @ModelProp(defaultValue = "PRIMITIVE_DEFAULT")
  public void primitiveWithExplicitDefault(int title) {

  }
}