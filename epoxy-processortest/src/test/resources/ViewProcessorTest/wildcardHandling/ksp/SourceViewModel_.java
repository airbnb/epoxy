package com.airbnb.epoxy;

import android.view.View;
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
public class SourceViewModel_ extends EpoxyModel<SourceView> implements GeneratedModel<SourceView>, SourceViewModelBuilder {
  private OnModelBoundListener<SourceViewModel_, SourceView> onModelBoundListener_epoxyGeneratedModel;

  private OnModelUnboundListener<SourceViewModel_, SourceView> onModelUnboundListener_epoxyGeneratedModel;

  private OnModelVisibilityStateChangedListener<SourceViewModel_, SourceView> onModelVisibilityStateChangedListener_epoxyGeneratedModel;

  private OnModelVisibilityChangedListener<SourceViewModel_, SourceView> onModelVisibilityChangedListener_epoxyGeneratedModel;

  @Nullable
  private KeyedListener<?, View.OnClickListener> keyedListener_KeyedListener = (KeyedListener<?, View.OnClickListener>) null;

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
  public SourceView buildView(ViewGroup parent) {
    SourceView v = new SourceView(parent.getContext());
    v.setLayoutParams(new ViewGroup.MarginLayoutParams(ViewGroup.MarginLayoutParams.MATCH_PARENT, ViewGroup.MarginLayoutParams.WRAP_CONTENT));
    return v;
  }

  @Override
  public void handlePreBind(final EpoxyViewHolder holder, final SourceView object,
      final int position) {
    validateStateHasNotChangedSinceAdded("The model was changed between being added to the controller and being bound.", position);
  }

  @Override
  public void bind(final SourceView object) {
    super.bind(object);
    object.setKeyedListener(keyedListener_KeyedListener);
  }

  @Override
  public void bind(final SourceView object, EpoxyModel previousModel) {
    if (!(previousModel instanceof SourceViewModel_)) {
      bind(object);
      return;
    }
    SourceViewModel_ that = (SourceViewModel_) previousModel;
    super.bind(object);

    if (((keyedListener_KeyedListener == null) != (that.keyedListener_KeyedListener == null))) {
      object.setKeyedListener(keyedListener_KeyedListener);
    }
  }

  @Override
  public void handlePostBind(final SourceView object, int position) {
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
  public SourceViewModel_ onBind(OnModelBoundListener<SourceViewModel_, SourceView> listener) {
    onMutation();
    this.onModelBoundListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public void unbind(SourceView object) {
    super.unbind(object);
    if (onModelUnboundListener_epoxyGeneratedModel != null) {
      onModelUnboundListener_epoxyGeneratedModel.onModelUnbound(this, object);
    }
    object.setKeyedListener((KeyedListener<?, View.OnClickListener>) null);
  }

  /**
   * Register a listener that will be called when this model is unbound from a view.
   * <p>
   * The listener will contribute to this model's hashCode state per the {@link
   * com.airbnb.epoxy.EpoxyAttribute.Option#DoNotHash} rules.
   * <p>
   * You may clear the listener by setting a null value, or by calling {@link #reset()}
   */
  public SourceViewModel_ onUnbind(OnModelUnboundListener<SourceViewModel_, SourceView> listener) {
    onMutation();
    this.onModelUnboundListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public void onVisibilityStateChanged(int visibilityState, final SourceView object) {
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
  public SourceViewModel_ onVisibilityStateChanged(
      OnModelVisibilityStateChangedListener<SourceViewModel_, SourceView> listener) {
    onMutation();
    this.onModelVisibilityStateChangedListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public void onVisibilityChanged(float percentVisibleHeight, float percentVisibleWidth,
      int visibleHeight, int visibleWidth, final SourceView object) {
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
  public SourceViewModel_ onVisibilityChanged(
      OnModelVisibilityChangedListener<SourceViewModel_, SourceView> listener) {
    onMutation();
    this.onModelVisibilityChangedListener_epoxyGeneratedModel = listener;
    return this;
  }

  /**
   * <i>Optional</i>: Default value is (KeyedListener<?, View.OnClickListener>) null
   *
   * @see SourceView#setKeyedListener(KeyedListener<?, View.OnClickListener>)
   */
  public SourceViewModel_ keyedListener(
      @Nullable KeyedListener<?, View.OnClickListener> keyedListener) {
    onMutation();
    this.keyedListener_KeyedListener = keyedListener;
    return this;
  }

  @Nullable
  public KeyedListener<?, View.OnClickListener> keyedListener() {
    return keyedListener_KeyedListener;
  }

  @Override
  public SourceViewModel_ id(long p0) {
    super.id(p0);
    return this;
  }

  @Override
  public SourceViewModel_ id(@Nullable Number... p0) {
    super.id(p0);
    return this;
  }

  @Override
  public SourceViewModel_ id(long p0, long p1) {
    super.id(p0, p1);
    return this;
  }

  @Override
  public SourceViewModel_ id(@Nullable CharSequence p0) {
    super.id(p0);
    return this;
  }

  @Override
  public SourceViewModel_ id(@Nullable CharSequence p0, @Nullable CharSequence... p1) {
    super.id(p0, p1);
    return this;
  }

  @Override
  public SourceViewModel_ id(@Nullable CharSequence p0, long p1) {
    super.id(p0, p1);
    return this;
  }

  @Override
  public SourceViewModel_ layout(@LayoutRes int p0) {
    throw new UnsupportedOperationException("Layout resources are unsupported with programmatic views.");
  }

  @Override
  public SourceViewModel_ spanSizeOverride(@Nullable EpoxyModel.SpanSizeOverrideCallback p0) {
    super.spanSizeOverride(p0);
    return this;
  }

  @Override
  public SourceViewModel_ show() {
    super.show();
    return this;
  }

  @Override
  public SourceViewModel_ show(boolean p0) {
    super.show(p0);
    return this;
  }

  @Override
  public SourceViewModel_ hide() {
    super.hide();
    return this;
  }

  @Override
  @LayoutRes
  protected int getDefaultLayout() {
    throw new UnsupportedOperationException("Layout resources are unsupported for views created programmatically.");
  }

  @Override
  public SourceViewModel_ reset() {
    onModelBoundListener_epoxyGeneratedModel = null;
    onModelUnboundListener_epoxyGeneratedModel = null;
    onModelVisibilityStateChangedListener_epoxyGeneratedModel = null;
    onModelVisibilityChangedListener_epoxyGeneratedModel = null;
    this.keyedListener_KeyedListener = (KeyedListener<?, View.OnClickListener>) null;
    super.reset();
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof SourceViewModel_)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    SourceViewModel_ that = (SourceViewModel_) o;
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
    if (((keyedListener_KeyedListener == null) != (that.keyedListener_KeyedListener == null))) {
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
    _result = 31 * _result + (keyedListener_KeyedListener != null ? 1 : 0);
    return _result;
  }

  @Override
  public String toString() {
    return "SourceViewModel_{" +
        "keyedListener_KeyedListener=" + keyedListener_KeyedListener +
        "}" + super.toString();
  }

  @Override
  public int getSpanSize(int totalSpanCount, int position, int itemCount) {
    return totalSpanCount;
  }
}
