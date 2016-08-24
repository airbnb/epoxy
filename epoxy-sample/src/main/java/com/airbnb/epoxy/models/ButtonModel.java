package com.airbnb.epoxy.models;

import android.support.annotation.StringRes;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModel;
import com.airbnb.epoxy.R;

public class ButtonModel extends EpoxyModel<Button> {
  @EpoxyAttribute @StringRes int text;
  @EpoxyAttribute OnClickListener clickListener;

  @Override
  protected int getDefaultLayout() {
    return R.layout.model_button;
  }

  @Override
  public void bind(Button button) {
    button.setText(text);
    button.setOnClickListener(clickListener);
  }

  @Override
  public int getSpanSize(int totalSpanCount, int position, int itemCount) {
    return totalSpanCount;
  }
}
