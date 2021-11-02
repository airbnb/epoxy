package com.airbnb.epoxy;

import android.view.View;
import androidx.annotation.Dimension;
import androidx.annotation.IntRange;
import androidx.annotation.LayoutRes;
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
import java.util.Map;
import kotlin.jvm.functions.Function3;

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

  TestManyTypesViewModelBuilder stringValue(@NonNull String stringValue);

  TestManyTypesViewModelBuilder nullableStringValue(@Nullable String nullableStringValue);

  TestManyTypesViewModelBuilder function(
      @NonNull Function3<Integer, Integer, Integer, Integer> function);

  TestManyTypesViewModelBuilder intValue(int intValue);

  TestManyTypesViewModelBuilder intValueWithAnnotation(@StringRes int intValueWithAnnotation);

  TestManyTypesViewModelBuilder intValueWithRangeAnnotation(
      @IntRange(from = 0, to = 200) int intValueWithRangeAnnotation);

  TestManyTypesViewModelBuilder intValueWithDimenTypeAnnotation(
      @Dimension(unit = 0) int intValueWithDimenTypeAnnotation);

  TestManyTypesViewModelBuilder intWithMultipleAnnotations(
      @IntRange(from = 0, to = 200) @Dimension(unit = 0) int intWithMultipleAnnotations);

  TestManyTypesViewModelBuilder integerValue(@NonNull Integer integerValue);

  TestManyTypesViewModelBuilder boolValue(boolean boolValue);

  TestManyTypesViewModelBuilder models(@NonNull List<? extends EpoxyModel<?>> models);

  TestManyTypesViewModelBuilder booleanValue(@NonNull Boolean booleanValue);

  TestManyTypesViewModelBuilder arrayValue(@NonNull String[] arrayValue);

  TestManyTypesViewModelBuilder listValue(@NonNull List<String> listValue);

  TestManyTypesViewModelBuilder mapValue(@NonNull Map<Integer, ?> mapValue);

  TestManyTypesViewModelBuilder clickListener(
      @NonNull final OnModelClickListener<TestManyTypesViewModel_, TestManyTypesView> clickListener);

  TestManyTypesViewModelBuilder clickListener(@NonNull View.OnClickListener clickListener);

  TestManyTypesViewModelBuilder title(@Nullable CharSequence title);

  TestManyTypesViewModelBuilder title(@StringRes int stringRes);

  TestManyTypesViewModelBuilder title(@StringRes int stringRes, Object... formatArgs);

  TestManyTypesViewModelBuilder titleQuantityRes(@PluralsRes int pluralRes, int quantity,
      Object... formatArgs);

  TestManyTypesViewModelBuilder id(long id);

  TestManyTypesViewModelBuilder id(@Nullable Number... ids);

  TestManyTypesViewModelBuilder id(long id1, long id2);

  TestManyTypesViewModelBuilder id(@Nullable CharSequence key);

  TestManyTypesViewModelBuilder id(@Nullable CharSequence key, @Nullable CharSequence... otherKeys);

  TestManyTypesViewModelBuilder id(@Nullable CharSequence key, long id);

  TestManyTypesViewModelBuilder layout(@LayoutRes int layoutRes);

  TestManyTypesViewModelBuilder spanSizeOverride(
      @Nullable EpoxyModel.SpanSizeOverrideCallback spanSizeCallback);
}
