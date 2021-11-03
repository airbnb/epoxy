package com.airbnb.epoxy;

import android.view.ViewParent;
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
public class AbstractModelWithHolder_ extends AbstractModelWithHolder implements GeneratedModel<Holder>, AbstractModelWithHolderBuilder {
  private OnModelBoundListener<AbstractModelWithHolder_, Holder> onModelBoundListener_epoxyGeneratedModel;

  private OnModelUnboundListener<AbstractModelWithHolder_, Holder> onModelUnboundListener_epoxyGeneratedModel;

  private OnModelVisibilityStateChangedListener<AbstractModelWithHolder_, Holder> onModelVisibilityStateChangedListener_epoxyGeneratedModel;

  private OnModelVisibilityChangedListener<AbstractModelWithHolder_, Holder> onModelVisibilityChangedListener_epoxyGeneratedModel;

  public AbstractModelWithHolder_() {
    super();
  }

  @Override
  public void addTo(EpoxyController controller) {
    super.addTo(controller);
    addWithDebugValidation(controller);
  }

  @Override
  public void handlePreBind(final EpoxyViewHolder holder, final Holder object, final int position) {
    validateStateHasNotChangedSinceAdded("The model was changed between being added to the controller and being bound.", position);
  }

  @Override
  public void handlePostBind(final Holder object, int position) {
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
  public AbstractModelWithHolder_ onBind(
      OnModelBoundListener<AbstractModelWithHolder_, Holder> listener) {
    onMutation();
    this.onModelBoundListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public void unbind(Holder object) {
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
  public AbstractModelWithHolder_ onUnbind(
      OnModelUnboundListener<AbstractModelWithHolder_, Holder> listener) {
    onMutation();
    this.onModelUnboundListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public void onVisibilityStateChanged(int visibilityState, final Holder object) {
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
  public AbstractModelWithHolder_ onVisibilityStateChanged(
      OnModelVisibilityStateChangedListener<AbstractModelWithHolder_, Holder> listener) {
    onMutation();
    this.onModelVisibilityStateChangedListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public void onVisibilityChanged(float percentVisibleHeight, float percentVisibleWidth,
      int visibleHeight, int visibleWidth, final Holder object) {
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
  public AbstractModelWithHolder_ onVisibilityChanged(
      OnModelVisibilityChangedListener<AbstractModelWithHolder_, Holder> listener) {
    onMutation();
    this.onModelVisibilityChangedListener_epoxyGeneratedModel = listener;
    return this;
  }

  public AbstractModelWithHolder_ value(int value) {
    onMutation();
    super.setValue(value);
    return this;
  }

  public int value() {
    return super.getValue();
  }

  @Override
  public AbstractModelWithHolder_ id(long p0) {
    super.id(p0);
    return this;
  }

  @Override
  public AbstractModelWithHolder_ id(@Nullable Number... p0) {
    super.id(p0);
    return this;
  }

  @Override
  public AbstractModelWithHolder_ id(long p0, long p1) {
    super.id(p0, p1);
    return this;
  }

  @Override
  public AbstractModelWithHolder_ id(@Nullable CharSequence p0) {
    super.id(p0);
    return this;
  }

  @Override
  public AbstractModelWithHolder_ id(@Nullable CharSequence p0, @Nullable CharSequence... p1) {
    super.id(p0, p1);
    return this;
  }

  @Override
  public AbstractModelWithHolder_ id(@Nullable CharSequence p0, long p1) {
    super.id(p0, p1);
    return this;
  }

  @Override
  public AbstractModelWithHolder_ layout(@LayoutRes int p0) {
    super.layout(p0);
    return this;
  }

  @Override
  public AbstractModelWithHolder_ spanSizeOverride(
      @Nullable EpoxyModel.SpanSizeOverrideCallback p0) {
    super.spanSizeOverride(p0);
    return this;
  }

  @Override
  public AbstractModelWithHolder_ show() {
    super.show();
    return this;
  }

  @Override
  public AbstractModelWithHolder_ show(boolean p0) {
    super.show(p0);
    return this;
  }

  @Override
  public AbstractModelWithHolder_ hide() {
    super.hide();
    return this;
  }

  @Override
  protected Holder createNewHolder(ViewParent parent) {
    return new Holder();
  }

  @Override
  public AbstractModelWithHolder_ reset() {
    onModelBoundListener_epoxyGeneratedModel = null;
    onModelUnboundListener_epoxyGeneratedModel = null;
    onModelVisibilityStateChangedListener_epoxyGeneratedModel = null;
    onModelVisibilityChangedListener_epoxyGeneratedModel = null;
    super.setValue(0);
    super.reset();
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof AbstractModelWithHolder_)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    AbstractModelWithHolder_ that = (AbstractModelWithHolder_) o;
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
    if ((getValue() != that.getValue())) {
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
    _result = 31 * _result + getValue();
    return _result;
  }

  @Override
  public String toString() {
    return "AbstractModelWithHolder_{" +
        "value=" + getValue() +
        "}" + super.toString();
  }
}
