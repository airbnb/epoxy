package com.airbnb.epoxy.utils

import android.content.Context
import android.content.pm.ApplicationInfo

@PublishedApi
internal val Context.isDebuggable: Boolean
    get() = (applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
