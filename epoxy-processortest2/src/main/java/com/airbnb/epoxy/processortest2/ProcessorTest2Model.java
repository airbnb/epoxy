package com.airbnb.epoxy.processortest2;

import android.view.View;

import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModel;

public class ProcessorTest2Model<T extends View> extends EpoxyModel<T> {

  @EpoxyAttribute protected int processorTest2ValueProtected;
  @EpoxyAttribute public int processorTest2ValuePublic;
  @EpoxyAttribute int processorTest2ValuePackagePrivate;
  @EpoxyAttribute public int someAttributeAlsoWithSetter;
  @EpoxyAttribute public final int someFinalAttribute = 0;

  @Override
  protected int getDefaultLayout() {
    return 0;
  }

  public ProcessorTest2Model<T> someAttributeAlsoWithSetter(int foo) {
    someAttributeAlsoWithSetter = foo;
    return this;
  }
}
