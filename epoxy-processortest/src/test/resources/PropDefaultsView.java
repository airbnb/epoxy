package com.airbnb.epoxy;

import android.content.Context;
import androidx.annotation.Nullable;
import android.view.View;

@ModelView(defaultLayout = 1)
public class PropDefaultsView extends View {
  static final int PRIMITIVE_DEFAULT = 23;
  static final String STRING_DEFAULT = "hello world";

  public PropDefaultsView(Context context) {
    super(context);
  }

  @ModelProp
  public void defaultsToNull(@Nullable CharSequence title) {

  }

  @ModelProp
  public void noDefaultSoItIsRequired(CharSequence title) {

  }

  @ModelProp
  public void primitivesHaveImplicitDefaultsAndCannotBeRequired(int title) {

  }

  @ModelProp(defaultValue = "PRIMITIVE_DEFAULT")
  public void primitiveWithExplicitDefault(int title) {

  }

  @ModelProp(defaultValue = "STRING_DEFAULT")
  public void objectWithDefault(String title) {

  }

  @ModelProp(defaultValue = "STRING_DEFAULT")
  public void objectWithDefaultAndNullable(@Nullable String title) {

  }
}
