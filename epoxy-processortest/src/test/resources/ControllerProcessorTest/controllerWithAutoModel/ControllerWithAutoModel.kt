package com.airbnb.epoxy.adapter

import android.view.View
import com.airbnb.epoxy.AutoModel
import com.airbnb.epoxy.BasicModelWithAttribute_
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.processortest2.ProcessorTest2Model

class ControllerWithAutoModel : EpoxyController() {
    @AutoModel
    lateinit var modelWithAttribute1: BasicModelWithAttribute_

    @AutoModel
    lateinit var modelWithAttribute2: BasicModelWithAttribute_

    @AutoModel
    lateinit var modelFromClassPath: ProcessorTest2Model<View>

    protected override fun buildModels() {}
}
