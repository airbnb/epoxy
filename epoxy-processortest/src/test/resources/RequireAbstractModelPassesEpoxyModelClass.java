package com.airbnb.epoxy;

@ModuleEpoxyConfig(requireAbstractModels = true)
@EpoxyModelClass
public abstract class RequireAbstractModelPassesEpoxyModelClass extends EpoxyModel<Object> {

  @Override
  protected int getDefaultLayout() {
    return 0;
  }
}