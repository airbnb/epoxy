package com.airbnb.epoxy;

import android.support.annotation.LayoutRes;
import java.lang.CharSequence;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;

/**
 * Generated file. Do not modify! */
public class ModelWithType_<T extends String> extends ModelWithType<T> implements GeneratedModel<Object> {
  private OnModelBoundListener<ModelWithType_<T>, Object> onModelBoundListener_epoxyGeneratedModel;

  private OnModelUnboundListener<ModelWithType_<T>, Object> onModelUnboundListener_epoxyGeneratedModel;

  public ModelWithType_() {
    super();
  }

  @Override
  public void addTo(EpoxyController controller) {
    super.addTo(controller);
    addWithDebugValidation(controller);
  }

  @Override
  public void handlePreBind(final EpoxyViewHolder holder, final Object object) {
    validateStateHasNotChangedSinceAdded("The model was changed between being added to the controller and being bound.");
  }

  @Override
  public void handlePostBind(final Object object, int position) {
    if (onModelBoundListener_epoxyGeneratedModel != null) {
      onModelBoundListener_epoxyGeneratedModel.onModelBound(this, object, position);
    }
    validateStateHasNotChangedSinceAdded("The model was changed during the bind call.");
  }

  /**
   * Register a listener that will be called when this model is bound to a view.
   * <p>
   * The listener will contribute to this model's hashCode state per the {@link
   * com.airbnb.epoxy.EpoxyAttribute.Option#DoNotHash} rules.
   * <p>
   * You may clear the listener by setting a null value, or by calling {@link #reset()} */
  public ModelWithType_<T> onBind(OnModelBoundListener<ModelWithType_<T>, Object> listener) {
    validateMutability();
    this.onModelBoundListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public void unbind(Object object) {
    validateStateHasNotChangedSinceAdded("The model was changed between being being bound to the recycler view and being unbound.");
    super.unbind(object);
    if (onModelUnboundListener_epoxyGeneratedModel != null) {
      onModelUnboundListener_epoxyGeneratedModel.onModelUnbound(this, object);
    }
    validateStateHasNotChangedSinceAdded("The model was changed during the unbind method.");
  }

  /**
   * Register a listener that will be called when this model is unbound from a view.
   * <p>
   * The listener will contribute to this model's hashCode state per the {@link
   * com.airbnb.epoxy.EpoxyAttribute.Option#DoNotHash} rules.
   * <p>
   * You may clear the listener by setting a null value, or by calling {@link #reset()} */
  public ModelWithType_<T> onUnbind(OnModelUnboundListener<ModelWithType_<T>, Object> listener) {
    validateMutability();
    this.onModelUnboundListener_epoxyGeneratedModel = listener;
    return this;
  }

  public ModelWithType_<T> value(int value) {
    validateMutability();
    this.value = value;
    return this;
  }

  public int value() {
    return value;
  }

  @Override
  public ModelWithType_<T> id(long id) {
    super.id(id);
    return this;
  }

  @Override
  public ModelWithType_<T> id(CharSequence key) {
    super.id(key);
    return this;
  }

  @Override
  public ModelWithType_<T> id(CharSequence key, long id) {
    super.id(key, id);
    return this;
  }

  @Override
  public ModelWithType_<T> layout(@LayoutRes int arg0) {
    super.layout(arg0);
    return this;
  }

  @Override
  public ModelWithType_<T> show() {
    super.show();
    return this;
  }

  @Override
  public ModelWithType_<T> show(boolean show) {
    super.show(show);
    return this;
  }

  @Override
  public ModelWithType_<T> hide() {
    super.hide();
    return this;
  }

  @Override
  public ModelWithType_<T> reset() {
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
    if (!(o instanceof ModelWithType_)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    ModelWithType_ that = (ModelWithType_) o;
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
    return "ModelWithType_{" +
        "value=" + value +
        "}" + super.toString();
  }
}