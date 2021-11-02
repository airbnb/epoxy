package com.airbnb.epoxy;

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
public class OnVisibilityStateChangedViewModel_ extends EpoxyModel<OnVisibilityStateChangedView> implements GeneratedModel<OnVisibilityStateChangedView>, OnVisibilityStateChangedViewModelBuilder {
  private final BitSet assignedAttributes_epoxyGeneratedModel = new BitSet(1);

  private OnModelBoundListener<OnVisibilityStateChangedViewModel_, OnVisibilityStateChangedView> onModelBoundListener_epoxyGeneratedModel;

  private OnModelUnboundListener<OnVisibilityStateChangedViewModel_, OnVisibilityStateChangedView> onModelUnboundListener_epoxyGeneratedModel;

  private OnModelVisibilityStateChangedListener<OnVisibilityStateChangedViewModel_, OnVisibilityStateChangedView> onModelVisibilityStateChangedListener_epoxyGeneratedModel;

  private OnModelVisibilityChangedListener<OnVisibilityStateChangedViewModel_, OnVisibilityStateChangedView> onModelVisibilityChangedListener_epoxyGeneratedModel;

  /**
   * Bitset index: 0
   */
  @NonNull
  private CharSequence title_CharSequence;

  @Override
  public void addTo(EpoxyController controller) {
    super.addTo(controller);
    addWithDebugValidation(controller);
    if (!assignedAttributes_epoxyGeneratedModel.get(0)) {
    	throw new IllegalStateException("A value is required for setTitle");
    }
  }

  @Override
  public void handlePreBind(final EpoxyViewHolder holder, final OnVisibilityStateChangedView object,
      final int position) {
    validateStateHasNotChangedSinceAdded("The model was changed between being added to the controller and being bound.", position);
  }

  @Override
  public void bind(final OnVisibilityStateChangedView object) {
    super.bind(object);
    object.setTitle(title_CharSequence);
  }

  @Override
  public void bind(final OnVisibilityStateChangedView object, EpoxyModel previousModel) {
    if (!(previousModel instanceof OnVisibilityStateChangedViewModel_)) {
      bind(object);
      return;
    }
    OnVisibilityStateChangedViewModel_ that = (OnVisibilityStateChangedViewModel_) previousModel;
    super.bind(object);

    if ((title_CharSequence != null ? !title_CharSequence.equals(that.title_CharSequence) : that.title_CharSequence != null)) {
      object.setTitle(title_CharSequence);
    }
  }

  @Override
  public void handlePostBind(final OnVisibilityStateChangedView object, int position) {
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
  public OnVisibilityStateChangedViewModel_ onBind(
      OnModelBoundListener<OnVisibilityStateChangedViewModel_, OnVisibilityStateChangedView> listener) {
    onMutation();
    this.onModelBoundListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public void unbind(OnVisibilityStateChangedView object) {
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
  public OnVisibilityStateChangedViewModel_ onUnbind(
      OnModelUnboundListener<OnVisibilityStateChangedViewModel_, OnVisibilityStateChangedView> listener) {
    onMutation();
    this.onModelUnboundListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public void onVisibilityStateChanged(int visibilityState,
      final OnVisibilityStateChangedView object) {
    if (onModelVisibilityStateChangedListener_epoxyGeneratedModel != null) {
      onModelVisibilityStateChangedListener_epoxyGeneratedModel.onVisibilityStateChanged(this, object, visibilityState);
    }
    object.onVisibilityStateChanged1(visibilityState);
    object.onVisibilityStateChanged2(visibilityState);
    super.onVisibilityStateChanged(visibilityState, object);
  }

  /**
   * Register a listener that will be called when this model visibility state has changed.
   * <p>
   * The listener will contribute to this model's hashCode state per the {@link
   * com.airbnb.epoxy.EpoxyAttribute.Option#DoNotHash} rules.
   */
  public OnVisibilityStateChangedViewModel_ onVisibilityStateChanged(
      OnModelVisibilityStateChangedListener<OnVisibilityStateChangedViewModel_, OnVisibilityStateChangedView> listener) {
    onMutation();
    this.onModelVisibilityStateChangedListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public void onVisibilityChanged(float percentVisibleHeight, float percentVisibleWidth,
      int visibleHeight, int visibleWidth, final OnVisibilityStateChangedView object) {
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
  public OnVisibilityStateChangedViewModel_ onVisibilityChanged(
      OnModelVisibilityChangedListener<OnVisibilityStateChangedViewModel_, OnVisibilityStateChangedView> listener) {
    onMutation();
    this.onModelVisibilityChangedListener_epoxyGeneratedModel = listener;
    return this;
  }

  /**
   * <i>Required.</i>
   *
   * @see OnVisibilityStateChangedView#setTitle(CharSequence)
   */
  public OnVisibilityStateChangedViewModel_ title(@NonNull CharSequence title) {
    if (title == null) {
      throw new IllegalArgumentException("title cannot be null");
    }
    assignedAttributes_epoxyGeneratedModel.set(0);
    onMutation();
    this.title_CharSequence = title;
    return this;
  }

  @NonNull
  public CharSequence title() {
    return title_CharSequence;
  }

  @Override
  public OnVisibilityStateChangedViewModel_ id(long p0) {
    super.id(p0);
    return this;
  }

  @Override
  public OnVisibilityStateChangedViewModel_ id(@Nullable Number... p0) {
    super.id(p0);
    return this;
  }

  @Override
  public OnVisibilityStateChangedViewModel_ id(long p0, long p1) {
    super.id(p0, p1);
    return this;
  }

  @Override
  public OnVisibilityStateChangedViewModel_ id(@Nullable CharSequence p0) {
    super.id(p0);
    return this;
  }

  @Override
  public OnVisibilityStateChangedViewModel_ id(@Nullable CharSequence p0,
      @Nullable CharSequence... p1) {
    super.id(p0, p1);
    return this;
  }

  @Override
  public OnVisibilityStateChangedViewModel_ id(@Nullable CharSequence p0, long p1) {
    super.id(p0, p1);
    return this;
  }

  @Override
  public OnVisibilityStateChangedViewModel_ layout(@LayoutRes int p0) {
    super.layout(p0);
    return this;
  }

  @Override
  public OnVisibilityStateChangedViewModel_ spanSizeOverride(
      @Nullable EpoxyModel.SpanSizeOverrideCallback p0) {
    super.spanSizeOverride(p0);
    return this;
  }

  @Override
  public OnVisibilityStateChangedViewModel_ show() {
    super.show();
    return this;
  }

  @Override
  public OnVisibilityStateChangedViewModel_ show(boolean p0) {
    super.show(p0);
    return this;
  }

  @Override
  public OnVisibilityStateChangedViewModel_ hide() {
    super.hide();
    return this;
  }

  @Override
  @LayoutRes
  protected int getDefaultLayout() {
    return 1;
  }

  @Override
  public OnVisibilityStateChangedViewModel_ reset() {
    onModelBoundListener_epoxyGeneratedModel = null;
    onModelUnboundListener_epoxyGeneratedModel = null;
    onModelVisibilityStateChangedListener_epoxyGeneratedModel = null;
    onModelVisibilityChangedListener_epoxyGeneratedModel = null;
    assignedAttributes_epoxyGeneratedModel.clear();
    this.title_CharSequence = null;
    super.reset();
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof OnVisibilityStateChangedViewModel_)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    OnVisibilityStateChangedViewModel_ that = (OnVisibilityStateChangedViewModel_) o;
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
    if ((title_CharSequence != null ? !title_CharSequence.equals(that.title_CharSequence) : that.title_CharSequence != null)) {
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
    _result = 31 * _result + (title_CharSequence != null ? title_CharSequence.hashCode() : 0);
    return _result;
  }

  @Override
  public String toString() {
    return "OnVisibilityStateChangedViewModel_{" +
        "title_CharSequence=" + title_CharSequence +
        "}" + super.toString();
  }

  @Override
  public int getSpanSize(int totalSpanCount, int position, int itemCount) {
    return totalSpanCount;
  }
}
