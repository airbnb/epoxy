package com.airbnb.epoxy.sample

import android.view.View
import com.airbnb.epoxy.AutoModel
import com.airbnb.epoxy.DataBindingEpoxyModel.DataBindingHolder
import com.airbnb.epoxy.EpoxyAsyncUtil
import com.airbnb.epoxy.TypedEpoxyController
import com.airbnb.epoxy.sample.models.CarouselModelGroup
import com.airbnb.epoxy.sample.views.HeaderViewModel_

class SampleController(private val callbacks: AdapterCallbacks) :
    TypedEpoxyController<List<CarouselData>>(
        EpoxyAsyncUtil.getAsyncBackgroundHandler(),
        EpoxyAsyncUtil.getAsyncBackgroundHandler()
    ) {
    interface AdapterCallbacks {
        fun onAddCarouselClicked()
        fun onClearCarouselsClicked()
        fun onShuffleCarouselsClicked()
        fun onChangeAllColorsClicked()
        fun onAddColorToCarouselClicked(carousel: CarouselData?)
        fun onClearCarouselClicked(carousel: CarouselData?)
        fun onShuffleCarouselColorsClicked(carousel: CarouselData?)
        fun onChangeCarouselColorsClicked(carousel: CarouselData?)
        fun onColorClicked(carousel: CarouselData?, colorPosition: Int)
    }

    @AutoModel
    lateinit var header: HeaderViewModel_

    @AutoModel
    lateinit var addButton: ButtonBindingModel_

    @AutoModel
    lateinit var clearButton: ButtonBindingModel_

    @AutoModel
    lateinit var shuffleButton: ButtonBindingModel_

    @AutoModel
    lateinit var changeColorsButton: ButtonBindingModel_

    override fun buildModels(carousels: List<CarouselData>) {
        header
            .title(R.string.epoxy)
            .caption(R.string.header_subtitle)
        // "addTo" is not needed since implicit adding is enabled
        // (https://github.com/airbnb/epoxy/wiki/Epoxy-Controller#implicit-adding)
        addButton
            .textRes(R.string.button_add)
            .clickListener { model: ButtonBindingModel_?, parentView: DataBindingHolder?, clickedView: View?, position: Int -> callbacks.onAddCarouselClicked() }
        clearButton
            .textRes(R.string.button_clear)
            .clickListener { v: View? -> callbacks.onClearCarouselsClicked() }
            .addIf(carousels.size > 0, this)
        shuffleButton
            .textRes(R.string.button_shuffle)
            .clickListener { v: View? -> callbacks.onShuffleCarouselsClicked() }
            .addIf(carousels.size > 1, this)
        changeColorsButton
            .textRes(R.string.button_change)
            .clickListener { v: View? -> callbacks.onChangeAllColorsClicked() }
            .addIf(carousels.size > 0, this)
        for (i in carousels.indices) {
            val carousel = carousels[i]
            add(CarouselModelGroup(carousel, callbacks))
        }
    }

    override fun onExceptionSwallowed(exception: RuntimeException) {
        // Best practice is to throw in debug so you are aware of any issues that Epoxy notices.
        // Otherwise Epoxy does its best to swallow these exceptions and continue gracefully
        throw exception
    }

    init {
        // Demonstrating how model building and diffing can be done in the background.
        // You can control them separately by passing in separate handler, as shown below.
        //    super(new Handler(), BACKGROUND_HANDLER);
//    super(BACKGROUND_HANDLER, new Handler());
        isDebugLoggingEnabled = true
    }
}
