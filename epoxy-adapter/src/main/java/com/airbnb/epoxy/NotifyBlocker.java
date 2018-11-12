package com.airbnb.epoxy;

import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver;

/**
 * We don't allow any data change notifications except the ones done though diffing. Forcing
 * changes to happen through diffing reduces the chance for developer error when implementing an
 * adapter.
 * <p>
 * This observer throws upon any changes done outside of diffing.
 */
class NotifyBlocker extends AdapterDataObserver {

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
}
