package com.airbnb.epoxy;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.lang.CharSequence;
import java.lang.Number;
import java.lang.String;

@EpoxyBuildScope
public interface SourceViewModelBuilder {
  SourceViewModelBuilder onBind(OnModelBoundListener<SourceViewModel_, SourceView> listener);

  SourceViewModelBuilder onUnbind(OnModelUnboundListener<SourceViewModel_, SourceView> listener);

  SourceViewModelBuilder onVisibilityStateChanged(
      OnModelVisibilityStateChangedListener<SourceViewModel_, SourceView> listener);

  SourceViewModelBuilder onVisibilityChanged(
      OnModelVisibilityChangedListener<SourceViewModel_, SourceView> listener);

  SourceViewModelBuilder sectionId(@NonNull String sectionId);

  SourceViewModelBuilder id(long id);

  SourceViewModelBuilder id(@Nullable Number... ids);

  SourceViewModelBuilder id(long id1, long id2);

  SourceViewModelBuilder id(@Nullable CharSequence key);

  SourceViewModelBuilder id(@Nullable CharSequence key, @Nullable CharSequence... otherKeys);

  SourceViewModelBuilder id(@Nullable CharSequence key, long id);

  SourceViewModelBuilder layout(@LayoutRes int layoutRes);

  SourceViewModelBuilder spanSizeOverride(
      @Nullable EpoxyModel.SpanSizeOverrideCallback spanSizeCallback);
}
