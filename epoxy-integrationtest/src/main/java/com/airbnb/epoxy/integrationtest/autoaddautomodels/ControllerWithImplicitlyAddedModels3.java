package com.airbnb.epoxy.integrationtest.autoaddautomodels;

import com.airbnb.epoxy.AutoModel;
import com.airbnb.epoxy.EpoxyController;
import com.airbnb.epoxy.integrationtest.Model_;

import java.util.ArrayList;
import java.util.List;

public class ControllerWithImplicitlyAddedModels3 extends EpoxyController {
  @AutoModel Model_ model1;
  @AutoModel Model_ model2;
  @AutoModel Model_ model3;
  @AutoModel Model_ model4;

  private List<Model_> expectedModels;

  @Override
  protected void buildModels() {
    expectedModels = new ArrayList<>();

    model1.value(1);
    expectedModels.add(model1);

    Model_ localModel = new Model_();
    add(localModel.id(1));
    expectedModels.add(localModel);

    localModel = new Model_();
    add(localModel.id(2).value(2));
    expectedModels.add(localModel);

    model2.value(2);
    expectedModels.add(model2);

    localModel = new Model_();
    localModel.id(3).value(3).addTo(this);
    expectedModels.add(localModel);

    model3.addIf(false, this);

    add(model4);
    expectedModels.add(model4);
  }

  public List<Model_> getExpectedModels() {
    return expectedModels;
  }
}
