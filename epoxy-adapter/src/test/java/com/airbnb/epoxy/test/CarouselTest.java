package com.airbnb.epoxy.test;

import android.content.Context;

import com.airbnb.epoxy.Carousel;
import com.airbnb.epoxy.Carousel.SnapHelperFactory;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.SnapHelper;

@RunWith(RobolectricTestRunner.class)
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
