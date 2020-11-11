package com.airbnb.epoxy;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.databinding.ViewDataBinding;
import java.lang.CharSequence;
import java.lang.IllegalStateException;
import java.lang.Number;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;

/**
 * Generated file. Do not modify!
 */
public class ModelWithDataBindingBindingModel_ extends DataBindingEpoxyModel implements GeneratedModel<DataBindingEpoxyModel.DataBindingHolder>, ModelWithDataBindingBindingModelBuilder {
  private OnModelBoundListener<ModelWithDataBindingBindingModel_, DataBindingEpoxyModel.DataBindingHolder> onModelBoundListener_epoxyGeneratedModel;

  private OnModelUnboundListener<ModelWithDataBindingBindingModel_, DataBindingEpoxyModel.DataBindingHolder> onModelUnboundListener_epoxyGeneratedModel;

  private OnModelVisibilityStateChangedListener<ModelWithDataBindingBindingModel_, DataBindingEpoxyModel.DataBindingHolder> onModelVisibilityStateChangedListener_epoxyGeneratedModel;

  private OnModelVisibilityChangedListener<ModelWithDataBindingBindingModel_, DataBindingEpoxyModel.DataBindingHolder> onModelVisibilityChangedListener_epoxyGeneratedModel;

  private String stringValue;

  @Override
  public void addTo(EpoxyController controller) {
    super.addTo(controller);
    addWithDebugValidation(controller);
  }

  @Override
  public void handlePreBind(final EpoxyViewHolder holder,
      final DataBindingEpoxyModel.DataBindingHolder object, final int position) {
    validateStateHasNotChangedSinceAdded("The model was changed between being added to the controller and being bound.", position);
  }

  @Override
  public void handlePostBind(final DataBindingEpoxyModel.DataBindingHolder object, int position) {
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
  public ModelWithDataBindingBindingModel_ onBind(
      OnModelBoundListener<ModelWithDataBindingBindingModel_, DataBindingEpoxyModel.DataBindingHolder> listener) {
    onMutation();
    this.onModelBoundListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public void unbind(DataBindingEpoxyModel.DataBindingHolder object) {
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
  public ModelWithDataBindingBindingModel_ onUnbind(
      OnModelUnboundListener<ModelWithDataBindingBindingModel_, DataBindingEpoxyModel.DataBindingHolder> listener) {
    onMutation();
    this.onModelUnboundListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public void onVisibilityStateChanged(int visibilityState,
      final DataBindingEpoxyModel.DataBindingHolder object) {
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
  public ModelWithDataBindingBindingModel_ onVisibilityStateChanged(
      OnModelVisibilityStateChangedListener<ModelWithDataBindingBindingModel_, DataBindingEpoxyModel.DataBindingHolder> listener) {
    onMutation();
    this.onModelVisibilityStateChangedListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public void onVisibilityChanged(float percentVisibleHeight, float percentVisibleWidth,
      int visibleHeight, int visibleWidth, final DataBindingEpoxyModel.DataBindingHolder object) {
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
  public ModelWithDataBindingBindingModel_ onVisibilityChanged(
      OnModelVisibilityChangedListener<ModelWithDataBindingBindingModel_, DataBindingEpoxyModel.DataBindingHolder> listener) {
    onMutation();
    this.onModelVisibilityChangedListener_epoxyGeneratedModel = listener;
    return this;
  }

  public ModelWithDataBindingBindingModel_ stringValue(String stringValue) {
    onMutation();
    this.stringValue = stringValue;
    return this;
  }

  public String stringValue() {
    return stringValue;
  }

  @Override
  public ModelWithDataBindingBindingModel_ id(long id) {
    super.id(id);
    return this;
  }

  @Override
  public ModelWithDataBindingBindingModel_ id(@Nullable Number... arg0) {
    super.id(arg0);
    return this;
  }

  @Override
  public ModelWithDataBindingBindingModel_ id(long id1, long id2) {
    super.id(id1, id2);
    return this;
  }

  @Override
  public ModelWithDataBindingBindingModel_ id(@Nullable CharSequence arg0) {
    super.id(arg0);
    return this;
  }

  @Override
  public ModelWithDataBindingBindingModel_ id(@Nullable CharSequence arg0,
      @Nullable CharSequence... arg1) {
    super.id(arg0, arg1);
    return this;
  }

  @Override
  public ModelWithDataBindingBindingModel_ id(@Nullable CharSequence arg0, long arg1) {
    super.id(arg0, arg1);
    return this;
  }

  @Override
  public ModelWithDataBindingBindingModel_ layout(@LayoutRes int arg0) {
    super.layout(arg0);
    return this;
  }

  @Override
  public ModelWithDataBindingBindingModel_ spanSizeOverride(
      @Nullable EpoxyModel.SpanSizeOverrideCallback arg0) {
    super.spanSizeOverride(arg0);
    return this;
  }

  @Override
  public ModelWithDataBindingBindingModel_ show() {
    super.show();
    return this;
  }

  @Override
  public ModelWithDataBindingBindingModel_ show(boolean show) {
    super.show(show);
    return this;
  }

  @Override
  public ModelWithDataBindingBindingModel_ hide() {
    super.hide();
    return this;
  }

  @Override
  @LayoutRes
  protected int getDefaultLayout() {
    return R.layout.model_with_data_binding;
  }

  @Override
  protected void setDataBindingVariables(ViewDataBinding binding) {
    if (!binding.setVariable(BR.stringValue, stringValue)) {
      throw new IllegalStateException("The attribute stringValue was defined in your data binding model (com.airbnb.epoxy.DataBindingEpoxyModel) but a data variable of that name was not found in the layout.");
    }
  }

  @Override
  protected void setDataBindingVariables(ViewDataBinding binding, EpoxyModel previousModel) {
    if (!(previousModel instanceof ModelWithDataBindingBindingModel_)) {
      setDataBindingVariables(binding);
      return;
    }
    ModelWithDataBindingBindingModel_ that = (ModelWithDataBindingBindingModel_) previousModel;
    if ((stringValue != null ? !stringValue.equals(that.stringValue) : that.stringValue != null)) {
      binding.setVariable(BR.stringValue, stringValue);
    }
  }

  @Override
  public ModelWithDataBindingBindingModel_ reset() {
    onModelBoundListener_epoxyGeneratedModel = null;
    onModelUnboundListener_epoxyGeneratedModel = null;
    onModelVisibilityStateChangedListener_epoxyGeneratedModel = null;
    onModelVisibilityChangedListener_epoxyGeneratedModel = null;
    this.stringValue = null;
    super.reset();
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof ModelWithDataBindingBindingModel_)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    ModelWithDataBindingBindingModel_ that = (ModelWithDataBindingBindingModel_) o;
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
    if ((stringValue != null ? !stringValue.equals(that.stringValue) : that.stringValue != null)) {
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
    _result = 31 * _result + (stringValue != null ? stringValue.hashCode() : 0);
    return _result;
  }

  @Override
  public String toString() {
    return "ModelWithDataBindingBindingModel_{" +
        "stringValue=" + stringValue +
        "}" + super.toString();
  }
}