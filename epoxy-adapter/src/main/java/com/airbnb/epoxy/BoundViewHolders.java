/*
 * Copyright (C) 2016 Airbnb, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.airbnb.epoxy;

import android.support.annotation.Nullable;
import android.support.v4.util.LongSparseArray;

import java.util.Iterator;
import java.util.NoSuchElementException;

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
