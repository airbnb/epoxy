
package com.airbnb.epoxy;

import android.support.annotation.IntDef;
import android.support.v4.util.Pools;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

/** Defines an operation that makes a change to the epoxy model list. */
class UpdateOp {
  private static final Pools.Pool<UpdateOp> UPDATE_OP_POOL = new Pools.SimplePool<>(10);

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
    UpdateOp op = UPDATE_OP_POOL.acquire();
    if (op == null) {
      op = new UpdateOp();
    }

    op.type = type;
    op.positionStart = positionStart;
    op.itemCount = itemCount;

    return op;
  }

  static UpdateOp instance(@Type int type, int positionStart) {
    return instance(type, positionStart, 1);
  }

  static void release(List<UpdateOp> diff) {
    for (UpdateOp updateOp : diff) {
      UPDATE_OP_POOL.release(updateOp);
    }
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
