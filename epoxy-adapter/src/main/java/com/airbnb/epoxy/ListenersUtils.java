package com.airbnb.epoxy;

import android.view.View;
import android.view.ViewParent;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

public class ListenersUtils {

  @Nullable
  static EpoxyViewHolder getEpoxyHolderForChildView(View v) {
    RecyclerView recyclerView = findParentRecyclerView(v);
    if (recyclerView == null) {
      return null;
    }

    ViewHolder viewHolder = recyclerView.findContainingViewHolder(v);
    if (viewHolder == null) {
      return null;
    }

    if (!(viewHolder instanceof EpoxyViewHolder)) {
      return null;
    }

    return (EpoxyViewHolder) viewHolder;
  }

  @Nullable
  private static RecyclerView findParentRecyclerView(@Nullable View v) {
    if (v == null) {
      return null;
    }

    ViewParent parent = v.getParent();
    if (parent instanceof RecyclerView) {
      return (RecyclerView) parent;
    }

    if (parent instanceof View) {
      return findParentRecyclerView((View) parent);
    }

    return null;
  }
}
