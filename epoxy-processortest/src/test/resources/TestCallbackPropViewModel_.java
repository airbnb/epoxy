package com.airbnb.epoxy;

import android.view.View;
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
public class TestCallbackPropViewModel_ extends EpoxyModel<TestCallbackPropView> implements GeneratedModel<TestCallbackPropView>, TestCallbackPropViewModelBuilder {
  private OnModelBoundListener<TestCallbackPropViewModel_, TestCallbackPropView> onModelBoundListener_epoxyGeneratedModel;

  private OnModelUnboundListener<TestCallbackPropViewModel_, TestCallbackPropView> onModelUnboundListener_epoxyGeneratedModel;

  private OnModelVisibilityStateChangedListener<TestCallbackPropViewModel_, TestCallbackPropView> onModelVisibilityStateChangedListener_epoxyGeneratedModel;

  private OnModelVisibilityChangedListener<TestCallbackPropViewModel_, TestCallbackPropView> onModelVisibilityChangedListener_epoxyGeneratedModel;

  @Nullable
  private View.OnClickListener listener_OnClickListener = (View.OnClickListener) null;

  @Override
  public void addTo(EpoxyController controller) {
    super.addTo(controller);
    addWithDebugValidation(controller);
  }

  @Override
  public void handlePreBind(final EpoxyViewHolder holder, final TestCallbackPropView object,
      final int position) {
    validateStateHasNotChangedSinceAdded("The model was changed between being added to the controller and being bound.", position);
  }

  @Override
  public void bind(final TestCallbackPropView object) {
    super.bind(object);
    object.setListener(listener_OnClickListener);
  }

  @Override
  public void bind(final TestCallbackPropView object, EpoxyModel previousModel) {
    if (!(previousModel instanceof TestCallbackPropViewModel_)) {
      bind(object);
      return;
    }
    TestCallbackPropViewModel_ that = (TestCallbackPropViewModel_) previousModel;
    super.bind(object);

    if (((listener_OnClickListener == null) != (that.listener_OnClickListener == null))) {
      object.setListener(listener_OnClickListener);
    }
  }

  @Override
  public void handlePostBind(final TestCallbackPropView object, int position) {
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
  public TestCallbackPropViewModel_ onBind(
      OnModelBoundListener<TestCallbackPropViewModel_, TestCallbackPropView> listener) {
    onMutation();
    this.onModelBoundListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public void unbind(TestCallbackPropView object) {
    super.unbind(object);
    if (onModelUnboundListener_epoxyGeneratedModel != null) {
      onModelUnboundListener_epoxyGeneratedModel.onModelUnbound(this, object);
    }
    object.setListener((View.OnClickListener) null);
  }

  /**
   * Register a listener that will be called when this model is unbound from a view.
   * <p>
   * The listener will contribute to this model's hashCode state per the {@link
   * com.airbnb.epoxy.EpoxyAttribute.Option#DoNotHash} rules.
   * <p>
   * You may clear the listener by setting a null value, or by calling {@link #reset()}
   */
  public TestCallbackPropViewModel_ onUnbind(
      OnModelUnboundListener<TestCallbackPropViewModel_, TestCallbackPropView> listener) {
    onMutation();
    this.onModelUnboundListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public void onVisibilityStateChanged(int visibilityState, final TestCallbackPropView object) {
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
  public TestCallbackPropViewModel_ onVisibilityStateChanged(
      OnModelVisibilityStateChangedListener<TestCallbackPropViewModel_, TestCallbackPropView> listener) {
    onMutation();
    this.onModelVisibilityStateChangedListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public void onVisibilityChanged(float percentVisibleHeight, float percentVisibleWidth,
      int visibleHeight, int visibleWidth, final TestCallbackPropView object) {
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
  public TestCallbackPropViewModel_ onVisibilityChanged(
      OnModelVisibilityChangedListener<TestCallbackPropViewModel_, TestCallbackPropView> listener) {
    onMutation();
    this.onModelVisibilityChangedListener_epoxyGeneratedModel = listener;
    return this;
  }

  /**
   * Set a click listener that will provide the parent view, model, and adapter position of the clicked view. This will clear the normal View.OnClickListener if one has been set
   */
  public TestCallbackPropViewModel_ listener(
      @Nullable final OnModelClickListener<TestCallbackPropViewModel_, TestCallbackPropView> listener) {
    onMutation();
    if (listener == null) {
      this.listener_OnClickListener = null;
    }
    else {
      this.listener_OnClickListener = new WrappedEpoxyModelClickListener(listener);
    }
    return this;
  }

  /**
   * <i>Optional</i>: Default value is (View.OnClickListener) null
   *
   * @see TestCallbackPropView#setListener(View.OnClickListener)
   */
  public TestCallbackPropViewModel_ listener(@Nullable View.OnClickListener listener) {
    onMutation();
    this.listener_OnClickListener = listener;
    return this;
  }

  @Nullable
  public View.OnClickListener listener() {
    return listener_OnClickListener;
  }

  @Override
  public TestCallbackPropViewModel_ id(long id) {
    super.id(id);
    return this;
  }

  @Override
  public TestCallbackPropViewModel_ id(@Nullable Number... ids) {
    super.id(ids);
    return this;
  }

  @Override
  public TestCallbackPropViewModel_ id(long id1, long id2) {
    super.id(id1, id2);
    return this;
  }

  @Override
  public TestCallbackPropViewModel_ id(@Nullable CharSequence key) {
    super.id(key);
    return this;
  }

  @Override
  public TestCallbackPropViewModel_ id(@Nullable CharSequence key,
      @Nullable CharSequence... otherKeys) {
    super.id(key, otherKeys);
    return this;
  }

  @Override
  public TestCallbackPropViewModel_ id(@Nullable CharSequence key, long id) {
    super.id(key, id);
    return this;
  }

  @Override
  public TestCallbackPropViewModel_ layout(@LayoutRes int layoutRes) {
    super.layout(layoutRes);
    return this;
  }

  @Override
  public TestCallbackPropViewModel_ spanSizeOverride(
      @Nullable EpoxyModel.SpanSizeOverrideCallback spanSizeCallback) {
    super.spanSizeOverride(spanSizeCallback);
    return this;
  }

  @Override
  public TestCallbackPropViewModel_ show() {
    super.show();
    return this;
  }

  @Override
  public TestCallbackPropViewModel_ show(boolean show) {
    super.show(show);
    return this;
  }

  @Override
  public TestCallbackPropViewModel_ hide() {
    super.hide();
    return this;
  }

  @Override
  @LayoutRes
  protected int getDefaultLayout() {
    return 1;
  }

  @Override
  public TestCallbackPropViewModel_ reset() {
    onModelBoundListener_epoxyGeneratedModel = null;
    onModelUnboundListener_epoxyGeneratedModel = null;
    onModelVisibilityStateChangedListener_epoxyGeneratedModel = null;
    onModelVisibilityChangedListener_epoxyGeneratedModel = null;
    this.listener_OnClickListener = (View.OnClickListener) null;
    super.reset();
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof TestCallbackPropViewModel_)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    TestCallbackPropViewModel_ that = (TestCallbackPropViewModel_) o;
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
    if (((listener_OnClickListener == null) != (that.listener_OnClickListener == null))) {
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
    _result = 31 * _result + (listener_OnClickListener != null ? 1 : 0);
    return _result;
  }

  @Override
  public String toString() {
    return "TestCallbackPropViewModel_{" +
        "listener_OnClickListener=" + listener_OnClickListener +
        "}" + super.toString();
  }

  @Override
  public int getSpanSize(int totalSpanCount, int position, int itemCount) {
    return totalSpanCount;
  }
}
