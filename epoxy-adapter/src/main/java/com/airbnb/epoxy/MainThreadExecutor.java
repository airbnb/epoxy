package com.airbnb.epoxy;

import static com.airbnb.epoxy.EpoxyAsyncUtil.AYSNC_MAIN_THREAD_HANDLER;
import static com.airbnb.epoxy.EpoxyAsyncUtil.MAIN_THREAD_HANDLER;

class MainThreadExecutor extends HandlerExecutor {
  static final MainThreadExecutor INSTANCE = new MainThreadExecutor(false);
  static final MainThreadExecutor ASYNC_INSTANCE = new MainThreadExecutor(true);

  MainThreadExecutor(boolean async) {
    super(async ? AYSNC_MAIN_THREAD_HANDLER : MAIN_THREAD_HANDLER);
  }
}


