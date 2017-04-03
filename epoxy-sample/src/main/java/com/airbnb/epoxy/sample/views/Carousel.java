package com.airbnb.epoxy.sample.views;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.airbnb.epoxy.EpoxyModel;
import com.airbnb.epoxy.SimpleEpoxyController;

import java.util.List;

public class Carousel extends RecyclerView {
  private static final int SPAN_COUNT = 2;

  private final SimpleEpoxyController controller = new SimpleEpoxyController();
  private final GridLayoutManager layoutManager;

  public Carousel(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);

    // Carousels are generally fixed height. Using fixed size is a small optimization we can make
    // in that case. This isn't safe to do if the models set in this carousel have varying heights.
    setHasFixedSize(true);

    // For the example app we use a grid, but in many real world scenarios a simple
    // linearlayoutmanager is common. You could modify this carousel code to programmatically
    // set the layoutmanager depending on your needs, or hardcode it to a horizontal
    // linearlayoutmanager.
    layoutManager =
        new GridLayoutManager(context, SPAN_COUNT, LinearLayoutManager.HORIZONTAL, false);
    controller.setSpanCount(SPAN_COUNT);
    layoutManager.setSpanSizeLookup(controller.getSpanSizeLookup());
    setLayoutManager(layoutManager);
  }

  @Override
  protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    setAdapter(controller.getAdapter());
  }

  @Override
  protected void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    // We  use swapAdapter instead of setAdapter so that the view pool is not cleared.
    // 'removeAndRecycleExistingViews=true' is used
    // since the carousel is going off screen and these views can now be recycled to be used in
    // another carousel (assuming there is a shared view pool)
    swapAdapter(null, true);
  }

  public void setInitialPrefetchItemCount(int count) {
    layoutManager.setInitialPrefetchItemCount(count);
  }

  public void setModels(List<? extends EpoxyModel<?>> models) {
    controller.setModels(models);
  }

  public void clearModels() {
    controller.clearModels();
  }
}
