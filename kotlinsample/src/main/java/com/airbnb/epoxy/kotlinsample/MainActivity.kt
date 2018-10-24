package com.airbnb.epoxy.kotlinsample

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.EpoxyRecyclerView
import com.airbnb.epoxy.EpoxyVisibilityTracker
import com.airbnb.epoxy.OnModelVisibilityStateChangedListener
import com.airbnb.epoxy.kotlinsample.models.ItemDataClass
import com.airbnb.epoxy.kotlinsample.models.itemCustomView
import com.airbnb.epoxy.kotlinsample.models.itemEpoxyHolder

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: EpoxyRecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity)

        recyclerView = findViewById(R.id.recycler_view)

        // Attach the visibility tracker to the RecyclerView. This will enable visibility events.
        val epoxyVisibilityTracker = EpoxyVisibilityTracker()
        epoxyVisibilityTracker.attach(recyclerView)

        recyclerView.withModels {

            for(i in 0 until 100) {
                dataBindingItem {
                    id("data binding $i")
                    text("this is a data binding model")
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
                    title("this is a green custom view item")
                    listener { _ ->
                        Toast.makeText(this@MainActivity, "clicked", Toast.LENGTH_LONG).show()
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

                // Since data classes do not use code generation, there's no extension generated here
                ItemDataClass("this is a Data Class Item")
                    .id("data class $i")
                    .addTo(this)
            }
        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}

fun EpoxyRecyclerView.withModels(buildModelsCallback: EpoxyController.() -> Unit) {
    setControllerAndBuildModels(object : EpoxyController() {
        override fun buildModels() {
            buildModelsCallback()
        }
    })
}

