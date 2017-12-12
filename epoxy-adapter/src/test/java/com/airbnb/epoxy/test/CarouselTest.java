package com.airbnb.epoxy.test;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.SnapHelper;

import com.airbnb.epoxy.Carousel;
import com.airbnb.epoxy.Carousel.SnapHelperFactory;
import com.airbnb.epoxy.TestRunner;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

@Config(sdk = 21, manifest = TestRunner.MANIFEST_PATH)
@RunWith(TestRunner.class)
public class CarouselTest {

  @Test
  public void testOverrideGlobalSnapHelper() {
    Carousel.setDefaultGlobalSnapHelperFactory(new SnapHelperFactory() {
      @NonNull
      @Override
      public SnapHelper buildSnapHelper(Context context) {
        return new LinearSnapHelper();
      }
    });
  }

  @Test
  public void testODisableGlobalSnapHelper() {
    Carousel.setDefaultGlobalSnapHelperFactory(null);
  }
}