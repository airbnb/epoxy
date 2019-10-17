package com.airbnb.epoxy

import com.airbnb.epoxy.EpoxyController.ModelInterceptorCallback

internal class ModelDebugValidator(private val model: EpoxyModel<*>) {
    private val controllersAddedTo = mutableMapOf<EpoxyController, ControllerDetails>()

    /**
     * We save the hashCode at the time the model is added to this controller
     * so we can compare it to the hashCode at later points in time
     * in order to validate that it doesn't change and enforce immutability.
     */
    fun onAddedToController(controller: EpoxyController) {
        if (controller.isModelAddedMultipleTimes(model)) {
            throw  IllegalEpoxyUsage(
                "This model was already added to the controller at position "
                    + controller.getFirstIndexOfModelInBuildingList(model)
            )
        }


        // It is ok if this model was already added to the controller - it is fine if it
        // is reused (liked with the paged controller).
        // TODO For the differ to properly detect changes it compares the previous model with the new model.
        // If those are the same then it can't detect changes! We can't allow a reused model to
        // change until we can report those changes to the differ.
        val controllerDetails = controllersAddedTo.getOrPut(controller) { ControllerDetails() }
        controllerDetails.lastValidHashCode = model.hashCode()

        // The one time it is valid to change the model is during an interceptor callback. To support
        // that we need to update the hashCode after interceptors have been run.
        controller.addAfterInterceptorCallback(object : ModelInterceptorCallback {
            override fun onInterceptorsStarted(controller: EpoxyController) {
                controllerDetails.isCurrentlyInInterceptors = true
            }

            override fun onInterceptorsFinished(controller: EpoxyController) {
                controllerDetails.lastValidHashCode = model.hashCode()
                controllerDetails.isCurrentlyInInterceptors = false
            }
        })
    }

    fun validateMutationAllowed() {
        controllersAddedTo.forEach { (controller, details) ->
            // If the model was previously added to the controller, but the controller is now
            // rebuilding models and the model is reused and not yet added again, we can allow changes
            // so that the model can be updated.
            // There is a small edge case of the view on screen being improperly updated if it's
            // model changes like this, but that window is very small since the diff will be dispatched
            // very soon.
            // This allows a practical way to update cached models that are reused, such as with the
            // paged list controller.
            val isReusedInModelBuildingAndNotYetReAdded =
                controller.isBuildingModels && controller.getFirstIndexOfModelInBuildingList(model) == -1

            if (!details.isCurrentlyInInterceptors && !isReusedInModelBuildingAndNotYetReAdded) {
                throw ImmutableModelException(
                    model,
                    controller,
                    controller.getPosition()
                )
            }
        }
    }

    private fun EpoxyController.getPosition(): Int {
        return if (isBuildingModels) {
            getFirstIndexOfModelInBuildingList(model)
        } else {
            adapter.getModelPosition(model)
        }
    }

    fun validateStateHasNotChanged(descriptionOfChange: String, modelPosition: Int) {
        controllersAddedTo.forEach { (controller, details) ->
            if (!details.isCurrentlyInInterceptors && details.lastValidHashCode != model.hashCode()) {
                throw ImmutableModelException(model, controller, descriptionOfChange, modelPosition)
            }
        }
    }

    data class ControllerDetails(
        var lastValidHashCode: Int = 0,
        var isCurrentlyInInterceptors: Boolean = false
    )
}