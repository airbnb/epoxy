package com.airbnb.epoxy.sample;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class CarouselData implements Parcelable {
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

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeLong(this.id);
    dest.writeTypedList(this.colors);
  }

  protected CarouselData(Parcel in) {
    this.id = in.readLong();
    this.colors = in.createTypedArrayList(ColorData.CREATOR);
  }

  public static final Creator<CarouselData> CREATOR = new Creator<CarouselData>() {
    @Override
    public CarouselData createFromParcel(Parcel source) {
      return new CarouselData(source);
    }

    @Override
    public CarouselData[] newArray(int size) {
      return new CarouselData[size];
    }
  };
}
