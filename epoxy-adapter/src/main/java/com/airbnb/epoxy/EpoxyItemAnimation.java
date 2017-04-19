package com.airbnb.epoxy;

import android.animation.Animator;

/**
 * Created by Thilo on 4/13/2017.
 */

public abstract class EpoxyItemAnimation {
  EpoxyViewHolder holder;

  public static AnimatorWrapper fromAnimator(final Animator anim) {
    return new AnimatorWrapper(anim);
  }

  public abstract long getDuration();

  public abstract long getStartDelay();

  public abstract void setStartDelay(long delay);

  public abstract boolean isRunning();

  public abstract void start();

  public abstract void cancel();

  public abstract void end();

  public abstract void addListener(Listener listener);

  public abstract void removeListener(Listener listener);

  public interface Listener {
    void onAnimationStart(EpoxyItemAnimation animation);
    void onAnimationEnd(EpoxyItemAnimation animation);
  }

  public final static class AnimatorWrapper extends EpoxyItemAnimation {
    public final Animator wrappedAnimator;

    public AnimatorWrapper(Animator anim) {
      wrappedAnimator = anim;
    }

    @Override
    public long getDuration() {
      return wrappedAnimator.getDuration();
    }

    @Override
    public long getStartDelay() {
      return wrappedAnimator.getStartDelay();
    }

    @Override
    public void setStartDelay(long delay) {
      wrappedAnimator.setStartDelay(delay);
    }

    @Override
    public boolean isRunning() {
      return wrappedAnimator.isRunning();
    }

    @Override
    public void start() {
      wrappedAnimator.start();
    }

    @Override
    public void cancel() {
      wrappedAnimator.cancel();
    }

    @Override
    public void end() {
      wrappedAnimator.end();
    }

    @Override
    public void addListener(Listener listener) {
      wrappedAnimator.addListener();
    }

    @Override
    public void removeListener(Listener listener) {
      wrappedAnimator.removeListener();
    }
  }
}
