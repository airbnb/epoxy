package com.airbnb.epoxy.kotlinsample

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.epoxy.EpoxyRecyclerView
import com.airbnb.epoxy.EpoxyVisibilityTracker
import com.airbnb.epoxy.epoxyView
import com.airbnb.epoxy.kotlinsample.models.itemCustomView

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: EpoxyRecyclerView

    private val viewBinder by epoxyView(
        viewId = R.id.epoxy_view_stub,
        initializer = { },
        modelProvider = {
            itemCustomView {
                id("view binder")
                color(Color.BLACK)
                title("Epoxy view outside of the RecyclerView")
                onVisibilityStateChanged { _, _, visibilityState ->
                    Log.d(TAG, "ViewBinder -> $visibilityState")
                }
                listener { _ ->
                    Toast.makeText(this@MainActivity, "ViewBinder clicked", Toast.LENGTH_LONG).show()
                }
            }
        },
        useVisibilityTracking = true
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity)

        viewBinder.invalidate()

        recyclerView = findViewById(R.id.recycler_view)

        // Attach the visibility tracker to the RecyclerView. This will enable visibility events.
        val epoxyVisibilityTracker = EpoxyVisibilityTracker()
        epoxyVisibilityTracker.partialImpressionThresholdPercentage = 75
        epoxyVisibilityTracker.attach(recyclerView)

        recyclerView.adapter = MainController(this).adapter
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}
