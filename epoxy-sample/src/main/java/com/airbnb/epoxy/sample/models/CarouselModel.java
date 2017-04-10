package com.airbnb.epoxy.sample.models;

import android.support.v7.widget.RecyclerView.RecycledViewPool;
import android.view.ViewGroup;

import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModel;
import com.airbnb.epoxy.EpoxyModelClass;
import com.airbnb.epoxy.EpoxyModelWithView;
import com.airbnb.epoxy.sample.views.Carousel;

import java.util.List;

@EpoxyModelClass
public abstract class CarouselModel extends EpoxyModelWithView<Carousel> {

  @EpoxyAttribute List<? extends EpoxyModel<?>> models;
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
    return new Carousel(parent.getContext(), null);
  }

  @Override
  public boolean shouldSaveViewState() {
    // Save the state of the scroll position
    return true;
  }
}
