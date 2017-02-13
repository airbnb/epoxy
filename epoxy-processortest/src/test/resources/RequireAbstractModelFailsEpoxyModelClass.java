package com.airbnb.epoxy;

@EpoxyConfig(requireAbstractModels = true)
@EpoxyModelClass
public class RequireAbstractModelFailsEpoxyModelClass extends EpoxyModel<Object> {

  @Override
  protected int getDefaultLayout() {
    return 0;
  }
}