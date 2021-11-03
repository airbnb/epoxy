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
public class PropGroupsViewModel_ extends EpoxyModel<PropGroupsView> implements GeneratedModel<PropGroupsView>, PropGroupsViewModelBuilder {
  private final BitSet assignedAttributes_epoxyGeneratedModel = new BitSet(14);

  private OnModelBoundListener<PropGroupsViewModel_, PropGroupsView> onModelBoundListener_epoxyGeneratedModel;

  private OnModelUnboundListener<PropGroupsViewModel_, PropGroupsView> onModelUnboundListener_epoxyGeneratedModel;

  private OnModelVisibilityStateChangedListener<PropGroupsViewModel_, PropGroupsView> onModelVisibilityStateChangedListener_epoxyGeneratedModel;

  private OnModelVisibilityChangedListener<PropGroupsViewModel_, PropGroupsView> onModelVisibilityChangedListener_epoxyGeneratedModel;

  /**
   * Bitset index: 0
   */
  @Nullable
  private CharSequence something_CharSequence = (CharSequence) null;

  /**
   * Bitset index: 1
   */
  private int something_Int = 0;

  /**
   * Bitset index: 2
   */
  @NonNull
  private CharSequence somethingElse_CharSequence;

  /**
   * Bitset index: 3
   */
  private int somethingElse_Int = 0;

  /**
   * Bitset index: 4
   */
  private int primitive_Int = 0;

  /**
   * Bitset index: 5
   */
  private long primitive_Long = 0L;

  /**
   * Bitset index: 6
   */
  private int primitiveWithDefault_Int = 0;

  /**
   * Bitset index: 7
   */
  private long primitiveWithDefault_Long = PropGroupsView.DEFAULT_PRIMITIVE;

  /**
   * Bitset index: 8
   */
  private long primitiveAndObjectGroupWithPrimitiveDefault_Long = PropGroupsView.DEFAULT_PRIMITIVE;

  /**
   * Bitset index: 9
   */
  @NonNull
  private CharSequence primitiveAndObjectGroupWithPrimitiveDefault_CharSequence;

  /**
   * Bitset index: 10
   */
  private long oneThing_Long = 0L;

  /**
   * Bitset index: 11
   */
  @NonNull
  private CharSequence anotherThing_CharSequence;

  /**
   * Bitset index: 12
   */
  @NonNull
  private String requiredGroup_String;

  /**
   * Bitset index: 13
   */
  @NonNull
  private CharSequence requiredGroup_CharSequence;

  @Override
  public void addTo(EpoxyController controller) {
    super.addTo(controller);
    addWithDebugValidation(controller);
    if (!assignedAttributes_epoxyGeneratedModel.get(12) && !assignedAttributes_epoxyGeneratedModel.get(13)) {
    	throw new IllegalStateException("A value is required for requiredGroup");
    }
  }

  @Override
  public void handlePreBind(final EpoxyViewHolder holder, final PropGroupsView object,
      final int position) {
    validateStateHasNotChangedSinceAdded("The model was changed between being added to the controller and being bound.", position);
  }

  @Override
  public void bind(final PropGroupsView object) {
    super.bind(object);
    if (assignedAttributes_epoxyGeneratedModel.get(4)) {
      object.setPrimitive(primitive_Int);
    }
    else if (assignedAttributes_epoxyGeneratedModel.get(5)) {
      object.setPrimitive(primitive_Long);
    }
    else {
      object.setPrimitive(primitive_Int);
    }
    if (assignedAttributes_epoxyGeneratedModel.get(12)) {
      object.requiredGroup(requiredGroup_String);
    }
    else {
      object.requiredGroup(requiredGroup_CharSequence);
    }
    if (assignedAttributes_epoxyGeneratedModel.get(8)) {
      object.primitiveAndObjectGroupWithPrimitiveDefault(primitiveAndObjectGroupWithPrimitiveDefault_Long);
    }
    else if (assignedAttributes_epoxyGeneratedModel.get(9)) {
      object.primitiveAndObjectGroupWithPrimitiveDefault(primitiveAndObjectGroupWithPrimitiveDefault_CharSequence);
    }
    else {
      object.primitiveAndObjectGroupWithPrimitiveDefault(primitiveAndObjectGroupWithPrimitiveDefault_Long);
    }
    if (assignedAttributes_epoxyGeneratedModel.get(10)) {
      object.setOneThing(oneThing_Long);
    }
    else if (assignedAttributes_epoxyGeneratedModel.get(11)) {
      object.setAnotherThing(anotherThing_CharSequence);
    }
    else {
      object.setOneThing(oneThing_Long);
    }
    if (assignedAttributes_epoxyGeneratedModel.get(0)) {
      object.setSomething(something_CharSequence);
    }
    else if (assignedAttributes_epoxyGeneratedModel.get(1)) {
      object.setSomething(something_Int);
    }
    else {
      object.setSomething(something_CharSequence);
    }
    if (assignedAttributes_epoxyGeneratedModel.get(2)) {
      object.setSomethingElse(somethingElse_CharSequence);
    }
    else if (assignedAttributes_epoxyGeneratedModel.get(3)) {
      object.setSomethingElse(somethingElse_Int);
    }
    else {
      object.setSomethingElse(somethingElse_Int);
    }
    if (assignedAttributes_epoxyGeneratedModel.get(6)) {
      object.setPrimitiveWithDefault(primitiveWithDefault_Int);
    }
    else if (assignedAttributes_epoxyGeneratedModel.get(7)) {
      object.setPrimitiveWithDefault(primitiveWithDefault_Long);
    }
    else {
      object.setPrimitiveWithDefault(primitiveWithDefault_Long);
    }
  }

  @Override
  public void bind(final PropGroupsView object, EpoxyModel previousModel) {
    if (!(previousModel instanceof PropGroupsViewModel_)) {
      bind(object);
      return;
    }
    PropGroupsViewModel_ that = (PropGroupsViewModel_) previousModel;
    super.bind(object);

    if (assignedAttributes_epoxyGeneratedModel.get(4)) {
      if ((primitive_Int != that.primitive_Int)) {
        object.setPrimitive(primitive_Int);
      }
    }
    else if (assignedAttributes_epoxyGeneratedModel.get(5)) {
      if ((primitive_Long != that.primitive_Long)) {
        object.setPrimitive(primitive_Long);
      }
    }
    // A value was not set so we should use the default value, but we only need to set it if the previous model had a custom value set.
    else if (that.assignedAttributes_epoxyGeneratedModel.get(4) || that.assignedAttributes_epoxyGeneratedModel.get(5)) {
      object.setPrimitive(primitive_Int);
    }

    if (assignedAttributes_epoxyGeneratedModel.get(12)) {
      if (!that.assignedAttributes_epoxyGeneratedModel.get(12) || (requiredGroup_String != null ? !requiredGroup_String.equals(that.requiredGroup_String) : that.requiredGroup_String != null)) {
        object.requiredGroup(requiredGroup_String);
      }
    }
    else if (assignedAttributes_epoxyGeneratedModel.get(13)) {
      if (!that.assignedAttributes_epoxyGeneratedModel.get(13) || (requiredGroup_CharSequence != null ? !requiredGroup_CharSequence.equals(that.requiredGroup_CharSequence) : that.requiredGroup_CharSequence != null)) {
        object.requiredGroup(requiredGroup_CharSequence);
      }
    }

    if (assignedAttributes_epoxyGeneratedModel.get(8)) {
      if ((primitiveAndObjectGroupWithPrimitiveDefault_Long != that.primitiveAndObjectGroupWithPrimitiveDefault_Long)) {
        object.primitiveAndObjectGroupWithPrimitiveDefault(primitiveAndObjectGroupWithPrimitiveDefault_Long);
      }
    }
    else if (assignedAttributes_epoxyGeneratedModel.get(9)) {
      if (!that.assignedAttributes_epoxyGeneratedModel.get(9) || (primitiveAndObjectGroupWithPrimitiveDefault_CharSequence != null ? !primitiveAndObjectGroupWithPrimitiveDefault_CharSequence.equals(that.primitiveAndObjectGroupWithPrimitiveDefault_CharSequence) : that.primitiveAndObjectGroupWithPrimitiveDefault_CharSequence != null)) {
        object.primitiveAndObjectGroupWithPrimitiveDefault(primitiveAndObjectGroupWithPrimitiveDefault_CharSequence);
      }
    }
    // A value was not set so we should use the default value, but we only need to set it if the previous model had a custom value set.
    else if (that.assignedAttributes_epoxyGeneratedModel.get(8) || that.assignedAttributes_epoxyGeneratedModel.get(9)) {
      object.primitiveAndObjectGroupWithPrimitiveDefault(primitiveAndObjectGroupWithPrimitiveDefault_Long);
    }

    if (assignedAttributes_epoxyGeneratedModel.get(10)) {
      if ((oneThing_Long != that.oneThing_Long)) {
        object.setOneThing(oneThing_Long);
      }
    }
    else if (assignedAttributes_epoxyGeneratedModel.get(11)) {
      if (!that.assignedAttributes_epoxyGeneratedModel.get(11) || (anotherThing_CharSequence != null ? !anotherThing_CharSequence.equals(that.anotherThing_CharSequence) : that.anotherThing_CharSequence != null)) {
        object.setAnotherThing(anotherThing_CharSequence);
      }
    }
    // A value was not set so we should use the default value, but we only need to set it if the previous model had a custom value set.
    else if (that.assignedAttributes_epoxyGeneratedModel.get(10) || that.assignedAttributes_epoxyGeneratedModel.get(11)) {
      object.setOneThing(oneThing_Long);
    }

    if (assignedAttributes_epoxyGeneratedModel.get(0)) {
      if (!that.assignedAttributes_epoxyGeneratedModel.get(0) || (something_CharSequence != null ? !something_CharSequence.equals(that.something_CharSequence) : that.something_CharSequence != null)) {
        object.setSomething(something_CharSequence);
      }
    }
    else if (assignedAttributes_epoxyGeneratedModel.get(1)) {
      if ((something_Int != that.something_Int)) {
        object.setSomething(something_Int);
      }
    }
    // A value was not set so we should use the default value, but we only need to set it if the previous model had a custom value set.
    else if (that.assignedAttributes_epoxyGeneratedModel.get(0) || that.assignedAttributes_epoxyGeneratedModel.get(1)) {
      object.setSomething(something_CharSequence);
    }

    if (assignedAttributes_epoxyGeneratedModel.get(2)) {
      if (!that.assignedAttributes_epoxyGeneratedModel.get(2) || (somethingElse_CharSequence != null ? !somethingElse_CharSequence.equals(that.somethingElse_CharSequence) : that.somethingElse_CharSequence != null)) {
        object.setSomethingElse(somethingElse_CharSequence);
      }
    }
    else if (assignedAttributes_epoxyGeneratedModel.get(3)) {
      if ((somethingElse_Int != that.somethingElse_Int)) {
        object.setSomethingElse(somethingElse_Int);
      }
    }
    // A value was not set so we should use the default value, but we only need to set it if the previous model had a custom value set.
    else if (that.assignedAttributes_epoxyGeneratedModel.get(2) || that.assignedAttributes_epoxyGeneratedModel.get(3)) {
      object.setSomethingElse(somethingElse_Int);
    }

    if (assignedAttributes_epoxyGeneratedModel.get(6)) {
      if ((primitiveWithDefault_Int != that.primitiveWithDefault_Int)) {
        object.setPrimitiveWithDefault(primitiveWithDefault_Int);
      }
    }
    else if (assignedAttributes_epoxyGeneratedModel.get(7)) {
      if ((primitiveWithDefault_Long != that.primitiveWithDefault_Long)) {
        object.setPrimitiveWithDefault(primitiveWithDefault_Long);
      }
    }
    // A value was not set so we should use the default value, but we only need to set it if the previous model had a custom value set.
    else if (that.assignedAttributes_epoxyGeneratedModel.get(6) || that.assignedAttributes_epoxyGeneratedModel.get(7)) {
      object.setPrimitiveWithDefault(primitiveWithDefault_Long);
    }
  }

  @Override
  public void handlePostBind(final PropGroupsView object, int position) {
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
  public PropGroupsViewModel_ onBind(
      OnModelBoundListener<PropGroupsViewModel_, PropGroupsView> listener) {
    onMutation();
    this.onModelBoundListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public void unbind(PropGroupsView object) {
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
  public PropGroupsViewModel_ onUnbind(
      OnModelUnboundListener<PropGroupsViewModel_, PropGroupsView> listener) {
    onMutation();
    this.onModelUnboundListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public void onVisibilityStateChanged(int visibilityState, final PropGroupsView object) {
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
  public PropGroupsViewModel_ onVisibilityStateChanged(
      OnModelVisibilityStateChangedListener<PropGroupsViewModel_, PropGroupsView> listener) {
    onMutation();
    this.onModelVisibilityStateChangedListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public void onVisibilityChanged(float percentVisibleHeight, float percentVisibleWidth,
      int visibleHeight, int visibleWidth, final PropGroupsView object) {
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
  public PropGroupsViewModel_ onVisibilityChanged(
      OnModelVisibilityChangedListener<PropGroupsViewModel_, PropGroupsView> listener) {
    onMutation();
    this.onModelVisibilityChangedListener_epoxyGeneratedModel = listener;
    return this;
  }

  /**
   * <i>Optional</i>: Default value is (CharSequence) null
   *
   * @see PropGroupsView#setSomething(CharSequence)
   */
  public PropGroupsViewModel_ something(@Nullable CharSequence something) {
    assignedAttributes_epoxyGeneratedModel.set(0);
    assignedAttributes_epoxyGeneratedModel.clear(1);
    this.something_Int = 0;
    onMutation();
    this.something_CharSequence = something;
    return this;
  }

  @Nullable
  public CharSequence somethingCharSequence() {
    return something_CharSequence;
  }

  /**
   * <i>Optional</i>: Default value is 0
   *
   * @see PropGroupsView#setSomething(int)
   */
  public PropGroupsViewModel_ something(int something) {
    assignedAttributes_epoxyGeneratedModel.set(1);
    assignedAttributes_epoxyGeneratedModel.clear(0);
    this.something_CharSequence = (CharSequence) null;
    onMutation();
    this.something_Int = something;
    return this;
  }

  public int somethingInt() {
    return something_Int;
  }

  /**
   * <i>Required.</i>
   *
   * @see PropGroupsView#setSomethingElse(CharSequence)
   */
  public PropGroupsViewModel_ somethingElse(@NonNull CharSequence somethingElse) {
    if (somethingElse == null) {
      throw new IllegalArgumentException("somethingElse cannot be null");
    }
    assignedAttributes_epoxyGeneratedModel.set(2);
    assignedAttributes_epoxyGeneratedModel.clear(3);
    this.somethingElse_Int = 0;
    onMutation();
    this.somethingElse_CharSequence = somethingElse;
    return this;
  }

  @NonNull
  public CharSequence somethingElseCharSequence() {
    return somethingElse_CharSequence;
  }

  /**
   * <i>Optional</i>: Default value is 0
   *
   * @see PropGroupsView#setSomethingElse(int)
   */
  public PropGroupsViewModel_ somethingElse(int somethingElse) {
    assignedAttributes_epoxyGeneratedModel.set(3);
    assignedAttributes_epoxyGeneratedModel.clear(2);
    this.somethingElse_CharSequence = null;
    onMutation();
    this.somethingElse_Int = somethingElse;
    return this;
  }

  public int somethingElseInt() {
    return somethingElse_Int;
  }

  /**
   * <i>Optional</i>: Default value is 0
   *
   * @see PropGroupsView#setPrimitive(int)
   */
  public PropGroupsViewModel_ primitive(int primitive) {
    assignedAttributes_epoxyGeneratedModel.set(4);
    assignedAttributes_epoxyGeneratedModel.clear(5);
    this.primitive_Long = 0L;
    onMutation();
    this.primitive_Int = primitive;
    return this;
  }

  public int primitiveInt() {
    return primitive_Int;
  }

  /**
   * <i>Optional</i>: Default value is 0L
   *
   * @see PropGroupsView#setPrimitive(long)
   */
  public PropGroupsViewModel_ primitive(long primitive) {
    assignedAttributes_epoxyGeneratedModel.set(5);
    assignedAttributes_epoxyGeneratedModel.clear(4);
    this.primitive_Int = 0;
    onMutation();
    this.primitive_Long = primitive;
    return this;
  }

  public long primitiveLong() {
    return primitive_Long;
  }

  /**
   * <i>Optional</i>: Default value is 0
   *
   * @see PropGroupsView#setPrimitiveWithDefault(int)
   */
  public PropGroupsViewModel_ primitiveWithDefault(int primitiveWithDefault) {
    assignedAttributes_epoxyGeneratedModel.set(6);
    assignedAttributes_epoxyGeneratedModel.clear(7);
    this.primitiveWithDefault_Long = PropGroupsView.DEFAULT_PRIMITIVE;
    onMutation();
    this.primitiveWithDefault_Int = primitiveWithDefault;
    return this;
  }

  public int primitiveWithDefaultInt() {
    return primitiveWithDefault_Int;
  }

  /**
   * <i>Optional</i>: Default value is <b>{@value PropGroupsView#DEFAULT_PRIMITIVE}</b>
   *
   * @see PropGroupsView#setPrimitiveWithDefault(long)
   */
  public PropGroupsViewModel_ primitiveWithDefault(long primitiveWithDefault) {
    assignedAttributes_epoxyGeneratedModel.set(7);
    assignedAttributes_epoxyGeneratedModel.clear(6);
    this.primitiveWithDefault_Int = 0;
    onMutation();
    this.primitiveWithDefault_Long = primitiveWithDefault;
    return this;
  }

  public long primitiveWithDefaultLong() {
    return primitiveWithDefault_Long;
  }

  /**
   * <i>Optional</i>: Default value is <b>{@value PropGroupsView#DEFAULT_PRIMITIVE}</b>
   *
   * @see PropGroupsView#primitiveAndObjectGroupWithPrimitiveDefault(long)
   */
  public PropGroupsViewModel_ primitiveAndObjectGroupWithPrimitiveDefault(
      long primitiveAndObjectGroupWithPrimitiveDefault) {
    assignedAttributes_epoxyGeneratedModel.set(8);
    assignedAttributes_epoxyGeneratedModel.clear(9);
    this.primitiveAndObjectGroupWithPrimitiveDefault_CharSequence = null;
    onMutation();
    this.primitiveAndObjectGroupWithPrimitiveDefault_Long = primitiveAndObjectGroupWithPrimitiveDefault;
    return this;
  }

  public long primitiveAndObjectGroupWithPrimitiveDefaultLong() {
    return primitiveAndObjectGroupWithPrimitiveDefault_Long;
  }

  /**
   * <i>Required.</i>
   *
   * @see PropGroupsView#primitiveAndObjectGroupWithPrimitiveDefault(CharSequence)
   */
  public PropGroupsViewModel_ primitiveAndObjectGroupWithPrimitiveDefault(
      @NonNull CharSequence primitiveAndObjectGroupWithPrimitiveDefault) {
    if (primitiveAndObjectGroupWithPrimitiveDefault == null) {
      throw new IllegalArgumentException("primitiveAndObjectGroupWithPrimitiveDefault cannot be null");
    }
    assignedAttributes_epoxyGeneratedModel.set(9);
    assignedAttributes_epoxyGeneratedModel.clear(8);
    this.primitiveAndObjectGroupWithPrimitiveDefault_Long = PropGroupsView.DEFAULT_PRIMITIVE;
    onMutation();
    this.primitiveAndObjectGroupWithPrimitiveDefault_CharSequence = primitiveAndObjectGroupWithPrimitiveDefault;
    return this;
  }

  @NonNull
  public CharSequence primitiveAndObjectGroupWithPrimitiveDefaultCharSequence() {
    return primitiveAndObjectGroupWithPrimitiveDefault_CharSequence;
  }

  /**
   * <i>Optional</i>: Default value is 0L
   *
   * @see PropGroupsView#setOneThing(long)
   */
  public PropGroupsViewModel_ oneThing(long oneThing) {
    assignedAttributes_epoxyGeneratedModel.set(10);
    assignedAttributes_epoxyGeneratedModel.clear(11);
    this.anotherThing_CharSequence = null;
    onMutation();
    this.oneThing_Long = oneThing;
    return this;
  }

  public long oneThingLong() {
    return oneThing_Long;
  }

  /**
   * <i>Required.</i>
   *
   * @see PropGroupsView#setAnotherThing(CharSequence)
   */
  public PropGroupsViewModel_ anotherThing(@NonNull CharSequence anotherThing) {
    if (anotherThing == null) {
      throw new IllegalArgumentException("anotherThing cannot be null");
    }
    assignedAttributes_epoxyGeneratedModel.set(11);
    assignedAttributes_epoxyGeneratedModel.clear(10);
    this.oneThing_Long = 0L;
    onMutation();
    this.anotherThing_CharSequence = anotherThing;
    return this;
  }

  @NonNull
  public CharSequence anotherThingCharSequence() {
    return anotherThing_CharSequence;
  }

  /**
   * <i>Required.</i>
   *
   * @see PropGroupsView#requiredGroup(String)
   */
  public PropGroupsViewModel_ requiredGroup(@NonNull String requiredGroup) {
    if (requiredGroup == null) {
      throw new IllegalArgumentException("requiredGroup cannot be null");
    }
    assignedAttributes_epoxyGeneratedModel.set(12);
    assignedAttributes_epoxyGeneratedModel.clear(13);
    this.requiredGroup_CharSequence = null;
    onMutation();
    this.requiredGroup_String = requiredGroup;
    return this;
  }

  @NonNull
  public String requiredGroupString() {
    return requiredGroup_String;
  }

  /**
   * <i>Required.</i>
   *
   * @see PropGroupsView#requiredGroup(CharSequence)
   */
  public PropGroupsViewModel_ requiredGroup(@NonNull CharSequence requiredGroup) {
    if (requiredGroup == null) {
      throw new IllegalArgumentException("requiredGroup cannot be null");
    }
    assignedAttributes_epoxyGeneratedModel.set(13);
    assignedAttributes_epoxyGeneratedModel.clear(12);
    this.requiredGroup_String = null;
    onMutation();
    this.requiredGroup_CharSequence = requiredGroup;
    return this;
  }

  @NonNull
  public CharSequence requiredGroupCharSequence() {
    return requiredGroup_CharSequence;
  }

  @Override
  public PropGroupsViewModel_ id(long id) {
    super.id(id);
    return this;
  }

  @Override
  public PropGroupsViewModel_ id(@Nullable Number... ids) {
    super.id(ids);
    return this;
  }

  @Override
  public PropGroupsViewModel_ id(long id1, long id2) {
    super.id(id1, id2);
    return this;
  }

  @Override
  public PropGroupsViewModel_ id(@Nullable CharSequence key) {
    super.id(key);
    return this;
  }

  @Override
  public PropGroupsViewModel_ id(@Nullable CharSequence key, @Nullable CharSequence... otherKeys) {
    super.id(key, otherKeys);
    return this;
  }

  @Override
  public PropGroupsViewModel_ id(@Nullable CharSequence key, long id) {
    super.id(key, id);
    return this;
  }

  @Override
  public PropGroupsViewModel_ layout(@LayoutRes int layoutRes) {
    super.layout(layoutRes);
    return this;
  }

  @Override
  public PropGroupsViewModel_ spanSizeOverride(
      @Nullable EpoxyModel.SpanSizeOverrideCallback spanSizeCallback) {
    super.spanSizeOverride(spanSizeCallback);
    return this;
  }

  @Override
  public PropGroupsViewModel_ show() {
    super.show();
    return this;
  }

  @Override
  public PropGroupsViewModel_ show(boolean show) {
    super.show(show);
    return this;
  }

  @Override
  public PropGroupsViewModel_ hide() {
    super.hide();
    return this;
  }

  @Override
  @LayoutRes
  protected int getDefaultLayout() {
    return 1;
  }

  @Override
  public PropGroupsViewModel_ reset() {
    onModelBoundListener_epoxyGeneratedModel = null;
    onModelUnboundListener_epoxyGeneratedModel = null;
    onModelVisibilityStateChangedListener_epoxyGeneratedModel = null;
    onModelVisibilityChangedListener_epoxyGeneratedModel = null;
    assignedAttributes_epoxyGeneratedModel.clear();
    this.something_CharSequence = (CharSequence) null;
    this.something_Int = 0;
    this.somethingElse_CharSequence = null;
    this.somethingElse_Int = 0;
    this.primitive_Int = 0;
    this.primitive_Long = 0L;
    this.primitiveWithDefault_Int = 0;
    this.primitiveWithDefault_Long = PropGroupsView.DEFAULT_PRIMITIVE;
    this.primitiveAndObjectGroupWithPrimitiveDefault_Long = PropGroupsView.DEFAULT_PRIMITIVE;
    this.primitiveAndObjectGroupWithPrimitiveDefault_CharSequence = null;
    this.oneThing_Long = 0L;
    this.anotherThing_CharSequence = null;
    this.requiredGroup_String = null;
    this.requiredGroup_CharSequence = null;
    super.reset();
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof PropGroupsViewModel_)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    PropGroupsViewModel_ that = (PropGroupsViewModel_) o;
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
    if ((something_CharSequence != null ? !something_CharSequence.equals(that.something_CharSequence) : that.something_CharSequence != null)) {
      return false;
    }
    if ((something_Int != that.something_Int)) {
      return false;
    }
    if ((somethingElse_CharSequence != null ? !somethingElse_CharSequence.equals(that.somethingElse_CharSequence) : that.somethingElse_CharSequence != null)) {
      return false;
    }
    if ((somethingElse_Int != that.somethingElse_Int)) {
      return false;
    }
    if ((primitive_Int != that.primitive_Int)) {
      return false;
    }
    if ((primitive_Long != that.primitive_Long)) {
      return false;
    }
    if ((primitiveWithDefault_Int != that.primitiveWithDefault_Int)) {
      return false;
    }
    if ((primitiveWithDefault_Long != that.primitiveWithDefault_Long)) {
      return false;
    }
    if ((primitiveAndObjectGroupWithPrimitiveDefault_Long != that.primitiveAndObjectGroupWithPrimitiveDefault_Long)) {
      return false;
    }
    if ((primitiveAndObjectGroupWithPrimitiveDefault_CharSequence != null ? !primitiveAndObjectGroupWithPrimitiveDefault_CharSequence.equals(that.primitiveAndObjectGroupWithPrimitiveDefault_CharSequence) : that.primitiveAndObjectGroupWithPrimitiveDefault_CharSequence != null)) {
      return false;
    }
    if ((oneThing_Long != that.oneThing_Long)) {
      return false;
    }
    if ((anotherThing_CharSequence != null ? !anotherThing_CharSequence.equals(that.anotherThing_CharSequence) : that.anotherThing_CharSequence != null)) {
      return false;
    }
    if ((requiredGroup_String != null ? !requiredGroup_String.equals(that.requiredGroup_String) : that.requiredGroup_String != null)) {
      return false;
    }
    if ((requiredGroup_CharSequence != null ? !requiredGroup_CharSequence.equals(that.requiredGroup_CharSequence) : that.requiredGroup_CharSequence != null)) {
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
    _result = 31 * _result + (something_CharSequence != null ? something_CharSequence.hashCode() : 0);
    _result = 31 * _result + something_Int;
    _result = 31 * _result + (somethingElse_CharSequence != null ? somethingElse_CharSequence.hashCode() : 0);
    _result = 31 * _result + somethingElse_Int;
    _result = 31 * _result + primitive_Int;
    _result = 31 * _result + (int) (primitive_Long ^ (primitive_Long >>> 32));
    _result = 31 * _result + primitiveWithDefault_Int;
    _result = 31 * _result + (int) (primitiveWithDefault_Long ^ (primitiveWithDefault_Long >>> 32));
    _result = 31 * _result + (int) (primitiveAndObjectGroupWithPrimitiveDefault_Long ^ (primitiveAndObjectGroupWithPrimitiveDefault_Long >>> 32));
    _result = 31 * _result + (primitiveAndObjectGroupWithPrimitiveDefault_CharSequence != null ? primitiveAndObjectGroupWithPrimitiveDefault_CharSequence.hashCode() : 0);
    _result = 31 * _result + (int) (oneThing_Long ^ (oneThing_Long >>> 32));
    _result = 31 * _result + (anotherThing_CharSequence != null ? anotherThing_CharSequence.hashCode() : 0);
    _result = 31 * _result + (requiredGroup_String != null ? requiredGroup_String.hashCode() : 0);
    _result = 31 * _result + (requiredGroup_CharSequence != null ? requiredGroup_CharSequence.hashCode() : 0);
    return _result;
  }

  @Override
  public String toString() {
    return "PropGroupsViewModel_{" +
        "something_CharSequence=" + something_CharSequence +
        ", something_Int=" + something_Int +
        ", somethingElse_CharSequence=" + somethingElse_CharSequence +
        ", somethingElse_Int=" + somethingElse_Int +
        ", primitive_Int=" + primitive_Int +
        ", primitive_Long=" + primitive_Long +
        ", primitiveWithDefault_Int=" + primitiveWithDefault_Int +
        ", primitiveWithDefault_Long=" + primitiveWithDefault_Long +
        ", primitiveAndObjectGroupWithPrimitiveDefault_Long=" + primitiveAndObjectGroupWithPrimitiveDefault_Long +
        ", primitiveAndObjectGroupWithPrimitiveDefault_CharSequence=" + primitiveAndObjectGroupWithPrimitiveDefault_CharSequence +
        ", oneThing_Long=" + oneThing_Long +
        ", anotherThing_CharSequence=" + anotherThing_CharSequence +
        ", requiredGroup_String=" + requiredGroup_String +
        ", requiredGroup_CharSequence=" + requiredGroup_CharSequence +
        "}" + super.toString();
  }

  @Override
  public int getSpanSize(int totalSpanCount, int position, int itemCount) {
    return totalSpanCount;
  }
}
