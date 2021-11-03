package com.airbnb.epoxy;

import android.view.View;
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

  SourceViewModelBuilder keyedListener(
      @Nullable KeyedListener<?, View.OnClickListener> keyedListener);

  SourceViewModelBuilder id(long p0);

  SourceViewModelBuilder id(@Nullable Number... p0);

  SourceViewModelBuilder id(long p0, long p1);

  SourceViewModelBuilder id(@Nullable CharSequence p0);

  SourceViewModelBuilder id(@Nullable CharSequence p0, @Nullable CharSequence... p1);

  SourceViewModelBuilder id(@Nullable CharSequence p0, long p1);

  SourceViewModelBuilder spanSizeOverride(@Nullable EpoxyModel.SpanSizeOverrideCallback p0);
}
