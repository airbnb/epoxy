package com.airbnb.epoxy;

import android.view.View;

import com.airbnb.epoxy.EpoxyModel.SpanSizeOverrideCallback;
import com.airbnb.epoxy.integrationtest.BuildConfig;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
public class EpoxyModelIntegrationTest {

  static class ModelWithSpanCount extends EpoxyModel<View> {
    @Override
    protected int getDefaultLayout() {
      return 0;
    }

    @Override
    public int getSpanSize(int totalSpanCount, int position, int itemCount) {
      return 6;
    }
  }

  @Test
  public void modelReturnsSpanCount() {
    ModelWithSpanCount model = new ModelWithSpanCount();
    assertEquals(6, model.spanSize(0, 0, 0));
  }

  static class ModelWithSpanCountCallback extends EpoxyModel<View> {
    @Override
    protected int getDefaultLayout() {
      return 0;
    }
  }

  @Test
  public void modelReturnsSpanCountFromCallback() {
    ModelWithSpanCountCallback model = new ModelWithSpanCountCallback();
    model.spanSizeOverride(new SpanSizeOverrideCallback() {
      @Override
      public int getSpanSize(int totalSpanCount, int position, int itemCount) {
        return 7;
      }
    });

    assertEquals(7, model.spanSize(0, 0, 0));
  }
}
