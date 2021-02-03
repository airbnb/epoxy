package com.airbnb.epoxy.paging3

import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class ListPagingSource<T : Any>(
    private val coroutineContext: CoroutineContext,
    private val defaultDelay: Long,
    private val data: List<T>
) :
    PagingSource<Int, T>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
        return withContext(coroutineContext) {
            delay(defaultDelay)
            val key = params.key ?: 0
            LoadResult.Page(data.subList(key, key + params.loadSize), null, key + params.loadSize)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, T>): Int? = null

    override val jumpingSupported: Boolean
        get() = super.jumpingSupported
}
