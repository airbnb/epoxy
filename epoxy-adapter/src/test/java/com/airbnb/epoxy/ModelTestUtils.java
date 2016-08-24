package com.airbnb.epoxy;

import java.util.ArrayList;
import java.util.List;

class ModelTestUtils {
  private static final int DEFAULT_NUM_MODELS = 20;

  static void changeValues(List<TestModel> models) {
    changeValues(models, 0, models.size());
  }

  static void changeValues(List<TestModel> models, int start, int count) {
    for (int i = start; i < count; i++) {
      models.set(i, models.get(i).clone().randomizeValue());
    }
  }

  static void remove(List<TestModel> models, int start, int count) {
    models.subList(start, start + count).clear();
  }

  static void removeAfter(List<TestModel> models, int start) {
    remove(models, start, models.size() - start);
  }

  static void addModels(List<TestModel> list) {
    addModels(DEFAULT_NUM_MODELS, list);
  }

  static void addModels(int count, List<TestModel> list) {
    addModels(count, list, list.size());
  }

  static void addModels(List<TestModel> list, int index) {
    addModels(DEFAULT_NUM_MODELS, list, index);
  }

  static void addModels(int count, List<TestModel> list, int index) {
    List<TestModel> modelsToAdd = new ArrayList<>(count);
    for (int i = 0; i < count; i++) {
      modelsToAdd.add(new TestModel());
    }

    list.addAll(index, modelsToAdd);
  }

  static List<EpoxyModel<?>> convertList(List<TestModel> list) {
    List<EpoxyModel<?>> result = new ArrayList<>(list.size());
    for (TestModel testModel : list) {
      result.add(testModel);
    }
    return result;
  }
}
