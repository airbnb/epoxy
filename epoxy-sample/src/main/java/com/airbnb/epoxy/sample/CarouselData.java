package com.airbnb.epoxy.sample;

import java.util.List;

public class CarouselData {
  private final long id;
  private final List<ColorData> colors;

  public CarouselData(long id, List<ColorData> colors) {
    this.id = id;
    this.colors = colors;
  }

  public List<ColorData> getColors() {
    return colors;
  }

  public long getId() {
    return id;
  }
}
