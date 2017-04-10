package com.airbnb.epoxy.sample.models;

import android.support.annotation.StringRes;

import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModel;
import com.airbnb.epoxy.EpoxyModelClass;
import com.airbnb.epoxy.R;
import com.airbnb.epoxy.sample.views.HeaderView;

/**
 * This model shows an example of binding to a specific view type. In this case it is a custom view
 * we made, but it could also be another single view, like an EditText or Button.
 */
@EpoxyModelClass(layout = R.layout.model_header)
public abstract class HeaderModel extends EpoxyModel<HeaderView> {
  @EpoxyAttribute @StringRes int title;
  @EpoxyAttribute @StringRes int caption;

  @Override
  public void bind(HeaderView view) {
    view.setTitle(title);
    view.setCaption(caption);
  }

  @Override
  public int getSpanSize(int totalSpanCount, int position, int itemCount) {
    // We want the header to take up all spans so it fills the screen width
    return totalSpanCount;
  }
}
