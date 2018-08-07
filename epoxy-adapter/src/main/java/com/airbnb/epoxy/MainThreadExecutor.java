package com.airbnb.epoxy;

import android.os.Handler;
import android.os.Looper;

class MainThreadExecutor extends HandlerExecutor {
  static final MainThreadExecutor INSTANCE = new MainThreadExecutor();

  MainThreadExecutor() {
    super(new Handler(Looper.getMainLooper()));
  }
}
