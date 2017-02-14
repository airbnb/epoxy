package com.airbnb.epoxy;

@ModuleEpoxyConfig(requireAbstractModels = true)
public class RequireAbstractModelFailsClassWithAttribute extends EpoxyModel<Object> {

  @EpoxyAttribute String value;

  @Override
  protected int getDefaultLayout() {
    return 0;
  }
}