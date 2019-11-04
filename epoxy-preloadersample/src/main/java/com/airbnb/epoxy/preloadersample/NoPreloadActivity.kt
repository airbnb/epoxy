package com.airbnb.epoxy.preloadersample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.list_activity.*

class NoPreloadActivity : AppCompatActivity() {

    private val images by lazy { intent.getStringArrayExtra(MainActivity.IMAGES_LIST_TAG) }
    private val controller by lazy { ImagesController(false) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.list_activity)

        recycler_view.setHasFixedSize(true)
        recycler_view.setController(controller)

        controller.setFilterDuplicates(true)
        controller.setData(images)
    }
}
