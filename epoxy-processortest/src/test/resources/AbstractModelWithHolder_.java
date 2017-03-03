package com.airbnb.epoxy;

import android.support.annotation.LayoutRes;
import java.lang.CharSequence;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;

/**
 * Generated file. Do not modify! */
public class AbstractModelWithHolder_ extends AbstractModelWithHolder implements GeneratedModel<AbstractModelWithHolder.Holder> {
  private OnModelBoundListener<AbstractModelWithHolder_, AbstractModelWithHolder.Holder> onModelBoundListener_epoxyGeneratedModel;

  private OnModelUnboundListener<AbstractModelWithHolder_, AbstractModelWithHolder.Holder> onModelUnboundListener_epoxyGeneratedModel;

  public AbstractModelWithHolder_() {
    super();
  }

  @Override
  public void handlePreBind(final EpoxyViewHolder holder,
      final AbstractModelWithHolder.Holder object) {
  }

  @Override
  public void handlePostBind(final EpoxyViewHolder holder,
      final AbstractModelWithHolder.Holder object) {
    if (onModelBoundListener_epoxyGeneratedModel != null) {
      onModelBoundListener_epoxyGeneratedModel.onModelBound(this, object);
    }
  }

  public AbstractModelWithHolder_ onBind(OnModelBoundListener<AbstractModelWithHolder_, AbstractModelWithHolder.Holder> listener) {
    this.onModelBoundListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public void unbind(AbstractModelWithHolder.Holder object) {
    super.unbind(object);
    if (onModelUnboundListener_epoxyGeneratedModel != null) {
      onModelUnboundListener_epoxyGeneratedModel.onModelUnbound(this, object);
    }
  }

  public AbstractModelWithHolder_ onUnbind(OnModelUnboundListener<AbstractModelWithHolder_, AbstractModelWithHolder.Holder> listener) {
    this.onModelUnboundListener_epoxyGeneratedModel = listener;
    return this;
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
  public AbstractModelWithHolder_ id(CharSequence key) {
    super.id(key);
    return this;
  }

  @Override
  public AbstractModelWithHolder_ id(CharSequence key, long id) {
    super.id(key, id);
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
    onModelBoundListener_epoxyGeneratedModel = null;
    onModelUnboundListener_epoxyGeneratedModel = null;
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
    if ((onModelBoundListener_epoxyGeneratedModel == null) != (that.onModelBoundListener_epoxyGeneratedModel == null)) {
      return false;
    }
    if ((onModelUnboundListener_epoxyGeneratedModel == null) != (that.onModelUnboundListener_epoxyGeneratedModel == null)) {
      return false;
    }
    if (value != that.value) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + (onModelBoundListener_epoxyGeneratedModel != null ? 1 : 0);
    result = 31 * result + (onModelUnboundListener_epoxyGeneratedModel != null ? 1 : 0);
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