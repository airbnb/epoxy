package com.airbnb.epoxy;

import android.animation.Animator;

/**
 * Created by Thilo on 4/13/2017.
 */

interface EpoxyItemAnimation {
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
  long getDuration();
  long getStartDelay();
  void setStartDelay(long delay);
  boolean isRunning();
  void start();
  void cancel();
  void end();
  void addListener();
  void removeListener();

  interface Listener {
    void onAnimationStart(EpoxyItemAnimation animation);
    void onAnimationEnd(EpoxyItemAnimation animation);
  }
}
