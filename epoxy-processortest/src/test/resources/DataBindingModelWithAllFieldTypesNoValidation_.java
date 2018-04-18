package com.airbnb.epoxy;

import android.databinding.ViewDataBinding;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
 * Generated file. Do not modify! */
public class DataBindingModelWithAllFieldTypesNoValidation_ extends DataBindingModelWithAllFieldTypesNoValidation implements GeneratedModel<DataBindingEpoxyModel.DataBindingHolder>, DataBindingModelWithAllFieldTypesNoValidationBuilder {
  private OnModelBoundListener<DataBindingModelWithAllFieldTypesNoValidation_, DataBindingEpoxyModel.DataBindingHolder> onModelBoundListener_epoxyGeneratedModel;

  private OnModelUnboundListener<DataBindingModelWithAllFieldTypesNoValidation_, DataBindingEpoxyModel.DataBindingHolder> onModelUnboundListener_epoxyGeneratedModel;

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
   * You may clear the listener by setting a null value, or by calling {@link #reset()} */
  public DataBindingModelWithAllFieldTypesNoValidation_ onBind(OnModelBoundListener<DataBindingModelWithAllFieldTypesNoValidation_, DataBindingEpoxyModel.DataBindingHolder> listener) {
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
   * You may clear the listener by setting a null value, or by calling {@link #reset()} */
  public DataBindingModelWithAllFieldTypesNoValidation_ onUnbind(OnModelUnboundListener<DataBindingModelWithAllFieldTypesNoValidation_, DataBindingEpoxyModel.DataBindingHolder> listener) {
    onMutation();
    this.onModelUnboundListener_epoxyGeneratedModel = listener;
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

  public DataBindingModelWithAllFieldTypesNoValidation_ valueDoubleWrapper(Double valueDoubleWrapper) {
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

  public DataBindingModelWithAllFieldTypesNoValidation_ valueBooleanWrapper(Boolean valueBooleanWrapper) {
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

  public DataBindingModelWithAllFieldTypesNoValidation_ valueObjectArray(Object[] valueObjectArray) {
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
  public DataBindingModelWithAllFieldTypesNoValidation_ id(@NonNull Number... arg0) {
    super.id(arg0);
    return this;
  }

  @Override
  public DataBindingModelWithAllFieldTypesNoValidation_ id(long id1, long id2) {
    super.id(id1, id2);
    return this;
  }

  @Override
  public DataBindingModelWithAllFieldTypesNoValidation_ id(@NonNull CharSequence arg0) {
    super.id(arg0);
    return this;
  }

  @Override
  public DataBindingModelWithAllFieldTypesNoValidation_ id(@NonNull CharSequence arg0,
      @NonNull CharSequence... arg1) {
    super.id(arg0, arg1);
    return this;
  }

  @Override
  public DataBindingModelWithAllFieldTypesNoValidation_ id(@NonNull CharSequence arg0, long arg1) {
    super.id(arg0, arg1);
    return this;
  }

  @Override
  public DataBindingModelWithAllFieldTypesNoValidation_ layout(@LayoutRes int arg0) {
    super.layout(arg0);
    return this;
  }

  @Override
  public DataBindingModelWithAllFieldTypesNoValidation_ spanSizeOverride(@Nullable EpoxyModel.SpanSizeOverrideCallback arg0) {
    super.spanSizeOverride(arg0);
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
    int result = super.hashCode();
    result = 31 * result + (onModelBoundListener_epoxyGeneratedModel != null ? 1 : 0);
    result = 31 * result + (onModelUnboundListener_epoxyGeneratedModel != null ? 1 : 0);
    long temp;
    result = 31 * result + valueInt;
    result = 31 * result + (valueInteger != null ? valueInteger.hashCode() : 0);
    result = 31 * result + valueShort;
    result = 31 * result + (valueShortWrapper != null ? valueShortWrapper.hashCode() : 0);
    result = 31 * result + valueChar;
    result = 31 * result + (valueCharacter != null ? valueCharacter.hashCode() : 0);
    result = 31 * result + valuebByte;
    result = 31 * result + (valueByteWrapper != null ? valueByteWrapper.hashCode() : 0);
    result = 31 * result + (int) (valueLong ^ (valueLong >>> 32));
    result = 31 * result + (valueLongWrapper != null ? valueLongWrapper.hashCode() : 0);
    temp = Double.doubleToLongBits(valueDouble);
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    result = 31 * result + (valueDoubleWrapper != null ? valueDoubleWrapper.hashCode() : 0);
    result = 31 * result + (valueFloat != +0.0f ? Float.floatToIntBits(valueFloat) : 0);
    result = 31 * result + (valueFloatWrapper != null ? valueFloatWrapper.hashCode() : 0);
    result = 31 * result + (valueBoolean ? 1 : 0);
    result = 31 * result + (valueBooleanWrapper != null ? valueBooleanWrapper.hashCode() : 0);
    result = 31 * result + Arrays.hashCode(valueIntArray);
    result = 31 * result + Arrays.hashCode(valueObjectArray);
    result = 31 * result + (valueString != null ? valueString.hashCode() : 0);
    result = 31 * result + (valueObject != null ? valueObject.hashCode() : 0);
    result = 31 * result + (valueList != null ? valueList.hashCode() : 0);
    return result;
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