package com.airbnb.epoxy.composeinterop.maverickssample

import android.app.Application
import com.airbnb.mvrx.Mavericks

class SampleApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Mavericks.initialize(this)
    }
}
