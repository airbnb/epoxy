package com.airbnb.epoxy;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.PluralsRes;
import android.support.annotation.StringRes;
import java.lang.CharSequence;
import java.lang.Number;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.util.BitSet;

/**
 * Generated file. Do not modify! */
public class TestNullStringOverloadsViewModel_ extends EpoxyModel<TestNullStringOverloadsView> implements GeneratedModel<TestNullStringOverloadsView>, TestNullStringOverloadsViewModelBuilder {
  private final BitSet assignedAttributes_epoxyGeneratedModel = new BitSet(1);

  private OnModelBoundListener<TestNullStringOverloadsViewModel_, TestNullStringOverloadsView> onModelBoundListener_epoxyGeneratedModel;

  private OnModelUnboundListener<TestNullStringOverloadsViewModel_, TestNullStringOverloadsView> onModelUnboundListener_epoxyGeneratedModel;

  /**
   * Bitset index: 0 */
  private StringAttributeData title_StringAttributeData =  new StringAttributeData((CharSequence) null);

  @Override
  public void addTo(EpoxyController controller) {
    super.addTo(controller);
    addWithDebugValidation(controller);
  }

  @Override
  public void handlePreBind(final EpoxyViewHolder holder, final TestNullStringOverloadsView object,
      final int position) {
    validateStateHasNotChangedSinceAdded("The model was changed between being added to the controller and being bound.", position);
  }

  @Override
  public void bind(final TestNullStringOverloadsView object) {
    super.bind(object);
    object.setTitle(title_StringAttributeData.toString(object.getContext()));
  }

  @Override
  public void bind(final TestNullStringOverloadsView object, EpoxyModel previousModel) {
    if (!(previousModel instanceof TestNullStringOverloadsViewModel_)) {
      bind(object);
      return;
    }
    TestNullStringOverloadsViewModel_ that = (TestNullStringOverloadsViewModel_) previousModel;
    super.bind(object);

    if ((title_StringAttributeData != null ? !title_StringAttributeData.equals(that.title_StringAttributeData) : that.title_StringAttributeData != null)) {
      object.setTitle(title_StringAttributeData.toString(object.getContext()));
    }
  }

  @Override
  public void handlePostBind(final TestNullStringOverloadsView object, int position) {
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
  public TestNullStringOverloadsViewModel_ onBind(OnModelBoundListener<TestNullStringOverloadsViewModel_, TestNullStringOverloadsView> listener) {
    onMutation();
    this.onModelBoundListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public void unbind(TestNullStringOverloadsView object) {
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
  public TestNullStringOverloadsViewModel_ onUnbind(OnModelUnboundListener<TestNullStringOverloadsViewModel_, TestNullStringOverloadsView> listener) {
    onMutation();
    this.onModelUnboundListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Nullable
  public CharSequence getTitle(Context context) {
    return title_StringAttributeData.toString(context);
  }

  /**
   * <i>Optional</i>: Default value is (CharSequence) null
   *
   * @see TestNullStringOverloadsView#setTitle(CharSequence)
   */
  public TestNullStringOverloadsViewModel_ title(@Nullable CharSequence title) {
    onMutation();
    assignedAttributes_epoxyGeneratedModel.set(0);
    title_StringAttributeData.setValue(title);
    return this;
  }

  /**
   * If a value of 0 is set then this attribute will revert to its default value.
   * <p>
   * <i>Optional</i>: Default value is (CharSequence) null
   *
   * @see TestNullStringOverloadsView#setTitle(CharSequence)
   */
  public TestNullStringOverloadsViewModel_ title(@StringRes int stringRes) {
    onMutation();
    assignedAttributes_epoxyGeneratedModel.set(0);
    title_StringAttributeData.setValue(stringRes);
    return this;
  }

  /**
   * If a value of 0 is set then this attribute will revert to its default value.
   * <p>
   * <i>Optional</i>: Default value is (CharSequence) null
   *
   * @see TestNullStringOverloadsView#setTitle(CharSequence)
   */
  public TestNullStringOverloadsViewModel_ title(@StringRes int stringRes, Object... formatArgs) {
    onMutation();
    assignedAttributes_epoxyGeneratedModel.set(0);
    title_StringAttributeData.setValue(stringRes, formatArgs);
    return this;
  }

  /**
   * If a value of 0 is set then this attribute will revert to its default value.
   * <p>
   * <i>Optional</i>: Default value is (CharSequence) null
   *
   * @see TestNullStringOverloadsView#setTitle(CharSequence)
   */
  public TestNullStringOverloadsViewModel_ titleQuantityRes(@PluralsRes int pluralRes, int quantity,
      Object... formatArgs) {
    onMutation();
    assignedAttributes_epoxyGeneratedModel.set(0);
    title_StringAttributeData.setValue(pluralRes, quantity, formatArgs);
    return this;
  }

  @Override
  public TestNullStringOverloadsViewModel_ id(long id) {
    super.id(id);
    return this;
  }

  @Override
  public TestNullStringOverloadsViewModel_ id(@NonNull Number... arg0) {
    super.id(arg0);
    return this;
  }

  @Override
  public TestNullStringOverloadsViewModel_ id(long id1, long id2) {
    super.id(id1, id2);
    return this;
  }

  @Override
  public TestNullStringOverloadsViewModel_ id(@NonNull CharSequence arg0) {
    super.id(arg0);
    return this;
  }

  @Override
  public TestNullStringOverloadsViewModel_ id(@NonNull CharSequence arg0,
      @NonNull CharSequence... arg1) {
    super.id(arg0, arg1);
    return this;
  }

  @Override
  public TestNullStringOverloadsViewModel_ id(@NonNull CharSequence arg0, long arg1) {
    super.id(arg0, arg1);
    return this;
  }

  @Override
  public TestNullStringOverloadsViewModel_ layout(@LayoutRes int arg0) {
    super.layout(arg0);
    return this;
  }

  @Override
  public TestNullStringOverloadsViewModel_ spanSizeOverride(@Nullable EpoxyModel.SpanSizeOverrideCallback arg0) {
    super.spanSizeOverride(arg0);
    return this;
  }

  @Override
  public TestNullStringOverloadsViewModel_ show() {
    super.show();
    return this;
  }

  @Override
  public TestNullStringOverloadsViewModel_ show(boolean show) {
    super.show(show);
    return this;
  }

  @Override
  public TestNullStringOverloadsViewModel_ hide() {
    super.hide();
    return this;
  }

  @Override
  @LayoutRes
  protected int getDefaultLayout() {
    return 1;
  }

  @Override
  public TestNullStringOverloadsViewModel_ reset() {
    onModelBoundListener_epoxyGeneratedModel = null;
    onModelUnboundListener_epoxyGeneratedModel = null;
    assignedAttributes_epoxyGeneratedModel.clear();
    this.title_StringAttributeData =  new StringAttributeData((CharSequence) null);
    super.reset();
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof TestNullStringOverloadsViewModel_)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    TestNullStringOverloadsViewModel_ that = (TestNullStringOverloadsViewModel_) o;
    if (((onModelBoundListener_epoxyGeneratedModel == null) != (that.onModelBoundListener_epoxyGeneratedModel == null))) {
      return false;
    }
    if (((onModelUnboundListener_epoxyGeneratedModel == null) != (that.onModelUnboundListener_epoxyGeneratedModel == null))) {
      return false;
    }
    if ((title_StringAttributeData != null ? !title_StringAttributeData.equals(that.title_StringAttributeData) : that.title_StringAttributeData != null)) {
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
    return "TestNullStringOverloadsViewModel_{" +
        "title_StringAttributeData=" + title_StringAttributeData +
        "}" + super.toString();
  }

  @Override
  public int getSpanSize(int totalSpanCount, int position, int itemCount) {
    return totalSpanCount;
  }
}