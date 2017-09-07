package com.airbnb.epoxy;

import android.support.annotation.Dimension;
import android.support.annotation.IntRange;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.annotation.PluralsRes;
import android.support.annotation.StringRes;
import android.view.View;
import java.lang.Boolean;
import java.lang.CharSequence;
import java.lang.Integer;
import java.lang.Number;
import java.lang.Object;
import java.lang.String;
import java.util.List;

public interface TestManyTypesViewModelBuilder {
  TestManyTypesViewModel_ onBind(OnModelBoundListener<TestManyTypesViewModel_, TestManyTypesView> listener);

  TestManyTypesViewModel_ onUnbind(OnModelUnboundListener<TestManyTypesViewModel_, TestManyTypesView> listener);

  TestManyTypesViewModel_ stringValue(String stringValue);

  TestManyTypesViewModel_ nullableStringValue(@Nullable String nullableStringValue);

  TestManyTypesViewModel_ intValue(int intValue);

  TestManyTypesViewModel_ intValueWithAnnotation(@StringRes int intValueWithAnnotation);

  TestManyTypesViewModel_ intValueWithRangeAnnotation(@IntRange(from = 0, to = 200) int intValueWithRangeAnnotation);

  TestManyTypesViewModel_ intValueWithDimenTypeAnnotation(@Dimension(unit = 0) int intValueWithDimenTypeAnnotation);

  TestManyTypesViewModel_ intWithMultipleAnnotations(@IntRange(from = 0, to = 200) @Dimension(unit = 0) int intWithMultipleAnnotations);

  TestManyTypesViewModel_ integerValue(Integer integerValue);

  TestManyTypesViewModel_ boolValue(boolean boolValue);

  TestManyTypesViewModel_ booleanValue(Boolean booleanValue);

  TestManyTypesViewModel_ arrayValue(String[] arrayValue);

  TestManyTypesViewModel_ listValue(List<String> listValue);

  TestManyTypesViewModel_ clickListener(final OnModelClickListener<TestManyTypesViewModel_, TestManyTypesView> clickListener);

  TestManyTypesViewModel_ clickListener(View.OnClickListener clickListener);

  TestManyTypesViewModel_ title(@Nullable CharSequence title);

  TestManyTypesViewModel_ title(@StringRes int stringRes);

  TestManyTypesViewModel_ title(@StringRes int stringRes, Object... formatArgs);

  TestManyTypesViewModel_ titleQuantityRes(@PluralsRes int pluralRes, int quantity,
      Object... formatArgs);

  TestManyTypesViewModel_ id(long id);

  TestManyTypesViewModel_ id(Number... ids);

  TestManyTypesViewModel_ id(long id1, long id2);

  TestManyTypesViewModel_ id(CharSequence key);

  TestManyTypesViewModel_ id(CharSequence key, CharSequence... otherKeys);

  TestManyTypesViewModel_ id(CharSequence key, long id);

  TestManyTypesViewModel_ layout(@LayoutRes int arg0);

  TestManyTypesViewModel_ spanSizeOverride(@Nullable EpoxyModel.SpanSizeOverrideCallback arg0);
}