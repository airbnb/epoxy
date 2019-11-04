package com.airbnb.epoxy

import android.view.View
import com.airbnb.epoxy.integrationtest.ClassWithNestedInterface_NestedInterfaceModel_
import com.airbnb.epoxy.integrationtest.InterfaceForView2Model_
import com.airbnb.epoxy.integrationtest.InterfaceForViewModel_
import com.airbnb.epoxy.integrationtest.ViewWithInterface2Model_
import com.airbnb.epoxy.integrationtest.ViewWithInterfaceModel_
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [21])
class ModelViewInterfaceTest {

    @Test
    fun interfaceIsGeneratedForView() {
        val model: InterfaceForViewModel_ = ViewWithInterfaceModel_()
        model.text("")
    }

    @Test
    fun generatedInterfaceIncludesBaseModelProps() {
        val model: InterfaceForViewModel_ = ViewWithInterfaceModel_()
        model.id(1)
            .id("sfd")
            .spanSizeOverride(null)
    }

    @Test
    fun textPropMethodsAreOnInterface() {
        val model: InterfaceForViewModel_ = ViewWithInterfaceModel_()
        model.text("")
            .text2("asdf")
            .text2(3)
            .text2(3, "arg")
    }

    @Test
    fun multipleModelsDeclareInterface() {
        val model1: InterfaceForViewModel_ = ViewWithInterfaceModel_()
        val model2: InterfaceForViewModel_ = ViewWithInterface2Model_()

        model1.text("")
            .text2("")
            .id(1)

        model2.text("")
            .text2("")
            .id(1)
    }

    @Test
    fun viewCanHaveMultipleInterfaces() {
        val model = ViewWithInterfaceModel_()
        val interface1 = model as InterfaceForViewModel_
        val interface2 = model as InterfaceForView2Model_

        interface1.text("")
        interface2.listener(null)
    }

    @Test
    fun nestedInterfaceWorks() {
        val model = ViewWithInterfaceModel_() as ClassWithNestedInterface_NestedInterfaceModel_
        model.listener(View.OnClickListener { })
    }
}
