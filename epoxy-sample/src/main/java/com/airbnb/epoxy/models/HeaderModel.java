package com.airbnb.epoxy.models;

import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModel;
import com.airbnb.epoxy.R;
import com.airbnb.epoxy.views.HeaderView;

public class HeaderModel extends EpoxyModel<HeaderView> {
  @EpoxyAttribute String title;
  @EpoxyAttribute String caption;

  @Override
  protected int getDefaultLayout() {
    return R.layout.view_model_header;
  }

  @Override
  public void bind(HeaderView view) {
    view.setTitle(title);
    view.setCaption(caption);
  }
}
