
package com.airbnb.epoxy;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;

import androidx.annotation.IntDef;
import androidx.annotation.Nullable;

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
  ArrayList<EpoxyModel<?>> payloads;

  private UpdateOp() {
  }

  static UpdateOp instance(@Type int type, int positionStart, int itemCount,
      @Nullable EpoxyModel<?> payload) {
    UpdateOp op = new UpdateOp();

    op.type = type;
    op.positionStart = positionStart;
    op.itemCount = itemCount;

    op.addPayload(payload);

    return op;
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

  void addPayload(@Nullable EpoxyModel<?> payload) {
    if (payload == null) {
      return;
    }

    if (payloads == null) {
      // In most cases this won't be a batch update so we can expect just one payload
      payloads = new ArrayList<>(1);
    } else if (payloads.size() == 1) {
      // There are multiple payloads, but we don't know how big the batch will end up being.
      // To prevent resizing the list many times we bump it to a medium size
      payloads.ensureCapacity(10);
    }

    payloads.add(payload);
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
