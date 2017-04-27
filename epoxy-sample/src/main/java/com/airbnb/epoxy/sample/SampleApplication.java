package com.airbnb.epoxy.sample;

import android.app.Application;

import com.facebook.soloader.SoLoader;

public class SampleApplication extends Application {
  @Override
  public void onCreate() {
    super.onCreate();
    // This is for the optional Litho integration
    SoLoader.init(this, false);
  }
}
