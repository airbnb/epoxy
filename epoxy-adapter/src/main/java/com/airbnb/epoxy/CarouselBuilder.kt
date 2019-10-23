package com.airbnb.epoxy

fun BaseEpoxyController.epoxyCarouselBuilder(builder: EpoxyCarouselBuilder.() -> Unit): CarouselModel_ {
    val carouselBuilder = EpoxyCarouselBuilder().apply { builder() }
    add(carouselBuilder.carouselModel)
    return carouselBuilder.carouselModel
}

class EpoxyCarouselBuilder(
    internal val carouselModel: CarouselModel_ = CarouselModel_()
) : BaseEpoxyController(), CarouselModelBuilder by carouselModel {
    private val models = mutableListOf<EpoxyModel<*>>()

    override fun add(model: EpoxyModel<*>) {
        models.add(model)

        // Set models list every time a model is added so that it can run debug validations to
        // ensure it is still valid to mutate the carousel model.
        models(models)
    }

    override fun buildModels() {
        error("This should not be called")
    }
}
