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
public class GroupPropMultipleSupportedAttributeModelViewModel_ extends EpoxyModel<GroupPropMultipleSupportedAttributeModelView> implements GeneratedModel<GroupPropMultipleSupportedAttributeModelView>, GroupPropMultipleSupportedAttributeModelViewModelBuilder {
  private final BitSet assignedAttributes_epoxyGeneratedModel = new BitSet(2);

  private OnModelBoundListener<GroupPropMultipleSupportedAttributeModelViewModel_, GroupPropMultipleSupportedAttributeModelView> onModelBoundListener_epoxyGeneratedModel;

  private OnModelUnboundListener<GroupPropMultipleSupportedAttributeModelViewModel_, GroupPropMultipleSupportedAttributeModelView> onModelUnboundListener_epoxyGeneratedModel;

  /**
   * Bitset index: 0 */
  @NonNull
  private String title_String;

  /**
   * Bitset index: 1 */
  private int title_Int = 0;

  @Override
  public void addTo(EpoxyController controller) {
    super.addTo(controller);
    addWithDebugValidation(controller);
  }

  @Override
  public void handlePreBind(final EpoxyViewHolder holder,
      final GroupPropMultipleSupportedAttributeModelView object, final int position) {
    validateStateHasNotChangedSinceAdded("The model was changed between being added to the controller and being bound.", position);
  }

  @Override
  public void bind(final GroupPropMultipleSupportedAttributeModelView object) {
    super.bind(object);
    if (assignedAttributes_epoxyGeneratedModel.get(0)) {
      object.setTitle(title_String);
    }
    else if (assignedAttributes_epoxyGeneratedModel.get(1)) {
      object.setTitle(title_Int);
    }
    else {
      object.setTitle(title_Int);
    }
  }

  @Override
  public void bind(final GroupPropMultipleSupportedAttributeModelView object,
      EpoxyModel previousModel) {
    if (!(previousModel instanceof GroupPropMultipleSupportedAttributeModelViewModel_)) {
      bind(object);
      return;
    }
    GroupPropMultipleSupportedAttributeModelViewModel_ that = (GroupPropMultipleSupportedAttributeModelViewModel_) previousModel;
    super.bind(object);

    if (assignedAttributes_epoxyGeneratedModel.get(0)) {
      if (!that.assignedAttributes_epoxyGeneratedModel.get(0) || (title_String != null ? !title_String.equals(that.title_String) : that.title_String != null)) {
        object.setTitle(title_String);
      }
    }
    else if (assignedAttributes_epoxyGeneratedModel.get(1)) {
      if ((title_Int != that.title_Int)) {
        object.setTitle(title_Int);
      }
    }
    // A value was not set so we should use the default value, but we only need to set it if the previous model had a custom value set.
    else if (that.assignedAttributes_epoxyGeneratedModel.get(0) || that.assignedAttributes_epoxyGeneratedModel.get(1)) {
      object.setTitle(title_Int);
    }
  }

  @Override
  public void handlePostBind(final GroupPropMultipleSupportedAttributeModelView object,
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
  public GroupPropMultipleSupportedAttributeModelViewModel_ onBind(OnModelBoundListener<GroupPropMultipleSupportedAttributeModelViewModel_, GroupPropMultipleSupportedAttributeModelView> listener) {
    onMutation();
    this.onModelBoundListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public void unbind(GroupPropMultipleSupportedAttributeModelView object) {
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
  public GroupPropMultipleSupportedAttributeModelViewModel_ onUnbind(OnModelUnboundListener<GroupPropMultipleSupportedAttributeModelViewModel_, GroupPropMultipleSupportedAttributeModelView> listener) {
    onMutation();
    this.onModelUnboundListener_epoxyGeneratedModel = listener;
    return this;
  }

  /**
   * <i>Required.</i>
   *
   * @see GroupPropMultipleSupportedAttributeModelView#setTitle(String)
   */
  public GroupPropMultipleSupportedAttributeModelViewModel_ title(@NonNull String title) {
    if (title == null) {
      throw new IllegalArgumentException("title cannot be null");
    }
    assignedAttributes_epoxyGeneratedModel.set(0);
    assignedAttributes_epoxyGeneratedModel.clear(1);
    this.title_Int = 0;
    onMutation();
    this.title_String = title;
    return this;
  }

  @NonNull
  public String titleString() {
    return title_String;
  }

  /**
   * <i>Optional</i>: Default value is 0
   *
   * @see GroupPropMultipleSupportedAttributeModelView#setTitle(int)
   */
  public GroupPropMultipleSupportedAttributeModelViewModel_ title(int title) {
    assignedAttributes_epoxyGeneratedModel.set(1);
    assignedAttributes_epoxyGeneratedModel.clear(0);
    this.title_String = null;
    onMutation();
    this.title_Int = title;
    return this;
  }

  public int titleInt() {
    return title_Int;
  }

  @Override
  public GroupPropMultipleSupportedAttributeModelViewModel_ id(long id) {
    super.id(id);
    return this;
  }

  @Override
  public GroupPropMultipleSupportedAttributeModelViewModel_ id(@Nullable Number... arg0) {
    super.id(arg0);
    return this;
  }

  @Override
  public GroupPropMultipleSupportedAttributeModelViewModel_ id(long id1, long id2) {
    super.id(id1, id2);
    return this;
  }

  @Override
  public GroupPropMultipleSupportedAttributeModelViewModel_ id(@Nullable CharSequence arg0) {
    super.id(arg0);
    return this;
  }

  @Override
  public GroupPropMultipleSupportedAttributeModelViewModel_ id(@Nullable CharSequence arg0,
      @Nullable CharSequence... arg1) {
    super.id(arg0, arg1);
    return this;
  }

  @Override
  public GroupPropMultipleSupportedAttributeModelViewModel_ id(@Nullable CharSequence arg0,
      long arg1) {
    super.id(arg0, arg1);
    return this;
  }

  @Override
  public GroupPropMultipleSupportedAttributeModelViewModel_ layout(@LayoutRes int arg0) {
    super.layout(arg0);
    return this;
  }

  @Override
  public GroupPropMultipleSupportedAttributeModelViewModel_ spanSizeOverride(@Nullable EpoxyModel.SpanSizeOverrideCallback arg0) {
    super.spanSizeOverride(arg0);
    return this;
  }

  @Override
  public GroupPropMultipleSupportedAttributeModelViewModel_ show() {
    super.show();
    return this;
  }

  @Override
  public GroupPropMultipleSupportedAttributeModelViewModel_ show(boolean show) {
    super.show(show);
    return this;
  }

  @Override
  public GroupPropMultipleSupportedAttributeModelViewModel_ hide() {
    super.hide();
    return this;
  }

  @Override
  @LayoutRes
  protected int getDefaultLayout() {
    return 1;
  }

  @Override
  public GroupPropMultipleSupportedAttributeModelViewModel_ reset() {
    onModelBoundListener_epoxyGeneratedModel = null;
    onModelUnboundListener_epoxyGeneratedModel = null;
    assignedAttributes_epoxyGeneratedModel.clear();
    this.title_String = null;
    this.title_Int = 0;
    super.reset();
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof GroupPropMultipleSupportedAttributeModelViewModel_)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    GroupPropMultipleSupportedAttributeModelViewModel_ that = (GroupPropMultipleSupportedAttributeModelViewModel_) o;
    if (((onModelBoundListener_epoxyGeneratedModel == null) != (that.onModelBoundListener_epoxyGeneratedModel == null))) {
      return false;
    }
    if (((onModelUnboundListener_epoxyGeneratedModel == null) != (that.onModelUnboundListener_epoxyGeneratedModel == null))) {
      return false;
    }
    if ((title_String != null ? !title_String.equals(that.title_String) : that.title_String != null)) {
      return false;
    }
    if ((title_Int != that.title_Int)) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + (onModelBoundListener_epoxyGeneratedModel != null ? 1 : 0);
    result = 31 * result + (onModelUnboundListener_epoxyGeneratedModel != null ? 1 : 0);
    result = 31 * result + (title_String != null ? title_String.hashCode() : 0);
    result = 31 * result + title_Int;
    return result;
  }

  @Override
  public String toString() {
    return "GroupPropMultipleSupportedAttributeModelViewModel_{" +
        "title_String=" + title_String +
        ", title_Int=" + title_Int +
        "}" + super.toString();
  }

  @Override
  public int getSpanSize(int totalSpanCount, int position, int itemCount) {
    return totalSpanCount;
  }
}