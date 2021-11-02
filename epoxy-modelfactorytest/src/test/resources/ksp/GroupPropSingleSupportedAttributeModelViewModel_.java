package com.airbnb.epoxy;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
public class GroupPropSingleSupportedAttributeModelViewModel_ extends EpoxyModel<GroupPropSingleSupportedAttributeModelView> implements GeneratedModel<GroupPropSingleSupportedAttributeModelView>, GroupPropSingleSupportedAttributeModelViewModelBuilder {
  private final BitSet assignedAttributes_epoxyGeneratedModel = new BitSet(2);

  private OnModelBoundListener<GroupPropSingleSupportedAttributeModelViewModel_, GroupPropSingleSupportedAttributeModelView> onModelBoundListener_epoxyGeneratedModel;

  private OnModelUnboundListener<GroupPropSingleSupportedAttributeModelViewModel_, GroupPropSingleSupportedAttributeModelView> onModelUnboundListener_epoxyGeneratedModel;

  private OnModelVisibilityStateChangedListener<GroupPropSingleSupportedAttributeModelViewModel_, GroupPropSingleSupportedAttributeModelView> onModelVisibilityStateChangedListener_epoxyGeneratedModel;

  private OnModelVisibilityChangedListener<GroupPropSingleSupportedAttributeModelViewModel_, GroupPropSingleSupportedAttributeModelView> onModelVisibilityChangedListener_epoxyGeneratedModel;

  /**
   * Bitset index: 0
   */
  @NonNull
  private String title_String;

  /**
   * Bitset index: 1
   */
  @NonNull
  private Object title_Object;

  @Override
  public void addTo(EpoxyController controller) {
    super.addTo(controller);
    addWithDebugValidation(controller);
    if (!assignedAttributes_epoxyGeneratedModel.get(0) && !assignedAttributes_epoxyGeneratedModel.get(1)) {
    	throw new IllegalStateException("A value is required for setTitle");
    }
  }

  @Override
  public void handlePreBind(final EpoxyViewHolder holder,
      final GroupPropSingleSupportedAttributeModelView object, final int position) {
    validateStateHasNotChangedSinceAdded("The model was changed between being added to the controller and being bound.", position);
  }

  @Override
  public void bind(final GroupPropSingleSupportedAttributeModelView object) {
    super.bind(object);
    if (assignedAttributes_epoxyGeneratedModel.get(0)) {
      object.setTitle(title_String);
    }
    else {
      object.setTitle(title_Object);
    }
  }

  @Override
  public void bind(final GroupPropSingleSupportedAttributeModelView object,
      EpoxyModel previousModel) {
    if (!(previousModel instanceof GroupPropSingleSupportedAttributeModelViewModel_)) {
      bind(object);
      return;
    }
    GroupPropSingleSupportedAttributeModelViewModel_ that = (GroupPropSingleSupportedAttributeModelViewModel_) previousModel;
    super.bind(object);

    if (assignedAttributes_epoxyGeneratedModel.get(0)) {
      if (!that.assignedAttributes_epoxyGeneratedModel.get(0) || (title_String != null ? !title_String.equals(that.title_String) : that.title_String != null)) {
        object.setTitle(title_String);
      }
    }
    else if (assignedAttributes_epoxyGeneratedModel.get(1)) {
      if (!that.assignedAttributes_epoxyGeneratedModel.get(1) || (title_Object != null ? !title_Object.equals(that.title_Object) : that.title_Object != null)) {
        object.setTitle(title_Object);
      }
    }
  }

  @Override
  public void handlePostBind(final GroupPropSingleSupportedAttributeModelView object,
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
  public GroupPropSingleSupportedAttributeModelViewModel_ onBind(
      OnModelBoundListener<GroupPropSingleSupportedAttributeModelViewModel_, GroupPropSingleSupportedAttributeModelView> listener) {
    onMutation();
    this.onModelBoundListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public void unbind(GroupPropSingleSupportedAttributeModelView object) {
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
  public GroupPropSingleSupportedAttributeModelViewModel_ onUnbind(
      OnModelUnboundListener<GroupPropSingleSupportedAttributeModelViewModel_, GroupPropSingleSupportedAttributeModelView> listener) {
    onMutation();
    this.onModelUnboundListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public void onVisibilityStateChanged(int visibilityState,
      final GroupPropSingleSupportedAttributeModelView object) {
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
  public GroupPropSingleSupportedAttributeModelViewModel_ onVisibilityStateChanged(
      OnModelVisibilityStateChangedListener<GroupPropSingleSupportedAttributeModelViewModel_, GroupPropSingleSupportedAttributeModelView> listener) {
    onMutation();
    this.onModelVisibilityStateChangedListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public void onVisibilityChanged(float percentVisibleHeight, float percentVisibleWidth,
      int visibleHeight, int visibleWidth,
      final GroupPropSingleSupportedAttributeModelView object) {
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
  public GroupPropSingleSupportedAttributeModelViewModel_ onVisibilityChanged(
      OnModelVisibilityChangedListener<GroupPropSingleSupportedAttributeModelViewModel_, GroupPropSingleSupportedAttributeModelView> listener) {
    onMutation();
    this.onModelVisibilityChangedListener_epoxyGeneratedModel = listener;
    return this;
  }

  /**
   * <i>Required.</i>
   *
   * @see GroupPropSingleSupportedAttributeModelView#setTitle(String)
   */
  public GroupPropSingleSupportedAttributeModelViewModel_ title(@NonNull String title) {
    if (title == null) {
      throw new IllegalArgumentException("title cannot be null");
    }
    assignedAttributes_epoxyGeneratedModel.set(0);
    assignedAttributes_epoxyGeneratedModel.clear(1);
    this.title_Object = null;
    onMutation();
    this.title_String = title;
    return this;
  }

  @NonNull
  public String titleString() {
    return title_String;
  }

  /**
   * <i>Required.</i>
   *
   * @see GroupPropSingleSupportedAttributeModelView#setTitle(Object)
   */
  public GroupPropSingleSupportedAttributeModelViewModel_ title(@NonNull Object title) {
    if (title == null) {
      throw new IllegalArgumentException("title cannot be null");
    }
    assignedAttributes_epoxyGeneratedModel.set(1);
    assignedAttributes_epoxyGeneratedModel.clear(0);
    this.title_String = null;
    onMutation();
    this.title_Object = title;
    return this;
  }

  @NonNull
  public Object titleObject() {
    return title_Object;
  }

  @Override
  public GroupPropSingleSupportedAttributeModelViewModel_ id(long p0) {
    super.id(p0);
    return this;
  }

  @Override
  public GroupPropSingleSupportedAttributeModelViewModel_ id(@Nullable Number... p0) {
    super.id(p0);
    return this;
  }

  @Override
  public GroupPropSingleSupportedAttributeModelViewModel_ id(long p0, long p1) {
    super.id(p0, p1);
    return this;
  }

  @Override
  public GroupPropSingleSupportedAttributeModelViewModel_ id(@Nullable CharSequence p0) {
    super.id(p0);
    return this;
  }

  @Override
  public GroupPropSingleSupportedAttributeModelViewModel_ id(@Nullable CharSequence p0,
      @Nullable CharSequence... p1) {
    super.id(p0, p1);
    return this;
  }

  @Override
  public GroupPropSingleSupportedAttributeModelViewModel_ id(@Nullable CharSequence p0, long p1) {
    super.id(p0, p1);
    return this;
  }

  @Override
  public GroupPropSingleSupportedAttributeModelViewModel_ layout(@LayoutRes int p0) {
    super.layout(p0);
    return this;
  }

  @Override
  public GroupPropSingleSupportedAttributeModelViewModel_ spanSizeOverride(
      @Nullable EpoxyModel.SpanSizeOverrideCallback p0) {
    super.spanSizeOverride(p0);
    return this;
  }

  @Override
  public GroupPropSingleSupportedAttributeModelViewModel_ show() {
    super.show();
    return this;
  }

  @Override
  public GroupPropSingleSupportedAttributeModelViewModel_ show(boolean p0) {
    super.show(p0);
    return this;
  }

  @Override
  public GroupPropSingleSupportedAttributeModelViewModel_ hide() {
    super.hide();
    return this;
  }

  @Override
  @LayoutRes
  protected int getDefaultLayout() {
    return 1;
  }

  @Override
  public GroupPropSingleSupportedAttributeModelViewModel_ reset() {
    onModelBoundListener_epoxyGeneratedModel = null;
    onModelUnboundListener_epoxyGeneratedModel = null;
    onModelVisibilityStateChangedListener_epoxyGeneratedModel = null;
    onModelVisibilityChangedListener_epoxyGeneratedModel = null;
    assignedAttributes_epoxyGeneratedModel.clear();
    this.title_String = null;
    this.title_Object = null;
    super.reset();
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof GroupPropSingleSupportedAttributeModelViewModel_)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    GroupPropSingleSupportedAttributeModelViewModel_ that = (GroupPropSingleSupportedAttributeModelViewModel_) o;
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
    if ((title_String != null ? !title_String.equals(that.title_String) : that.title_String != null)) {
      return false;
    }
    if ((title_Object != null ? !title_Object.equals(that.title_Object) : that.title_Object != null)) {
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
    _result = 31 * _result + (title_String != null ? title_String.hashCode() : 0);
    _result = 31 * _result + (title_Object != null ? title_Object.hashCode() : 0);
    return _result;
  }

  @Override
  public String toString() {
    return "GroupPropSingleSupportedAttributeModelViewModel_{" +
        "title_String=" + title_String +
        ", title_Object=" + title_Object +
        "}" + super.toString();
  }

  public static GroupPropSingleSupportedAttributeModelViewModel_ from(ModelProperties properties) {
    GroupPropSingleSupportedAttributeModelViewModel_ model = new GroupPropSingleSupportedAttributeModelViewModel_();
    model.id(properties.getId());
    if (properties.has("title")) {
      model.title(properties.getString("title"));
    }
    return model;
  }

  @Override
  public int getSpanSize(int totalSpanCount, int position, int itemCount) {
    return totalSpanCount;
  }
}
