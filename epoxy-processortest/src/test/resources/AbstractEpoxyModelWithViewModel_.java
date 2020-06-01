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
 * Generated file. Do not modify! */
public class AbstractEpoxyModelWithView_ extends AbstractEpoxyModelWithView implements GeneratedModel<View>, AbstractEpoxyModelWithViewBuilder {
  private OnModelBoundListener<AbstractEpoxyModelWithView_, View> onModelBoundListener_epoxyGeneratedModel;

  private OnModelUnboundListener<AbstractEpoxyModelWithView_, View> onModelUnboundListener_epoxyGeneratedModel;

  private OnModelVisibilityStateChangedListener<AbstractEpoxyModelWithView_, View> onModelVisibilityStateChangedListener_epoxyGeneratedModel;

  private OnModelVisibilityChangedListener<AbstractEpoxyModelWithView_, View> onModelVisibilityChangedListener_epoxyGeneratedModel;

  public AbstractEpoxyModelWithView_() {
    super();
  }

  @Override
  public void addTo(EpoxyController controller) {
    super.addTo(controller);
    addWithDebugValidation(controller);
  }

  @Override
  public void handlePreBind(final EpoxyViewHolder holder, final View object, final int position) {
    validateStateHasNotChangedSinceAdded("The model was changed between being added to the controller and being bound.", position);
  }

  @Override
  public void handlePostBind(final View object, int position) {
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
  public AbstractEpoxyModelWithView_ onBind(
      OnModelBoundListener<AbstractEpoxyModelWithView_, View> listener) {
    onMutation();
    this.onModelBoundListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public void unbind(View object) {
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
  public AbstractEpoxyModelWithView_ onUnbind(
      OnModelUnboundListener<AbstractEpoxyModelWithView_, View> listener) {
    onMutation();
    this.onModelUnboundListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public void onVisibilityStateChanged(int visibilityState, final View object) {
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
  public AbstractEpoxyModelWithView_ onVisibilityStateChanged(
      OnModelVisibilityStateChangedListener<AbstractEpoxyModelWithView_, View> listener) {
    onMutation();
    this.onModelVisibilityStateChangedListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public void onVisibilityChanged(float percentVisibleHeight, float percentVisibleWidth,
      int visibleHeight, int visibleWidth, final View object) {
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
  public AbstractEpoxyModelWithView_ onVisibilityChanged(
      OnModelVisibilityChangedListener<AbstractEpoxyModelWithView_, View> listener) {
    onMutation();
    this.onModelVisibilityChangedListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public AbstractEpoxyModelWithView_ layout(@LayoutRes int arg0) {
    super.layout(arg0);
    return this;
  }

  @Override
  public AbstractEpoxyModelWithView_ id(long id) {
    super.id(id);
    return this;
  }

  @Override
  public AbstractEpoxyModelWithView_ id(@Nullable Number... arg0) {
    super.id(arg0);
    return this;
  }

  @Override
  public AbstractEpoxyModelWithView_ id(long id1, long id2) {
    super.id(id1, id2);
    return this;
  }

  @Override
  public AbstractEpoxyModelWithView_ id(@Nullable CharSequence arg0) {
    super.id(arg0);
    return this;
  }

  @Override
  public AbstractEpoxyModelWithView_ id(@Nullable CharSequence arg0,
      @Nullable CharSequence... arg1) {
    super.id(arg0, arg1);
    return this;
  }

  @Override
  public AbstractEpoxyModelWithView_ id(@Nullable CharSequence arg0, long arg1) {
    super.id(arg0, arg1);
    return this;
  }

  @Override
  public AbstractEpoxyModelWithView_ spanSizeOverride(
      @Nullable EpoxyModel.SpanSizeOverrideCallback arg0) {
    super.spanSizeOverride(arg0);
    return this;
  }

  @Override
  public AbstractEpoxyModelWithView_ show() {
    super.show();
    return this;
  }

  @Override
  public AbstractEpoxyModelWithView_ show(boolean show) {
    super.show(show);
    return this;
  }

  @Override
  public AbstractEpoxyModelWithView_ hide() {
    super.hide();
    return this;
  }

  @Override
  public AbstractEpoxyModelWithView_ reset() {
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
    if (!(o instanceof AbstractEpoxyModelWithView_)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    AbstractEpoxyModelWithView_ that = (AbstractEpoxyModelWithView_) o;
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
    return "AbstractEpoxyModelWithView_{" +
        "}" + super.toString();
  }
}