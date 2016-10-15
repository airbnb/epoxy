package com.airbnb.epoxy;

import android.support.annotation.LayoutRes;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;

/**
 * Generated file. Do not modify! */
public class ModelWithSuper_ extends ModelWithSuper {
  public ModelWithSuper_() {
    super();
  }

  public ModelWithSuper_ valueInt(int valueInt) {
    this.valueInt = valueInt;
    super.valueInt(valueInt);
    return this;
  }

  public int valueInt() {
    return valueInt;
  }

  @Override
  public ModelWithSuper_ id(long id) {
    super.id(id);
    return this;
  }

  @Override
  public ModelWithSuper_ layout(@LayoutRes int layout) {
    super.layout(layout);
    return this;
  }

  @Override
  public ModelWithSuper_ show() {
    super.show();
    return this;
  }

  @Override
  public ModelWithSuper_ show(boolean show) {
    super.show(show);
    return this;
  }

  @Override
  public ModelWithSuper_ hide() {
    super.hide();
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof ModelWithSuper_)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    ModelWithSuper_ that = (ModelWithSuper_) o;
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
    return "ModelWithSuper_{" +
        "valueInt=" + valueInt +
        "}";
  }
}
