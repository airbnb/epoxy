package com.airbnb.epoxy.sample

import android.app.Application
import com.airbnb.mvrx.Mavericks

class SampleApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        Mavericks.initialize(this)
    }
}