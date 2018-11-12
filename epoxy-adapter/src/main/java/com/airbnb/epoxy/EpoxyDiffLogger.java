package com.airbnb.epoxy;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver;

/**
 * This data observer can be registered with an Epoxy adapter or controller to log all item change
 * events. This may be useful to use in debug builds in order to observe model updates and monitor
 * for issues.
 * <p>
 * You may want to look for unexpected item updates to catch improper hashCode/equals
 * implementations in your models.
 * <p>
 * Additionally, you may want to look for frequent or unnecessary updates as an opportunity for
 * optimization.
 */
public class EpoxyDiffLogger extends AdapterDataObserver {
  private final String tag;

  public EpoxyDiffLogger(String tag) {
    this.tag = tag;
  }

  @Override
  public void onItemRangeChanged(int positionStart, int itemCount) {
    Log.d(tag, "Item range changed. Start: " + positionStart + " Count: " + itemCount);
  }

  @Override
  public void onItemRangeChanged(int positionStart, int itemCount, @Nullable Object payload) {
    if (payload == null) {
      onItemRangeChanged(positionStart, itemCount);
    } else {
      Log.d(tag,
          "Item range changed with payloads. Start: " + positionStart + " Count: " + itemCount);
    }
  }

  @Override
  public void onItemRangeInserted(int positionStart, int itemCount) {
    Log.d(tag, "Item range inserted. Start: " + positionStart + " Count: " + itemCount);
  }

  @Override
  public void onItemRangeRemoved(int positionStart, int itemCount) {
    Log.d(tag, "Item range removed. Start: " + positionStart + " Count: " + itemCount);
  }

  @Override
  public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
    Log.d(tag, "Item moved. From: " + fromPosition + " To: " + toPosition);
  }
}
