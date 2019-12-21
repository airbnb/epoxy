package com.airbnb.epoxy

import java.util.AbstractList
import java.util.ArrayList
import java.util.ConcurrentModificationException
import java.util.NoSuchElementException

/**
 * Used by our [EpoxyAdapter] to track models. It simply wraps ArrayList and notifies an
 * observer when remove or insertion operations are done on the list. This allows us to optimize
 * diffing since we have a knowledge of what changed in the list.
 */
internal open class ModelList : ArrayList<EpoxyModel<*>> {
    interface ModelListObserver {
        fun onItemRangeInserted(positionStart: Int, itemCount: Int)
        fun onItemRangeRemoved(positionStart: Int, itemCount: Int)
    }

    /**
     * An Iterator implementation that calls through to the parent list's methods for modification.
     * Some implementations, like the Android ArrayList.ArrayListIterator class, modify the list data
     * directly instead of calling into the parent list's methods. We need the implementation to call
     * the parent methods so that the proper notifications are done.
     */
    private open inner class Itr : MutableIterator<EpoxyModel<*>> {
        var cursor = 0 // index of next element to return
        var lastRet = -1 // index of last element returned; -1 if no such
        var expectedModCount = modCount

        override fun hasNext(): Boolean = cursor != size

        override fun next(): EpoxyModel<*> {
            checkForComodification()
            val i = cursor
            cursor = i + 1
            lastRet = i
            return this@ModelList[i]
        }

        override fun remove() {
            if (lastRet < 0) {
                throw IllegalStateException()
            }
            checkForComodification()

            try {
                removeAt(lastRet)
                cursor = lastRet
                lastRet = -1
                expectedModCount = modCount
            } catch (ex: IndexOutOfBoundsException) {
                throw ConcurrentModificationException()
            }
        }

        fun checkForComodification() {
            if (modCount != expectedModCount) {
                throw ConcurrentModificationException()
            }
        }
    }

    /**
     * A ListIterator implementation that calls through to the parent list's methods for modification.
     * Some implementations may modify the list data directly instead of calling into the parent
     * list's methods. We need the implementation to call the parent methods so that the proper
     * notifications are done.
     */
    private inner class ListItr(index: Int) : Itr(), MutableListIterator<EpoxyModel<*>> {
        init {
            cursor = index
        }

        override fun hasPrevious(): Boolean = cursor != 0

        override fun nextIndex(): Int = cursor

        override fun previousIndex(): Int = cursor - 1

        override fun previous(): EpoxyModel<*> {
            checkForComodification()
            val i = cursor - 1
            if (i < 0) {
                throw NoSuchElementException()
            }

            cursor = i
            lastRet = i
            return this@ModelList[i]
        }

        override fun set(element: EpoxyModel<*>) {
            if (lastRet < 0) {
                throw IllegalStateException()
            }
            checkForComodification()

            try {
                this@ModelList[lastRet] = element
            } catch (ex: IndexOutOfBoundsException) {
                throw ConcurrentModificationException()
            }
        }

        override fun add(element: EpoxyModel<*>) {
            checkForComodification()

            try {
                val i: Int = cursor

                this@ModelList.add(i, element)
                cursor = i + 1
                lastRet = -1
                expectedModCount = modCount
            } catch (ex: IndexOutOfBoundsException) {
                throw ConcurrentModificationException()
            }
        }
    }

    /**
     * A SubList implementation from Android's AbstractList class. It's copied here to make sure the
     * implementation doesn't change, since some implementations, like the Java 1.8 ArrayList.SubList
     * class, modify the list data directly instead of calling into the parent list's methods. We need
     * the implementation to call the parent methods so that the proper notifications are done.
     */
    private class SubList(private val fullList: ModelList, start: Int, end: Int) : AbstractList<EpoxyModel<*>>() {
        private class SubListIterator(
            private val iterator: MutableListIterator<EpoxyModel<*>>,
            private val subList: SubList,
            private val start: Int,
            length: Int
        ) : MutableListIterator<EpoxyModel<*>> {
            private var end: Int = start + length

            override fun add(element: EpoxyModel<*>) {
                iterator.add(element)
                subList.sizeChanged(true)
                end++
            }

            override fun set(element: EpoxyModel<*>) {
                iterator.set(element)
            }

            override fun hasNext(): Boolean = iterator.nextIndex() < end

            override fun hasPrevious(): Boolean = iterator.previousIndex() >= start

            override fun next(): EpoxyModel<*> {
                if (iterator.nextIndex() < end) {
                    return iterator.next()
                }
                throw NoSuchElementException()
            }

            override fun nextIndex(): Int = iterator.nextIndex() - start

            override fun previous(): EpoxyModel<*> {
                if (iterator.previousIndex() >= start) {
                    return iterator.previous()
                }
                throw NoSuchElementException()
            }

            override fun previousIndex(): Int {
                val previous = iterator.previousIndex()
                return if (previous >= start) {
                    previous - start
                } else {
                    -1
                }
            }

            override fun remove() {
                iterator.remove()
                subList.sizeChanged(false)
                end--
            }
        }

        private val offset: Int = start
        private var _size: Int = end - start
        override val size: Int
            get() {
                if (modCount == fullList.modCount) {
                    return _size
                }
                throw ConcurrentModificationException()
            }

        init {
            modCount = fullList.modCount
        }

        override fun add(index: Int, element: EpoxyModel<*>) {
            if (modCount == fullList.modCount) {
                if (index in 0..size) {
                    fullList.add(index + offset, element)
                    _size++
                    modCount = fullList.modCount
                } else {
                    throw IndexOutOfBoundsException()
                }
            } else {
                throw ConcurrentModificationException()
            }
        }

        override fun addAll(index: Int, elements: Collection<EpoxyModel<*>>): Boolean {
            if (modCount == fullList.modCount) {
                if (index in 0..size) {
                    val result = fullList.addAll(index + offset, elements)
                    if (result) {
                        _size += elements.size
                        modCount = fullList.modCount
                    }
                    return result
                }
                throw IndexOutOfBoundsException()
            }
            throw ConcurrentModificationException()
        }

        override fun addAll(elements: Collection<EpoxyModel<*>>): Boolean {
            if (modCount == fullList.modCount) {
                val result = fullList.addAll(offset + size, elements)
                if (result) {
                    _size += elements.size
                    modCount = fullList.modCount
                }
                return result
            }
            throw ConcurrentModificationException()
        }

        override fun get(index: Int): EpoxyModel<*> {
            if (modCount == fullList.modCount) {
                if (index in 0..lastIndex) {
                    return fullList[index + offset]
                }
                throw IndexOutOfBoundsException()
            }
            throw ConcurrentModificationException()
        }

        override fun set(index: Int, element: EpoxyModel<*>): EpoxyModel<*> {
            if (modCount == fullList.modCount) {
                if (index in 0..lastIndex) {
                    return fullList.set(index + offset, element)
                }
                throw IndexOutOfBoundsException()
            }
            throw ConcurrentModificationException()
        }

        override fun iterator(): MutableIterator<EpoxyModel<*>> = listIterator(0)

        override fun listIterator(index: Int): MutableListIterator<EpoxyModel<*>> {
            if (modCount == fullList.modCount) {
                if (index in 0..size) {
                    return SubListIterator(fullList.listIterator(index + offset), this, offset, size)
                }
                throw IndexOutOfBoundsException()
            }
            throw ConcurrentModificationException()
        }

        override fun removeAt(location: Int): EpoxyModel<*> {
            if (modCount == fullList.modCount) {
                if (location in 0..lastIndex) {
                    val result = fullList.removeAt(location + offset)
                    _size--
                    modCount = fullList.modCount
                    return result
                }
                throw IndexOutOfBoundsException()
            }
            throw ConcurrentModificationException()
        }

        override fun removeRange(start: Int, end: Int) {
            if (start != end) {
                if (modCount == fullList.modCount) {
                    fullList.removeRange(start + offset, end + offset)
                    _size -= end - start
                    modCount = fullList.modCount
                } else {
                    throw ConcurrentModificationException()
                }
            }
        }

        fun sizeChanged(increment: Boolean) {
            if (increment) {
                _size++
            } else {
                _size--
            }
            modCount = fullList.modCount
        }
    }

    constructor()
    constructor(expectedModelCount: Int) : super(expectedModelCount)

    private var notificationsPaused = false
    private var observer: ModelListObserver? = null

    fun pauseNotifications() {
        if (notificationsPaused) {
            throw IllegalStateException("Notifications already paused")
        }
        notificationsPaused = true
    }

    fun resumeNotifications() {
        if (!notificationsPaused) {
            throw IllegalStateException("Notifications already resumed")
        }
        notificationsPaused = false
    }

    fun setObserver(observer: ModelListObserver) {
        this.observer = observer
    }

    private fun notifyInsertion(positionStart: Int, itemCount: Int) {
        if (!notificationsPaused) {
            observer?.onItemRangeInserted(positionStart, itemCount)
        }
    }

    private fun notifyRemoval(positionStart: Int, itemCount: Int) {
        if (!notificationsPaused) {
            observer?.onItemRangeRemoved(positionStart, itemCount)
        }
    }

    override fun set(index: Int, element: EpoxyModel<*>): EpoxyModel<*> {
        val previousModel = super.set(index, element)
        if (previousModel.id() != element.id()) {
            notifyRemoval(index, 1)
            notifyInsertion(index, 1)
        }

        return previousModel
    }

    override fun add(element: EpoxyModel<*>): Boolean {
        notifyInsertion(size, 1)
        return super.add(element)
    }

    override fun add(index: Int, element: EpoxyModel<*>) {
        notifyInsertion(index, 1)
        super.add(index, element)
    }

    override fun addAll(elements: Collection<EpoxyModel<*>>): Boolean {
        notifyInsertion(size, elements.size)
        return super.addAll(elements)
    }

    override fun addAll(index: Int, elements: Collection<EpoxyModel<*>>): Boolean {
        notifyInsertion(index, elements.size)
        return super.addAll(index, elements)
    }

    override fun clear() {
        if (isNotEmpty()) {
            notifyRemoval(0, size)
            super.clear()
        }
    }

    override fun removeAt(index: Int): EpoxyModel<*> {
        notifyRemoval(index, 1)
        return super.removeAt(index)
    }

    override fun remove(element: EpoxyModel<*>): Boolean {
        val index = indexOf(element)
        if (index == -1) {
            return false
        }
        notifyRemoval(index, 1)
        super.removeAt(index)
        return true
    }

    public override fun removeRange(fromIndex: Int, toIndex: Int) {
        if (fromIndex == toIndex) {
            return
        }

        notifyRemoval(fromIndex, toIndex - fromIndex)
        super.removeRange(fromIndex, toIndex)
    }

    override fun removeAll(elements: Collection<EpoxyModel<*>>): Boolean {
        // Using this implementation from the Android ArrayList since the Java 1.8 ArrayList
        // doesn't call through to remove. Calling through to remove lets us leverage the notification
        // done there
        var result = false
        val it: MutableIterator<*> = iterator()
        while (it.hasNext()) {
            if (elements.contains(it.next())) {
                it.remove()
                result = true
            }
        }
        return result
    }

    override fun retainAll(elements: Collection<EpoxyModel<*>>): Boolean {
        // Using this implementation from the Android ArrayList since the Java 1.8 ArrayList
        // doesn't call through to remove. Calling through to remove lets us leverage the notification
        // done there
        var result = false
        val it: MutableIterator<*> = iterator()
        while (it.hasNext()) {
            if (!elements.contains(it.next())) {
                it.remove()
                result = true
            }
        }
        return result
    }

    override fun iterator(): MutableIterator<EpoxyModel<*>> = Itr()

    override fun listIterator(): MutableListIterator<EpoxyModel<*>> = ListItr(0)

    override fun listIterator(index: Int): MutableListIterator<EpoxyModel<*>> = ListItr(index)

    override fun subList(fromIndex: Int, toIndex: Int): MutableList<EpoxyModel<*>> {
        if (fromIndex >= 0 && toIndex <= size) {
            if (fromIndex <= toIndex) {
                return SubList(this, fromIndex, toIndex)
            }
            throw IllegalArgumentException()
        }
        throw IndexOutOfBoundsException()
    }
}
