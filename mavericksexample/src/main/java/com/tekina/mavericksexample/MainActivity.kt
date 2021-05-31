package com.tekina.mavericksexample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (!commit) {
            commit = true
            supportFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, MyFragment())
                .commit()
        }
    }

    companion object {
        var commit = false
    }
}