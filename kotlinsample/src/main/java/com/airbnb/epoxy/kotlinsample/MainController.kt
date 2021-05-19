package com.airbnb.epoxy.kotlinsample

import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.widget.Toast
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.EpoxyModel
import com.airbnb.epoxy.ModelCollector
import com.airbnb.epoxy.group
import com.airbnb.epoxy.kotlinsample.helpers.carouselNoSnapBuilder
import com.airbnb.epoxy.kotlinsample.models.ItemDataClass
import com.airbnb.epoxy.kotlinsample.models.ItemViewBindingDataClass
import com.airbnb.epoxy.kotlinsample.models.carouselItemCustomView
import com.airbnb.epoxy.kotlinsample.models.coloredSquareView
import com.airbnb.epoxy.kotlinsample.models.decoratedLinearGroup
import com.airbnb.epoxy.kotlinsample.models.itemCustomView
import com.airbnb.epoxy.kotlinsample.models.itemEpoxyHolder
import com.airbnb.epoxy.kotlinsample.models.itemViewBindingEpoxyHolder

class MainController(
    private val mainActivity: MainActivity
) : EpoxyController() {

    override fun buildModels() {
        group {
            id("epoxyModelGroupDsl")
            layout(R.layout.vertical_linear_group)

            coloredSquareView {
                id("coloredSquareView 1")
                color(Color.DKGRAY)
                onVisibilityStateChanged { model, _, visibilityState ->
                    Log.d(TAG, "$model -> $visibilityState")
                }
            }

            coloredSquareView {
                id("coloredSquareView 2")
                color(Color.GRAY)
            }

            coloredSquareView {
                id("coloredSquareView 3")
                color(Color.LTGRAY)
            }

            carouselNoSnapBuilder {
                id("nested carousel")
                val lastPage = 10
                for (i in 0 until lastPage) {
                    carouselItemCustomView {
                        id("nested carousel $i")
                        title("Page $i / $lastPage")
                        onVisibilityStateChanged { model, _, visibilityState ->
                            this@MainController.logVisibilityState(model, visibilityState, i)
                        }
                    }
                }
            }
        }

        decoratedLinearGroup {
            id("epoxyModelGroupWithLayoutDsl")

            this@MainController.addColoredSquareView(Color.DKGRAY)
            this@MainController.addColoredSquareView(Color.GRAY)
            this@MainController.addColoredSquareView(Color.LTGRAY)
        }

        for (i in 0 until 100) {
            dataBindingItem {
                id("data binding $i")
                text("this is a data binding model2")
                onClick { _ ->
                    Toast.makeText(this@MainController.mainActivity, "clicked", Toast.LENGTH_LONG).show()
                }
                onVisibilityStateChanged { model, _, visibilityState ->
                    this@MainController.logVisibilityState(model, visibilityState)
                }
            }

            itemCustomView {
                id("custom view $i")
                color(Color.GREEN)
                title("Open sticky header activity")
                listener { _ ->
                    Toast.makeText(this@MainController.mainActivity, "clicked", Toast.LENGTH_LONG).show()
                    this@MainController.mainActivity.startActivity(Intent(this@MainController.mainActivity, StickyHeaderActivity::class.java))
                }
            }

            itemEpoxyHolder {
                id("view holder $i")
                title("this is a View Holder item")
                listener {
                    Toast.makeText(this@MainController.mainActivity, "clicked", Toast.LENGTH_LONG)
                        .show()
                }
            }

            itemViewBindingEpoxyHolder {
                id("view binding $i")
                title("This is a ViewBinding item")
                listener {
                    Toast.makeText(this@MainController.mainActivity, "clicked", Toast.LENGTH_LONG)
                        .show()
                }
            }

            carouselNoSnapBuilder {
                id("carousel $i")
                val lastPage = 10
                for (j in 0 until lastPage) {
                    carouselItemCustomView {
                        id("carousel $i-$j")
                        title("Page $j / $lastPage")
                    }
                }
            }

            // Since data classes do not use code generation, there's no extension generated here
            ItemDataClass("this is a Data Class Item")
                .id("data class $i")
                .addTo(this)

            ItemViewBindingDataClass("This is a Data Class Item using ViewBinding")
                .id("data class view binding $i")
                .addTo(this)
        }
    }

    private fun ModelCollector.addColoredSquareView(color: Int) {
        coloredSquareView {
            id("coloredSquareView-$color")
            color(Color.DKGRAY)
            onVisibilityStateChanged { model, _, visibilityState ->
                this@MainController.logVisibilityState(model, visibilityState)
            }
        }
    }

    private fun logVisibilityState(model: EpoxyModel<*>, visibilityState: Int, position: Int? = null) {
        Log.d(TAG, "$model -> $visibilityState position=$position")
    }

    companion object {
        private const val TAG = "MainController"
    }
}