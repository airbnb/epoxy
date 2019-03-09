package com.airbnb.epoxy;

import android.view.View;
import android.view.ViewGroup;
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
import java.lang.UnsupportedOperationException;
import java.util.BitSet;

/**
 * Generated file. Do not modify! */
public class TestFieldPropIgnoreRequireHashCodeOptionViewModel_ extends EpoxyModel<TestFieldPropIgnoreRequireHashCodeOptionView> implements GeneratedModel<TestFieldPropIgnoreRequireHashCodeOptionView>, TestFieldPropIgnoreRequireHashCodeOptionViewModelBuilder {
  private final BitSet assignedAttributes_epoxyGeneratedModel = new BitSet(1);

  private OnModelBoundListener<TestFieldPropIgnoreRequireHashCodeOptionViewModel_, TestFieldPropIgnoreRequireHashCodeOptionView> onModelBoundListener_epoxyGeneratedModel;

  private OnModelUnboundListener<TestFieldPropIgnoreRequireHashCodeOptionViewModel_, TestFieldPropIgnoreRequireHashCodeOptionView> onModelUnboundListener_epoxyGeneratedModel;

  private OnModelVisibilityStateChangedListener<TestFieldPropIgnoreRequireHashCodeOptionViewModel_, TestFieldPropIgnoreRequireHashCodeOptionView> onModelVisibilityStateChangedListener_epoxyGeneratedModel;

  private OnModelVisibilityChangedListener<TestFieldPropIgnoreRequireHashCodeOptionViewModel_, TestFieldPropIgnoreRequireHashCodeOptionView> onModelVisibilityChangedListener_epoxyGeneratedModel;

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
  protected TestFieldPropIgnoreRequireHashCodeOptionView buildView(ViewGroup parent) {
    TestFieldPropIgnoreRequireHashCodeOptionView v = new TestFieldPropIgnoreRequireHashCodeOptionView(parent.getContext());
    v.setLayoutParams(new ViewGroup.MarginLayoutParams(ViewGroup.MarginLayoutParams.WRAP_CONTENT, ViewGroup.MarginLayoutParams.WRAP_CONTENT));
    return v;
  }

  @Override
  public void handlePreBind(final EpoxyViewHolder holder,
      final TestFieldPropIgnoreRequireHashCodeOptionView object, final int position) {
    validateStateHasNotChangedSinceAdded("The model was changed between being added to the controller and being bound.", position);
  }

  @Override
  public void bind(final TestFieldPropIgnoreRequireHashCodeOptionView object) {
    super.bind(object);
    object.value = value_OnClickListener;
  }

  @Override
  public void bind(final TestFieldPropIgnoreRequireHashCodeOptionView object,
      EpoxyModel previousModel) {
    if (!(previousModel instanceof TestFieldPropIgnoreRequireHashCodeOptionViewModel_)) {
      bind(object);
      return;
    }
    TestFieldPropIgnoreRequireHashCodeOptionViewModel_ that = (TestFieldPropIgnoreRequireHashCodeOptionViewModel_) previousModel;
    super.bind(object);

    if ((value_OnClickListener != null ? !value_OnClickListener.equals(that.value_OnClickListener) : that.value_OnClickListener != null)) {
      object.value = value_OnClickListener;
    }
  }

  @Override
  public void handlePostBind(final TestFieldPropIgnoreRequireHashCodeOptionView object,
      int position) {
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
  public TestFieldPropIgnoreRequireHashCodeOptionViewModel_ onBind(
      OnModelBoundListener<TestFieldPropIgnoreRequireHashCodeOptionViewModel_, TestFieldPropIgnoreRequireHashCodeOptionView> listener) {
    onMutation();
    this.onModelBoundListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public void unbind(TestFieldPropIgnoreRequireHashCodeOptionView object) {
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
  public TestFieldPropIgnoreRequireHashCodeOptionViewModel_ onUnbind(
      OnModelUnboundListener<TestFieldPropIgnoreRequireHashCodeOptionViewModel_, TestFieldPropIgnoreRequireHashCodeOptionView> listener) {
    onMutation();
    this.onModelUnboundListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public void onVisibilityStateChanged(int visibilityState,
      final TestFieldPropIgnoreRequireHashCodeOptionView object) {
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
  public TestFieldPropIgnoreRequireHashCodeOptionViewModel_ onVisibilityStateChanged(
      OnModelVisibilityStateChangedListener<TestFieldPropIgnoreRequireHashCodeOptionViewModel_, TestFieldPropIgnoreRequireHashCodeOptionView> listener) {
    onMutation();
    this.onModelVisibilityStateChangedListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public void onVisibilityChanged(float percentVisibleHeight, float percentVisibleWidth,
      int visibleHeight, int visibleWidth,
      final TestFieldPropIgnoreRequireHashCodeOptionView object) {
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
  public TestFieldPropIgnoreRequireHashCodeOptionViewModel_ onVisibilityChanged(
      OnModelVisibilityChangedListener<TestFieldPropIgnoreRequireHashCodeOptionViewModel_, TestFieldPropIgnoreRequireHashCodeOptionView> listener) {
    onMutation();
    this.onModelVisibilityChangedListener_epoxyGeneratedModel = listener;
    return this;
  }

  /**
   * Set a click listener that will provide the parent view, model, and adapter position of the clicked view. This will clear the normal View.OnClickListener if one has been set */
  public TestFieldPropIgnoreRequireHashCodeOptionViewModel_ value(
      @NonNull final OnModelClickListener<TestFieldPropIgnoreRequireHashCodeOptionViewModel_, TestFieldPropIgnoreRequireHashCodeOptionView> value) {
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
   * @see TestFieldPropIgnoreRequireHashCodeOptionView#value
   */
  public TestFieldPropIgnoreRequireHashCodeOptionViewModel_ value(
      @NonNull View.OnClickListener value) {
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
  public TestFieldPropIgnoreRequireHashCodeOptionViewModel_ id(long id) {
    super.id(id);
    return this;
  }

  @Override
  public TestFieldPropIgnoreRequireHashCodeOptionViewModel_ id(@Nullable Number... arg0) {
    super.id(arg0);
    return this;
  }

  @Override
  public TestFieldPropIgnoreRequireHashCodeOptionViewModel_ id(long id1, long id2) {
    super.id(id1, id2);
    return this;
  }

  @Override
  public TestFieldPropIgnoreRequireHashCodeOptionViewModel_ id(@Nullable CharSequence arg0) {
    super.id(arg0);
    return this;
  }

  @Override
  public TestFieldPropIgnoreRequireHashCodeOptionViewModel_ id(@Nullable CharSequence arg0,
      @Nullable CharSequence... arg1) {
    super.id(arg0, arg1);
    return this;
  }

  @Override
  public TestFieldPropIgnoreRequireHashCodeOptionViewModel_ id(@Nullable CharSequence arg0,
      long arg1) {
    super.id(arg0, arg1);
    return this;
  }

  @Override
  public TestFieldPropIgnoreRequireHashCodeOptionViewModel_ layout(@LayoutRes int arg0) {
    throw new UnsupportedOperationException("Layout resources are unsupported with programmatic views.");
  }

  @Override
  public TestFieldPropIgnoreRequireHashCodeOptionViewModel_ spanSizeOverride(
      @Nullable EpoxyModel.SpanSizeOverrideCallback arg0) {
    super.spanSizeOverride(arg0);
    return this;
  }

  @Override
  public TestFieldPropIgnoreRequireHashCodeOptionViewModel_ show() {
    super.show();
    return this;
  }

  @Override
  public TestFieldPropIgnoreRequireHashCodeOptionViewModel_ show(boolean show) {
    super.show(show);
    return this;
  }

  @Override
  public TestFieldPropIgnoreRequireHashCodeOptionViewModel_ hide() {
    super.hide();
    return this;
  }

  @Override
  @LayoutRes
  protected int getDefaultLayout() {
    throw new UnsupportedOperationException("Layout resources are unsupported for views created programmatically.");
  }

  @Override
  public TestFieldPropIgnoreRequireHashCodeOptionViewModel_ reset() {
    onModelBoundListener_epoxyGeneratedModel = null;
    onModelUnboundListener_epoxyGeneratedModel = null;
    onModelVisibilityStateChangedListener_epoxyGeneratedModel = null;
    onModelVisibilityChangedListener_epoxyGeneratedModel = null;
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
    if (!(o instanceof TestFieldPropIgnoreRequireHashCodeOptionViewModel_)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    TestFieldPropIgnoreRequireHashCodeOptionViewModel_ that = (TestFieldPropIgnoreRequireHashCodeOptionViewModel_) o;
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
    if ((value_OnClickListener != null ? !value_OnClickListener.equals(that.value_OnClickListener) : that.value_OnClickListener != null)) {
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
    result = 31 * result + (value_OnClickListener != null ? value_OnClickListener.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "TestFieldPropIgnoreRequireHashCodeOptionViewModel_{" +
        "value_OnClickListener=" + value_OnClickListener +
        "}" + super.toString();
  }

  @Override
  public int getSpanSize(int totalSpanCount, int position, int itemCount) {
    return totalSpanCount;
  }
}
