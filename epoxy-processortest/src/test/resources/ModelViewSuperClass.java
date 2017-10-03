package com.airbnb.epoxy;

import android.content.Context;
import android.view.View;

import com.airbnb.epoxy.ModelView.Size;

@ModelView(autoLayout = Size.MATCH_WIDTH_MATCH_HEIGHT)
public class ModelViewSuperClass extends View {

  public ModelViewSuperClass(Context context) {
    super(context);
  }

  @ModelProp
  void superClassValue(int value) {

  }

  @OnViewRecycled
  void onClear() {

  }

  @AfterPropsSet
  void afterProps() {

  }
}