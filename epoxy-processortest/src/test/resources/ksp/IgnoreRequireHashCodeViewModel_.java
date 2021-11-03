package com.airbnb.epoxy;

import android.view.View;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.lang.CharSequence;
import java.lang.IllegalArgumentException;
import java.lang.IllegalStateException;
import java.lang.Number;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.util.BitSet;

/**
 * Generated file. Do not modify!
 */
public class IgnoreRequireHashCodeViewModel_ extends EpoxyModel<IgnoreRequireHashCodeView> implements GeneratedModel<IgnoreRequireHashCodeView>, IgnoreRequireHashCodeViewModelBuilder {
  private final BitSet assignedAttributes_epoxyGeneratedModel = new BitSet(1);

  private OnModelBoundListener<IgnoreRequireHashCodeViewModel_, IgnoreRequireHashCodeView> onModelBoundListener_epoxyGeneratedModel;

  private OnModelUnboundListener<IgnoreRequireHashCodeViewModel_, IgnoreRequireHashCodeView> onModelUnboundListener_epoxyGeneratedModel;

  private OnModelVisibilityStateChangedListener<IgnoreRequireHashCodeViewModel_, IgnoreRequireHashCodeView> onModelVisibilityStateChangedListener_epoxyGeneratedModel;

  private OnModelVisibilityChangedListener<IgnoreRequireHashCodeViewModel_, IgnoreRequireHashCodeView> onModelVisibilityChangedListener_epoxyGeneratedModel;

  /**
   * Bitset index: 0
   */
  @NonNull
  private View.OnClickListener clickListener_OnClickListener;

  @Override
  public void addTo(EpoxyController controller) {
    super.addTo(controller);
    addWithDebugValidation(controller);
    if (!assignedAttributes_epoxyGeneratedModel.get(0)) {
    	throw new IllegalStateException("A value is required for setClickListener");
    }
  }

  @Override
  public void handlePreBind(final EpoxyViewHolder holder, final IgnoreRequireHashCodeView object,
      final int position) {
    validateStateHasNotChangedSinceAdded("The model was changed between being added to the controller and being bound.", position);
  }

  @Override
  public void bind(final IgnoreRequireHashCodeView object) {
    super.bind(object);
    object.setClickListener(clickListener_OnClickListener);
  }

  @Override
  public void bind(final IgnoreRequireHashCodeView object, EpoxyModel previousModel) {
    if (!(previousModel instanceof IgnoreRequireHashCodeViewModel_)) {
      bind(object);
      return;
    }
    IgnoreRequireHashCodeViewModel_ that = (IgnoreRequireHashCodeViewModel_) previousModel;
    super.bind(object);

    if ((clickListener_OnClickListener != null ? !clickListener_OnClickListener.equals(that.clickListener_OnClickListener) : that.clickListener_OnClickListener != null)) {
      object.setClickListener(clickListener_OnClickListener);
    }
  }

  @Override
  public void handlePostBind(final IgnoreRequireHashCodeView object, int position) {
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
  public IgnoreRequireHashCodeViewModel_ onBind(
      OnModelBoundListener<IgnoreRequireHashCodeViewModel_, IgnoreRequireHashCodeView> listener) {
    onMutation();
    this.onModelBoundListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public void unbind(IgnoreRequireHashCodeView object) {
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
  public IgnoreRequireHashCodeViewModel_ onUnbind(
      OnModelUnboundListener<IgnoreRequireHashCodeViewModel_, IgnoreRequireHashCodeView> listener) {
    onMutation();
    this.onModelUnboundListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public void onVisibilityStateChanged(int visibilityState,
      final IgnoreRequireHashCodeView object) {
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
  public IgnoreRequireHashCodeViewModel_ onVisibilityStateChanged(
      OnModelVisibilityStateChangedListener<IgnoreRequireHashCodeViewModel_, IgnoreRequireHashCodeView> listener) {
    onMutation();
    this.onModelVisibilityStateChangedListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public void onVisibilityChanged(float percentVisibleHeight, float percentVisibleWidth,
      int visibleHeight, int visibleWidth, final IgnoreRequireHashCodeView object) {
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
  public IgnoreRequireHashCodeViewModel_ onVisibilityChanged(
      OnModelVisibilityChangedListener<IgnoreRequireHashCodeViewModel_, IgnoreRequireHashCodeView> listener) {
    onMutation();
    this.onModelVisibilityChangedListener_epoxyGeneratedModel = listener;
    return this;
  }

  /**
   * Set a click listener that will provide the parent view, model, and adapter position of the clicked view. This will clear the normal View.OnClickListener if one has been set
   */
  public IgnoreRequireHashCodeViewModel_ clickListener(
      @NonNull final OnModelClickListener<IgnoreRequireHashCodeViewModel_, IgnoreRequireHashCodeView> clickListener) {
    assignedAttributes_epoxyGeneratedModel.set(0);
    onMutation();
    if (clickListener == null) {
      this.clickListener_OnClickListener = null;
    }
    else {
      this.clickListener_OnClickListener = new WrappedEpoxyModelClickListener(clickListener);
    }
    return this;
  }

  /**
   * <i>Required.</i>
   *
   * @see IgnoreRequireHashCodeView#setClickListener(View.OnClickListener)
   */
  public IgnoreRequireHashCodeViewModel_ clickListener(
      @NonNull View.OnClickListener clickListener) {
    if (clickListener == null) {
      throw new IllegalArgumentException("clickListener cannot be null");
    }
    assignedAttributes_epoxyGeneratedModel.set(0);
    onMutation();
    this.clickListener_OnClickListener = clickListener;
    return this;
  }

  @NonNull
  public View.OnClickListener clickListener() {
    return clickListener_OnClickListener;
  }

  @Override
  public IgnoreRequireHashCodeViewModel_ id(long p0) {
    super.id(p0);
    return this;
  }

  @Override
  public IgnoreRequireHashCodeViewModel_ id(@Nullable Number... p0) {
    super.id(p0);
    return this;
  }

  @Override
  public IgnoreRequireHashCodeViewModel_ id(long p0, long p1) {
    super.id(p0, p1);
    return this;
  }

  @Override
  public IgnoreRequireHashCodeViewModel_ id(@Nullable CharSequence p0) {
    super.id(p0);
    return this;
  }

  @Override
  public IgnoreRequireHashCodeViewModel_ id(@Nullable CharSequence p0,
      @Nullable CharSequence... p1) {
    super.id(p0, p1);
    return this;
  }

  @Override
  public IgnoreRequireHashCodeViewModel_ id(@Nullable CharSequence p0, long p1) {
    super.id(p0, p1);
    return this;
  }

  @Override
  public IgnoreRequireHashCodeViewModel_ layout(@LayoutRes int p0) {
    super.layout(p0);
    return this;
  }

  @Override
  public IgnoreRequireHashCodeViewModel_ spanSizeOverride(
      @Nullable EpoxyModel.SpanSizeOverrideCallback p0) {
    super.spanSizeOverride(p0);
    return this;
  }

  @Override
  public IgnoreRequireHashCodeViewModel_ show() {
    super.show();
    return this;
  }

  @Override
  public IgnoreRequireHashCodeViewModel_ show(boolean p0) {
    super.show(p0);
    return this;
  }

  @Override
  public IgnoreRequireHashCodeViewModel_ hide() {
    super.hide();
    return this;
  }

  @Override
  @LayoutRes
  protected int getDefaultLayout() {
    return 1;
  }

  @Override
  public IgnoreRequireHashCodeViewModel_ reset() {
    onModelBoundListener_epoxyGeneratedModel = null;
    onModelUnboundListener_epoxyGeneratedModel = null;
    onModelVisibilityStateChangedListener_epoxyGeneratedModel = null;
    onModelVisibilityChangedListener_epoxyGeneratedModel = null;
    assignedAttributes_epoxyGeneratedModel.clear();
    this.clickListener_OnClickListener = null;
    super.reset();
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof IgnoreRequireHashCodeViewModel_)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    IgnoreRequireHashCodeViewModel_ that = (IgnoreRequireHashCodeViewModel_) o;
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
    if ((clickListener_OnClickListener != null ? !clickListener_OnClickListener.equals(that.clickListener_OnClickListener) : that.clickListener_OnClickListener != null)) {
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
    _result = 31 * _result + (clickListener_OnClickListener != null ? clickListener_OnClickListener.hashCode() : 0);
    return _result;
  }

  @Override
  public String toString() {
    return "IgnoreRequireHashCodeViewModel_{" +
        "clickListener_OnClickListener=" + clickListener_OnClickListener +
        "}" + super.toString();
  }

  @Override
  public int getSpanSize(int totalSpanCount, int position, int itemCount) {
    return totalSpanCount;
  }
}
