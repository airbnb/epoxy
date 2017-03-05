package com.airbnb.epoxy.adapter;

import com.airbnb.epoxy.AdapterHelper;
import com.airbnb.epoxy.BasicModelWithAttribute_;

import java.lang.IllegalStateException;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;

/**
 * Generated file. Do not modify!
 */
public class AdapterWithAutoModel_EpoxyHelper extends AdapterHelper<AdapterWithAutoModel> {
  private final AdapterWithAutoModel adapter;
  private Object modelWithAttribute1;
  private Object modelWithAttribute2;

  public AdapterWithAutoModel_EpoxyHelper(AdapterWithAutoModel adapter) {
    this.adapter = adapter;
  }

  @Override
  public void resetAutoModels() {
    validateModelsHaveNotChanged();

    adapter.modelWithAttribute1 = new BasicModelWithAttribute_();
    adapter.modelWithAttribute1.id(-1);
    adapter.modelWithAttribute2 = new BasicModelWithAttribute_();
    adapter.modelWithAttribute2.id(-2);

    saveModelsForNextValidation();
  }

  private void validateModelsHaveNotChanged() {
    validateSameModel(modelWithAttribute1, adapter.modelWithAttribute1, "modelWithAttribute1")
    validateSameModel(modelWithAttribute2, adapter.modelWithAttribute2, "modelWithAttribute2")
  }

  private void validateSameModel(Object expectedObject, Object actualObject, String fieldName) {
    if (expectedObject != actualObject) {
      throw new IllegalStateException("Fields annotated with AutoModel cannot be directly assigned. The adapter manages these fields for you. (" + adapter.getClass().getSimpleName() + "#" + fieldName + ")");
    }
  }

  private void saveModelsForNextValidation() {
    modelWithAttribute1 = adapter.modelWithAttribute1;
    modelWithAttribute2 = adapter.modelWithAttribute2;
  }
}
