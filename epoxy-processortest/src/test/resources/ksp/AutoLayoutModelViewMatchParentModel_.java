package com.airbnb.epoxy;

import android.view.ViewGroup;
import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import java.lang.CharSequence;
import java.lang.Number;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.UnsupportedOperationException;

/**
 * Generated file. Do not modify!
 */
public class AutoLayoutModelViewMatchParentModel_ extends EpoxyModel<AutoLayoutModelViewMatchParent> implements GeneratedModel<AutoLayoutModelViewMatchParent>, AutoLayoutModelViewMatchParentModelBuilder {
  private OnModelBoundListener<AutoLayoutModelViewMatchParentModel_, AutoLayoutModelViewMatchParent> onModelBoundListener_epoxyGeneratedModel;

  private OnModelUnboundListener<AutoLayoutModelViewMatchParentModel_, AutoLayoutModelViewMatchParent> onModelUnboundListener_epoxyGeneratedModel;

  private OnModelVisibilityStateChangedListener<AutoLayoutModelViewMatchParentModel_, AutoLayoutModelViewMatchParent> onModelVisibilityStateChangedListener_epoxyGeneratedModel;

  private OnModelVisibilityChangedListener<AutoLayoutModelViewMatchParentModel_, AutoLayoutModelViewMatchParent> onModelVisibilityChangedListener_epoxyGeneratedModel;

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
  public AutoLayoutModelViewMatchParent buildView(ViewGroup parent) {
    AutoLayoutModelViewMatchParent v = new AutoLayoutModelViewMatchParent(parent.getContext());
    v.setLayoutParams(new ViewGroup.MarginLayoutParams(ViewGroup.MarginLayoutParams.MATCH_PARENT, ViewGroup.MarginLayoutParams.MATCH_PARENT));
    return v;
  }

  @Override
  public void handlePreBind(final EpoxyViewHolder holder,
      final AutoLayoutModelViewMatchParent object, final int position) {
    validateStateHasNotChangedSinceAdded("The model was changed between being added to the controller and being bound.", position);
  }

  @Override
  public void bind(final AutoLayoutModelViewMatchParent object) {
    super.bind(object);
    object.setValue(value_Int);
  }

  @Override
  public void bind(final AutoLayoutModelViewMatchParent object, EpoxyModel previousModel) {
    if (!(previousModel instanceof AutoLayoutModelViewMatchParentModel_)) {
      bind(object);
      return;
    }
    AutoLayoutModelViewMatchParentModel_ that = (AutoLayoutModelViewMatchParentModel_) previousModel;
    super.bind(object);

    if ((value_Int != that.value_Int)) {
      object.setValue(value_Int);
    }
  }

  @Override
  public void handlePostBind(final AutoLayoutModelViewMatchParent object, int position) {
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
  public AutoLayoutModelViewMatchParentModel_ onBind(
      OnModelBoundListener<AutoLayoutModelViewMatchParentModel_, AutoLayoutModelViewMatchParent> listener) {
    onMutation();
    this.onModelBoundListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public void unbind(AutoLayoutModelViewMatchParent object) {
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
  public AutoLayoutModelViewMatchParentModel_ onUnbind(
      OnModelUnboundListener<AutoLayoutModelViewMatchParentModel_, AutoLayoutModelViewMatchParent> listener) {
    onMutation();
    this.onModelUnboundListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public void onVisibilityStateChanged(int visibilityState,
      final AutoLayoutModelViewMatchParent object) {
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
  public AutoLayoutModelViewMatchParentModel_ onVisibilityStateChanged(
      OnModelVisibilityStateChangedListener<AutoLayoutModelViewMatchParentModel_, AutoLayoutModelViewMatchParent> listener) {
    onMutation();
    this.onModelVisibilityStateChangedListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public void onVisibilityChanged(float percentVisibleHeight, float percentVisibleWidth,
      int visibleHeight, int visibleWidth, final AutoLayoutModelViewMatchParent object) {
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
  public AutoLayoutModelViewMatchParentModel_ onVisibilityChanged(
      OnModelVisibilityChangedListener<AutoLayoutModelViewMatchParentModel_, AutoLayoutModelViewMatchParent> listener) {
    onMutation();
    this.onModelVisibilityChangedListener_epoxyGeneratedModel = listener;
    return this;
  }

  /**
   * <i>Optional</i>: Default value is 0
   *
   * @see AutoLayoutModelViewMatchParent#setValue(int)
   */
  public AutoLayoutModelViewMatchParentModel_ value(int value) {
    onMutation();
    this.value_Int = value;
    return this;
  }

  public int value() {
    return value_Int;
  }

  @Override
  public AutoLayoutModelViewMatchParentModel_ id(long p0) {
    super.id(p0);
    return this;
  }

  @Override
  public AutoLayoutModelViewMatchParentModel_ id(@Nullable Number... p0) {
    super.id(p0);
    return this;
  }

  @Override
  public AutoLayoutModelViewMatchParentModel_ id(long p0, long p1) {
    super.id(p0, p1);
    return this;
  }

  @Override
  public AutoLayoutModelViewMatchParentModel_ id(@Nullable CharSequence p0) {
    super.id(p0);
    return this;
  }

  @Override
  public AutoLayoutModelViewMatchParentModel_ id(@Nullable CharSequence p0,
      @Nullable CharSequence... p1) {
    super.id(p0, p1);
    return this;
  }

  @Override
  public AutoLayoutModelViewMatchParentModel_ id(@Nullable CharSequence p0, long p1) {
    super.id(p0, p1);
    return this;
  }

  @Override
  public AutoLayoutModelViewMatchParentModel_ layout(@LayoutRes int p0) {
    throw new UnsupportedOperationException("Layout resources are unsupported with programmatic views.");
  }

  @Override
  public AutoLayoutModelViewMatchParentModel_ spanSizeOverride(
      @Nullable EpoxyModel.SpanSizeOverrideCallback p0) {
    super.spanSizeOverride(p0);
    return this;
  }

  @Override
  public AutoLayoutModelViewMatchParentModel_ show() {
    super.show();
    return this;
  }

  @Override
  public AutoLayoutModelViewMatchParentModel_ show(boolean p0) {
    super.show(p0);
    return this;
  }

  @Override
  public AutoLayoutModelViewMatchParentModel_ hide() {
    super.hide();
    return this;
  }

  @Override
  @LayoutRes
  protected int getDefaultLayout() {
    throw new UnsupportedOperationException("Layout resources are unsupported for views created programmatically.");
  }

  @Override
  public AutoLayoutModelViewMatchParentModel_ reset() {
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
    if (!(o instanceof AutoLayoutModelViewMatchParentModel_)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    AutoLayoutModelViewMatchParentModel_ that = (AutoLayoutModelViewMatchParentModel_) o;
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
    return "AutoLayoutModelViewMatchParentModel_{" +
        "value_Int=" + value_Int +
        "}" + super.toString();
  }

  @Override
  public int getSpanSize(int totalSpanCount, int position, int itemCount) {
    return totalSpanCount;
  }
}
