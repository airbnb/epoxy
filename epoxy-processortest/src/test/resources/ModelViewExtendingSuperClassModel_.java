package com.airbnb.epoxy;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.ViewGroup;
import java.lang.CharSequence;
import java.lang.Number;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.UnsupportedOperationException;
import java.util.BitSet;

/**
 * Generated file. Do not modify! */
public class ModelViewExtendingSuperClassModel_ extends EpoxyModel<ModelViewExtendingSuperClass> implements GeneratedModel<ModelViewExtendingSuperClass>, ModelViewExtendingSuperClassModelBuilder {
  private final BitSet assignedAttributes_epoxyGeneratedModel = new BitSet(2);

  private OnModelBoundListener<ModelViewExtendingSuperClassModel_, ModelViewExtendingSuperClass> onModelBoundListener_epoxyGeneratedModel;

  private OnModelUnboundListener<ModelViewExtendingSuperClassModel_, ModelViewExtendingSuperClass> onModelUnboundListener_epoxyGeneratedModel;

  /**
   * Bitset index: 0 */
  private int subClassValue_Int = 0;

  /**
   * Bitset index: 1 */
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
  protected ModelViewExtendingSuperClass buildView(ViewGroup parent) {
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
   * You may clear the listener by setting a null value, or by calling {@link #reset()} */
  public ModelViewExtendingSuperClassModel_ onBind(OnModelBoundListener<ModelViewExtendingSuperClassModel_, ModelViewExtendingSuperClass> listener) {
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
   * You may clear the listener by setting a null value, or by calling {@link #reset()} */
  public ModelViewExtendingSuperClassModel_ onUnbind(OnModelUnboundListener<ModelViewExtendingSuperClassModel_, ModelViewExtendingSuperClass> listener) {
    onMutation();
    this.onModelUnboundListener_epoxyGeneratedModel = listener;
    return this;
  }

  /**
   * <i>Optional</i>: Default value is 0
   *
   * @see ModelViewExtendingSuperClass#subClassValue(int)
   */
  public ModelViewExtendingSuperClassModel_ subClassValue(int subClassValue) {
    assignedAttributes_epoxyGeneratedModel.set(0);
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
    assignedAttributes_epoxyGeneratedModel.set(1);
    onMutation();
    this.superClassValue_Int = superClassValue;
    return this;
  }

  public int superClassValue() {
    return superClassValue_Int;
  }

  @Override
  public ModelViewExtendingSuperClassModel_ id(long id) {
    super.id(id);
    return this;
  }

  @Override
  public ModelViewExtendingSuperClassModel_ id(@NonNull Number... arg0) {
    super.id(arg0);
    return this;
  }

  @Override
  public ModelViewExtendingSuperClassModel_ id(long id1, long id2) {
    super.id(id1, id2);
    return this;
  }

  @Override
  public ModelViewExtendingSuperClassModel_ id(@NonNull CharSequence arg0) {
    super.id(arg0);
    return this;
  }

  @Override
  public ModelViewExtendingSuperClassModel_ id(@NonNull CharSequence arg0,
      @NonNull CharSequence... arg1) {
    super.id(arg0, arg1);
    return this;
  }

  @Override
  public ModelViewExtendingSuperClassModel_ id(@NonNull CharSequence arg0, long arg1) {
    super.id(arg0, arg1);
    return this;
  }

  @Override
  public ModelViewExtendingSuperClassModel_ layout(@LayoutRes int arg0) {
    throw new UnsupportedOperationException("Layout resources are unsupported with programmatic views.");
  }

  @Override
  public ModelViewExtendingSuperClassModel_ spanSizeOverride(@Nullable EpoxyModel.SpanSizeOverrideCallback arg0) {
    super.spanSizeOverride(arg0);
    return this;
  }

  @Override
  public ModelViewExtendingSuperClassModel_ show() {
    super.show();
    return this;
  }

  @Override
  public ModelViewExtendingSuperClassModel_ show(boolean show) {
    super.show(show);
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
    assignedAttributes_epoxyGeneratedModel.clear();
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
    int result = super.hashCode();
    result = 31 * result + (onModelBoundListener_epoxyGeneratedModel != null ? 1 : 0);
    result = 31 * result + (onModelUnboundListener_epoxyGeneratedModel != null ? 1 : 0);
    result = 31 * result + subClassValue_Int;
    result = 31 * result + superClassValue_Int;
    return result;
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