package com.airbnb.epoxy;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.annotation.PluralsRes;
import android.support.annotation.StringRes;

import java.util.Arrays;

public class StringAttributeData {
  private final boolean hasDefault;
  @Nullable private final CharSequence defaultString;

  @Nullable private CharSequence string;
  @StringRes private int stringRes;
  @PluralsRes private int pluralRes;
  private int quantity;
  @Nullable private Object[] formatArgs;

  public StringAttributeData() {
    hasDefault = false;
    defaultString = null;
  }

  public StringAttributeData(@Nullable String defaultString) {
    hasDefault = true;
    this.defaultString = defaultString;
    string = defaultString;
  }

  public void setValue(@Nullable CharSequence string) {
    this.string = string;
    stringRes = 0;
    pluralRes = 0;
  }

  public void setValue(@StringRes int stringRes) {
    setValue(stringRes, null);
  }

  public void setValue(@StringRes int stringRes, @Nullable Object[] formatArgs) {
    if (stringRes > 0) {
      this.stringRes = stringRes;
      this.formatArgs = formatArgs;
      string = null;
      pluralRes = 0;
    } else {
      handleInvalidStringRes(stringRes);
    }
  }

  private void handleInvalidStringRes(int stringRes) {
    if (stringRes == 0) {
      if (hasDefault) {
        setValue(defaultString);
      } else {
        throw new IllegalArgumentException("0 is an invalid value for required strings.");
      }
    } else {
      throw new IllegalArgumentException("String resource cannot be negative: " + stringRes);
    }
  }

  public void setValue(@PluralsRes int pluralRes, int quantity, @Nullable Object[] formatArgs) {
    if (pluralRes > 0) {
      this.pluralRes = pluralRes;
      this.quantity = quantity;
      this.formatArgs = formatArgs;
      string = null;
      stringRes = 0;
    } else {
      handleInvalidStringRes(pluralRes);
    }
  }

  public CharSequence toString(Context context) {
    if (pluralRes > 0) {
      if (formatArgs != null) {
        return context.getResources().getQuantityString(pluralRes, quantity, formatArgs);
      } else {
        return context.getResources().getQuantityString(pluralRes, quantity);
      }
    } else if (stringRes > 0) {
      if (formatArgs != null) {
        return context.getResources().getString(stringRes, formatArgs);
      } else {
        return context.getResources().getText(stringRes);
      }
    } else {
      return string;
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof StringAttributeData)) {
      return false;
    }

    StringAttributeData that = (StringAttributeData) o;

    if (stringRes != that.stringRes) {
      return false;
    }
    if (pluralRes != that.pluralRes) {
      return false;
    }
    if (quantity != that.quantity) {
      return false;
    }
    if (string != null ? !string.equals(that.string) : that.string != null) {
      return false;
    }

    return Arrays.equals(formatArgs, that.formatArgs);
  }

  @Override
  public int hashCode() {
    int result = string != null ? string.hashCode() : 0;
    result = 31 * result + stringRes;
    result = 31 * result + pluralRes;
    result = 31 * result + quantity;
    result = 31 * result + Arrays.hashCode(formatArgs);
    return result;
  }
}
