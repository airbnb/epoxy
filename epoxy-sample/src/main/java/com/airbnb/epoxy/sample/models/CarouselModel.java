package com.airbnb.epoxy.sample.models;

import android.content.Context;
import android.support.v7.widget.RecyclerView.RecycledViewPool;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModel;
import com.airbnb.epoxy.EpoxyModelClass;
import com.airbnb.epoxy.EpoxyModelWithView;
import com.airbnb.epoxy.sample.views.Carousel;

import java.util.List;

@EpoxyModelClass
public abstract class CarouselModel extends EpoxyModelWithView<Carousel> {
  private static final int CAROUSEL_VIEW_HEIGHT_DP = 100;
  private static int carouselViewHeightPx = -1;

  @EpoxyAttribute List<EpoxyModel<?>> models;
  @EpoxyAttribute int numItemsExpectedOnDisplay;
  @EpoxyAttribute(hash = false) RecycledViewPool recycledViewPool;

  @Override
  public void bind(Carousel carousel) {
    // If there are multiple carousels showing the same item types, you can benefit by having a
    // shared view pool between those carousels
    // so new views aren't created for each new carousel.
    if (recycledViewPool != null) {
      carousel.setRecycledViewPool(recycledViewPool);
    }

    if (numItemsExpectedOnDisplay != 0) {
      carousel.setInitialPrefetchItemCount(numItemsExpectedOnDisplay);
    }

    carousel.setModels(models);
  }

  @Override
  public void unbind(Carousel carousel) {
    carousel.clearModels();
  }

  @Override
  protected Carousel buildView(ViewGroup parent) {
    Context context = parent.getContext();
    Carousel carousel = new Carousel(context, null);

    if (carouselViewHeightPx == -1) {
      carouselViewHeightPx =
          (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, CAROUSEL_VIEW_HEIGHT_DP,
              context.getResources().getDisplayMetrics());
    }

    LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, carouselViewHeightPx);
    carousel.setLayoutParams(params);

    return carousel;
  }

  @Override
  public int getSpanSize(int totalSpanCount, int position, int itemCount) {
    return totalSpanCount;
  }
}
