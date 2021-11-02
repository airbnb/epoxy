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
public class CallbackPropModelViewModel_ extends EpoxyModel<CallbackPropModelView> implements GeneratedModel<CallbackPropModelView>, CallbackPropModelViewModelBuilder {
  private OnModelBoundListener<CallbackPropModelViewModel_, CallbackPropModelView> onModelBoundListener_epoxyGeneratedModel;

  private OnModelUnboundListener<CallbackPropModelViewModel_, CallbackPropModelView> onModelUnboundListener_epoxyGeneratedModel;

  private OnModelVisibilityStateChangedListener<CallbackPropModelViewModel_, CallbackPropModelView> onModelVisibilityStateChangedListener_epoxyGeneratedModel;

  private OnModelVisibilityChangedListener<CallbackPropModelViewModel_, CallbackPropModelView> onModelVisibilityChangedListener_epoxyGeneratedModel;

  @Nullable
  private View.OnClickListener onClickListener_OnClickListener = (View.OnClickListener) null;

  @Override
  public void addTo(EpoxyController controller) {
    super.addTo(controller);
    addWithDebugValidation(controller);
  }

  @Override
  public void handlePreBind(final EpoxyViewHolder holder, final CallbackPropModelView object,
      final int position) {
    validateStateHasNotChangedSinceAdded("The model was changed between being added to the controller and being bound.", position);
  }

  @Override
  public void bind(final CallbackPropModelView object) {
    super.bind(object);
    object.setOnClickListener(onClickListener_OnClickListener);
  }

  @Override
  public void bind(final CallbackPropModelView object, EpoxyModel previousModel) {
    if (!(previousModel instanceof CallbackPropModelViewModel_)) {
      bind(object);
      return;
    }
    CallbackPropModelViewModel_ that = (CallbackPropModelViewModel_) previousModel;
    super.bind(object);

    if (((onClickListener_OnClickListener == null) != (that.onClickListener_OnClickListener == null))) {
      object.setOnClickListener(onClickListener_OnClickListener);
    }
  }

  @Override
  public void handlePostBind(final CallbackPropModelView object, int position) {
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
  public CallbackPropModelViewModel_ onBind(
      OnModelBoundListener<CallbackPropModelViewModel_, CallbackPropModelView> listener) {
    onMutation();
    this.onModelBoundListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public void unbind(CallbackPropModelView object) {
    super.unbind(object);
    if (onModelUnboundListener_epoxyGeneratedModel != null) {
      onModelUnboundListener_epoxyGeneratedModel.onModelUnbound(this, object);
    }
    object.setOnClickListener((View.OnClickListener) null);
  }

  /**
   * Register a listener that will be called when this model is unbound from a view.
   * <p>
   * The listener will contribute to this model's hashCode state per the {@link
   * com.airbnb.epoxy.EpoxyAttribute.Option#DoNotHash} rules.
   * <p>
   * You may clear the listener by setting a null value, or by calling {@link #reset()}
   */
  public CallbackPropModelViewModel_ onUnbind(
      OnModelUnboundListener<CallbackPropModelViewModel_, CallbackPropModelView> listener) {
    onMutation();
    this.onModelUnboundListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public void onVisibilityStateChanged(int visibilityState, final CallbackPropModelView object) {
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
  public CallbackPropModelViewModel_ onVisibilityStateChanged(
      OnModelVisibilityStateChangedListener<CallbackPropModelViewModel_, CallbackPropModelView> listener) {
    onMutation();
    this.onModelVisibilityStateChangedListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public void onVisibilityChanged(float percentVisibleHeight, float percentVisibleWidth,
      int visibleHeight, int visibleWidth, final CallbackPropModelView object) {
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
  public CallbackPropModelViewModel_ onVisibilityChanged(
      OnModelVisibilityChangedListener<CallbackPropModelViewModel_, CallbackPropModelView> listener) {
    onMutation();
    this.onModelVisibilityChangedListener_epoxyGeneratedModel = listener;
    return this;
  }

  /**
   * Set a click listener that will provide the parent view, model, and adapter position of the clicked view. This will clear the normal View.OnClickListener if one has been set
   */
  public CallbackPropModelViewModel_ onClickListener(
      @Nullable final OnModelClickListener<CallbackPropModelViewModel_, CallbackPropModelView> onClickListener) {
    onMutation();
    if (onClickListener == null) {
      this.onClickListener_OnClickListener = null;
    }
    else {
      this.onClickListener_OnClickListener = new WrappedEpoxyModelClickListener(onClickListener);
    }
    return this;
  }

  /**
   * <i>Optional</i>: Default value is (View.OnClickListener) null
   *
   * @see CallbackPropModelView#setOnClickListener(View.OnClickListener)
   */
  public CallbackPropModelViewModel_ onClickListener(
      @Nullable View.OnClickListener onClickListener) {
    onMutation();
    this.onClickListener_OnClickListener = onClickListener;
    return this;
  }

  @Nullable
  public View.OnClickListener onClickListener() {
    return onClickListener_OnClickListener;
  }

  @Override
  public CallbackPropModelViewModel_ id(long id) {
    super.id(id);
    return this;
  }

  @Override
  public CallbackPropModelViewModel_ id(@Nullable Number... ids) {
    super.id(ids);
    return this;
  }

  @Override
  public CallbackPropModelViewModel_ id(long id1, long id2) {
    super.id(id1, id2);
    return this;
  }

  @Override
  public CallbackPropModelViewModel_ id(@Nullable CharSequence key) {
    super.id(key);
    return this;
  }

  @Override
  public CallbackPropModelViewModel_ id(@Nullable CharSequence key,
      @Nullable CharSequence... otherKeys) {
    super.id(key, otherKeys);
    return this;
  }

  @Override
  public CallbackPropModelViewModel_ id(@Nullable CharSequence key, long id) {
    super.id(key, id);
    return this;
  }

  @Override
  public CallbackPropModelViewModel_ layout(@LayoutRes int layoutRes) {
    super.layout(layoutRes);
    return this;
  }

  @Override
  public CallbackPropModelViewModel_ spanSizeOverride(
      @Nullable EpoxyModel.SpanSizeOverrideCallback spanSizeCallback) {
    super.spanSizeOverride(spanSizeCallback);
    return this;
  }

  @Override
  public CallbackPropModelViewModel_ show() {
    super.show();
    return this;
  }

  @Override
  public CallbackPropModelViewModel_ show(boolean show) {
    super.show(show);
    return this;
  }

  @Override
  public CallbackPropModelViewModel_ hide() {
    super.hide();
    return this;
  }

  @Override
  @LayoutRes
  protected int getDefaultLayout() {
    return 1;
  }

  @Override
  public CallbackPropModelViewModel_ reset() {
    onModelBoundListener_epoxyGeneratedModel = null;
    onModelUnboundListener_epoxyGeneratedModel = null;
    onModelVisibilityStateChangedListener_epoxyGeneratedModel = null;
    onModelVisibilityChangedListener_epoxyGeneratedModel = null;
    this.onClickListener_OnClickListener = (View.OnClickListener) null;
    super.reset();
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof CallbackPropModelViewModel_)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    CallbackPropModelViewModel_ that = (CallbackPropModelViewModel_) o;
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
    if (((onClickListener_OnClickListener == null) != (that.onClickListener_OnClickListener == null))) {
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
    _result = 31 * _result + (onClickListener_OnClickListener != null ? 1 : 0);
    return _result;
  }

  @Override
  public String toString() {
    return "CallbackPropModelViewModel_{" +
        "onClickListener_OnClickListener=" + onClickListener_OnClickListener +
        "}" + super.toString();
  }

  public static CallbackPropModelViewModel_ from(ModelProperties properties) {
    CallbackPropModelViewModel_ model = new CallbackPropModelViewModel_();
    model.id(properties.getId());
    if (properties.has("onClickListener")) {
      model.onClickListener(properties.getOnClickListener("onClickListener"));
    }
    return model;
  }

  @Override
  public int getSpanSize(int totalSpanCount, int position, int itemCount) {
    return totalSpanCount;
  }
}
