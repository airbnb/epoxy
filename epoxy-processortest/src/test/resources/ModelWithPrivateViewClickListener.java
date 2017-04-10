package com.airbnb.epoxy;

import android.view.View;
import android.view.View.OnClickListener;

import com.airbnb.epoxy.EpoxyAttribute.Option;

import static com.airbnb.epoxy.EpoxyAttribute.Option.DoNotHash;

public class ModelWithPrivateViewClickListener extends EpoxyModel<Object> {
  @EpoxyAttribute(DoNotHash) private View.OnClickListener clickListener;

  @Override
  protected int getDefaultLayout() {
    return 0;
  }

  public OnClickListener getClickListener() {
    return clickListener;
  }

  public void setClickListener(OnClickListener clickListener) {
    this.clickListener = clickListener;
  }
}
