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

/**
 * Generated file. Do not modify!
 */
public class TextPropDefaultViewModel_ extends EpoxyModel<TextPropDefaultView> implements GeneratedModel<TextPropDefaultView>, TextPropDefaultViewModelBuilder {
  private final BitSet assignedAttributes_epoxyGeneratedModel = new BitSet(2);

  private OnModelBoundListener<TextPropDefaultViewModel_, TextPropDefaultView> onModelBoundListener_epoxyGeneratedModel;

  private OnModelUnboundListener<TextPropDefaultViewModel_, TextPropDefaultView> onModelUnboundListener_epoxyGeneratedModel;

  private OnModelVisibilityStateChangedListener<TextPropDefaultViewModel_, TextPropDefaultView> onModelVisibilityStateChangedListener_epoxyGeneratedModel;

  private OnModelVisibilityChangedListener<TextPropDefaultViewModel_, TextPropDefaultView> onModelVisibilityChangedListener_epoxyGeneratedModel;

  /**
   * Bitset index: 0
   */
  private StringAttributeData textWithDefault_StringAttributeData =  new StringAttributeData(R.string.string_resource_value);

  private StringAttributeData nullableTextWithDefault_StringAttributeData =  new StringAttributeData(R.string.string_resource_value);

  @Override
  public void addTo(EpoxyController controller) {
    super.addTo(controller);
    addWithDebugValidation(controller);
    if (!assignedAttributes_epoxyGeneratedModel.get(0)) {
    	throw new IllegalStateException("A value is required for textWithDefault");
    }
  }

  @Override
  public void handlePreBind(final EpoxyViewHolder holder, final TextPropDefaultView object,
      final int position) {
    validateStateHasNotChangedSinceAdded("The model was changed between being added to the controller and being bound.", position);
  }

  @Override
  public void bind(final TextPropDefaultView object) {
    super.bind(object);
    object.textWithDefault(textWithDefault_StringAttributeData.toString(object.getContext()));
    object.nullableTextWithDefault(nullableTextWithDefault_StringAttributeData.toString(object.getContext()));
  }

  @Override
  public void bind(final TextPropDefaultView object, EpoxyModel previousModel) {
    if (!(previousModel instanceof TextPropDefaultViewModel_)) {
      bind(object);
      return;
    }
    TextPropDefaultViewModel_ that = (TextPropDefaultViewModel_) previousModel;
    super.bind(object);

    if ((textWithDefault_StringAttributeData != null ? !textWithDefault_StringAttributeData.equals(that.textWithDefault_StringAttributeData) : that.textWithDefault_StringAttributeData != null)) {
      object.textWithDefault(textWithDefault_StringAttributeData.toString(object.getContext()));
    }

    if ((nullableTextWithDefault_StringAttributeData != null ? !nullableTextWithDefault_StringAttributeData.equals(that.nullableTextWithDefault_StringAttributeData) : that.nullableTextWithDefault_StringAttributeData != null)) {
      object.nullableTextWithDefault(nullableTextWithDefault_StringAttributeData.toString(object.getContext()));
    }
  }

  @Override
  public void handlePostBind(final TextPropDefaultView object, int position) {
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
  public TextPropDefaultViewModel_ onBind(
      OnModelBoundListener<TextPropDefaultViewModel_, TextPropDefaultView> listener) {
    onMutation();
    this.onModelBoundListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public void unbind(TextPropDefaultView object) {
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
  public TextPropDefaultViewModel_ onUnbind(
      OnModelUnboundListener<TextPropDefaultViewModel_, TextPropDefaultView> listener) {
    onMutation();
    this.onModelUnboundListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public void onVisibilityStateChanged(int visibilityState, final TextPropDefaultView object) {
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
  public TextPropDefaultViewModel_ onVisibilityStateChanged(
      OnModelVisibilityStateChangedListener<TextPropDefaultViewModel_, TextPropDefaultView> listener) {
    onMutation();
    this.onModelVisibilityStateChangedListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public void onVisibilityChanged(float percentVisibleHeight, float percentVisibleWidth,
      int visibleHeight, int visibleWidth, final TextPropDefaultView object) {
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
  public TextPropDefaultViewModel_ onVisibilityChanged(
      OnModelVisibilityChangedListener<TextPropDefaultViewModel_, TextPropDefaultView> listener) {
    onMutation();
    this.onModelVisibilityChangedListener_epoxyGeneratedModel = listener;
    return this;
  }

  public CharSequence getTextWithDefault(Context context) {
    return textWithDefault_StringAttributeData.toString(context);
  }

  /**
   * <i>Required.</i>
   *
   * @see TextPropDefaultView#textWithDefault(CharSequence)
   */
  public TextPropDefaultViewModel_ textWithDefault(@NonNull CharSequence textWithDefault) {
    onMutation();
    assignedAttributes_epoxyGeneratedModel.set(0);
    if (textWithDefault == null) {
      throw new IllegalArgumentException("textWithDefault cannot be null");
    }
    textWithDefault_StringAttributeData.setValue(textWithDefault);
    return this;
  }

  /**
   * Throws if a value <= 0 is set.
   * <p>
   * <i>Required.</i>
   *
   * @see TextPropDefaultView#textWithDefault(CharSequence)
   */
  public TextPropDefaultViewModel_ textWithDefault(@StringRes int stringRes) {
    onMutation();
    assignedAttributes_epoxyGeneratedModel.set(0);
    textWithDefault_StringAttributeData.setValue(stringRes);
    return this;
  }

  /**
   * Throws if a value <= 0 is set.
   * <p>
   * <i>Required.</i>
   *
   * @see TextPropDefaultView#textWithDefault(CharSequence)
   */
  public TextPropDefaultViewModel_ textWithDefault(@StringRes int stringRes, Object... formatArgs) {
    onMutation();
    assignedAttributes_epoxyGeneratedModel.set(0);
    textWithDefault_StringAttributeData.setValue(stringRes, formatArgs);
    return this;
  }

  /**
   * Throws if a value <= 0 is set.
   * <p>
   * <i>Required.</i>
   *
   * @see TextPropDefaultView#textWithDefault(CharSequence)
   */
  public TextPropDefaultViewModel_ textWithDefaultQuantityRes(@PluralsRes int pluralRes,
      int quantity, Object... formatArgs) {
    onMutation();
    assignedAttributes_epoxyGeneratedModel.set(0);
    textWithDefault_StringAttributeData.setValue(pluralRes, quantity, formatArgs);
    return this;
  }

  @Nullable
  public CharSequence getNullableTextWithDefault(Context context) {
    return nullableTextWithDefault_StringAttributeData.toString(context);
  }

  /**
   * <i>Optional</i>: Default value is R.string.string_resource_value
   *
   * @see TextPropDefaultView#nullableTextWithDefault(CharSequence)
   */
  public TextPropDefaultViewModel_ nullableTextWithDefault(
      @Nullable CharSequence nullableTextWithDefault) {
    onMutation();
    nullableTextWithDefault_StringAttributeData.setValue(nullableTextWithDefault);
    return this;
  }

  /**
   * If a value of 0 is set then this attribute will revert to its default value.
   * <p>
   * <i>Optional</i>: Default value is R.string.string_resource_value
   *
   * @see TextPropDefaultView#nullableTextWithDefault(CharSequence)
   */
  public TextPropDefaultViewModel_ nullableTextWithDefault(@StringRes int stringRes) {
    onMutation();
    nullableTextWithDefault_StringAttributeData.setValue(stringRes);
    return this;
  }

  /**
   * If a value of 0 is set then this attribute will revert to its default value.
   * <p>
   * <i>Optional</i>: Default value is R.string.string_resource_value
   *
   * @see TextPropDefaultView#nullableTextWithDefault(CharSequence)
   */
  public TextPropDefaultViewModel_ nullableTextWithDefault(@StringRes int stringRes,
      Object... formatArgs) {
    onMutation();
    nullableTextWithDefault_StringAttributeData.setValue(stringRes, formatArgs);
    return this;
  }

  /**
   * If a value of 0 is set then this attribute will revert to its default value.
   * <p>
   * <i>Optional</i>: Default value is R.string.string_resource_value
   *
   * @see TextPropDefaultView#nullableTextWithDefault(CharSequence)
   */
  public TextPropDefaultViewModel_ nullableTextWithDefaultQuantityRes(@PluralsRes int pluralRes,
      int quantity, Object... formatArgs) {
    onMutation();
    nullableTextWithDefault_StringAttributeData.setValue(pluralRes, quantity, formatArgs);
    return this;
  }

  @Override
  public TextPropDefaultViewModel_ id(long id) {
    super.id(id);
    return this;
  }

  @Override
  public TextPropDefaultViewModel_ id(@Nullable Number... ids) {
    super.id(ids);
    return this;
  }

  @Override
  public TextPropDefaultViewModel_ id(long id1, long id2) {
    super.id(id1, id2);
    return this;
  }

  @Override
  public TextPropDefaultViewModel_ id(@Nullable CharSequence key) {
    super.id(key);
    return this;
  }

  @Override
  public TextPropDefaultViewModel_ id(@Nullable CharSequence key,
      @Nullable CharSequence... otherKeys) {
    super.id(key, otherKeys);
    return this;
  }

  @Override
  public TextPropDefaultViewModel_ id(@Nullable CharSequence key, long id) {
    super.id(key, id);
    return this;
  }

  @Override
  public TextPropDefaultViewModel_ layout(@LayoutRes int layoutRes) {
    super.layout(layoutRes);
    return this;
  }

  @Override
  public TextPropDefaultViewModel_ spanSizeOverride(
      @Nullable EpoxyModel.SpanSizeOverrideCallback spanSizeCallback) {
    super.spanSizeOverride(spanSizeCallback);
    return this;
  }

  @Override
  public TextPropDefaultViewModel_ show() {
    super.show();
    return this;
  }

  @Override
  public TextPropDefaultViewModel_ show(boolean show) {
    super.show(show);
    return this;
  }

  @Override
  public TextPropDefaultViewModel_ hide() {
    super.hide();
    return this;
  }

  @Override
  @LayoutRes
  protected int getDefaultLayout() {
    return 1;
  }

  @Override
  public TextPropDefaultViewModel_ reset() {
    onModelBoundListener_epoxyGeneratedModel = null;
    onModelUnboundListener_epoxyGeneratedModel = null;
    onModelVisibilityStateChangedListener_epoxyGeneratedModel = null;
    onModelVisibilityChangedListener_epoxyGeneratedModel = null;
    assignedAttributes_epoxyGeneratedModel.clear();
    this.textWithDefault_StringAttributeData =  new StringAttributeData(R.string.string_resource_value);
    this.nullableTextWithDefault_StringAttributeData =  new StringAttributeData(R.string.string_resource_value);
    super.reset();
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof TextPropDefaultViewModel_)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    TextPropDefaultViewModel_ that = (TextPropDefaultViewModel_) o;
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
    if ((textWithDefault_StringAttributeData != null ? !textWithDefault_StringAttributeData.equals(that.textWithDefault_StringAttributeData) : that.textWithDefault_StringAttributeData != null)) {
      return false;
    }
    if ((nullableTextWithDefault_StringAttributeData != null ? !nullableTextWithDefault_StringAttributeData.equals(that.nullableTextWithDefault_StringAttributeData) : that.nullableTextWithDefault_StringAttributeData != null)) {
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
    _result = 31 * _result + (textWithDefault_StringAttributeData != null ? textWithDefault_StringAttributeData.hashCode() : 0);
    _result = 31 * _result + (nullableTextWithDefault_StringAttributeData != null ? nullableTextWithDefault_StringAttributeData.hashCode() : 0);
    return _result;
  }

  @Override
  public String toString() {
    return "TextPropDefaultViewModel_{" +
        "textWithDefault_StringAttributeData=" + textWithDefault_StringAttributeData +
        ", nullableTextWithDefault_StringAttributeData=" + nullableTextWithDefault_StringAttributeData +
        "}" + super.toString();
  }

  @Override
  public int getSpanSize(int totalSpanCount, int position, int itemCount) {
    return totalSpanCount;
  }
}
