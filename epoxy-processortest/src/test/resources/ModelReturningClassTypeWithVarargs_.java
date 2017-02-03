package com.airbnb.epoxy;

import android.support.annotation.LayoutRes;
import java.lang.CharSequence;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;

/**
 * Generated file. Do not modify!
 */
public class ModelReturningClassTypeWithVarargs_ extends ModelReturningClassTypeWithVarargs {
  public ModelReturningClassTypeWithVarargs_() {
    super();
  }

  public ModelReturningClassTypeWithVarargs_ value(int value) {
    this.value = value;
    return this;
  }

  public int value() {
    return value;
  }

  @Override
  public ModelReturningClassTypeWithVarargs_ classType(String... varargs) {
    super.classType(varargs);
    return this;
  }

  @Override
  public ModelReturningClassTypeWithVarargs_ classType(String first, String... varargs) {
    super.classType(first, varargs);
    return this;
  }

  @Override
  public ModelReturningClassTypeWithVarargs_ id(long id) {
    super.id(id);
    return this;
  }

  @Override
  public ModelReturningClassTypeWithVarargs_ id(CharSequence key) {
    super.id(key);
    return this;
  }

  @Override
  public ModelReturningClassTypeWithVarargs_ id(CharSequence key, long id) {
    super.id(key, id);
    return this;
  }

  @Override
  public ModelReturningClassTypeWithVarargs_ layout(@LayoutRes int arg0) {
    super.layout(arg0);
    return this;
  }

  @Override
  public ModelReturningClassTypeWithVarargs_ show() {
    super.show();
    return this;
  }

  @Override
  public ModelReturningClassTypeWithVarargs_ show(boolean show) {
    super.show(show);
    return this;
  }

  @Override
  public ModelReturningClassTypeWithVarargs_ hide() {
    super.hide();
    return this;
  }

  @Override
  public ModelReturningClassTypeWithVarargs_ reset() {
    this.value = 0;
    super.reset();
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof ModelReturningClassTypeWithVarargs_)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    ModelReturningClassTypeWithVarargs_ that = (ModelReturningClassTypeWithVarargs_) o;
    if (value != that.value) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + value;
    return result;
  }

  @Override
  public String toString() {
    return "ModelReturningClassTypeWithVarargs_{" +
        "value=" + value +
        "}" + super.toString();
  }
}
