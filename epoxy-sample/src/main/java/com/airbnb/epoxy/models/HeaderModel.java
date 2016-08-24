package com.airbnb.epoxy.models;

import android.support.annotation.StringRes;

import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModel;
import com.airbnb.epoxy.R;
import com.airbnb.epoxy.views.HeaderView;

public class HeaderModel extends EpoxyModel<HeaderView> {
  @EpoxyAttribute @StringRes int title;
  @EpoxyAttribute @StringRes int caption;

  @Override
  protected int getDefaultLayout() {
    return R.layout.model_header;
  }

  @Override
  public void bind(HeaderView view) {
    view.setTitle(title);
    view.setCaption(caption);
  }

  @Override
  public int getSpanSize(int totalSpanCount, int position, int itemCount) {
    return totalSpanCount;
  }
}
