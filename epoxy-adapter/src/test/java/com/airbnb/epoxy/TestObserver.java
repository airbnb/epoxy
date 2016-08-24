package com.airbnb.epoxy;

import android.support.v7.widget.RecyclerView.AdapterDataObserver;

import java.util.ArrayList;
import java.util.List;

class TestObserver extends AdapterDataObserver {
  List<TestModel> diffedModels = new ArrayList<>();
  int operationCount = 0;
  private boolean showLogs;

  TestObserver(boolean showLogs) {
    this.showLogs = showLogs;
  }

  @Override
  public void onItemRangeChanged(int positionStart, int itemCount) {
    if (showLogs) {
      System.out.println("Item range changed. Start: " + positionStart + " Count: " + itemCount);
    }
    for (int i = positionStart; i < positionStart + itemCount; i++) {
      diffedModels.get(i).updated = true;
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

    diffedModels.addAll(positionStart, modelsToAdd);
    operationCount++;
  }

  @Override
  public void onItemRangeRemoved(int positionStart, int itemCount) {
    if (showLogs) {
      System.out.println("Item range removed. Start: " + positionStart + " Count: " + itemCount);
    }
    diffedModels.subList(positionStart, positionStart + itemCount).clear();
    operationCount++;
  }

  @Override
  public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
    if (showLogs) {
      System.out.println("Item moved. From: " + fromPosition + " To: " + toPosition);
    }
    TestModel itemToMove = diffedModels.remove(fromPosition);
    diffedModels.add(toPosition, itemToMove);
    operationCount++;
  }
}
