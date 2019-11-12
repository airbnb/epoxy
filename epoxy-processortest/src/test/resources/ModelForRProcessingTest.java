package com.airbnb.epoxy;

@EpoxyModelClass(layout = R.layout.res)
public abstract class ModelForRProcessingTest extends EpoxyModel<Object> {
  @EpoxyAttribute int value;
}