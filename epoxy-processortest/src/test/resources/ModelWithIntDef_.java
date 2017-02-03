package com.airbnb.epoxy.models;

import android.support.annotation.LayoutRes;
import java.lang.CharSequence;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;

/**
 * Generated file. Do not modify! */
public class ModelWithIntDef_ extends ModelWithIntDef {
  public ModelWithIntDef_() {
    super();
  }

  public ModelWithIntDef_ type(@ModelWithIntDef.MyType int type) {
    this.type = type;
    return this;
  }

  @ModelWithIntDef.MyType
  public int type() {
    return type;
  }

  @Override
  public ModelWithIntDef_ id(long id) {
    super.id(id);
    return this;
  }

  @Override
  public ModelWithIntDef_ id(CharSequence key) {
    super.id(key);
    return this;
  }

  @Override
  public ModelWithIntDef_ id(CharSequence key, long id) {
    super.id(key, id);
    return this;
  }

  @Override
  public ModelWithIntDef_ layout(@LayoutRes int arg0) {
    super.layout(arg0);
    return this;
  }

  @Override
  public ModelWithIntDef_ show() {
    super.show();
    return this;
  }

  @Override
  public ModelWithIntDef_ show(boolean show) {
    super.show(show);
    return this;
  }

  @Override
  public ModelWithIntDef_ hide() {
    super.hide();
    return this;
  }

  @Override
  public ModelWithIntDef_ reset() {
    this.type = 0;
    super.reset();
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof ModelWithIntDef_)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    ModelWithIntDef_ that = (ModelWithIntDef_) o;
    if (type != that.type) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + type;
    return result;
  }

  @Override
  public String toString() {
    return "ModelWithIntDef_{" +
        "type=" + type +
        "}" + super.toString();
  }
}
