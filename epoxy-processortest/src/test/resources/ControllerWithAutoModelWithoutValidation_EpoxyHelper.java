package com.airbnb.epoxy.adapter;

import com.airbnb.epoxy.BasicModelWithAttribute_;
import com.airbnb.epoxy.ControllerHelper;
import java.lang.Override;

/**
 * Generated file. Do not modify! */
public class ControllerWithAutoModelWithoutValidation_EpoxyHelper extends ControllerHelper<ControllerWithAutoModelWithoutValidation> {
  private final ControllerWithAutoModelWithoutValidation controller;

  public ControllerWithAutoModelWithoutValidation_EpoxyHelper(
      ControllerWithAutoModelWithoutValidation controller) {
    this.controller = controller;
  }

  @Override
  public void resetAutoModels() {
    controller.modelWithAttribute2 = new BasicModelWithAttribute_();
    controller.modelWithAttribute2.id(-1);
    controller.modelWithAttribute1 = new BasicModelWithAttribute_();
    controller.modelWithAttribute1.id(-2);
  }
}