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
public class ModelWithAllPrivateFieldTypes_ extends ModelWithAllPrivateFieldTypes implements GeneratedModel<Object> {
  private OnModelBoundListener<ModelWithAllPrivateFieldTypes_, Object> onModelBoundListener_epoxyGeneratedModel;

  private OnModelUnboundListener<ModelWithAllPrivateFieldTypes_, Object> onModelUnboundListener_epoxyGeneratedModel;

  public ModelWithAllPrivateFieldTypes_() {
    super();
  }

  @Override
  public void handlePreBind(final EpoxyViewHolder holder, final Object object) {
  }

  @Override
  public void handlePostBind(final EpoxyViewHolder holder, final Object object) {
    if (onModelBoundListener_epoxyGeneratedModel != null) {
      onModelBoundListener_epoxyGeneratedModel.onModelBound(this, object);
    }
  }

  /**
   * Register a listener that will be called when this model is bound to a view.
   * <p>
   * The listener will contribute to this model's hashCode state per the {@link
   * com.airbnb.epoxy.EpoxyAttribute.Option#DoNotHash} rules.
   * <p>
   * You may clear the listener by setting a null value, or by calling {@link #reset()} */
  public ModelWithAllPrivateFieldTypes_ onBind(OnModelBoundListener<ModelWithAllPrivateFieldTypes_, Object> listener) {
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
  public ModelWithAllPrivateFieldTypes_ onUnbind(OnModelUnboundListener<ModelWithAllPrivateFieldTypes_, Object> listener) {
    this.onModelUnboundListener_epoxyGeneratedModel = listener;
    return this;
  }

  public ModelWithAllPrivateFieldTypes_ valueInteger(Integer valueInteger) {
    this.setValueInteger(valueInteger);
    return this;
  }

  public Integer valueInteger() {
    return getValueInteger();
  }

  public ModelWithAllPrivateFieldTypes_ valueShort(short valueShort) {
    this.setValueShort(valueShort);
    return this;
  }

  public short valueShort() {
    return getValueShort();
  }

  public ModelWithAllPrivateFieldTypes_ valueLong(long valueLong) {
    this.setValueLong(valueLong);
    return this;
  }

  public long valueLong() {
    return getValueLong();
  }

  public ModelWithAllPrivateFieldTypes_ valueList(List<String> valueList) {
    this.setValueList(valueList);
    return this;
  }

  public List<String> valueList() {
    return getValueList();
  }

  public ModelWithAllPrivateFieldTypes_ valueShortWrapper(Short valueShortWrapper) {
    this.setValueShortWrapper(valueShortWrapper);
    return this;
  }

  public Short valueShortWrapper() {
    return getValueShortWrapper();
  }

  public ModelWithAllPrivateFieldTypes_ valueDouble(double valueDouble) {
    this.setValueDouble(valueDouble);
    return this;
  }

  public double valueDouble() {
    return getValueDouble();
  }

  public ModelWithAllPrivateFieldTypes_ valueChar(char valueChar) {
    this.setValueChar(valueChar);
    return this;
  }

  public char valueChar() {
    return getValueChar();
  }

  public ModelWithAllPrivateFieldTypes_ valueInt(int valueInt) {
    this.setValueInt(valueInt);
    return this;
  }

  public int valueInt() {
    return getValueInt();
  }

  public ModelWithAllPrivateFieldTypes_ valueDoubleWrapper(Double valueDoubleWrapper) {
    this.setValueDoubleWrapper(valueDoubleWrapper);
    return this;
  }

  public Double valueDoubleWrapper() {
    return getValueDoubleWrapper();
  }

  public ModelWithAllPrivateFieldTypes_ valueFloatWrapper(Float valueFloatWrapper) {
    this.setValueFloatWrapper(valueFloatWrapper);
    return this;
  }

  public Float valueFloatWrapper() {
    return getValueFloatWrapper();
  }

  public ModelWithAllPrivateFieldTypes_ valueBooleanWrapper(Boolean valueBooleanWrapper) {
    this.setValueBooleanWrapper(valueBooleanWrapper);
    return this;
  }

  public Boolean valueBooleanWrapper() {
    return getValueBooleanWrapper();
  }

  public ModelWithAllPrivateFieldTypes_ valueByteWrapper(Byte valueByteWrapper) {
    this.setValueByteWrapper(valueByteWrapper);
    return this;
  }

  public Byte valueByteWrapper() {
    return getValueByteWrapper();
  }

  public ModelWithAllPrivateFieldTypes_ valuebByte(byte valuebByte) {
    this.setValuebByte(valuebByte);
    return this;
  }

  public byte valuebByte() {
    return getValuebByte();
  }

  public ModelWithAllPrivateFieldTypes_ valueLongWrapper(Long valueLongWrapper) {
    this.setValueLongWrapper(valueLongWrapper);
    return this;
  }

  public Long valueLongWrapper() {
    return getValueLongWrapper();
  }

  public ModelWithAllPrivateFieldTypes_ valueCharacter(Character valueCharacter) {
    this.setValueCharacter(valueCharacter);
    return this;
  }

  public Character valueCharacter() {
    return getValueCharacter();
  }

  public ModelWithAllPrivateFieldTypes_ valueString(String valueString) {
    this.setValueString(valueString);
    return this;
  }

  public String valueString() {
    return getValueString();
  }

  public ModelWithAllPrivateFieldTypes_ valueFloat(float valueFloat) {
    this.setValueFloat(valueFloat);
    return this;
  }

  public float valueFloat() {
    return getValueFloat();
  }

  public ModelWithAllPrivateFieldTypes_ valueBoolean(boolean valueBoolean) {
    this.setValueBoolean(valueBoolean);
    return this;
  }

  public boolean valueBoolean() {
    return isValueBoolean();
  }

  public ModelWithAllPrivateFieldTypes_ valueObjectArray(Object[] valueObjectArray) {
    this.setValueObjectArray(valueObjectArray);
    return this;
  }

  public Object[] valueObjectArray() {
    return getValueObjectArray();
  }

  public ModelWithAllPrivateFieldTypes_ valueObject(Object valueObject) {
    this.setValueObject(valueObject);
    return this;
  }

  public Object valueObject() {
    return getValueObject();
  }

  public ModelWithAllPrivateFieldTypes_ valueIntArray(int[] valueIntArray) {
    this.setValueIntArray(valueIntArray);
    return this;
  }

  public int[] valueIntArray() {
    return getValueIntArray();
  }

  @Override
  public ModelWithAllPrivateFieldTypes_ getValueObject() {
    super.getValueObject();
    return this;
  }

  @Override
  public ModelWithAllPrivateFieldTypes_ id(long id) {
    super.id(id);
    return this;
  }

  @Override
  public ModelWithAllPrivateFieldTypes_ id(CharSequence key) {
    super.id(key);
    return this;
  }

  @Override
  public ModelWithAllPrivateFieldTypes_ id(CharSequence key, long id) {
    super.id(key, id);
    return this;
  }

  @Override
  public ModelWithAllPrivateFieldTypes_ layout(@LayoutRes int arg0) {
    super.layout(arg0);
    return this;
  }

  @Override
  public ModelWithAllPrivateFieldTypes_ show() {
    super.show();
    return this;
  }

  @Override
  public ModelWithAllPrivateFieldTypes_ show(boolean show) {
    super.show(show);
    return this;
  }

  @Override
  public ModelWithAllPrivateFieldTypes_ hide() {
    super.hide();
    return this;
  }

  @Override
  public ModelWithAllPrivateFieldTypes_ reset() {
    onModelBoundListener_epoxyGeneratedModel = null;
    onModelUnboundListener_epoxyGeneratedModel = null;
    this.setValueInteger(null);
    this.setValueShort((short) 0);
    this.setValueLong(0L);
    this.setValueList(null);
    this.setValueShortWrapper(null);
    this.setValueDouble(0.0d);
    this.setValueChar((char) 0);
    this.setValueInt(0);
    this.setValueDoubleWrapper(null);
    this.setValueFloatWrapper(null);
    this.setValueBooleanWrapper(null);
    this.setValueByteWrapper(null);
    this.setValuebByte((byte) 0);
    this.setValueLongWrapper(null);
    this.setValueCharacter(null);
    this.setValueString(null);
    this.setValueFloat(0.0f);
    this.setValueBoolean(false);
    this.setValueObjectArray(null);
    this.setValueObject(null);
    this.setValueIntArray(null);
    super.reset();
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof ModelWithAllPrivateFieldTypes_)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    ModelWithAllPrivateFieldTypes_ that = (ModelWithAllPrivateFieldTypes_) o;
    if ((onModelBoundListener_epoxyGeneratedModel == null) != (that.onModelBoundListener_epoxyGeneratedModel == null)) {
      return false;
    }
    if ((onModelUnboundListener_epoxyGeneratedModel == null) != (that.onModelUnboundListener_epoxyGeneratedModel == null)) {
      return false;
    }
    if (getValueInteger() != null ? !getValueInteger().equals(that.getValueInteger()) : that.getValueInteger() != null) {
      return false;
    }
    if (getValueShort() != that.getValueShort()) {
      return false;
    }
    if (getValueLong() != that.getValueLong()) {
      return false;
    }
    if (getValueList() != null ? !getValueList().equals(that.getValueList()) : that.getValueList() != null) {
      return false;
    }
    if (getValueShortWrapper() != null ? !getValueShortWrapper().equals(that.getValueShortWrapper()) : that.getValueShortWrapper() != null) {
      return false;
    }
    if (Double.compare(that.getValueDouble(), getValueDouble()) != 0) {
      return false;
    }
    if (getValueChar() != that.getValueChar()) {
      return false;
    }
    if (getValueInt() != that.getValueInt()) {
      return false;
    }
    if (getValueDoubleWrapper() != null ? !getValueDoubleWrapper().equals(that.getValueDoubleWrapper()) : that.getValueDoubleWrapper() != null) {
      return false;
    }
    if (getValueFloatWrapper() != null ? !getValueFloatWrapper().equals(that.getValueFloatWrapper()) : that.getValueFloatWrapper() != null) {
      return false;
    }
    if (getValueBooleanWrapper() != null ? !getValueBooleanWrapper().equals(that.getValueBooleanWrapper()) : that.getValueBooleanWrapper() != null) {
      return false;
    }
    if (getValueByteWrapper() != null ? !getValueByteWrapper().equals(that.getValueByteWrapper()) : that.getValueByteWrapper() != null) {
      return false;
    }
    if (getValuebByte() != that.getValuebByte()) {
      return false;
    }
    if (getValueLongWrapper() != null ? !getValueLongWrapper().equals(that.getValueLongWrapper()) : that.getValueLongWrapper() != null) {
      return false;
    }
    if (getValueCharacter() != null ? !getValueCharacter().equals(that.getValueCharacter()) : that.getValueCharacter() != null) {
      return false;
    }
    if (getValueString() != null ? !getValueString().equals(that.getValueString()) : that.getValueString() != null) {
      return false;
    }
    if (Float.compare(that.getValueFloat(), getValueFloat()) != 0) {
      return false;
    }
    if (isValueBoolean() != that.isValueBoolean()) {
      return false;
    }
    if (!Arrays.equals(getValueObjectArray(), that.getValueObjectArray())) {
      return false;
    }
    if (getValueObject() != null ? !getValueObject().equals(that.getValueObject()) : that.getValueObject() != null) {
      return false;
    }
    if (!Arrays.equals(getValueIntArray(), that.getValueIntArray())) {
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
    result = 31 * result + (getValueInteger() != null ? getValueInteger().hashCode() : 0);
    result = 31 * result + getValueShort();
    result = 31 * result + (int) (getValueLong() ^ (getValueLong() >>> 32));
    result = 31 * result + (getValueList() != null ? getValueList().hashCode() : 0);
    result = 31 * result + (getValueShortWrapper() != null ? getValueShortWrapper().hashCode() : 0);
    temp = Double.doubleToLongBits(getValueDouble());
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    result = 31 * result + getValueChar();
    result = 31 * result + getValueInt();
    result = 31 * result + (getValueDoubleWrapper() != null ? getValueDoubleWrapper().hashCode() : 0);
    result = 31 * result + (getValueFloatWrapper() != null ? getValueFloatWrapper().hashCode() : 0);
    result = 31 * result + (getValueBooleanWrapper() != null ? getValueBooleanWrapper().hashCode() : 0);
    result = 31 * result + (getValueByteWrapper() != null ? getValueByteWrapper().hashCode() : 0);
    result = 31 * result + getValuebByte();
    result = 31 * result + (getValueLongWrapper() != null ? getValueLongWrapper().hashCode() : 0);
    result = 31 * result + (getValueCharacter() != null ? getValueCharacter().hashCode() : 0);
    result = 31 * result + (getValueString() != null ? getValueString().hashCode() : 0);
    result = 31 * result + (getValueFloat() != +0.0f ? Float.floatToIntBits(getValueFloat()) : 0);
    result = 31 * result + (isValueBoolean() ? 1 : 0);
    result = 31 * result + Arrays.hashCode(getValueObjectArray());
    result = 31 * result + (getValueObject() != null ? getValueObject().hashCode() : 0);
    result = 31 * result + Arrays.hashCode(getValueIntArray());
    return result;
  }

  @Override
  public String toString() {
    return "ModelWithAllPrivateFieldTypes_{" +
        "valueInteger=" + getValueInteger() +
        ", valueShort=" + getValueShort() +
        ", valueLong=" + getValueLong() +
        ", valueList=" + getValueList() +
        ", valueShortWrapper=" + getValueShortWrapper() +
        ", valueDouble=" + getValueDouble() +
        ", valueChar=" + getValueChar() +
        ", valueInt=" + getValueInt() +
        ", valueDoubleWrapper=" + getValueDoubleWrapper() +
        ", valueFloatWrapper=" + getValueFloatWrapper() +
        ", valueBooleanWrapper=" + getValueBooleanWrapper() +
        ", valueByteWrapper=" + getValueByteWrapper() +
        ", valuebByte=" + getValuebByte() +
        ", valueLongWrapper=" + getValueLongWrapper() +
        ", valueCharacter=" + getValueCharacter() +
        ", valueString=" + getValueString() +
        ", valueFloat=" + getValueFloat() +
        ", valueBoolean=" + isValueBoolean() +
        ", valueObjectArray=" + getValueObjectArray() +
        ", valueObject=" + getValueObject() +
        ", valueIntArray=" + getValueIntArray() +
        "}" + super.toString();
  }
}