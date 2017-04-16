package com.airbnb.epoxy;

@EpoxyModelClass(layout = R.layout.model_with_data_binding)
abstract class TestDataBindingModel extends DataBindingEpoxyModel {
  @EpoxyAttribute String stringValue;
}
