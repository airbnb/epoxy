package com.airbnb.epoxy;

import android.support.annotation.LayoutRes;
import java.lang.Object;
import java.lang.Override;

/**
 * Generated file. Do not modify! */
public class ModelWithFinalField_ extends ModelWithFinalField {
  public ModelWithFinalField_(long param1, int param2) {
    super(param1, param2);
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
  public ModelWithFinalField_ layout(@LayoutRes int layoutRes) {
    super.layout(layoutRes);
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
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof ModelWithFinalField)) {
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
}
