package com.airbnb.epoxy;

import android.content.Context;
import android.view.View;

import com.airbnb.epoxy.AfterPropsSet;
import com.airbnb.epoxy.ModelProp;
import com.airbnb.epoxy.ModelProp.Option;
import com.airbnb.epoxy.ModelView;

import javax.annotation.Nullable;

@ModelView(autoLayout = ModelView.Size.WRAP_WIDTH_WRAP_HEIGHT)

public class TestFieldPropNullOnRecycleOptionView extends View {
  @ModelProp(options = {Option.NullOnRecycle, Option.IgnoreRequireHashCode}) @Nullable
  OnClickListener value;

  public TestFieldPropNullOnRecycleOptionView(Context context) {
    super(context);
  }

  @AfterPropsSet
  public void call() {
  }
}
