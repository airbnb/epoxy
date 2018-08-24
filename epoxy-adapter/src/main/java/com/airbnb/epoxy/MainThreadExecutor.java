package com.airbnb.epoxy;

import android.os.Looper;

import static com.airbnb.epoxy.EpoxyAsyncUtil.createHandler;

class MainThreadExecutor extends HandlerExecutor {
  static final MainThreadExecutor INSTANCE = new MainThreadExecutor(false);
  static final MainThreadExecutor ASYNC_INSTANCE = new MainThreadExecutor(true);

  MainThreadExecutor(boolean async) {
    super(createHandler(Looper.getMainLooper(), async));
  }
}


