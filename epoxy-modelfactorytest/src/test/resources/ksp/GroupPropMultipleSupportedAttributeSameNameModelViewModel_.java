package com.airbnb.epoxy;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.lang.CharSequence;
import java.lang.IllegalArgumentException;
import java.lang.Number;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.util.BitSet;

/**
 * Generated file. Do not modify!
 */
public class GroupPropMultipleSupportedAttributeSameNameModelViewModel_ extends EpoxyModel<GroupPropMultipleSupportedAttributeSameNameModelView> implements GeneratedModel<GroupPropMultipleSupportedAttributeSameNameModelView>, GroupPropMultipleSupportedAttributeSameNameModelViewModelBuilder {
  private final BitSet assignedAttributes_epoxyGeneratedModel = new BitSet(2);

  private OnModelBoundListener<GroupPropMultipleSupportedAttributeSameNameModelViewModel_, GroupPropMultipleSupportedAttributeSameNameModelView> onModelBoundListener_epoxyGeneratedModel;

  private OnModelUnboundListener<GroupPropMultipleSupportedAttributeSameNameModelViewModel_, GroupPropMultipleSupportedAttributeSameNameModelView> onModelUnboundListener_epoxyGeneratedModel;

  private OnModelVisibilityStateChangedListener<GroupPropMultipleSupportedAttributeSameNameModelViewModel_, GroupPropMultipleSupportedAttributeSameNameModelView> onModelVisibilityStateChangedListener_epoxyGeneratedModel;

  private OnModelVisibilityChangedListener<GroupPropMultipleSupportedAttributeSameNameModelViewModel_, GroupPropMultipleSupportedAttributeSameNameModelView> onModelVisibilityChangedListener_epoxyGeneratedModel;

  /**
   * Bitset index: 0
   */
  @NonNull
  private String titleString_String;

  /**
   * Bitset index: 1
   */
  private int titleInt_Int = 0;

  @Override
  public void addTo(EpoxyController controller) {
    super.addTo(controller);
    addWithDebugValidation(controller);
  }

  @Override
  public void handlePreBind(final EpoxyViewHolder holder,
      final GroupPropMultipleSupportedAttributeSameNameModelView object, final int position) {
    validateStateHasNotChangedSinceAdded("The model was changed between being added to the controller and being bound.", position);
  }

  @Override
  public void bind(final GroupPropMultipleSupportedAttributeSameNameModelView object) {
    super.bind(object);
    if (assignedAttributes_epoxyGeneratedModel.get(0)) {
      object.setTitleString(titleString_String);
    }
    else if (assignedAttributes_epoxyGeneratedModel.get(1)) {
      object.setTitleInt(titleInt_Int);
    }
    else {
      object.setTitleInt(titleInt_Int);
    }
  }

  @Override
  public void bind(final GroupPropMultipleSupportedAttributeSameNameModelView object,
      EpoxyModel previousModel) {
    if (!(previousModel instanceof GroupPropMultipleSupportedAttributeSameNameModelViewModel_)) {
      bind(object);
      return;
    }
    GroupPropMultipleSupportedAttributeSameNameModelViewModel_ that = (GroupPropMultipleSupportedAttributeSameNameModelViewModel_) previousModel;
    super.bind(object);

    if (assignedAttributes_epoxyGeneratedModel.get(0)) {
      if (!that.assignedAttributes_epoxyGeneratedModel.get(0) || (titleString_String != null ? !titleString_String.equals(that.titleString_String) : that.titleString_String != null)) {
        object.setTitleString(titleString_String);
      }
    }
    else if (assignedAttributes_epoxyGeneratedModel.get(1)) {
      if ((titleInt_Int != that.titleInt_Int)) {
        object.setTitleInt(titleInt_Int);
      }
    }
    // A value was not set so we should use the default value, but we only need to set it if the previous model had a custom value set.
    else if (that.assignedAttributes_epoxyGeneratedModel.get(0) || that.assignedAttributes_epoxyGeneratedModel.get(1)) {
      object.setTitleInt(titleInt_Int);
    }
  }

  @Override
  public void handlePostBind(final GroupPropMultipleSupportedAttributeSameNameModelView object,
      int position) {
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
  public GroupPropMultipleSupportedAttributeSameNameModelViewModel_ onBind(
      OnModelBoundListener<GroupPropMultipleSupportedAttributeSameNameModelViewModel_, GroupPropMultipleSupportedAttributeSameNameModelView> listener) {
    onMutation();
    this.onModelBoundListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public void unbind(GroupPropMultipleSupportedAttributeSameNameModelView object) {
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
  public GroupPropMultipleSupportedAttributeSameNameModelViewModel_ onUnbind(
      OnModelUnboundListener<GroupPropMultipleSupportedAttributeSameNameModelViewModel_, GroupPropMultipleSupportedAttributeSameNameModelView> listener) {
    onMutation();
    this.onModelUnboundListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public void onVisibilityStateChanged(int visibilityState,
      final GroupPropMultipleSupportedAttributeSameNameModelView object) {
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
  public GroupPropMultipleSupportedAttributeSameNameModelViewModel_ onVisibilityStateChanged(
      OnModelVisibilityStateChangedListener<GroupPropMultipleSupportedAttributeSameNameModelViewModel_, GroupPropMultipleSupportedAttributeSameNameModelView> listener) {
    onMutation();
    this.onModelVisibilityStateChangedListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public void onVisibilityChanged(float percentVisibleHeight, float percentVisibleWidth,
      int visibleHeight, int visibleWidth,
      final GroupPropMultipleSupportedAttributeSameNameModelView object) {
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
  public GroupPropMultipleSupportedAttributeSameNameModelViewModel_ onVisibilityChanged(
      OnModelVisibilityChangedListener<GroupPropMultipleSupportedAttributeSameNameModelViewModel_, GroupPropMultipleSupportedAttributeSameNameModelView> listener) {
    onMutation();
    this.onModelVisibilityChangedListener_epoxyGeneratedModel = listener;
    return this;
  }

  /**
   * <i>Required.</i>
   *
   * @see GroupPropMultipleSupportedAttributeSameNameModelView#setTitleString(String)
   */
  public GroupPropMultipleSupportedAttributeSameNameModelViewModel_ titleString(
      @NonNull String titleString) {
    if (titleString == null) {
      throw new IllegalArgumentException("titleString cannot be null");
    }
    assignedAttributes_epoxyGeneratedModel.set(0);
    assignedAttributes_epoxyGeneratedModel.clear(1);
    this.titleInt_Int = 0;
    onMutation();
    this.titleString_String = titleString;
    return this;
  }

  @NonNull
  public String titleStringString() {
    return titleString_String;
  }

  /**
   * <i>Optional</i>: Default value is 0
   *
   * @see GroupPropMultipleSupportedAttributeSameNameModelView#setTitleInt(int)
   */
  public GroupPropMultipleSupportedAttributeSameNameModelViewModel_ titleInt(int titleInt) {
    assignedAttributes_epoxyGeneratedModel.set(1);
    assignedAttributes_epoxyGeneratedModel.clear(0);
    this.titleString_String = null;
    onMutation();
    this.titleInt_Int = titleInt;
    return this;
  }

  public int titleIntInt() {
    return titleInt_Int;
  }

  @Override
  public GroupPropMultipleSupportedAttributeSameNameModelViewModel_ id(long p0) {
    super.id(p0);
    return this;
  }

  @Override
  public GroupPropMultipleSupportedAttributeSameNameModelViewModel_ id(@Nullable Number... p0) {
    super.id(p0);
    return this;
  }

  @Override
  public GroupPropMultipleSupportedAttributeSameNameModelViewModel_ id(long p0, long p1) {
    super.id(p0, p1);
    return this;
  }

  @Override
  public GroupPropMultipleSupportedAttributeSameNameModelViewModel_ id(@Nullable CharSequence p0) {
    super.id(p0);
    return this;
  }

  @Override
  public GroupPropMultipleSupportedAttributeSameNameModelViewModel_ id(@Nullable CharSequence p0,
      @Nullable CharSequence... p1) {
    super.id(p0, p1);
    return this;
  }

  @Override
  public GroupPropMultipleSupportedAttributeSameNameModelViewModel_ id(@Nullable CharSequence p0,
      long p1) {
    super.id(p0, p1);
    return this;
  }

  @Override
  public GroupPropMultipleSupportedAttributeSameNameModelViewModel_ layout(@LayoutRes int p0) {
    super.layout(p0);
    return this;
  }

  @Override
  public GroupPropMultipleSupportedAttributeSameNameModelViewModel_ spanSizeOverride(
      @Nullable EpoxyModel.SpanSizeOverrideCallback p0) {
    super.spanSizeOverride(p0);
    return this;
  }

  @Override
  public GroupPropMultipleSupportedAttributeSameNameModelViewModel_ show() {
    super.show();
    return this;
  }

  @Override
  public GroupPropMultipleSupportedAttributeSameNameModelViewModel_ show(boolean p0) {
    super.show(p0);
    return this;
  }

  @Override
  public GroupPropMultipleSupportedAttributeSameNameModelViewModel_ hide() {
    super.hide();
    return this;
  }

  @Override
  @LayoutRes
  protected int getDefaultLayout() {
    return 1;
  }

  @Override
  public GroupPropMultipleSupportedAttributeSameNameModelViewModel_ reset() {
    onModelBoundListener_epoxyGeneratedModel = null;
    onModelUnboundListener_epoxyGeneratedModel = null;
    onModelVisibilityStateChangedListener_epoxyGeneratedModel = null;
    onModelVisibilityChangedListener_epoxyGeneratedModel = null;
    assignedAttributes_epoxyGeneratedModel.clear();
    this.titleString_String = null;
    this.titleInt_Int = 0;
    super.reset();
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof GroupPropMultipleSupportedAttributeSameNameModelViewModel_)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    GroupPropMultipleSupportedAttributeSameNameModelViewModel_ that = (GroupPropMultipleSupportedAttributeSameNameModelViewModel_) o;
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
    if ((titleString_String != null ? !titleString_String.equals(that.titleString_String) : that.titleString_String != null)) {
      return false;
    }
    if ((titleInt_Int != that.titleInt_Int)) {
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
    _result = 31 * _result + (titleString_String != null ? titleString_String.hashCode() : 0);
    _result = 31 * _result + titleInt_Int;
    return _result;
  }

  @Override
  public String toString() {
    return "GroupPropMultipleSupportedAttributeSameNameModelViewModel_{" +
        "titleString_String=" + titleString_String +
        ", titleInt_Int=" + titleInt_Int +
        "}" + super.toString();
  }

  public static GroupPropMultipleSupportedAttributeSameNameModelViewModel_ from(
      ModelProperties properties) {
    GroupPropMultipleSupportedAttributeSameNameModelViewModel_ model = new GroupPropMultipleSupportedAttributeSameNameModelViewModel_();
    model.id(properties.getId());
    if (properties.has("titleString")) {
      model.titleString(properties.getString("titleString"));
    } else if (properties.has("titleInt")) {
      model.titleInt(properties.getInt("titleInt"));
    }
    return model;
  }

  @Override
  public int getSpanSize(int totalSpanCount, int position, int itemCount) {
    return totalSpanCount;
  }
}
