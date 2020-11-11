package com.airbnb.epoxy.preloadersample

import android.annotation.SuppressLint
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.epoxy.preloadersample.databinding.ActivityMainBinding
import com.bumptech.glide.Glide

class MainActivity : AppCompatActivity() {

    companion object {
        const val IMAGES_LIST_TAG = "images_list_tag_extra"
    }

    private val images = arrayOf(
        "https://i.redd.it/nbju2rir9xp11.jpg",
        "https://i.redditmedia.com/pA8syU4qzqyqn8ggXxZntuM-JIvBWGQvRo1c44yIvPs.jpg?s=b92a768940b1fa07b54e47d5dbe95f99",
        "https://i.redd.it/g7cv0byl5wp11.jpg",
        "https://i.redd.it/c2ozz6pdhyp11.jpg",
        "https://i.redd.it/y20c2oi9myp11.jpg",
        "https://i.redd.it/6b740x7ptyp11.jpg",
        "https://i.redd.it/qqi87wl8uup11.jpg",
        "https://i.redditmedia.com/7PuRyOWNt8vu4uygz0Xajlms9URmNp2_9z4xC-jSseA.jpg?s=3ddfadc3de602db2d98985470b36aa00",
        "https://i.redd.it/hpz1lhk1jzp11.jpg",
        "https://i.redd.it/kkzgwnb0rtt11.jpg",
        "https://i.redd.it/hosvfvb7yxp11.jpg",
        "https://i.redd.it/x0v1ixpp3yp11.jpg",
        "https://i.redd.it/u4ofokaxuyp11.jpg",
        "https://i.redditmedia.com/OHQZkFAaBOP-PNiQB-reJojV37CGu-TIa7wtoci0hXU.jpg?s=e2f84c1fc171090229a566104e7c77b0"
    )

    @SuppressLint("StaticFieldLeak")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Memory and disk cache is cleared to give accurate representation of load times
        Glide.get(this).clearMemory()

        object : AsyncTask<Unit, Unit, Unit>() {
            override fun doInBackground(vararg params: Unit?) {
                Glide.get(applicationContext).clearDiskCache()
            }
        }.execute()

        binding.buttonNoPreload.setOnClickListener {
            val intent = Intent(this, NoPreloadActivity::class.java)
            intent.putExtra(IMAGES_LIST_TAG, images)

            startActivity(intent)
        }

        binding.buttonPreload.setOnClickListener {
            val intent = Intent(this, PreloadActivity::class.java)
            intent.putExtra(IMAGES_LIST_TAG, images)

            startActivity(intent)
        }
    }
}
