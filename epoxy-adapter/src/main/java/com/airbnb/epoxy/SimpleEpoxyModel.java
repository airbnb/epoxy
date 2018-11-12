package com.airbnb.epoxy;

import android.view.View;

import androidx.annotation.CallSuper;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;

/**
 * Helper class for cases where you don't need to do anything special when binding the view. This
 * allows you to just provide the layout instead of needing to create a separate {@link EpoxyModel}
 * subclass. This is useful for static layouts. You can also specify an onClick listener and the
 * span size.
 */
public class SimpleEpoxyModel extends EpoxyModel<View> {
  @LayoutRes private final int layoutRes;
  private View.OnClickListener onClickListener;
  private int spanCount = 1;

  public SimpleEpoxyModel(@LayoutRes int layoutRes) {
    this.layoutRes = layoutRes;
  }

  public SimpleEpoxyModel onClick(View.OnClickListener listener) {
    this.onClickListener = listener;
    return this;
  }

  public SimpleEpoxyModel span(int span) {
    spanCount = span;
    return this;
  }

  @CallSuper
  @Override
  public void bind(@NonNull View view) {
    super.bind(view);
    view.setOnClickListener(onClickListener);
    view.setClickable(onClickListener != null);
  }

  @CallSuper
  @Override
  public void unbind(@NonNull View view) {
    super.unbind(view);
    view.setOnClickListener(null);
  }

  @Override
  protected int getDefaultLayout() {
    return layoutRes;
  }

  @Override
  public int getSpanSize(int totalSpanCount, int position, int itemCount) {
    return spanCount;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof SimpleEpoxyModel)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }

    SimpleEpoxyModel that = (SimpleEpoxyModel) o;

    if (layoutRes != that.layoutRes) {
      return false;
    }
    if (spanCount != that.spanCount) {
      return false;
    }
    return onClickListener != null ? onClickListener.equals(that.onClickListener)
        : that.onClickListener == null;
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + layoutRes;
    result = 31 * result + (onClickListener != null ? onClickListener.hashCode() : 0);
    result = 31 * result + spanCount;
    return result;
  }
}
