package com.airbnb.epoxy;

import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.View.OnLayoutChangeListener;

import com.airbnb.viewmodeladapter.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver;
import androidx.recyclerview.widget.RecyclerView.OnChildAttachStateChangeListener;
import androidx.recyclerview.widget.RecyclerView.OnScrollListener;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

/**
 * A simple way to track visibility events on {@link com.airbnb.epoxy.EpoxyModel} within a {@link
 * androidx.recyclerview.widget.RecyclerView}.
 * <p>
 * {@link EpoxyVisibilityTracker} works with any {@link androidx.recyclerview.widget.RecyclerView}
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

  @IdRes
  private static final int TAG_ID = R.id.epoxy_visibility_tracker;

  /**
   * @param recyclerView the view.
   * @return the tracker for the given {@link RecyclerView}. Null if no tracker was attached.
   */
  @Nullable
  private static EpoxyVisibilityTracker getTracker(@NonNull RecyclerView recyclerView) {
    return (EpoxyVisibilityTracker) recyclerView.getTag(TAG_ID);
  }

  /**
   * Store the tracker for the given {@link RecyclerView}.
   * @param recyclerView the view
   * @param tracker the tracker
   */
  private static void setTracker(
      @NonNull RecyclerView recyclerView,
      @Nullable EpoxyVisibilityTracker tracker) {
    recyclerView.setTag(TAG_ID, tracker);
  }

  // Not actionable at runtime. It is only useful for internal test-troubleshooting.
  static final boolean DEBUG_LOG = true;

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

  /** All nested visibility trackers */
  private Map<RecyclerView, EpoxyVisibilityTracker> nestedTrackers = new HashMap<>();

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
    setTracker(recyclerView, this);
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
    setTracker(recyclerView, null);
    attachedRecyclerView = null;
  }

  /**
   * The tracker is storing visibility states internally and is using if to send events, only the
   * difference is sent. Use this method to clear the states and thus regenerate the visibility
   * events. This may be useful when you change the adapter on the {@link
   * androidx.recyclerview.widget.RecyclerView}
   */
  public void clearVisibilityStates() {
    // Clear our visibility items
    visibilityIdToItemMap.clear();
    visibilityIdToItems.clear();
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
      final ViewHolder holder = recyclerView.getChildViewHolder(child);
      if (holder instanceof EpoxyViewHolder) {
        boolean changed = processVisibilityEvents(
            recyclerView,
            (EpoxyViewHolder) holder,
            detachEvent,
            eventOriginForDebug
        );
        if (changed) {
          if (child instanceof RecyclerView) {
            EpoxyVisibilityTracker tracker = nestedTrackers.get(child);
            if (tracker != null) {
              // If view visibility changed and there was a tracker on it then notify it.
              tracker.processChangeEvent("parent");
            }
          }
        }
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
   * @param detachEvent         true if the event originated from a view detached from the
   *                            recycler view
   * @param eventOriginForDebug a debug strings used for logs
   * @return true if changed
   */
  private boolean processVisibilityEvents(
      @NonNull RecyclerView recyclerView,
      @NonNull EpoxyViewHolder epoxyHolder,
      boolean detachEvent,
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
      // New view discovered, assign an EpoxyVisibilityItem
      vi = new EpoxyVisibilityItem(epoxyHolder.getAdapterPosition());
      visibilityIdToItemMap.put(id, vi);
      visibilityIdToItems.add(vi);
    } else if (epoxyHolder.getAdapterPosition() != RecyclerView.NO_POSITION
        && vi.getAdapterPosition() != epoxyHolder.getAdapterPosition()) {
      // EpoxyVisibilityItem being re-used for a different adapter position
      vi.reset(epoxyHolder.getAdapterPosition());
    }

    boolean changed = false;
    if (vi.update(itemView, recyclerView, detachEvent)) {
      // View is measured, process events
      vi.handleVisible(epoxyHolder, detachEvent);
      vi.handleFocus(epoxyHolder, detachEvent);
      vi.handleFullImpressionVisible(epoxyHolder, detachEvent);
      changed = vi.handleChanged(epoxyHolder, onChangedEnabled);
    }
    return changed;
  }

  private void processChildRecyclerViewAttached(@NonNull RecyclerView childRecyclerView) {
    // Register itself in the EpoxyVisibilityTracker. This will take care of nested list
    // tracking (ex: carousel)
    EpoxyVisibilityTracker tracker = getTracker(childRecyclerView);
    if (tracker == null) {
      tracker = new EpoxyVisibilityTracker();
      tracker.attach(childRecyclerView);
    }
    nestedTrackers.put(childRecyclerView, tracker);
  }

  private void processChildRecyclerViewDetached(@NonNull RecyclerView childRecyclerView) {
    nestedTrackers.remove(childRecyclerView);
  }

  /**
   * Helper class that host the {@link androidx.recyclerview.widget.RecyclerView} listener
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
    public void onChildViewAttachedToWindow(@NonNull View child) {
      if (child instanceof RecyclerView) {
        processChildRecyclerViewAttached((RecyclerView) child);
      }
      processChild(child, false, "onChildViewAttachedToWindow");
    }

    @Override
    public void onChildViewDetachedFromWindow(@NonNull View child) {
      if (child instanceof RecyclerView) {
        processChildRecyclerViewDetached((RecyclerView) child);
      }
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
      if (notEpoxyManaged(attachedRecyclerView)) {
        return;
      }
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
      if (notEpoxyManaged(attachedRecyclerView)) {
        return;
      }
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
      if (notEpoxyManaged(attachedRecyclerView)) {
        return;
      }
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
     * ps: anyway {@link androidx.recyclerview.widget.AdapterListUpdateCallback}
     * does not seem to use range for moved items.
     */
    @Override
    public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
      if (notEpoxyManaged(attachedRecyclerView)) {
        return;
      }
      for (int i = 0; i < itemCount; i++) {
        onItemMoved(fromPosition + i, toPosition + i);
      }
    }

    private void onItemMoved(int fromPosition, int toPosition) {
      if (notEpoxyManaged(attachedRecyclerView)) {
        return;
      }
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

    /**
     * @param recyclerView the recycler view
     * @return true if managed by an {@link BaseEpoxyAdapter}
     */
    private boolean notEpoxyManaged(RecyclerView recyclerView) {
      return recyclerView == null || !(recyclerView.getAdapter() instanceof BaseEpoxyAdapter);
    }
  }
}
