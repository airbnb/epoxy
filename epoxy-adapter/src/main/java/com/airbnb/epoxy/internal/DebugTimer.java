package com.airbnb.epoxy.internal;

import android.support.annotation.RestrictTo;
import android.support.annotation.RestrictTo.Scope;
import android.util.Log;

@RestrictTo(Scope.LIBRARY_GROUP)
public class DebugTimer implements Timer {

  private final String tag;
  private long startTime;

  public DebugTimer(String tag) {
    this.tag = tag;
    reset();
  }

  private void reset() {
    startTime = -1;
  }

  @Override
  public void start() {
    if (startTime != -1) {
      throw new IllegalStateException("Timer was already started");
    }

    startTime = System.nanoTime();
  }

  @Override
  public void stop(String message) {
    if (startTime == -1) {
      throw new IllegalStateException("Timer was not started");
    }

    float durationMs = (System.nanoTime() - startTime) / 1000000f;
    Log.d(tag, String.format(message + ": %.3fms", durationMs));
    reset();
  }
}
