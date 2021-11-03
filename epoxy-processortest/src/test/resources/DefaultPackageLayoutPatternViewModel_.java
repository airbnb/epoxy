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
public class DefaultPackageLayoutPatternViewModel_ extends EpoxyModel<DefaultPackageLayoutPatternView> implements GeneratedModel<DefaultPackageLayoutPatternView>, DefaultPackageLayoutPatternViewModelBuilder {
  private OnModelBoundListener<DefaultPackageLayoutPatternViewModel_, DefaultPackageLayoutPatternView> onModelBoundListener_epoxyGeneratedModel;

  private OnModelUnboundListener<DefaultPackageLayoutPatternViewModel_, DefaultPackageLayoutPatternView> onModelUnboundListener_epoxyGeneratedModel;

  private OnModelVisibilityStateChangedListener<DefaultPackageLayoutPatternViewModel_, DefaultPackageLayoutPatternView> onModelVisibilityStateChangedListener_epoxyGeneratedModel;

  private OnModelVisibilityChangedListener<DefaultPackageLayoutPatternViewModel_, DefaultPackageLayoutPatternView> onModelVisibilityChangedListener_epoxyGeneratedModel;

  @Override
  public void addTo(EpoxyController controller) {
    super.addTo(controller);
    addWithDebugValidation(controller);
  }

  @Override
  public void handlePreBind(final EpoxyViewHolder holder,
      final DefaultPackageLayoutPatternView object, final int position) {
    validateStateHasNotChangedSinceAdded("The model was changed between being added to the controller and being bound.", position);
  }

  @Override
  public void bind(final DefaultPackageLayoutPatternView object) {
    super.bind(object);
  }

  @Override
  public void bind(final DefaultPackageLayoutPatternView object, EpoxyModel previousModel) {
    if (!(previousModel instanceof DefaultPackageLayoutPatternViewModel_)) {
      bind(object);
      return;
    }
    DefaultPackageLayoutPatternViewModel_ that = (DefaultPackageLayoutPatternViewModel_) previousModel;
    super.bind(object);
  }

  @Override
  public void handlePostBind(final DefaultPackageLayoutPatternView object, int position) {
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
  public DefaultPackageLayoutPatternViewModel_ onBind(
      OnModelBoundListener<DefaultPackageLayoutPatternViewModel_, DefaultPackageLayoutPatternView> listener) {
    onMutation();
    this.onModelBoundListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public void unbind(DefaultPackageLayoutPatternView object) {
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
  public DefaultPackageLayoutPatternViewModel_ onUnbind(
      OnModelUnboundListener<DefaultPackageLayoutPatternViewModel_, DefaultPackageLayoutPatternView> listener) {
    onMutation();
    this.onModelUnboundListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public void onVisibilityStateChanged(int visibilityState,
      final DefaultPackageLayoutPatternView object) {
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
  public DefaultPackageLayoutPatternViewModel_ onVisibilityStateChanged(
      OnModelVisibilityStateChangedListener<DefaultPackageLayoutPatternViewModel_, DefaultPackageLayoutPatternView> listener) {
    onMutation();
    this.onModelVisibilityStateChangedListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public void onVisibilityChanged(float percentVisibleHeight, float percentVisibleWidth,
      int visibleHeight, int visibleWidth, final DefaultPackageLayoutPatternView object) {
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
  public DefaultPackageLayoutPatternViewModel_ onVisibilityChanged(
      OnModelVisibilityChangedListener<DefaultPackageLayoutPatternViewModel_, DefaultPackageLayoutPatternView> listener) {
    onMutation();
    this.onModelVisibilityChangedListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public DefaultPackageLayoutPatternViewModel_ id(long id) {
    super.id(id);
    return this;
  }

  @Override
  public DefaultPackageLayoutPatternViewModel_ id(@Nullable Number... ids) {
    super.id(ids);
    return this;
  }

  @Override
  public DefaultPackageLayoutPatternViewModel_ id(long id1, long id2) {
    super.id(id1, id2);
    return this;
  }

  @Override
  public DefaultPackageLayoutPatternViewModel_ id(@Nullable CharSequence key) {
    super.id(key);
    return this;
  }

  @Override
  public DefaultPackageLayoutPatternViewModel_ id(@Nullable CharSequence key,
      @Nullable CharSequence... otherKeys) {
    super.id(key, otherKeys);
    return this;
  }

  @Override
  public DefaultPackageLayoutPatternViewModel_ id(@Nullable CharSequence key, long id) {
    super.id(key, id);
    return this;
  }

  @Override
  public DefaultPackageLayoutPatternViewModel_ layout(@LayoutRes int layoutRes) {
    super.layout(layoutRes);
    return this;
  }

  @Override
  public DefaultPackageLayoutPatternViewModel_ spanSizeOverride(
      @Nullable EpoxyModel.SpanSizeOverrideCallback spanSizeCallback) {
    super.spanSizeOverride(spanSizeCallback);
    return this;
  }

  @Override
  public DefaultPackageLayoutPatternViewModel_ show() {
    super.show();
    return this;
  }

  @Override
  public DefaultPackageLayoutPatternViewModel_ show(boolean show) {
    super.show(show);
    return this;
  }

  @Override
  public DefaultPackageLayoutPatternViewModel_ hide() {
    super.hide();
    return this;
  }

  @Override
  @LayoutRes
  protected int getDefaultLayout() {
    return R.layout.default_package_layout_pattern_view;
  }

  @Override
  public DefaultPackageLayoutPatternViewModel_ reset() {
    onModelBoundListener_epoxyGeneratedModel = null;
    onModelUnboundListener_epoxyGeneratedModel = null;
    onModelVisibilityStateChangedListener_epoxyGeneratedModel = null;
    onModelVisibilityChangedListener_epoxyGeneratedModel = null;
    super.reset();
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof DefaultPackageLayoutPatternViewModel_)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    DefaultPackageLayoutPatternViewModel_ that = (DefaultPackageLayoutPatternViewModel_) o;
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
    return true;
  }

  @Override
  public int hashCode() {
    int _result = super.hashCode();
    _result = 31 * _result + (onModelBoundListener_epoxyGeneratedModel != null ? 1 : 0);
    _result = 31 * _result + (onModelUnboundListener_epoxyGeneratedModel != null ? 1 : 0);
    _result = 31 * _result + (onModelVisibilityStateChangedListener_epoxyGeneratedModel != null ? 1 : 0);
    _result = 31 * _result + (onModelVisibilityChangedListener_epoxyGeneratedModel != null ? 1 : 0);
    return _result;
  }

  @Override
  public String toString() {
    return "DefaultPackageLayoutPatternViewModel_{" +
        "}" + super.toString();
  }

  @Override
  public int getSpanSize(int totalSpanCount, int position, int itemCount) {
    return totalSpanCount;
  }
}
