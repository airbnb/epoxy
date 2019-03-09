package com.airbnb.epoxy;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import java.lang.CharSequence;
import java.lang.Number;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;

/**
 * Generated file. Do not modify! */
public class GenerateDefaultLayoutMethodNextParentLayout$WithLayout_ extends GenerateDefaultLayoutMethodNextParentLayout.WithLayout implements GeneratedModel<Object>, GenerateDefaultLayoutMethodNextParentLayout_WithLayoutBuilder {
  private OnModelBoundListener<GenerateDefaultLayoutMethodNextParentLayout$WithLayout_, Object> onModelBoundListener_epoxyGeneratedModel;

  private OnModelUnboundListener<GenerateDefaultLayoutMethodNextParentLayout$WithLayout_, Object> onModelUnboundListener_epoxyGeneratedModel;

  private OnModelVisibilityStateChangedListener<GenerateDefaultLayoutMethodNextParentLayout$WithLayout_, Object> onModelVisibilityStateChangedListener_epoxyGeneratedModel;

  private OnModelVisibilityChangedListener<GenerateDefaultLayoutMethodNextParentLayout$WithLayout_, Object> onModelVisibilityChangedListener_epoxyGeneratedModel;

  public GenerateDefaultLayoutMethodNextParentLayout$WithLayout_() {
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
   * You may clear the listener by setting a null value, or by calling {@link #reset()} */
  public GenerateDefaultLayoutMethodNextParentLayout$WithLayout_ onBind(
      OnModelBoundListener<GenerateDefaultLayoutMethodNextParentLayout$WithLayout_, Object> listener) {
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
   * You may clear the listener by setting a null value, or by calling {@link #reset()} */
  public GenerateDefaultLayoutMethodNextParentLayout$WithLayout_ onUnbind(
      OnModelUnboundListener<GenerateDefaultLayoutMethodNextParentLayout$WithLayout_, Object> listener) {
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
   * <p>
   * You may clear the listener by setting a null value, or by calling {@link #reset()} */
  public GenerateDefaultLayoutMethodNextParentLayout$WithLayout_ onVisibilityStateChanged(
      OnModelVisibilityStateChangedListener<GenerateDefaultLayoutMethodNextParentLayout$WithLayout_, Object> listener) {
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
   * <p>
   * You may clear the listener by setting a null value, or by calling {@link #reset()} */
  public GenerateDefaultLayoutMethodNextParentLayout$WithLayout_ onVisibilityChanged(
      OnModelVisibilityChangedListener<GenerateDefaultLayoutMethodNextParentLayout$WithLayout_, Object> listener) {
    onMutation();
    this.onModelVisibilityChangedListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public GenerateDefaultLayoutMethodNextParentLayout$WithLayout_ id(long id) {
    super.id(id);
    return this;
  }

  @Override
  public GenerateDefaultLayoutMethodNextParentLayout$WithLayout_ id(@Nullable Number... arg0) {
    super.id(arg0);
    return this;
  }

  @Override
  public GenerateDefaultLayoutMethodNextParentLayout$WithLayout_ id(long id1, long id2) {
    super.id(id1, id2);
    return this;
  }

  @Override
  public GenerateDefaultLayoutMethodNextParentLayout$WithLayout_ id(@Nullable CharSequence arg0) {
    super.id(arg0);
    return this;
  }

  @Override
  public GenerateDefaultLayoutMethodNextParentLayout$WithLayout_ id(@Nullable CharSequence arg0,
      @Nullable CharSequence... arg1) {
    super.id(arg0, arg1);
    return this;
  }

  @Override
  public GenerateDefaultLayoutMethodNextParentLayout$WithLayout_ id(@Nullable CharSequence arg0,
      long arg1) {
    super.id(arg0, arg1);
    return this;
  }

  @Override
  public GenerateDefaultLayoutMethodNextParentLayout$WithLayout_ layout(@LayoutRes int arg0) {
    super.layout(arg0);
    return this;
  }

  @Override
  public GenerateDefaultLayoutMethodNextParentLayout$WithLayout_ spanSizeOverride(
      @Nullable EpoxyModel.SpanSizeOverrideCallback arg0) {
    super.spanSizeOverride(arg0);
    return this;
  }

  @Override
  public GenerateDefaultLayoutMethodNextParentLayout$WithLayout_ show() {
    super.show();
    return this;
  }

  @Override
  public GenerateDefaultLayoutMethodNextParentLayout$WithLayout_ show(boolean show) {
    super.show(show);
    return this;
  }

  @Override
  public GenerateDefaultLayoutMethodNextParentLayout$WithLayout_ hide() {
    super.hide();
    return this;
  }

  @Override
  @LayoutRes
  protected int getDefaultLayout() {
    return 1;
  }

  @Override
  public GenerateDefaultLayoutMethodNextParentLayout$WithLayout_ reset() {
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
    if (!(o instanceof GenerateDefaultLayoutMethodNextParentLayout$WithLayout_)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    GenerateDefaultLayoutMethodNextParentLayout$WithLayout_ that = (GenerateDefaultLayoutMethodNextParentLayout$WithLayout_) o;
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
    int result = super.hashCode();
    result = 31 * result + (onModelBoundListener_epoxyGeneratedModel != null ? 1 : 0);
    result = 31 * result + (onModelUnboundListener_epoxyGeneratedModel != null ? 1 : 0);
    result = 31 * result + (onModelVisibilityStateChangedListener_epoxyGeneratedModel != null ? 1 : 0);
    result = 31 * result + (onModelVisibilityChangedListener_epoxyGeneratedModel != null ? 1 : 0);
    return result;
  }

  @Override
  public String toString() {
    return "GenerateDefaultLayoutMethodNextParentLayout$WithLayout_{" +
        "}" + super.toString();
  }
}
