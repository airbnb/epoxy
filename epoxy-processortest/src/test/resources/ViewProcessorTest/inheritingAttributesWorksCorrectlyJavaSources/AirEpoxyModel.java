package com.airbnb.epoxy;

import android.view.View;

import com.airbnb.epoxy.EpoxyAttribute.Option;

import androidx.annotation.Nullable;
import android.view.View;

public abstract class AirEpoxyModel<T extends View> extends EpoxyModel<T> {

  /**
   * Whether the divider should be shown on the bound view. This should only be set if {@link #supportsDividers()} is true for your model.
   * <p>
   * If this is left as null then the divider will default to hidden.
   * <p>
   * The getter and setter are both omitted so the {@link AirModel} divider methods can be used instead.
   */
  @EpoxyAttribute({Option.NoGetter, Option.NoSetter}) protected Boolean showDivider;

  /**
   * The number of items that should be shown on screen. This assumes the model is used in a horizontal RecyclerView (aka {@link Carousel}), and the
   * view will have its width resized so that the appropriate number of items fit on screen.
   * <p>
   * We don't generate a getter since this class defines one, so it is accessible with just a `AirEpoxyModel` reference.
   */
  @Nullable @EpoxyAttribute(Option.NoGetter) protected SomeType numCarouselItemsShown;

  public AirEpoxyModel<T> numCarouselItemsShown(SomeType numCarouselItemsShown) {
    this.numCarouselItemsShown = numCarouselItemsShown;
    return this;
  }

  @Nullable
  public SomeType numCarouselItemsShown() {
    return numCarouselItemsShown;
  }

  public static class SomeType {

  }
}