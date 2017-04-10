package com.airbnb.epoxy;

import com.airbnb.epoxy.othermodule.R;

@EpoxyModelClass(layout = R.layout.res_in_other_module)
public abstract class ModelForTestingDuplicateRValues extends EpoxyModel<Object> {
  @EpoxyAttribute int value;
}