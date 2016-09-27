package com.airbnb.epoxy;

import android.support.annotation.LayoutRes;
import java.lang.Object;
import java.lang.Override;

/**
 * Generated file. Do not modify!
 */
public class BasicModelWithAttribute_ extends BasicModelWithAttribute {
  public BasicModelWithAttribute_() {
    super();
  }

  public BasicModelWithAttribute_ value(int value) {
    this.value = value;
    return this;
  }

  public int value() {
    return value;
  }

  @Override
  public BasicModelWithAttribute_ id(long id) {
    super.id(id);
    return this;
  }

  @Override
  public BasicModelWithAttribute_ layout(@LayoutRes int layoutRes) {
    super.layout(layoutRes);
    return this;
  }

  @Override
  public BasicModelWithAttribute_ show() {
    super.show();
    return this;
  }

  @Override
  public BasicModelWithAttribute_ show(boolean show) {
    super.show(show);
    return this;
  }

  @Override
  public BasicModelWithAttribute_ hide() {
    super.hide();
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof BasicModelWithAttribute_)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    BasicModelWithAttribute_ that = (BasicModelWithAttribute_) o;
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
}
