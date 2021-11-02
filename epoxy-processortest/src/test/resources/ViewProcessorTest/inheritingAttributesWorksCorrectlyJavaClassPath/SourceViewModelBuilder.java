package com.airbnb.epoxy;

import java.lang.CharSequence;
import java.lang.Number;
import java.lang.String;
import org.jetbrains.annotations.Nullable;

@EpoxyBuildScope
public interface SourceViewModelBuilder {
  SourceViewModelBuilder onBind(OnModelBoundListener<SourceViewModel_, SourceView> listener);

  SourceViewModelBuilder onUnbind(OnModelUnboundListener<SourceViewModel_, SourceView> listener);

  SourceViewModelBuilder onVisibilityStateChanged(
      OnModelVisibilityStateChangedListener<SourceViewModel_, SourceView> listener);

  SourceViewModelBuilder onVisibilityChanged(
      OnModelVisibilityChangedListener<SourceViewModel_, SourceView> listener);

  SourceViewModelBuilder sectionId(@Nullable("") String sectionId);

  SourceViewModelBuilder processorTest2ValueProtected(int processorTest2ValueProtected);

  SourceViewModelBuilder processorTest2ValuePublic(int processorTest2ValuePublic);

  SourceViewModelBuilder someAttributeAlsoWithSetter(int someAttributeAlsoWithSetter);

  SourceViewModelBuilder id(long id);

  SourceViewModelBuilder id(@androidx.annotation.Nullable Number... ids);

  SourceViewModelBuilder id(long id1, long id2);

  SourceViewModelBuilder id(@androidx.annotation.Nullable CharSequence key);

  SourceViewModelBuilder id(@androidx.annotation.Nullable CharSequence key,
      @androidx.annotation.Nullable CharSequence... otherKeys);

  SourceViewModelBuilder id(@androidx.annotation.Nullable CharSequence key, long id);

  SourceViewModelBuilder spanSizeOverride(
      @androidx.annotation.Nullable EpoxyModel.SpanSizeOverrideCallback spanSizeCallback);
}
