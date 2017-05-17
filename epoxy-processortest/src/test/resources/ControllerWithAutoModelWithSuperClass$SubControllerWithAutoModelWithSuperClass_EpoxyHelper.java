package com.airbnb.epoxy.adapter;

import com.airbnb.epoxy.BasicModelWithAttribute_;
import com.airbnb.epoxy.ControllerHelper;
import com.airbnb.epoxy.EpoxyModel;
import java.lang.IllegalStateException;
import java.lang.Override;
import java.lang.String;

/**
 * Generated file. Do not modify! */
public class ControllerWithAutoModelWithSuperClass$SubControllerWithAutoModelWithSuperClass_EpoxyHelper extends ControllerHelper<ControllerWithAutoModelWithSuperClass.SubControllerWithAutoModelWithSuperClass> {
  private final ControllerWithAutoModelWithSuperClass.SubControllerWithAutoModelWithSuperClass controller;

  private EpoxyModel modelWithAttribute1;

  private EpoxyModel modelWithAttribute2;

  private EpoxyModel modelWithAttribute3;

  public ControllerWithAutoModelWithSuperClass$SubControllerWithAutoModelWithSuperClass_EpoxyHelper(ControllerWithAutoModelWithSuperClass.SubControllerWithAutoModelWithSuperClass controller) {
    this.controller = controller;
  }

  @Override
  public void resetAutoModels() {
    validateModelsHaveNotChanged();
    controller.modelWithAttribute1 = new BasicModelWithAttribute_();
    controller.modelWithAttribute1.id(-1);
    controller.modelWithAttribute2 = new BasicModelWithAttribute_();
    controller.modelWithAttribute2.id(-2);
    controller.modelWithAttribute3 = new BasicModelWithAttribute_();
    controller.modelWithAttribute3.id(-3);
    saveModelsForNextValidation();
  }

  private void validateModelsHaveNotChanged() {
    validateSameModel(modelWithAttribute1, controller.modelWithAttribute1, "modelWithAttribute1", -1);
    validateSameModel(modelWithAttribute2, controller.modelWithAttribute2, "modelWithAttribute2", -2);
    validateSameModel(modelWithAttribute3, controller.modelWithAttribute3, "modelWithAttribute3", -3);
    validateModelHashCodesHaveNotChanged(controller);
  }

  private void validateSameModel(EpoxyModel expectedObject, EpoxyModel actualObject,
      String fieldName, int id) {
    if (expectedObject != actualObject) {
      throw new IllegalStateException("Fields annotated with AutoModel cannot be directly assigned. The controller manages these fields for you. (" + controller.getClass().getSimpleName() + "#" + fieldName + ")");
    }
    if (actualObject != null && actualObject.id() != id) {
      throw new IllegalStateException("Fields annotated with AutoModel cannot have their id changed manually. The controller manages the ids of these models for you. (" + controller.getClass().getSimpleName() + "#" + fieldName + ")");
    }
  }

  private void saveModelsForNextValidation() {
    modelWithAttribute1 = controller.modelWithAttribute1;
    modelWithAttribute2 = controller.modelWithAttribute2;
    modelWithAttribute3 = controller.modelWithAttribute3;
  }
}