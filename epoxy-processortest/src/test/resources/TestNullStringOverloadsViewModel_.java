package com.airbnb.epoxy;

import android.content.Context;
import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.annotation.PluralsRes;
import androidx.annotation.StringRes;
import java.lang.CharSequence;
import java.lang.Number;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;

/**
 * Generated file. Do not modify!
 */
public class TestNullStringOverloadsViewModel_ extends EpoxyModel<TestNullStringOverloadsView> implements GeneratedModel<TestNullStringOverloadsView>, TestNullStringOverloadsViewModelBuilder {
  private OnModelBoundListener<TestNullStringOverloadsViewModel_, TestNullStringOverloadsView> onModelBoundListener_epoxyGeneratedModel;

  private OnModelUnboundListener<TestNullStringOverloadsViewModel_, TestNullStringOverloadsView> onModelUnboundListener_epoxyGeneratedModel;

  private OnModelVisibilityStateChangedListener<TestNullStringOverloadsViewModel_, TestNullStringOverloadsView> onModelVisibilityStateChangedListener_epoxyGeneratedModel;

  private OnModelVisibilityChangedListener<TestNullStringOverloadsViewModel_, TestNullStringOverloadsView> onModelVisibilityChangedListener_epoxyGeneratedModel;

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
   * You may clear the listener by setting a null value, or by calling {@link #reset()}
   */
  public TestNullStringOverloadsViewModel_ onBind(
      OnModelBoundListener<TestNullStringOverloadsViewModel_, TestNullStringOverloadsView> listener) {
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
   * You may clear the listener by setting a null value, or by calling {@link #reset()}
   */
  public TestNullStringOverloadsViewModel_ onUnbind(
      OnModelUnboundListener<TestNullStringOverloadsViewModel_, TestNullStringOverloadsView> listener) {
    onMutation();
    this.onModelUnboundListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public void onVisibilityStateChanged(int visibilityState,
      final TestNullStringOverloadsView object) {
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
  public TestNullStringOverloadsViewModel_ onVisibilityStateChanged(
      OnModelVisibilityStateChangedListener<TestNullStringOverloadsViewModel_, TestNullStringOverloadsView> listener) {
    onMutation();
    this.onModelVisibilityStateChangedListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public void onVisibilityChanged(float percentVisibleHeight, float percentVisibleWidth,
      int visibleHeight, int visibleWidth, final TestNullStringOverloadsView object) {
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
  public TestNullStringOverloadsViewModel_ onVisibilityChanged(
      OnModelVisibilityChangedListener<TestNullStringOverloadsViewModel_, TestNullStringOverloadsView> listener) {
    onMutation();
    this.onModelVisibilityChangedListener_epoxyGeneratedModel = listener;
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
    title_StringAttributeData.setValue(pluralRes, quantity, formatArgs);
    return this;
  }

  @Override
  public TestNullStringOverloadsViewModel_ id(long id) {
    super.id(id);
    return this;
  }

  @Override
  public TestNullStringOverloadsViewModel_ id(@Nullable Number... ids) {
    super.id(ids);
    return this;
  }

  @Override
  public TestNullStringOverloadsViewModel_ id(long id1, long id2) {
    super.id(id1, id2);
    return this;
  }

  @Override
  public TestNullStringOverloadsViewModel_ id(@Nullable CharSequence key) {
    super.id(key);
    return this;
  }

  @Override
  public TestNullStringOverloadsViewModel_ id(@Nullable CharSequence key,
      @Nullable CharSequence... otherKeys) {
    super.id(key, otherKeys);
    return this;
  }

  @Override
  public TestNullStringOverloadsViewModel_ id(@Nullable CharSequence key, long id) {
    super.id(key, id);
    return this;
  }

  @Override
  public TestNullStringOverloadsViewModel_ layout(@LayoutRes int layoutRes) {
    super.layout(layoutRes);
    return this;
  }

  @Override
  public TestNullStringOverloadsViewModel_ spanSizeOverride(
      @Nullable EpoxyModel.SpanSizeOverrideCallback spanSizeCallback) {
    super.spanSizeOverride(spanSizeCallback);
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
    onModelVisibilityStateChangedListener_epoxyGeneratedModel = null;
    onModelVisibilityChangedListener_epoxyGeneratedModel = null;
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
    if (((onModelVisibilityStateChangedListener_epoxyGeneratedModel == null) != (that.onModelVisibilityStateChangedListener_epoxyGeneratedModel == null))) {
      return false;
    }
    if (((onModelVisibilityChangedListener_epoxyGeneratedModel == null) != (that.onModelVisibilityChangedListener_epoxyGeneratedModel == null))) {
      return false;
    }
    if ((title_StringAttributeData != null ? !title_StringAttributeData.equals(that.title_StringAttributeData) : that.title_StringAttributeData != null)) {
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
    _result = 31 * _result + (title_StringAttributeData != null ? title_StringAttributeData.hashCode() : 0);
    return _result;
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
