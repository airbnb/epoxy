package com.airbnb.epoxy.sample;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.ColorInt;

public class ColorData implements Parcelable {
  private final long id;
  @ColorInt private int colorInt;
  private boolean playAnimation;

  public ColorData(int colorInt, long id) {
    this.colorInt = colorInt;
    this.id = id;
  }

  public long getId() {
    return id;
  }

  @ColorInt
  public int getColorInt() {
    return colorInt;
  }

  public void setColorInt(@ColorInt int colorInt) {
    this.colorInt = colorInt;
  }

  public void setPlayAnimation(boolean playAnimation) {
    this.playAnimation = playAnimation;
  }

  public boolean shouldPlayAnimation() {
    return playAnimation;
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
