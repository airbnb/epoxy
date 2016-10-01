package com.airbnb.epoxy;

import com.airbnb.epoxy.ModelList.ModelListObserver;

/**
 * Listens for changes to the model array list in the adapter, storing and batching insertions and
 * removals to that list. This knowledge helps us be more efficient with the differ, avoiding diff
 * computations when not necessary and allowing us to take some shortcuts.
 * <p>
 * This isn't a silver bullet, as users could clear the list and add back the same objects resulting
 * in no net change, but this would record a batch removal and batch insert. Thus when we record
 * both removals and insertions we still need to run a full diff. However, if the list doesn't
 * change, or only changes due to insertions or removals, we can skip doing a full diff.
 */
class DifferModelListObserver extends UpdateOpHelper implements ModelListObserver {

  @Override
  public void onItemRangeInserted(int positionStart, int itemCount) {
    add(positionStart, itemCount);
  }

  @Override
  public void onItemRangeRemoved(int positionStart, int itemCount) {
    remove(positionStart, itemCount);
  }

  boolean hasNoChanges() {
    return !hasInsertions() && !hasRemovals();
  }

  boolean hasOnlyInsertions() {
    return !hasRemovals() && hasInsertions();
  }

  boolean hasOnlyRemovals() {
    return !hasInsertions() && hasRemovals();
  }
}
