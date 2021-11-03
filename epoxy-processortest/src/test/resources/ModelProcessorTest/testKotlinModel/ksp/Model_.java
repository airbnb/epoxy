package com.airbnb.epoxy;

import android.view.View;
import android.view.ViewParent;
import androidx.annotation.DrawableRes;
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
public class Model_ extends Model implements GeneratedModel<Model.Holder>, ModelBuilder {
  private OnModelBoundListener<Model_, Model.Holder> onModelBoundListener_epoxyGeneratedModel;

  private OnModelUnboundListener<Model_, Model.Holder> onModelUnboundListener_epoxyGeneratedModel;

  private OnModelVisibilityStateChangedListener<Model_, Model.Holder> onModelVisibilityStateChangedListener_epoxyGeneratedModel;

  private OnModelVisibilityChangedListener<Model_, Model.Holder> onModelVisibilityChangedListener_epoxyGeneratedModel;

  public Model_() {
    super();
  }

  @Override
  public void addTo(EpoxyController controller) {
    super.addTo(controller);
    addWithDebugValidation(controller);
  }

  @Override
  public void handlePreBind(final EpoxyViewHolder holder, final Model.Holder object,
      final int position) {
    validateStateHasNotChangedSinceAdded("The model was changed between being added to the controller and being bound.", position);
  }

  @Override
  public void handlePostBind(final Model.Holder object, int position) {
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
  public Model_ onBind(OnModelBoundListener<Model_, Model.Holder> listener) {
    onMutation();
    this.onModelBoundListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public void unbind(Model.Holder object) {
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
  public Model_ onUnbind(OnModelUnboundListener<Model_, Model.Holder> listener) {
    onMutation();
    this.onModelUnboundListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public void onVisibilityStateChanged(int visibilityState, final Model.Holder object) {
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
  public Model_ onVisibilityStateChanged(
      OnModelVisibilityStateChangedListener<Model_, Model.Holder> listener) {
    onMutation();
    this.onModelVisibilityStateChangedListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public void onVisibilityChanged(float percentVisibleHeight, float percentVisibleWidth,
      int visibleHeight, int visibleWidth, final Model.Holder object) {
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
  public Model_ onVisibilityChanged(
      OnModelVisibilityChangedListener<Model_, Model.Holder> listener) {
    onMutation();
    this.onModelVisibilityChangedListener_epoxyGeneratedModel = listener;
    return this;
  }

  public Model_ imageRes(@DrawableRes int imageRes) {
    onMutation();
    super.setImageRes(imageRes);
    return this;
  }

  @DrawableRes
  public int imageRes() {
    return super.getImageRes();
  }

  /**
   * Set a click listener that will provide the parent view, model, and adapter position of the clicked view. This will clear the normal View.OnClickListener if one has been set
   */
  public Model_ clickListener(
      @Nullable final OnModelClickListener<Model_, Model.Holder> clickListener) {
    onMutation();
    if (clickListener == null) {
      super.setClickListener(null);
    }
    else {
      super.setClickListener(new WrappedEpoxyModelClickListener(clickListener));
    }
    return this;
  }

  public Model_ clickListener(@Nullable View.OnClickListener clickListener) {
    onMutation();
    super.setClickListener(clickListener);
    return this;
  }

  @Nullable
  public View.OnClickListener clickListener() {
    return super.getClickListener();
  }

  @Override
  public Model_ id(long p0) {
    super.id(p0);
    return this;
  }

  @Override
  public Model_ id(@Nullable Number... p0) {
    super.id(p0);
    return this;
  }

  @Override
  public Model_ id(long p0, long p1) {
    super.id(p0, p1);
    return this;
  }

  @Override
  public Model_ id(@Nullable CharSequence p0) {
    super.id(p0);
    return this;
  }

  @Override
  public Model_ id(@Nullable CharSequence p0, @Nullable CharSequence... p1) {
    super.id(p0, p1);
    return this;
  }

  @Override
  public Model_ id(@Nullable CharSequence p0, long p1) {
    super.id(p0, p1);
    return this;
  }

  @Override
  public Model_ layout(@LayoutRes int p0) {
    super.layout(p0);
    return this;
  }

  @Override
  public Model_ spanSizeOverride(@Nullable EpoxyModel.SpanSizeOverrideCallback p0) {
    super.spanSizeOverride(p0);
    return this;
  }

  @Override
  public Model_ show() {
    super.show();
    return this;
  }

  @Override
  public Model_ show(boolean p0) {
    super.show(p0);
    return this;
  }

  @Override
  public Model_ hide() {
    super.hide();
    return this;
  }

  @Override
  protected Model.Holder createNewHolder(ViewParent parent) {
    return new Model.Holder();
  }

  @Override
  public Model_ reset() {
    onModelBoundListener_epoxyGeneratedModel = null;
    onModelUnboundListener_epoxyGeneratedModel = null;
    onModelVisibilityStateChangedListener_epoxyGeneratedModel = null;
    onModelVisibilityChangedListener_epoxyGeneratedModel = null;
    super.setImageRes(0);
    super.setClickListener(null);
    super.reset();
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof Model_)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    Model_ that = (Model_) o;
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
    if ((getImageRes() != that.getImageRes())) {
      return false;
    }
    if (((getClickListener() == null) != (that.getClickListener() == null))) {
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
    _result = 31 * _result + getImageRes();
    _result = 31 * _result + (getClickListener() != null ? 1 : 0);
    return _result;
  }

  @Override
  public String toString() {
    return "Model_{" +
        "imageRes=" + getImageRes() +
        ", clickListener=" + getClickListener() +
        "}" + super.toString();
  }
}
