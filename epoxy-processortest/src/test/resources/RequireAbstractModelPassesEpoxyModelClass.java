package com.airbnb.epoxy;

@EpoxyConfig(requireAbstractModels = true)
@EpoxyModelClass
public abstract class RequireAbstractModelPassesEpoxyModelClass extends EpoxyModel<Object> {

  @Override
  protected int getDefaultLayout() {
    return 0;
  }
}