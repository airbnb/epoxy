package com.airbnb.epoxy;

import android.view.View;

import com.airbnb.epoxy.EpoxyAttribute.Option;

import static com.airbnb.epoxy.EpoxyAttribute.Option.DoNotHash;

public class ModelWithViewClickListener extends EpoxyModel<Object> {
  @EpoxyAttribute(DoNotHash) View.OnClickListener clickListener;

  @Override
  protected int getDefaultLayout() {
    return 0;
  }
}