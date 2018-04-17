package com.airbnb.epoxy;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.PluralsRes;
import android.support.annotation.StringRes;
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
import javax.annotation.Nullable;

/**
 * Generated file. Do not modify! */
public class TestFieldPropChildViewModel_ extends EpoxyModel<TestFieldPropChildView> implements GeneratedModel<TestFieldPropChildView>, TestFieldPropChildViewModelBuilder {
  private final BitSet assignedAttributes_epoxyGeneratedModel = new BitSet(2);

  private OnModelBoundListener<TestFieldPropChildViewModel_, TestFieldPropChildView> onModelBoundListener_epoxyGeneratedModel;

  private OnModelUnboundListener<TestFieldPropChildViewModel_, TestFieldPropChildView> onModelUnboundListener_epoxyGeneratedModel;

  /**
   * Bitset index: 0 */
  private StringAttributeData textValue_StringAttributeData =  new StringAttributeData();

  /**
   * Bitset index: 1 */
  @Nullable
  private View.OnClickListener value_OnClickListener = (View.OnClickListener) null;

  @Override
  public void addTo(EpoxyController controller) {
    super.addTo(controller);
    addWithDebugValidation(controller);
    if (!assignedAttributes_epoxyGeneratedModel.get(0)) {
    	throw new IllegalStateException("A value is required for textValue");
    }
  }

  @Override
  protected int getViewType() {
    return 0;
  }

  @Override
  protected TestFieldPropChildView buildView(ViewGroup parent) {
    TestFieldPropChildView v = new TestFieldPropChildView(parent.getContext());
    v.setLayoutParams(new ViewGroup.MarginLayoutParams(ViewGroup.MarginLayoutParams.WRAP_CONTENT, ViewGroup.MarginLayoutParams.WRAP_CONTENT));
    return v;
  }

  @Override
  public void handlePreBind(final EpoxyViewHolder holder, final TestFieldPropChildView object,
      final int position) {
    validateStateHasNotChangedSinceAdded("The model was changed between being added to the controller and being bound.", position);
  }

  @Override
  public void bind(final TestFieldPropChildView object) {
    super.bind(object);
    object.textValue = textValue_StringAttributeData.toString(object.getContext());
    object.value = value_OnClickListener;
  }

  @Override
  public void bind(final TestFieldPropChildView object, EpoxyModel previousModel) {
    if (!(previousModel instanceof TestFieldPropChildViewModel_)) {
      bind(object);
      return;
    }
    TestFieldPropChildViewModel_ that = (TestFieldPropChildViewModel_) previousModel;
    super.bind(object);

    if ((textValue_StringAttributeData != null ? !textValue_StringAttributeData.equals(that.textValue_StringAttributeData) : that.textValue_StringAttributeData != null)) {
      object.textValue = textValue_StringAttributeData.toString(object.getContext());
    }

    if ((value_OnClickListener != null ? !value_OnClickListener.equals(that.value_OnClickListener) : that.value_OnClickListener != null)) {
      object.value = value_OnClickListener;
    }
  }

  @Override
  public void handlePostBind(final TestFieldPropChildView object, int position) {
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
  public TestFieldPropChildViewModel_ onBind(OnModelBoundListener<TestFieldPropChildViewModel_, TestFieldPropChildView> listener) {
    onMutation();
    this.onModelBoundListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public void unbind(TestFieldPropChildView object) {
    super.unbind(object);
    if (onModelUnboundListener_epoxyGeneratedModel != null) {
      onModelUnboundListener_epoxyGeneratedModel.onModelUnbound(this, object);
    }
    object.value = (View.OnClickListener) null;
  }

  /**
   * Register a listener that will be called when this model is unbound from a view.
   * <p>
   * The listener will contribute to this model's hashCode state per the {@link
   * com.airbnb.epoxy.EpoxyAttribute.Option#DoNotHash} rules.
   * <p>
   * You may clear the listener by setting a null value, or by calling {@link #reset()} */
  public TestFieldPropChildViewModel_ onUnbind(OnModelUnboundListener<TestFieldPropChildViewModel_, TestFieldPropChildView> listener) {
    onMutation();
    this.onModelUnboundListener_epoxyGeneratedModel = listener;
    return this;
  }

  public CharSequence getTextValue(Context context) {
    return textValue_StringAttributeData.toString(context);
  }

  /**
   * <i>Required.</i>
   *
   * @see TestFieldPropChildView#textValue
   */
  public TestFieldPropChildViewModel_ textValue(@NonNull CharSequence textValue) {
    onMutation();
    assignedAttributes_epoxyGeneratedModel.set(0);
    if (textValue == null) {
      throw new IllegalArgumentException("textValue cannot be null");
    }
    textValue_StringAttributeData.setValue(textValue);
    return this;
  }

  /**
   * Throws if a value <= 0 is set.
   * <p>
   * <i>Required.</i>
   *
   * @see TestFieldPropChildView#textValue
   */
  public TestFieldPropChildViewModel_ textValue(@StringRes int stringRes) {
    onMutation();
    assignedAttributes_epoxyGeneratedModel.set(0);
    textValue_StringAttributeData.setValue(stringRes);
    return this;
  }

  /**
   * Throws if a value <= 0 is set.
   * <p>
   * <i>Required.</i>
   *
   * @see TestFieldPropChildView#textValue
   */
  public TestFieldPropChildViewModel_ textValue(@StringRes int stringRes, Object... formatArgs) {
    onMutation();
    assignedAttributes_epoxyGeneratedModel.set(0);
    textValue_StringAttributeData.setValue(stringRes, formatArgs);
    return this;
  }

  /**
   * Throws if a value <= 0 is set.
   * <p>
   * <i>Required.</i>
   *
   * @see TestFieldPropChildView#textValue
   */
  public TestFieldPropChildViewModel_ textValueQuantityRes(@PluralsRes int pluralRes, int quantity,
      Object... formatArgs) {
    onMutation();
    assignedAttributes_epoxyGeneratedModel.set(0);
    textValue_StringAttributeData.setValue(pluralRes, quantity, formatArgs);
    return this;
  }

  /**
   * Set a click listener that will provide the parent view, model, and adapter position of the clicked view. This will clear the normal View.OnClickListener if one has been set */
  @Nullable
  public TestFieldPropChildViewModel_ value(final OnModelClickListener<TestFieldPropChildViewModel_, TestFieldPropChildView> value) {
    assignedAttributes_epoxyGeneratedModel.set(1);
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
   * <i>Optional</i>: Default value is (View.OnClickListener) null
   *
   * @see TestFieldPropChildView#value
   */
  public TestFieldPropChildViewModel_ value(@Nullable View.OnClickListener value) {
    assignedAttributes_epoxyGeneratedModel.set(1);
    onMutation();
    this.value_OnClickListener = value;
    return this;
  }

  @Nullable
  public View.OnClickListener value() {
    return value_OnClickListener;
  }

  @Override
  public TestFieldPropChildViewModel_ id(long id) {
    super.id(id);
    return this;
  }

  @Override
  public TestFieldPropChildViewModel_ id(@NonNull Number... arg0) {
    super.id(arg0);
    return this;
  }

  @Override
  public TestFieldPropChildViewModel_ id(long id1, long id2) {
    super.id(id1, id2);
    return this;
  }

  @Override
  public TestFieldPropChildViewModel_ id(@NonNull CharSequence arg0) {
    super.id(arg0);
    return this;
  }

  @Override
  public TestFieldPropChildViewModel_ id(@NonNull CharSequence arg0,
      @NonNull CharSequence... arg1) {
    super.id(arg0, arg1);
    return this;
  }

  @Override
  public TestFieldPropChildViewModel_ id(@NonNull CharSequence arg0, long arg1) {
    super.id(arg0, arg1);
    return this;
  }

  @Override
  public TestFieldPropChildViewModel_ layout(@LayoutRes int arg0) {
    throw new UnsupportedOperationException("Layout resources are unsupported with programmatic views.");
  }

  @Override
  public TestFieldPropChildViewModel_ spanSizeOverride(@android.support.annotation.Nullable EpoxyModel.SpanSizeOverrideCallback arg0) {
    super.spanSizeOverride(arg0);
    return this;
  }

  @Override
  public TestFieldPropChildViewModel_ show() {
    super.show();
    return this;
  }

  @Override
  public TestFieldPropChildViewModel_ show(boolean show) {
    super.show(show);
    return this;
  }

  @Override
  public TestFieldPropChildViewModel_ hide() {
    super.hide();
    return this;
  }

  @Override
  @LayoutRes
  protected int getDefaultLayout() {
    throw new UnsupportedOperationException("Layout resources are unsupported for views created programmatically.");
  }

  @Override
  public TestFieldPropChildViewModel_ reset() {
    onModelBoundListener_epoxyGeneratedModel = null;
    onModelUnboundListener_epoxyGeneratedModel = null;
    assignedAttributes_epoxyGeneratedModel.clear();
    this.textValue_StringAttributeData =  new StringAttributeData();
    this.value_OnClickListener = (View.OnClickListener) null;
    super.reset();
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof TestFieldPropChildViewModel_)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    TestFieldPropChildViewModel_ that = (TestFieldPropChildViewModel_) o;
    if (((onModelBoundListener_epoxyGeneratedModel == null) != (that.onModelBoundListener_epoxyGeneratedModel == null))) {
      return false;
    }
    if (((onModelUnboundListener_epoxyGeneratedModel == null) != (that.onModelUnboundListener_epoxyGeneratedModel == null))) {
      return false;
    }
    if ((textValue_StringAttributeData != null ? !textValue_StringAttributeData.equals(that.textValue_StringAttributeData) : that.textValue_StringAttributeData != null)) {
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
    result = 31 * result + (textValue_StringAttributeData != null ? textValue_StringAttributeData.hashCode() : 0);
    result = 31 * result + (value_OnClickListener != null ? value_OnClickListener.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "TestFieldPropChildViewModel_{" +
        "textValue_StringAttributeData=" + textValue_StringAttributeData +
        ", value_OnClickListener=" + value_OnClickListener +
        "}" + super.toString();
  }

  @Override
  public int getSpanSize(int totalSpanCount, int position, int itemCount) {
    return totalSpanCount;
  }
}