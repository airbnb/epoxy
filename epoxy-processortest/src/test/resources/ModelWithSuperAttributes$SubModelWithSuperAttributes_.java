package com.airbnb.epoxy;

import android.support.annotation.LayoutRes;
import java.lang.CharSequence;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;

/**
 * Generated file. Do not modify! */
public class ModelWithSuperAttributes$SubModelWithSuperAttributes_ extends ModelWithSuperAttributes.SubModelWithSuperAttributes {
  public ModelWithSuperAttributes$SubModelWithSuperAttributes_() {
    super();
  }

  public ModelWithSuperAttributes$SubModelWithSuperAttributes_ subValue(int subValue) {
    this.subValue = subValue;
    return this;
  }

  public int subValue() {
    return subValue;
  }

  public ModelWithSuperAttributes$SubModelWithSuperAttributes_ superValue(int superValue) {
    this.superValue = superValue;
    return this;
  }

  public int superValue() {
    return superValue;
  }

  @Override
  public ModelWithSuperAttributes$SubModelWithSuperAttributes_ id(long id) {
    super.id(id);
    return this;
  }

  @Override
  public ModelWithSuperAttributes$SubModelWithSuperAttributes_ id(CharSequence key) {
    super.id(key);
    return this;
  }

  @Override
  public ModelWithSuperAttributes$SubModelWithSuperAttributes_ id(CharSequence key, long id) {
    super.id(key, id);
    return this;
  }

  @Override
  public ModelWithSuperAttributes$SubModelWithSuperAttributes_ layout(@LayoutRes int arg0) {
    super.layout(arg0);
    return this;
  }

  @Override
  public ModelWithSuperAttributes$SubModelWithSuperAttributes_ show() {
    super.show();
    return this;
  }

  @Override
  public ModelWithSuperAttributes$SubModelWithSuperAttributes_ show(boolean show) {
    super.show(show);
    return this;
  }

  @Override
  public ModelWithSuperAttributes$SubModelWithSuperAttributes_ hide() {
    super.hide();
    return this;
  }

  @Override
  public ModelWithSuperAttributes$SubModelWithSuperAttributes_ reset() {
    this.subValue = 0;
    this.superValue = 0;
    super.reset();
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof ModelWithSuperAttributes$SubModelWithSuperAttributes_)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    ModelWithSuperAttributes$SubModelWithSuperAttributes_ that =
        (ModelWithSuperAttributes$SubModelWithSuperAttributes_) o;
    if (subValue != that.subValue) {
      return false;
    }
    if (superValue != that.superValue) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + subValue;
    result = 31 * result + superValue;
    return result;
  }

  @Override
  public String toString() {
    return "ModelWithSuperAttributes$SubModelWithSuperAttributes_{" +
        "subValue=" + subValue +
        ", superValue=" + superValue +
        "}" + super.toString();
  }
}
