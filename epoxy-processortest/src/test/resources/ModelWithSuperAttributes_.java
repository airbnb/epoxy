package com.airbnb.epoxy;

import android.support.annotation.LayoutRes;
import java.lang.CharSequence;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;

/**
 * Generated file. Do not modify! */
public class ModelWithSuperAttributes_ extends ModelWithSuperAttributes {
  public ModelWithSuperAttributes_() {
    super();
  }

  public ModelWithSuperAttributes_ superValue(int superValue) {
    this.superValue = superValue;
    return this;
  }

  public int superValue() {
    return superValue;
  }

  @Override
  public ModelWithSuperAttributes_ id(long id) {
    super.id(id);
    return this;
  }

  @Override
  public ModelWithSuperAttributes_ id(CharSequence key) {
    super.id(key);
    return this;
  }

  @Override
  public ModelWithSuperAttributes_ id(CharSequence key, long id) {
    super.id(key, id);
    return this;
  }

  @Override
  public ModelWithSuperAttributes_ layout(@LayoutRes int arg0) {
    super.layout(arg0);
    return this;
  }

  @Override
  public ModelWithSuperAttributes_ show() {
    super.show();
    return this;
  }

  @Override
  public ModelWithSuperAttributes_ show(boolean show) {
    super.show(show);
    return this;
  }

  @Override
  public ModelWithSuperAttributes_ hide() {
    super.hide();
    return this;
  }

  @Override
  public ModelWithSuperAttributes_ reset() {
    this.superValue = 0;
    super.reset();
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof ModelWithSuperAttributes_)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    ModelWithSuperAttributes_ that = (ModelWithSuperAttributes_) o;
    if (superValue != that.superValue) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + superValue;
    return result;
  }

  @Override
  public String toString() {
    return "ModelWithSuperAttributes_{" +
        "superValue=" + superValue +
        "}" + super.toString();
  }
}
