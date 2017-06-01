package com.airbnb.epoxy;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.annotation.PluralsRes;
import android.support.annotation.StringRes;

import java.util.Arrays;

public class StringAttributeData {
  private CharSequence string;
  @StringRes private int stringRes;
  @PluralsRes private int pluralRes;
  private int quantity;
  @Nullable private Object[] formatArgs;

  public StringAttributeData() {
  }

  public StringAttributeData(String defaultString) {
    string = defaultString;
  }

  public void setValue(CharSequence string) {
    this.string = string;
    stringRes = 0;
    pluralRes = 0;
    formatArgs = null;
  }

  public void setValue(@StringRes int stringRes) {
    string = null;
    this.stringRes = stringRes;
    pluralRes = 0;
    formatArgs = null;
  }

  public void setValue(@StringRes int stringRes, Object[] formatArgs) {

  }

  public void setValue(@PluralsRes int pluralRes, int quantity, Object[] formatArgs) {

  }

  public String toString(Context context) {
    return context.getResources().getString(stringRes, formatArgs);
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
