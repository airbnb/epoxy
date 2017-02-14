package com.airbnb.epoxy;

@ModuleEpoxyConfig(requireAbstractModels = true)
public abstract class RequireAbstractModelPassesClassWithAttribute extends EpoxyModel<Object> {

  @EpoxyAttribute String value;

  @Override
  protected int getDefaultLayout() {
    return 0;
  }
}