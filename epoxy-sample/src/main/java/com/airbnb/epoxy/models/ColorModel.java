package com.airbnb.epoxy.models;

import android.support.annotation.ColorInt;
import android.view.View;

import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.R;
import com.airbnb.epoxy.SimpleEpoxyModel;

/**
 * This is an example of using {@link com.airbnb.epoxy.SimpleEpoxyModel}, which is useful if you
 * don't need to do anything special in onBind. You can also instantiate {@link
 * com.airbnb.epoxy.SimpleEpoxyModel} directly instead of subclassing it if you don't need to do
 * anything in onBind.
 */
public class ColorModel extends SimpleEpoxyModel {
  @EpoxyAttribute @ColorInt int color;

  public ColorModel(@ColorInt int color) {
    super(R.layout.model_color);
    this.color = color;
  }

  @Override
  public void bind(View view) {
    super.bind(view);
    view.setBackgroundColor(color);
  }
}
