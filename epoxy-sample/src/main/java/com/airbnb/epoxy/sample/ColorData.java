package com.airbnb.epoxy.sample;

import android.support.annotation.ColorInt;

public class ColorData {
  @ColorInt private int colorInt;
  private final long id;

  public ColorData(int colorInt, long id) {
    this.colorInt = colorInt;
    this.id = id;
  }

  @ColorInt
  public int getColorInt() {
    return colorInt;
  }

  public void setColorInt(@ColorInt int colorInt) {
    this.colorInt = colorInt;
  }

  public long getId() {
    return id;
  }
}
