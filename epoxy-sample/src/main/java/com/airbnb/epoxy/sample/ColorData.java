package com.airbnb.epoxy.sample;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.ColorInt;

public class ColorData implements Parcelable {
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

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeInt(this.colorInt);
    dest.writeLong(this.id);
  }

  protected ColorData(Parcel in) {
    this.colorInt = in.readInt();
    this.id = in.readLong();
  }

  public static final Creator<ColorData> CREATOR = new Creator<ColorData>() {
    @Override
    public ColorData createFromParcel(Parcel source) {
      return new ColorData(source);
    }

    @Override
    public ColorData[] newArray(int size) {
      return new ColorData[size];
    }
  };
}
