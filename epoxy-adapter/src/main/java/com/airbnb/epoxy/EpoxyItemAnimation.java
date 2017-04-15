package com.airbnb.epoxy;

import android.animation.Animator;

/**
 * Created by Thilo on 4/13/2017.
 */

public abstract class EpoxyItemAnimation {
  EpoxyViewHolder holder;

  static EpoxyItemAnimation fromAnimator(final Animator anim) {
    return new EpoxyItemAnimation() {
      @Override
      public long getDuration() {
        return anim.getDuration();
      }

      @Override
      public long getStartDelay() {
        return anim.getStartDelay();
      }

      @Override
      public void setStartDelay(long delay) {
        anim.setStartDelay(delay);
      }

      @Override
      public boolean isRunning() {
        return anim.isRunning();
      }

      @Override
      public void start() {
        anim.start();
      }

      @Override
      public void cancel() {
        anim.cancel();
      }

      @Override
      public void end() {
        anim.end();
      }
    };
  }

  abstract long getDuration();

  abstract long getStartDelay();

  abstract void setStartDelay(long delay);

  abstract boolean isRunning();

  abstract void start();

  abstract void cancel();

  abstract void end();

  abstract void addListener(Listener listener);

  abstract void removeListener(Listener listener);

  interface Listener {
    void onAnimationStart(EpoxyItemAnimation animation);
    void onAnimationEnd(EpoxyItemAnimation animation);
  }
}
