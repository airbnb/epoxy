package com.airbnb.epoxy;

/**
 * This ArrayList subclass enforces that no changes are made to the list after {@link #freeze()} is
 * called. This prevents model interceptors from storing the list and trying to change it later. We
 * could copy the list before diffing, but that would waste memory to make the copy for every
 * buildModels cycle, plus the interceptors could still try to modify the list and be confused about
 * why it doesn't do anything.
 */
class ControllerModelList extends ModelList {

  private static final ModelListObserver OBSERVER = new ModelListObserver() {
    @Override
    public void onItemRangeInserted(int positionStart, int itemCount) {
      throw new IllegalStateException(
          "Models cannot be changed once they are added to the controller");
    }

    @Override
    public void onItemRangeRemoved(int positionStart, int itemCount) {
      throw new IllegalStateException(
          "Models cannot be changed once they are added to the controller");
    }
  };

  ControllerModelList(int expectedModelCount) {
    super(expectedModelCount);
    pauseNotifications();
  }

  void freeze() {
    setObserver(OBSERVER);
    resumeNotifications();
  }
}
