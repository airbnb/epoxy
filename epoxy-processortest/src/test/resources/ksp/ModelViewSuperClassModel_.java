package com.airbnb.epoxy;

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
public class ModelViewSuperClassModel_ extends EpoxyModel<ModelViewSuperClass> implements GeneratedModel<ModelViewSuperClass>, ModelViewSuperClassModelBuilder {
  private OnModelBoundListener<ModelViewSuperClassModel_, ModelViewSuperClass> onModelBoundListener_epoxyGeneratedModel;

  private OnModelUnboundListener<ModelViewSuperClassModel_, ModelViewSuperClass> onModelUnboundListener_epoxyGeneratedModel;

  private OnModelVisibilityStateChangedListener<ModelViewSuperClassModel_, ModelViewSuperClass> onModelVisibilityStateChangedListener_epoxyGeneratedModel;

  private OnModelVisibilityChangedListener<ModelViewSuperClassModel_, ModelViewSuperClass> onModelVisibilityChangedListener_epoxyGeneratedModel;

  private int superClassValue_Int = 0;

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
  public ModelViewSuperClass buildView(ViewGroup parent) {
    ModelViewSuperClass v = new ModelViewSuperClass(parent.getContext());
    v.setLayoutParams(new ViewGroup.MarginLayoutParams(ViewGroup.MarginLayoutParams.MATCH_PARENT, ViewGroup.MarginLayoutParams.MATCH_PARENT));
    return v;
  }

  @Override
  public void handlePreBind(final EpoxyViewHolder holder, final ModelViewSuperClass object,
      final int position) {
    validateStateHasNotChangedSinceAdded("The model was changed between being added to the controller and being bound.", position);
  }

  @Override
  public void bind(final ModelViewSuperClass object) {
    super.bind(object);
    object.superClassValue(superClassValue_Int);
  }

  @Override
  public void bind(final ModelViewSuperClass object, EpoxyModel previousModel) {
    if (!(previousModel instanceof ModelViewSuperClassModel_)) {
      bind(object);
      return;
    }
    ModelViewSuperClassModel_ that = (ModelViewSuperClassModel_) previousModel;
    super.bind(object);

    if ((superClassValue_Int != that.superClassValue_Int)) {
      object.superClassValue(superClassValue_Int);
    }
  }

  @Override
  public void handlePostBind(final ModelViewSuperClass object, int position) {
    if (onModelBoundListener_epoxyGeneratedModel != null) {
      onModelBoundListener_epoxyGeneratedModel.onModelBound(this, object, position);
    }
    validateStateHasNotChangedSinceAdded("The model was changed during the bind call.", position);
    object.afterProps();
  }

  /**
   * Register a listener that will be called when this model is bound to a view.
   * <p>
   * The listener will contribute to this model's hashCode state per the {@link
   * com.airbnb.epoxy.EpoxyAttribute.Option#DoNotHash} rules.
   * <p>
   * You may clear the listener by setting a null value, or by calling {@link #reset()}
   */
  public ModelViewSuperClassModel_ onBind(
      OnModelBoundListener<ModelViewSuperClassModel_, ModelViewSuperClass> listener) {
    onMutation();
    this.onModelBoundListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public void unbind(ModelViewSuperClass object) {
    super.unbind(object);
    if (onModelUnboundListener_epoxyGeneratedModel != null) {
      onModelUnboundListener_epoxyGeneratedModel.onModelUnbound(this, object);
    }
    object.onClear();
  }

  /**
   * Register a listener that will be called when this model is unbound from a view.
   * <p>
   * The listener will contribute to this model's hashCode state per the {@link
   * com.airbnb.epoxy.EpoxyAttribute.Option#DoNotHash} rules.
   * <p>
   * You may clear the listener by setting a null value, or by calling {@link #reset()}
   */
  public ModelViewSuperClassModel_ onUnbind(
      OnModelUnboundListener<ModelViewSuperClassModel_, ModelViewSuperClass> listener) {
    onMutation();
    this.onModelUnboundListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public void onVisibilityStateChanged(int visibilityState, final ModelViewSuperClass object) {
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
  public ModelViewSuperClassModel_ onVisibilityStateChanged(
      OnModelVisibilityStateChangedListener<ModelViewSuperClassModel_, ModelViewSuperClass> listener) {
    onMutation();
    this.onModelVisibilityStateChangedListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public void onVisibilityChanged(float percentVisibleHeight, float percentVisibleWidth,
      int visibleHeight, int visibleWidth, final ModelViewSuperClass object) {
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
  public ModelViewSuperClassModel_ onVisibilityChanged(
      OnModelVisibilityChangedListener<ModelViewSuperClassModel_, ModelViewSuperClass> listener) {
    onMutation();
    this.onModelVisibilityChangedListener_epoxyGeneratedModel = listener;
    return this;
  }

  /**
   * <i>Optional</i>: Default value is 0
   *
   * @see ModelViewSuperClass#superClassValue(int)
   */
  public ModelViewSuperClassModel_ superClassValue(int superClassValue) {
    onMutation();
    this.superClassValue_Int = superClassValue;
    return this;
  }

  public int superClassValue() {
    return superClassValue_Int;
  }

  @Override
  public ModelViewSuperClassModel_ id(long p0) {
    super.id(p0);
    return this;
  }

  @Override
  public ModelViewSuperClassModel_ id(@Nullable Number... p0) {
    super.id(p0);
    return this;
  }

  @Override
  public ModelViewSuperClassModel_ id(long p0, long p1) {
    super.id(p0, p1);
    return this;
  }

  @Override
  public ModelViewSuperClassModel_ id(@Nullable CharSequence p0) {
    super.id(p0);
    return this;
  }

  @Override
  public ModelViewSuperClassModel_ id(@Nullable CharSequence p0, @Nullable CharSequence... p1) {
    super.id(p0, p1);
    return this;
  }

  @Override
  public ModelViewSuperClassModel_ id(@Nullable CharSequence p0, long p1) {
    super.id(p0, p1);
    return this;
  }

  @Override
  public ModelViewSuperClassModel_ layout(@LayoutRes int p0) {
    throw new UnsupportedOperationException("Layout resources are unsupported with programmatic views.");
  }

  @Override
  public ModelViewSuperClassModel_ spanSizeOverride(
      @Nullable EpoxyModel.SpanSizeOverrideCallback p0) {
    super.spanSizeOverride(p0);
    return this;
  }

  @Override
  public ModelViewSuperClassModel_ show() {
    super.show();
    return this;
  }

  @Override
  public ModelViewSuperClassModel_ show(boolean p0) {
    super.show(p0);
    return this;
  }

  @Override
  public ModelViewSuperClassModel_ hide() {
    super.hide();
    return this;
  }

  @Override
  @LayoutRes
  protected int getDefaultLayout() {
    throw new UnsupportedOperationException("Layout resources are unsupported for views created programmatically.");
  }

  @Override
  public ModelViewSuperClassModel_ reset() {
    onModelBoundListener_epoxyGeneratedModel = null;
    onModelUnboundListener_epoxyGeneratedModel = null;
    onModelVisibilityStateChangedListener_epoxyGeneratedModel = null;
    onModelVisibilityChangedListener_epoxyGeneratedModel = null;
    this.superClassValue_Int = 0;
    super.reset();
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof ModelViewSuperClassModel_)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    ModelViewSuperClassModel_ that = (ModelViewSuperClassModel_) o;
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
    if ((superClassValue_Int != that.superClassValue_Int)) {
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
    _result = 31 * _result + superClassValue_Int;
    return _result;
  }

  @Override
  public String toString() {
    return "ModelViewSuperClassModel_{" +
        "superClassValue_Int=" + superClassValue_Int +
        "}" + super.toString();
  }

  @Override
  public int getSpanSize(int totalSpanCount, int position, int itemCount) {
    return totalSpanCount;
  }
}
