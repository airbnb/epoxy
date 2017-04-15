package com.airbnb.epoxy;

import android.databinding.ViewDataBinding;
import android.support.annotation.LayoutRes;
import java.lang.Boolean;
import java.lang.Byte;
import java.lang.CharSequence;
import java.lang.Character;
import java.lang.Double;
import java.lang.Float;
import java.lang.IllegalStateException;
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
public class DataBindingModelWithAllFieldTypes_ extends DataBindingModelWithAllFieldTypes implements GeneratedModel<DataBindingEpoxyModel.DataBindingHolder> {
  private OnModelBoundListener<DataBindingModelWithAllFieldTypes_, DataBindingEpoxyModel.DataBindingHolder> onModelBoundListener_epoxyGeneratedModel;

  private OnModelUnboundListener<DataBindingModelWithAllFieldTypes_, DataBindingEpoxyModel.DataBindingHolder> onModelUnboundListener_epoxyGeneratedModel;

  public DataBindingModelWithAllFieldTypes_() {
    super();
  }

  @Override
  public void addTo(EpoxyController controller) {
    super.addTo(controller);
    addWithDebugValidation(controller);
  }

  @Override
  public void handlePreBind(final EpoxyViewHolder holder,
      final DataBindingEpoxyModel.DataBindingHolder object, int position) {
    validateStateHasNotChangedSinceAdded("The model was changed between being added to the controller and being bound.", position);
  }

  @Override
  public void handlePostBind(final DataBindingEpoxyModel.DataBindingHolder object, int position) {
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
  public DataBindingModelWithAllFieldTypes_ onBind(OnModelBoundListener<DataBindingModelWithAllFieldTypes_, DataBindingEpoxyModel.DataBindingHolder> listener) {
    validateMutability();
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
  public DataBindingModelWithAllFieldTypes_ onUnbind(OnModelUnboundListener<DataBindingModelWithAllFieldTypes_, DataBindingEpoxyModel.DataBindingHolder> listener) {
    validateMutability();
    this.onModelUnboundListener_epoxyGeneratedModel = listener;
    return this;
  }

  public DataBindingModelWithAllFieldTypes_ valueInteger(Integer valueInteger) {
    validateMutability();
    this.valueInteger = valueInteger;
    return this;
  }

  public Integer valueInteger() {
    return valueInteger;
  }

  public DataBindingModelWithAllFieldTypes_ valueShort(short valueShort) {
    validateMutability();
    this.valueShort = valueShort;
    return this;
  }

  public short valueShort() {
    return valueShort;
  }

  public DataBindingModelWithAllFieldTypes_ valueLong(long valueLong) {
    validateMutability();
    this.valueLong = valueLong;
    return this;
  }

  public long valueLong() {
    return valueLong;
  }

  public DataBindingModelWithAllFieldTypes_ valueList(List<String> valueList) {
    validateMutability();
    this.valueList = valueList;
    return this;
  }

  public List<String> valueList() {
    return valueList;
  }

  public DataBindingModelWithAllFieldTypes_ valueShortWrapper(Short valueShortWrapper) {
    validateMutability();
    this.valueShortWrapper = valueShortWrapper;
    return this;
  }

  public Short valueShortWrapper() {
    return valueShortWrapper;
  }

  public DataBindingModelWithAllFieldTypes_ valueDouble(double valueDouble) {
    validateMutability();
    this.valueDouble = valueDouble;
    return this;
  }

  public double valueDouble() {
    return valueDouble;
  }

  public DataBindingModelWithAllFieldTypes_ valueChar(char valueChar) {
    validateMutability();
    this.valueChar = valueChar;
    return this;
  }

  public char valueChar() {
    return valueChar;
  }

  public DataBindingModelWithAllFieldTypes_ valueInt(int valueInt) {
    validateMutability();
    this.valueInt = valueInt;
    return this;
  }

  public int valueInt() {
    return valueInt;
  }

  public DataBindingModelWithAllFieldTypes_ valueDoubleWrapper(Double valueDoubleWrapper) {
    validateMutability();
    this.valueDoubleWrapper = valueDoubleWrapper;
    return this;
  }

  public Double valueDoubleWrapper() {
    return valueDoubleWrapper;
  }

  public DataBindingModelWithAllFieldTypes_ valueFloatWrapper(Float valueFloatWrapper) {
    validateMutability();
    this.valueFloatWrapper = valueFloatWrapper;
    return this;
  }

  public Float valueFloatWrapper() {
    return valueFloatWrapper;
  }

  public DataBindingModelWithAllFieldTypes_ valueBooleanWrapper(Boolean valueBooleanWrapper) {
    validateMutability();
    this.valueBooleanWrapper = valueBooleanWrapper;
    return this;
  }

  public Boolean valueBooleanWrapper() {
    return valueBooleanWrapper;
  }

  public DataBindingModelWithAllFieldTypes_ valueByteWrapper(Byte valueByteWrapper) {
    validateMutability();
    this.valueByteWrapper = valueByteWrapper;
    return this;
  }

  public Byte valueByteWrapper() {
    return valueByteWrapper;
  }

  public DataBindingModelWithAllFieldTypes_ valuebByte(byte valuebByte) {
    validateMutability();
    this.valuebByte = valuebByte;
    return this;
  }

  public byte valuebByte() {
    return valuebByte;
  }

  public DataBindingModelWithAllFieldTypes_ valueLongWrapper(Long valueLongWrapper) {
    validateMutability();
    this.valueLongWrapper = valueLongWrapper;
    return this;
  }

  public Long valueLongWrapper() {
    return valueLongWrapper;
  }

  public DataBindingModelWithAllFieldTypes_ valueCharacter(Character valueCharacter) {
    validateMutability();
    this.valueCharacter = valueCharacter;
    return this;
  }

  public Character valueCharacter() {
    return valueCharacter;
  }

  public DataBindingModelWithAllFieldTypes_ valueString(String valueString) {
    validateMutability();
    this.valueString = valueString;
    return this;
  }

  public String valueString() {
    return valueString;
  }

  public DataBindingModelWithAllFieldTypes_ valueFloat(float valueFloat) {
    validateMutability();
    this.valueFloat = valueFloat;
    return this;
  }

  public float valueFloat() {
    return valueFloat;
  }

  public DataBindingModelWithAllFieldTypes_ valueBoolean(boolean valueBoolean) {
    validateMutability();
    this.valueBoolean = valueBoolean;
    return this;
  }

  public boolean valueBoolean() {
    return valueBoolean;
  }

  public DataBindingModelWithAllFieldTypes_ valueObjectArray(Object[] valueObjectArray) {
    validateMutability();
    this.valueObjectArray = valueObjectArray;
    return this;
  }

  public Object[] valueObjectArray() {
    return valueObjectArray;
  }

  public DataBindingModelWithAllFieldTypes_ valueObject(Object valueObject) {
    validateMutability();
    this.valueObject = valueObject;
    return this;
  }

  public Object valueObject() {
    return valueObject;
  }

  public DataBindingModelWithAllFieldTypes_ valueIntArray(int[] valueIntArray) {
    validateMutability();
    this.valueIntArray = valueIntArray;
    return this;
  }

  public int[] valueIntArray() {
    return valueIntArray;
  }

  @Override
  public DataBindingModelWithAllFieldTypes_ id(long id) {
    super.id(id);
    return this;
  }

  @Override
  public DataBindingModelWithAllFieldTypes_ id(Number... ids) {
    super.id(ids);
    return this;
  }

  @Override
  public DataBindingModelWithAllFieldTypes_ id(long id1, long id2) {
    super.id(id1, id2);
    return this;
  }

  @Override
  public DataBindingModelWithAllFieldTypes_ id(CharSequence key) {
    super.id(key);
    return this;
  }

  @Override
  public DataBindingModelWithAllFieldTypes_ id(CharSequence key, long id) {
    super.id(key, id);
    return this;
  }

  @Override
  public DataBindingModelWithAllFieldTypes_ layout(@LayoutRes int arg0) {
    super.layout(arg0);
    return this;
  }

  @Override
  public DataBindingModelWithAllFieldTypes_ show() {
    super.show();
    return this;
  }

  @Override
  public DataBindingModelWithAllFieldTypes_ show(boolean show) {
    super.show(show);
    return this;
  }

  @Override
  public DataBindingModelWithAllFieldTypes_ hide() {
    super.hide();
    return this;
  }

  @Override
  protected void setDataBindingVariables(ViewDataBinding binding) {
    if (!binding.setVariable(BR.valueInteger, valueInteger)) {
      throw new IllegalStateException("The attribute valueInteger was defined in your data binding model (com.airbnb.epoxy.DataBindingModelWithAllFieldTypes) but a data variable of that name was not found in the layout.");
    }
    if (!binding.setVariable(BR.valueShort, valueShort)) {
      throw new IllegalStateException("The attribute valueShort was defined in your data binding model (com.airbnb.epoxy.DataBindingModelWithAllFieldTypes) but a data variable of that name was not found in the layout.");
    }
    if (!binding.setVariable(BR.valueLong, valueLong)) {
      throw new IllegalStateException("The attribute valueLong was defined in your data binding model (com.airbnb.epoxy.DataBindingModelWithAllFieldTypes) but a data variable of that name was not found in the layout.");
    }
    if (!binding.setVariable(BR.valueList, valueList)) {
      throw new IllegalStateException("The attribute valueList was defined in your data binding model (com.airbnb.epoxy.DataBindingModelWithAllFieldTypes) but a data variable of that name was not found in the layout.");
    }
    if (!binding.setVariable(BR.valueShortWrapper, valueShortWrapper)) {
      throw new IllegalStateException("The attribute valueShortWrapper was defined in your data binding model (com.airbnb.epoxy.DataBindingModelWithAllFieldTypes) but a data variable of that name was not found in the layout.");
    }
    if (!binding.setVariable(BR.valueDouble, valueDouble)) {
      throw new IllegalStateException("The attribute valueDouble was defined in your data binding model (com.airbnb.epoxy.DataBindingModelWithAllFieldTypes) but a data variable of that name was not found in the layout.");
    }
    if (!binding.setVariable(BR.valueChar, valueChar)) {
      throw new IllegalStateException("The attribute valueChar was defined in your data binding model (com.airbnb.epoxy.DataBindingModelWithAllFieldTypes) but a data variable of that name was not found in the layout.");
    }
    if (!binding.setVariable(BR.valueInt, valueInt)) {
      throw new IllegalStateException("The attribute valueInt was defined in your data binding model (com.airbnb.epoxy.DataBindingModelWithAllFieldTypes) but a data variable of that name was not found in the layout.");
    }
    if (!binding.setVariable(BR.valueDoubleWrapper, valueDoubleWrapper)) {
      throw new IllegalStateException("The attribute valueDoubleWrapper was defined in your data binding model (com.airbnb.epoxy.DataBindingModelWithAllFieldTypes) but a data variable of that name was not found in the layout.");
    }
    if (!binding.setVariable(BR.valueFloatWrapper, valueFloatWrapper)) {
      throw new IllegalStateException("The attribute valueFloatWrapper was defined in your data binding model (com.airbnb.epoxy.DataBindingModelWithAllFieldTypes) but a data variable of that name was not found in the layout.");
    }
    if (!binding.setVariable(BR.valueBooleanWrapper, valueBooleanWrapper)) {
      throw new IllegalStateException("The attribute valueBooleanWrapper was defined in your data binding model (com.airbnb.epoxy.DataBindingModelWithAllFieldTypes) but a data variable of that name was not found in the layout.");
    }
    if (!binding.setVariable(BR.valueByteWrapper, valueByteWrapper)) {
      throw new IllegalStateException("The attribute valueByteWrapper was defined in your data binding model (com.airbnb.epoxy.DataBindingModelWithAllFieldTypes) but a data variable of that name was not found in the layout.");
    }
    if (!binding.setVariable(BR.valuebByte, valuebByte)) {
      throw new IllegalStateException("The attribute valuebByte was defined in your data binding model (com.airbnb.epoxy.DataBindingModelWithAllFieldTypes) but a data variable of that name was not found in the layout.");
    }
    if (!binding.setVariable(BR.valueLongWrapper, valueLongWrapper)) {
      throw new IllegalStateException("The attribute valueLongWrapper was defined in your data binding model (com.airbnb.epoxy.DataBindingModelWithAllFieldTypes) but a data variable of that name was not found in the layout.");
    }
    if (!binding.setVariable(BR.valueCharacter, valueCharacter)) {
      throw new IllegalStateException("The attribute valueCharacter was defined in your data binding model (com.airbnb.epoxy.DataBindingModelWithAllFieldTypes) but a data variable of that name was not found in the layout.");
    }
    if (!binding.setVariable(BR.valueString, valueString)) {
      throw new IllegalStateException("The attribute valueString was defined in your data binding model (com.airbnb.epoxy.DataBindingModelWithAllFieldTypes) but a data variable of that name was not found in the layout.");
    }
    if (!binding.setVariable(BR.valueFloat, valueFloat)) {
      throw new IllegalStateException("The attribute valueFloat was defined in your data binding model (com.airbnb.epoxy.DataBindingModelWithAllFieldTypes) but a data variable of that name was not found in the layout.");
    }
    if (!binding.setVariable(BR.valueBoolean, valueBoolean)) {
      throw new IllegalStateException("The attribute valueBoolean was defined in your data binding model (com.airbnb.epoxy.DataBindingModelWithAllFieldTypes) but a data variable of that name was not found in the layout.");
    }
    if (!binding.setVariable(BR.valueObjectArray, valueObjectArray)) {
      throw new IllegalStateException("The attribute valueObjectArray was defined in your data binding model (com.airbnb.epoxy.DataBindingModelWithAllFieldTypes) but a data variable of that name was not found in the layout.");
    }
    if (!binding.setVariable(BR.valueObject, valueObject)) {
      throw new IllegalStateException("The attribute valueObject was defined in your data binding model (com.airbnb.epoxy.DataBindingModelWithAllFieldTypes) but a data variable of that name was not found in the layout.");
    }
    if (!binding.setVariable(BR.valueIntArray, valueIntArray)) {
      throw new IllegalStateException("The attribute valueIntArray was defined in your data binding model (com.airbnb.epoxy.DataBindingModelWithAllFieldTypes) but a data variable of that name was not found in the layout.");
    }
  }

  @Override
  protected void setDataBindingVariables(ViewDataBinding binding, EpoxyModel previousModel) {
    if (!(previousModel instanceof DataBindingModelWithAllFieldTypes_)) {
      setDataBindingVariables(binding);
      return;
    }
    DataBindingModelWithAllFieldTypes_ that = (DataBindingModelWithAllFieldTypes_) previousModel;
    if (valueInteger != null ? !valueInteger.equals(that.valueInteger) : that.valueInteger != null) {
      binding.setVariable(BR.valueInteger, valueInteger);
    }
    if (valueShort != that.valueShort) {
      binding.setVariable(BR.valueShort, valueShort);
    }
    if (valueLong != that.valueLong) {
      binding.setVariable(BR.valueLong, valueLong);
    }
    if (valueList != null ? !valueList.equals(that.valueList) : that.valueList != null) {
      binding.setVariable(BR.valueList, valueList);
    }
    if (valueShortWrapper != null ? !valueShortWrapper.equals(that.valueShortWrapper) : that.valueShortWrapper != null) {
      binding.setVariable(BR.valueShortWrapper, valueShortWrapper);
    }
    if (Double.compare(that.valueDouble, valueDouble) != 0) {
      binding.setVariable(BR.valueDouble, valueDouble);
    }
    if (valueChar != that.valueChar) {
      binding.setVariable(BR.valueChar, valueChar);
    }
    if (valueInt != that.valueInt) {
      binding.setVariable(BR.valueInt, valueInt);
    }
    if (valueDoubleWrapper != null ? !valueDoubleWrapper.equals(that.valueDoubleWrapper) : that.valueDoubleWrapper != null) {
      binding.setVariable(BR.valueDoubleWrapper, valueDoubleWrapper);
    }
    if (valueFloatWrapper != null ? !valueFloatWrapper.equals(that.valueFloatWrapper) : that.valueFloatWrapper != null) {
      binding.setVariable(BR.valueFloatWrapper, valueFloatWrapper);
    }
    if (valueBooleanWrapper != null ? !valueBooleanWrapper.equals(that.valueBooleanWrapper) : that.valueBooleanWrapper != null) {
      binding.setVariable(BR.valueBooleanWrapper, valueBooleanWrapper);
    }
    if (valueByteWrapper != null ? !valueByteWrapper.equals(that.valueByteWrapper) : that.valueByteWrapper != null) {
      binding.setVariable(BR.valueByteWrapper, valueByteWrapper);
    }
    if (valuebByte != that.valuebByte) {
      binding.setVariable(BR.valuebByte, valuebByte);
    }
    if (valueLongWrapper != null ? !valueLongWrapper.equals(that.valueLongWrapper) : that.valueLongWrapper != null) {
      binding.setVariable(BR.valueLongWrapper, valueLongWrapper);
    }
    if (valueCharacter != null ? !valueCharacter.equals(that.valueCharacter) : that.valueCharacter != null) {
      binding.setVariable(BR.valueCharacter, valueCharacter);
    }
    if (valueString != null ? !valueString.equals(that.valueString) : that.valueString != null) {
      binding.setVariable(BR.valueString, valueString);
    }
    if (Float.compare(that.valueFloat, valueFloat) != 0) {
      binding.setVariable(BR.valueFloat, valueFloat);
    }
    if (valueBoolean != that.valueBoolean) {
      binding.setVariable(BR.valueBoolean, valueBoolean);
    }
    if (!Arrays.equals(valueObjectArray, that.valueObjectArray)) {
      binding.setVariable(BR.valueObjectArray, valueObjectArray);
    }
    if (valueObject != null ? !valueObject.equals(that.valueObject) : that.valueObject != null) {
      binding.setVariable(BR.valueObject, valueObject);
    }
    if (!Arrays.equals(valueIntArray, that.valueIntArray)) {
      binding.setVariable(BR.valueIntArray, valueIntArray);
    }
  }

  @Override
  public DataBindingModelWithAllFieldTypes_ reset() {
    onModelBoundListener_epoxyGeneratedModel = null;
    onModelUnboundListener_epoxyGeneratedModel = null;
    this.valueInteger = null;
    this.valueShort = (short) 0;
    this.valueLong = 0L;
    this.valueList = null;
    this.valueShortWrapper = null;
    this.valueDouble = 0.0d;
    this.valueChar = (char) 0;
    this.valueInt = 0;
    this.valueDoubleWrapper = null;
    this.valueFloatWrapper = null;
    this.valueBooleanWrapper = null;
    this.valueByteWrapper = null;
    this.valuebByte = (byte) 0;
    this.valueLongWrapper = null;
    this.valueCharacter = null;
    this.valueString = null;
    this.valueFloat = 0.0f;
    this.valueBoolean = false;
    this.valueObjectArray = null;
    this.valueObject = null;
    this.valueIntArray = null;
    super.reset();
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof DataBindingModelWithAllFieldTypes_)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    DataBindingModelWithAllFieldTypes_ that = (DataBindingModelWithAllFieldTypes_) o;
    if ((onModelBoundListener_epoxyGeneratedModel == null) != (that.onModelBoundListener_epoxyGeneratedModel == null)) {
      return false;
    }
    if ((onModelUnboundListener_epoxyGeneratedModel == null) != (that.onModelUnboundListener_epoxyGeneratedModel == null)) {
      return false;
    }
    if (valueInteger != null ? !valueInteger.equals(that.valueInteger) : that.valueInteger != null) {
      return false;
    }
    if (valueShort != that.valueShort) {
      return false;
    }
    if (valueLong != that.valueLong) {
      return false;
    }
    if (valueList != null ? !valueList.equals(that.valueList) : that.valueList != null) {
      return false;
    }
    if (valueShortWrapper != null ? !valueShortWrapper.equals(that.valueShortWrapper) : that.valueShortWrapper != null) {
      return false;
    }
    if (Double.compare(that.valueDouble, valueDouble) != 0) {
      return false;
    }
    if (valueChar != that.valueChar) {
      return false;
    }
    if (valueInt != that.valueInt) {
      return false;
    }
    if (valueDoubleWrapper != null ? !valueDoubleWrapper.equals(that.valueDoubleWrapper) : that.valueDoubleWrapper != null) {
      return false;
    }
    if (valueFloatWrapper != null ? !valueFloatWrapper.equals(that.valueFloatWrapper) : that.valueFloatWrapper != null) {
      return false;
    }
    if (valueBooleanWrapper != null ? !valueBooleanWrapper.equals(that.valueBooleanWrapper) : that.valueBooleanWrapper != null) {
      return false;
    }
    if (valueByteWrapper != null ? !valueByteWrapper.equals(that.valueByteWrapper) : that.valueByteWrapper != null) {
      return false;
    }
    if (valuebByte != that.valuebByte) {
      return false;
    }
    if (valueLongWrapper != null ? !valueLongWrapper.equals(that.valueLongWrapper) : that.valueLongWrapper != null) {
      return false;
    }
    if (valueCharacter != null ? !valueCharacter.equals(that.valueCharacter) : that.valueCharacter != null) {
      return false;
    }
    if (valueString != null ? !valueString.equals(that.valueString) : that.valueString != null) {
      return false;
    }
    if (Float.compare(that.valueFloat, valueFloat) != 0) {
      return false;
    }
    if (valueBoolean != that.valueBoolean) {
      return false;
    }
    if (!Arrays.equals(valueObjectArray, that.valueObjectArray)) {
      return false;
    }
    if (valueObject != null ? !valueObject.equals(that.valueObject) : that.valueObject != null) {
      return false;
    }
    if (!Arrays.equals(valueIntArray, that.valueIntArray)) {
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
    result = 31 * result + (valueInteger != null ? valueInteger.hashCode() : 0);
    result = 31 * result + valueShort;
    result = 31 * result + (int) (valueLong ^ (valueLong >>> 32));
    result = 31 * result + (valueList != null ? valueList.hashCode() : 0);
    result = 31 * result + (valueShortWrapper != null ? valueShortWrapper.hashCode() : 0);
    temp = Double.doubleToLongBits(valueDouble);
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    result = 31 * result + valueChar;
    result = 31 * result + valueInt;
    result = 31 * result + (valueDoubleWrapper != null ? valueDoubleWrapper.hashCode() : 0);
    result = 31 * result + (valueFloatWrapper != null ? valueFloatWrapper.hashCode() : 0);
    result = 31 * result + (valueBooleanWrapper != null ? valueBooleanWrapper.hashCode() : 0);
    result = 31 * result + (valueByteWrapper != null ? valueByteWrapper.hashCode() : 0);
    result = 31 * result + valuebByte;
    result = 31 * result + (valueLongWrapper != null ? valueLongWrapper.hashCode() : 0);
    result = 31 * result + (valueCharacter != null ? valueCharacter.hashCode() : 0);
    result = 31 * result + (valueString != null ? valueString.hashCode() : 0);
    result = 31 * result + (valueFloat != +0.0f ? Float.floatToIntBits(valueFloat) : 0);
    result = 31 * result + (valueBoolean ? 1 : 0);
    result = 31 * result + Arrays.hashCode(valueObjectArray);
    result = 31 * result + (valueObject != null ? valueObject.hashCode() : 0);
    result = 31 * result + Arrays.hashCode(valueIntArray);
    return result;
  }

  @Override
  public String toString() {
    return "DataBindingModelWithAllFieldTypes_{" +
        "valueInteger=" + valueInteger +
        ", valueShort=" + valueShort +
        ", valueLong=" + valueLong +
        ", valueList=" + valueList +
        ", valueShortWrapper=" + valueShortWrapper +
        ", valueDouble=" + valueDouble +
        ", valueChar=" + valueChar +
        ", valueInt=" + valueInt +
        ", valueDoubleWrapper=" + valueDoubleWrapper +
        ", valueFloatWrapper=" + valueFloatWrapper +
        ", valueBooleanWrapper=" + valueBooleanWrapper +
        ", valueByteWrapper=" + valueByteWrapper +
        ", valuebByte=" + valuebByte +
        ", valueLongWrapper=" + valueLongWrapper +
        ", valueCharacter=" + valueCharacter +
        ", valueString=" + valueString +
        ", valueFloat=" + valueFloat +
        ", valueBoolean=" + valueBoolean +
        ", valueObjectArray=" + valueObjectArray +
        ", valueObject=" + valueObject +
        ", valueIntArray=" + valueIntArray +
        "}" + super.toString();
  }
}