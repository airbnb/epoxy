package com.airbnb.epoxy.compose.sample

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        findViewById<View>(R.id.button).setOnClickListener {
            startActivity(Intent(this, ComposableInteropActivity::class.java))
        }

        findViewById<View>(R.id.button2).setOnClickListener {
            startActivity(Intent(this, EpoxyInteropActivity::class.java))
        }
    }
}
