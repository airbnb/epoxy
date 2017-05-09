package com.airbnb.epoxy;

import android.support.annotation.LayoutRes;
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
public class ModelWithAllFieldTypes_ extends ModelWithAllFieldTypes implements GeneratedModel<Object> {
  private OnModelBoundListener<ModelWithAllFieldTypes_, Object> onModelBoundListener_epoxyGeneratedModel;

  private OnModelUnboundListener<ModelWithAllFieldTypes_, Object> onModelUnboundListener_epoxyGeneratedModel;

  public ModelWithAllFieldTypes_() {
    super();
  }

  @Override
  public void addTo(EpoxyController controller) {
    super.addTo(controller);
    addWithDebugValidation(controller);
  }

  @Override
  public void handlePreBind(final EpoxyViewHolder holder, final Object object, int position) {
    validateStateHasNotChangedSinceAdded("The model was changed between being added to the controller and being bound.", position);
  }

  @Override
  public void handlePostBind(final Object object, int position) {
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
  public ModelWithAllFieldTypes_ onBind(OnModelBoundListener<ModelWithAllFieldTypes_, Object> listener) {
    onMutation();
    this.onModelBoundListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public void unbind(Object object) {
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
  public ModelWithAllFieldTypes_ onUnbind(OnModelUnboundListener<ModelWithAllFieldTypes_, Object> listener) {
    onMutation();
    this.onModelUnboundListener_epoxyGeneratedModel = listener;
    return this;
  }

  public ModelWithAllFieldTypes_ valueInteger(Integer valueInteger) {
    onMutation();
    super.valueInteger = valueInteger;
    return this;
  }

  public Integer valueInteger() {
    return valueInteger;
  }

  public ModelWithAllFieldTypes_ valueShort(short valueShort) {
    onMutation();
    super.valueShort = valueShort;
    return this;
  }

  public short valueShort() {
    return valueShort;
  }

  public ModelWithAllFieldTypes_ valueLong(long valueLong) {
    onMutation();
    super.valueLong = valueLong;
    return this;
  }

  public long valueLong() {
    return valueLong;
  }

  public ModelWithAllFieldTypes_ valueList(List<String> valueList) {
    onMutation();
    super.valueList = valueList;
    return this;
  }

  public List<String> valueList() {
    return valueList;
  }

  public ModelWithAllFieldTypes_ valueShortWrapper(Short valueShortWrapper) {
    onMutation();
    super.valueShortWrapper = valueShortWrapper;
    return this;
  }

  public Short valueShortWrapper() {
    return valueShortWrapper;
  }

  public ModelWithAllFieldTypes_ valueDouble(double valueDouble) {
    onMutation();
    super.valueDouble = valueDouble;
    return this;
  }

  public double valueDouble() {
    return valueDouble;
  }

  public ModelWithAllFieldTypes_ valueChar(char valueChar) {
    onMutation();
    super.valueChar = valueChar;
    return this;
  }

  public char valueChar() {
    return valueChar;
  }

  public ModelWithAllFieldTypes_ valueInt(int valueInt) {
    onMutation();
    super.valueInt = valueInt;
    return this;
  }

  public int valueInt() {
    return valueInt;
  }

  public ModelWithAllFieldTypes_ valueDoubleWrapper(Double valueDoubleWrapper) {
    onMutation();
    super.valueDoubleWrapper = valueDoubleWrapper;
    return this;
  }

  public Double valueDoubleWrapper() {
    return valueDoubleWrapper;
  }

  public ModelWithAllFieldTypes_ valueFloatWrapper(Float valueFloatWrapper) {
    onMutation();
    super.valueFloatWrapper = valueFloatWrapper;
    return this;
  }

  public Float valueFloatWrapper() {
    return valueFloatWrapper;
  }

  public ModelWithAllFieldTypes_ valueBooleanWrapper(Boolean valueBooleanWrapper) {
    onMutation();
    super.valueBooleanWrapper = valueBooleanWrapper;
    return this;
  }

  public Boolean valueBooleanWrapper() {
    return valueBooleanWrapper;
  }

  public ModelWithAllFieldTypes_ valueByteWrapper(Byte valueByteWrapper) {
    onMutation();
    super.valueByteWrapper = valueByteWrapper;
    return this;
  }

  public Byte valueByteWrapper() {
    return valueByteWrapper;
  }

  public ModelWithAllFieldTypes_ valuebByte(byte valuebByte) {
    onMutation();
    super.valuebByte = valuebByte;
    return this;
  }

  public byte valuebByte() {
    return valuebByte;
  }

  public ModelWithAllFieldTypes_ valueLongWrapper(Long valueLongWrapper) {
    onMutation();
    super.valueLongWrapper = valueLongWrapper;
    return this;
  }

  public Long valueLongWrapper() {
    return valueLongWrapper;
  }

  public ModelWithAllFieldTypes_ valueCharacter(Character valueCharacter) {
    onMutation();
    super.valueCharacter = valueCharacter;
    return this;
  }

  public Character valueCharacter() {
    return valueCharacter;
  }

  public ModelWithAllFieldTypes_ valueString(String valueString) {
    onMutation();
    super.valueString = valueString;
    return this;
  }

  public String valueString() {
    return valueString;
  }

  public ModelWithAllFieldTypes_ valueFloat(float valueFloat) {
    onMutation();
    super.valueFloat = valueFloat;
    return this;
  }

  public float valueFloat() {
    return valueFloat;
  }

  public ModelWithAllFieldTypes_ valueBoolean(boolean valueBoolean) {
    onMutation();
    super.valueBoolean = valueBoolean;
    return this;
  }

  public boolean valueBoolean() {
    return valueBoolean;
  }

  public ModelWithAllFieldTypes_ valueObjectArray(Object[] valueObjectArray) {
    onMutation();
    super.valueObjectArray = valueObjectArray;
    return this;
  }

  public Object[] valueObjectArray() {
    return valueObjectArray;
  }

  public ModelWithAllFieldTypes_ valueObject(Object valueObject) {
    onMutation();
    super.valueObject = valueObject;
    return this;
  }

  public Object valueObject() {
    return valueObject;
  }

  public ModelWithAllFieldTypes_ valueIntArray(int[] valueIntArray) {
    onMutation();
    super.valueIntArray = valueIntArray;
    return this;
  }

  public int[] valueIntArray() {
    return valueIntArray;
  }

  @Override
  public ModelWithAllFieldTypes_ id(long id) {
    super.id(id);
    return this;
  }

  @Override
  public ModelWithAllFieldTypes_ id(Number... ids) {
    super.id(ids);
    return this;
  }

  @Override
  public ModelWithAllFieldTypes_ id(long id1, long id2) {
    super.id(id1, id2);
    return this;
  }

  @Override
  public ModelWithAllFieldTypes_ id(CharSequence key) {
    super.id(key);
    return this;
  }

  @Override
  public ModelWithAllFieldTypes_ id(CharSequence key, long id) {
    super.id(key, id);
    return this;
  }

  @Override
  public ModelWithAllFieldTypes_ layout(@LayoutRes int arg0) {
    super.layout(arg0);
    return this;
  }

  @Override
  public ModelWithAllFieldTypes_ show() {
    super.show();
    return this;
  }

  @Override
  public ModelWithAllFieldTypes_ show(boolean show) {
    super.show(show);
    return this;
  }

  @Override
  public ModelWithAllFieldTypes_ hide() {
    super.hide();
    return this;
  }

  @Override
  public ModelWithAllFieldTypes_ reset() {
    onModelBoundListener_epoxyGeneratedModel = null;
    onModelUnboundListener_epoxyGeneratedModel = null;
    super.valueInteger = null;
    super.valueShort = (short) 0;
    super.valueLong = 0L;
    super.valueList = null;
    super.valueShortWrapper = null;
    super.valueDouble = 0.0d;
    super.valueChar = (char) 0;
    super.valueInt = 0;
    super.valueDoubleWrapper = null;
    super.valueFloatWrapper = null;
    super.valueBooleanWrapper = null;
    super.valueByteWrapper = null;
    super.valuebByte = (byte) 0;
    super.valueLongWrapper = null;
    super.valueCharacter = null;
    super.valueString = null;
    super.valueFloat = 0.0f;
    super.valueBoolean = false;
    super.valueObjectArray = null;
    super.valueObject = null;
    super.valueIntArray = null;
    super.reset();
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof ModelWithAllFieldTypes_)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    ModelWithAllFieldTypes_ that = (ModelWithAllFieldTypes_) o;
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
    return "ModelWithAllFieldTypes_{" +
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