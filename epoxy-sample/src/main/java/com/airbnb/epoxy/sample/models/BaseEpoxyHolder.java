package com.airbnb.epoxy.sample.models;

import android.view.View;

import com.airbnb.epoxy.EpoxyHolder;

import androidx.annotation.CallSuper;
import butterknife.ButterKnife;

/**
 * Creating a base holder class allows us to leverage ButterKnife's view binding for all subclasses.
 * This makes subclasses much cleaner, and is a highly recommended pattern.
 */
public abstract class BaseEpoxyHolder extends EpoxyHolder {
  @CallSuper
  @Override
  protected void bindView(@NonNull View itemView) {
    ButterKnife.bind(this, itemView);
  }
}
