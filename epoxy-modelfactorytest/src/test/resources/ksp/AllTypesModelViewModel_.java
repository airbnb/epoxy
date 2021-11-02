package com.airbnb.epoxy;

import android.view.View;
import androidx.annotation.DrawableRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RawRes;
import java.lang.Boolean;
import java.lang.CharSequence;
import java.lang.Double;
import java.lang.IllegalArgumentException;
import java.lang.IllegalStateException;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Number;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.util.BitSet;
import java.util.List;

/**
 * Generated file. Do not modify!
 */
public class AllTypesModelViewModel_ extends EpoxyModel<AllTypesModelView> implements GeneratedModel<AllTypesModelView>, AllTypesModelViewModelBuilder {
  private final BitSet assignedAttributes_epoxyGeneratedModel = new BitSet(15);

  private OnModelBoundListener<AllTypesModelViewModel_, AllTypesModelView> onModelBoundListener_epoxyGeneratedModel;

  private OnModelUnboundListener<AllTypesModelViewModel_, AllTypesModelView> onModelUnboundListener_epoxyGeneratedModel;

  private OnModelVisibilityStateChangedListener<AllTypesModelViewModel_, AllTypesModelView> onModelVisibilityStateChangedListener_epoxyGeneratedModel;

  private OnModelVisibilityChangedListener<AllTypesModelViewModel_, AllTypesModelView> onModelVisibilityChangedListener_epoxyGeneratedModel;

  private boolean booleanValue_Boolean = false;

  /**
   * Bitset index: 1
   */
  @NonNull
  private Boolean boxedBooleanValue_Boolean;

  /**
   * Bitset index: 2
   */
  @NonNull
  private CharSequence charSequenceValue_CharSequence;

  /**
   * Bitset index: 3
   */
  @NonNull
  private Double boxedDoubleValue_Double;

  private double doubleValue_Double = 0.0d;

  @DrawableRes
  private int drawableRes_Int = 0;

  /**
   * Bitset index: 6
   */
  @NonNull
  private List<? extends EpoxyModel<?>> epoxyModelList_List;

  private int intValue_Int = 0;

  /**
   * Bitset index: 8
   */
  @NonNull
  private Integer boxedIntValue_Integer;

  private long longValue_Long = 0L;

  /**
   * Bitset index: 10
   */
  @NonNull
  private Long boxedLongValue_Long;

  /**
   * Bitset index: 11
   */
  @NonNull
  private View.OnClickListener onClickListener_OnClickListener;

  @RawRes
  private int rawRes_Int = 0;

  /**
   * Bitset index: 13
   */
  @NonNull
  private String stringValue_String;

  /**
   * Bitset index: 14
   */
  @NonNull
  private List<String> stringList_List;

  @Override
  public void addTo(EpoxyController controller) {
    super.addTo(controller);
    addWithDebugValidation(controller);
    if (!assignedAttributes_epoxyGeneratedModel.get(8)) {
    	throw new IllegalStateException("A value is required for setBoxedIntValue");
    }
    if (!assignedAttributes_epoxyGeneratedModel.get(1)) {
    	throw new IllegalStateException("A value is required for setBoxedBooleanValue");
    }
    if (!assignedAttributes_epoxyGeneratedModel.get(3)) {
    	throw new IllegalStateException("A value is required for setBoxedDoubleValue");
    }
    if (!assignedAttributes_epoxyGeneratedModel.get(6)) {
    	throw new IllegalStateException("A value is required for setEpoxyModelList");
    }
    if (!assignedAttributes_epoxyGeneratedModel.get(10)) {
    	throw new IllegalStateException("A value is required for setBoxedLongValue");
    }
    if (!assignedAttributes_epoxyGeneratedModel.get(14)) {
    	throw new IllegalStateException("A value is required for setStringList");
    }
    if (!assignedAttributes_epoxyGeneratedModel.get(13)) {
    	throw new IllegalStateException("A value is required for setStringValue");
    }
    if (!assignedAttributes_epoxyGeneratedModel.get(11)) {
    	throw new IllegalStateException("A value is required for setOnClickListener");
    }
    if (!assignedAttributes_epoxyGeneratedModel.get(2)) {
    	throw new IllegalStateException("A value is required for setCharSequenceValue");
    }
  }

  @Override
  public void handlePreBind(final EpoxyViewHolder holder, final AllTypesModelView object,
      final int position) {
    validateStateHasNotChangedSinceAdded("The model was changed between being added to the controller and being bound.", position);
  }

  @Override
  public void bind(final AllTypesModelView object) {
    super.bind(object);
    object.setBooleanValue(booleanValue_Boolean);
    object.setBoxedIntValue(boxedIntValue_Integer);
    object.setBoxedBooleanValue(boxedBooleanValue_Boolean);
    object.setBoxedDoubleValue(boxedDoubleValue_Double);
    object.setEpoxyModelList(epoxyModelList_List);
    object.setBoxedLongValue(boxedLongValue_Long);
    object.setLongValue(longValue_Long);
    object.setStringList(stringList_List);
    object.setStringValue(stringValue_String);
    object.setOnClickListener(onClickListener_OnClickListener);
    object.setIntValue(intValue_Int);
    object.setDrawableRes(drawableRes_Int);
    object.setDoubleValue(doubleValue_Double);
    object.setRawRes(rawRes_Int);
    object.setCharSequenceValue(charSequenceValue_CharSequence);
  }

  @Override
  public void bind(final AllTypesModelView object, EpoxyModel previousModel) {
    if (!(previousModel instanceof AllTypesModelViewModel_)) {
      bind(object);
      return;
    }
    AllTypesModelViewModel_ that = (AllTypesModelViewModel_) previousModel;
    super.bind(object);

    if ((booleanValue_Boolean != that.booleanValue_Boolean)) {
      object.setBooleanValue(booleanValue_Boolean);
    }

    if ((boxedIntValue_Integer != null ? !boxedIntValue_Integer.equals(that.boxedIntValue_Integer) : that.boxedIntValue_Integer != null)) {
      object.setBoxedIntValue(boxedIntValue_Integer);
    }

    if ((boxedBooleanValue_Boolean != null ? !boxedBooleanValue_Boolean.equals(that.boxedBooleanValue_Boolean) : that.boxedBooleanValue_Boolean != null)) {
      object.setBoxedBooleanValue(boxedBooleanValue_Boolean);
    }

    if ((boxedDoubleValue_Double != null ? !boxedDoubleValue_Double.equals(that.boxedDoubleValue_Double) : that.boxedDoubleValue_Double != null)) {
      object.setBoxedDoubleValue(boxedDoubleValue_Double);
    }

    if ((epoxyModelList_List != null ? !epoxyModelList_List.equals(that.epoxyModelList_List) : that.epoxyModelList_List != null)) {
      object.setEpoxyModelList(epoxyModelList_List);
    }

    if ((boxedLongValue_Long != null ? !boxedLongValue_Long.equals(that.boxedLongValue_Long) : that.boxedLongValue_Long != null)) {
      object.setBoxedLongValue(boxedLongValue_Long);
    }

    if ((longValue_Long != that.longValue_Long)) {
      object.setLongValue(longValue_Long);
    }

    if ((stringList_List != null ? !stringList_List.equals(that.stringList_List) : that.stringList_List != null)) {
      object.setStringList(stringList_List);
    }

    if ((stringValue_String != null ? !stringValue_String.equals(that.stringValue_String) : that.stringValue_String != null)) {
      object.setStringValue(stringValue_String);
    }

    if (((onClickListener_OnClickListener == null) != (that.onClickListener_OnClickListener == null))) {
      object.setOnClickListener(onClickListener_OnClickListener);
    }

    if ((intValue_Int != that.intValue_Int)) {
      object.setIntValue(intValue_Int);
    }

    if ((drawableRes_Int != that.drawableRes_Int)) {
      object.setDrawableRes(drawableRes_Int);
    }

    if ((Double.compare(that.doubleValue_Double, doubleValue_Double) != 0)) {
      object.setDoubleValue(doubleValue_Double);
    }

    if ((rawRes_Int != that.rawRes_Int)) {
      object.setRawRes(rawRes_Int);
    }

    if ((charSequenceValue_CharSequence != null ? !charSequenceValue_CharSequence.equals(that.charSequenceValue_CharSequence) : that.charSequenceValue_CharSequence != null)) {
      object.setCharSequenceValue(charSequenceValue_CharSequence);
    }
  }

  @Override
  public void handlePostBind(final AllTypesModelView object, int position) {
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
  public AllTypesModelViewModel_ onBind(
      OnModelBoundListener<AllTypesModelViewModel_, AllTypesModelView> listener) {
    onMutation();
    this.onModelBoundListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public void unbind(AllTypesModelView object) {
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
  public AllTypesModelViewModel_ onUnbind(
      OnModelUnboundListener<AllTypesModelViewModel_, AllTypesModelView> listener) {
    onMutation();
    this.onModelUnboundListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public void onVisibilityStateChanged(int visibilityState, final AllTypesModelView object) {
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
  public AllTypesModelViewModel_ onVisibilityStateChanged(
      OnModelVisibilityStateChangedListener<AllTypesModelViewModel_, AllTypesModelView> listener) {
    onMutation();
    this.onModelVisibilityStateChangedListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public void onVisibilityChanged(float percentVisibleHeight, float percentVisibleWidth,
      int visibleHeight, int visibleWidth, final AllTypesModelView object) {
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
  public AllTypesModelViewModel_ onVisibilityChanged(
      OnModelVisibilityChangedListener<AllTypesModelViewModel_, AllTypesModelView> listener) {
    onMutation();
    this.onModelVisibilityChangedListener_epoxyGeneratedModel = listener;
    return this;
  }

  /**
   * <i>Optional</i>: Default value is false
   *
   * @see AllTypesModelView#setBooleanValue(boolean)
   */
  public AllTypesModelViewModel_ booleanValue(boolean booleanValue) {
    onMutation();
    this.booleanValue_Boolean = booleanValue;
    return this;
  }

  public boolean booleanValue() {
    return booleanValue_Boolean;
  }

  /**
   * <i>Required.</i>
   *
   * @see AllTypesModelView#setBoxedBooleanValue(Boolean)
   */
  public AllTypesModelViewModel_ boxedBooleanValue(@NonNull Boolean boxedBooleanValue) {
    if (boxedBooleanValue == null) {
      throw new IllegalArgumentException("boxedBooleanValue cannot be null");
    }
    assignedAttributes_epoxyGeneratedModel.set(1);
    onMutation();
    this.boxedBooleanValue_Boolean = boxedBooleanValue;
    return this;
  }

  @NonNull
  public Boolean boxedBooleanValue() {
    return boxedBooleanValue_Boolean;
  }

  /**
   * <i>Required.</i>
   *
   * @see AllTypesModelView#setCharSequenceValue(CharSequence)
   */
  public AllTypesModelViewModel_ charSequenceValue(@NonNull CharSequence charSequenceValue) {
    if (charSequenceValue == null) {
      throw new IllegalArgumentException("charSequenceValue cannot be null");
    }
    assignedAttributes_epoxyGeneratedModel.set(2);
    onMutation();
    this.charSequenceValue_CharSequence = charSequenceValue;
    return this;
  }

  @NonNull
  public CharSequence charSequenceValue() {
    return charSequenceValue_CharSequence;
  }

  /**
   * <i>Required.</i>
   *
   * @see AllTypesModelView#setBoxedDoubleValue(Double)
   */
  public AllTypesModelViewModel_ boxedDoubleValue(@NonNull Double boxedDoubleValue) {
    if (boxedDoubleValue == null) {
      throw new IllegalArgumentException("boxedDoubleValue cannot be null");
    }
    assignedAttributes_epoxyGeneratedModel.set(3);
    onMutation();
    this.boxedDoubleValue_Double = boxedDoubleValue;
    return this;
  }

  @NonNull
  public Double boxedDoubleValue() {
    return boxedDoubleValue_Double;
  }

  /**
   * <i>Optional</i>: Default value is 0.0d
   *
   * @see AllTypesModelView#setDoubleValue(double)
   */
  public AllTypesModelViewModel_ doubleValue(double doubleValue) {
    onMutation();
    this.doubleValue_Double = doubleValue;
    return this;
  }

  public double doubleValue() {
    return doubleValue_Double;
  }

  /**
   * <i>Optional</i>: Default value is 0
   *
   * @see AllTypesModelView#setDrawableRes(int)
   */
  public AllTypesModelViewModel_ drawableRes(@DrawableRes int drawableRes) {
    onMutation();
    this.drawableRes_Int = drawableRes;
    return this;
  }

  @DrawableRes
  public int drawableRes() {
    return drawableRes_Int;
  }

  /**
   * <i>Required.</i>
   *
   * @see AllTypesModelView#setEpoxyModelList(List<? extends EpoxyModel<?>>)
   */
  public AllTypesModelViewModel_ epoxyModelList(
      @NonNull List<? extends EpoxyModel<?>> epoxyModelList) {
    if (epoxyModelList == null) {
      throw new IllegalArgumentException("epoxyModelList cannot be null");
    }
    assignedAttributes_epoxyGeneratedModel.set(6);
    onMutation();
    this.epoxyModelList_List = epoxyModelList;
    return this;
  }

  @NonNull
  public List<? extends EpoxyModel<?>> epoxyModelList() {
    return epoxyModelList_List;
  }

  /**
   * <i>Optional</i>: Default value is 0
   *
   * @see AllTypesModelView#setIntValue(int)
   */
  public AllTypesModelViewModel_ intValue(int intValue) {
    onMutation();
    this.intValue_Int = intValue;
    return this;
  }

  public int intValue() {
    return intValue_Int;
  }

  /**
   * <i>Required.</i>
   *
   * @see AllTypesModelView#setBoxedIntValue(Integer)
   */
  public AllTypesModelViewModel_ boxedIntValue(@NonNull Integer boxedIntValue) {
    if (boxedIntValue == null) {
      throw new IllegalArgumentException("boxedIntValue cannot be null");
    }
    assignedAttributes_epoxyGeneratedModel.set(8);
    onMutation();
    this.boxedIntValue_Integer = boxedIntValue;
    return this;
  }

  @NonNull
  public Integer boxedIntValue() {
    return boxedIntValue_Integer;
  }

  /**
   * <i>Optional</i>: Default value is 0L
   *
   * @see AllTypesModelView#setLongValue(long)
   */
  public AllTypesModelViewModel_ longValue(long longValue) {
    onMutation();
    this.longValue_Long = longValue;
    return this;
  }

  public long longValue() {
    return longValue_Long;
  }

  /**
   * <i>Required.</i>
   *
   * @see AllTypesModelView#setBoxedLongValue(Long)
   */
  public AllTypesModelViewModel_ boxedLongValue(@NonNull Long boxedLongValue) {
    if (boxedLongValue == null) {
      throw new IllegalArgumentException("boxedLongValue cannot be null");
    }
    assignedAttributes_epoxyGeneratedModel.set(10);
    onMutation();
    this.boxedLongValue_Long = boxedLongValue;
    return this;
  }

  @NonNull
  public Long boxedLongValue() {
    return boxedLongValue_Long;
  }

  /**
   * Set a click listener that will provide the parent view, model, and adapter position of the clicked view. This will clear the normal View.OnClickListener if one has been set
   */
  public AllTypesModelViewModel_ onClickListener(
      @NonNull final OnModelClickListener<AllTypesModelViewModel_, AllTypesModelView> onClickListener) {
    assignedAttributes_epoxyGeneratedModel.set(11);
    onMutation();
    if (onClickListener == null) {
      this.onClickListener_OnClickListener = null;
    }
    else {
      this.onClickListener_OnClickListener = new WrappedEpoxyModelClickListener(onClickListener);
    }
    return this;
  }

  /**
   * <i>Required.</i>
   *
   * @see AllTypesModelView#setOnClickListener(View.OnClickListener)
   */
  public AllTypesModelViewModel_ onClickListener(@NonNull View.OnClickListener onClickListener) {
    if (onClickListener == null) {
      throw new IllegalArgumentException("onClickListener cannot be null");
    }
    assignedAttributes_epoxyGeneratedModel.set(11);
    onMutation();
    this.onClickListener_OnClickListener = onClickListener;
    return this;
  }

  @NonNull
  public View.OnClickListener onClickListener() {
    return onClickListener_OnClickListener;
  }

  /**
   * <i>Optional</i>: Default value is 0
   *
   * @see AllTypesModelView#setRawRes(int)
   */
  public AllTypesModelViewModel_ rawRes(@RawRes int rawRes) {
    onMutation();
    this.rawRes_Int = rawRes;
    return this;
  }

  @RawRes
  public int rawRes() {
    return rawRes_Int;
  }

  /**
   * <i>Required.</i>
   *
   * @see AllTypesModelView#setStringValue(String)
   */
  public AllTypesModelViewModel_ stringValue(@NonNull String stringValue) {
    if (stringValue == null) {
      throw new IllegalArgumentException("stringValue cannot be null");
    }
    assignedAttributes_epoxyGeneratedModel.set(13);
    onMutation();
    this.stringValue_String = stringValue;
    return this;
  }

  @NonNull
  public String stringValue() {
    return stringValue_String;
  }

  /**
   * <i>Required.</i>
   *
   * @see AllTypesModelView#setStringList(List<String>)
   */
  public AllTypesModelViewModel_ stringList(@NonNull List<String> stringList) {
    if (stringList == null) {
      throw new IllegalArgumentException("stringList cannot be null");
    }
    assignedAttributes_epoxyGeneratedModel.set(14);
    onMutation();
    this.stringList_List = stringList;
    return this;
  }

  @NonNull
  public List<String> stringList() {
    return stringList_List;
  }

  @Override
  public AllTypesModelViewModel_ id(long p0) {
    super.id(p0);
    return this;
  }

  @Override
  public AllTypesModelViewModel_ id(@Nullable Number... p0) {
    super.id(p0);
    return this;
  }

  @Override
  public AllTypesModelViewModel_ id(long p0, long p1) {
    super.id(p0, p1);
    return this;
  }

  @Override
  public AllTypesModelViewModel_ id(@Nullable CharSequence p0) {
    super.id(p0);
    return this;
  }

  @Override
  public AllTypesModelViewModel_ id(@Nullable CharSequence p0, @Nullable CharSequence... p1) {
    super.id(p0, p1);
    return this;
  }

  @Override
  public AllTypesModelViewModel_ id(@Nullable CharSequence p0, long p1) {
    super.id(p0, p1);
    return this;
  }

  @Override
  public AllTypesModelViewModel_ layout(@LayoutRes int p0) {
    super.layout(p0);
    return this;
  }

  @Override
  public AllTypesModelViewModel_ spanSizeOverride(
      @Nullable EpoxyModel.SpanSizeOverrideCallback p0) {
    super.spanSizeOverride(p0);
    return this;
  }

  @Override
  public AllTypesModelViewModel_ show() {
    super.show();
    return this;
  }

  @Override
  public AllTypesModelViewModel_ show(boolean p0) {
    super.show(p0);
    return this;
  }

  @Override
  public AllTypesModelViewModel_ hide() {
    super.hide();
    return this;
  }

  @Override
  @LayoutRes
  protected int getDefaultLayout() {
    return 1;
  }

  @Override
  public AllTypesModelViewModel_ reset() {
    onModelBoundListener_epoxyGeneratedModel = null;
    onModelUnboundListener_epoxyGeneratedModel = null;
    onModelVisibilityStateChangedListener_epoxyGeneratedModel = null;
    onModelVisibilityChangedListener_epoxyGeneratedModel = null;
    assignedAttributes_epoxyGeneratedModel.clear();
    this.booleanValue_Boolean = false;
    this.boxedBooleanValue_Boolean = null;
    this.charSequenceValue_CharSequence = null;
    this.boxedDoubleValue_Double = null;
    this.doubleValue_Double = 0.0d;
    this.drawableRes_Int = 0;
    this.epoxyModelList_List = null;
    this.intValue_Int = 0;
    this.boxedIntValue_Integer = null;
    this.longValue_Long = 0L;
    this.boxedLongValue_Long = null;
    this.onClickListener_OnClickListener = null;
    this.rawRes_Int = 0;
    this.stringValue_String = null;
    this.stringList_List = null;
    super.reset();
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof AllTypesModelViewModel_)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    AllTypesModelViewModel_ that = (AllTypesModelViewModel_) o;
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
    if ((booleanValue_Boolean != that.booleanValue_Boolean)) {
      return false;
    }
    if ((boxedBooleanValue_Boolean != null ? !boxedBooleanValue_Boolean.equals(that.boxedBooleanValue_Boolean) : that.boxedBooleanValue_Boolean != null)) {
      return false;
    }
    if ((charSequenceValue_CharSequence != null ? !charSequenceValue_CharSequence.equals(that.charSequenceValue_CharSequence) : that.charSequenceValue_CharSequence != null)) {
      return false;
    }
    if ((boxedDoubleValue_Double != null ? !boxedDoubleValue_Double.equals(that.boxedDoubleValue_Double) : that.boxedDoubleValue_Double != null)) {
      return false;
    }
    if ((Double.compare(that.doubleValue_Double, doubleValue_Double) != 0)) {
      return false;
    }
    if ((drawableRes_Int != that.drawableRes_Int)) {
      return false;
    }
    if ((epoxyModelList_List != null ? !epoxyModelList_List.equals(that.epoxyModelList_List) : that.epoxyModelList_List != null)) {
      return false;
    }
    if ((intValue_Int != that.intValue_Int)) {
      return false;
    }
    if ((boxedIntValue_Integer != null ? !boxedIntValue_Integer.equals(that.boxedIntValue_Integer) : that.boxedIntValue_Integer != null)) {
      return false;
    }
    if ((longValue_Long != that.longValue_Long)) {
      return false;
    }
    if ((boxedLongValue_Long != null ? !boxedLongValue_Long.equals(that.boxedLongValue_Long) : that.boxedLongValue_Long != null)) {
      return false;
    }
    if (((onClickListener_OnClickListener == null) != (that.onClickListener_OnClickListener == null))) {
      return false;
    }
    if ((rawRes_Int != that.rawRes_Int)) {
      return false;
    }
    if ((stringValue_String != null ? !stringValue_String.equals(that.stringValue_String) : that.stringValue_String != null)) {
      return false;
    }
    if ((stringList_List != null ? !stringList_List.equals(that.stringList_List) : that.stringList_List != null)) {
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
    long temp;
    _result = 31 * _result + (booleanValue_Boolean ? 1 : 0);
    _result = 31 * _result + (boxedBooleanValue_Boolean != null ? boxedBooleanValue_Boolean.hashCode() : 0);
    _result = 31 * _result + (charSequenceValue_CharSequence != null ? charSequenceValue_CharSequence.hashCode() : 0);
    _result = 31 * _result + (boxedDoubleValue_Double != null ? boxedDoubleValue_Double.hashCode() : 0);
    temp = Double.doubleToLongBits(doubleValue_Double);
    _result = 31 * _result + (int) (temp ^ (temp >>> 32));
    _result = 31 * _result + drawableRes_Int;
    _result = 31 * _result + (epoxyModelList_List != null ? epoxyModelList_List.hashCode() : 0);
    _result = 31 * _result + intValue_Int;
    _result = 31 * _result + (boxedIntValue_Integer != null ? boxedIntValue_Integer.hashCode() : 0);
    _result = 31 * _result + (int) (longValue_Long ^ (longValue_Long >>> 32));
    _result = 31 * _result + (boxedLongValue_Long != null ? boxedLongValue_Long.hashCode() : 0);
    _result = 31 * _result + (onClickListener_OnClickListener != null ? 1 : 0);
    _result = 31 * _result + rawRes_Int;
    _result = 31 * _result + (stringValue_String != null ? stringValue_String.hashCode() : 0);
    _result = 31 * _result + (stringList_List != null ? stringList_List.hashCode() : 0);
    return _result;
  }

  @Override
  public String toString() {
    return "AllTypesModelViewModel_{" +
        "booleanValue_Boolean=" + booleanValue_Boolean +
        ", boxedBooleanValue_Boolean=" + boxedBooleanValue_Boolean +
        ", charSequenceValue_CharSequence=" + charSequenceValue_CharSequence +
        ", boxedDoubleValue_Double=" + boxedDoubleValue_Double +
        ", doubleValue_Double=" + doubleValue_Double +
        ", drawableRes_Int=" + drawableRes_Int +
        ", epoxyModelList_List=" + epoxyModelList_List +
        ", intValue_Int=" + intValue_Int +
        ", boxedIntValue_Integer=" + boxedIntValue_Integer +
        ", longValue_Long=" + longValue_Long +
        ", boxedLongValue_Long=" + boxedLongValue_Long +
        ", onClickListener_OnClickListener=" + onClickListener_OnClickListener +
        ", rawRes_Int=" + rawRes_Int +
        ", stringValue_String=" + stringValue_String +
        ", stringList_List=" + stringList_List +
        "}" + super.toString();
  }

  public static AllTypesModelViewModel_ from(ModelProperties properties) {
    AllTypesModelViewModel_ model = new AllTypesModelViewModel_();
    model.id(properties.getId());
    if (properties.has("booleanValue")) {
      model.booleanValue(properties.getBoolean("booleanValue"));
    }
    if (properties.has("boxedBooleanValue")) {
      model.boxedBooleanValue(properties.getBoolean("boxedBooleanValue"));
    }
    if (properties.has("charSequenceValue")) {
      model.charSequenceValue(properties.getString("charSequenceValue"));
    }
    if (properties.has("boxedDoubleValue")) {
      model.boxedDoubleValue(properties.getDouble("boxedDoubleValue"));
    }
    if (properties.has("doubleValue")) {
      model.doubleValue(properties.getDouble("doubleValue"));
    }
    if (properties.has("drawableRes")) {
      model.drawableRes(properties.getDrawableRes("drawableRes"));
    }
    if (properties.has("epoxyModelList")) {
      model.epoxyModelList(properties.getEpoxyModelList("epoxyModelList"));
    }
    if (properties.has("intValue")) {
      model.intValue(properties.getInt("intValue"));
    }
    if (properties.has("boxedIntValue")) {
      model.boxedIntValue(properties.getInt("boxedIntValue"));
    }
    if (properties.has("longValue")) {
      model.longValue(properties.getLong("longValue"));
    }
    if (properties.has("boxedLongValue")) {
      model.boxedLongValue(properties.getLong("boxedLongValue"));
    }
    if (properties.has("onClickListener")) {
      model.onClickListener(properties.getOnClickListener("onClickListener"));
    }
    if (properties.has("rawRes")) {
      model.rawRes(properties.getRawRes("rawRes"));
    }
    if (properties.has("stringValue")) {
      model.stringValue(properties.getString("stringValue"));
    }
    if (properties.has("stringList")) {
      model.stringList(properties.getStringList("stringList"));
    }
    return model;
  }

  @Override
  public int getSpanSize(int totalSpanCount, int position, int itemCount) {
    return totalSpanCount;
  }
}
