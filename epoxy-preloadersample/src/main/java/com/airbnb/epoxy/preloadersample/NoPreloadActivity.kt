package com.airbnb.epoxy.preloadersample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.epoxy.preloadersample.databinding.ListActivityBinding

class NoPreloadActivity : AppCompatActivity() {

    private val images by lazy { intent.getStringArrayExtra(MainActivity.IMAGES_LIST_TAG) }
    private val controller by lazy { ImagesController(false) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ListActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerView.apply {
            setController(controller)
            setHasFixedSize(true)
        }

        controller.setFilterDuplicates(true)
        controller.setData(images)
    }
}
