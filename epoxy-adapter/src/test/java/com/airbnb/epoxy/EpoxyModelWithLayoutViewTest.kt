package com.airbnb.epoxy

import android.widget.Space
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class EpoxyModelWithLayoutViewTest {

    @Test
    fun `EpoxyModelWithLayoutView relays specified values from constructor`() {
        val layoutRes = 12
        val spanCount = 4
        val model = EpoxyModelWithLayoutView<Space>(layoutRes, spanCount)

        assert(model.defaultLayout == layoutRes)
        assert(model.getSpanSize(100, 0, 10) == spanCount)
    }
}
