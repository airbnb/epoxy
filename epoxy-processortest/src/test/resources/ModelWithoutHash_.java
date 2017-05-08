package com.airbnb.epoxy;

import android.support.annotation.LayoutRes;
import java.lang.CharSequence;
import java.lang.Number;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;

/**
 * Generated file. Do not modify! */
public class ModelWithoutHash_ extends ModelWithoutHash implements GeneratedModel<Object> {
  private OnModelBoundListener<ModelWithoutHash_, Object> onModelBoundListener_epoxyGeneratedModel;

  private OnModelUnboundListener<ModelWithoutHash_, Object> onModelUnboundListener_epoxyGeneratedModel;

  public ModelWithoutHash_() {
    super();
  }

  @Override
  public void addTo(EpoxyController controller) {
    super.addTo(controller);
    addWithDebugValidation(controller);
  }

  @Override
  public void handlePreBind(final EpoxyViewHolder holder, final Object object, int position) {
    validateStateHasNotChangedSinceAdded("The model was changed between being added to the controller and being bound.", position);
  }

  @Override
  public void handlePostBind(final Object object, int position) {
    if (onModelBoundListener_epoxyGeneratedModel != null) {
      onModelBoundListener_epoxyGeneratedModel.onModelBound(this, object, position);
    }
    validateStateHasNotChangedSinceAdded("The model was changed during the bind call.", position);
  }

  /**
   * Register a listener that will be called when this model is bound to a view.
   * <p>
   * The listener will contribute to this model's hashCode state per the {@link
   * com.airbnb.epoxy.EpoxyAttribute.Option#DoNotHash} rules.
   * <p>
   * You may clear the listener by setting a null value, or by calling {@link #reset()} */
  public ModelWithoutHash_ onBind(OnModelBoundListener<ModelWithoutHash_, Object> listener) {
    onMutation();
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
  public ModelWithoutHash_ onUnbind(OnModelUnboundListener<ModelWithoutHash_, Object> listener) {
    onMutation();
    this.onModelUnboundListener_epoxyGeneratedModel = listener;
    return this;
  }

  public ModelWithoutHash_ value2(int value2) {
    onMutation();
    super.value2 = value2;
    return this;
  }

  public int value2() {
    return value2;
  }

  public ModelWithoutHash_ value(int value) {
    onMutation();
    super.value = value;
    return this;
  }

  public int value() {
    return value;
  }

  public ModelWithoutHash_ value3(String value3) {
    onMutation();
    super.value3 = value3;
    return this;
  }

  public String value3() {
    return value3;
  }

  @Override
  public ModelWithoutHash_ id(long id) {
    super.id(id);
    return this;
  }

  @Override
  public ModelWithoutHash_ id(Number... ids) {
    super.id(ids);
    return this;
  }

  @Override
  public ModelWithoutHash_ id(long id1, long id2) {
    super.id(id1, id2);
    return this;
  }

  @Override
  public ModelWithoutHash_ id(CharSequence key) {
    super.id(key);
    return this;
  }

  @Override
  public ModelWithoutHash_ id(CharSequence key, long id) {
    super.id(key, id);
    return this;
  }

  @Override
  public ModelWithoutHash_ layout(@LayoutRes int arg0) {
    super.layout(arg0);
    return this;
  }

  @Override
  public ModelWithoutHash_ show() {
    super.show();
    return this;
  }

  @Override
  public ModelWithoutHash_ show(boolean show) {
    super.show(show);
    return this;
  }

  @Override
  public ModelWithoutHash_ hide() {
    super.hide();
    return this;
  }

  @Override
  public ModelWithoutHash_ reset() {
    onModelBoundListener_epoxyGeneratedModel = null;
    onModelUnboundListener_epoxyGeneratedModel = null;
    super.value2 = 0;
    super.value = 0;
    super.value3 = null;
    super.reset();
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof ModelWithoutHash_)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    ModelWithoutHash_ that = (ModelWithoutHash_) o;
    if ((onModelBoundListener_epoxyGeneratedModel == null) != (that.onModelBoundListener_epoxyGeneratedModel == null)) {
      return false;
    }
    if ((onModelUnboundListener_epoxyGeneratedModel == null) != (that.onModelUnboundListener_epoxyGeneratedModel == null)) {
      return false;
    }
    if (value != that.value) {
      return false;
    }
    if ((value3 == null) != (that.value3 == null)) {
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
    result = 31 * result + (value3 != null ? 1 : 0);
    return result;
  }

  @Override
  public String toString() {
    return "ModelWithoutHash_{" +
        "value2=" + value2 +
        ", value=" + value +
        ", value3=" + value3 +
        "}" + super.toString();
  }
}