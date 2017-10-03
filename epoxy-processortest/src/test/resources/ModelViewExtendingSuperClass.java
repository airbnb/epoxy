package com.airbnb.epoxy;

import android.content.Context;
import android.view.View;

import com.airbnb.epoxy.ModelView.Size;

@ModelView(autoLayout = Size.MATCH_WIDTH_MATCH_HEIGHT)
public class ModelViewExtendingSuperClass extends View {

  public ModelViewExtendingSuperClass(Context context) {
    super(context);
  }

  @ModelProp
  void subClassValue(int value) {

  }

  @ModelProp
  void superClassValue(int value) {
    // same as super class, expect no duplicate
  }

  @OnViewRecycled
  void onClear() {

  }

  @OnViewRecycled
  void onSubClassCleared() {

  }

  @AfterPropsSet
  void afterProps() {

  }

  @AfterPropsSet
  void onSubclassAfterPropsSet() {

  }
}