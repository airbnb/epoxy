package com.airbnb.epoxy;

import android.view.ViewGroup;
import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import java.lang.CharSequence;
import java.lang.NullPointerException;
import java.lang.Number;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.UnsupportedOperationException;

/**
 * Generated file. Do not modify!
 */
public class AutoLayoutModelViewManualLayoutParamsModel_ extends EpoxyModel<AutoLayoutModelViewManualLayoutParams> implements GeneratedModel<AutoLayoutModelViewManualLayoutParams>, AutoLayoutModelViewManualLayoutParamsModelBuilder {
  private OnModelBoundListener<AutoLayoutModelViewManualLayoutParamsModel_, AutoLayoutModelViewManualLayoutParams> onModelBoundListener_epoxyGeneratedModel;

  private OnModelUnboundListener<AutoLayoutModelViewManualLayoutParamsModel_, AutoLayoutModelViewManualLayoutParams> onModelUnboundListener_epoxyGeneratedModel;

  private OnModelVisibilityStateChangedListener<AutoLayoutModelViewManualLayoutParamsModel_, AutoLayoutModelViewManualLayoutParams> onModelVisibilityStateChangedListener_epoxyGeneratedModel;

  private OnModelVisibilityChangedListener<AutoLayoutModelViewManualLayoutParamsModel_, AutoLayoutModelViewManualLayoutParams> onModelVisibilityChangedListener_epoxyGeneratedModel;

  private int value_Int = 0;

  @Override
  public void addTo(EpoxyController controller) {
    super.addTo(controller);
    addWithDebugValidation(controller);
  }

  @Override
  protected int getViewType() {
    return 0;
  }

  @Override
  public AutoLayoutModelViewManualLayoutParams buildView(ViewGroup parent) {
    AutoLayoutModelViewManualLayoutParams v = new AutoLayoutModelViewManualLayoutParams(parent.getContext());
    if (v.getLayoutParams() == null) {
      throw new NullPointerException("Layout params is required to be set for Size.MANUAL");
    }
    return v;
  }

  @Override
  public void handlePreBind(final EpoxyViewHolder holder,
      final AutoLayoutModelViewManualLayoutParams object, final int position) {
    validateStateHasNotChangedSinceAdded("The model was changed between being added to the controller and being bound.", position);
  }

  @Override
  public void bind(final AutoLayoutModelViewManualLayoutParams object) {
    super.bind(object);
    object.setValue(value_Int);
  }

  @Override
  public void bind(final AutoLayoutModelViewManualLayoutParams object, EpoxyModel previousModel) {
    if (!(previousModel instanceof AutoLayoutModelViewManualLayoutParamsModel_)) {
      bind(object);
      return;
    }
    AutoLayoutModelViewManualLayoutParamsModel_ that = (AutoLayoutModelViewManualLayoutParamsModel_) previousModel;
    super.bind(object);

    if ((value_Int != that.value_Int)) {
      object.setValue(value_Int);
    }
  }

  @Override
  public void handlePostBind(final AutoLayoutModelViewManualLayoutParams object, int position) {
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
  public AutoLayoutModelViewManualLayoutParamsModel_ onBind(
      OnModelBoundListener<AutoLayoutModelViewManualLayoutParamsModel_, AutoLayoutModelViewManualLayoutParams> listener) {
    onMutation();
    this.onModelBoundListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public void unbind(AutoLayoutModelViewManualLayoutParams object) {
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
  public AutoLayoutModelViewManualLayoutParamsModel_ onUnbind(
      OnModelUnboundListener<AutoLayoutModelViewManualLayoutParamsModel_, AutoLayoutModelViewManualLayoutParams> listener) {
    onMutation();
    this.onModelUnboundListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public void onVisibilityStateChanged(int visibilityState,
      final AutoLayoutModelViewManualLayoutParams object) {
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
  public AutoLayoutModelViewManualLayoutParamsModel_ onVisibilityStateChanged(
      OnModelVisibilityStateChangedListener<AutoLayoutModelViewManualLayoutParamsModel_, AutoLayoutModelViewManualLayoutParams> listener) {
    onMutation();
    this.onModelVisibilityStateChangedListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public void onVisibilityChanged(float percentVisibleHeight, float percentVisibleWidth,
      int visibleHeight, int visibleWidth, final AutoLayoutModelViewManualLayoutParams object) {
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
  public AutoLayoutModelViewManualLayoutParamsModel_ onVisibilityChanged(
      OnModelVisibilityChangedListener<AutoLayoutModelViewManualLayoutParamsModel_, AutoLayoutModelViewManualLayoutParams> listener) {
    onMutation();
    this.onModelVisibilityChangedListener_epoxyGeneratedModel = listener;
    return this;
  }

  /**
   * <i>Optional</i>: Default value is 0
   *
   * @see AutoLayoutModelViewManualLayoutParams#setValue(int)
   */
  public AutoLayoutModelViewManualLayoutParamsModel_ value(int value) {
    onMutation();
    this.value_Int = value;
    return this;
  }

  public int value() {
    return value_Int;
  }

  @Override
  public AutoLayoutModelViewManualLayoutParamsModel_ id(long p0) {
    super.id(p0);
    return this;
  }

  @Override
  public AutoLayoutModelViewManualLayoutParamsModel_ id(@Nullable Number... p0) {
    super.id(p0);
    return this;
  }

  @Override
  public AutoLayoutModelViewManualLayoutParamsModel_ id(long p0, long p1) {
    super.id(p0, p1);
    return this;
  }

  @Override
  public AutoLayoutModelViewManualLayoutParamsModel_ id(@Nullable CharSequence p0) {
    super.id(p0);
    return this;
  }

  @Override
  public AutoLayoutModelViewManualLayoutParamsModel_ id(@Nullable CharSequence p0,
      @Nullable CharSequence... p1) {
    super.id(p0, p1);
    return this;
  }

  @Override
  public AutoLayoutModelViewManualLayoutParamsModel_ id(@Nullable CharSequence p0, long p1) {
    super.id(p0, p1);
    return this;
  }

  @Override
  public AutoLayoutModelViewManualLayoutParamsModel_ layout(@LayoutRes int p0) {
    throw new UnsupportedOperationException("Layout resources are unsupported with programmatic views.");
  }

  @Override
  public AutoLayoutModelViewManualLayoutParamsModel_ spanSizeOverride(
      @Nullable EpoxyModel.SpanSizeOverrideCallback p0) {
    super.spanSizeOverride(p0);
    return this;
  }

  @Override
  public AutoLayoutModelViewManualLayoutParamsModel_ show() {
    super.show();
    return this;
  }

  @Override
  public AutoLayoutModelViewManualLayoutParamsModel_ show(boolean p0) {
    super.show(p0);
    return this;
  }

  @Override
  public AutoLayoutModelViewManualLayoutParamsModel_ hide() {
    super.hide();
    return this;
  }

  @Override
  @LayoutRes
  protected int getDefaultLayout() {
    throw new UnsupportedOperationException("Layout resources are unsupported for views created programmatically.");
  }

  @Override
  public AutoLayoutModelViewManualLayoutParamsModel_ reset() {
    onModelBoundListener_epoxyGeneratedModel = null;
    onModelUnboundListener_epoxyGeneratedModel = null;
    onModelVisibilityStateChangedListener_epoxyGeneratedModel = null;
    onModelVisibilityChangedListener_epoxyGeneratedModel = null;
    this.value_Int = 0;
    super.reset();
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof AutoLayoutModelViewManualLayoutParamsModel_)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    AutoLayoutModelViewManualLayoutParamsModel_ that = (AutoLayoutModelViewManualLayoutParamsModel_) o;
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
    if ((value_Int != that.value_Int)) {
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
    _result = 31 * _result + value_Int;
    return _result;
  }

  @Override
  public String toString() {
    return "AutoLayoutModelViewManualLayoutParamsModel_{" +
        "value_Int=" + value_Int +
        "}" + super.toString();
  }

  @Override
  public int getSpanSize(int totalSpanCount, int position, int itemCount) {
    return totalSpanCount;
  }
}
