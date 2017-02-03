package com.airbnb.epoxy;

import android.support.annotation.LayoutRes;
import java.lang.CharSequence;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.util.Arrays;

/**
 * Generated file. Do not modify! */
public class ModelWithVarargsConstructors_ extends ModelWithVarargsConstructors {
  public ModelWithVarargsConstructors(String... varargs) {
    super(varargs);
  }

  public ModelWithVarargsConstructors(int valueInt, String... varargs) {
    super(valueInt, varargs);
  }

  public ModelWithVarargsConstructors_ varargs(String[] varargs) {
    this.varargs = varargs;
    return this;
  }

  public String[] varargs() {
    return varargs;
  }

  public ModelWithVarargsConstructors_ valueInt(int valueInt) {
    this.valueInt = valueInt;
    return this;
  }

  public int valueInt() {
    return valueInt;
  }

  @Override
  public ModelWithVarargsConstructors_ id(long id) {
    super.id(id);
    return this;
  }

  @Override
  public ModelWithVarargsConstructors_ id(CharSequence key) {
    super.id(key);
    return this;
  }

  @Override
  public ModelWithVarargsConstructors_ id(CharSequence key, long id) {
    super.id(key, id);
    return this;
  }

  @Override
  public ModelWithVarargsConstructors_ layout(@LayoutRes int arg0) {
    super.layout(arg0);
    return this;
  }

  @Override
  public ModelWithVarargsConstructors_ show() {
    super.show();
    return this;
  }

  @Override
  public ModelWithVarargsConstructors_ show(boolean show) {
    super.show(show);
    return this;
  }

  @Override
  public ModelWithVarargsConstructors_ hide() {
    super.hide();
    return this;
  }

  @Override
  public ModelWithVarargsConstructors_ reset() {
    this.varargs = null;
    this.valueInt = 0;
    super.reset();
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof ModelWithVarargsConstructors_)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    ModelWithVarargsConstructors_ that = (ModelWithVarargsConstructors_) o;
    if (!Arrays.equals(varargs, that.varargs)) {
      return false;
    }
    if (valueInt != that.valueInt) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + Arrays.hashCode(varargs);
    result = 31 * result + valueInt;
    return result;
  }

  @Override
  public String toString() {
    return "ModelWithVarargsConstructors_{" +
        "varargs=" + varargs +
        ", valueInt=" + valueInt +
        "}" + super.toString();
  }
}
