package com.airbnb.epoxy;

@EpoxyConfig(requireAbstractModels = true)
public class RequireAbstractModelFailsClassWithAttribute extends EpoxyModel<Object> {

  @EpoxyAttribute String value;

  @Override
  protected int getDefaultLayout() {
    return 0;
  }
}