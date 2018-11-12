package com.airbnb.epoxy;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import androidx.annotation.NonNull;

/**
 * Used by our {@link EpoxyAdapter} to track models. It simply wraps ArrayList and notifies an
 * observer when remove or insertion operations are done on the list. This allows us to optimize
 * diffing since we have a knowledge of what changed in the list.
 */
class ModelList extends ArrayList<EpoxyModel<?>> {

  ModelList(int expectedModelCount) {
    super(expectedModelCount);
  }

  ModelList() {

  }

  interface ModelListObserver {
    void onItemRangeInserted(int positionStart, int itemCount);
    void onItemRangeRemoved(int positionStart, int itemCount);
  }

  private boolean notificationsPaused;
  private ModelListObserver observer;

  void pauseNotifications() {
    if (notificationsPaused) {
      throw new IllegalStateException("Notifications already paused");
    }
    notificationsPaused = true;
  }

  void resumeNotifications() {
    if (!notificationsPaused) {
      throw new IllegalStateException("Notifications already resumed");
    }
    notificationsPaused = false;
  }

  void setObserver(ModelListObserver observer) {
    this.observer = observer;
  }

  private void notifyInsertion(int positionStart, int itemCount) {
    if (!notificationsPaused && observer != null) {
      observer.onItemRangeInserted(positionStart, itemCount);
    }
  }

  private void notifyRemoval(int positionStart, int itemCount) {
    if (!notificationsPaused && observer != null) {
      observer.onItemRangeRemoved(positionStart, itemCount);
    }
  }

  @Override
  public EpoxyModel<?> set(int index, EpoxyModel<?> element) {
    EpoxyModel<?> previousModel = super.set(index, element);

    if (previousModel.id() != element.id()) {
      notifyRemoval(index, 1);
      notifyInsertion(index, 1);
    }

    return previousModel;
  }

  @Override
  public boolean add(EpoxyModel<?> epoxyModel) {
    notifyInsertion(size(), 1);
    return super.add(epoxyModel);
  }

  @Override
  public void add(int index, EpoxyModel<?> element) {
    notifyInsertion(index, 1);
    super.add(index, element);
  }

  @Override
  public boolean addAll(Collection<? extends EpoxyModel<?>> c) {
    notifyInsertion(size(), c.size());
    return super.addAll(c);
  }

  @Override
  public boolean addAll(int index, Collection<? extends EpoxyModel<?>> c) {
    notifyInsertion(index, c.size());
    return super.addAll(index, c);
  }

  @Override
  public EpoxyModel<?> remove(int index) {
    notifyRemoval(index, 1);
    return super.remove(index);
  }

  @Override
  public boolean remove(Object o) {
    int index = indexOf(o);

    if (index == -1) {
      return false;
    }

    notifyRemoval(index, 1);
    super.remove(index);
    return true;
  }

  @Override
  public void clear() {
    if (!isEmpty()) {
      notifyRemoval(0, size());
      super.clear();
    }
  }

  @Override
  protected void removeRange(int fromIndex, int toIndex) {
    if (fromIndex == toIndex) {
      return;
    }

    notifyRemoval(fromIndex, toIndex - fromIndex);
    super.removeRange(fromIndex, toIndex);
  }

  @Override
  public boolean removeAll(Collection<?> collection) {
    // Using this implementation from the Android ArrayList since the Java 1.8 ArrayList
    // doesn't call through to remove. Calling through to remove lets us leverage the notification
    // done there
    boolean result = false;
    Iterator<?> it = iterator();
    while (it.hasNext()) {
      if (collection.contains(it.next())) {
        it.remove();
        result = true;
      }
    }
    return result;
  }

  @Override
  public boolean retainAll(Collection<?> collection) {
    // Using this implementation from the Android ArrayList since the Java 1.8 ArrayList
    // doesn't call through to remove. Calling through to remove lets us leverage the notification
    // done there
    boolean result = false;
    Iterator<?> it = iterator();
    while (it.hasNext()) {
      if (!collection.contains(it.next())) {
        it.remove();
        result = true;
      }
    }
    return result;
  }

  @NonNull
  @Override
  public Iterator<EpoxyModel<?>> iterator() {
    return new Itr();
  }

  /**
   * An Iterator implementation that calls through to the parent list's methods for modification.
   * Some implementations, like the Android ArrayList.ArrayListIterator class, modify the list data
   * directly instead of calling into the parent list's methods. We need the implementation to call
   * the parent methods so that the proper notifications are done.
   */
  private class Itr implements Iterator<EpoxyModel<?>> {
    int cursor;       // index of next element to return
    int lastRet = -1; // index of last element returned; -1 if no such
    int expectedModCount = modCount;

    public boolean hasNext() {
      return cursor != size();
    }

    @SuppressWarnings("unchecked")
    public EpoxyModel<?> next() {
      checkForComodification();
      int i = cursor;
      cursor = i + 1;
      lastRet = i;
      return ModelList.this.get(i);
    }

    public void remove() {
      if (lastRet < 0) {
        throw new IllegalStateException();
      }
      checkForComodification();

      try {
        ModelList.this.remove(lastRet);
        cursor = lastRet;
        lastRet = -1;
        expectedModCount = modCount;
      } catch (IndexOutOfBoundsException ex) {
        throw new ConcurrentModificationException();
      }
    }

    final void checkForComodification() {
      if (modCount != expectedModCount) {
        throw new ConcurrentModificationException();
      }
    }
  }

  @NonNull
  @Override
  public ListIterator<EpoxyModel<?>> listIterator() {
    return new ListItr(0);
  }

  @NonNull
  @Override
  public ListIterator<EpoxyModel<?>> listIterator(int index) {
    return new ListItr(index);
  }

  /**
   * A ListIterator implementation that calls through to the parent list's methods for modification.
   * Some implementations may modify the list data directly instead of calling into the parent
   * list's methods. We need the implementation to call the parent methods so that the proper
   * notifications are done.
   */
  private class ListItr extends Itr implements ListIterator<EpoxyModel<?>> {
    ListItr(int index) {
      cursor = index;
    }

    public boolean hasPrevious() {
      return cursor != 0;
    }

    public int nextIndex() {
      return cursor;
    }

    public int previousIndex() {
      return cursor - 1;
    }

    @SuppressWarnings("unchecked")
    public EpoxyModel<?> previous() {
      checkForComodification();
      int i = cursor - 1;
      if (i < 0) {
        throw new NoSuchElementException();
      }

      cursor = i;
      lastRet = i;
      return ModelList.this.get(i);
    }

    public void set(EpoxyModel<?> e) {
      if (lastRet < 0) {
        throw new IllegalStateException();
      }
      checkForComodification();

      try {
        ModelList.this.set(lastRet, e);
      } catch (IndexOutOfBoundsException ex) {
        throw new ConcurrentModificationException();
      }
    }

    public void add(EpoxyModel<?> e) {
      checkForComodification();

      try {
        int i = cursor;
        ModelList.this.add(i, e);
        cursor = i + 1;
        lastRet = -1;
        expectedModCount = modCount;
      } catch (IndexOutOfBoundsException ex) {
        throw new ConcurrentModificationException();
      }
    }
  }

  @NonNull
  @Override
  public List<EpoxyModel<?>> subList(int start, int end) {
    if (start >= 0 && end <= size()) {
      if (start <= end) {
        return new SubList(this, start, end);
      }
      throw new IllegalArgumentException();
    }
    throw new IndexOutOfBoundsException();
  }

  /**
   * A SubList implementation from Android's AbstractList class. It's copied here to make sure the
   * implementation doesn't change, since some implementations, like the Java 1.8 ArrayList.SubList
   * class, modify the list data directly instead of calling into the parent list's methods. We need
   * the implementation to call the parent methods so that the proper notifications are done.
   */
  private static class SubList extends AbstractList<EpoxyModel<?>> {
    private final ModelList fullList;
    private int offset;
    private int size;

    private static final class SubListIterator implements ListIterator<EpoxyModel<?>> {
      private final SubList subList;
      private final ListIterator<EpoxyModel<?>> iterator;
      private int start;
      private int end;

      SubListIterator(ListIterator<EpoxyModel<?>> it, SubList list, int offset, int length) {
        iterator = it;
        subList = list;
        start = offset;
        end = start + length;
      }

      public void add(EpoxyModel<?> object) {
        iterator.add(object);
        subList.sizeChanged(true);
        end++;
      }

      public boolean hasNext() {
        return iterator.nextIndex() < end;
      }

      public boolean hasPrevious() {
        return iterator.previousIndex() >= start;
      }

      public EpoxyModel<?> next() {
        if (iterator.nextIndex() < end) {
          return iterator.next();
        }
        throw new NoSuchElementException();
      }

      public int nextIndex() {
        return iterator.nextIndex() - start;
      }

      public EpoxyModel<?> previous() {
        if (iterator.previousIndex() >= start) {
          return iterator.previous();
        }
        throw new NoSuchElementException();
      }

      public int previousIndex() {
        int previous = iterator.previousIndex();
        if (previous >= start) {
          return previous - start;
        }
        return -1;
      }

      public void remove() {
        iterator.remove();
        subList.sizeChanged(false);
        end--;
      }

      public void set(EpoxyModel<?> object) {
        iterator.set(object);
      }
    }

    SubList(ModelList list, int start, int end) {
      fullList = list;
      modCount = fullList.modCount;
      offset = start;
      size = end - start;
    }

    @Override
    public void add(int location, EpoxyModel<?> object) {
      if (modCount == fullList.modCount) {
        if (location >= 0 && location <= size) {
          fullList.add(location + offset, object);
          size++;
          modCount = fullList.modCount;
        } else {
          throw new IndexOutOfBoundsException();
        }
      } else {
        throw new ConcurrentModificationException();
      }
    }

    @Override
    public boolean addAll(int location, Collection<? extends EpoxyModel<?>> collection) {
      if (modCount == fullList.modCount) {
        if (location >= 0 && location <= size) {
          boolean result = fullList.addAll(location + offset, collection);
          if (result) {
            size += collection.size();
            modCount = fullList.modCount;
          }
          return result;
        }
        throw new IndexOutOfBoundsException();
      }
      throw new ConcurrentModificationException();
    }

    @Override
    public boolean addAll(@NonNull Collection<? extends EpoxyModel<?>> collection) {
      if (modCount == fullList.modCount) {
        boolean result = fullList.addAll(offset + size, collection);
        if (result) {
          size += collection.size();
          modCount = fullList.modCount;
        }
        return result;
      }
      throw new ConcurrentModificationException();
    }

    @Override
    public EpoxyModel<?> get(int location) {
      if (modCount == fullList.modCount) {
        if (location >= 0 && location < size) {
          return fullList.get(location + offset);
        }
        throw new IndexOutOfBoundsException();
      }
      throw new ConcurrentModificationException();
    }

    @NonNull
    @Override
    public Iterator<EpoxyModel<?>> iterator() {
      return listIterator(0);
    }

    @NonNull
    @Override
    public ListIterator<EpoxyModel<?>> listIterator(int location) {
      if (modCount == fullList.modCount) {
        if (location >= 0 && location <= size) {
          return new SubListIterator(fullList.listIterator(location + offset), this, offset, size);
        }
        throw new IndexOutOfBoundsException();
      }
      throw new ConcurrentModificationException();
    }

    @Override
    public EpoxyModel<?> remove(int location) {
      if (modCount == fullList.modCount) {
        if (location >= 0 && location < size) {
          EpoxyModel<?> result = fullList.remove(location + offset);
          size--;
          modCount = fullList.modCount;
          return result;
        }
        throw new IndexOutOfBoundsException();
      }
      throw new ConcurrentModificationException();
    }

    @Override
    protected void removeRange(int start, int end) {
      if (start != end) {
        if (modCount == fullList.modCount) {
          fullList.removeRange(start + offset, end + offset);
          size -= end - start;
          modCount = fullList.modCount;
        } else {
          throw new ConcurrentModificationException();
        }
      }
    }

    @Override
    public EpoxyModel<?> set(int location, EpoxyModel<?> object) {
      if (modCount == fullList.modCount) {
        if (location >= 0 && location < size) {
          return fullList.set(location + offset, object);
        }
        throw new IndexOutOfBoundsException();
      }
      throw new ConcurrentModificationException();
    }

    @Override
    public int size() {
      if (modCount == fullList.modCount) {
        return size;
      }
      throw new ConcurrentModificationException();
    }

    void sizeChanged(boolean increment) {
      if (increment) {
        size++;
      } else {
        size--;
      }
      modCount = fullList.modCount;
    }
  }
}
