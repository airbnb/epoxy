package com.airbnb.epoxy;

import android.view.View;
import androidx.annotation.Dimension;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.PluralsRes;
import androidx.annotation.StringRes;
import java.lang.Boolean;
import java.lang.CharSequence;
import java.lang.Integer;
import java.lang.Number;
import java.lang.Object;
import java.lang.String;
import java.util.List;
import kotlin.jvm.functions.Function2;

@EpoxyBuildScope
public interface TestManyTypesViewModelBuilder {
  TestManyTypesViewModelBuilder onBind(
      OnModelBoundListener<TestManyTypesViewModel_, TestManyTypesView> listener);

  TestManyTypesViewModelBuilder onUnbind(
      OnModelUnboundListener<TestManyTypesViewModel_, TestManyTypesView> listener);

  TestManyTypesViewModelBuilder onVisibilityStateChanged(
      OnModelVisibilityStateChangedListener<TestManyTypesViewModel_, TestManyTypesView> listener);

  TestManyTypesViewModelBuilder onVisibilityChanged(
      OnModelVisibilityChangedListener<TestManyTypesViewModel_, TestManyTypesView> listener);

  TestManyTypesViewModelBuilder enabled(boolean enabled);

  TestManyTypesViewModelBuilder stringValue(@NonNull String stringValue);

  TestManyTypesViewModelBuilder functionType(
      @NonNull Function2<? super String, ? super String, Integer> functionType);

  TestManyTypesViewModelBuilder listOfDataClass(@NonNull List<SomeDataClass> listOfDataClass);

  TestManyTypesViewModelBuilder listOfEnumClass(@NonNull List<SomeEnumClass> listOfEnumClass);

  TestManyTypesViewModelBuilder nullableStringValue(@Nullable String nullableStringValue);

  TestManyTypesViewModelBuilder intValue(int intValue);

  TestManyTypesViewModelBuilder intValueWithDefault(int intValueWithDefault);

  TestManyTypesViewModelBuilder intValueWithAnnotation(@StringRes int intValueWithAnnotation);

  TestManyTypesViewModelBuilder intValueWithRangeAnnotation(
      @IntRange(from = 0, to = 200) int intValueWithRangeAnnotation);

  TestManyTypesViewModelBuilder intValueWithDimenTypeAnnotation(
      @Dimension(unit = 0) int intValueWithDimenTypeAnnotation);

  TestManyTypesViewModelBuilder intWithMultipleAnnotations(
      @IntRange(from = 0, to = 200) @Dimension(unit = 0) int intWithMultipleAnnotations);

  TestManyTypesViewModelBuilder integerValue(int integerValue);

  TestManyTypesViewModelBuilder boolValue(boolean boolValue);

  TestManyTypesViewModelBuilder models(@NonNull List<? extends EpoxyModel<?>> models);

  TestManyTypesViewModelBuilder booleanValue(@Nullable Boolean booleanValue);

  TestManyTypesViewModelBuilder arrayValue(@Nullable String[] arrayValue);

  TestManyTypesViewModelBuilder listValue(@Nullable List<String> listValue);

  TestManyTypesViewModelBuilder clickListener(
      @Nullable final OnModelClickListener<TestManyTypesViewModel_, TestManyTypesView> clickListener);

  TestManyTypesViewModelBuilder clickListener(@Nullable View.OnClickListener clickListener);

  TestManyTypesViewModelBuilder customClickListener(
      @Nullable CustomClickListenerSubclass customClickListener);

  TestManyTypesViewModelBuilder title(@Nullable CharSequence title);

  TestManyTypesViewModelBuilder title(@StringRes int stringRes);

  TestManyTypesViewModelBuilder title(@StringRes int stringRes, Object... formatArgs);

  TestManyTypesViewModelBuilder titleQuantityRes(@PluralsRes int pluralRes, int quantity,
      Object... formatArgs);

  TestManyTypesViewModelBuilder myProperty(int myProperty);

  TestManyTypesViewModelBuilder myNullableProperty(@Nullable Integer myNullableProperty);

  TestManyTypesViewModelBuilder delegatedProperty(int delegatedProperty);

  TestManyTypesViewModelBuilder id(long p0);

  TestManyTypesViewModelBuilder id(@Nullable Number... p0);

  TestManyTypesViewModelBuilder id(long p0, long p1);

  TestManyTypesViewModelBuilder id(@Nullable CharSequence p0);

  TestManyTypesViewModelBuilder id(@Nullable CharSequence p0, @Nullable CharSequence... p1);

  TestManyTypesViewModelBuilder id(@Nullable CharSequence p0, long p1);

  TestManyTypesViewModelBuilder spanSizeOverride(@Nullable EpoxyModel.SpanSizeOverrideCallback p0);
}
