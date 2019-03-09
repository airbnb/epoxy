package com.airbnb.epoxy;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import java.lang.CharSequence;
import java.lang.Number;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.util.BitSet;

/**
 * Generated file. Do not modify! */
public class GeneratedModelSuffixViewSuffix_ extends EpoxyModel<GeneratedModelSuffixView> implements GeneratedModel<GeneratedModelSuffixView>, GeneratedModelSuffixViewSuffixBuilder {
  private final BitSet assignedAttributes_epoxyGeneratedModel = new BitSet(0);

  private OnModelBoundListener<GeneratedModelSuffixViewSuffix_, GeneratedModelSuffixView> onModelBoundListener_epoxyGeneratedModel;

  private OnModelUnboundListener<GeneratedModelSuffixViewSuffix_, GeneratedModelSuffixView> onModelUnboundListener_epoxyGeneratedModel;

  private OnModelVisibilityStateChangedListener<GeneratedModelSuffixViewSuffix_, GeneratedModelSuffixView> onModelVisibilityStateChangedListener_epoxyGeneratedModel;

  private OnModelVisibilityChangedListener<GeneratedModelSuffixViewSuffix_, GeneratedModelSuffixView> onModelVisibilityChangedListener_epoxyGeneratedModel;

  @Override
  public void addTo(EpoxyController controller) {
    super.addTo(controller);
    addWithDebugValidation(controller);
  }

  @Override
  public void handlePreBind(final EpoxyViewHolder holder, final GeneratedModelSuffixView object,
      final int position) {
    validateStateHasNotChangedSinceAdded("The model was changed between being added to the controller and being bound.", position);
  }

  @Override
  public void bind(final GeneratedModelSuffixView object) {
    super.bind(object);
  }

  @Override
  public void bind(final GeneratedModelSuffixView object, EpoxyModel previousModel) {
    if (!(previousModel instanceof GeneratedModelSuffixViewSuffix_)) {
      bind(object);
      return;
    }
    GeneratedModelSuffixViewSuffix_ that = (GeneratedModelSuffixViewSuffix_) previousModel;
    super.bind(object);
  }

  @Override
  public void handlePostBind(final GeneratedModelSuffixView object, int position) {
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
  public GeneratedModelSuffixViewSuffix_ onBind(
      OnModelBoundListener<GeneratedModelSuffixViewSuffix_, GeneratedModelSuffixView> listener) {
    onMutation();
    this.onModelBoundListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public void unbind(GeneratedModelSuffixView object) {
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
  public GeneratedModelSuffixViewSuffix_ onUnbind(
      OnModelUnboundListener<GeneratedModelSuffixViewSuffix_, GeneratedModelSuffixView> listener) {
    onMutation();
    this.onModelUnboundListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public void onVisibilityStateChanged(int visibilityState, final GeneratedModelSuffixView object) {
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
  public GeneratedModelSuffixViewSuffix_ onVisibilityStateChanged(
      OnModelVisibilityStateChangedListener<GeneratedModelSuffixViewSuffix_, GeneratedModelSuffixView> listener) {
    onMutation();
    this.onModelVisibilityStateChangedListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public void onVisibilityChanged(float percentVisibleHeight, float percentVisibleWidth,
      int visibleHeight, int visibleWidth, final GeneratedModelSuffixView object) {
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
  public GeneratedModelSuffixViewSuffix_ onVisibilityChanged(
      OnModelVisibilityChangedListener<GeneratedModelSuffixViewSuffix_, GeneratedModelSuffixView> listener) {
    onMutation();
    this.onModelVisibilityChangedListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public GeneratedModelSuffixViewSuffix_ id(long id) {
    super.id(id);
    return this;
  }

  @Override
  public GeneratedModelSuffixViewSuffix_ id(@Nullable Number... arg0) {
    super.id(arg0);
    return this;
  }

  @Override
  public GeneratedModelSuffixViewSuffix_ id(long id1, long id2) {
    super.id(id1, id2);
    return this;
  }

  @Override
  public GeneratedModelSuffixViewSuffix_ id(@Nullable CharSequence arg0) {
    super.id(arg0);
    return this;
  }

  @Override
  public GeneratedModelSuffixViewSuffix_ id(@Nullable CharSequence arg0,
      @Nullable CharSequence... arg1) {
    super.id(arg0, arg1);
    return this;
  }

  @Override
  public GeneratedModelSuffixViewSuffix_ id(@Nullable CharSequence arg0, long arg1) {
    super.id(arg0, arg1);
    return this;
  }

  @Override
  public GeneratedModelSuffixViewSuffix_ layout(@LayoutRes int arg0) {
    super.layout(arg0);
    return this;
  }

  @Override
  public GeneratedModelSuffixViewSuffix_ spanSizeOverride(
      @Nullable EpoxyModel.SpanSizeOverrideCallback arg0) {
    super.spanSizeOverride(arg0);
    return this;
  }

  @Override
  public GeneratedModelSuffixViewSuffix_ show() {
    super.show();
    return this;
  }

  @Override
  public GeneratedModelSuffixViewSuffix_ show(boolean show) {
    super.show(show);
    return this;
  }

  @Override
  public GeneratedModelSuffixViewSuffix_ hide() {
    super.hide();
    return this;
  }

  @Override
  @LayoutRes
  protected int getDefaultLayout() {
    return R.layout.generated_model_suffix_view;
  }

  @Override
  public GeneratedModelSuffixViewSuffix_ reset() {
    onModelBoundListener_epoxyGeneratedModel = null;
    onModelUnboundListener_epoxyGeneratedModel = null;
    onModelVisibilityStateChangedListener_epoxyGeneratedModel = null;
    onModelVisibilityChangedListener_epoxyGeneratedModel = null;
    assignedAttributes_epoxyGeneratedModel.clear();
    super.reset();
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof GeneratedModelSuffixViewSuffix_)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    GeneratedModelSuffixViewSuffix_ that = (GeneratedModelSuffixViewSuffix_) o;
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
    return "GeneratedModelSuffixViewSuffix_{" +
        "}" + super.toString();
  }

  @Override
  public int getSpanSize(int totalSpanCount, int position, int itemCount) {
    return totalSpanCount;
  }
}
