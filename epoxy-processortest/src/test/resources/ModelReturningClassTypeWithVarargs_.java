package com.airbnb.epoxy;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import java.lang.CharSequence;
import java.lang.Number;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;

/**
 * Generated file. Do not modify!
 */
public class ModelReturningClassTypeWithVarargs_ extends ModelReturningClassTypeWithVarargs implements GeneratedModel<Object>, ModelReturningClassTypeWithVarargsBuilder {
  private OnModelBoundListener<ModelReturningClassTypeWithVarargs_, Object> onModelBoundListener_epoxyGeneratedModel;

  private OnModelUnboundListener<ModelReturningClassTypeWithVarargs_, Object> onModelUnboundListener_epoxyGeneratedModel;

  private OnModelVisibilityStateChangedListener<ModelReturningClassTypeWithVarargs_, Object> onModelVisibilityStateChangedListener_epoxyGeneratedModel;

  private OnModelVisibilityChangedListener<ModelReturningClassTypeWithVarargs_, Object> onModelVisibilityChangedListener_epoxyGeneratedModel;

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
   * You may clear the listener by setting a null value, or by calling {@link #reset()}
   */
  public ModelReturningClassTypeWithVarargs_ onBind(
      OnModelBoundListener<ModelReturningClassTypeWithVarargs_, Object> listener) {
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
   * You may clear the listener by setting a null value, or by calling {@link #reset()}
   */
  public ModelReturningClassTypeWithVarargs_ onUnbind(
      OnModelUnboundListener<ModelReturningClassTypeWithVarargs_, Object> listener) {
    onMutation();
    this.onModelUnboundListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public void onVisibilityStateChanged(int visibilityState, final Object object) {
    if (onModelVisibilityStateChangedListener_epoxyGeneratedModel != null) {
      onModelVisibilityStateChangedListener_epoxyGeneratedModel.onVisibilityStateChanged(this, object, visibilityState);
    }
    super.onVisibilityStateChanged(visibilityState, object);
  }

  /**
   * Register a listener that will be called when this model visibility state has changed.
   * <p>
   * The listener will contribute to this model's hashCode state per the {@link
   * com.airbnb.epoxy.EpoxyAttribute.Option#DoNotHash} rules.
   */
  public ModelReturningClassTypeWithVarargs_ onVisibilityStateChanged(
      OnModelVisibilityStateChangedListener<ModelReturningClassTypeWithVarargs_, Object> listener) {
    onMutation();
    this.onModelVisibilityStateChangedListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public void onVisibilityChanged(float percentVisibleHeight, float percentVisibleWidth,
      int visibleHeight, int visibleWidth, final Object object) {
    if (onModelVisibilityChangedListener_epoxyGeneratedModel != null) {
      onModelVisibilityChangedListener_epoxyGeneratedModel.onVisibilityChanged(this, object, percentVisibleHeight, percentVisibleWidth, visibleHeight, visibleWidth);
    }
    super.onVisibilityChanged(percentVisibleHeight, percentVisibleWidth, visibleHeight, visibleWidth, object);
  }

  /**
   * Register a listener that will be called when this model visibility has changed.
   * <p>
   * The listener will contribute to this model's hashCode state per the {@link
   * com.airbnb.epoxy.EpoxyAttribute.Option#DoNotHash} rules.
   */
  public ModelReturningClassTypeWithVarargs_ onVisibilityChanged(
      OnModelVisibilityChangedListener<ModelReturningClassTypeWithVarargs_, Object> listener) {
    onMutation();
    this.onModelVisibilityChangedListener_epoxyGeneratedModel = listener;
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
  public ModelReturningClassTypeWithVarargs_ id(@Nullable Number... ids) {
    super.id(ids);
    return this;
  }

  @Override
  public ModelReturningClassTypeWithVarargs_ id(long id1, long id2) {
    super.id(id1, id2);
    return this;
  }

  @Override
  public ModelReturningClassTypeWithVarargs_ id(@Nullable CharSequence key) {
    super.id(key);
    return this;
  }

  @Override
  public ModelReturningClassTypeWithVarargs_ id(@Nullable CharSequence key,
      @Nullable CharSequence... otherKeys) {
    super.id(key, otherKeys);
    return this;
  }

  @Override
  public ModelReturningClassTypeWithVarargs_ id(@Nullable CharSequence key, long id) {
    super.id(key, id);
    return this;
  }

  @Override
  public ModelReturningClassTypeWithVarargs_ layout(@LayoutRes int layoutRes) {
    super.layout(layoutRes);
    return this;
  }

  @Override
  public ModelReturningClassTypeWithVarargs_ spanSizeOverride(
      @Nullable EpoxyModel.SpanSizeOverrideCallback spanSizeCallback) {
    super.spanSizeOverride(spanSizeCallback);
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
    onModelVisibilityStateChangedListener_epoxyGeneratedModel = null;
    onModelVisibilityChangedListener_epoxyGeneratedModel = null;
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
    if (((onModelVisibilityStateChangedListener_epoxyGeneratedModel == null) != (that.onModelVisibilityStateChangedListener_epoxyGeneratedModel == null))) {
      return false;
    }
    if (((onModelVisibilityChangedListener_epoxyGeneratedModel == null) != (that.onModelVisibilityChangedListener_epoxyGeneratedModel == null))) {
      return false;
    }
    if ((value != that.value)) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int _result = super.hashCode();
    _result = 31 * _result + (onModelBoundListener_epoxyGeneratedModel != null ? 1 : 0);
    _result = 31 * _result + (onModelUnboundListener_epoxyGeneratedModel != null ? 1 : 0);
    _result = 31 * _result + (onModelVisibilityStateChangedListener_epoxyGeneratedModel != null ? 1 : 0);
    _result = 31 * _result + (onModelVisibilityChangedListener_epoxyGeneratedModel != null ? 1 : 0);
    _result = 31 * _result + value;
    return _result;
  }

  @Override
  public String toString() {
    return "ModelReturningClassTypeWithVarargs_{" +
        "value=" + value +
        "}" + super.toString();
  }
}
