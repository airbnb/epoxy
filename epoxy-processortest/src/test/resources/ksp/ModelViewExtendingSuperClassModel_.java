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
public class ModelViewExtendingSuperClassModel_ extends EpoxyModel<ModelViewExtendingSuperClass> implements GeneratedModel<ModelViewExtendingSuperClass>, ModelViewExtendingSuperClassModelBuilder {
  private OnModelBoundListener<ModelViewExtendingSuperClassModel_, ModelViewExtendingSuperClass> onModelBoundListener_epoxyGeneratedModel;

  private OnModelUnboundListener<ModelViewExtendingSuperClassModel_, ModelViewExtendingSuperClass> onModelUnboundListener_epoxyGeneratedModel;

  private OnModelVisibilityStateChangedListener<ModelViewExtendingSuperClassModel_, ModelViewExtendingSuperClass> onModelVisibilityStateChangedListener_epoxyGeneratedModel;

  private OnModelVisibilityChangedListener<ModelViewExtendingSuperClassModel_, ModelViewExtendingSuperClass> onModelVisibilityChangedListener_epoxyGeneratedModel;

  private int subClassValue_Int = 0;

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
  public ModelViewExtendingSuperClass buildView(ViewGroup parent) {
    ModelViewExtendingSuperClass v = new ModelViewExtendingSuperClass(parent.getContext());
    v.setLayoutParams(new ViewGroup.MarginLayoutParams(ViewGroup.MarginLayoutParams.MATCH_PARENT, ViewGroup.MarginLayoutParams.MATCH_PARENT));
    return v;
  }

  @Override
  public void handlePreBind(final EpoxyViewHolder holder, final ModelViewExtendingSuperClass object,
      final int position) {
    validateStateHasNotChangedSinceAdded("The model was changed between being added to the controller and being bound.", position);
  }

  @Override
  public void bind(final ModelViewExtendingSuperClass object) {
    super.bind(object);
    object.subClassValue(subClassValue_Int);
    object.superClassValue(superClassValue_Int);
  }

  @Override
  public void bind(final ModelViewExtendingSuperClass object, EpoxyModel previousModel) {
    if (!(previousModel instanceof ModelViewExtendingSuperClassModel_)) {
      bind(object);
      return;
    }
    ModelViewExtendingSuperClassModel_ that = (ModelViewExtendingSuperClassModel_) previousModel;
    super.bind(object);

    if ((subClassValue_Int != that.subClassValue_Int)) {
      object.subClassValue(subClassValue_Int);
    }

    if ((superClassValue_Int != that.superClassValue_Int)) {
      object.superClassValue(superClassValue_Int);
    }
  }

  @Override
  public void handlePostBind(final ModelViewExtendingSuperClass object, int position) {
    if (onModelBoundListener_epoxyGeneratedModel != null) {
      onModelBoundListener_epoxyGeneratedModel.onModelBound(this, object, position);
    }
    validateStateHasNotChangedSinceAdded("The model was changed during the bind call.", position);
    object.afterProps();
    object.onSubclassAfterPropsSet();
  }

  /**
   * Register a listener that will be called when this model is bound to a view.
   * <p>
   * The listener will contribute to this model's hashCode state per the {@link
   * com.airbnb.epoxy.EpoxyAttribute.Option#DoNotHash} rules.
   * <p>
   * You may clear the listener by setting a null value, or by calling {@link #reset()}
   */
  public ModelViewExtendingSuperClassModel_ onBind(
      OnModelBoundListener<ModelViewExtendingSuperClassModel_, ModelViewExtendingSuperClass> listener) {
    onMutation();
    this.onModelBoundListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public void unbind(ModelViewExtendingSuperClass object) {
    super.unbind(object);
    if (onModelUnboundListener_epoxyGeneratedModel != null) {
      onModelUnboundListener_epoxyGeneratedModel.onModelUnbound(this, object);
    }
    object.onClear();
    object.onSubClassCleared();
  }

  /**
   * Register a listener that will be called when this model is unbound from a view.
   * <p>
   * The listener will contribute to this model's hashCode state per the {@link
   * com.airbnb.epoxy.EpoxyAttribute.Option#DoNotHash} rules.
   * <p>
   * You may clear the listener by setting a null value, or by calling {@link #reset()}
   */
  public ModelViewExtendingSuperClassModel_ onUnbind(
      OnModelUnboundListener<ModelViewExtendingSuperClassModel_, ModelViewExtendingSuperClass> listener) {
    onMutation();
    this.onModelUnboundListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public void onVisibilityStateChanged(int visibilityState,
      final ModelViewExtendingSuperClass object) {
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
  public ModelViewExtendingSuperClassModel_ onVisibilityStateChanged(
      OnModelVisibilityStateChangedListener<ModelViewExtendingSuperClassModel_, ModelViewExtendingSuperClass> listener) {
    onMutation();
    this.onModelVisibilityStateChangedListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public void onVisibilityChanged(float percentVisibleHeight, float percentVisibleWidth,
      int visibleHeight, int visibleWidth, final ModelViewExtendingSuperClass object) {
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
  public ModelViewExtendingSuperClassModel_ onVisibilityChanged(
      OnModelVisibilityChangedListener<ModelViewExtendingSuperClassModel_, ModelViewExtendingSuperClass> listener) {
    onMutation();
    this.onModelVisibilityChangedListener_epoxyGeneratedModel = listener;
    return this;
  }

  /**
   * <i>Optional</i>: Default value is 0
   *
   * @see ModelViewExtendingSuperClass#subClassValue(int)
   */
  public ModelViewExtendingSuperClassModel_ subClassValue(int subClassValue) {
    onMutation();
    this.subClassValue_Int = subClassValue;
    return this;
  }

  public int subClassValue() {
    return subClassValue_Int;
  }

  /**
   * <i>Optional</i>: Default value is 0
   *
   * @see ModelViewExtendingSuperClass#superClassValue(int)
   */
  public ModelViewExtendingSuperClassModel_ superClassValue(int superClassValue) {
    onMutation();
    this.superClassValue_Int = superClassValue;
    return this;
  }

  public int superClassValue() {
    return superClassValue_Int;
  }

  @Override
  public ModelViewExtendingSuperClassModel_ id(long p0) {
    super.id(p0);
    return this;
  }

  @Override
  public ModelViewExtendingSuperClassModel_ id(@Nullable Number... p0) {
    super.id(p0);
    return this;
  }

  @Override
  public ModelViewExtendingSuperClassModel_ id(long p0, long p1) {
    super.id(p0, p1);
    return this;
  }

  @Override
  public ModelViewExtendingSuperClassModel_ id(@Nullable CharSequence p0) {
    super.id(p0);
    return this;
  }

  @Override
  public ModelViewExtendingSuperClassModel_ id(@Nullable CharSequence p0,
      @Nullable CharSequence... p1) {
    super.id(p0, p1);
    return this;
  }

  @Override
  public ModelViewExtendingSuperClassModel_ id(@Nullable CharSequence p0, long p1) {
    super.id(p0, p1);
    return this;
  }

  @Override
  public ModelViewExtendingSuperClassModel_ layout(@LayoutRes int p0) {
    throw new UnsupportedOperationException("Layout resources are unsupported with programmatic views.");
  }

  @Override
  public ModelViewExtendingSuperClassModel_ spanSizeOverride(
      @Nullable EpoxyModel.SpanSizeOverrideCallback p0) {
    super.spanSizeOverride(p0);
    return this;
  }

  @Override
  public ModelViewExtendingSuperClassModel_ show() {
    super.show();
    return this;
  }

  @Override
  public ModelViewExtendingSuperClassModel_ show(boolean p0) {
    super.show(p0);
    return this;
  }

  @Override
  public ModelViewExtendingSuperClassModel_ hide() {
    super.hide();
    return this;
  }

  @Override
  @LayoutRes
  protected int getDefaultLayout() {
    throw new UnsupportedOperationException("Layout resources are unsupported for views created programmatically.");
  }

  @Override
  public ModelViewExtendingSuperClassModel_ reset() {
    onModelBoundListener_epoxyGeneratedModel = null;
    onModelUnboundListener_epoxyGeneratedModel = null;
    onModelVisibilityStateChangedListener_epoxyGeneratedModel = null;
    onModelVisibilityChangedListener_epoxyGeneratedModel = null;
    this.subClassValue_Int = 0;
    this.superClassValue_Int = 0;
    super.reset();
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof ModelViewExtendingSuperClassModel_)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    ModelViewExtendingSuperClassModel_ that = (ModelViewExtendingSuperClassModel_) o;
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
    if ((subClassValue_Int != that.subClassValue_Int)) {
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
    _result = 31 * _result + subClassValue_Int;
    _result = 31 * _result + superClassValue_Int;
    return _result;
  }

  @Override
  public String toString() {
    return "ModelViewExtendingSuperClassModel_{" +
        "subClassValue_Int=" + subClassValue_Int +
        ", superClassValue_Int=" + superClassValue_Int +
        "}" + super.toString();
  }

  @Override
  public int getSpanSize(int totalSpanCount, int position, int itemCount) {
    return totalSpanCount;
  }
}
