package com.airbnb.epoxy;

import android.content.Context;
import android.view.View;

@ModelView(defaultLayout = 1)
public class PropDefaultsView_throwsForWrongType extends View {
  static final String PRIMITIVE_DEFAULT = 23;

  public PropDefaultsView_throwsForWrongType(Context context) {
    super(context);
  }

  @ModelProp(defaultValue = "PRIMITIVE_DEFAULT")
  public void primitiveWithExplicitDefault(int title) {

  }
}