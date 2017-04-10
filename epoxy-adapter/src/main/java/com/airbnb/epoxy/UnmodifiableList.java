package com.airbnb.epoxy;

import android.support.annotation.NonNull;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

class UnmodifiableList<E> implements List<E> {

  final List<? extends E> list;

  UnmodifiableList(List<? extends E> list) {
    this.list = list;
  }

  public int size() {
    return list.size();
  }

  public boolean isEmpty() {
    return list.isEmpty();
  }

  public boolean contains(Object o) {
    return list.contains(o);
  }

  @NonNull
  public Object[] toArray() {
    return list.toArray();
  }

  @NonNull
  public <T> T[] toArray(@NonNull T[] a) {
    return list.toArray(a);
  }

  public String toString() {
    return list.toString();
  }

  @NonNull
  public Iterator<E> iterator() {
    return new Iterator<E>() {
      private final Iterator<? extends E> i = list.iterator();

      public boolean hasNext() {
        return i.hasNext();
      }

      public E next() {
        return i.next();
      }

      public void remove() {
        throw new UnsupportedOperationException();
      }
    };
  }

  public boolean add(E e) {
    throw new UnsupportedOperationException();
  }

  public boolean remove(Object o) {
    throw new UnsupportedOperationException();
  }

  public boolean containsAll(@NonNull Collection<?> coll) {
    return list.containsAll(coll);
  }

  public boolean addAll(Collection<? extends E> coll) {
    throw new UnsupportedOperationException();
  }

  public boolean removeAll(@NonNull Collection<?> coll) {
    throw new UnsupportedOperationException();
  }

  public boolean retainAll(@NonNull Collection<?> coll) {
    throw new UnsupportedOperationException();
  }

  public void clear() {
    throw new UnsupportedOperationException();
  }

  public boolean equals(Object o) {
    return o == this || list.equals(o);
  }

  public int hashCode() {
    return list.hashCode();
  }

  public E get(int index) {
    return list.get(index);
  }

  public E set(int index, E element) {
    throw new UnsupportedOperationException();
  }

  public void add(int index, E element) {
    throw new UnsupportedOperationException();
  }

  public E remove(int index) {
    throw new UnsupportedOperationException();
  }

  public int indexOf(Object o) {
    return list.indexOf(o);
  }

  public int lastIndexOf(Object o) {
    return list.lastIndexOf(o);
  }

  public boolean addAll(int index, Collection<? extends E> c) {
    throw new UnsupportedOperationException();
  }

  public ListIterator<E> listIterator() {
    return listIterator(0);
  }

  @NonNull
  public ListIterator<E> listIterator(final int index) {
    return new ListIterator<E>() {
      private final ListIterator<? extends E> i
          = list.listIterator(index);

      public boolean hasNext() {
        return i.hasNext();
      }

      public E next() {
        return i.next();
      }

      public boolean hasPrevious() {
        return i.hasPrevious();
      }

      public E previous() {
        return i.previous();
      }

      public int nextIndex() {
        return i.nextIndex();
      }

      public int previousIndex() {
        return i.previousIndex();
      }

      public void remove() {
        throw new UnsupportedOperationException();
      }

      public void set(E e) {
        throw new UnsupportedOperationException();
      }

      public void add(E e) {
        throw new UnsupportedOperationException();
      }
    };
  }

  @NonNull
  public List<E> subList(int fromIndex, int toIndex) {
    return new UnmodifiableList<>(list.subList(fromIndex, toIndex));
  }
}
