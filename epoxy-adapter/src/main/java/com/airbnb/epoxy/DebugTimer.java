package com.airbnb.epoxy;

import android.util.Log;

class DebugTimer implements Timer {

  private final String tag;
  private long startTime;
  private String sectionName;

  DebugTimer(String tag) {
    this.tag = tag;
    reset();
  }

  private void reset() {
    startTime = -1;
    sectionName = null;
  }

  @Override
  public void start(String sectionName) {
    if (startTime != -1) {
      throw new IllegalStateException("Timer was already started");
    }

    startTime = System.nanoTime();
    this.sectionName = sectionName;
  }

  @Override
  public void stop() {
    if (startTime == -1) {
      throw new IllegalStateException("Timer was not started");
    }

    float durationMs = (System.nanoTime() - startTime) / 1000000f;
    Log.d(tag, String.format(sectionName + ": %.3fms", durationMs));
    reset();
  }
}
