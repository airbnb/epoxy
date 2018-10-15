package com.airbnb.epoxy;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.AdapterDataObserver;
import android.support.v7.widget.RecyclerView.OnChildAttachStateChangeListener;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.SparseArray;
import android.view.View;
import android.view.View.OnLayoutChangeListener;

import java.util.ArrayList;
import java.util.List;

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
 *
 * @see OnVisibilityChanged
 * @see OnVisibilityStateChanged
 * @see OnModelVisibilityChangedListener
 * @see OnModelVisibilityStateChangedListener
 */
public class EpoxyVisibilityTracker {

  /** Maintain visibility item indexed by view id (identity hashcode) */
  private final SparseArray<EpoxyVisibilityItem> visibilityIdToItemMap = new SparseArray<>();
  private final List<EpoxyVisibilityItem> visibilityIdToItems = new ArrayList<>();

  /** listener used to process scroll, layout and attach events */
  private final Listener listener = new Listener();

  /** listener used to process data events */
  private final DataObserver observer = new DataObserver();

  @Nullable
  private RecyclerView attachedRecyclerView = null;
  @Nullable
  private Adapter lastAdapterSeen = null;

  private boolean onChangedEnabled = true;

  /**
   * Enable or disable visibility changed event. Default is `true`, disable it if you don't need
   * (triggered by every pixel scrolled).
   *
   * @see OnVisibilityChanged
   * @see OnModelVisibilityChangedListener
   */
  public void setOnChangedEnabled(boolean enabled) {
    onChangedEnabled = enabled;
  }

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

  private void processChangeEvent(String debug) {
    processChildren(null, debug);
  }

  private void processChangeEventWithDetachedView(@Nullable View detachedView, String debug) {
    processChildren(detachedView, debug);
  }

  private void processChildren(@Nullable View detachedView, String debug) {
    processNewAdapterInNecessary();
    final RecyclerView recyclerView = attachedRecyclerView;
    if (recyclerView != null) {
      for (int i = 0; i < recyclerView.getChildCount(); i++) {
        final View child = recyclerView.getChildAt(i);
        if (child != null) {
          processChild(child, child == detachedView, debug);
        }
      }
    }
  }

  /**
   * If there is a new adapter on the attached RecyclerView it will resister the data observer and
   * clear the current visibility states
   */
  private void processNewAdapterInNecessary() {
    if (attachedRecyclerView != null && attachedRecyclerView.getAdapter() != null) {
      if (lastAdapterSeen != attachedRecyclerView.getAdapter()) {
        if (lastAdapterSeen != null) {
          // Unregister the old adapter
          lastAdapterSeen.unregisterAdapterDataObserver(this.observer);
        }
        // Register the new adapter
        attachedRecyclerView.getAdapter().registerAdapterDataObserver(this.observer);
        lastAdapterSeen = attachedRecyclerView.getAdapter();
        // Clear our visibility items
        visibilityIdToItemMap.clear();
        visibilityIdToItems.clear();
        System.out.println("attached on " + lastAdapterSeen);
      }
    }
  }

  private void processChild(@NonNull View child, boolean detachEvent, String debug) {
    final RecyclerView recyclerView = attachedRecyclerView;
    if (recyclerView != null) {
      recyclerView.getChildViewHolder(child);
      final ViewHolder holder = recyclerView.getChildViewHolder(child);
      if (holder instanceof EpoxyViewHolder) {
        processVisibilityEvents(recyclerView, (EpoxyViewHolder) holder,
            recyclerView.getLayoutManager().canScrollVertically(), detachEvent, debug);
      } else {
        throw new IllegalEpoxyUsage(
            "`EpoxyVisibilityTracker` cannot be used with non-epoxy view holders."
        );
      }
    }
  }

  private void processVisibilityEvents(
      @NonNull RecyclerView recyclerView,
      @NonNull EpoxyViewHolder epoxyHolder,
      boolean vertical, boolean detachEvent, String debug
  ) {

    // TODO EpoxyVisibilityTrackerTest testInsertData / testInsertData are disabled as they fail as
    // insert/delete not properly handled in the tracker

    System.out.println(String.format(debug +
            ".processVisibilityEvents %s, %s, %s",
        System.identityHashCode(epoxyHolder),
        detachEvent,
        epoxyHolder.getAdapterPosition()
    ));
//    new Throwable(String.format(
//        "processVisibilityEvents %s, %s, %s",
//        System.identityHashCode(epoxyHolder),
//        detachEvent,
//        epoxyHolder.getAdapterPosition()
//    )).printStackTrace();

    final View itemView = epoxyHolder.itemView;
    final int id = System.identityHashCode(itemView);

    EpoxyVisibilityItem vi = visibilityIdToItemMap.get(id);
    if (vi == null) {
      vi = new EpoxyVisibilityItem();
      visibilityIdToItemMap.put(id, vi);
      visibilityIdToItems.add(vi);
    }

    if (epoxyHolder.getAdapterPosition() == RecyclerView.NO_POSITION) {
      System.out.println("no position vs " + vi.getAdapterPosition());
      return;
    }

    if (vi.getAdapterPosition() != epoxyHolder.getAdapterPosition()) {
      // EpoxyVisibilityItem being re-used for a different position
      vi.reset(epoxyHolder.getAdapterPosition());
      System.out.println("reset");
    }

    if (vi.update(itemView, recyclerView, vertical, detachEvent)) {
      // View is measured, process events
      vi.handleVisible(epoxyHolder, detachEvent);
      vi.handleFocus(epoxyHolder, detachEvent);
      vi.handleFullImpressionVisible(epoxyHolder, detachEvent);
      if (onChangedEnabled) {
        vi.handleChanged(epoxyHolder);
      }
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
      processChangeEvent("onLayoutChange");
    }

    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
      processChangeEvent("onScrolled");
    }

    @Override
    public void onChildViewAttachedToWindow(View child) {
      processChangeEvent("onChildViewAttachedToWindow");
    }

    @Override
    public void onChildViewDetachedFromWindow(View child) {
      // On detach event send the detached view
      processChangeEventWithDetachedView(child, "onChildViewDetachedFromWindow");
    }
  }

  class DataObserver extends AdapterDataObserver {

    @Override
    public void onChanged() {
      // Clear the current visibility states
      visibilityIdToItemMap.clear();
      visibilityIdToItems.clear();
    }

    @Override
    public void onItemRangeInserted(int positionStart, int itemCount) {
      // For all items after the inserted range shift the adapter position by item count
      for (EpoxyVisibilityItem item : visibilityIdToItems) {
        if (item.getAdapterPosition() >= positionStart) {
          item.shiftBy(itemCount);
        }
      }
    }

    @Override
    public void onItemRangeRemoved(int positionStart, int itemCount) {
      // For all items after the inserted range shift the adapter position by item count
      for (EpoxyVisibilityItem item : visibilityIdToItems) {
        if (item.getAdapterPosition() >= positionStart) {
          item.shiftBy(-itemCount);
        }
      }
    }

    @Override
    public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
      System.out.println("onItemRangeMoved");
    }
  }
}
