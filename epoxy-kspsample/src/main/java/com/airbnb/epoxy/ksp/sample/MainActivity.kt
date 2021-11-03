package com.airbnb.epoxy.ksp.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.epoxy.EpoxyRecyclerView
import com.airbnb.epoxy.ksp.sample.epoxyviews.headerView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<EpoxyRecyclerView>(R.id.epoxy_recycler_view).withModels {
            headerView {
                id("header")
                title("Hello World")
            }
        }
    }
}
