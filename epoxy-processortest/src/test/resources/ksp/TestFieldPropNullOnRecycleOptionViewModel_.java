package com.airbnb.epoxy;

import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.LayoutRes;
import java.lang.CharSequence;
import java.lang.Number;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.UnsupportedOperationException;
import javax.annotation.Nullable;

/**
 * Generated file. Do not modify!
 */
public class TestFieldPropNullOnRecycleOptionViewModel_ extends EpoxyModel<TestFieldPropNullOnRecycleOptionView> implements GeneratedModel<TestFieldPropNullOnRecycleOptionView>, TestFieldPropNullOnRecycleOptionViewModelBuilder {
  private OnModelBoundListener<TestFieldPropNullOnRecycleOptionViewModel_, TestFieldPropNullOnRecycleOptionView> onModelBoundListener_epoxyGeneratedModel;

  private OnModelUnboundListener<TestFieldPropNullOnRecycleOptionViewModel_, TestFieldPropNullOnRecycleOptionView> onModelUnboundListener_epoxyGeneratedModel;

  private OnModelVisibilityStateChangedListener<TestFieldPropNullOnRecycleOptionViewModel_, TestFieldPropNullOnRecycleOptionView> onModelVisibilityStateChangedListener_epoxyGeneratedModel;

  private OnModelVisibilityChangedListener<TestFieldPropNullOnRecycleOptionViewModel_, TestFieldPropNullOnRecycleOptionView> onModelVisibilityChangedListener_epoxyGeneratedModel;

  @Nullable
  private View.OnClickListener value_OnClickListener = (View.OnClickListener) null;

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
  public TestFieldPropNullOnRecycleOptionView buildView(ViewGroup parent) {
    TestFieldPropNullOnRecycleOptionView v = new TestFieldPropNullOnRecycleOptionView(parent.getContext());
    v.setLayoutParams(new ViewGroup.MarginLayoutParams(ViewGroup.MarginLayoutParams.WRAP_CONTENT, ViewGroup.MarginLayoutParams.WRAP_CONTENT));
    return v;
  }

  @Override
  public void handlePreBind(final EpoxyViewHolder holder,
      final TestFieldPropNullOnRecycleOptionView object, final int position) {
    validateStateHasNotChangedSinceAdded("The model was changed between being added to the controller and being bound.", position);
  }

  @Override
  public void bind(final TestFieldPropNullOnRecycleOptionView object) {
    super.bind(object);
    object.value = value_OnClickListener;
  }

  @Override
  public void bind(final TestFieldPropNullOnRecycleOptionView object, EpoxyModel previousModel) {
    if (!(previousModel instanceof TestFieldPropNullOnRecycleOptionViewModel_)) {
      bind(object);
      return;
    }
    TestFieldPropNullOnRecycleOptionViewModel_ that = (TestFieldPropNullOnRecycleOptionViewModel_) previousModel;
    super.bind(object);

    if ((value_OnClickListener != null ? !value_OnClickListener.equals(that.value_OnClickListener) : that.value_OnClickListener != null)) {
      object.value = value_OnClickListener;
    }
  }

  @Override
  public void handlePostBind(final TestFieldPropNullOnRecycleOptionView object, int position) {
    if (onModelBoundListener_epoxyGeneratedModel != null) {
      onModelBoundListener_epoxyGeneratedModel.onModelBound(this, object, position);
    }
    validateStateHasNotChangedSinceAdded("The model was changed during the bind call.", position);
    object.call();
  }

  /**
   * Register a listener that will be called when this model is bound to a view.
   * <p>
   * The listener will contribute to this model's hashCode state per the {@link
   * com.airbnb.epoxy.EpoxyAttribute.Option#DoNotHash} rules.
   * <p>
   * You may clear the listener by setting a null value, or by calling {@link #reset()}
   */
  public TestFieldPropNullOnRecycleOptionViewModel_ onBind(
      OnModelBoundListener<TestFieldPropNullOnRecycleOptionViewModel_, TestFieldPropNullOnRecycleOptionView> listener) {
    onMutation();
    this.onModelBoundListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public void unbind(TestFieldPropNullOnRecycleOptionView object) {
    super.unbind(object);
    if (onModelUnboundListener_epoxyGeneratedModel != null) {
      onModelUnboundListener_epoxyGeneratedModel.onModelUnbound(this, object);
    }
    object.value = (View.OnClickListener) null;
  }

  /**
   * Register a listener that will be called when this model is unbound from a view.
   * <p>
   * The listener will contribute to this model's hashCode state per the {@link
   * com.airbnb.epoxy.EpoxyAttribute.Option#DoNotHash} rules.
   * <p>
   * You may clear the listener by setting a null value, or by calling {@link #reset()}
   */
  public TestFieldPropNullOnRecycleOptionViewModel_ onUnbind(
      OnModelUnboundListener<TestFieldPropNullOnRecycleOptionViewModel_, TestFieldPropNullOnRecycleOptionView> listener) {
    onMutation();
    this.onModelUnboundListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public void onVisibilityStateChanged(int visibilityState,
      final TestFieldPropNullOnRecycleOptionView object) {
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
  public TestFieldPropNullOnRecycleOptionViewModel_ onVisibilityStateChanged(
      OnModelVisibilityStateChangedListener<TestFieldPropNullOnRecycleOptionViewModel_, TestFieldPropNullOnRecycleOptionView> listener) {
    onMutation();
    this.onModelVisibilityStateChangedListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public void onVisibilityChanged(float percentVisibleHeight, float percentVisibleWidth,
      int visibleHeight, int visibleWidth, final TestFieldPropNullOnRecycleOptionView object) {
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
  public TestFieldPropNullOnRecycleOptionViewModel_ onVisibilityChanged(
      OnModelVisibilityChangedListener<TestFieldPropNullOnRecycleOptionViewModel_, TestFieldPropNullOnRecycleOptionView> listener) {
    onMutation();
    this.onModelVisibilityChangedListener_epoxyGeneratedModel = listener;
    return this;
  }

  /**
   * Set a click listener that will provide the parent view, model, and adapter position of the clicked view. This will clear the normal View.OnClickListener if one has been set
   */
  public TestFieldPropNullOnRecycleOptionViewModel_ value(
      @Nullable final OnModelClickListener<TestFieldPropNullOnRecycleOptionViewModel_, TestFieldPropNullOnRecycleOptionView> value) {
    onMutation();
    if (value == null) {
      this.value_OnClickListener = null;
    }
    else {
      this.value_OnClickListener = new WrappedEpoxyModelClickListener(value);
    }
    return this;
  }

  /**
   * <i>Optional</i>: Default value is (View.OnClickListener) null
   *
   * @see TestFieldPropNullOnRecycleOptionView#value
   */
  public TestFieldPropNullOnRecycleOptionViewModel_ value(@Nullable View.OnClickListener value) {
    onMutation();
    this.value_OnClickListener = value;
    return this;
  }

  @Nullable
  public View.OnClickListener value() {
    return value_OnClickListener;
  }

  @Override
  public TestFieldPropNullOnRecycleOptionViewModel_ id(long p0) {
    super.id(p0);
    return this;
  }

  @Override
  public TestFieldPropNullOnRecycleOptionViewModel_ id(@androidx.annotation.Nullable Number... p0) {
    super.id(p0);
    return this;
  }

  @Override
  public TestFieldPropNullOnRecycleOptionViewModel_ id(long p0, long p1) {
    super.id(p0, p1);
    return this;
  }

  @Override
  public TestFieldPropNullOnRecycleOptionViewModel_ id(
      @androidx.annotation.Nullable CharSequence p0) {
    super.id(p0);
    return this;
  }

  @Override
  public TestFieldPropNullOnRecycleOptionViewModel_ id(
      @androidx.annotation.Nullable CharSequence p0,
      @androidx.annotation.Nullable CharSequence... p1) {
    super.id(p0, p1);
    return this;
  }

  @Override
  public TestFieldPropNullOnRecycleOptionViewModel_ id(
      @androidx.annotation.Nullable CharSequence p0, long p1) {
    super.id(p0, p1);
    return this;
  }

  @Override
  public TestFieldPropNullOnRecycleOptionViewModel_ layout(@LayoutRes int p0) {
    throw new UnsupportedOperationException("Layout resources are unsupported with programmatic views.");
  }

  @Override
  public TestFieldPropNullOnRecycleOptionViewModel_ spanSizeOverride(
      @androidx.annotation.Nullable EpoxyModel.SpanSizeOverrideCallback p0) {
    super.spanSizeOverride(p0);
    return this;
  }

  @Override
  public TestFieldPropNullOnRecycleOptionViewModel_ show() {
    super.show();
    return this;
  }

  @Override
  public TestFieldPropNullOnRecycleOptionViewModel_ show(boolean p0) {
    super.show(p0);
    return this;
  }

  @Override
  public TestFieldPropNullOnRecycleOptionViewModel_ hide() {
    super.hide();
    return this;
  }

  @Override
  @LayoutRes
  protected int getDefaultLayout() {
    throw new UnsupportedOperationException("Layout resources are unsupported for views created programmatically.");
  }

  @Override
  public TestFieldPropNullOnRecycleOptionViewModel_ reset() {
    onModelBoundListener_epoxyGeneratedModel = null;
    onModelUnboundListener_epoxyGeneratedModel = null;
    onModelVisibilityStateChangedListener_epoxyGeneratedModel = null;
    onModelVisibilityChangedListener_epoxyGeneratedModel = null;
    this.value_OnClickListener = (View.OnClickListener) null;
    super.reset();
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof TestFieldPropNullOnRecycleOptionViewModel_)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    TestFieldPropNullOnRecycleOptionViewModel_ that = (TestFieldPropNullOnRecycleOptionViewModel_) o;
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
    if ((value_OnClickListener != null ? !value_OnClickListener.equals(that.value_OnClickListener) : that.value_OnClickListener != null)) {
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
    _result = 31 * _result + (value_OnClickListener != null ? value_OnClickListener.hashCode() : 0);
    return _result;
  }

  @Override
  public String toString() {
    return "TestFieldPropNullOnRecycleOptionViewModel_{" +
        "value_OnClickListener=" + value_OnClickListener +
        "}" + super.toString();
  }

  @Override
  public int getSpanSize(int totalSpanCount, int position, int itemCount) {
    return totalSpanCount;
  }
}
