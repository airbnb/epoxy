package com.airbnb.epoxy.adapter;

import com.airbnb.epoxy.AdapterHelper;
import com.airbnb.epoxy.BasicModelWithAttribute_;
import java.lang.Override;

/**
 * Generated file. Do not modify!
 */
public class AdapterWithAutoModelWithoutValidation_EpoxyHelper extends AdapterHelper<AdapterWithAutoModelWithoutValidation> {
  private final AdapterWithAutoModelWithoutValidation adapter;

  public AdapterWithAutoModelWithoutValidation_EpoxyHelper(AdapterWithAutoModelWithoutValidation adapter) {
    this.adapter = adapter;
  }

  @Override
  public void resetAutoModels() {
    adapter.modelWithAttribute1 = new BasicModelWithAttribute_();
    adapter.modelWithAttribute1.id(-1);
    adapter.modelWithAttribute2 = new BasicModelWithAttribute_();
    adapter.modelWithAttribute2.id(-2);
  }
}
