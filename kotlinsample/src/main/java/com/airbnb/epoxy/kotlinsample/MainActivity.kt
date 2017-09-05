package com.airbnb.epoxy.kotlinsample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.airbnb.epoxy.EpoxyController


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        MyController().requestModelBuild()
        // work in progress
    }
}


class MyController : EpoxyController() {
    override fun buildModels() {

    }
}




