package com.airbnb.epoxy.adapter;

import android.view.View;
import com.airbnb.epoxy.BasicModelWithAttribute_;
import com.airbnb.epoxy.ControllerHelper;
import com.airbnb.epoxy.EpoxyModel;
import com.airbnb.epoxy.processortest2.ProcessorTest2Model;
import java.lang.IllegalStateException;
import java.lang.Override;
import java.lang.String;

/**
 * Generated file. Do not modify!
 */
public class ControllerWithAutoModel_EpoxyHelper extends ControllerHelper<ControllerWithAutoModel> {
  private final ControllerWithAutoModel controller;

  private EpoxyModel modelFromClassPath;

  private EpoxyModel modelWithAttribute2;

  private EpoxyModel modelWithAttribute1;

  public ControllerWithAutoModel_EpoxyHelper(ControllerWithAutoModel controller) {
    this.controller = controller;
  }

  @Override
  public void resetAutoModels() {
    validateModelsHaveNotChanged();
    controller.modelFromClassPath = new ProcessorTest2Model<View>();
    controller.modelFromClassPath.id(-1);
    controller.modelWithAttribute2 = new BasicModelWithAttribute_();
    controller.modelWithAttribute2.id(-2);
    controller.modelWithAttribute1 = new BasicModelWithAttribute_();
    controller.modelWithAttribute1.id(-3);
    saveModelsForNextValidation();
  }

  private void validateModelsHaveNotChanged() {
    validateSameModel(modelFromClassPath, controller.modelFromClassPath, "modelFromClassPath", -1);
    validateSameModel(modelWithAttribute2, controller.modelWithAttribute2, "modelWithAttribute2", -2);
    validateSameModel(modelWithAttribute1, controller.modelWithAttribute1, "modelWithAttribute1", -3);
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
    modelFromClassPath = controller.modelFromClassPath;
    modelWithAttribute2 = controller.modelWithAttribute2;
    modelWithAttribute1 = controller.modelWithAttribute1;
  }
}
