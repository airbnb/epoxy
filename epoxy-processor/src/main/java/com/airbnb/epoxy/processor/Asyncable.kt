package com.airbnb.epoxy.processor

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async

interface Asyncable {
    val logger: Logger
    val coroutineScope: CoroutineScope
    val coroutinesEnabled: Boolean

    suspend fun <T, R : Any> Iterable<T>.map(
        tag: String,
        parallel: Boolean = true,
        transform: suspend (T) -> R?
    ): List<R> {
        val parallelize = parallel && coroutinesEnabled
        return logger.measure(tag, numItems = count(), isParallel = parallelize) {
            if (!parallelize) {
                mapNotNull {
                    try {
                        transform(it)
                    } catch (e: Exception) {
                        logger.logError(e, "$tag failed")
                        null
                    }
                }
            } else {
                this@map.map {
                    coroutineScope.async { transform(it) }
                }.awaitAndLog(tag)
            }
        }
    }

    suspend fun <T> Iterable<T>.forEach(
        tag: String,
        parallel: Boolean = true,
        block: suspend (T) -> Unit
    ) {
        val parallelize = parallel && coroutinesEnabled
        logger.measure(tag, numItems = count(), isParallel = parallelize) {
            if (!parallelize) {
                forEach {
                    try {
                        block(it)
                    } catch (e: Exception) {
                        logger.logError(e, "$tag failed")
                    }
                }
            } else {
                map {
                    coroutineScope.async { block(it) }
                }.awaitAndLog(tag)
            }
        }
    }

    suspend fun <T : Any> Iterable<T>.filter(
        tag: String,
        parallel: Boolean = true,
        block: suspend (T) -> Boolean
    ): List<T> {
        val parallelize = parallel && coroutinesEnabled
        return logger.measure(tag, numItems = count(), isParallel = parallelize) {
            if (!parallelize) {
                filter {
                    try {
                        block(it)
                    } catch (e: Exception) {
                        logger.logError(e, "$tag failed")
                        false
                    }
                }
            } else {
                map {
                    coroutineScope.async { if (block(it)) it else null }
                }.awaitAndLog(tag)
            }
        }
    }

    suspend fun <K, V> Map<K, V>.forEach(
        tag: String,
        parallel: Boolean = true,
        block: suspend (K, V) -> Any?
    ) {
        val parallelize = parallel && coroutinesEnabled
        logger.measure(tag, numItems = size, isParallel = parallelize) {
            if (!parallelize) {
                forEach {
                    try {
                        block(it.key, it.value)
                    } catch (e: Exception) {
                        logger.logError(e, "$tag failed")
                    }
                }
            } else {
                map { (k, v) ->
                    coroutineScope.async { block(k, v) }
                }.awaitAndLog(tag)
                Unit
            }
        }
    }

    suspend fun <K, V, R : Any> Map<K, V>.map(
        tag: String,
        parallel: Boolean = true,
        transform: suspend (K, V) -> R?
    ): List<R> {
        val parallelize = parallel && coroutinesEnabled
        return logger.measure(tag, numItems = count(), isParallel = parallelize) {
            if (!parallelize) {
                mapNotNull {
                    try {
                        transform(it.key, it.value)
                    } catch (e: Exception) {
                        logger.logError(e, "$tag failed")
                        null
                    }
                }
            } else {
                this@map.map {
                    coroutineScope.async { transform(it.key, it.value) }
                }.awaitAndLog(tag)
            }
        }
    }

    private suspend fun <T : Any> List<Deferred<T?>>.awaitAndLog(tag: String): List<T> {
        return mapNotNull {
            try {
                it.await()
            } catch (e: Exception) {
                logger.logError(e, "$tag failed")
                null
            }
        }
    }
}
