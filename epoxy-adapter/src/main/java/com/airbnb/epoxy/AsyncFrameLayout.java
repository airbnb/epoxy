package com.airbnb.epoxy;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;

/**
 * Base class to support Async layout inflation with EPoxy
 */
public abstract class AsyncFrameLayout extends FrameLayout {
  protected boolean isInflated = false;
  protected List<Runnable> pendingFunctions = new ArrayList<>();

  public AsyncFrameLayout(Context context) {
    super(context);
  }

  public AsyncFrameLayout(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
  }

  public AsyncFrameLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  /**
   * Call this method once the inflation is completed
   */
  protected void onInflationComplete() {
    isInflated = true;
    for (Runnable runnable: pendingFunctions) {
      runnable.run();
    }
    pendingFunctions.clear();
  }

  public void executeWhenInflated(Runnable runnable) {
    if (isInflated) {
      runnable.run();
    } else {
      pendingFunctions.add(runnable);
    }
  }
}
