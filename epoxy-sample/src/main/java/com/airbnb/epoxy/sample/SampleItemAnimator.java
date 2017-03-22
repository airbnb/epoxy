package com.airbnb.epoxy.sample;

import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView.ViewHolder;

import com.airbnb.epoxy.EpoxyModel;
import com.airbnb.epoxy.EpoxyViewHolder;
import com.airbnb.epoxy.sample.models.CarouselModel;

import java.util.List;

public class SampleItemAnimator extends DefaultItemAnimator {

  @Override
  public boolean canReuseUpdatedViewHolder(@NonNull ViewHolder viewHolder, @NonNull
      List<Object> payloads) {

    EpoxyViewHolder epoxyViewHolder = (EpoxyViewHolder) viewHolder;
    EpoxyModel<?> model = epoxyViewHolder.getModel();

    if (model instanceof CarouselModel) {
      return true;
    }

    return super.canReuseUpdatedViewHolder(viewHolder, payloads);
  }
}
