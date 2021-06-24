package com.airbnb.epoxy.kotlinsample

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.epoxy.EpoxyRecyclerView
import com.airbnb.epoxy.stickyheader.StickyHeaderLinearLayoutManager

/**
 * Shows an activity with sticky header using
 * [StickyHeaderLinearLayoutManager]
 */
class StickyHeaderActivity : AppCompatActivity() {
    private lateinit var recyclerView: EpoxyRecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity)

        findViewById<View>(R.id.epoxy_view_stub)?.visibility = View.GONE

        recyclerView = findViewById(R.id.recycler_view)

//        Sample usage when using [EpoxyAdapter]
//        recyclerView.layoutManager = StickyHeaderLinearLayoutManager(this)
//        recyclerView.adapter = StickyHeaderAdapter(this)

        recyclerView.layoutManager = StickyHeaderLinearLayoutManager(this)
        recyclerView.setController(StickyHeaderController(this))
        recyclerView.requestModelBuild()
    }
}
