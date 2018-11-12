package com.airbnb.epoxy.models;

import androidx.annotation.IntDef;

import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModel;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.SOURCE;

public class ModelWithIntDef extends EpoxyModel<Object> {

  @Retention(SOURCE)
  @IntDef({TYPE_1})
  public @interface MyType {}

  public static final int TYPE_1 = 1;

  @EpoxyAttribute @MyType int type;

  @Override
  protected int getDefaultLayout() {
    return 0;
  }
}
