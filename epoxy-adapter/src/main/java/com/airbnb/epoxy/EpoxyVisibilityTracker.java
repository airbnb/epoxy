package com.airbnb.epoxy;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.AdapterDataObserver;
import android.support.v7.widget.RecyclerView.OnChildAttachStateChangeListener;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
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

  private static final String TAG = "EpoxyVisibilityTracker";

  // Not actionable at runtime. It is only useful for internal test-troubleshooting.
  static final boolean DEBUG_LOG = false;

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

  /** This flag is for optimizing the process on detach. If detach is from data changed then it
   * need to re-process all views, else no need (ex: scroll). */
  private boolean visibleDataChanged = false;

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
   * @param recyclerView The recycler view that the EpoxyController has its adapter added to.
   */
  public void detach(@NonNull RecyclerView recyclerView) {
    recyclerView.removeOnScrollListener(this.listener);
    recyclerView.removeOnLayoutChangeListener(this.listener);
    recyclerView.removeOnChildAttachStateChangeListener(this.listener);
    attachedRecyclerView = null;
  }

  private void processChangeEvent(String debug) {
    processChangeEventWithDetachedView(null, debug);
  }

  private void processChangeEventWithDetachedView(@Nullable View detachedView, String debug) {
    final RecyclerView recyclerView = attachedRecyclerView;
    if (recyclerView != null) {

      // On every every events lookup for a new adapter
      processNewAdapterIfNecessary();

      // Process the detached child if any
      if (detachedView != null) {
        processChild(detachedView, true, debug);
      }

      // Process all attached children

      for (int i = 0; i < recyclerView.getChildCount(); i++) {
        final View child = recyclerView.getChildAt(i);
        if (child != null && child != detachedView) {
          // Is some case the detached child is still in the recycler view. Don't process it as it
          // was already processed.
          processChild(child, false, debug);
        }
      }
    }
  }

  /**
   * If there is a new adapter on the attached RecyclerView it will resister the data observer and
   * clear the current visibility states
   */
  private void processNewAdapterIfNecessary() {
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

  /**
   * Don't call this method directly, it is called from
   * {@link EpoxyVisibilityTracker#processVisibilityEvents}
   *
   * @param child               the view to process for visibility event
   * @param detachEvent         true if the child was just detached
   * @param eventOriginForDebug a debug strings used for logs
   */
  private void processChild(@NonNull View child, boolean detachEvent, String eventOriginForDebug) {
    final RecyclerView recyclerView = attachedRecyclerView;
    if (recyclerView != null) {
      recyclerView.getChildViewHolder(child);
      final ViewHolder holder = recyclerView.getChildViewHolder(child);
      if (holder instanceof EpoxyViewHolder) {
        processVisibilityEvents(
            recyclerView,
            (EpoxyViewHolder) holder,
            recyclerView.getLayoutManager().canScrollVertically(),
            detachEvent,
            eventOriginForDebug
        );
      } else {
        throw new IllegalEpoxyUsage(
            "`EpoxyVisibilityTracker` cannot be used with non-epoxy view holders."
        );
      }
    }
  }

  /**
   * Call this methods every time something related to ui (scroll, layout, ...) or something related
   * to data changed.
   *
   * @param recyclerView        the recycler view
   * @param epoxyHolder         the {@link RecyclerView}
   * @param vertical            true if the scrolling is vertical
   * @param detachEvent         true if the event originated from a view detached from the
   *                            recycler view
   * @param eventOriginForDebug a debug strings used for logs
   */
  private void processVisibilityEvents(
      @NonNull RecyclerView recyclerView,
      @NonNull EpoxyViewHolder epoxyHolder,
      boolean vertical, boolean detachEvent,
      String eventOriginForDebug
  ) {

    if (DEBUG_LOG) {
      Log.d(TAG, String.format("%s.processVisibilityEvents %s, %s, %s",
          eventOriginForDebug,
          System.identityHashCode(epoxyHolder),
          detachEvent,
          epoxyHolder.getAdapterPosition()
      ));
    }

    final View itemView = epoxyHolder.itemView;
    final int id = System.identityHashCode(itemView);

    EpoxyVisibilityItem vi = visibilityIdToItemMap.get(id);
    if (vi == null) {
      vi = new EpoxyVisibilityItem();
      visibilityIdToItemMap.put(id, vi);
      visibilityIdToItems.add(vi);
    }

    if (vi.getAdapterPosition() != epoxyHolder.getAdapterPosition() && !detachEvent) {
      // EpoxyVisibilityItem being re-used for a different position
      vi.reset(epoxyHolder.getAdapterPosition());
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
      processChild(child, false, "onChildViewAttachedToWindow");
    }

    @Override
    public void onChildViewDetachedFromWindow(View child) {
      if (visibleDataChanged) {
        // On detach event caused by data set changed we need to re-process all children because
        // the removal caused the others views to changes.
        processChangeEventWithDetachedView(child, "onChildViewDetachedFromWindow");
        visibleDataChanged = false;
      } else {
        processChild(child, true, "onChildViewDetachedFromWindow");
      }
    }
  }

  /**
   * The layout/scroll events are not enough to detect all sort of visibility changes. We also
   * need to look at the data events from the adapter.
   */
  class DataObserver extends AdapterDataObserver {

    /**
     * Clear the current visibility statues
     */
    @Override
    public void onChanged() {
      if (DEBUG_LOG) {
        Log.d(TAG, "onChanged()");
      }
      visibilityIdToItemMap.clear();
      visibilityIdToItems.clear();
      visibleDataChanged = true;
    }

    /**
     * For all items after the inserted range shift each {@link EpoxyVisibilityTracker} adapter
     * position by inserted item count.
     */
    @Override
    public void onItemRangeInserted(int positionStart, int itemCount) {
      if (DEBUG_LOG) {
        Log.d(TAG, String.format("onItemRangeInserted(%d, %d)", positionStart, itemCount));
      }
      for (EpoxyVisibilityItem item : visibilityIdToItems) {
        if (item.getAdapterPosition() >= positionStart) {
          visibleDataChanged = true;
          item.shiftBy(itemCount);
        }
      }
    }

    /**
     * For all items after the removed range reverse-shift each {@link EpoxyVisibilityTracker}
     * adapter position by removed item count
     */
    @Override
    public void onItemRangeRemoved(int positionStart, int itemCount) {
      if (DEBUG_LOG) {
        Log.d(TAG, String.format("onItemRangeRemoved(%d, %d)", positionStart, itemCount));
      }
      for (EpoxyVisibilityItem item : visibilityIdToItems) {
        if (item.getAdapterPosition() >= positionStart) {
          visibleDataChanged = true;
          item.shiftBy(-itemCount);
        }
      }
    }

    /**
     * This is a bit more complex, for move we need to first swap the moved position then shift the
     * items between the swap. To simplify we split any range passed to individual item moved.
     *
     * ps: anyway {@link android.support.v7.util.AdapterListUpdateCallback} does not seem to use
     * range for moved items.
     */
    @Override
    public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
      for (int i = 0; i < itemCount; i++) {
        onItemMoved(fromPosition + i, toPosition + i);
      }
    }

    private void onItemMoved(int fromPosition, int toPosition) {
      if (DEBUG_LOG) {
        Log.d(TAG,
            String.format("onItemRangeMoved(%d, %d, %d)", fromPosition, toPosition, 1));
      }
      for (EpoxyVisibilityItem item : visibilityIdToItems) {
        int position = item.getAdapterPosition();
        if (position == fromPosition) {
          // We found the item to be moved, just swap the position.
          item.shiftBy(toPosition - fromPosition);
          visibleDataChanged = true;
        } else if (fromPosition < toPosition) {
          // Item will be moved down in the list
          if (position > fromPosition && position <= toPosition) {
            // Item is between the moved from and to indexes, it should move up
            item.shiftBy(-1);
            visibleDataChanged = true;
          }
        } else if (fromPosition > toPosition) {
          // Item will be moved up in the list
          if (position >= toPosition && position < fromPosition) {
            // Item is between the moved to and from indexes, it should move down
            item.shiftBy(1);
            visibleDataChanged = true;
          }
        }
      }
    }
  }
}
