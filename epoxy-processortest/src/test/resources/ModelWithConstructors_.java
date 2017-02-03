package com.airbnb.epoxy;

import android.support.annotation.LayoutRes;
import java.lang.CharSequence;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;

/**
 * Generated file. Do not modify! */
public class ModelWithConstructors_ extends ModelWithConstructors {
  public ModelWithConstructors_(long id, int valueInt) {
    super(id, valueInt);
  }

  public ModelWithConstructors_(int valueInt) {
    super(valueInt);
  }

  public ModelWithConstructors_() {
    super();
  }

  public ModelWithConstructors_ valueInt(int valueInt) {
    this.valueInt = valueInt;
    return this;
  }

  public int valueInt() {
    return valueInt;
  }

  @Override
  public ModelWithConstructors_ id(long id) {
    super.id(id);
    return this;
  }

  @Override
  public ModelWithConstructors_ id(CharSequence key) {
    super.id(key);
    return this;
  }

  @Override
  public ModelWithConstructors_ id(CharSequence key, long id) {
    super.id(key, id);
    return this;
  }

  @Override
  public ModelWithConstructors_ layout(@LayoutRes int arg0) {
    super.layout(arg0);
    return this;
  }

  @Override
  public ModelWithConstructors_ show() {
    super.show();
    return this;
  }

  @Override
  public ModelWithConstructors_ show(boolean show) {
    super.show(show);
    return this;
  }

  @Override
  public ModelWithConstructors_ hide() {
    super.hide();
    return this;
  }

  @Override
  public ModelWithConstructors_ reset() {
    this.valueInt = 0;
    super.reset();
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof ModelWithConstructors_)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    ModelWithConstructors_ that = (ModelWithConstructors_) o;
    if (valueInt != that.valueInt) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + valueInt;
    return result;
  }

  @Override
  public String toString() {
    return "ModelWithConstructors_{" +
        "valueInt=" + valueInt +
        "}" + super.toString();
  }
}
