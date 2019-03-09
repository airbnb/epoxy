package com.airbnb.epoxy;

import android.content.Context;
import androidx.annotation.Nullable;
import android.view.View;

@ModelView(defaultLayout = 1)
public class PropDefaultsView_throwsForNonFinalValue extends View {
  static int PRIMITIVE_DEFAULT = 23;

  public PropDefaultsView_throwsForNonFinalValue(Context context) {
    super(context);
  }
  @ModelProp(defaultValue = "PRIMITIVE_DEFAULT")
  public void primitiveWithExplicitDefault(int title) {

  }
}
