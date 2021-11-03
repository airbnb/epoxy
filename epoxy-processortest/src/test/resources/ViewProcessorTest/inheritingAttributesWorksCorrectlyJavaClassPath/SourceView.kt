package com.airbnb.epoxy

import android.content.Context
import android.view.View
import com.airbnb.epoxy.processortest2.ProcessorTest2Model

@ModelView(
    autoLayout = ModelView.Size.MATCH_WIDTH_WRAP_HEIGHT,
    baseModelClass = ProcessorTest2Model::class
)
class SourceView(context: Context) : View(context) {

    var sectionId: String? = null
        @ModelProp set
}
