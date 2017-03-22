package com.airbnb.epoxy.sample.views;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.airbnb.epoxy.EpoxyModel;
import com.airbnb.epoxy.SimpleEpoxyController;

import java.util.List;

public class Carousel extends RecyclerView {
  private final SimpleEpoxyController controller = new SimpleEpoxyController();
  private final LinearLayoutManager linearLayoutManager;

  public Carousel(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);

    // Carousels are generally fixed height. Using fixed size is a small optimization we can make
    // in that case. This isn't safe to do if the models set in this carousel have varying heights.
    setHasFixedSize(true);

    linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
    setLayoutManager(linearLayoutManager);
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
    linearLayoutManager.setInitialPrefetchItemCount(count);
  }

  public void setModels(List<EpoxyModel<?>> models) {
    controller.setModels(models);
  }

  public void clearModels() {
    controller.clearModels();
  }
}
