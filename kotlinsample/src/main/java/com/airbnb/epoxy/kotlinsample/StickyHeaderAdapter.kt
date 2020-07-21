package com.airbnb.epoxy.kotlinsample

import android.content.Context
import android.widget.Toast
import com.airbnb.epoxy.EpoxyAdapter
import com.airbnb.epoxy.kotlinsample.models.ItemEpoxyHolder_
import com.airbnb.epoxy.kotlinsample.models.StickyItemEpoxyHolder
import com.airbnb.epoxy.kotlinsample.models.StickyItemEpoxyHolder_
import com.airbnb.epoxy.stickyheader.StickyHeaderCallbacks

/**
 * Showcases [EpoxyAdapter] with sticky header support
 */
class StickyHeaderAdapter(
    private val context: Context
) : EpoxyAdapter(), StickyHeaderCallbacks {

    init {
        enableDiffing()
        for (i in 0 until 100) {
            addModel(
                when {
                    i % 5 == 0 -> StickyItemEpoxyHolder_().apply {
                        id("sticky-header $i")
                        title("Sticky header $i")
                        listener {
                            Toast.makeText(context, "clicked", Toast.LENGTH_LONG).show()
                        }
                    }
                    else -> ItemEpoxyHolder_().apply {
                        id("view holder $i")
                        title("this is a View Holder item")
                        listener {
                            Toast.makeText(context, "clicked", Toast.LENGTH_LONG)
                                .show()
                        }
                    }
                }
            )
        }
        notifyModelsChanged()
    }

    // Feel feel to use any logic here to determine if the [position] is sticky view or not
    override fun isStickyHeader(position: Int) = models[position] is StickyItemEpoxyHolder
}
