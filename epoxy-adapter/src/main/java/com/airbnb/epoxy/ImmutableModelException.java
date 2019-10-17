package com.airbnb.epoxy;

import androidx.annotation.NonNull;

/**
 * Thrown if a model is changed after it is added to an {@link com.airbnb.epoxy.EpoxyController}.
 */
class ImmutableModelException extends RuntimeException {
  private static final String MODEL_CANNOT_BE_CHANGED_MESSAGE =
      "Epoxy attribute fields on a model cannot be changed once the model is added to a "
          + "controller. Check that these fields are not updated, or that the assigned objects "
          + "are not mutated, outside of the buildModels method. The only exception is if "
          + "the change is made inside an Interceptor callback. Consider using an interceptor"
          + " if you need to change a model after it is added to the controller and before it"
          + " is set on the adapter. If the model is already set on the adapter then you must"
          + " call `requestModelBuild` instead to recreate all models.";

  ImmutableModelException(EpoxyModel model, EpoxyController controller, int modelPosition) {
    this(model, controller, "", modelPosition);
  }

  ImmutableModelException(EpoxyModel model, EpoxyController controller,
      String descriptionOfWhenChangeHappened, int modelPosition) {
    super(buildMessage(model, controller, descriptionOfWhenChangeHappened, modelPosition));
  }

  @NonNull
  private static String buildMessage(EpoxyModel model, EpoxyController controller,
      String descriptionOfWhenChangeHappened, int modelPosition) {
    return new StringBuilder(descriptionOfWhenChangeHappened)
        .append(" Controller: ")
        .append(controller.getClass().getSimpleName())
        .append(" Position: ")
        .append(modelPosition)
        .append(" Model: ")
        .append(model.toString())
        .append("\n\n")
        .append(MODEL_CANNOT_BE_CHANGED_MESSAGE)
        .toString();
  }
}
