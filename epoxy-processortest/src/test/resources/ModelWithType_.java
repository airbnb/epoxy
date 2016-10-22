package com.airbnb.epoxy;

import android.support.annotation.LayoutRes;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;

/**
 * Generated file. Do not modify! */
public class ModelWithType_<T extends String> extends ModelWithType<T> {
  public ModelWithType_() {
    super();
  }

  public ModelWithType_<T> value(int value) {
    this.value = value;
    return this;
  }

  public int value() {
    return value;
  }

  @Override
  public ModelWithType_<T> id(long id) {
    super.id(id);
    return this;
  }

  @Override
  public ModelWithType_<T> layout(@LayoutRes int layout) {
    super.layout(layout);
    return this;
  }

  @Override
  public ModelWithType_<T> show() {
    super.show();
    return this;
  }

  @Override
  public ModelWithType_<T> show(boolean show) {
    super.show(show);
    return this;
  }

  @Override
  public ModelWithType_<T> hide() {
    super.hide();
    return this;
  }

  public void reset() {
    this.value = 0;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof ModelWithType_)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    ModelWithType_ that = (ModelWithType_) o;
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
    return "ModelWithType_{" +
        "value=" + value +
        "}" + super.toString();
  }
}
