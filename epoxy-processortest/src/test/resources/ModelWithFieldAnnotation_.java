package com.airbnb.epoxy;

import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import java.lang.CharSequence;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;

/**
 * Generated file. Do not modify! */
public class ModelWithFieldAnnotation_ extends ModelWithFieldAnnotation {
  public ModelWithFieldAnnotation_() {
    super();
  }

  public ModelWithFieldAnnotation_ title(@Nullable String title) {
    this.title = title;
    return this;
  }

  @Nullable
  public String title() {
    return title;
  }

  @Override
  public ModelWithFieldAnnotation_ id(long id) {
    super.id(id);
    return this;
  }

  @Override
  public ModelWithFieldAnnotation_ id(CharSequence key) {
    super.id(key);
    return this;
  }

  @Override
  public ModelWithFieldAnnotation_ id(CharSequence key, long id) {
    super.id(key, id);
    return this;
  }

  @Override
  public ModelWithFieldAnnotation_ layout(@LayoutRes int arg0) {
    super.layout(arg0);
    return this;
  }

  @Override
  public ModelWithFieldAnnotation_ show() {
    super.show();
    return this;
  }

  @Override
  public ModelWithFieldAnnotation_ show(boolean show) {
    super.show(show);
    return this;
  }

  @Override
  public ModelWithFieldAnnotation_ hide() {
    super.hide();
    return this;
  }

  @Override
  public ModelWithFieldAnnotation_ reset() {
    this.title = null;
    super.reset();
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof ModelWithFieldAnnotation_)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    ModelWithFieldAnnotation_ that = (ModelWithFieldAnnotation_) o;
    if (title != null ? !title.equals(that.title) : that.title != null) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + (title != null ? title.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "ModelWithFieldAnnotation_{" +
        "title=" + title +
        "}" + super.toString();
  }
}
