package com.airbnb.epoxy

import android.content.Context
import android.content.ContextWrapper
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import androidx.recyclerview.widget.RecyclerView
import java.lang.ref.WeakReference
import java.util.ArrayList

internal class LifecycleOwnerRecyclerPool {

    /**
     * Store one unique pool per lifecycle owner. They are cleared out when lifecycles are destroyed,
     * so this only needs to hold pools for active lifecycles.
     */
    private val pools = ArrayList<PoolReference>(5)

    @JvmOverloads
    fun getPool(
        context: Context,
        poolFactory: () -> RecyclerView.RecycledViewPool
    ): PoolReference {

        val iterator = pools.iterator()
        var poolToUse: PoolReference? = null

        while (iterator.hasNext()) {
            val poolReference = iterator.next()
            when {
                poolReference.context === context -> {
                    if (poolToUse != null) {
                        throw IllegalStateException("A pool was already found")
                    }
                    poolToUse = poolReference
                    // finish iterating to remove any old contexts
                }
                poolReference.context.isLifecycleDestroyed() -> {
                    // A pool from a different lifecycle owner that was destroyed.
                    // Clear the pool references to allow the lifecycle owner to be GC'd
                    poolReference.viewPool.clear()
                    iterator.remove()
                }
            }
        }

        if (poolToUse == null) {
            poolToUse = PoolReference(context, poolFactory(), this)
            context.lifecycle()?.addObserver(poolToUse)
            pools.add(poolToUse)
        }

        return poolToUse
    }

    fun clearIfDestroyed(pool: PoolReference) {
        if (pool.context.isLifecycleDestroyed()) {
            pool.viewPool.clear()
            pools.remove(pool)
        }
    }

    private fun Context.lifecycle(): Lifecycle? {
        if (this is LifecycleOwner) {
            return lifecycle
        }

        if (this is ContextWrapper) {
            return baseContext.lifecycle()
        }

        return null
    }
}

internal class PoolReference(
    context: Context,
    val viewPool: RecyclerView.RecycledViewPool,
    private val parent: LifecycleOwnerRecyclerPool
) : LifecycleObserver {
    private val contextReference: WeakReference<Context> = WeakReference(context)

    val context: Context? get() = contextReference.get()

    fun clearIfDestroyed() {
        parent.clearIfDestroyed(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onLifecycleDestroyed() {
        clearIfDestroyed()
    }
}

internal fun Context?.isLifecycleDestroyed(): Boolean {
    if (this == null) {
        return true
    }

    if (this !is LifecycleOwner) {
        return (this as? ContextWrapper)?.baseContext?.isLifecycleDestroyed() ?: false
    }

    if (lifecycle.currentState >= Lifecycle.State.DESTROYED) {
        return true
    }

    return false
}
