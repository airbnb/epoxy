package com.airbnb.epoxy;

import androidx.annotation.Nullable;
import java.lang.CharSequence;
import java.lang.Number;

@EpoxyBuildScope
public interface SourceViewModelBuilder {
  SourceViewModelBuilder onBind(OnModelBoundListener<SourceViewModel_, SourceView> listener);

  SourceViewModelBuilder onUnbind(OnModelUnboundListener<SourceViewModel_, SourceView> listener);

  SourceViewModelBuilder onVisibilityStateChanged(
      OnModelVisibilityStateChangedListener<SourceViewModel_, SourceView> listener);

  SourceViewModelBuilder onVisibilityChanged(
      OnModelVisibilityChangedListener<SourceViewModel_, SourceView> listener);

  SourceViewModelBuilder foo(int foo);

  SourceViewModelBuilder id(long id);

  SourceViewModelBuilder id(@Nullable Number... ids);

  SourceViewModelBuilder id(long id1, long id2);

  SourceViewModelBuilder id(@Nullable CharSequence key);

  SourceViewModelBuilder id(@Nullable CharSequence key, @Nullable CharSequence... otherKeys);

  SourceViewModelBuilder id(@Nullable CharSequence key, long id);

  SourceViewModelBuilder spanSizeOverride(
      @Nullable EpoxyModel.SpanSizeOverrideCallback spanSizeCallback);
}
