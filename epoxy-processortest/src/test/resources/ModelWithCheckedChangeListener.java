package com.airbnb.epoxy;

import android.view.View;
import android.widget.CompoundButton;

import com.airbnb.epoxy.EpoxyAttribute.Option;

import static com.airbnb.epoxy.EpoxyAttribute.Option.DoNotHash;

public class ModelWithCheckedChangeListener extends EpoxyModel<Object> {
  @EpoxyAttribute(DoNotHash) CompoundButton.OnCheckedChangeListener checkedListener;

  @Override
  protected int getDefaultLayout() {
    return 0;
  }
}