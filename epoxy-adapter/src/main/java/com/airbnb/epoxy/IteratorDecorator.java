package com.airbnb.epoxy;

import java.util.Iterator;
import java.util.function.Consumer;

/**
 * Created by Thilo on 4/10/2017.
 */

abstract class IteratorDecorator<E> implements Iterator<E> {
  protected E current = null;
  private Iterator<E> it;

  public IteratorDecorator(Iterator<E> decorated) {
    this.it = decorated;
  }

  @Override
  public boolean hasNext() {
    return it.hasNext();
  }

  @Override
  public void remove() {
    it.remove();
  }

  @Override
  public E next() {
    current = it.next();
    return current;
  }

  @Override
  public void forEachRemaining(Consumer action) {
    it.forEachRemaining(action);
  }
}