package com.airbnb.epoxy;

import android.content.Context;
import android.view.ViewGroup;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.PluralsRes;
import androidx.annotation.StringRes;
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
 * Generated file. Do not modify!
 */
public class TestFieldPropTextPropViewModel_ extends EpoxyModel<TestFieldPropTextPropView> implements GeneratedModel<TestFieldPropTextPropView>, TestFieldPropTextPropViewModelBuilder {
  private final BitSet assignedAttributes_epoxyGeneratedModel = new BitSet(1);

  private OnModelBoundListener<TestFieldPropTextPropViewModel_, TestFieldPropTextPropView> onModelBoundListener_epoxyGeneratedModel;

  private OnModelUnboundListener<TestFieldPropTextPropViewModel_, TestFieldPropTextPropView> onModelUnboundListener_epoxyGeneratedModel;

  private OnModelVisibilityStateChangedListener<TestFieldPropTextPropViewModel_, TestFieldPropTextPropView> onModelVisibilityStateChangedListener_epoxyGeneratedModel;

  private OnModelVisibilityChangedListener<TestFieldPropTextPropViewModel_, TestFieldPropTextPropView> onModelVisibilityChangedListener_epoxyGeneratedModel;

  /**
   * Bitset index: 0
   */
  private StringAttributeData value_StringAttributeData =  new StringAttributeData();

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
  public TestFieldPropTextPropView buildView(ViewGroup parent) {
    TestFieldPropTextPropView v = new TestFieldPropTextPropView(parent.getContext());
    v.setLayoutParams(new ViewGroup.MarginLayoutParams(ViewGroup.MarginLayoutParams.WRAP_CONTENT, ViewGroup.MarginLayoutParams.WRAP_CONTENT));
    return v;
  }

  @Override
  public void handlePreBind(final EpoxyViewHolder holder, final TestFieldPropTextPropView object,
      final int position) {
    validateStateHasNotChangedSinceAdded("The model was changed between being added to the controller and being bound.", position);
  }

  @Override
  public void bind(final TestFieldPropTextPropView object) {
    super.bind(object);
    object.value = value_StringAttributeData.toString(object.getContext());
  }

  @Override
  public void bind(final TestFieldPropTextPropView object, EpoxyModel previousModel) {
    if (!(previousModel instanceof TestFieldPropTextPropViewModel_)) {
      bind(object);
      return;
    }
    TestFieldPropTextPropViewModel_ that = (TestFieldPropTextPropViewModel_) previousModel;
    super.bind(object);

    if ((value_StringAttributeData != null ? !value_StringAttributeData.equals(that.value_StringAttributeData) : that.value_StringAttributeData != null)) {
      object.value = value_StringAttributeData.toString(object.getContext());
    }
  }

  @Override
  public void handlePostBind(final TestFieldPropTextPropView object, int position) {
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
   * You may clear the listener by setting a null value, or by calling {@link #reset()}
   */
  public TestFieldPropTextPropViewModel_ onBind(
      OnModelBoundListener<TestFieldPropTextPropViewModel_, TestFieldPropTextPropView> listener) {
    onMutation();
    this.onModelBoundListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public void unbind(TestFieldPropTextPropView object) {
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
  public TestFieldPropTextPropViewModel_ onUnbind(
      OnModelUnboundListener<TestFieldPropTextPropViewModel_, TestFieldPropTextPropView> listener) {
    onMutation();
    this.onModelUnboundListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public void onVisibilityStateChanged(int visibilityState,
      final TestFieldPropTextPropView object) {
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
  public TestFieldPropTextPropViewModel_ onVisibilityStateChanged(
      OnModelVisibilityStateChangedListener<TestFieldPropTextPropViewModel_, TestFieldPropTextPropView> listener) {
    onMutation();
    this.onModelVisibilityStateChangedListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public void onVisibilityChanged(float percentVisibleHeight, float percentVisibleWidth,
      int visibleHeight, int visibleWidth, final TestFieldPropTextPropView object) {
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
  public TestFieldPropTextPropViewModel_ onVisibilityChanged(
      OnModelVisibilityChangedListener<TestFieldPropTextPropViewModel_, TestFieldPropTextPropView> listener) {
    onMutation();
    this.onModelVisibilityChangedListener_epoxyGeneratedModel = listener;
    return this;
  }

  public CharSequence getValue(Context context) {
    return value_StringAttributeData.toString(context);
  }

  /**
   * <i>Required.</i>
   *
   * @see TestFieldPropTextPropView#value
   */
  public TestFieldPropTextPropViewModel_ value(@NonNull CharSequence value) {
    onMutation();
    assignedAttributes_epoxyGeneratedModel.set(0);
    if (value == null) {
      throw new IllegalArgumentException("value cannot be null");
    }
    value_StringAttributeData.setValue(value);
    return this;
  }

  /**
   * Throws if a value <= 0 is set.
   * <p>
   * <i>Required.</i>
   *
   * @see TestFieldPropTextPropView#value
   */
  public TestFieldPropTextPropViewModel_ value(@StringRes int stringRes) {
    onMutation();
    assignedAttributes_epoxyGeneratedModel.set(0);
    value_StringAttributeData.setValue(stringRes);
    return this;
  }

  /**
   * Throws if a value <= 0 is set.
   * <p>
   * <i>Required.</i>
   *
   * @see TestFieldPropTextPropView#value
   */
  public TestFieldPropTextPropViewModel_ value(@StringRes int stringRes, Object... formatArgs) {
    onMutation();
    assignedAttributes_epoxyGeneratedModel.set(0);
    value_StringAttributeData.setValue(stringRes, formatArgs);
    return this;
  }

  /**
   * Throws if a value <= 0 is set.
   * <p>
   * <i>Required.</i>
   *
   * @see TestFieldPropTextPropView#value
   */
  public TestFieldPropTextPropViewModel_ valueQuantityRes(@PluralsRes int pluralRes, int quantity,
      Object... formatArgs) {
    onMutation();
    assignedAttributes_epoxyGeneratedModel.set(0);
    value_StringAttributeData.setValue(pluralRes, quantity, formatArgs);
    return this;
  }

  @Override
  public TestFieldPropTextPropViewModel_ id(long id) {
    super.id(id);
    return this;
  }

  @Override
  public TestFieldPropTextPropViewModel_ id(@Nullable Number... ids) {
    super.id(ids);
    return this;
  }

  @Override
  public TestFieldPropTextPropViewModel_ id(long id1, long id2) {
    super.id(id1, id2);
    return this;
  }

  @Override
  public TestFieldPropTextPropViewModel_ id(@Nullable CharSequence key) {
    super.id(key);
    return this;
  }

  @Override
  public TestFieldPropTextPropViewModel_ id(@Nullable CharSequence key,
      @Nullable CharSequence... otherKeys) {
    super.id(key, otherKeys);
    return this;
  }

  @Override
  public TestFieldPropTextPropViewModel_ id(@Nullable CharSequence key, long id) {
    super.id(key, id);
    return this;
  }

  @Override
  public TestFieldPropTextPropViewModel_ layout(@LayoutRes int layoutRes) {
    throw new UnsupportedOperationException("Layout resources are unsupported with programmatic views.");
  }

  @Override
  public TestFieldPropTextPropViewModel_ spanSizeOverride(
      @Nullable EpoxyModel.SpanSizeOverrideCallback spanSizeCallback) {
    super.spanSizeOverride(spanSizeCallback);
    return this;
  }

  @Override
  public TestFieldPropTextPropViewModel_ show() {
    super.show();
    return this;
  }

  @Override
  public TestFieldPropTextPropViewModel_ show(boolean show) {
    super.show(show);
    return this;
  }

  @Override
  public TestFieldPropTextPropViewModel_ hide() {
    super.hide();
    return this;
  }

  @Override
  @LayoutRes
  protected int getDefaultLayout() {
    throw new UnsupportedOperationException("Layout resources are unsupported for views created programmatically.");
  }

  @Override
  public TestFieldPropTextPropViewModel_ reset() {
    onModelBoundListener_epoxyGeneratedModel = null;
    onModelUnboundListener_epoxyGeneratedModel = null;
    onModelVisibilityStateChangedListener_epoxyGeneratedModel = null;
    onModelVisibilityChangedListener_epoxyGeneratedModel = null;
    assignedAttributes_epoxyGeneratedModel.clear();
    this.value_StringAttributeData =  new StringAttributeData();
    super.reset();
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof TestFieldPropTextPropViewModel_)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    TestFieldPropTextPropViewModel_ that = (TestFieldPropTextPropViewModel_) o;
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
    if ((value_StringAttributeData != null ? !value_StringAttributeData.equals(that.value_StringAttributeData) : that.value_StringAttributeData != null)) {
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
    _result = 31 * _result + (value_StringAttributeData != null ? value_StringAttributeData.hashCode() : 0);
    return _result;
  }

  @Override
  public String toString() {
    return "TestFieldPropTextPropViewModel_{" +
        "value_StringAttributeData=" + value_StringAttributeData +
        "}" + super.toString();
  }

  @Override
  public int getSpanSize(int totalSpanCount, int position, int itemCount) {
    return totalSpanCount;
  }
}
