package com.airbnb.epoxy;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
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

@EpoxyBuildScope
public interface ModelWithAllFieldTypesBuilder {
  ModelWithAllFieldTypesBuilder onBind(
      OnModelBoundListener<ModelWithAllFieldTypes_, Object> listener);

  ModelWithAllFieldTypesBuilder onUnbind(
      OnModelUnboundListener<ModelWithAllFieldTypes_, Object> listener);

  ModelWithAllFieldTypesBuilder onVisibilityStateChanged(
      OnModelVisibilityStateChangedListener<ModelWithAllFieldTypes_, Object> listener);

  ModelWithAllFieldTypesBuilder onVisibilityChanged(
      OnModelVisibilityChangedListener<ModelWithAllFieldTypes_, Object> listener);

  ModelWithAllFieldTypesBuilder valueInt(int valueInt);

  ModelWithAllFieldTypesBuilder valueInteger(Integer valueInteger);

  ModelWithAllFieldTypesBuilder valueShort(short valueShort);

  ModelWithAllFieldTypesBuilder valueShortWrapper(Short valueShortWrapper);

  ModelWithAllFieldTypesBuilder valueChar(char valueChar);

  ModelWithAllFieldTypesBuilder valueCharacter(Character valueCharacter);

  ModelWithAllFieldTypesBuilder valuebByte(byte valuebByte);

  ModelWithAllFieldTypesBuilder valueByteWrapper(Byte valueByteWrapper);

  ModelWithAllFieldTypesBuilder valueLong(long valueLong);

  ModelWithAllFieldTypesBuilder valueLongWrapper(Long valueLongWrapper);

  ModelWithAllFieldTypesBuilder valueDouble(double valueDouble);

  ModelWithAllFieldTypesBuilder valueDoubleWrapper(Double valueDoubleWrapper);

  ModelWithAllFieldTypesBuilder valueFloat(float valueFloat);

  ModelWithAllFieldTypesBuilder valueFloatWrapper(Float valueFloatWrapper);

  ModelWithAllFieldTypesBuilder valueBoolean(boolean valueBoolean);

  ModelWithAllFieldTypesBuilder valueBooleanWrapper(Boolean valueBooleanWrapper);

  ModelWithAllFieldTypesBuilder valueIntArray(int[] valueIntArray);

  ModelWithAllFieldTypesBuilder valueObjectArray(Object[] valueObjectArray);

  ModelWithAllFieldTypesBuilder valueString(String valueString);

  ModelWithAllFieldTypesBuilder valueObject(Object valueObject);

  ModelWithAllFieldTypesBuilder valueList(List<String> valueList);

  ModelWithAllFieldTypesBuilder id(long id);

  ModelWithAllFieldTypesBuilder id(@Nullable Number... ids);

  ModelWithAllFieldTypesBuilder id(long id1, long id2);

  ModelWithAllFieldTypesBuilder id(@Nullable CharSequence key);

  ModelWithAllFieldTypesBuilder id(@Nullable CharSequence key, @Nullable CharSequence... otherKeys);

  ModelWithAllFieldTypesBuilder id(@Nullable CharSequence key, long id);

  ModelWithAllFieldTypesBuilder layout(@LayoutRes int layoutRes);

  ModelWithAllFieldTypesBuilder spanSizeOverride(
      @Nullable EpoxyModel.SpanSizeOverrideCallback spanSizeCallback);
}
