package com.airbnb.epoxy;

import android.content.Context;
import android.support.annotation.StringRes;

import java.util.Arrays;

public class StringResAttribute {
  @StringRes private final int id;
  private final Object[] formatArgs;

  public StringResAttribute(@StringRes int id, Object[] formatArgs) {
    if (id < 0) {
      // A 0 value is ignored since the generated code handles 0 as null/default
      throw new IllegalArgumentException("Id cannot be negative");
    }

    this.id = id;
    this.formatArgs = formatArgs;
  }

  @StringRes
  public int getId() {
    return id;
  }

  public Object[] getFormatArgs() {
    return formatArgs;
  }

  public String toString(Context context) {
    return context.getResources().getString(id, formatArgs);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof StringResAttribute)) {
      return false;
    }

    StringResAttribute that = (StringResAttribute) o;

    if (id != that.id) {
      return false;
    }
    // Probably incorrect - comparing Object[] arrays with Arrays.equals
    return Arrays.equals(formatArgs, that.formatArgs);
  }

  @Override
  public int hashCode() {
    int result = id;
    result = 31 * result + Arrays.hashCode(formatArgs);
    return result;
  }
}
