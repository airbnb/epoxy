package com.airbnb.epoxy;

import java.util.ArrayList;
import java.util.List;

class ModelTestUtils {
  private static final int DEFAULT_NUM_MODELS = 20;

  static void changeValues(List models) {
    changeValues(models, 0, models.size());
  }

  static void changeValues(List models, int start, int count) {
    for (int i = start; i < count; i++) {
      models.set(i, ((TestModel) models.get(i)).clone().randomizeValue());
    }
  }

  static void changeValue(EpoxyModel<?> model) {
    ((TestModel) model).randomizeValue();
  }

  static void remove(List models, int start, int count) {
    models.subList(start, start + count).clear();
  }

  static void removeModelsAfterPosition(List models, int start) {
    remove(models, start, models.size() - start);
  }

  static void addModels(List list) {
    addModels(DEFAULT_NUM_MODELS, list);
  }

  static void addModels(int count, List list) {
    addModels(count, list, list.size());
  }

  static void addModels(List list, int index) {
    addModels(DEFAULT_NUM_MODELS, list, index);
  }

  static void addModels(int count, List list, int index) {
    List<TestModel> modelsToAdd = new ArrayList<>(count);
    for (int i = 0; i < count; i++) {
      modelsToAdd.add(new TestModel());
    }

    list.addAll(index, modelsToAdd);
  }

  static List<EpoxyModel<?>> convertToGenericModels(List<TestModel> list) {
    List<EpoxyModel<?>> result = new ArrayList<>(list.size());
    for (TestModel testModel : list) {
      result.add(testModel);
    }
    return result;
  }

  static List<TestModel> convertToTestModels(List<EpoxyModel<?>> list) {
    List<TestModel> result = new ArrayList<>(list.size());
    for (EpoxyModel<?> model : list) {
      result.add((TestModel) model);
    }
    return result;
  }
}
