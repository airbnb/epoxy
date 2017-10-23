package com.airbnb.epoxy.internal;

import android.support.annotation.RestrictTo;
import android.support.annotation.RestrictTo.Scope;

@RestrictTo(Scope.LIBRARY_GROUP)
public interface Timer {
  void start();
  void stop(String message);
}
