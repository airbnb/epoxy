package com.airbnb.epoxy;

import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.MainThread;
import android.support.annotation.Nullable;

import java.lang.reflect.Constructor;

/**
 * Various helpers for running Epoxy operations off the main thread.
 */
public final class EpoxyAsyncUtil {
  private EpoxyAsyncUtil() {
  }

  /**
   * A Handler class that uses the main thread's Looper.
   */
  public static final Handler MAIN_THREAD_HANDLER =
      createHandler(Looper.getMainLooper(), false);

  /**
   * A Handler class that uses the main thread's Looper. Additionally, this handler calls
   * {@link Message#setAsynchronous(boolean)} for
   * each {@link Message} that is sent to it or {@link Runnable} that is posted to it
   */
  public static final Handler AYSNC_MAIN_THREAD_HANDLER =
      createHandler(Looper.getMainLooper(), true);

  private static Handler asyncBackgroundHandler;

  /**
   * A Handler class that uses a separate background thread dedicated to Epoxy. Additionally,
   * this handler calls {@link Message#setAsynchronous(boolean)} for
   * each {@link Message} that is sent to it or {@link Runnable} that is posted to it
   */
  @MainThread
  public static Handler getAsyncBackgroundHandler() {
    // This is initialized lazily so we don't create the thread unless it will be used.
    // It isn't synchronized so it should only be accessed on the main thread.
    if (asyncBackgroundHandler == null) {
      asyncBackgroundHandler = createHandler(buildBackgroundLooper("epoxy"), true);
    }

    return asyncBackgroundHandler;
  }

  /**
   * Create a Handler with the given Looper
   *
   * @param async If true the Handler will calls {@link Message#setAsynchronous(boolean)} for
   *              each {@link Message} that is sent to it or {@link Runnable} that is posted to it.
   */
  public static Handler createHandler(Looper looper, boolean async) {
    if (!async) {
      return new Handler(looper);
    }

    if (Build.VERSION.SDK_INT >= 28) {
      return Handler.createAsync(looper);
    }

    if (Build.VERSION.SDK_INT >= 17) {
      Constructor<Handler> handlerConstructor = asyncHandlerConstructor();
      if (handlerConstructor != null) {
        try {
          return handlerConstructor.newInstance(looper, null, true);
        } catch (Throwable e) {
          // Fallback
        }
      }
    }

    return new Handler(looper);
  }

  /**
   * Create a new looper that runs on a new background thread.
   */
  public static Looper buildBackgroundLooper(String threadName) {
    HandlerThread handlerThread = new HandlerThread(threadName);
    handlerThread.start();
    return handlerThread.getLooper();
  }

  @Nullable
  private static Constructor<Handler> asyncHandlerConstructor() {
    try {
      //noinspection JavaReflectionMemberAccess
      return Handler.class.getConstructor(
          Looper.class,
          Handler.Callback.class,
          Boolean.class
      );
    } catch (Throwable e) {
      return null;
    }
  }
}
