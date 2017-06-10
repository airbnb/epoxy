package com.airbnb.epoxy;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.annotation.PluralsRes;
import android.support.annotation.StringRes;
import java.lang.CharSequence;
import java.lang.IllegalArgumentException;
import java.lang.IllegalStateException;
import java.lang.Number;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.util.BitSet;

/**
 * Generated file. Do not modify! */
public class TestStringOverloadsViewModel_ extends EpoxyModel<TestStringOverloadsView> implements GeneratedModel<TestStringOverloadsView> {
  private final BitSet assignedAttributes_epoxyGeneratedModel = new BitSet(1);

  private OnModelBoundListener<TestStringOverloadsViewModel_, TestStringOverloadsView> onModelBoundListener_epoxyGeneratedModel;

  private OnModelUnboundListener<TestStringOverloadsViewModel_, TestStringOverloadsView> onModelUnboundListener_epoxyGeneratedModel;

  /**
   * Bitset index: 0 */
  private StringAttributeData title_StringAttributeData =  new StringAttributeData();

  @Override
  public void addTo(EpoxyController controller) {
    super.addTo(controller);
    addWithDebugValidation(controller);
    if (!assignedAttributes_epoxyGeneratedModel.get(0)) {
    	throw new IllegalStateException("A value is required for setTitle");
    }
  }

  @Override
  public void handlePreBind(final EpoxyViewHolder holder, final TestStringOverloadsView object,
      int position) {
    validateStateHasNotChangedSinceAdded("The model was changed between being added to the controller and being bound.", position);
  }

  @Override
  public void bind(final TestStringOverloadsView object) {
    super.bind(object);
    object.setTitle(title_StringAttributeData.toString(object.getContext()));
  }

  @Override
  public void bind(final TestStringOverloadsView object, EpoxyModel previousModel) {
    if (!(previousModel instanceof TestStringOverloadsViewModel_)) {
      bind(object);
      return;
    }
    TestStringOverloadsViewModel_ that = (TestStringOverloadsViewModel_) previousModel;
    super.bind(object);

    if (!title_StringAttributeData.equals(that.title_StringAttributeData)) {
      object.setTitle(title_StringAttributeData.toString(object.getContext()));
    }
  }

  @Override
  public void handlePostBind(final TestStringOverloadsView object, int position) {
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
   * You may clear the listener by setting a null value, or by calling {@link #reset()} */
  public TestStringOverloadsViewModel_ onBind(OnModelBoundListener<TestStringOverloadsViewModel_, TestStringOverloadsView> listener) {
    onMutation();
    this.onModelBoundListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public void unbind(TestStringOverloadsView object) {
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
  public TestStringOverloadsViewModel_ onUnbind(OnModelUnboundListener<TestStringOverloadsViewModel_, TestStringOverloadsView> listener) {
    onMutation();
    this.onModelUnboundListener_epoxyGeneratedModel = listener;
    return this;
  }

  public CharSequence getTitle(Context context) {
    return title_StringAttributeData.toString(context);
  }

  /**
   * <i>Required.</i>
   *
   * @see TestStringOverloadsView#setTitle(CharSequence)
   */
  public TestStringOverloadsViewModel_ title(CharSequence title) {
    onMutation();
    assignedAttributes_epoxyGeneratedModel.set(0);
    if (title == null) {
      throw new IllegalArgumentException("title cannot be null");
    }
    title_StringAttributeData.setValue(title);
    return this;
  }

  /**
   * Throws if a value <= 0 is set.
   * <p>
   * <i>Required.</i>
   *
   * @see TestStringOverloadsView#setTitle(CharSequence)
   */
  public TestStringOverloadsViewModel_ title(@StringRes int stringRes) {
    onMutation();
    assignedAttributes_epoxyGeneratedModel.set(0);
    title_StringAttributeData.setValue(stringRes);
    return this;
  }

  /**
   * Throws if a value <= 0 is set.
   * <p>
   * <i>Required.</i>
   *
   * @see TestStringOverloadsView#setTitle(CharSequence)
   */
  public TestStringOverloadsViewModel_ title(@StringRes int stringRes, Object... formatArgs) {
    onMutation();
    assignedAttributes_epoxyGeneratedModel.set(0);
    title_StringAttributeData.setValue(stringRes, formatArgs);
    return this;
  }

  /**
   * Throws if a value <= 0 is set.
   * <p>
   * <i>Required.</i>
   *
   * @see TestStringOverloadsView#setTitle(CharSequence)
   */
  public TestStringOverloadsViewModel_ titleQuantityRes(@PluralsRes int pluralRes, int quantity,
      Object... formatArgs) {
    onMutation();
    assignedAttributes_epoxyGeneratedModel.set(0);
    title_StringAttributeData.setValue(pluralRes, quantity, formatArgs);
    return this;
  }

  @Override
  public TestStringOverloadsViewModel_ id(long id) {
    super.id(id);
    return this;
  }

  @Override
  public TestStringOverloadsViewModel_ id(Number... ids) {
    super.id(ids);
    return this;
  }

  @Override
  public TestStringOverloadsViewModel_ id(long id1, long id2) {
    super.id(id1, id2);
    return this;
  }

  @Override
  public TestStringOverloadsViewModel_ id(CharSequence key) {
    super.id(key);
    return this;
  }

  @Override
  public TestStringOverloadsViewModel_ id(CharSequence key, CharSequence... otherKeys) {
    super.id(key, otherKeys);
    return this;
  }

  @Override
  public TestStringOverloadsViewModel_ id(CharSequence key, long id) {
    super.id(key, id);
    return this;
  }

  @Override
  public TestStringOverloadsViewModel_ layout(@LayoutRes int arg0) {
    super.layout(arg0);
    return this;
  }

  @Override
  public TestStringOverloadsViewModel_ spanSizeOverride(@Nullable EpoxyModel.SpanSizeOverrideCallback arg0) {
    super.spanSizeOverride(arg0);
    return this;
  }

  @Override
  public TestStringOverloadsViewModel_ show() {
    super.show();
    return this;
  }

  @Override
  public TestStringOverloadsViewModel_ show(boolean show) {
    super.show(show);
    return this;
  }

  @Override
  public TestStringOverloadsViewModel_ hide() {
    super.hide();
    return this;
  }

  @Override
  @LayoutRes
  protected int getDefaultLayout() {
    return 1;
  }

  @Override
  public TestStringOverloadsViewModel_ reset() {
    onModelBoundListener_epoxyGeneratedModel = null;
    onModelUnboundListener_epoxyGeneratedModel = null;
    assignedAttributes_epoxyGeneratedModel.clear();
    this.title_StringAttributeData =  new StringAttributeData();
    super.reset();
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof TestStringOverloadsViewModel_)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    TestStringOverloadsViewModel_ that = (TestStringOverloadsViewModel_) o;
    if ((onModelBoundListener_epoxyGeneratedModel == null) != (that.onModelBoundListener_epoxyGeneratedModel == null)) {
      return false;
    }
    if ((onModelUnboundListener_epoxyGeneratedModel == null) != (that.onModelUnboundListener_epoxyGeneratedModel == null)) {
      return false;
    }
    if (title_StringAttributeData != null ? !title_StringAttributeData.equals(that.title_StringAttributeData) : that.title_StringAttributeData != null) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + (onModelBoundListener_epoxyGeneratedModel != null ? 1 : 0);
    result = 31 * result + (onModelUnboundListener_epoxyGeneratedModel != null ? 1 : 0);
    result = 31 * result + (title_StringAttributeData != null ? title_StringAttributeData.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "TestStringOverloadsViewModel_{" +
        "title_StringAttributeData=" + title_StringAttributeData +
        "}" + super.toString();
  }

  @Override
  public int getSpanSize(int totalSpanCount, int position, int itemCount) {
    return totalSpanCount;
  }
}