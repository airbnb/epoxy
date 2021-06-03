package com.airbnb.epoxy.kotlinsample

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.epoxy.EpoxyModel
import com.airbnb.epoxy.EpoxyRecyclerView
import com.airbnb.epoxy.EpoxyTouchHelper
import com.airbnb.epoxy.TypedEpoxyController

class DragAndDropActivity : AppCompatActivity() {
    private lateinit var recyclerView: EpoxyRecyclerView
    private var fruits = mutableListOf(
        "Apples",
        "Blueberries",
        "Bananas",
        "Oranges",
        "Dragon fruit",
        "Mango",
        "Avocado",
        "Lychee"
    )

    private var epoxyController = object : TypedEpoxyController<List<String>>() {

        override fun buildModels(data: List<String>?) {
            data?.forEach { fruit ->
                dataBindingItem {
                    id("data binding $fruit")
                    text(fruit)
                    onClick { _ ->
                        Toast.makeText(
                            this@DragAndDropActivity,
                            "clicked $fruit",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    onVisibilityStateChanged { model, view, visibilityState ->
                        Log.d(TAG, "$model -> $visibilityState")
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity)
        findViewById<View>(R.id.epoxy_view_stub)?.visibility = View.GONE

        recyclerView = findViewById(R.id.recycler_view)

        EpoxyTouchHelper.initDragging(epoxyController)
            .withRecyclerView(recyclerView)
            .forVerticalList()
            .forAllModels()
            .andCallbacks(object : EpoxyTouchHelper.DragCallbacks<EpoxyModel<*>>() {
                override fun onModelMoved(
                    fromPosition: Int,
                    toPosition: Int,
                    modelBeingMoved: EpoxyModel<*>?,
                    itemView: View?
                ) {
                    Log.d(TAG, "onModelMoved from:$fromPosition -> to:$toPosition")
                    val removed = fruits.removeAt(fromPosition)
                    fruits.add(toPosition, removed)
                    epoxyController.setData(fruits)
                }
            })

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setController(epoxyController)
        epoxyController.setData(fruits)
    }

    companion object {
        private const val TAG = "DragAndDropActivity"
    }
}
