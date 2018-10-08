package com.airbnb.epoxy;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.support.v7.widget.RecyclerView.OnChildAttachStateChangeListener;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.SparseArray;
import android.view.View;
import android.view.View.OnLayoutChangeListener;

/**
 * Helper class to handle visibility event from an {@link com.airbnb.epoxy.EpoxyRecyclerView}.
 */
class EpoxyVisibilityTracker {

  /** Maintain visibility item indexed by view id (identity hashcode) */
  private final SparseArray<EpoxyVisibilityItem> visibilityIdToItemMap = new SparseArray<>();

  /** listener used to process scroll, layout and attach events */
  private final Listener listener = new Listener();

  @Nullable
  private RecyclerView attachedRecyclerView = null;

  @Nullable
  private LinearLayoutManager attachedLinearLayoutManager = null;

  /**
   * Attach the tracker.
   *
   * @param recyclerView The EpoxyRecyclerView.
   */
  void attach(@NonNull EpoxyRecyclerView recyclerView) {
    attachedRecyclerView = recyclerView;
    recyclerView.addOnScrollListener(this.listener);
    recyclerView.addOnLayoutChangeListener(this.listener);
    recyclerView.addOnChildAttachStateChangeListener(this.listener);
  }

  /**
   * Detach the tracker
   *
   * @param recyclerView The EpoxyRecyclerView.
   */
  void detach(@NonNull EpoxyRecyclerView recyclerView) {
    recyclerView.removeOnScrollListener(this.listener);
    recyclerView.removeOnLayoutChangeListener(this.listener);
    recyclerView.removeOnChildAttachStateChangeListener(this.listener);
    attachedRecyclerView = null;
    attachedLinearLayoutManager = null;
  }

  @Nullable
  private LinearLayoutManager getLinearLayoutManager() {
    if (attachedLinearLayoutManager == null && attachedRecyclerView != null) {
      final LayoutManager lm = attachedRecyclerView.getLayoutManager();
      if (lm instanceof LinearLayoutManager) {
        attachedLinearLayoutManager = (LinearLayoutManager) lm;
      } else {
        throw new IllegalStateException("setVisibilityTrackingEnabled(true) require to have a "
            + "LinearLayoutManager, found " + lm.getClass().getName() + ".");
      }
    }
    return attachedLinearLayoutManager;
  }

  private void processChildren() {
    final LinearLayoutManager llm = getLinearLayoutManager();
    if (llm != null) {
      for (int i = 0; i < llm.getChildCount(); i++) {
        final View child = llm.getChildAt(i);
        if (child != null) {
          processChild(child);
        }
      }
    }
  }

  private void processChild(@NonNull View child) {
    processChild(child, false);
  }

  private void processChild(@NonNull View child, boolean detachEvent) {
    final LinearLayoutManager llm = getLinearLayoutManager();
    if (attachedRecyclerView != null && llm != null) {
      attachedRecyclerView.getChildViewHolder(child);
      final ViewHolder holder = attachedRecyclerView.getChildViewHolder(child);
      if (holder instanceof EpoxyViewHolder) {
        processVisibilityEvents(attachedRecyclerView, (EpoxyViewHolder) holder,
            llm.getOrientation(), detachEvent);
      }
    }
  }

  private void processVisibilityEvents(
      @NonNull RecyclerView recyclerView,
      @NonNull EpoxyViewHolder epoxyHolder,
      int orientation, boolean detachEvent
  ) {
    if (epoxyHolder.getAdapterPosition() == RecyclerView.NO_POSITION) {
      return;
    }

    final View itemView = epoxyHolder.itemView;
    final int id = System.identityHashCode(itemView);

    EpoxyVisibilityItem vi = visibilityIdToItemMap.get(id);
    if (vi == null) {
      vi = new EpoxyVisibilityItem();
      visibilityIdToItemMap.put(id, vi);
    }

    if (vi.getAdapterPosition() != epoxyHolder.getAdapterPosition()) {
      // EpoxyVisibilityItem being re-used for a different position
      vi.reset(epoxyHolder.getAdapterPosition());
    }

    if (vi.update(itemView, recyclerView, orientation)) {
      // View is measured, process events
      vi.handleVisible(epoxyHolder);
      vi.handleInvisible(epoxyHolder, detachEvent);
      vi.handleFocusedVisible(epoxyHolder);
      vi.handleUnfocusedVisible(epoxyHolder, detachEvent);
      vi.handleFullImpressionVisible(epoxyHolder);
      vi.handleChanged(epoxyHolder);
    }
  }

  /**
   * Helper class that host the {@link android.support.v7.widget.RecyclerView} listener
   * implementations
   */
  private class Listener extends OnScrollListener
      implements OnLayoutChangeListener, OnChildAttachStateChangeListener {

    @Override
    public void onLayoutChange(
        @NonNull View recyclerView,
        int left, int top, int right, int bottom,
        int oldLeft, int oldTop, int oldRight, int oldBottom
    ) {
      processChildren();
    }

    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
      processChildren();
    }

    @Override
    public void onChildViewAttachedToWindow(View child) {
      processChild(child);
    }

    @Override
    public void onChildViewDetachedFromWindow(View child) {
      processChild(child, true);
    }
  }
}
