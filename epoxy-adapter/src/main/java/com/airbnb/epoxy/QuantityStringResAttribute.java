package com.airbnb.epoxy;

import android.content.Context;

import java.util.Arrays;

import androidx.annotation.Nullable;
import androidx.annotation.PluralsRes;

public class QuantityStringResAttribute {
  @PluralsRes private final int id;
  private final int quantity;
  @Nullable private final Object[] formatArgs;

  public QuantityStringResAttribute(@PluralsRes int id, int quantity,
      @Nullable Object[] formatArgs) {
    this.quantity = quantity;
    this.id = id;
    this.formatArgs = formatArgs;
  }

  public QuantityStringResAttribute(int id, int quantity) {
    this(id, quantity, null);
  }

  @PluralsRes
  public int getId() {
    return id;
  }

  public int getQuantity() {
    return quantity;
  }

  @Nullable
  public Object[] getFormatArgs() {
    return formatArgs;
  }

  public CharSequence toString(Context context) {
    if (formatArgs == null || formatArgs.length == 0) {
      return context.getResources().getQuantityString(id, quantity);
    } else {
      return context.getResources().getQuantityString(id, quantity, formatArgs);
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof QuantityStringResAttribute)) {
      return false;
    }

    QuantityStringResAttribute that = (QuantityStringResAttribute) o;

    if (id != that.id) {
      return false;
    }
    if (quantity != that.quantity) {
      return false;
    }
    // Probably incorrect - comparing Object[] arrays with Arrays.equals
    return Arrays.equals(formatArgs, that.formatArgs);
  }

  @Override
  public int hashCode() {
    int result = id;
    result = 31 * result + quantity;
    result = 31 * result + Arrays.hashCode(formatArgs);
    return result;
  }
}
