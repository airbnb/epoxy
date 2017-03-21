package com.airbnb.epoxy;

import android.support.annotation.NonNull;

/**
 * Thrown if a model is changed after it is added to an {@link com.airbnb.epoxy.EpoxyController}.
 */
class ImmutableModelException extends RuntimeException {
  private static final String MODEL_CANNOT_BE_CHANGED_MESSAGE =
      "A model cannot be changed once it is added to a controller. The only exception is if "
          + "the change is made inside an Interceptor callback. Consider using an interceptor"
          + " if you need to change a model after it is added to the controller and before it"
          + " is set on the adapter. If the model is already set on the adapter then you must"
          + " call `requestModelBuild` instead to recreate all models.";

  ImmutableModelException(EpoxyController controller, EpoxyModel model) {
    this(controller, model, "");
  }

  ImmutableModelException(EpoxyController controller, EpoxyModel model,
      String descriptionOfWhenChangeHappened) {
    super(buildMessage(controller, model, descriptionOfWhenChangeHappened));
  }

  @NonNull
  private static String buildMessage(EpoxyController controller, EpoxyModel model,
      String descriptionOfWhenChangeHappened) {
    return new StringBuilder(descriptionOfWhenChangeHappened)
        .append(" Position: ")
        .append(getModelPosition(controller, model))
        .append(" Model: ")
        .append(model.toString())
        .append("\n\n")
        .append(MODEL_CANNOT_BE_CHANGED_MESSAGE)
        .toString();
  }

  private static int getModelPosition(EpoxyController controller, EpoxyModel model) {
    if (controller.isBuildingModels()) {
      return controller.getIndexOfModelInBuildingList(model);
    }

    return controller.getAdapter().getModelPosition(model);
  }
}
