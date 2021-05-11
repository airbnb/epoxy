package com.airbnb.epoxy.kotlinsample

import android.content.Context
import android.widget.Toast
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.kotlinsample.models.StickyItemEpoxyHolder
import com.airbnb.epoxy.kotlinsample.models.itemEpoxyHolder
import com.airbnb.epoxy.kotlinsample.models.stickyItemEpoxyHolder
import com.airbnb.epoxy.stickyheader.StickyHeaderCallbacks

/**
 * Showcases [EpoxyController] with sticky header support
 */
class StickyHeaderController(
    private val context: Context
) : EpoxyController(), StickyHeaderCallbacks {

    override fun buildModels() {
        for (i in 0 until 100) {
            when {
                i % 5 == 0 -> stickyItemEpoxyHolder {
                    id("sticky-header $i")
                    title("Sticky header $i")
                    listener {
                        Toast.makeText(this@StickyHeaderController.context, "clicked", Toast.LENGTH_LONG).show()
                    }
                }
                else -> itemEpoxyHolder {
                    id("view holder $i")
                    title("this is a View Holder item")
                    listener {
                        Toast.makeText(this@StickyHeaderController.context, "clicked", Toast.LENGTH_LONG)
                            .show()
                    }
                }
            }
        }
    }

    // Feel feel to use any logic here to determine if the [position] is sticky view or not
    override fun isStickyHeader(position: Int) = adapter.getModelAtPosition(position) is StickyItemEpoxyHolder
}
