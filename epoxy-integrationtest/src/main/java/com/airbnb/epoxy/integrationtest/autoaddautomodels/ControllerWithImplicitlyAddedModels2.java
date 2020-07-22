package com.airbnb.epoxy.integrationtest.autoaddautomodels;

import com.airbnb.epoxy.AutoModel;
import com.airbnb.epoxy.EpoxyController;
import com.airbnb.epoxy.integrationtest.Model_;

import java.util.ArrayList;
import java.util.List;

public class ControllerWithImplicitlyAddedModels2 extends EpoxyController {
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

    add(model1);
    expectedModels.add(model1);

    add(model2.value(2));
    expectedModels.add(model2);

    add(model3.value(3));
    expectedModels.add(model3);

    model4.value(4);
    expectedModels.add(model4);

    model5.value(5);
    expectedModels.add(model5);

    model6.addIf(false, this);

    model7.addIf(false, this);

    model8.addIf(true, this);
    expectedModels.add(model8);

    add(model9);
    expectedModels.add(model9);
  }

  public List<Model_> getExpectedModels() {
    return expectedModels;
  }
}
