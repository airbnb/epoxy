package com.airbnb.epoxy;

import android.support.annotation.LayoutRes;
import java.lang.CharSequence;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;

/**
 * Generated file. Do not modify! */
public class ModelWithConstructors_ extends ModelWithConstructors implements GeneratedModel<Object> {
  private OnModelBoundListener<ModelWithConstructors_, Object> onModelBoundListener_epoxyGeneratedModel;

  private OnModelUnboundListener<ModelWithConstructors_, Object> onModelUnboundListener_epoxyGeneratedModel;

  public ModelWithConstructors_(long id, int valueInt) {
    super(id, valueInt);
  }

  public ModelWithConstructors_(int valueInt) {
    super(valueInt);
  }

  public ModelWithConstructors_() {
    super();
  }

  @Override
  public void handlePreBind(final EpoxyViewHolder holder, final Object object) {
  }

  @Override
  public void handlePostBind(final Object object, int position) {
    if (onModelBoundListener_epoxyGeneratedModel != null) {
      onModelBoundListener_epoxyGeneratedModel.onModelBound(this, object, position);
    }
  }

  /**
   * Register a listener that will be called when this model is bound to a view.
   * <p>
   * The listener will contribute to this model's hashCode state per the {@link
   * com.airbnb.epoxy.EpoxyAttribute.Option#DoNotHash} rules.
   * <p>
   * You may clear the listener by setting a null value, or by calling {@link #reset()} */
  public ModelWithConstructors_ onBind(OnModelBoundListener<ModelWithConstructors_, Object> listener) {
    this.onModelBoundListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public void unbind(Object object) {
    super.unbind(object);
    if (onModelUnboundListener_epoxyGeneratedModel != null) {
      onModelUnboundListener_epoxyGeneratedModel.onModelUnbound(this, object);
    }
  }

  /**
   * Register a listener that will be called when this model is unbound from a view.
   * <p>
   * The listener will contribute to this model's hashCode state per the {@link
   * com.airbnb.epoxy.EpoxyAttribute.Option#DoNotHash} rules.
   * <p>
   * You may clear the listener by setting a null value, or by calling {@link #reset()} */
  public ModelWithConstructors_ onUnbind(OnModelUnboundListener<ModelWithConstructors_, Object> listener) {
    this.onModelUnboundListener_epoxyGeneratedModel = listener;
    return this;
  }

  public ModelWithConstructors_ valueInt(int valueInt) {
    this.valueInt = valueInt;
    return this;
  }

  public int valueInt() {
    return valueInt;
  }

  @Override
  public ModelWithConstructors_ id(long id) {
    super.id(id);
    return this;
  }

  @Override
  public ModelWithConstructors_ id(CharSequence key) {
    super.id(key);
    return this;
  }

  @Override
  public ModelWithConstructors_ id(CharSequence key, long id) {
    super.id(key, id);
    return this;
  }

  @Override
  public ModelWithConstructors_ layout(@LayoutRes int arg0) {
    super.layout(arg0);
    return this;
  }

  @Override
  public ModelWithConstructors_ show() {
    super.show();
    return this;
  }

  @Override
  public ModelWithConstructors_ show(boolean show) {
    super.show(show);
    return this;
  }

  @Override
  public ModelWithConstructors_ hide() {
    super.hide();
    return this;
  }

  @Override
  public ModelWithConstructors_ reset() {
    onModelBoundListener_epoxyGeneratedModel = null;
    onModelUnboundListener_epoxyGeneratedModel = null;
    this.valueInt = 0;
    super.reset();
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof ModelWithConstructors_)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    ModelWithConstructors_ that = (ModelWithConstructors_) o;
    if ((onModelBoundListener_epoxyGeneratedModel == null) != (that.onModelBoundListener_epoxyGeneratedModel == null)) {
      return false;
    }
    if ((onModelUnboundListener_epoxyGeneratedModel == null) != (that.onModelUnboundListener_epoxyGeneratedModel == null)) {
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
    result = 31 * result + (onModelBoundListener_epoxyGeneratedModel != null ? 1 : 0);
    result = 31 * result + (onModelUnboundListener_epoxyGeneratedModel != null ? 1 : 0);
    result = 31 * result + valueInt;
    return result;
  }

  @Override
  public String toString() {
    return "ModelWithConstructors_{" +
        "valueInt=" + valueInt +
        "}" + super.toString();
  }
}