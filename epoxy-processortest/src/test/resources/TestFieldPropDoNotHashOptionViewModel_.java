package com.airbnb.epoxy;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import java.lang.CharSequence;
import java.lang.IllegalArgumentException;
import java.lang.IllegalStateException;
import java.lang.Number;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.UnsupportedOperationException;
import java.util.BitSet;

/**
 * Generated file. Do not modify! */
public class TestFieldPropDoNotHashOptionViewModel_ extends EpoxyModel<TestFieldPropDoNotHashOptionView> implements GeneratedModel<TestFieldPropDoNotHashOptionView>, TestFieldPropDoNotHashOptionViewModelBuilder {
  private final BitSet assignedAttributes_epoxyGeneratedModel = new BitSet(1);

  private OnModelBoundListener<TestFieldPropDoNotHashOptionViewModel_, TestFieldPropDoNotHashOptionView> onModelBoundListener_epoxyGeneratedModel;

  private OnModelUnboundListener<TestFieldPropDoNotHashOptionViewModel_, TestFieldPropDoNotHashOptionView> onModelUnboundListener_epoxyGeneratedModel;

  /**
   * Bitset index: 0 */
  @NonNull
  private View.OnClickListener value_OnClickListener;

  @Override
  public void addTo(EpoxyController controller) {
    super.addTo(controller);
    addWithDebugValidation(controller);
    if (!assignedAttributes_epoxyGeneratedModel.get(0)) {
    	throw new IllegalStateException("A value is required for value");
    }
  }

  @Override
  protected int getViewType() {
    return 0;
  }

  @Override
  protected TestFieldPropDoNotHashOptionView buildView(ViewGroup parent) {
    TestFieldPropDoNotHashOptionView v = new TestFieldPropDoNotHashOptionView(parent.getContext());
    v.setLayoutParams(new ViewGroup.MarginLayoutParams(ViewGroup.MarginLayoutParams.WRAP_CONTENT, ViewGroup.MarginLayoutParams.WRAP_CONTENT));
    return v;
  }

  @Override
  public void handlePreBind(final EpoxyViewHolder holder,
      final TestFieldPropDoNotHashOptionView object, final int position) {
    validateStateHasNotChangedSinceAdded("The model was changed between being added to the controller and being bound.", position);
  }

  @Override
  public void bind(final TestFieldPropDoNotHashOptionView object) {
    super.bind(object);
    object.value = value_OnClickListener;
  }

  @Override
  public void bind(final TestFieldPropDoNotHashOptionView object, EpoxyModel previousModel) {
    if (!(previousModel instanceof TestFieldPropDoNotHashOptionViewModel_)) {
      bind(object);
      return;
    }
    TestFieldPropDoNotHashOptionViewModel_ that = (TestFieldPropDoNotHashOptionViewModel_) previousModel;
    super.bind(object);

    if (((value_OnClickListener == null) != (that.value_OnClickListener == null))) {
      object.value = value_OnClickListener;
    }
  }

  @Override
  public void handlePostBind(final TestFieldPropDoNotHashOptionView object, int position) {
    if (onModelBoundListener_epoxyGeneratedModel != null) {
      onModelBoundListener_epoxyGeneratedModel.onModelBound(this, object, position);
    }
    validateStateHasNotChangedSinceAdded("The model was changed during the bind call.", position);
    object.call();
  }

  /**
   * Register a listener that will be called when this model is bound to a view.
   * <p>
   * The listener will contribute to this model's hashCode state per the {@link
   * com.airbnb.epoxy.EpoxyAttribute.Option#DoNotHash} rules.
   * <p>
   * You may clear the listener by setting a null value, or by calling {@link #reset()} */
  public TestFieldPropDoNotHashOptionViewModel_ onBind(OnModelBoundListener<TestFieldPropDoNotHashOptionViewModel_, TestFieldPropDoNotHashOptionView> listener) {
    onMutation();
    this.onModelBoundListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public void unbind(TestFieldPropDoNotHashOptionView object) {
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
  public TestFieldPropDoNotHashOptionViewModel_ onUnbind(OnModelUnboundListener<TestFieldPropDoNotHashOptionViewModel_, TestFieldPropDoNotHashOptionView> listener) {
    onMutation();
    this.onModelUnboundListener_epoxyGeneratedModel = listener;
    return this;
  }

  /**
   * Set a click listener that will provide the parent view, model, and adapter position of the clicked view. This will clear the normal View.OnClickListener if one has been set */
  public TestFieldPropDoNotHashOptionViewModel_ value(@NonNull final OnModelClickListener<TestFieldPropDoNotHashOptionViewModel_, TestFieldPropDoNotHashOptionView> value) {
    assignedAttributes_epoxyGeneratedModel.set(0);
    onMutation();
    if (value == null) {
      this.value_OnClickListener = null;
    }
    else {
      this.value_OnClickListener = new WrappedEpoxyModelClickListener(value);
    }
    return this;
  }

  /**
   * <i>Required.</i>
   *
   * @see TestFieldPropDoNotHashOptionView#value
   */
  public TestFieldPropDoNotHashOptionViewModel_ value(@NonNull View.OnClickListener value) {
    if (value == null) {
      throw new IllegalArgumentException("value cannot be null");
    }
    assignedAttributes_epoxyGeneratedModel.set(0);
    onMutation();
    this.value_OnClickListener = value;
    return this;
  }

  @NonNull
  public View.OnClickListener value() {
    return value_OnClickListener;
  }

  @Override
  public TestFieldPropDoNotHashOptionViewModel_ id(long id) {
    super.id(id);
    return this;
  }

  @Override
  public TestFieldPropDoNotHashOptionViewModel_ id(@Nullable Number... arg0) {
    super.id(arg0);
    return this;
  }

  @Override
  public TestFieldPropDoNotHashOptionViewModel_ id(long id1, long id2) {
    super.id(id1, id2);
    return this;
  }

  @Override
  public TestFieldPropDoNotHashOptionViewModel_ id(@Nullable CharSequence arg0) {
    super.id(arg0);
    return this;
  }

  @Override
  public TestFieldPropDoNotHashOptionViewModel_ id(@Nullable CharSequence arg0,
      @Nullable CharSequence... arg1) {
    super.id(arg0, arg1);
    return this;
  }

  @Override
  public TestFieldPropDoNotHashOptionViewModel_ id(@Nullable CharSequence arg0, long arg1) {
    super.id(arg0, arg1);
    return this;
  }

  @Override
  public TestFieldPropDoNotHashOptionViewModel_ layout(@LayoutRes int arg0) {
    throw new UnsupportedOperationException("Layout resources are unsupported with programmatic views.");
  }

  @Override
  public TestFieldPropDoNotHashOptionViewModel_ spanSizeOverride(@Nullable EpoxyModel.SpanSizeOverrideCallback arg0) {
    super.spanSizeOverride(arg0);
    return this;
  }

  @Override
  public TestFieldPropDoNotHashOptionViewModel_ show() {
    super.show();
    return this;
  }

  @Override
  public TestFieldPropDoNotHashOptionViewModel_ show(boolean show) {
    super.show(show);
    return this;
  }

  @Override
  public TestFieldPropDoNotHashOptionViewModel_ hide() {
    super.hide();
    return this;
  }

  @Override
  @LayoutRes
  protected int getDefaultLayout() {
    throw new UnsupportedOperationException("Layout resources are unsupported for views created programmatically.");
  }

  @Override
  public TestFieldPropDoNotHashOptionViewModel_ reset() {
    onModelBoundListener_epoxyGeneratedModel = null;
    onModelUnboundListener_epoxyGeneratedModel = null;
    assignedAttributes_epoxyGeneratedModel.clear();
    this.value_OnClickListener = null;
    super.reset();
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof TestFieldPropDoNotHashOptionViewModel_)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    TestFieldPropDoNotHashOptionViewModel_ that = (TestFieldPropDoNotHashOptionViewModel_) o;
    if (((onModelBoundListener_epoxyGeneratedModel == null) != (that.onModelBoundListener_epoxyGeneratedModel == null))) {
      return false;
    }
    if (((onModelUnboundListener_epoxyGeneratedModel == null) != (that.onModelUnboundListener_epoxyGeneratedModel == null))) {
      return false;
    }
    if (((value_OnClickListener == null) != (that.value_OnClickListener == null))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + (onModelBoundListener_epoxyGeneratedModel != null ? 1 : 0);
    result = 31 * result + (onModelUnboundListener_epoxyGeneratedModel != null ? 1 : 0);
    result = 31 * result + (value_OnClickListener != null ? 1 : 0);
    return result;
  }

  @Override
  public String toString() {
    return "TestFieldPropDoNotHashOptionViewModel_{" +
        "value_OnClickListener=" + value_OnClickListener +
        "}" + super.toString();
  }

  @Override
  public int getSpanSize(int totalSpanCount, int position, int itemCount) {
    return totalSpanCount;
  }
}