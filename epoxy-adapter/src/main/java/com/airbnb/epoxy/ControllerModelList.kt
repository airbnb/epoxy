package com.airbnb.epoxy

/**
 * This ArrayList subclass enforces that no changes are made to the list after [.freeze] is
 * called. This prevents model interceptors from storing the list and trying to change it later. We
 * could copy the list before diffing, but that would waste memory to make the copy for every
 * buildModels cycle, plus the interceptors could still try to modify the list and be confused about
 * why it doesn't do anything.
 */
internal class ControllerModelList(expectedModelCount: Int) : ModelList(expectedModelCount) {
    companion object {
        private val OBSERVER: ModelListObserver = object : ModelListObserver {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                throw IllegalStateException("Models cannot be changed once they are added to the controller")
            }

            override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                throw IllegalStateException("Models cannot be changed once they are added to the controller")
            }
        }
    }

    init {
        pauseNotifications()
    }

    fun freeze() {
        setObserver(OBSERVER)
        resumeNotifications()
    }
}
