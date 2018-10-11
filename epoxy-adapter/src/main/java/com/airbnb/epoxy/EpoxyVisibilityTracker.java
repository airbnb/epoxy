package com.airbnb.epoxy;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnChildAttachStateChangeListener;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.SparseArray;
import android.view.View;
import android.view.View.OnLayoutChangeListener;

/**
 * A simple way to track visibility events on {@link com.airbnb.epoxy.EpoxyModel} within a {@link
 * android.support.v7.widget.RecyclerView}.
 * <p>
 * {@link EpoxyVisibilityTracker} works with any {@link android.support.v7.widget.RecyclerView}
 * backed by an Epoxy controller. Once attached the events will be forwarded to the Epoxy model (or
 * to the Epoxy view when using annotations).
 * <p>
 * Note regarding nested lists: The visibility event tracking is not properly handled yet. This is
 * on the todo.
 * <p>
 * @see OnVisibilityChanged (annotation)
 * @see OnVisibilityStateChanged (annotation)
 */
public class EpoxyVisibilityTracker {

  /** Maintain visibility item indexed by view id (identity hashcode) */
  private final SparseArray<EpoxyVisibilityItem> visibilityIdToItemMap = new SparseArray<>();

  /** listener used to process scroll, layout and attach events */
  private final Listener listener = new Listener();

  @Nullable
  private RecyclerView attachedRecyclerView = null;

  /**
   * Attach the tracker.
   *
   * @param recyclerView The recyclerview that the EpoxyController has its adapter added to.
   */
  public void attach(@NonNull RecyclerView recyclerView) {
    attachedRecyclerView = recyclerView;
    recyclerView.addOnScrollListener(this.listener);
    recyclerView.addOnLayoutChangeListener(this.listener);
    recyclerView.addOnChildAttachStateChangeListener(this.listener);
  }

  /**
   * Detach the tracker
   *
   * @param recyclerView The recyclerview that the EpoxyController has its adapter added to.
   */
  public void detach(@NonNull RecyclerView recyclerView) {
    recyclerView.removeOnScrollListener(this.listener);
    recyclerView.removeOnLayoutChangeListener(this.listener);
    recyclerView.removeOnChildAttachStateChangeListener(this.listener);
    attachedRecyclerView = null;
  }

  private void processChildren() {
    final RecyclerView recyclerView = attachedRecyclerView;
    if (recyclerView != null) {
      for (int i = 0; i < recyclerView.getChildCount(); i++) {
        final View child = recyclerView.getChildAt(i);
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
    final RecyclerView recyclerView = attachedRecyclerView;
    if (recyclerView != null) {
      recyclerView.getChildViewHolder(child);
      final ViewHolder holder = recyclerView.getChildViewHolder(child);
      if (holder instanceof EpoxyViewHolder) {
        processVisibilityEvents(recyclerView, (EpoxyViewHolder) holder,
            recyclerView.getLayoutManager().canScrollVertically(), detachEvent);
      } else throw new IllegalEpoxyUsage(
          "`EpoxyVisibilityTracker` cannot be used with non-epoxy view holders."
      );
    }
  }

  private void processVisibilityEvents(
      @NonNull RecyclerView recyclerView,
      @NonNull EpoxyViewHolder epoxyHolder,
      boolean vertical, boolean detachEvent
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

    if (vi.update(itemView, recyclerView, vertical)) {
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
