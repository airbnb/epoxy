package com.airbnb.epoxy;

import java.util.List;

public class ModelWithAllPrivateFieldTypes extends EpoxyModel<Object> {
  @EpoxyAttribute private int valueInt;
  @EpoxyAttribute private Integer valueInteger;
  @EpoxyAttribute private short valueShort;
  @EpoxyAttribute private Short valueShortWrapper;
  @EpoxyAttribute private char valueChar;
  @EpoxyAttribute private Character valueCharacter;
  @EpoxyAttribute private byte valuebByte;
  @EpoxyAttribute private Byte valueByteWrapper;
  @EpoxyAttribute private long valueLong;
  @EpoxyAttribute private Long valueLongWrapper;
  @EpoxyAttribute private double valueDouble;
  @EpoxyAttribute private Double valueDoubleWrapper;
  @EpoxyAttribute private float valueFloat;
  @EpoxyAttribute private Float valueFloatWrapper;
  @EpoxyAttribute private boolean valueBoolean;
  @EpoxyAttribute private Boolean valueBooleanWrapper;
  @EpoxyAttribute private int[] valueIntArray;
  @EpoxyAttribute private Object[] valueObjectArray;
  @EpoxyAttribute private String valueString;
  @EpoxyAttribute private Object valueObject;
  @EpoxyAttribute private List<String> valueList;

  @Override
  protected int getDefaultLayout() {
    return 0;
  }

  public int getValueInt() {
    return valueInt;
  }

  public void setValueInt(int valueInt) {
    this.valueInt = valueInt;
  }

  public Integer getValueInteger() {
    return valueInteger;
  }

  public void setValueInteger(Integer valueInteger) {
    this.valueInteger = valueInteger;
  }

  public short getValueShort() {
    return valueShort;
  }

  public void setValueShort(short valueShort) {
    this.valueShort = valueShort;
  }

  public Short getValueShortWrapper() {
    return valueShortWrapper;
  }

  public void setValueShortWrapper(Short valueShortWrapper) {
    this.valueShortWrapper = valueShortWrapper;
  }

  public char getValueChar() {
    return valueChar;
  }

  public void setValueChar(char valueChar) {
    this.valueChar = valueChar;
  }

  public Character getValueCharacter() {
    return valueCharacter;
  }

  public void setValueCharacter(Character valueCharacter) {
    this.valueCharacter = valueCharacter;
  }

  public byte getValuebByte() {
    return valuebByte;
  }

  public void setValuebByte(byte valuebByte) {
    this.valuebByte = valuebByte;
  }

  public Byte getValueByteWrapper() {
    return valueByteWrapper;
  }

  public void setValueByteWrapper(Byte valueByteWrapper) {
    this.valueByteWrapper = valueByteWrapper;
  }

  public long getValueLong() {
    return valueLong;
  }

  public void setValueLong(long valueLong) {
    this.valueLong = valueLong;
  }

  public Long getValueLongWrapper() {
    return valueLongWrapper;
  }

  public void setValueLongWrapper(Long valueLongWrapper) {
    this.valueLongWrapper = valueLongWrapper;
  }

  public double getValueDouble() {
    return valueDouble;
  }

  public void setValueDouble(double valueDouble) {
    this.valueDouble = valueDouble;
  }

  public Double getValueDoubleWrapper() {
    return valueDoubleWrapper;
  }

  public void setValueDoubleWrapper(Double valueDoubleWrapper) {
    this.valueDoubleWrapper = valueDoubleWrapper;
  }

  public float getValueFloat() {
    return valueFloat;
  }

  public void setValueFloat(float valueFloat) {
    this.valueFloat = valueFloat;
  }

  public Float getValueFloatWrapper() {
    return valueFloatWrapper;
  }

  public void setValueFloatWrapper(Float valueFloatWrapper) {
    this.valueFloatWrapper = valueFloatWrapper;
  }

  public boolean isValueBoolean() {
    return valueBoolean;
  }

  public void setValueBoolean(boolean valueBoolean) {
    this.valueBoolean = valueBoolean;
  }

  public Boolean getValueBooleanWrapper() {
    return valueBooleanWrapper;
  }

  public void setValueBooleanWrapper(Boolean valueBooleanWrapper) {
    this.valueBooleanWrapper = valueBooleanWrapper;
  }

  public int[] getValueIntArray() {
    return valueIntArray;
  }

  public void setValueIntArray(int[] valueIntArray) {
    this.valueIntArray = valueIntArray;
  }

  public Object[] getValueObjectArray() {
    return valueObjectArray;
  }

  public void setValueObjectArray(Object[] valueObjectArray) {
    this.valueObjectArray = valueObjectArray;
  }

  public String getValueString() {
    return valueString;
  }

  public void setValueString(String valueString) {
    this.valueString = valueString;
  }

  public Object getValueObject() {
    return valueObject;
  }

  public void setValueObject(Object valueObject) {
    this.valueObject = valueObject;
  }

  public List<String> getValueList() {
    return valueList;
  }

  public void setValueList(List<String> valueList) {
    this.valueList = valueList;
  }
}