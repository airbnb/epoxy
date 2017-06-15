package com.airbnb.epoxy;

import android.widget.FrameLayout;

public abstract class TestBaseModel<T extends FrameLayout> extends EpoxyModel<T> {
  @EpoxyAttribute String baseModelString;
}
