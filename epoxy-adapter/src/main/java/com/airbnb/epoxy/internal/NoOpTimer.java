package com.airbnb.epoxy.internal;

import android.support.annotation.RestrictTo;
import android.support.annotation.RestrictTo.Scope;

@RestrictTo(Scope.LIBRARY_GROUP)
public class NoOpTimer implements Timer {
  @Override
  public void start() {

  }

  @Override
  public void stop(String message) {

  }
}
