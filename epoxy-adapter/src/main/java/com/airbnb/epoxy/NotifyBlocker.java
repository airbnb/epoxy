package com.airbnb.epoxy;

import android.support.v7.util.ListUpdateCallback;
import android.support.v7.widget.RecyclerView.AdapterDataObserver;

/**
 * We don't allow any data change notifications except the ones done though diffing. Forcing
 * changes to happen through diffing reduces the chance for developer error when implementing an
 * adapter.
 * <p>
 * This observer throws upon any changes done outside of diffing.
 */
class NotifyBlocker extends AdapterDataObserver implements ListUpdateCallback {

  private final EpoxyControllerAdapter adapter;

  NotifyBlocker(EpoxyControllerAdapter adapter) {
    this.adapter = adapter;
  }

  private boolean changesAllowed;

  void allowChanges() {
    changesAllowed = true;
  }

  void blockChanges() {
    changesAllowed = false;
  }

  @Override
  public void onChanged() {
    if (!changesAllowed) {
      throw new IllegalStateException(
          "You cannot notify item changes directly. Call `requestModelBuild` instead.");
    }
  }

  @Override
  public void onItemRangeChanged(int positionStart, int itemCount) {
    onChanged();
  }

  @Override
  public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
    onChanged();
  }

  @Override
  public void onItemRangeInserted(int positionStart, int itemCount) {
    onChanged();
  }

  @Override
  public void onItemRangeRemoved(int positionStart, int itemCount) {
    onChanged();
  }

  @Override
  public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
    onChanged();
  }

  /** {@inheritDoc} */
  @Override
  public void onInserted(int position, int count) {
    allowChanges();
    adapter.notifyItemRangeInserted(position, count);
    blockChanges();
  }

  /** {@inheritDoc} */
  @Override
  public void onRemoved(int position, int count) {
    allowChanges();
    adapter.notifyItemRangeRemoved(position, count);
    blockChanges();
  }

  /** {@inheritDoc} */
  @Override
  public void onMoved(int fromPosition, int toPosition) {
    allowChanges();
    adapter.notifyItemMoved(fromPosition, toPosition);
    blockChanges();
  }

  /** {@inheritDoc} */
  @Override
  public void onChanged(int position, int count, Object payload) {
    allowChanges();
    adapter.notifyItemRangeChanged(position, count, payload);
    blockChanges();
  }
}
