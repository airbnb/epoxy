package com.airbnb.epoxy;

import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import java.lang.CharSequence;
import java.lang.Number;
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
  public ModelWithVarargsConstructors_ onBind(OnModelBoundListener<ModelWithVarargsConstructors_, Object> listener) {
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
  public ModelWithVarargsConstructors_ onUnbind(OnModelUnboundListener<ModelWithVarargsConstructors_, Object> listener) {
    onMutation();
    this.onModelUnboundListener_epoxyGeneratedModel = listener;
    return this;
  }

  public ModelWithVarargsConstructors_ valueInt(int valueInt) {
    onMutation();
    super.valueInt = valueInt;
    return this;
  }

  public int valueInt() {
    return valueInt;
  }

  public ModelWithVarargsConstructors_ varargs(String[] varargs) {
    onMutation();
    super.varargs = varargs;
    return this;
  }

  public String[] varargs() {
    return varargs;
  }

  @Override
  public ModelWithVarargsConstructors_ id(long id) {
    super.id(id);
    return this;
  }

  @Override
  public ModelWithVarargsConstructors_ id(Number... ids) {
    super.id(ids);
    return this;
  }

  @Override
  public ModelWithVarargsConstructors_ id(long id1, long id2) {
    super.id(id1, id2);
    return this;
  }

  @Override
  public ModelWithVarargsConstructors_ id(CharSequence key) {
    super.id(key);
    return this;
  }

  @Override
  public ModelWithVarargsConstructors_ id(CharSequence key, CharSequence... otherKeys) {
    super.id(key, otherKeys);
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
  public ModelWithVarargsConstructors_ spanSizeOverride(@Nullable EpoxyModel.SpanSizeOverrideCallback arg0) {
    super.spanSizeOverride(arg0);
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
    super.valueInt = 0;
    super.varargs = null;
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
    if (valueInt != that.valueInt) {
      return false;
    }
    if (!Arrays.equals(varargs, that.varargs)) {
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
    result = 31 * result + Arrays.hashCode(varargs);
    return result;
  }

  @Override
  public String toString() {
    return "ModelWithVarargsConstructors_{" +
        "valueInt=" + valueInt +
        ", varargs=" + varargs +
        "}" + super.toString();
  }
}