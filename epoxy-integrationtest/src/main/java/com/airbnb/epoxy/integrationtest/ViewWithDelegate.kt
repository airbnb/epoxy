package com.airbnb.epoxy.integrationtest

import android.content.Context
import android.view.View
import com.airbnb.epoxy.ModelProp
import com.airbnb.epoxy.ModelView

@ModelView(autoLayout = ModelView.Size.WRAP_WIDTH_MATCH_HEIGHT)
class ViewWithDelegate @JvmOverloads constructor(
    context: Context,
    implementation: InterfaceImplementation = InterfaceImplementation()
) : View(context), InterfaceWithModelProp by implementation

interface InterfaceWithModelProp {

    @set:ModelProp
    var flag: Boolean
}

class InterfaceImplementation : InterfaceWithModelProp {

    override var flag: Boolean = false
}
