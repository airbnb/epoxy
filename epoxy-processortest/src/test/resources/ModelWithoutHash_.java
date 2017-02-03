package com.airbnb.epoxy;

import android.support.annotation.LayoutRes;
import java.lang.CharSequence;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;

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

  public ModelWithoutHash_ value3(String value3) {
    this.value3 = value3;
    return this;
  }

  public String value3() {
    return value3;
  }

  @Override
  public ModelWithoutHash_ id(long id) {
    super.id(id);
    return this;
  }

  @Override
  public ModelWithoutHash_ id(CharSequence key) {
    super.id(key);
    return this;
  }

  @Override
  public ModelWithoutHash_ id(CharSequence key, long id) {
    super.id(key, id);
    return this;
  }

  @Override
  public ModelWithoutHash_ layout(@LayoutRes int arg0) {
    super.layout(arg0);
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
  public ModelWithoutHash_ reset() {
    this.value2 = 0;
    this.value = 0;
    this.value3 = null;
    super.reset();
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
    if (value3 != null && that.value3 == null || value3 == null && that.value3 != null) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + value;
    result = 31 * result + (value3 != null ? 1 : 0);
    return result;
  }

  @Override
  public String toString() {
    return "ModelWithoutHash_{" +
        "value2=" + value2 +
        ", value=" + value +
        ", value3=" + value3 +
        "}" + super.toString();
  }
}
