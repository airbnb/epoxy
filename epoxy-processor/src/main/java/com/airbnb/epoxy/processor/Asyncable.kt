package com.airbnb.epoxy.processor

interface Asyncable {
    val logger: Logger

    fun <T, R : Any> Iterable<T>.mapNotNull(
        tag: String,
        parallel: Boolean = true,
        transform: (T) -> R?
    ): List<R> {
        return logger.measure(tag, numItems = count()) {
            this@mapNotNull.mapNotNull {
                try {
                    transform(it)
                } catch (e: Exception) {
                    logger.logError(e, "$tag failed")
                    null
                }
            }
        }
    }

    fun <T> Iterable<T>.forEach(
        tag: String,
        parallel: Boolean = true,
        block: (T) -> Unit
    ) {
        logger.measure(tag, numItems = count()) {
            forEach {
                try {
                    block(it)
                } catch (e: Exception) {
                    logger.logError(e, "$tag failed")
                }
            }
        }
    }

    fun <T : Any> Iterable<T>.filter(
        tag: String,
        parallel: Boolean = true,
        block: (T) -> Boolean
    ): List<T> {
        return logger.measure(tag, numItems = count()) {
            filter {
                try {
                    block(it)
                } catch (e: Exception) {
                    logger.logError(e, "$tag failed")
                    false
                }
            }
        }
    }

    fun <K, V> Map<K, V>.forEach(
        tag: String,
        parallel: Boolean = true,
        block: (K, V) -> Any?
    ) {
        logger.measure(tag, numItems = size) {
            forEach {
                try {
                    block(it.key, it.value)
                } catch (e: Exception) {
                    logger.logError(e, "$tag failed")
                }
            }
        }
    }

    fun <K, V, R : Any> Map<K, V>.mapNotNull(
        tag: String,
        parallel: Boolean = true,
        transform: (K, V) -> R?
    ): List<R> {
        return logger.measure(tag, numItems = count()) {
            this@mapNotNull.mapNotNull {
                try {
                    transform(it.key, it.value)
                } catch (e: Exception) {
                    logger.logError(e, "$tag failed")
                    null
                }
            }
        }
    }
}
