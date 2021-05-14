package com.airbnb.epoxy;

import android.view.View;
import android.view.ViewGroup;

import com.airbnb.epoxy.EpoxyModelClass;
import com.airbnb.epoxy.EpoxyModelWithView;

import androidx.annotation.NonNull;

@EpoxyModelClass
public abstract class AbstractEpoxyModelWithView extends EpoxyModelWithView<View> {
  @Override
  public View buildView(@NonNull ViewGroup parent) {
    return new View(parent.getContext());
  }
}
