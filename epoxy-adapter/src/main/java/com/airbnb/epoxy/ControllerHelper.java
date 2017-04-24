package com.airbnb.epoxy;

import java.util.List;

/**
 * A helper class for {@link EpoxyController} to handle {@link
 * com.airbnb.epoxy.AutoModel} models. This is only implemented by the generated classes created the
 * annotation processor.
 */
public abstract class ControllerHelper<T extends EpoxyController> {
  public abstract void resetAutoModels();

  protected void validateModelHashCodesHaveNotChanged(T controller) {
    List<EpoxyModel<?>> currentModels = controller.getAdapter().getCopyOfModels();

    for (int i = 0; i < currentModels.size(); i++) {
      EpoxyModel model = currentModels.get(i);
      model.validateStateHasNotChangedSinceAdded(
          "Model has changed since it was added to the controller.", i);
    }
  }

  protected void setControllerToStageTo(EpoxyModel<?> model, T controller) {
    model.controllerToStageTo = controller;
  }
}
