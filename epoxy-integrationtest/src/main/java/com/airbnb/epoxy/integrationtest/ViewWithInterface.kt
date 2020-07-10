package com.airbnb.epoxy.integrationtest

import android.content.Context
import android.view.View
import androidx.annotation.Nullable
import com.airbnb.epoxy.CallbackProp
import com.airbnb.epoxy.ModelProp
import com.airbnb.epoxy.ModelView
import com.airbnb.epoxy.ModelView.Size
import com.airbnb.epoxy.TextProp

@ModelView(autoLayout = Size.WRAP_WIDTH_MATCH_HEIGHT)
class ViewWithInterface(context: Context) :
    View(context),
    InterfaceForView,
    InterfaceForView2,
    ClassWithNestedInterface.NestedInterface,
    InterfaceWithNoPropMethods {

    override fun getSomething() = 5

    @ModelProp
    override fun valueOnNestedInterface(value: Int) {
    }

    @ModelProp
    override fun setText(title: CharSequence?) {
    }

    @TextProp
    override fun setText2(title: CharSequence?) {
    }

    @CallbackProp
    override fun setListener(title: View.OnClickListener?) {
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
        // It is intended that this override has the nullable annotation and the other doesn't, to
        // check that it doesn't affect the interface
    }

    @TextProp
    override fun setText2(@Nullable title: CharSequence?) {
    }

    @CallbackProp
    override fun setListener(title: View.OnClickListener?) {
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
    fun setListener(title: View.OnClickListener?)
}

interface InterfaceWithNoPropMethods {
    fun getSomething(): Int
}

class ClassWithNestedInterface {
    interface NestedInterface {
        fun valueOnNestedInterface(value: Int)
    }
}
