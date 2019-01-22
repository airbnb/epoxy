package com.airbnb.epoxy.integrationtest;

import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModel;
import com.airbnb.epoxy.EpoxyModelClass;
import com.airbnb.epoxy.EpoxyModelGroup;
import com.airbnb.epoxy.ModelGroupHolder;

import java.util.List;

import androidx.annotation.NonNull;

@EpoxyModelClass
public abstract class ModelGroupWithAnnotation extends EpoxyModelGroup {
  @EpoxyAttribute int backgroundColor;

  public ModelGroupWithAnnotation(List<? extends EpoxyModel<?>> models) {
    super(R.layout.model_with_click_listener, models);
  }

  @Override
  public void bind(@NonNull ModelGroupHolder holder) {
    super.bind(holder);
    holder.getRootView().setBackgroundColor(backgroundColor);
  }
}
