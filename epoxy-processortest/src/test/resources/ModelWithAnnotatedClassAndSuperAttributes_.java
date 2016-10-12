package com.airbnb.epoxy;

import android.support.annotation.LayoutRes;
import java.lang.Object;
import java.lang.Override;

/**
 * Generated file. Do not modify! */
public class ModelWithAnnotatedClassAndSuperAttributes_ extends ModelWithAnnotatedClassAndSuperAttributes {
  public ModelWithAnnotatedClassAndSuperAttributes_() {
    super();
  }

  public ModelWithAnnotatedClassAndSuperAttributes_ superValue(int superValue) {
    this.superValue = superValue;
    return this;
  }

  public int superValue() {
    return superValue;
  }

  @Override
  public ModelWithAnnotatedClassAndSuperAttributes_ id(long id) {
    super.id(id);
    return this;
  }

  @Override
  public ModelWithAnnotatedClassAndSuperAttributes_ layout(@LayoutRes int layout) {
    super.layout(layout);
    return this;
  }

  @Override
  public ModelWithAnnotatedClassAndSuperAttributes_ show() {
    super.show();
    return this;
  }

  @Override
  public ModelWithAnnotatedClassAndSuperAttributes_ show(boolean show) {
    super.show(show);
    return this;
  }

  @Override
  public ModelWithAnnotatedClassAndSuperAttributes_ hide() {
    super.hide();
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof ModelWithAnnotatedClassAndSuperAttributes_)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    ModelWithAnnotatedClassAndSuperAttributes_ that =
        (ModelWithAnnotatedClassAndSuperAttributes_) o;
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
}
