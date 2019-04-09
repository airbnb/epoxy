package com.airbnb.epoxy.integrationtest;

import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModel;

import androidx.annotation.NonNull;

public class ModelWithCheckedChangeListener extends EpoxyModel<View> {

  @EpoxyAttribute OnCheckedChangeListener checkedChangeListener;

  @Override
  protected int getDefaultLayout() {
    return R.layout.model_with_checked_change;
  }

  @Override
  public void bind(@NonNull View view) {
    if (view instanceof CompoundButton) {
      ((CompoundButton) view).setOnCheckedChangeListener(checkedChangeListener);
    }
  }
}
