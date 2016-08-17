package com.airbnb.epoxy;

import java.util.List;

public class ModelWithAllFieldTypes extends EpoxyModel<Object> {
  @EpoxyAttribute int valueInt;
  @EpoxyAttribute Integer valueInteger;
  @EpoxyAttribute short valueShort;
  @EpoxyAttribute Short valueShortWrapper;
  @EpoxyAttribute char valueChar;
  @EpoxyAttribute Character valueCharacter;
  @EpoxyAttribute byte valuebByte;
  @EpoxyAttribute Byte valueByteWrapper;
  @EpoxyAttribute long valueLong;
  @EpoxyAttribute Long valueLongWrapper;
  @EpoxyAttribute double valueDouble;
  @EpoxyAttribute Double valueDoubleWrapper;
  @EpoxyAttribute float valueFloat;
  @EpoxyAttribute Float valueFloatWrapper;
  @EpoxyAttribute boolean valueBoolean;
  @EpoxyAttribute Boolean valueBooleanWrapper;
  @EpoxyAttribute int[] valueIntArray;
  @EpoxyAttribute Object[] valueObjectArray;
  @EpoxyAttribute String valueString;
  @EpoxyAttribute Object valueObject;
  @EpoxyAttribute List<String> valueList;

  @Override
  protected int getDefaultLayout() {
    return 0;
  }
}