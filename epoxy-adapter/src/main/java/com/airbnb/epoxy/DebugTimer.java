package com.airbnb.epoxy;

import android.util.Log;

class DebugTimer implements Timer {

  private final String tag;
  private long startTime;

  DebugTimer(String tag) {
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
