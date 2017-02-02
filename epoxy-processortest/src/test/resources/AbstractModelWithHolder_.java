package com.airbnb.epoxy;

import android.support.annotation.LayoutRes;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;

/**
 * Generated file. Do not modify!
 */
public class AbstractModelWithHolder_ extends AbstractModelWithHolder {
  public AbstractModelWithHolder_() {
    super();
  }

  public AbstractModelWithHolder_ value(int value) {
    this.value = value;
    return this;
  }

  public int value() {
    return value;
  }

  @Override
  public AbstractModelWithHolder_ id(long id) {
    super.id(id);
    return this;
  }

  @Override
  public AbstractModelWithHolder_ layout(@LayoutRes int arg0) {
    super.layout(arg0);
    return this;
  }

  @Override
  public AbstractModelWithHolder_ show() {
    super.show();
    return this;
  }

  @Override
  public AbstractModelWithHolder_ show(boolean show) {
    super.show(show);
    return this;
  }

  @Override
  public AbstractModelWithHolder_ hide() {
    super.hide();
    return this;
  }

  @Override
  protected AbstractModelWithHolder.Holder createNewHolder() {
    return new AbstractModelWithHolder.Holder();
  }

  @Override
  public AbstractModelWithHolder_ reset() {
    this.value = 0;
    super.reset();
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof AbstractModelWithHolder_)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    AbstractModelWithHolder_ that = (AbstractModelWithHolder_) o;
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
    return "AbstractModelWithHolder_{" +
        "value=" + value +
        "}" + super.toString();
  }
}
