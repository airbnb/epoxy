/*
 * Copyright 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.airbnb.epoxy.paging

import androidx.paging.PositionalDataSource

/**
 * Simple data source that works with a given list and its loading can be stopped / started.
 */
class ListDataSource<T>(
    private val data: List<T>
) : PositionalDataSource<T>() {
    private var pendingActions = arrayListOf<() -> Unit>()
    private var running = true

    private fun compute(f: () -> Unit) {
        if (running) {
            f()
        } else {
            pendingActions.add(f)
        }
    }

    fun start() {
        running = true
        val pending = pendingActions
        pendingActions = arrayListOf()
        pending.forEach(this::compute)
    }

    fun stop() {
        running = false
    }

    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<T>) {
        compute {
            callback.onResult(
                data.subList(
                    params.startPosition,
                    Math.min(data.size, params.startPosition + params.loadSize)
                )
            )
        }
    }

    override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<T>) {
        val start = computeInitialLoadPosition(params, data.size)
        val itemCnt = computeInitialLoadSize(params, start, data.size)
        callback.onResult(
            data.subList(start, start + itemCnt),
            start,
            data.size
        )
    }
}
