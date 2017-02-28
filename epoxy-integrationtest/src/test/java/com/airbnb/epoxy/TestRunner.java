package com.airbnb.epoxy;

import org.junit.runners.model.InitializationError;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.manifest.AndroidManifest;
import org.robolectric.res.Fs;

public class TestRunner extends RobolectricTestRunner {
  public static final String MANIFEST_PATH = "../epoxy-integrationtest/src/main/AndroidManifest.xml";

  public TestRunner(Class<?> testClass) throws InitializationError {
    super(testClass);
  }

  @Override
  protected AndroidManifest getAppManifest(Config config) {
    String res = String.format("../epoxy-integrationtest/build/intermediates/res/merged/%1$s/%2$s",
        BuildConfig.FLAVOR, BuildConfig.BUILD_TYPE);
    String asset = "src/main/assets";
    return new AndroidManifest(Fs.fileFromPath(MANIFEST_PATH), Fs.fileFromPath(res),
        Fs.fileFromPath(asset));
  }
}