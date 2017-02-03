package com.airbnb.epoxy;

import android.support.annotation.LayoutRes;
import java.lang.CharSequence;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;

/**
 * Generated file. Do not modify! */
public class ModelWithFinalField_ extends ModelWithFinalField {
  public ModelWithFinalField_(long id, int valueInt) {
    super(id, valueInt);
  }

  public int valueInt() {
    return valueInt;
  }

  @Override
  public ModelWithFinalField_ id(long id) {
    super.id(id);
    return this;
  }

  @Override
  public ModelWithFinalField_ id(CharSequence key) {
    super.id(key);
    return this;
  }

  @Override
  public ModelWithFinalField_ id(CharSequence key, long id) {
    super.id(key, id);
    return this;
  }

  @Override
  public ModelWithFinalField_ layout(@LayoutRes int arg0) {
    super.layout(arg0);
    return this;
  }

  @Override
  public ModelWithFinalField_ show() {
    super.show();
    return this;
  }

  @Override
  public ModelWithFinalField_ show(boolean show) {
    super.show(show);
    return this;
  }

  @Override
  public ModelWithFinalField_ hide() {
    super.hide();
    return this;
  }

  @Override
  public ModelWithFinalField_ reset() {
    super.reset();
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof ModelWithFinalField_)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    ModelWithFinalField_ that = (ModelWithFinalField_) o;
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
    return "ModelWithFinalField_{" +
        "valueInt=" + valueInt +
        "}" + super.toString();
  }
}
