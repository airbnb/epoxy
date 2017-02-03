package com.airbnb.epoxy;

import android.support.annotation.LayoutRes;
import java.lang.CharSequence;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;

/**
 * Generated file. Do not modify!
 */
public class ModelWithoutSetter_ extends ModelWithoutSetter {
  public ModelWithoutSetter_() {
    super();
  }

  public int value() {
    return value;
  }

  @Override
  public ModelWithoutSetter_ id(long id) {
    super.id(id);
    return this;
  }

  @Override
  public ModelWithoutSetter_ id(CharSequence key) {
    super.id(key);
    return this;
  }

  @Override
  public ModelWithoutSetter_ id(CharSequence key, long id) {
    super.id(key, id);
    return this;
  }

  @Override
  public ModelWithoutSetter_ layout(@LayoutRes int arg0) {
    super.layout(arg0);
    return this;
  }

  @Override
  public ModelWithoutSetter_ show() {
    super.show();
    return this;
  }

  @Override
  public ModelWithoutSetter_ show(boolean show) {
    super.show(show);
    return this;
  }

  @Override
  public ModelWithoutSetter_ hide() {
    super.hide();
    return this;
  }

  @Override
  public ModelWithoutSetter_ reset() {
    this.value = 0;
    super.reset();
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof ModelWithoutSetter_)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    ModelWithoutSetter_ that = (ModelWithoutSetter_) o;
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
    return "ModelWithoutSetter_{" +
        "value=" + value +
        "}" + super.toString();
  }
}
