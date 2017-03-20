package com.airbnb.epoxy;

import android.support.annotation.LayoutRes;
import java.lang.CharSequence;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.util.Arrays;

/**
 * Generated file. Do not modify! */
public class ModelWithVarargsConstructors_ extends ModelWithVarargsConstructors implements GeneratedModel<Object> {
  private OnModelBoundListener<ModelWithVarargsConstructors_, Object> onModelBoundListener_epoxyGeneratedModel;

  private OnModelUnboundListener<ModelWithVarargsConstructors_, Object> onModelUnboundListener_epoxyGeneratedModel;

  public ModelWithVarargsConstructors_(String... varargs) {
    super(varargs);
  }

  public ModelWithVarargsConstructors_(int valueInt, String... varargs) {
    super(valueInt, varargs);
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
  public ModelWithVarargsConstructors_ onBind(OnModelBoundListener<ModelWithVarargsConstructors_, Object> listener) {
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
  public ModelWithVarargsConstructors_ onUnbind(OnModelUnboundListener<ModelWithVarargsConstructors_, Object> listener) {
    validateMutability();
    this.onModelUnboundListener_epoxyGeneratedModel = listener;
    return this;
  }

  public ModelWithVarargsConstructors_ varargs(String[] varargs) {
    validateMutability();
    this.varargs = varargs;
    return this;
  }

  public String[] varargs() {
    return varargs;
  }

  public ModelWithVarargsConstructors_ valueInt(int valueInt) {
    validateMutability();
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
    onModelBoundListener_epoxyGeneratedModel = null;
    onModelUnboundListener_epoxyGeneratedModel = null;
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
    if ((onModelBoundListener_epoxyGeneratedModel == null) != (that.onModelBoundListener_epoxyGeneratedModel == null)) {
      return false;
    }
    if ((onModelUnboundListener_epoxyGeneratedModel == null) != (that.onModelUnboundListener_epoxyGeneratedModel == null)) {
      return false;
    }
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
    result = 31 * result + (onModelBoundListener_epoxyGeneratedModel != null ? 1 : 0);
    result = 31 * result + (onModelUnboundListener_epoxyGeneratedModel != null ? 1 : 0);
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