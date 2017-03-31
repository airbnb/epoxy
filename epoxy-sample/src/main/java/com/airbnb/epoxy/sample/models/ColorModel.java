package com.airbnb.epoxy.sample.models;

import android.support.annotation.ColorInt;
import android.view.View;

import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModel;
import com.airbnb.epoxy.EpoxyModelClass;
import com.airbnb.epoxy.R;
import com.airbnb.epoxy.sample.ColorData;

/**
 * This is an example of using {@link com.airbnb.epoxy.SimpleEpoxyModel}, which is useful if you
 * don't need to do anything special in onBind. You can also instantiate {@link
 * com.airbnb.epoxy.SimpleEpoxyModel} directly instead of subclassing it if you don't need to do
 * anything in onBind.
 */
@EpoxyModelClass(layout = R.layout.model_color)
public abstract class ColorModel extends EpoxyModel<View> {
  @EpoxyAttribute @ColorInt int color;

  public ColorModel(ColorData color) {
    this.color = color.getColorInt();
    id(color.getId());
  }

  @Override
  public void bind(View view) {
    view.setBackgroundColor(color);
  }
}
