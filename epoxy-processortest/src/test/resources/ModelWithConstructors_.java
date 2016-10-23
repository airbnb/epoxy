package com.airbnb.epoxy;

import android.support.annotation.LayoutRes;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;

/**
 * Generated file. Do not modify! */
public class ModelWithConstructors_ extends ModelWithConstructors {
  public ModelWithConstructors_(long param1, int param2) {
    super(param1, param2);
  }

  public ModelWithConstructors_(int param1) {
    super(param1);
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
  public ModelWithConstructors_ layout(@LayoutRes int layout) {
    super.layout(layout);
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
    super.reset();
    this.valueInt = 0;
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
