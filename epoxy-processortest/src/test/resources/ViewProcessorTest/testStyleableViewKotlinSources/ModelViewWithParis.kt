package com.airbnb.epoxy

import android.content.Context
import android.view.View
import com.airbnb.epoxy.ModelView
import com.airbnb.epoxy.ModelProp
import com.airbnb.paris.annotations.Style
import com.airbnb.paris.annotations.Styleable
import kotlin.properties.Delegates

@Styleable
@ModelView
class ModelViewWithParis(context: Context?) : View(context) {
    @set:ModelProp
    var value = 0

    @set:ModelProp
    var delegatedProperty: Int by Delegates.observable(0) { _, _, _ ->

    }

    companion object {
        @JvmStatic
        @Style(isDefault = true)
        val headerStyle: com.airbnb.paris.styles.Style = modelViewWithParisStyle  {

        }
    }
}