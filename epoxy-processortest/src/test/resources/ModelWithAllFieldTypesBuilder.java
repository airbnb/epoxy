package com.airbnb.epoxy;

import android.support.annotation.LayoutRes;
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
import java.lang.Short;
import java.lang.String;
import java.util.List;

public interface ModelWithAllFieldTypesBuilder {
  ModelWithAllFieldTypes_ onBind(OnModelBoundListener<ModelWithAllFieldTypes_, Object> listener);

  ModelWithAllFieldTypes_ onUnbind(OnModelUnboundListener<ModelWithAllFieldTypes_, Object> listener);

  ModelWithAllFieldTypes_ valueInt(int valueInt);

  ModelWithAllFieldTypes_ valueInteger(Integer valueInteger);

  ModelWithAllFieldTypes_ valueShort(short valueShort);

  ModelWithAllFieldTypes_ valueShortWrapper(Short valueShortWrapper);

  ModelWithAllFieldTypes_ valueChar(char valueChar);

  ModelWithAllFieldTypes_ valueCharacter(Character valueCharacter);

  ModelWithAllFieldTypes_ valuebByte(byte valuebByte);

  ModelWithAllFieldTypes_ valueByteWrapper(Byte valueByteWrapper);

  ModelWithAllFieldTypes_ valueLong(long valueLong);

  ModelWithAllFieldTypes_ valueLongWrapper(Long valueLongWrapper);

  ModelWithAllFieldTypes_ valueDouble(double valueDouble);

  ModelWithAllFieldTypes_ valueDoubleWrapper(Double valueDoubleWrapper);

  ModelWithAllFieldTypes_ valueFloat(float valueFloat);

  ModelWithAllFieldTypes_ valueFloatWrapper(Float valueFloatWrapper);

  ModelWithAllFieldTypes_ valueBoolean(boolean valueBoolean);

  ModelWithAllFieldTypes_ valueBooleanWrapper(Boolean valueBooleanWrapper);

  ModelWithAllFieldTypes_ valueIntArray(int[] valueIntArray);

  ModelWithAllFieldTypes_ valueObjectArray(Object[] valueObjectArray);

  ModelWithAllFieldTypes_ valueString(String valueString);

  ModelWithAllFieldTypes_ valueObject(Object valueObject);

  ModelWithAllFieldTypes_ valueList(List<String> valueList);

  ModelWithAllFieldTypes_ id(long id);

  ModelWithAllFieldTypes_ id(Number... ids);

  ModelWithAllFieldTypes_ id(long id1, long id2);

  ModelWithAllFieldTypes_ id(CharSequence key);

  ModelWithAllFieldTypes_ id(CharSequence key, CharSequence... otherKeys);

  ModelWithAllFieldTypes_ id(CharSequence key, long id);

  ModelWithAllFieldTypes_ layout(@LayoutRes int arg0);

  ModelWithAllFieldTypes_ spanSizeOverride(@Nullable EpoxyModel.SpanSizeOverrideCallback arg0);
}