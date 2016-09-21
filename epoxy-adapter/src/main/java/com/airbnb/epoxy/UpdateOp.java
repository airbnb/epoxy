
package com.airbnb.epoxy;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/** Defines an operation that makes a change to the epoxy model list. */
class UpdateOp {
  @IntDef({ADD, REMOVE, UPDATE, MOVE})
  @Retention(RetentionPolicy.SOURCE)
  @interface Type {
  }

  static final int ADD = 0;
  static final int REMOVE = 1;
  static final int UPDATE = 2;
  static final int MOVE = 3;

  @Type int type;
  int positionStart;
  /** Holds the target position if this is a MOVE */
  int itemCount;

  private UpdateOp() {
  }

  static UpdateOp instance(@Type int type, int positionStart, int itemCount) {
    UpdateOp op = new UpdateOp();

    op.type = type;
    op.positionStart = positionStart;
    op.itemCount = itemCount;

    return op;
  }

  static UpdateOp instance(@Type int type, int positionStart) {
    return instance(type, positionStart, 1);
  }

  /** Returns the index one past the last item in the affected range. */
  int positionEnd() {
    return positionStart + itemCount;
  }

  boolean isAfter(int position) {
    return position < positionStart;
  }

  boolean isBefore(int position) {
    return position >= positionEnd();
  }

  boolean contains(int position) {
    return position >= positionStart && position < positionEnd();
  }

  @Override
  public String toString() {
    return "UpdateOp{"
        + "type=" + type
        + ", positionStart=" + positionStart
        + ", itemCount=" + itemCount
        + '}';
  }
}
