
package com.airbnb.epoxy;

import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView.ViewHolder;

import java.util.List;

/**
 * Item animator for use with {@link EpoxyAdapter} so that view holders are always reused during animations.
 */
public class EpoxyItemAnimator extends DefaultItemAnimator {
  @Override
  public boolean canReuseUpdatedViewHolder(@NonNull ViewHolder viewHolder,
      @NonNull List<Object> payloads) {
    return true;
  }
}
