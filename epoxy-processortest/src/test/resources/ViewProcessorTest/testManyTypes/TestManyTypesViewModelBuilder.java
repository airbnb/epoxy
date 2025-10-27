package com.airbnb.epoxy;

import android.view.View;
import androidx.annotation.Dimension;
import androidx.annotation.IntRange;
import androidx.annotation.PluralsRes;
import androidx.annotation.StringRes;
import java.lang.Boolean;
import java.lang.CharSequence;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Number;
import java.lang.Object;
import java.lang.String;
import java.util.List;
import kotlin.jvm.functions.Function2;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

  TestManyTypesViewModelBuilder myProperty(int myProperty);

  TestManyTypesViewModelBuilder myNullableProperty(@Nullable("") Integer myNullableProperty);

  TestManyTypesViewModelBuilder delegatedProperty(int delegatedProperty);

  TestManyTypesViewModelBuilder enabled(boolean enabled);

  TestManyTypesViewModelBuilder stringValue(
      @NotNull(value = "", exception = Exception.class) String stringValue);

  TestManyTypesViewModelBuilder functionType(
      @NotNull(value = "", exception = Exception.class) Function2<? super String, ? super String, Integer> functionType);

  TestManyTypesViewModelBuilder listOfDataClass(
      @NotNull(value = "", exception = Exception.class) List<SomeDataClass> listOfDataClass);

  TestManyTypesViewModelBuilder listOfEnumClass(
      @NotNull(value = "", exception = Exception.class) List<? extends SomeEnumClass> listOfEnumClass);

  TestManyTypesViewModelBuilder nullableStringValue(@Nullable("") String nullableStringValue);

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

  TestManyTypesViewModelBuilder models(
      @NotNull(value = "", exception = Exception.class) List<? extends EpoxyModel<?>> models);

  TestManyTypesViewModelBuilder booleanValue(@Nullable("") Boolean booleanValue);

  TestManyTypesViewModelBuilder arrayValue(@Nullable("") String[] arrayValue);

  TestManyTypesViewModelBuilder listValue(@Nullable("") List<String> listValue);

  TestManyTypesViewModelBuilder clickListener(
      @Nullable("") final OnModelClickListener<TestManyTypesViewModel_, TestManyTypesView> clickListener);

  TestManyTypesViewModelBuilder clickListener(@Nullable("") View.OnClickListener clickListener);

  TestManyTypesViewModelBuilder customClickListener(
      @Nullable("") CustomClickListenerSubclass customClickListener);

  TestManyTypesViewModelBuilder title(@androidx.annotation.Nullable CharSequence title);

  TestManyTypesViewModelBuilder title(@StringRes int stringRes);

  TestManyTypesViewModelBuilder title(@StringRes int stringRes, Object... formatArgs);

  TestManyTypesViewModelBuilder titleQuantityRes(@PluralsRes int pluralRes, int quantity,
      Object... formatArgs);

  TestManyTypesViewModelBuilder id(long id);

  TestManyTypesViewModelBuilder id(@androidx.annotation.Nullable Number... ids);

  TestManyTypesViewModelBuilder id(long id1, long id2);

  TestManyTypesViewModelBuilder id(@androidx.annotation.Nullable CharSequence key);

  TestManyTypesViewModelBuilder id(@androidx.annotation.Nullable CharSequence key,
      @androidx.annotation.Nullable CharSequence... otherKeys);

  TestManyTypesViewModelBuilder id(@androidx.annotation.Nullable CharSequence key, long id);

  TestManyTypesViewModelBuilder spanSizeOverride(
      @androidx.annotation.Nullable EpoxyModel.SpanSizeOverrideCallback spanSizeCallback);
}
