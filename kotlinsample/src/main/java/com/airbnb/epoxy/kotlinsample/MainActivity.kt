package com.airbnb.epoxy.kotlinsample

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.epoxy.EpoxyRecyclerView
import com.airbnb.epoxy.EpoxyVisibilityTracker
import com.airbnb.epoxy.kotlinsample.helpers.carouselNoSnapBuilder
import com.airbnb.epoxy.kotlinsample.models.ItemDataClass
import com.airbnb.epoxy.kotlinsample.models.ItemViewBindingDataClass
import com.airbnb.epoxy.kotlinsample.models.carouselItemCustomView
import com.airbnb.epoxy.kotlinsample.models.coloredSquareView
import com.airbnb.epoxy.kotlinsample.models.group
import com.airbnb.epoxy.kotlinsample.models.itemCustomView
import com.airbnb.epoxy.kotlinsample.models.itemEpoxyHolder
import com.airbnb.epoxy.kotlinsample.models.itemViewBindingEpoxyHolder

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: EpoxyRecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity)

        recyclerView = findViewById(R.id.recycler_view)

        // Attach the visibility tracker to the RecyclerView. This will enable visibility events.
        val epoxyVisibilityTracker = EpoxyVisibilityTracker()
        epoxyVisibilityTracker.setPartialImpressionThresholdPercentage(75)
        epoxyVisibilityTracker.attach(recyclerView)

        recyclerView.withModels {

            group {
                id("epoxyModelGroupDsl")
                layout(R.layout.vertical_linear_group)

                coloredSquareView {
                    id("coloredSquareView 1")
                    color(Color.DKGRAY)
                }

                coloredSquareView {
                    id("coloredSquareView 2")
                    color(Color.GRAY)
                }

                coloredSquareView {
                    id("coloredSquareView 3")
                    color(Color.LTGRAY)
                }
            }

            for (i in 0 until 100) {
                dataBindingItem {
                    id("data binding $i")
                    text("this is a data binding model2")
                    onClick { _ ->
                        Toast.makeText(this@MainActivity, "clicked", Toast.LENGTH_LONG).show()
                    }
                    onVisibilityStateChanged { model, view, visibilityState ->
                        Log.d(TAG, "$model -> $visibilityState")
                    }
                }

                itemCustomView {
                    id("custom view $i")
                    color(Color.GREEN)
                    title("Open sticky header activity")
                    listener { _ ->
                        Toast.makeText(this@MainActivity, "clicked", Toast.LENGTH_LONG).show()
                        startActivity(Intent(this@MainActivity, StickyHeaderActivity::class.java))
                    }
                }

                itemEpoxyHolder {
                    id("view holder $i")
                    title("this is a View Holder item")
                    listener {
                        Toast.makeText(this@MainActivity, "clicked", Toast.LENGTH_LONG)
                            .show()
                    }
                }

                itemViewBindingEpoxyHolder {
                    id("view binding $i")
                    title("This is a ViewBinding item")
                    listener {
                        Toast.makeText(this@MainActivity, "clicked", Toast.LENGTH_LONG)
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
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}
