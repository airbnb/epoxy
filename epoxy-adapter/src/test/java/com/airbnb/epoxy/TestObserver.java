package com.airbnb.epoxy;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

class TestObserver extends RecyclerView.AdapterDataObserver
{
  List<TestModel> modelsAfterDiffing = new ArrayList<>();
  List<TestModel> initialModels = new ArrayList<>();
  int operationCount = 0;
  private boolean showLogs;

  TestObserver(boolean showLogs) {
    this.showLogs = showLogs;
  }

  TestObserver() {
    this(false);
  }

  void setUpForNextDiff(List<TestModel> models) {
    initialModels = new ArrayList<>(models);
    modelsAfterDiffing = new ArrayList<>(models);
  }

  @Override
  public void onItemRangeChanged(int positionStart, int itemCount) {
    if (showLogs) {
      System.out.println("Item range changed. Start: " + positionStart + " Count: " + itemCount);
    }
    for (int i = positionStart; i < positionStart + itemCount; i++) {
      modelsAfterDiffing.get(i).updated = true;
    }
    operationCount++;
  }

  @Override
  public void onItemRangeInserted(int positionStart, int itemCount) {
    if (showLogs) {
      System.out.println("Item range inserted. Start: " + positionStart + " Count: " + itemCount);
    }
    List<TestModel> modelsToAdd = new ArrayList<>(itemCount);
    for (int i = 0; i < itemCount; i++) {
      modelsToAdd.add(InsertedModel.INSTANCE);
    }

    modelsAfterDiffing.addAll(positionStart, modelsToAdd);
    operationCount++;
  }

  @Override
  public void onItemRangeRemoved(int positionStart, int itemCount) {
    if (showLogs) {
      System.out.println("Item range removed. Start: " + positionStart + " Count: " + itemCount);
    }
    modelsAfterDiffing.subList(positionStart, positionStart + itemCount).clear();
    operationCount++;
  }

  @Override
  public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
    if (showLogs) {
      System.out.println("Item moved. From: " + fromPosition + " To: " + toPosition);
    }
    TestModel itemToMove = modelsAfterDiffing.remove(fromPosition);
    modelsAfterDiffing.add(toPosition, itemToMove);
    operationCount++;
  }
}
