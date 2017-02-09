package com.airbnb.epoxy;

import android.support.annotation.LayoutRes;
import java.lang.CharSequence;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;

/**
 * Generated file. Do not modify!
 */
public class ModelForRProcessingTest_ extends ModelForRProcessingTest {
  public ModelForRProcessingTest_() {
    super();
  }

  public ModelForRProcessingTest_ value(int value) {
    this.value = value;
    return this;
  }

  public int value() {
    return value;
  }

  @Override
  public ModelForRProcessingTest_ id(long id) {
    super.id(id);
    return this;
  }

  @Override
  public ModelForRProcessingTest_ id(CharSequence key) {
    super.id(key);
    return this;
  }

  @Override
  public ModelForRProcessingTest_ id(CharSequence key, long id) {
    super.id(key, id);
    return this;
  }

  @Override
  public ModelForRProcessingTest_ layout(@LayoutRes int arg0) {
    super.layout(arg0);
    return this;
  }

  @Override
  public ModelForRProcessingTest_ show() {
    super.show();
    return this;
  }

  @Override
  public ModelForRProcessingTest_ show(boolean show) {
    super.show(show);
    return this;
  }

  @Override
  public ModelForRProcessingTest_ hide() {
    super.hide();
    return this;
  }

  @Override
  @LayoutRes
  protected int getDefaultLayout() {
    return R.layout.res;
  }

  @Override
  public ModelForRProcessingTest_ reset() {
    this.value = 0;
    super.reset();
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof ModelForRProcessingTest_)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    ModelForRProcessingTest_ that = (ModelForRProcessingTest_) o;
    if (value != that.value) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + value;
    return result;
  }

  @Override
  public String toString() {
    return "ModelForRProcessingTest_{" +
        "value=" + value +
        "}" + super.toString();
  }
}
