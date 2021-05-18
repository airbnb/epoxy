package com.airbnb.epoxy.compose.sample

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun onComposableInteropClicked(view: View) {
        startActivity(Intent(this, ComposableInteropActivity::class.java))
    }

    fun onEpoxyInteropClicked(view: View) {
        startActivity(Intent(this, EpoxyInteropActivity::class.java))
    }
}
