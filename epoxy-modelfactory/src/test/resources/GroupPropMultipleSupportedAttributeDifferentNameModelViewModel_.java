package com.airbnb.epoxy;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.lang.CharSequence;
import java.lang.IllegalArgumentException;
import java.lang.Number;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.util.BitSet;

/**
 * Generated file. Do not modify! */
public class GroupPropMultipleSupportedAttributeDifferentNameModelViewModel_ extends EpoxyModel<GroupPropMultipleSupportedAttributeDifferentNameModelView> implements GeneratedModel<GroupPropMultipleSupportedAttributeDifferentNameModelView>, GroupPropMultipleSupportedAttributeDifferentNameModelViewModelBuilder {
  private final BitSet assignedAttributes_epoxyGeneratedModel = new BitSet(2);

  private OnModelBoundListener<GroupPropMultipleSupportedAttributeDifferentNameModelViewModel_, GroupPropMultipleSupportedAttributeDifferentNameModelView> onModelBoundListener_epoxyGeneratedModel;

  private OnModelUnboundListener<GroupPropMultipleSupportedAttributeDifferentNameModelViewModel_, GroupPropMultipleSupportedAttributeDifferentNameModelView> onModelUnboundListener_epoxyGeneratedModel;

  /**
   * Bitset index: 0 */
  @NonNull
  private String titleString_String;

  /**
   * Bitset index: 1 */
  private int titleInt_Int = 0;

  @Override
  public void addTo(EpoxyController controller) {
    super.addTo(controller);
    addWithDebugValidation(controller);
  }

  @Override
  public void handlePreBind(final EpoxyViewHolder holder,
      final GroupPropMultipleSupportedAttributeDifferentNameModelView object, final int position) {
    validateStateHasNotChangedSinceAdded("The model was changed between being added to the controller and being bound.", position);
  }

  @Override
  public void bind(final GroupPropMultipleSupportedAttributeDifferentNameModelView object) {
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
  public void bind(final GroupPropMultipleSupportedAttributeDifferentNameModelView object,
      EpoxyModel previousModel) {
    if (!(previousModel instanceof GroupPropMultipleSupportedAttributeDifferentNameModelViewModel_)) {
      bind(object);
      return;
    }
    GroupPropMultipleSupportedAttributeDifferentNameModelViewModel_ that = (GroupPropMultipleSupportedAttributeDifferentNameModelViewModel_) previousModel;
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
  public void handlePostBind(final GroupPropMultipleSupportedAttributeDifferentNameModelView object,
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
   * You may clear the listener by setting a null value, or by calling {@link #reset()} */
  public GroupPropMultipleSupportedAttributeDifferentNameModelViewModel_ onBind(OnModelBoundListener<GroupPropMultipleSupportedAttributeDifferentNameModelViewModel_, GroupPropMultipleSupportedAttributeDifferentNameModelView> listener) {
    onMutation();
    this.onModelBoundListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public void unbind(GroupPropMultipleSupportedAttributeDifferentNameModelView object) {
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
  public GroupPropMultipleSupportedAttributeDifferentNameModelViewModel_ onUnbind(OnModelUnboundListener<GroupPropMultipleSupportedAttributeDifferentNameModelViewModel_, GroupPropMultipleSupportedAttributeDifferentNameModelView> listener) {
    onMutation();
    this.onModelUnboundListener_epoxyGeneratedModel = listener;
    return this;
  }

  /**
   * <i>Required.</i>
   *
   * @see GroupPropMultipleSupportedAttributeDifferentNameModelView#setTitleString(String)
   */
  public GroupPropMultipleSupportedAttributeDifferentNameModelViewModel_ titleString(@NonNull String titleString) {
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
   * @see GroupPropMultipleSupportedAttributeDifferentNameModelView#setTitleInt(int)
   */
  public GroupPropMultipleSupportedAttributeDifferentNameModelViewModel_ titleInt(int titleInt) {
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
  public GroupPropMultipleSupportedAttributeDifferentNameModelViewModel_ id(long id) {
    super.id(id);
    return this;
  }

  @Override
  public GroupPropMultipleSupportedAttributeDifferentNameModelViewModel_ id(@Nullable Number... arg0) {
    super.id(arg0);
    return this;
  }

  @Override
  public GroupPropMultipleSupportedAttributeDifferentNameModelViewModel_ id(long id1, long id2) {
    super.id(id1, id2);
    return this;
  }

  @Override
  public GroupPropMultipleSupportedAttributeDifferentNameModelViewModel_ id(@Nullable CharSequence arg0) {
    super.id(arg0);
    return this;
  }

  @Override
  public GroupPropMultipleSupportedAttributeDifferentNameModelViewModel_ id(@Nullable CharSequence arg0,
      @Nullable CharSequence... arg1) {
    super.id(arg0, arg1);
    return this;
  }

  @Override
  public GroupPropMultipleSupportedAttributeDifferentNameModelViewModel_ id(@Nullable CharSequence arg0,
      long arg1) {
    super.id(arg0, arg1);
    return this;
  }

  @Override
  public GroupPropMultipleSupportedAttributeDifferentNameModelViewModel_ layout(@LayoutRes int arg0) {
    super.layout(arg0);
    return this;
  }

  @Override
  public GroupPropMultipleSupportedAttributeDifferentNameModelViewModel_ spanSizeOverride(@Nullable EpoxyModel.SpanSizeOverrideCallback arg0) {
    super.spanSizeOverride(arg0);
    return this;
  }

  @Override
  public GroupPropMultipleSupportedAttributeDifferentNameModelViewModel_ show() {
    super.show();
    return this;
  }

  @Override
  public GroupPropMultipleSupportedAttributeDifferentNameModelViewModel_ show(boolean show) {
    super.show(show);
    return this;
  }

  @Override
  public GroupPropMultipleSupportedAttributeDifferentNameModelViewModel_ hide() {
    super.hide();
    return this;
  }

  @Override
  @LayoutRes
  protected int getDefaultLayout() {
    return 1;
  }

  @Override
  public GroupPropMultipleSupportedAttributeDifferentNameModelViewModel_ reset() {
    onModelBoundListener_epoxyGeneratedModel = null;
    onModelUnboundListener_epoxyGeneratedModel = null;
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
    if (!(o instanceof GroupPropMultipleSupportedAttributeDifferentNameModelViewModel_)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    GroupPropMultipleSupportedAttributeDifferentNameModelViewModel_ that = (GroupPropMultipleSupportedAttributeDifferentNameModelViewModel_) o;
    if (((onModelBoundListener_epoxyGeneratedModel == null) != (that.onModelBoundListener_epoxyGeneratedModel == null))) {
      return false;
    }
    if (((onModelUnboundListener_epoxyGeneratedModel == null) != (that.onModelUnboundListener_epoxyGeneratedModel == null))) {
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
    int result = super.hashCode();
    result = 31 * result + (onModelBoundListener_epoxyGeneratedModel != null ? 1 : 0);
    result = 31 * result + (onModelUnboundListener_epoxyGeneratedModel != null ? 1 : 0);
    result = 31 * result + (titleString_String != null ? titleString_String.hashCode() : 0);
    result = 31 * result + titleInt_Int;
    return result;
  }

  @Override
  public String toString() {
    return "GroupPropMultipleSupportedAttributeDifferentNameModelViewModel_{" +
        "titleString_String=" + titleString_String +
        ", titleInt_Int=" + titleInt_Int +
        "}" + super.toString();
  }

  public static GroupPropMultipleSupportedAttributeDifferentNameModelViewModel_ from(ModelProperties properties) {
    GroupPropMultipleSupportedAttributeDifferentNameModelViewModel_ model = new GroupPropMultipleSupportedAttributeDifferentNameModelViewModel_();
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