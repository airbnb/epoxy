package com.airbnb.epoxy.models;

import android.support.annotation.ColorInt;
import android.view.View;

import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModel;
import com.airbnb.epoxy.R;

public class ColorModel extends EpoxyModel<View> {
  @EpoxyAttribute @ColorInt int color;

  public ColorModel(@ColorInt int color) {
    this.color = color;
  }

  @Override
  protected int getDefaultLayout() {
    return R.layout.model_color;
  }

  @Override
  public void bind(View view) {
    view.setBackgroundColor(color);
  }
}
