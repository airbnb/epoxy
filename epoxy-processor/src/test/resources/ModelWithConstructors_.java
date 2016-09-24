package com.airbnb.epoxy;

import android.support.annotation.LayoutRes;
import java.lang.Object;
import java.lang.Override;

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
  public ModelWithConstructors_ layout(@LayoutRes int layoutRes) {
    super.layout(layoutRes);
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
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof ModelWithConstructors)) {
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
}
