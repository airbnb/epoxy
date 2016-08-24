package com.airbnb.viewmodeladapter;

import android.view.View;

public class BasicModelWithAttribute extends ViewModel<View> {
  @ModelAttribute int value;

  @Override
  protected int getDefaultLayout() {
    return 0;
  }
}
