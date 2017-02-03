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
import java.lang.Object;
import java.lang.Override;
import java.lang.Short;
import java.lang.String;
import java.util.Arrays;
import java.util.List;

/**
 * Generated file. Do not modify! */
public class ModelWithAllFieldTypes_ extends ModelWithAllFieldTypes {
  public ModelWithAllFieldTypes_() {
    super();
  }

  public ModelWithAllFieldTypes_ valueInteger(Integer valueInteger) {
    this.valueInteger = valueInteger;
    return this;
  }

  public Integer valueInteger() {
    return valueInteger;
  }

  public ModelWithAllFieldTypes_ valueShort(short valueShort) {
    this.valueShort = valueShort;
    return this;
  }

  public short valueShort() {
    return valueShort;
  }

  public ModelWithAllFieldTypes_ valueLong(long valueLong) {
    this.valueLong = valueLong;
    return this;
  }

  public long valueLong() {
    return valueLong;
  }

  public ModelWithAllFieldTypes_ valueList(List<String> valueList) {
    this.valueList = valueList;
    return this;
  }

  public List<String> valueList() {
    return valueList;
  }

  public ModelWithAllFieldTypes_ valueShortWrapper(Short valueShortWrapper) {
    this.valueShortWrapper = valueShortWrapper;
    return this;
  }

  public Short valueShortWrapper() {
    return valueShortWrapper;
  }

  public ModelWithAllFieldTypes_ valueDouble(double valueDouble) {
    this.valueDouble = valueDouble;
    return this;
  }

  public double valueDouble() {
    return valueDouble;
  }

  public ModelWithAllFieldTypes_ valueChar(char valueChar) {
    this.valueChar = valueChar;
    return this;
  }

  public char valueChar() {
    return valueChar;
  }

  public ModelWithAllFieldTypes_ valueInt(int valueInt) {
    this.valueInt = valueInt;
    return this;
  }

  public int valueInt() {
    return valueInt;
  }

  public ModelWithAllFieldTypes_ valueDoubleWrapper(Double valueDoubleWrapper) {
    this.valueDoubleWrapper = valueDoubleWrapper;
    return this;
  }

  public Double valueDoubleWrapper() {
    return valueDoubleWrapper;
  }

  public ModelWithAllFieldTypes_ valueFloatWrapper(Float valueFloatWrapper) {
    this.valueFloatWrapper = valueFloatWrapper;
    return this;
  }

  public Float valueFloatWrapper() {
    return valueFloatWrapper;
  }

  public ModelWithAllFieldTypes_ valueBooleanWrapper(Boolean valueBooleanWrapper) {
    this.valueBooleanWrapper = valueBooleanWrapper;
    return this;
  }

  public Boolean valueBooleanWrapper() {
    return valueBooleanWrapper;
  }

  public ModelWithAllFieldTypes_ valueByteWrapper(Byte valueByteWrapper) {
    this.valueByteWrapper = valueByteWrapper;
    return this;
  }

  public Byte valueByteWrapper() {
    return valueByteWrapper;
  }

  public ModelWithAllFieldTypes_ valuebByte(byte valuebByte) {
    this.valuebByte = valuebByte;
    return this;
  }

  public byte valuebByte() {
    return valuebByte;
  }

  public ModelWithAllFieldTypes_ valueLongWrapper(Long valueLongWrapper) {
    this.valueLongWrapper = valueLongWrapper;
    return this;
  }

  public Long valueLongWrapper() {
    return valueLongWrapper;
  }

  public ModelWithAllFieldTypes_ valueCharacter(Character valueCharacter) {
    this.valueCharacter = valueCharacter;
    return this;
  }

  public Character valueCharacter() {
    return valueCharacter;
  }

  public ModelWithAllFieldTypes_ valueString(String valueString) {
    this.valueString = valueString;
    return this;
  }

  public String valueString() {
    return valueString;
  }

  public ModelWithAllFieldTypes_ valueFloat(float valueFloat) {
    this.valueFloat = valueFloat;
    return this;
  }

  public float valueFloat() {
    return valueFloat;
  }

  public ModelWithAllFieldTypes_ valueBoolean(boolean valueBoolean) {
    this.valueBoolean = valueBoolean;
    return this;
  }

  public boolean valueBoolean() {
    return valueBoolean;
  }

  public ModelWithAllFieldTypes_ valueObjectArray(Object[] valueObjectArray) {
    this.valueObjectArray = valueObjectArray;
    return this;
  }

  public Object[] valueObjectArray() {
    return valueObjectArray;
  }

  public ModelWithAllFieldTypes_ valueObject(Object valueObject) {
    this.valueObject = valueObject;
    return this;
  }

  public Object valueObject() {
    return valueObject;
  }

  public ModelWithAllFieldTypes_ valueIntArray(int[] valueIntArray) {
    this.valueIntArray = valueIntArray;
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
    this.valueInteger = null;
    this.valueShort = 0;
    this.valueLong = 0L;
    this.valueList = null;
    this.valueShortWrapper = null;
    this.valueDouble = 0.0d;
    this.valueChar = 0;
    this.valueInt = 0;
    this.valueDoubleWrapper = null;
    this.valueFloatWrapper = null;
    this.valueBooleanWrapper = null;
    this.valueByteWrapper = null;
    this.valuebByte = 0;
    this.valueLongWrapper = null;
    this.valueCharacter = null;
    this.valueString = null;
    this.valueFloat = 0.0f;
    this.valueBoolean = false;
    this.valueObjectArray = null;
    this.valueObject = null;
    this.valueIntArray = null;
    super.reset();
    return this
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
