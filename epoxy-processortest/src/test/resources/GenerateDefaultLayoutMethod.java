package com.airbnb.epoxy;

@EpoxyModelClass(layout = 1)
public abstract class GenerateDefaultLayoutMethod extends EpoxyModel<Object> {
  @EpoxyAttribute int value;
}