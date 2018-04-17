package com.airbnb.epoxy;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.lang.CharSequence;
import java.lang.Number;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;

/**
 * Generated file. Do not modify! */
public class ModelReturningClassTypeWithVarargs_ extends ModelReturningClassTypeWithVarargs implements GeneratedModel<Object>, ModelReturningClassTypeWithVarargsBuilder {
  private OnModelBoundListener<ModelReturningClassTypeWithVarargs_, Object> onModelBoundListener_epoxyGeneratedModel;

  private OnModelUnboundListener<ModelReturningClassTypeWithVarargs_, Object> onModelUnboundListener_epoxyGeneratedModel;

  public ModelReturningClassTypeWithVarargs_() {
    super();
  }

  @Override
  public void addTo(EpoxyController controller) {
    super.addTo(controller);
    addWithDebugValidation(controller);
  }

  @Override
  public void handlePreBind(final EpoxyViewHolder holder, final Object object, final int position) {
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
  public ModelReturningClassTypeWithVarargs_ onBind(OnModelBoundListener<ModelReturningClassTypeWithVarargs_, Object> listener) {
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
  public ModelReturningClassTypeWithVarargs_ onUnbind(OnModelUnboundListener<ModelReturningClassTypeWithVarargs_, Object> listener) {
    onMutation();
    this.onModelUnboundListener_epoxyGeneratedModel = listener;
    return this;
  }

  public ModelReturningClassTypeWithVarargs_ value(int value) {
    onMutation();
    super.value = value;
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
  public ModelReturningClassTypeWithVarargs_ id(@NonNull Number... arg0) {
    super.id(arg0);
    return this;
  }

  @Override
  public ModelReturningClassTypeWithVarargs_ id(long id1, long id2) {
    super.id(id1, id2);
    return this;
  }

  @Override
  public ModelReturningClassTypeWithVarargs_ id(@NonNull CharSequence arg0) {
    super.id(arg0);
    return this;
  }

  @Override
  public ModelReturningClassTypeWithVarargs_ id(@NonNull CharSequence arg0,
      @NonNull CharSequence... arg1) {
    super.id(arg0, arg1);
    return this;
  }

  @Override
  public ModelReturningClassTypeWithVarargs_ id(@NonNull CharSequence arg0, long arg1) {
    super.id(arg0, arg1);
    return this;
  }

  @Override
  public ModelReturningClassTypeWithVarargs_ layout(@LayoutRes int arg0) {
    super.layout(arg0);
    return this;
  }

  @Override
  public ModelReturningClassTypeWithVarargs_ spanSizeOverride(@Nullable EpoxyModel.SpanSizeOverrideCallback arg0) {
    super.spanSizeOverride(arg0);
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
    onModelBoundListener_epoxyGeneratedModel = null;
    onModelUnboundListener_epoxyGeneratedModel = null;
    super.value = 0;
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
    if (((onModelBoundListener_epoxyGeneratedModel == null) != (that.onModelBoundListener_epoxyGeneratedModel == null))) {
      return false;
    }
    if (((onModelUnboundListener_epoxyGeneratedModel == null) != (that.onModelUnboundListener_epoxyGeneratedModel == null))) {
      return false;
    }
    if ((value != that.value)) {
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
    return "ModelReturningClassTypeWithVarargs_{" +
        "value=" + value +
        "}" + super.toString();
  }
}