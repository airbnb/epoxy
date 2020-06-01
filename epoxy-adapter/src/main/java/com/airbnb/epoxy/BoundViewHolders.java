
package com.airbnb.epoxy;

import java.util.Iterator;
import java.util.NoSuchElementException;

import androidx.annotation.Nullable;
import androidx.collection.LongSparseArray;

/** Helper class for keeping track of {@link EpoxyViewHolder}s that are currently bound. */
@SuppressWarnings("WeakerAccess")
public class BoundViewHolders implements Iterable<EpoxyViewHolder> {
  private final LongSparseArray<EpoxyViewHolder> holders = new LongSparseArray<>();

  @Nullable
  public EpoxyViewHolder get(EpoxyViewHolder holder) {
    return holders.get(holder.getItemId());
  }

  public void put(EpoxyViewHolder holder) {
    holders.put(holder.getItemId(), holder);
  }

  public void remove(EpoxyViewHolder holder) {
    holders.remove(holder.getItemId());
  }

  public int size() {
    return holders.size();
  }

  @Override
  public Iterator<EpoxyViewHolder> iterator() {
    return new HolderIterator();
  }

  @Nullable
  public EpoxyViewHolder getHolderForModel(EpoxyModel<?> model) {
    return holders.get(model.id());
  }

  private class HolderIterator implements Iterator<EpoxyViewHolder> {
    private int position = 0;

    @Override
    public boolean hasNext() {
      return position < holders.size();
    }

    @Override
    public EpoxyViewHolder next() {
      if (!hasNext()) {
        throw new NoSuchElementException();
      }
      return holders.valueAt(position++);
    }

    @Override
    public void remove() {
      throw new UnsupportedOperationException();
    }
  }
}
