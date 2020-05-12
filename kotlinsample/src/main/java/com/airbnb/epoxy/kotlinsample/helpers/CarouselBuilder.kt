package com.airbnb.epoxy.kotlinsample.helpers

import com.airbnb.epoxy.BaseEpoxyController
import com.airbnb.epoxy.CarouselModelBuilder
import com.airbnb.epoxy.CarouselModel_
import com.airbnb.epoxy.EpoxyModel

/**
 * Example that illustrate how to add a builder for nested list (ex: carousel) that allow building
 * it using DSL :
 *
 *   carouselBuilder {
 *     id(...)
 *     for (...) {
 *       carouselItemCustomView {
 *         id(...)
 *       }
 *     }
 *   }
 *
 * @link https://github.com/airbnb/epoxy/issues/847
 */
fun BaseEpoxyController.carouselBuilder(builder: EpoxyCarouselBuilder.() -> Unit): CarouselModel_ {
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
