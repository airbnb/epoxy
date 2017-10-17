package com.airbnb.epoxy.integrationtest

import android.content.*
import android.support.annotation.*
import android.view.*
import com.airbnb.epoxy.*
import com.airbnb.epoxy.ModelView.Size

@ModelView(autoLayout = Size.WRAP_WIDTH_MATCH_HEIGHT)
class ViewWithInterface(context: Context) : View(context), InterfaceForView, InterfaceForView2 {

    @ModelProp
    override fun setText(title: CharSequence?) {

    }

    @TextProp
    override fun setText2(title: CharSequence?) {

    }

    @ModelProp
    override fun setText3(title: String) {

    }

    @ModelProp
    fun nonInterfaceProp(title: Int) {
        // method has different type from method of same name in other view
    }
}

@ModelView(autoLayout = Size.WRAP_WIDTH_MATCH_HEIGHT)
class ViewWithInterface2(context: Context) : View(context), InterfaceForView, InterfaceForView2 {

    @TextProp
    override fun setText(@Nullable title: CharSequence?) {
        // It is intended that this override has the nullable annotation and the other doesn't, to check that it doesn't affect the interface
    }

    @TextProp
    override fun setText2(@Nullable title: CharSequence?) {

    }

    @ModelProp
    override fun setText3(title: String) {

    }

    @ModelProp
    fun nonInterfaceProp(title: String) {

    }
}

interface InterfaceForView {
    fun setText(title: CharSequence?)
    fun setText2(title: CharSequence?)
}

interface InterfaceForView2 {
    fun setText3(title: String)
}
