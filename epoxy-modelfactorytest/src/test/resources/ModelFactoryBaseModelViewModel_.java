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
public class ModelFactoryBaseModelViewModel_ extends EpoxyModel<ModelFactoryBaseModelView> implements GeneratedModel<ModelFactoryBaseModelView>, ModelFactoryBaseModelViewModelBuilder {
  private final BitSet assignedAttributes_epoxyGeneratedModel = new BitSet(1);

  private OnModelBoundListener<ModelFactoryBaseModelViewModel_, ModelFactoryBaseModelView> onModelBoundListener_epoxyGeneratedModel;

  private OnModelUnboundListener<ModelFactoryBaseModelViewModel_, ModelFactoryBaseModelView> onModelUnboundListener_epoxyGeneratedModel;

  private OnModelVisibilityStateChangedListener<ModelFactoryBaseModelViewModel_, ModelFactoryBaseModelView> onModelVisibilityStateChangedListener_epoxyGeneratedModel;

  private OnModelVisibilityChangedListener<ModelFactoryBaseModelViewModel_, ModelFactoryBaseModelView> onModelVisibilityChangedListener_epoxyGeneratedModel;

  /**
   * Bitset index: 0
   */
  @NonNull
  private String title_String;

  @Override
  public void addTo(EpoxyController controller) {
    super.addTo(controller);
    addWithDebugValidation(controller);
    if (!assignedAttributes_epoxyGeneratedModel.get(0)) {
    	throw new IllegalStateException("A value is required for setTitle");
    }
  }

  @Override
  public void handlePreBind(final EpoxyViewHolder holder, final ModelFactoryBaseModelView object,
      final int position) {
    validateStateHasNotChangedSinceAdded("The model was changed between being added to the controller and being bound.", position);
  }

  @Override
  public void bind(final ModelFactoryBaseModelView object) {
    super.bind(object);
    object.setTitle(title_String);
  }

  @Override
  public void bind(final ModelFactoryBaseModelView object, EpoxyModel previousModel) {
    if (!(previousModel instanceof ModelFactoryBaseModelViewModel_)) {
      bind(object);
      return;
    }
    ModelFactoryBaseModelViewModel_ that = (ModelFactoryBaseModelViewModel_) previousModel;
    super.bind(object);

    if ((title_String != null ? !title_String.equals(that.title_String) : that.title_String != null)) {
      object.setTitle(title_String);
    }
  }

  @Override
  public void handlePostBind(final ModelFactoryBaseModelView object, int position) {
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
  public ModelFactoryBaseModelViewModel_ onBind(
      OnModelBoundListener<ModelFactoryBaseModelViewModel_, ModelFactoryBaseModelView> listener) {
    onMutation();
    this.onModelBoundListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public void unbind(ModelFactoryBaseModelView object) {
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
  public ModelFactoryBaseModelViewModel_ onUnbind(
      OnModelUnboundListener<ModelFactoryBaseModelViewModel_, ModelFactoryBaseModelView> listener) {
    onMutation();
    this.onModelUnboundListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public void onVisibilityStateChanged(int visibilityState,
      final ModelFactoryBaseModelView object) {
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
  public ModelFactoryBaseModelViewModel_ onVisibilityStateChanged(
      OnModelVisibilityStateChangedListener<ModelFactoryBaseModelViewModel_, ModelFactoryBaseModelView> listener) {
    onMutation();
    this.onModelVisibilityStateChangedListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public void onVisibilityChanged(float percentVisibleHeight, float percentVisibleWidth,
      int visibleHeight, int visibleWidth, final ModelFactoryBaseModelView object) {
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
  public ModelFactoryBaseModelViewModel_ onVisibilityChanged(
      OnModelVisibilityChangedListener<ModelFactoryBaseModelViewModel_, ModelFactoryBaseModelView> listener) {
    onMutation();
    this.onModelVisibilityChangedListener_epoxyGeneratedModel = listener;
    return this;
  }

  /**
   * <i>Required.</i>
   *
   * @see ModelFactoryBaseModelView#setTitle(String)
   */
  public ModelFactoryBaseModelViewModel_ title(@NonNull String title) {
    if (title == null) {
      throw new IllegalArgumentException("title cannot be null");
    }
    assignedAttributes_epoxyGeneratedModel.set(0);
    onMutation();
    this.title_String = title;
    return this;
  }

  @NonNull
  public String title() {
    return title_String;
  }

  @Override
  public ModelFactoryBaseModelViewModel_ id(long id) {
    super.id(id);
    return this;
  }

  @Override
  public ModelFactoryBaseModelViewModel_ id(@Nullable Number... ids) {
    super.id(ids);
    return this;
  }

  @Override
  public ModelFactoryBaseModelViewModel_ id(long id1, long id2) {
    super.id(id1, id2);
    return this;
  }

  @Override
  public ModelFactoryBaseModelViewModel_ id(@Nullable CharSequence key) {
    super.id(key);
    return this;
  }

  @Override
  public ModelFactoryBaseModelViewModel_ id(@Nullable CharSequence key,
      @Nullable CharSequence... otherKeys) {
    super.id(key, otherKeys);
    return this;
  }

  @Override
  public ModelFactoryBaseModelViewModel_ id(@Nullable CharSequence key, long id) {
    super.id(key, id);
    return this;
  }

  @Override
  public ModelFactoryBaseModelViewModel_ layout(@LayoutRes int layoutRes) {
    super.layout(layoutRes);
    return this;
  }

  @Override
  public ModelFactoryBaseModelViewModel_ spanSizeOverride(
      @Nullable EpoxyModel.SpanSizeOverrideCallback spanSizeCallback) {
    super.spanSizeOverride(spanSizeCallback);
    return this;
  }

  @Override
  public ModelFactoryBaseModelViewModel_ show() {
    super.show();
    return this;
  }

  @Override
  public ModelFactoryBaseModelViewModel_ show(boolean show) {
    super.show(show);
    return this;
  }

  @Override
  public ModelFactoryBaseModelViewModel_ hide() {
    super.hide();
    return this;
  }

  @Override
  @LayoutRes
  protected int getDefaultLayout() {
    return 1;
  }

  @Override
  public ModelFactoryBaseModelViewModel_ reset() {
    onModelBoundListener_epoxyGeneratedModel = null;
    onModelUnboundListener_epoxyGeneratedModel = null;
    onModelVisibilityStateChangedListener_epoxyGeneratedModel = null;
    onModelVisibilityChangedListener_epoxyGeneratedModel = null;
    assignedAttributes_epoxyGeneratedModel.clear();
    this.title_String = null;
    super.reset();
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof ModelFactoryBaseModelViewModel_)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    ModelFactoryBaseModelViewModel_ that = (ModelFactoryBaseModelViewModel_) o;
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
    return _result;
  }

  @Override
  public String toString() {
    return "ModelFactoryBaseModelViewModel_{" +
        "title_String=" + title_String +
        "}" + super.toString();
  }

  public static ModelFactoryBaseModelViewModel_ from(ModelProperties properties) {
    ModelFactoryBaseModelViewModel_ model = new ModelFactoryBaseModelViewModel_();
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
