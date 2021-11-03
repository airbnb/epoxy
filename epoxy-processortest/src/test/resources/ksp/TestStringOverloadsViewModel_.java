package com.airbnb.epoxy;

import android.content.Context;
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
import java.util.BitSet;
import java.util.List;

/**
 * Generated file. Do not modify!
 */
public class TestStringOverloadsViewModel_ extends EpoxyModel<TestStringOverloadsView> implements GeneratedModel<TestStringOverloadsView>, TestStringOverloadsViewModelBuilder {
  private final BitSet assignedAttributes_epoxyGeneratedModel = new BitSet(3);

  private OnModelBoundListener<TestStringOverloadsViewModel_, TestStringOverloadsView> onModelBoundListener_epoxyGeneratedModel;

  private OnModelUnboundListener<TestStringOverloadsViewModel_, TestStringOverloadsView> onModelUnboundListener_epoxyGeneratedModel;

  private OnModelVisibilityStateChangedListener<TestStringOverloadsViewModel_, TestStringOverloadsView> onModelVisibilityStateChangedListener_epoxyGeneratedModel;

  private OnModelVisibilityChangedListener<TestStringOverloadsViewModel_, TestStringOverloadsView> onModelVisibilityChangedListener_epoxyGeneratedModel;

  /**
   * Bitset index: 0
   */
  private StringAttributeData title_StringAttributeData =  new StringAttributeData();

  /**
   * Bitset index: 1
   */
  @Nullable
  private List<CharSequence> title_List = (List<CharSequence>) null;

  /**
   * Bitset index: 2
   */
  private StringAttributeData titleViaValueShortcut_StringAttributeData =  new StringAttributeData();

  @Override
  public void addTo(EpoxyController controller) {
    super.addTo(controller);
    addWithDebugValidation(controller);
    if (!assignedAttributes_epoxyGeneratedModel.get(2)) {
    	throw new IllegalStateException("A value is required for setTitleViaValueShortcut");
    }
  }

  @Override
  public void handlePreBind(final EpoxyViewHolder holder, final TestStringOverloadsView object,
      final int position) {
    validateStateHasNotChangedSinceAdded("The model was changed between being added to the controller and being bound.", position);
  }

  @Override
  public void bind(final TestStringOverloadsView object) {
    super.bind(object);
    object.setTitleViaValueShortcut(titleViaValueShortcut_StringAttributeData.toString(object.getContext()));
    if (assignedAttributes_epoxyGeneratedModel.get(0)) {
      object.setTitle(title_StringAttributeData.toString(object.getContext()));
    }
    else if (assignedAttributes_epoxyGeneratedModel.get(1)) {
      object.setTitle(title_List);
    }
    else {
      object.setTitle(title_List);
    }
  }

  @Override
  public void bind(final TestStringOverloadsView object, EpoxyModel previousModel) {
    if (!(previousModel instanceof TestStringOverloadsViewModel_)) {
      bind(object);
      return;
    }
    TestStringOverloadsViewModel_ that = (TestStringOverloadsViewModel_) previousModel;
    super.bind(object);

    if ((titleViaValueShortcut_StringAttributeData != null ? !titleViaValueShortcut_StringAttributeData.equals(that.titleViaValueShortcut_StringAttributeData) : that.titleViaValueShortcut_StringAttributeData != null)) {
      object.setTitleViaValueShortcut(titleViaValueShortcut_StringAttributeData.toString(object.getContext()));
    }

    if (assignedAttributes_epoxyGeneratedModel.get(0)) {
      if (!that.assignedAttributes_epoxyGeneratedModel.get(0) || (title_StringAttributeData != null ? !title_StringAttributeData.equals(that.title_StringAttributeData) : that.title_StringAttributeData != null)) {
        object.setTitle(title_StringAttributeData.toString(object.getContext()));
      }
    }
    else if (assignedAttributes_epoxyGeneratedModel.get(1)) {
      if (!that.assignedAttributes_epoxyGeneratedModel.get(1) || (title_List != null ? !title_List.equals(that.title_List) : that.title_List != null)) {
        object.setTitle(title_List);
      }
    }
    // A value was not set so we should use the default value, but we only need to set it if the previous model had a custom value set.
    else if (that.assignedAttributes_epoxyGeneratedModel.get(0) || that.assignedAttributes_epoxyGeneratedModel.get(1)) {
      object.setTitle(title_List);
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
   * You may clear the listener by setting a null value, or by calling {@link #reset()}
   */
  public TestStringOverloadsViewModel_ onBind(
      OnModelBoundListener<TestStringOverloadsViewModel_, TestStringOverloadsView> listener) {
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
   * You may clear the listener by setting a null value, or by calling {@link #reset()}
   */
  public TestStringOverloadsViewModel_ onUnbind(
      OnModelUnboundListener<TestStringOverloadsViewModel_, TestStringOverloadsView> listener) {
    onMutation();
    this.onModelUnboundListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public void onVisibilityStateChanged(int visibilityState, final TestStringOverloadsView object) {
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
  public TestStringOverloadsViewModel_ onVisibilityStateChanged(
      OnModelVisibilityStateChangedListener<TestStringOverloadsViewModel_, TestStringOverloadsView> listener) {
    onMutation();
    this.onModelVisibilityStateChangedListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public void onVisibilityChanged(float percentVisibleHeight, float percentVisibleWidth,
      int visibleHeight, int visibleWidth, final TestStringOverloadsView object) {
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
  public TestStringOverloadsViewModel_ onVisibilityChanged(
      OnModelVisibilityChangedListener<TestStringOverloadsViewModel_, TestStringOverloadsView> listener) {
    onMutation();
    this.onModelVisibilityChangedListener_epoxyGeneratedModel = listener;
    return this;
  }

  public CharSequence titleStringAttributeData(Context context) {
    return title_StringAttributeData.toString(context);
  }

  /**
   * <i>Required.</i>
   *
   * @see TestStringOverloadsView#setTitle(CharSequence)
   */
  public TestStringOverloadsViewModel_ title(@NonNull CharSequence title) {
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

  /**
   * <i>Optional</i>: Default value is (List<CharSequence>) null
   *
   * @see TestStringOverloadsView#setTitle(List<CharSequence>)
   */
  public TestStringOverloadsViewModel_ title(@Nullable List<CharSequence> title) {
    assignedAttributes_epoxyGeneratedModel.set(1);
    assignedAttributes_epoxyGeneratedModel.clear(0);
    this.title_StringAttributeData =  new StringAttributeData();
    onMutation();
    this.title_List = title;
    return this;
  }

  @Nullable
  public List<CharSequence> titleList() {
    return title_List;
  }

  public CharSequence getTitleViaValueShortcut(Context context) {
    return titleViaValueShortcut_StringAttributeData.toString(context);
  }

  /**
   * <i>Required.</i>
   *
   * @see TestStringOverloadsView#setTitleViaValueShortcut(CharSequence)
   */
  public TestStringOverloadsViewModel_ titleViaValueShortcut(
      @NonNull CharSequence titleViaValueShortcut) {
    onMutation();
    assignedAttributes_epoxyGeneratedModel.set(2);
    if (titleViaValueShortcut == null) {
      throw new IllegalArgumentException("titleViaValueShortcut cannot be null");
    }
    titleViaValueShortcut_StringAttributeData.setValue(titleViaValueShortcut);
    return this;
  }

  /**
   * Throws if a value <= 0 is set.
   * <p>
   * <i>Required.</i>
   *
   * @see TestStringOverloadsView#setTitleViaValueShortcut(CharSequence)
   */
  public TestStringOverloadsViewModel_ titleViaValueShortcut(@StringRes int stringRes) {
    onMutation();
    assignedAttributes_epoxyGeneratedModel.set(2);
    titleViaValueShortcut_StringAttributeData.setValue(stringRes);
    return this;
  }

  /**
   * Throws if a value <= 0 is set.
   * <p>
   * <i>Required.</i>
   *
   * @see TestStringOverloadsView#setTitleViaValueShortcut(CharSequence)
   */
  public TestStringOverloadsViewModel_ titleViaValueShortcut(@StringRes int stringRes,
      Object... formatArgs) {
    onMutation();
    assignedAttributes_epoxyGeneratedModel.set(2);
    titleViaValueShortcut_StringAttributeData.setValue(stringRes, formatArgs);
    return this;
  }

  /**
   * Throws if a value <= 0 is set.
   * <p>
   * <i>Required.</i>
   *
   * @see TestStringOverloadsView#setTitleViaValueShortcut(CharSequence)
   */
  public TestStringOverloadsViewModel_ titleViaValueShortcutQuantityRes(@PluralsRes int pluralRes,
      int quantity, Object... formatArgs) {
    onMutation();
    assignedAttributes_epoxyGeneratedModel.set(2);
    titleViaValueShortcut_StringAttributeData.setValue(pluralRes, quantity, formatArgs);
    return this;
  }

  @Override
  public TestStringOverloadsViewModel_ id(long p0) {
    super.id(p0);
    return this;
  }

  @Override
  public TestStringOverloadsViewModel_ id(@Nullable Number... p0) {
    super.id(p0);
    return this;
  }

  @Override
  public TestStringOverloadsViewModel_ id(long p0, long p1) {
    super.id(p0, p1);
    return this;
  }

  @Override
  public TestStringOverloadsViewModel_ id(@Nullable CharSequence p0) {
    super.id(p0);
    return this;
  }

  @Override
  public TestStringOverloadsViewModel_ id(@Nullable CharSequence p0, @Nullable CharSequence... p1) {
    super.id(p0, p1);
    return this;
  }

  @Override
  public TestStringOverloadsViewModel_ id(@Nullable CharSequence p0, long p1) {
    super.id(p0, p1);
    return this;
  }

  @Override
  public TestStringOverloadsViewModel_ layout(@LayoutRes int p0) {
    super.layout(p0);
    return this;
  }

  @Override
  public TestStringOverloadsViewModel_ spanSizeOverride(
      @Nullable EpoxyModel.SpanSizeOverrideCallback p0) {
    super.spanSizeOverride(p0);
    return this;
  }

  @Override
  public TestStringOverloadsViewModel_ show() {
    super.show();
    return this;
  }

  @Override
  public TestStringOverloadsViewModel_ show(boolean p0) {
    super.show(p0);
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
    onModelVisibilityStateChangedListener_epoxyGeneratedModel = null;
    onModelVisibilityChangedListener_epoxyGeneratedModel = null;
    assignedAttributes_epoxyGeneratedModel.clear();
    this.title_StringAttributeData =  new StringAttributeData();
    this.title_List = (List<CharSequence>) null;
    this.titleViaValueShortcut_StringAttributeData =  new StringAttributeData();
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
    if ((title_List != null ? !title_List.equals(that.title_List) : that.title_List != null)) {
      return false;
    }
    if ((titleViaValueShortcut_StringAttributeData != null ? !titleViaValueShortcut_StringAttributeData.equals(that.titleViaValueShortcut_StringAttributeData) : that.titleViaValueShortcut_StringAttributeData != null)) {
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
    _result = 31 * _result + (title_List != null ? title_List.hashCode() : 0);
    _result = 31 * _result + (titleViaValueShortcut_StringAttributeData != null ? titleViaValueShortcut_StringAttributeData.hashCode() : 0);
    return _result;
  }

  @Override
  public String toString() {
    return "TestStringOverloadsViewModel_{" +
        "title_StringAttributeData=" + title_StringAttributeData +
        ", title_List=" + title_List +
        ", titleViaValueShortcut_StringAttributeData=" + titleViaValueShortcut_StringAttributeData +
        "}" + super.toString();
  }

  @Override
  public int getSpanSize(int totalSpanCount, int position, int itemCount) {
    return totalSpanCount;
  }
}
