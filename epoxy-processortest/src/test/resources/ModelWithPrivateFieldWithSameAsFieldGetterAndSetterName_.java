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
public class ModelWithPrivateFieldWithSameAsFieldGetterAndSetterName_ extends ModelWithPrivateFieldWithSameAsFieldGetterAndSetterName implements GeneratedModel<Object>, ModelWithPrivateFieldWithSameAsFieldGetterAndSetterNameBuilder {
  private OnModelBoundListener<ModelWithPrivateFieldWithSameAsFieldGetterAndSetterName_, Object> onModelBoundListener_epoxyGeneratedModel;

  private OnModelUnboundListener<ModelWithPrivateFieldWithSameAsFieldGetterAndSetterName_, Object> onModelUnboundListener_epoxyGeneratedModel;

  private OnModelVisibilityStateChangedListener<ModelWithPrivateFieldWithSameAsFieldGetterAndSetterName_, Object> onModelVisibilityStateChangedListener_epoxyGeneratedModel;

  private OnModelVisibilityChangedListener<ModelWithPrivateFieldWithSameAsFieldGetterAndSetterName_, Object> onModelVisibilityChangedListener_epoxyGeneratedModel;

  public ModelWithPrivateFieldWithSameAsFieldGetterAndSetterName_() {
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
  public ModelWithPrivateFieldWithSameAsFieldGetterAndSetterName_ onBind(
      OnModelBoundListener<ModelWithPrivateFieldWithSameAsFieldGetterAndSetterName_, Object> listener) {
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
  public ModelWithPrivateFieldWithSameAsFieldGetterAndSetterName_ onUnbind(
      OnModelUnboundListener<ModelWithPrivateFieldWithSameAsFieldGetterAndSetterName_, Object> listener) {
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
  public ModelWithPrivateFieldWithSameAsFieldGetterAndSetterName_ onVisibilityStateChanged(
      OnModelVisibilityStateChangedListener<ModelWithPrivateFieldWithSameAsFieldGetterAndSetterName_, Object> listener) {
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
  public ModelWithPrivateFieldWithSameAsFieldGetterAndSetterName_ onVisibilityChanged(
      OnModelVisibilityChangedListener<ModelWithPrivateFieldWithSameAsFieldGetterAndSetterName_, Object> listener) {
    onMutation();
    this.onModelVisibilityChangedListener_epoxyGeneratedModel = listener;
    return this;
  }

  public ModelWithPrivateFieldWithSameAsFieldGetterAndSetterName_ isValue(boolean isValue) {
    onMutation();
    super.setValue(isValue);
    return this;
  }

  public boolean isValue() {
    return super.isValue();
  }

  @Override
  public ModelWithPrivateFieldWithSameAsFieldGetterAndSetterName_ id(long id) {
    super.id(id);
    return this;
  }

  @Override
  public ModelWithPrivateFieldWithSameAsFieldGetterAndSetterName_ id(@Nullable Number... ids) {
    super.id(ids);
    return this;
  }

  @Override
  public ModelWithPrivateFieldWithSameAsFieldGetterAndSetterName_ id(long id1, long id2) {
    super.id(id1, id2);
    return this;
  }

  @Override
  public ModelWithPrivateFieldWithSameAsFieldGetterAndSetterName_ id(@Nullable CharSequence key) {
    super.id(key);
    return this;
  }

  @Override
  public ModelWithPrivateFieldWithSameAsFieldGetterAndSetterName_ id(@Nullable CharSequence key,
      @Nullable CharSequence... otherKeys) {
    super.id(key, otherKeys);
    return this;
  }

  @Override
  public ModelWithPrivateFieldWithSameAsFieldGetterAndSetterName_ id(@Nullable CharSequence key,
      long id) {
    super.id(key, id);
    return this;
  }

  @Override
  public ModelWithPrivateFieldWithSameAsFieldGetterAndSetterName_ layout(@LayoutRes int layoutRes) {
    super.layout(layoutRes);
    return this;
  }

  @Override
  public ModelWithPrivateFieldWithSameAsFieldGetterAndSetterName_ spanSizeOverride(
      @Nullable EpoxyModel.SpanSizeOverrideCallback spanSizeCallback) {
    super.spanSizeOverride(spanSizeCallback);
    return this;
  }

  @Override
  public ModelWithPrivateFieldWithSameAsFieldGetterAndSetterName_ show() {
    super.show();
    return this;
  }

  @Override
  public ModelWithPrivateFieldWithSameAsFieldGetterAndSetterName_ show(boolean show) {
    super.show(show);
    return this;
  }

  @Override
  public ModelWithPrivateFieldWithSameAsFieldGetterAndSetterName_ hide() {
    super.hide();
    return this;
  }

  @Override
  public ModelWithPrivateFieldWithSameAsFieldGetterAndSetterName_ reset() {
    onModelBoundListener_epoxyGeneratedModel = null;
    onModelUnboundListener_epoxyGeneratedModel = null;
    onModelVisibilityStateChangedListener_epoxyGeneratedModel = null;
    onModelVisibilityChangedListener_epoxyGeneratedModel = null;
    super.setValue(false);
    super.reset();
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof ModelWithPrivateFieldWithSameAsFieldGetterAndSetterName_)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    ModelWithPrivateFieldWithSameAsFieldGetterAndSetterName_ that = (ModelWithPrivateFieldWithSameAsFieldGetterAndSetterName_) o;
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
    if ((isValue() != that.isValue())) {
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
    _result = 31 * _result + (isValue() ? 1 : 0);
    return _result;
  }

  @Override
  public String toString() {
    return "ModelWithPrivateFieldWithSameAsFieldGetterAndSetterName_{" +
        "isValue=" + isValue() +
        "}" + super.toString();
  }
}
