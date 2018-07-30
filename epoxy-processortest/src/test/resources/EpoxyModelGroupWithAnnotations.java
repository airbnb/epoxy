package com.airbnb.epoxy;

import java.util.Collection;

@EpoxyModelClass
public class EpoxyModelGroupWithAnnotations extends EpoxyModelGroup {

  public EpoxyModelGroupWithAnnotations(int layoutRes, Collection<? extends EpoxyModel<?>> models) {
    super(layoutRes, models);
  }

  @EpoxyAttribute int value;
}
