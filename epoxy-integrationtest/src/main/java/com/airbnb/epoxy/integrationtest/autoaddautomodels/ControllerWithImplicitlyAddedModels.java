package com.airbnb.epoxy.integrationtest.autoaddautomodels;

import com.airbnb.epoxy.AutoModel;
import com.airbnb.epoxy.EpoxyController;
import com.airbnb.epoxy.EpoxyModel.AddPredicate;
import com.airbnb.epoxy.integrationtest.Model_;

import java.util.ArrayList;
import java.util.List;

public class ControllerWithImplicitlyAddedModels extends EpoxyController {
  @AutoModel Model_ model1;
  @AutoModel Model_ model2;
  @AutoModel Model_ model3;
  @AutoModel Model_ model4;
  @AutoModel Model_ model5;
  @AutoModel Model_ model6;
  @AutoModel Model_ model7;
  @AutoModel Model_ model8;
  @AutoModel Model_ model9;

  private List<Model_> expectedModels;

  @Override
  protected void buildModels() {
    expectedModels = new ArrayList<>();

    model1
        .value(3);
    expectedModels.add(model1);

    add(model2);
    expectedModels.add(model2);

    model3
        .addTo(this);
    expectedModels.add(model3);

    model4
        .value(4)
        .addTo(this);
    expectedModels.add(model4);

    model5
        .value(34)
        .addIf(false, this);

    model6
        .value(34)
        .addIf(new AddPredicate() {
          @Override
          public boolean addIf() {
            return false;
          }
        }, this);

    model7
        .addIf(true, this);
    expectedModels.add(model7);

    model8
        .value(2)
        .addIf(true, this);
    expectedModels.add(model8);

    model9
        .value(34);
    expectedModels.add(model9);
  }

  public List<Model_> getExpectedModels() {
    return expectedModels;
  }
}
