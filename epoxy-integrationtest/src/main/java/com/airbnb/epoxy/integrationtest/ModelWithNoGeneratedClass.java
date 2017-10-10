package com.airbnb.epoxy.integrationtest;

import android.view.View;

import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModel;

// This class isn't used, but tests that a model is not generated for this and the processor and
// generated code handles this and compiles correctly. A kotlin extension function should not be
// generated
public abstract class ModelWithNoGeneratedClass extends EpoxyModel<View> {
  @EpoxyAttribute int value;
}
