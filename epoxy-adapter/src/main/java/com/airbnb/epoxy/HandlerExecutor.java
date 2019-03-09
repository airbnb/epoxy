package com.airbnb.epoxy;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;

import androidx.annotation.NonNull;

/**
 * An executor that does it's work via posting to a Handler.
 * <p>
 * A key feature of this is the runnable is executed synchronously if the current thread is the
 * same as the handler's thread.
 */
class HandlerExecutor implements Executor {
  final Handler handler;

  HandlerExecutor(Handler handler) {
    this.handler = handler;
  }

  @Override
  public void execute(@NonNull Runnable command) {
    // If we're already on the same thread then we can execute this synchronously
    if (Looper.myLooper() == handler.getLooper()) {
      command.run();
    } else {
      handler.post(command);
    }
  }
}
