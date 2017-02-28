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

  public AdapterWithAutoModel_EpoxyHelper(AdapterWithAutoModel adapter) {
    this.adapter = adapter;
  }

  @Override
  public void validateFieldsAreNull() {
    validateFieldIsNull(adapter.modelWithAttribute1, "modelWithAttribute1");
    validateFieldIsNull(adapter.modelWithAttribute2, "modelWithAttribute2");
  }

  private void validateFieldIsNull(Object fieldValue, String fieldName) {
    if (fieldValue != null) {
      throw new IllegalStateException("Fields annotated with AutoModel cannot be directly assigned. The adapter manages these fields for you. (" + adapter.getClass().getSimpleName() + "#" + fieldName + ")");
    }
  }

  @Override
  public void resetAutoModels() {
    adapter.modelWithAttribute1 = new BasicModelWithAttribute_();
    adapter.modelWithAttribute1.id(-1);
    adapter.modelWithAttribute2 = new BasicModelWithAttribute_();
    adapter.modelWithAttribute2.id(-2);
  }
}
