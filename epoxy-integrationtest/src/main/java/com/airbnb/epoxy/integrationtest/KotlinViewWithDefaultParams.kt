package com.airbnb.epoxy.integrationtest

import android.content.Context
import android.view.View
import com.airbnb.epoxy.ModelProp
import com.airbnb.epoxy.ModelView
import com.airbnb.epoxy.TextProp

@ModelView(autoLayout = ModelView.Size.WRAP_WIDTH_MATCH_HEIGHT)
class KotlinViewWithDefaultParams(context: Context) : View(context) {

    @JvmOverloads
    @ModelProp
    fun someIntWithDefault(num: Int = IntDefault) {
        someIntWithDefaultValue = num
    }

    var someIntWithDefaultValue = 0

    @JvmOverloads
    @TextProp
    fun someTextWithDefault(msg: CharSequence = TextDefault) {
        someTextWithDefaultValue = msg
    }

    var someTextWithDefaultValue: CharSequence = ""

    @JvmOverloads
    @ModelProp(group = "group")
    fun propInGroupWithDefaultParam(num: Int? = IntGroupDefault) {
        groupValue = num
    }

    @ModelProp(group = "group")
    fun otherPropInGroup(num: Int) {
        groupValue = num
    }

    // Make sure that overloads with the same function name and param name are distinguished correctly by type
    @JvmOverloads
    @ModelProp
    fun setImage(image: Map<String, Int> = emptyMap()) {
    }

    @ModelProp
    fun setImage(image: String) {
    }

    @ModelProp
    fun somePropWithoutDefault(num: Int) {
    }

    var groupValue: Int? = null

    companion object {
        const val IntDefault = 2
        const val IntGroupDefault = 3
        const val TextDefault = "hello world"
    }
}
