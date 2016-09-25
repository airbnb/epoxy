package com.airbnb.epoxy;

import android.support.annotation.LayoutRes;
import java.lang.Object;
import java.lang.Override;

/**
 * Generated file. Do not modify! */
public class ModelWithoutHash_ extends ModelWithoutHash {
  public ModelWithoutHash_() {
    super();
  }

  public ModelWithoutHash_ value2(int value2) {
    this.value2 = value2;
    return this;
  }

  public int value2() {
    return value2;
  }

  public ModelWithoutHash_ value(int value) {
    this.value = value;
    return this;
  }

  public int value() {
    return value;
  }

  @Override
  public ModelWithoutHash_ id(long id) {
    super.id(id);
    return this;
  }

  @Override
  public ModelWithoutHash_ layout(@LayoutRes int layoutRes) {
    super.layout(layoutRes);
    return this;
  }

  @Override
  public ModelWithoutHash_ show() {
    super.show();
    return this;
  }

  @Override
  public ModelWithoutHash_ show(boolean show) {
    super.show(show);
    return this;
  }

  @Override
  public ModelWithoutHash_ hide() {
    super.hide();
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof ModelWithoutHash_)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    ModelWithoutHash_ that = (ModelWithoutHash_) o;
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