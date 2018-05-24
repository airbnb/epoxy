package com.airbnb.epoxy;

import android.content.Context;
import androidx.annotation.Nullable;
import android.view.View;

@ModelView(defaultLayout = 1)
public class PropDefaultsView_throwsForNonStaticValue extends View {
  final int PRIMITIVE_DEFAULT = 23;

  public PropDefaultsView_throwsForNonStaticValue(Context context) {
    super(context);
  }
  @ModelProp(defaultValue = "PRIMITIVE_DEFAULT")
  public void primitiveWithExplicitDefault(int title) {

  }
}
