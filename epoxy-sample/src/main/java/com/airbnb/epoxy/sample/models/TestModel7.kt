package com.airbnb.epoxy.sample.models

import android.view.View
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModel
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.sample.R

@EpoxyModelClass(layout = R.layout.model_color)
abstract class TestModel7 : EpoxyModel<View>() {

    @EpoxyAttribute
    var num: Int = 0
    @EpoxyAttribute
    var num2: Int = 0
    @EpoxyAttribute
    var num3: Int = 0
    @EpoxyAttribute
    var num4: Int = 0
    @EpoxyAttribute
    var num5: Int = 0
}
