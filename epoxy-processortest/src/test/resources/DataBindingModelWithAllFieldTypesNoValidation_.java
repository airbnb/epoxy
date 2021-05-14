
package com.airbnb.epoxy;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.databinding.ViewDataBinding;
import java.lang.Boolean;
import java.lang.Byte;
import java.lang.CharSequence;
import java.lang.Character;
import java.lang.Double;
import java.lang.Float;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Number;
import java.lang.Object;
import java.lang.Override;
import java.lang.Short;
import java.lang.String;
import java.util.Arrays;
import java.util.List;

/**
 * Generated file. Do not modify!
 */
public class DataBindingModelWithAllFieldTypesNoValidation_ extends DataBindingModelWithAllFieldTypesNoValidation implements GeneratedModel<DataBindingEpoxyModel.DataBindingHolder>, DataBindingModelWithAllFieldTypesNoValidationBuilder {
  private OnModelBoundListener<DataBindingModelWithAllFieldTypesNoValidation_, DataBindingEpoxyModel.DataBindingHolder> onModelBoundListener_epoxyGeneratedModel;

  private OnModelUnboundListener<DataBindingModelWithAllFieldTypesNoValidation_, DataBindingEpoxyModel.DataBindingHolder> onModelUnboundListener_epoxyGeneratedModel;

  private OnModelVisibilityStateChangedListener<DataBindingModelWithAllFieldTypesNoValidation_, DataBindingEpoxyModel.DataBindingHolder> onModelVisibilityStateChangedListener_epoxyGeneratedModel;

  private OnModelVisibilityChangedListener<DataBindingModelWithAllFieldTypesNoValidation_, DataBindingEpoxyModel.DataBindingHolder> onModelVisibilityChangedListener_epoxyGeneratedModel;

  public DataBindingModelWithAllFieldTypesNoValidation_() {
    super();
  }

  @Override
  public void handlePreBind(final EpoxyViewHolder holder,
      final DataBindingEpoxyModel.DataBindingHolder object, final int position) {
  }

  @Override
  public void handlePostBind(final DataBindingEpoxyModel.DataBindingHolder object, int position) {
    if (onModelBoundListener_epoxyGeneratedModel != null) {
      onModelBoundListener_epoxyGeneratedModel.onModelBound(this, object, position);
    }
  }

  /**
   * Register a listener that will be called when this model is bound to a view.
   * <p>
   * The listener will contribute to this model's hashCode state per the {@link
   * com.airbnb.epoxy.EpoxyAttribute.Option#DoNotHash} rules.
   * <p>
   * You may clear the listener by setting a null value, or by calling {@link #reset()}
   */
  public DataBindingModelWithAllFieldTypesNoValidation_ onBind(
      OnModelBoundListener<DataBindingModelWithAllFieldTypesNoValidation_, DataBindingEpoxyModel.DataBindingHolder> listener) {
    onMutation();
    this.onModelBoundListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public void unbind(DataBindingEpoxyModel.DataBindingHolder object) {
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
  public DataBindingModelWithAllFieldTypesNoValidation_ onUnbind(
      OnModelUnboundListener<DataBindingModelWithAllFieldTypesNoValidation_, DataBindingEpoxyModel.DataBindingHolder> listener) {
    onMutation();
    this.onModelUnboundListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public void onVisibilityStateChanged(int visibilityState,
      final DataBindingEpoxyModel.DataBindingHolder object) {
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
  public DataBindingModelWithAllFieldTypesNoValidation_ onVisibilityStateChanged(
      OnModelVisibilityStateChangedListener<DataBindingModelWithAllFieldTypesNoValidation_, DataBindingEpoxyModel.DataBindingHolder> listener) {
    onMutation();
    this.onModelVisibilityStateChangedListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public void onVisibilityChanged(float percentVisibleHeight, float percentVisibleWidth,
      int visibleHeight, int visibleWidth, final DataBindingEpoxyModel.DataBindingHolder object) {
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
  public DataBindingModelWithAllFieldTypesNoValidation_ onVisibilityChanged(
      OnModelVisibilityChangedListener<DataBindingModelWithAllFieldTypesNoValidation_, DataBindingEpoxyModel.DataBindingHolder> listener) {
    onMutation();
    this.onModelVisibilityChangedListener_epoxyGeneratedModel = listener;
    return this;
  }

  public DataBindingModelWithAllFieldTypesNoValidation_ valueInt(int valueInt) {
    onMutation();
    super.valueInt = valueInt;
    return this;
  }

  public int valueInt() {
    return valueInt;
  }

  public DataBindingModelWithAllFieldTypesNoValidation_ valueInteger(Integer valueInteger) {
    onMutation();
    super.valueInteger = valueInteger;
    return this;
  }

  public Integer valueInteger() {
    return valueInteger;
  }

  public DataBindingModelWithAllFieldTypesNoValidation_ valueShort(short valueShort) {
    onMutation();
    super.valueShort = valueShort;
    return this;
  }

  public short valueShort() {
    return valueShort;
  }

  public DataBindingModelWithAllFieldTypesNoValidation_ valueShortWrapper(Short valueShortWrapper) {
    onMutation();
    super.valueShortWrapper = valueShortWrapper;
    return this;
  }

  public Short valueShortWrapper() {
    return valueShortWrapper;
  }

  public DataBindingModelWithAllFieldTypesNoValidation_ valueChar(char valueChar) {
    onMutation();
    super.valueChar = valueChar;
    return this;
  }

  public char valueChar() {
    return valueChar;
  }

  public DataBindingModelWithAllFieldTypesNoValidation_ valueCharacter(Character valueCharacter) {
    onMutation();
    super.valueCharacter = valueCharacter;
    return this;
  }

  public Character valueCharacter() {
    return valueCharacter;
  }

  public DataBindingModelWithAllFieldTypesNoValidation_ valuebByte(byte valuebByte) {
    onMutation();
    super.valuebByte = valuebByte;
    return this;
  }

  public byte valuebByte() {
    return valuebByte;
  }

  public DataBindingModelWithAllFieldTypesNoValidation_ valueByteWrapper(Byte valueByteWrapper) {
    onMutation();
    super.valueByteWrapper = valueByteWrapper;
    return this;
  }

  public Byte valueByteWrapper() {
    return valueByteWrapper;
  }

  public DataBindingModelWithAllFieldTypesNoValidation_ valueLong(long valueLong) {
    onMutation();
    super.valueLong = valueLong;
    return this;
  }

  public long valueLong() {
    return valueLong;
  }

  public DataBindingModelWithAllFieldTypesNoValidation_ valueLongWrapper(Long valueLongWrapper) {
    onMutation();
    super.valueLongWrapper = valueLongWrapper;
    return this;
  }

  public Long valueLongWrapper() {
    return valueLongWrapper;
  }

  public DataBindingModelWithAllFieldTypesNoValidation_ valueDouble(double valueDouble) {
    onMutation();
    super.valueDouble = valueDouble;
    return this;
  }

  public double valueDouble() {
    return valueDouble;
  }

  public DataBindingModelWithAllFieldTypesNoValidation_ valueDoubleWrapper(
      Double valueDoubleWrapper) {
    onMutation();
    super.valueDoubleWrapper = valueDoubleWrapper;
    return this;
  }

  public Double valueDoubleWrapper() {
    return valueDoubleWrapper;
  }

  public DataBindingModelWithAllFieldTypesNoValidation_ valueFloat(float valueFloat) {
    onMutation();
    super.valueFloat = valueFloat;
    return this;
  }

  public float valueFloat() {
    return valueFloat;
  }

  public DataBindingModelWithAllFieldTypesNoValidation_ valueFloatWrapper(Float valueFloatWrapper) {
    onMutation();
    super.valueFloatWrapper = valueFloatWrapper;
    return this;
  }

  public Float valueFloatWrapper() {
    return valueFloatWrapper;
  }

  public DataBindingModelWithAllFieldTypesNoValidation_ valueBoolean(boolean valueBoolean) {
    onMutation();
    super.valueBoolean = valueBoolean;
    return this;
  }

  public boolean valueBoolean() {
    return valueBoolean;
  }

  public DataBindingModelWithAllFieldTypesNoValidation_ valueBooleanWrapper(
      Boolean valueBooleanWrapper) {
    onMutation();
    super.valueBooleanWrapper = valueBooleanWrapper;
    return this;
  }

  public Boolean valueBooleanWrapper() {
    return valueBooleanWrapper;
  }

  public DataBindingModelWithAllFieldTypesNoValidation_ valueIntArray(int[] valueIntArray) {
    onMutation();
    super.valueIntArray = valueIntArray;
    return this;
  }

  public int[] valueIntArray() {
    return valueIntArray;
  }

  public DataBindingModelWithAllFieldTypesNoValidation_ valueObjectArray(
      Object[] valueObjectArray) {
    onMutation();
    super.valueObjectArray = valueObjectArray;
    return this;
  }

  public Object[] valueObjectArray() {
    return valueObjectArray;
  }

  public DataBindingModelWithAllFieldTypesNoValidation_ valueString(String valueString) {
    onMutation();
    super.valueString = valueString;
    return this;
  }

  public String valueString() {
    return valueString;
  }

  public DataBindingModelWithAllFieldTypesNoValidation_ valueObject(Object valueObject) {
    onMutation();
    super.valueObject = valueObject;
    return this;
  }

  public Object valueObject() {
    return valueObject;
  }

  public DataBindingModelWithAllFieldTypesNoValidation_ valueList(List<String> valueList) {
    onMutation();
    super.valueList = valueList;
    return this;
  }

  public List<String> valueList() {
    return valueList;
  }

  @Override
  public DataBindingModelWithAllFieldTypesNoValidation_ id(long id) {
    super.id(id);
    return this;
  }

  @Override
  public DataBindingModelWithAllFieldTypesNoValidation_ id(@Nullable Number... ids) {
    super.id(ids);
    return this;
  }

  @Override
  public DataBindingModelWithAllFieldTypesNoValidation_ id(long id1, long id2) {
    super.id(id1, id2);
    return this;
  }

  @Override
  public DataBindingModelWithAllFieldTypesNoValidation_ id(@Nullable CharSequence key) {
    super.id(key);
    return this;
  }

  @Override
  public DataBindingModelWithAllFieldTypesNoValidation_ id(@Nullable CharSequence key,
      @Nullable CharSequence... otherKeys) {
    super.id(key, otherKeys);
    return this;
  }

  @Override
  public DataBindingModelWithAllFieldTypesNoValidation_ id(@Nullable CharSequence key, long id) {
    super.id(key, id);
    return this;
  }

  @Override
  public DataBindingModelWithAllFieldTypesNoValidation_ layout(@LayoutRes int layoutRes) {
    super.layout(layoutRes);
    return this;
  }

  @Override
  public DataBindingModelWithAllFieldTypesNoValidation_ spanSizeOverride(
      @Nullable EpoxyModel.SpanSizeOverrideCallback spanSizeCallback) {
    super.spanSizeOverride(spanSizeCallback);
    return this;
  }

  @Override
  public DataBindingModelWithAllFieldTypesNoValidation_ show() {
    super.show();
    return this;
  }

  @Override
  public DataBindingModelWithAllFieldTypesNoValidation_ show(boolean show) {
    super.show(show);
    return this;
  }

  @Override
  public DataBindingModelWithAllFieldTypesNoValidation_ hide() {
    super.hide();
    return this;
  }

  @Override
  protected void setDataBindingVariables(ViewDataBinding binding) {
    binding.setVariable(BR.valueInt, valueInt);
    binding.setVariable(BR.valueInteger, valueInteger);
    binding.setVariable(BR.valueShort, valueShort);
    binding.setVariable(BR.valueShortWrapper, valueShortWrapper);
    binding.setVariable(BR.valueChar, valueChar);
    binding.setVariable(BR.valueCharacter, valueCharacter);
    binding.setVariable(BR.valuebByte, valuebByte);
    binding.setVariable(BR.valueByteWrapper, valueByteWrapper);
    binding.setVariable(BR.valueLong, valueLong);
    binding.setVariable(BR.valueLongWrapper, valueLongWrapper);
    binding.setVariable(BR.valueDouble, valueDouble);
    binding.setVariable(BR.valueDoubleWrapper, valueDoubleWrapper);
    binding.setVariable(BR.valueFloat, valueFloat);
    binding.setVariable(BR.valueFloatWrapper, valueFloatWrapper);
    binding.setVariable(BR.valueBoolean, valueBoolean);
    binding.setVariable(BR.valueBooleanWrapper, valueBooleanWrapper);
    binding.setVariable(BR.valueIntArray, valueIntArray);
    binding.setVariable(BR.valueObjectArray, valueObjectArray);
    binding.setVariable(BR.valueString, valueString);
    binding.setVariable(BR.valueObject, valueObject);
    binding.setVariable(BR.valueList, valueList);
  }

  @Override
  protected void setDataBindingVariables(ViewDataBinding binding, EpoxyModel previousModel) {
    if (!(previousModel instanceof DataBindingModelWithAllFieldTypesNoValidation_)) {
      setDataBindingVariables(binding);
      return;
    }
    DataBindingModelWithAllFieldTypesNoValidation_ that = (DataBindingModelWithAllFieldTypesNoValidation_) previousModel;
    if ((valueInt != that.valueInt)) {
      binding.setVariable(BR.valueInt, valueInt);
    }
    if ((valueInteger != null ? !valueInteger.equals(that.valueInteger) : that.valueInteger != null)) {
      binding.setVariable(BR.valueInteger, valueInteger);
    }
    if ((valueShort != that.valueShort)) {
      binding.setVariable(BR.valueShort, valueShort);
    }
    if ((valueShortWrapper != null ? !valueShortWrapper.equals(that.valueShortWrapper) : that.valueShortWrapper != null)) {
      binding.setVariable(BR.valueShortWrapper, valueShortWrapper);
    }
    if ((valueChar != that.valueChar)) {
      binding.setVariable(BR.valueChar, valueChar);
    }
    if ((valueCharacter != null ? !valueCharacter.equals(that.valueCharacter) : that.valueCharacter != null)) {
      binding.setVariable(BR.valueCharacter, valueCharacter);
    }
    if ((valuebByte != that.valuebByte)) {
      binding.setVariable(BR.valuebByte, valuebByte);
    }
    if ((valueByteWrapper != null ? !valueByteWrapper.equals(that.valueByteWrapper) : that.valueByteWrapper != null)) {
      binding.setVariable(BR.valueByteWrapper, valueByteWrapper);
    }
    if ((valueLong != that.valueLong)) {
      binding.setVariable(BR.valueLong, valueLong);
    }
    if ((valueLongWrapper != null ? !valueLongWrapper.equals(that.valueLongWrapper) : that.valueLongWrapper != null)) {
      binding.setVariable(BR.valueLongWrapper, valueLongWrapper);
    }
    if ((Double.compare(that.valueDouble, valueDouble) != 0)) {
      binding.setVariable(BR.valueDouble, valueDouble);
    }
    if ((valueDoubleWrapper != null ? !valueDoubleWrapper.equals(that.valueDoubleWrapper) : that.valueDoubleWrapper != null)) {
      binding.setVariable(BR.valueDoubleWrapper, valueDoubleWrapper);
    }
    if ((Float.compare(that.valueFloat, valueFloat) != 0)) {
      binding.setVariable(BR.valueFloat, valueFloat);
    }
    if ((valueFloatWrapper != null ? !valueFloatWrapper.equals(that.valueFloatWrapper) : that.valueFloatWrapper != null)) {
      binding.setVariable(BR.valueFloatWrapper, valueFloatWrapper);
    }
    if ((valueBoolean != that.valueBoolean)) {
      binding.setVariable(BR.valueBoolean, valueBoolean);
    }
    if ((valueBooleanWrapper != null ? !valueBooleanWrapper.equals(that.valueBooleanWrapper) : that.valueBooleanWrapper != null)) {
      binding.setVariable(BR.valueBooleanWrapper, valueBooleanWrapper);
    }
    if (!Arrays.equals(valueIntArray, that.valueIntArray)) {
      binding.setVariable(BR.valueIntArray, valueIntArray);
    }
    if (!Arrays.equals(valueObjectArray, that.valueObjectArray)) {
      binding.setVariable(BR.valueObjectArray, valueObjectArray);
    }
    if ((valueString != null ? !valueString.equals(that.valueString) : that.valueString != null)) {
      binding.setVariable(BR.valueString, valueString);
    }
    if ((valueObject != null ? !valueObject.equals(that.valueObject) : that.valueObject != null)) {
      binding.setVariable(BR.valueObject, valueObject);
    }
    if ((valueList != null ? !valueList.equals(that.valueList) : that.valueList != null)) {
      binding.setVariable(BR.valueList, valueList);
    }
  }

  @Override
  public DataBindingModelWithAllFieldTypesNoValidation_ reset() {
    onModelBoundListener_epoxyGeneratedModel = null;
    onModelUnboundListener_epoxyGeneratedModel = null;
    onModelVisibilityStateChangedListener_epoxyGeneratedModel = null;
    onModelVisibilityChangedListener_epoxyGeneratedModel = null;
    super.valueInt = 0;
    super.valueInteger = null;
    super.valueShort = (short) 0;
    super.valueShortWrapper = null;
    super.valueChar = (char) 0;
    super.valueCharacter = null;
    super.valuebByte = (byte) 0;
    super.valueByteWrapper = null;
    super.valueLong = 0L;
    super.valueLongWrapper = null;
    super.valueDouble = 0.0d;
    super.valueDoubleWrapper = null;
    super.valueFloat = 0.0f;
    super.valueFloatWrapper = null;
    super.valueBoolean = false;
    super.valueBooleanWrapper = null;
    super.valueIntArray = null;
    super.valueObjectArray = null;
    super.valueString = null;
    super.valueObject = null;
    super.valueList = null;
    super.reset();
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof DataBindingModelWithAllFieldTypesNoValidation_)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    DataBindingModelWithAllFieldTypesNoValidation_ that = (DataBindingModelWithAllFieldTypesNoValidation_) o;
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
    if ((valueInt != that.valueInt)) {
      return false;
    }
    if ((valueInteger != null ? !valueInteger.equals(that.valueInteger) : that.valueInteger != null)) {
      return false;
    }
    if ((valueShort != that.valueShort)) {
      return false;
    }
    if ((valueShortWrapper != null ? !valueShortWrapper.equals(that.valueShortWrapper) : that.valueShortWrapper != null)) {
      return false;
    }
    if ((valueChar != that.valueChar)) {
      return false;
    }
    if ((valueCharacter != null ? !valueCharacter.equals(that.valueCharacter) : that.valueCharacter != null)) {
      return false;
    }
    if ((valuebByte != that.valuebByte)) {
      return false;
    }
    if ((valueByteWrapper != null ? !valueByteWrapper.equals(that.valueByteWrapper) : that.valueByteWrapper != null)) {
      return false;
    }
    if ((valueLong != that.valueLong)) {
      return false;
    }
    if ((valueLongWrapper != null ? !valueLongWrapper.equals(that.valueLongWrapper) : that.valueLongWrapper != null)) {
      return false;
    }
    if ((Double.compare(that.valueDouble, valueDouble) != 0)) {
      return false;
    }
    if ((valueDoubleWrapper != null ? !valueDoubleWrapper.equals(that.valueDoubleWrapper) : that.valueDoubleWrapper != null)) {
      return false;
    }
    if ((Float.compare(that.valueFloat, valueFloat) != 0)) {
      return false;
    }
    if ((valueFloatWrapper != null ? !valueFloatWrapper.equals(that.valueFloatWrapper) : that.valueFloatWrapper != null)) {
      return false;
    }
    if ((valueBoolean != that.valueBoolean)) {
      return false;
    }
    if ((valueBooleanWrapper != null ? !valueBooleanWrapper.equals(that.valueBooleanWrapper) : that.valueBooleanWrapper != null)) {
      return false;
    }
    if (!Arrays.equals(valueIntArray, that.valueIntArray)) {
      return false;
    }
    if (!Arrays.equals(valueObjectArray, that.valueObjectArray)) {
      return false;
    }
    if ((valueString != null ? !valueString.equals(that.valueString) : that.valueString != null)) {
      return false;
    }
    if ((valueObject != null ? !valueObject.equals(that.valueObject) : that.valueObject != null)) {
      return false;
    }
    if ((valueList != null ? !valueList.equals(that.valueList) : that.valueList != null)) {
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
    _result = 31 * _result + valueInt;
    _result = 31 * _result + (valueInteger != null ? valueInteger.hashCode() : 0);
    _result = 31 * _result + valueShort;
    _result = 31 * _result + (valueShortWrapper != null ? valueShortWrapper.hashCode() : 0);
    _result = 31 * _result + valueChar;
    _result = 31 * _result + (valueCharacter != null ? valueCharacter.hashCode() : 0);
    _result = 31 * _result + valuebByte;
    _result = 31 * _result + (valueByteWrapper != null ? valueByteWrapper.hashCode() : 0);
    _result = 31 * _result + (int) (valueLong ^ (valueLong >>> 32));
    _result = 31 * _result + (valueLongWrapper != null ? valueLongWrapper.hashCode() : 0);
    temp = Double.doubleToLongBits(valueDouble);
    _result = 31 * _result + (int) (temp ^ (temp >>> 32));
    _result = 31 * _result + (valueDoubleWrapper != null ? valueDoubleWrapper.hashCode() : 0);
    _result = 31 * _result + (valueFloat != +0.0f ? Float.floatToIntBits(valueFloat) : 0);
    _result = 31 * _result + (valueFloatWrapper != null ? valueFloatWrapper.hashCode() : 0);
    _result = 31 * _result + (valueBoolean ? 1 : 0);
    _result = 31 * _result + (valueBooleanWrapper != null ? valueBooleanWrapper.hashCode() : 0);
    _result = 31 * _result + Arrays.hashCode(valueIntArray);
    _result = 31 * _result + Arrays.hashCode(valueObjectArray);
    _result = 31 * _result + (valueString != null ? valueString.hashCode() : 0);
    _result = 31 * _result + (valueObject != null ? valueObject.hashCode() : 0);
    _result = 31 * _result + (valueList != null ? valueList.hashCode() : 0);
    return _result;
  }

  @Override
  public String toString() {
    return "DataBindingModelWithAllFieldTypesNoValidation_{" +
        "valueInt=" + valueInt +
        ", valueInteger=" + valueInteger +
        ", valueShort=" + valueShort +
        ", valueShortWrapper=" + valueShortWrapper +
        ", valueChar=" + valueChar +
        ", valueCharacter=" + valueCharacter +
        ", valuebByte=" + valuebByte +
        ", valueByteWrapper=" + valueByteWrapper +
        ", valueLong=" + valueLong +
        ", valueLongWrapper=" + valueLongWrapper +
        ", valueDouble=" + valueDouble +
        ", valueDoubleWrapper=" + valueDoubleWrapper +
        ", valueFloat=" + valueFloat +
        ", valueFloatWrapper=" + valueFloatWrapper +
        ", valueBoolean=" + valueBoolean +
        ", valueBooleanWrapper=" + valueBooleanWrapper +
        ", valueIntArray=" + valueIntArray +
        ", valueObjectArray=" + valueObjectArray +
        ", valueString=" + valueString +
        ", valueObject=" + valueObject +
        ", valueList=" + valueList +
        "}" + super.toString();
  }
}

