package com.airbnb.epoxy;

import android.support.annotation.Dimension;
import android.support.annotation.IntRange;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
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
  TestManyTypesViewModelBuilder onBind(OnModelBoundListener<TestManyTypesViewModel_, TestManyTypesView> listener);

  TestManyTypesViewModelBuilder onUnbind(OnModelUnboundListener<TestManyTypesViewModel_, TestManyTypesView> listener);

  TestManyTypesViewModelBuilder stringValue(@NonNull String stringValue);

  TestManyTypesViewModelBuilder nullableStringValue(@Nullable String nullableStringValue);

  TestManyTypesViewModelBuilder intValue(int intValue);

  TestManyTypesViewModelBuilder intValueWithAnnotation(@StringRes int intValueWithAnnotation);

  TestManyTypesViewModelBuilder intValueWithRangeAnnotation(@IntRange(from = 0, to = 200) int intValueWithRangeAnnotation);

  TestManyTypesViewModelBuilder intValueWithDimenTypeAnnotation(@Dimension(unit = 0) int intValueWithDimenTypeAnnotation);

  TestManyTypesViewModelBuilder intWithMultipleAnnotations(@IntRange(from = 0, to = 200) @Dimension(unit = 0) int intWithMultipleAnnotations);

  TestManyTypesViewModelBuilder integerValue(@NonNull Integer integerValue);

  TestManyTypesViewModelBuilder boolValue(boolean boolValue);

  TestManyTypesViewModelBuilder booleanValue(@NonNull Boolean booleanValue);

  TestManyTypesViewModelBuilder arrayValue(@NonNull String[] arrayValue);

  TestManyTypesViewModelBuilder listValue(@NonNull List<String> listValue);

  TestManyTypesViewModelBuilder clickListener(final OnModelClickListener<TestManyTypesViewModel_, TestManyTypesView> clickListener);

  TestManyTypesViewModelBuilder clickListener(@NonNull View.OnClickListener clickListener);

  TestManyTypesViewModelBuilder title(@Nullable CharSequence title);

  TestManyTypesViewModelBuilder title(@StringRes int stringRes);

  TestManyTypesViewModelBuilder title(@StringRes int stringRes, Object... formatArgs);

  TestManyTypesViewModelBuilder titleQuantityRes(@PluralsRes int pluralRes, int quantity,
      Object... formatArgs);

  TestManyTypesViewModelBuilder id(long id);

  TestManyTypesViewModelBuilder id(@NonNull Number... arg0);

  TestManyTypesViewModelBuilder id(long id1, long id2);

  TestManyTypesViewModelBuilder id(@NonNull CharSequence arg0);

  TestManyTypesViewModelBuilder id(@NonNull CharSequence arg0, @NonNull CharSequence... arg1);

  TestManyTypesViewModelBuilder id(@NonNull CharSequence arg0, long arg1);

  TestManyTypesViewModelBuilder layout(@LayoutRes int arg0);

  TestManyTypesViewModelBuilder spanSizeOverride(@Nullable EpoxyModel.SpanSizeOverrideCallback arg0);
}