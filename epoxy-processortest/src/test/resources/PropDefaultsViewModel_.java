package com.airbnb.epoxy;

import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
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
public class PropDefaultsViewModel_ extends EpoxyModel<PropDefaultsView> implements GeneratedModel<PropDefaultsView> {
  private final BitSet assignedAttributes_epoxyGeneratedModel = new BitSet(6);

  private OnModelBoundListener<PropDefaultsViewModel_, PropDefaultsView> onModelBoundListener_epoxyGeneratedModel;

  private OnModelUnboundListener<PropDefaultsViewModel_, PropDefaultsView> onModelUnboundListener_epoxyGeneratedModel;

  /**
   * Bitset index: 0 */
  @Nullable
  private CharSequence defaultsToNull_CharSequence = null;

  /**
   * Bitset index: 1 */
  private CharSequence noDefaultSoItIsRequired_CharSequence;

  /**
   * Bitset index: 2 */
  private int primitivesHaveImplicitDefaultsAndCannotBeRequired_Int = 0;

  /**
   * Bitset index: 3 */
  private int primitiveWithExplicitDefault_Int = PropDefaultsView.PRIMITIVE_DEFAULT;

  /**
   * Bitset index: 4 */
  private String objectWithDefault_String = PropDefaultsView.STRING_DEFAULT;

  /**
   * Bitset index: 5 */
  @Nullable
  private String objectWithDefaultAndNullable_String = PropDefaultsView.STRING_DEFAULT;

  @Override
  public void addTo(EpoxyController controller) {
    super.addTo(controller);
    addWithDebugValidation(controller);
    if (!assignedAttributes_epoxyGeneratedModel.get(1)) {
    	throw new IllegalStateException("A value is required for noDefaultSoItIsRequired");
    }
  }

  @Override
  public void handlePreBind(final EpoxyViewHolder holder, final PropDefaultsView object,
      int position) {
    validateStateHasNotChangedSinceAdded("The model was changed between being added to the controller and being bound.", position);
  }

  @Override
  public void bind(final PropDefaultsView object) {
    super.bind(object);
    object.primitiveWithExplicitDefault(primitiveWithExplicitDefault_Int);
    object.defaultsToNull(defaultsToNull_CharSequence);
    object.noDefaultSoItIsRequired(noDefaultSoItIsRequired_CharSequence);
    object.objectWithDefaultAndNullable(objectWithDefaultAndNullable_String);
    object.objectWithDefault(objectWithDefault_String);
    object.primitivesHaveImplicitDefaultsAndCannotBeRequired(primitivesHaveImplicitDefaultsAndCannotBeRequired_Int);
  }

  @Override
  public void bind(final PropDefaultsView object, EpoxyModel previousModel) {
    if (!(previousModel instanceof PropDefaultsViewModel_)) {
      bind(object);
      return;
    }
    PropDefaultsViewModel_ that = (PropDefaultsViewModel_) previousModel;
    super.bind(object);

    if (primitiveWithExplicitDefault_Int != that.primitiveWithExplicitDefault_Int) {
      object.primitiveWithExplicitDefault(primitiveWithExplicitDefault_Int);
    }

    if (defaultsToNull_CharSequence != null ? !defaultsToNull_CharSequence.equals(that.defaultsToNull_CharSequence) : that.defaultsToNull_CharSequence != null) {
      object.defaultsToNull(defaultsToNull_CharSequence);
    }

    if (noDefaultSoItIsRequired_CharSequence != null ? !noDefaultSoItIsRequired_CharSequence.equals(that.noDefaultSoItIsRequired_CharSequence) : that.noDefaultSoItIsRequired_CharSequence != null) {
      object.noDefaultSoItIsRequired(noDefaultSoItIsRequired_CharSequence);
    }

    if (objectWithDefaultAndNullable_String != null ? !objectWithDefaultAndNullable_String.equals(that.objectWithDefaultAndNullable_String) : that.objectWithDefaultAndNullable_String != null) {
      object.objectWithDefaultAndNullable(objectWithDefaultAndNullable_String);
    }

    if (objectWithDefault_String != null ? !objectWithDefault_String.equals(that.objectWithDefault_String) : that.objectWithDefault_String != null) {
      object.objectWithDefault(objectWithDefault_String);
    }

    if (primitivesHaveImplicitDefaultsAndCannotBeRequired_Int != that.primitivesHaveImplicitDefaultsAndCannotBeRequired_Int) {
      object.primitivesHaveImplicitDefaultsAndCannotBeRequired(primitivesHaveImplicitDefaultsAndCannotBeRequired_Int);
    }
  }

  @Override
  public void handlePostBind(final PropDefaultsView object, int position) {
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
  public PropDefaultsViewModel_ onBind(OnModelBoundListener<PropDefaultsViewModel_, PropDefaultsView> listener) {
    onMutation();
    this.onModelBoundListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public void unbind(PropDefaultsView object) {
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
  public PropDefaultsViewModel_ onUnbind(OnModelUnboundListener<PropDefaultsViewModel_, PropDefaultsView> listener) {
    onMutation();
    this.onModelUnboundListener_epoxyGeneratedModel = listener;
    return this;
  }

  /**
   * <i>Optional</i>: Default value is null
   *
   * @see PropDefaultsView#defaultsToNull(CharSequence)
   */
  public PropDefaultsViewModel_ defaultsToNull(@Nullable CharSequence defaultsToNull) {
    assignedAttributes_epoxyGeneratedModel.set(0);
    onMutation();
    this.defaultsToNull_CharSequence = defaultsToNull;
    return this;
  }

  @Nullable
  public CharSequence defaultsToNull() {
    return defaultsToNull_CharSequence;
  }

  /**
   * <i>Required.</i>
   *
   * @see PropDefaultsView#noDefaultSoItIsRequired(CharSequence)
   */
  public PropDefaultsViewModel_ noDefaultSoItIsRequired(CharSequence noDefaultSoItIsRequired) {
    if (noDefaultSoItIsRequired == null) {
      throw new IllegalArgumentException("noDefaultSoItIsRequired cannot be null");
    }
    assignedAttributes_epoxyGeneratedModel.set(1);
    onMutation();
    this.noDefaultSoItIsRequired_CharSequence = noDefaultSoItIsRequired;
    return this;
  }

  public CharSequence noDefaultSoItIsRequired() {
    return noDefaultSoItIsRequired_CharSequence;
  }

  /**
   * <i>Optional</i>: Default value is 0
   *
   * @see PropDefaultsView#primitivesHaveImplicitDefaultsAndCannotBeRequired(int)
   */
  public PropDefaultsViewModel_ primitivesHaveImplicitDefaultsAndCannotBeRequired(int primitivesHaveImplicitDefaultsAndCannotBeRequired) {
    assignedAttributes_epoxyGeneratedModel.set(2);
    onMutation();
    this.primitivesHaveImplicitDefaultsAndCannotBeRequired_Int = primitivesHaveImplicitDefaultsAndCannotBeRequired;
    return this;
  }

  public int primitivesHaveImplicitDefaultsAndCannotBeRequired() {
    return primitivesHaveImplicitDefaultsAndCannotBeRequired_Int;
  }

  /**
   * <i>Optional</i>: Default value is <b>{@value PropDefaultsView#PRIMITIVE_DEFAULT}</b>
   *
   * @see PropDefaultsView#primitiveWithExplicitDefault(int)
   */
  public PropDefaultsViewModel_ primitiveWithExplicitDefault(int primitiveWithExplicitDefault) {
    assignedAttributes_epoxyGeneratedModel.set(3);
    onMutation();
    this.primitiveWithExplicitDefault_Int = primitiveWithExplicitDefault;
    return this;
  }

  public int primitiveWithExplicitDefault() {
    return primitiveWithExplicitDefault_Int;
  }

  /**
   * <i>Optional</i>: Default value is <b>{@value PropDefaultsView#STRING_DEFAULT}</b>
   *
   * @see PropDefaultsView#objectWithDefault(String)
   */
  public PropDefaultsViewModel_ objectWithDefault(String objectWithDefault) {
    if (objectWithDefault == null) {
      throw new IllegalArgumentException("objectWithDefault cannot be null");
    }
    assignedAttributes_epoxyGeneratedModel.set(4);
    onMutation();
    this.objectWithDefault_String = objectWithDefault;
    return this;
  }

  public String objectWithDefault() {
    return objectWithDefault_String;
  }

  /**
   * <i>Optional</i>: Default value is <b>{@value PropDefaultsView#STRING_DEFAULT}</b>
   *
   * @see PropDefaultsView#objectWithDefaultAndNullable(String)
   */
  public PropDefaultsViewModel_ objectWithDefaultAndNullable(@Nullable String objectWithDefaultAndNullable) {
    assignedAttributes_epoxyGeneratedModel.set(5);
    onMutation();
    this.objectWithDefaultAndNullable_String = objectWithDefaultAndNullable;
    return this;
  }

  @Nullable
  public String objectWithDefaultAndNullable() {
    return objectWithDefaultAndNullable_String;
  }

  @Override
  public PropDefaultsViewModel_ id(long id) {
    super.id(id);
    return this;
  }

  @Override
  public PropDefaultsViewModel_ id(Number... ids) {
    super.id(ids);
    return this;
  }

  @Override
  public PropDefaultsViewModel_ id(long id1, long id2) {
    super.id(id1, id2);
    return this;
  }

  @Override
  public PropDefaultsViewModel_ id(CharSequence key) {
    super.id(key);
    return this;
  }

  @Override
  public PropDefaultsViewModel_ id(CharSequence key, CharSequence... otherKeys) {
    super.id(key, otherKeys);
    return this;
  }

  @Override
  public PropDefaultsViewModel_ id(CharSequence key, long id) {
    super.id(key, id);
    return this;
  }

  @Override
  public PropDefaultsViewModel_ layout(@LayoutRes int arg0) {
    super.layout(arg0);
    return this;
  }

  @Override
  public PropDefaultsViewModel_ spanSizeOverride(@Nullable EpoxyModel.SpanSizeOverrideCallback arg0) {
    super.spanSizeOverride(arg0);
    return this;
  }

  @Override
  public PropDefaultsViewModel_ show() {
    super.show();
    return this;
  }

  @Override
  public PropDefaultsViewModel_ show(boolean show) {
    super.show(show);
    return this;
  }

  @Override
  public PropDefaultsViewModel_ hide() {
    super.hide();
    return this;
  }

  @Override
  @LayoutRes
  protected int getDefaultLayout() {
    return 1;
  }

  @Override
  public PropDefaultsViewModel_ reset() {
    onModelBoundListener_epoxyGeneratedModel = null;
    onModelUnboundListener_epoxyGeneratedModel = null;
    assignedAttributes_epoxyGeneratedModel.clear();
    this.defaultsToNull_CharSequence = null;
    this.noDefaultSoItIsRequired_CharSequence = null;
    this.primitivesHaveImplicitDefaultsAndCannotBeRequired_Int = 0;
    this.primitiveWithExplicitDefault_Int = PropDefaultsView.PRIMITIVE_DEFAULT;
    this.objectWithDefault_String = PropDefaultsView.STRING_DEFAULT;
    this.objectWithDefaultAndNullable_String = PropDefaultsView.STRING_DEFAULT;
    super.reset();
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof PropDefaultsViewModel_)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    PropDefaultsViewModel_ that = (PropDefaultsViewModel_) o;
    if ((onModelBoundListener_epoxyGeneratedModel == null) != (that.onModelBoundListener_epoxyGeneratedModel == null)) {
      return false;
    }
    if ((onModelUnboundListener_epoxyGeneratedModel == null) != (that.onModelUnboundListener_epoxyGeneratedModel == null)) {
      return false;
    }
    if (defaultsToNull_CharSequence != null ? !defaultsToNull_CharSequence.equals(that.defaultsToNull_CharSequence) : that.defaultsToNull_CharSequence != null) {
      return false;
    }
    if (noDefaultSoItIsRequired_CharSequence != null ? !noDefaultSoItIsRequired_CharSequence.equals(that.noDefaultSoItIsRequired_CharSequence) : that.noDefaultSoItIsRequired_CharSequence != null) {
      return false;
    }
    if (primitivesHaveImplicitDefaultsAndCannotBeRequired_Int != that.primitivesHaveImplicitDefaultsAndCannotBeRequired_Int) {
      return false;
    }
    if (primitiveWithExplicitDefault_Int != that.primitiveWithExplicitDefault_Int) {
      return false;
    }
    if (objectWithDefault_String != null ? !objectWithDefault_String.equals(that.objectWithDefault_String) : that.objectWithDefault_String != null) {
      return false;
    }
    if (objectWithDefaultAndNullable_String != null ? !objectWithDefaultAndNullable_String.equals(that.objectWithDefaultAndNullable_String) : that.objectWithDefaultAndNullable_String != null) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + (onModelBoundListener_epoxyGeneratedModel != null ? 1 : 0);
    result = 31 * result + (onModelUnboundListener_epoxyGeneratedModel != null ? 1 : 0);
    result = 31 * result + (defaultsToNull_CharSequence != null ? defaultsToNull_CharSequence.hashCode() : 0);
    result = 31 * result + (noDefaultSoItIsRequired_CharSequence != null ? noDefaultSoItIsRequired_CharSequence.hashCode() : 0);
    result = 31 * result + primitivesHaveImplicitDefaultsAndCannotBeRequired_Int;
    result = 31 * result + primitiveWithExplicitDefault_Int;
    result = 31 * result + (objectWithDefault_String != null ? objectWithDefault_String.hashCode() : 0);
    result = 31 * result + (objectWithDefaultAndNullable_String != null ? objectWithDefaultAndNullable_String.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "PropDefaultsViewModel_{" +
        "defaultsToNull_CharSequence=" + defaultsToNull_CharSequence +
        ", noDefaultSoItIsRequired_CharSequence=" + noDefaultSoItIsRequired_CharSequence +
        ", primitivesHaveImplicitDefaultsAndCannotBeRequired_Int=" + primitivesHaveImplicitDefaultsAndCannotBeRequired_Int +
        ", primitiveWithExplicitDefault_Int=" + primitiveWithExplicitDefault_Int +
        ", objectWithDefault_String=" + objectWithDefault_String +
        ", objectWithDefaultAndNullable_String=" + objectWithDefaultAndNullable_String +
        "}" + super.toString();
  }

  @Override
  public int getSpanSize(int totalSpanCount, int position, int itemCount) {
    return totalSpanCount;
  }
}