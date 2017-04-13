package com.airbnb.epoxy;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView.ItemAnimator;
import android.support.v7.widget.RecyclerView.State;
import android.support.v7.widget.RecyclerView.ViewHolder;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.List;

/**
 * base class for decorating ItemAnimators
 * currently only supports decorating change animations
 */
public class ItemAnimatorDecorator extends ItemAnimator {

  private final ItemAnimator mDecorated;

  // lists of holders that are currently being animated by the mDecorated ItemAnimator
  // they may be pending or running
  private HashSet<ViewHolder> mAppearAnims = new HashSet<>();
  private HashSet<ViewHolder> mChangeAnims = new HashSet<>();
  private HashSet<ViewHolder> mDisappearAnims = new HashSet<>();
  private HashSet<ViewHolder> mPersistAnims = new HashSet<>();

  public ItemAnimatorDecorator(@NonNull ItemAnimator decorated) {
    mDecorated = decorated;

    setAnimationFinishedListener(decorated, new AnimationFinishedListener() {
      @Override
      public void onAnimationFinished(ViewHolder holder) {
        dispatchAnimationFinished(holder);

        //remove holder from the lists of currently animating holders
        mAppearAnims.remove(holder);
        mChangeAnims.remove(holder);
        mDisappearAnims.remove(holder);
        mPersistAnims.remove(holder);
      }
    });
  }

  private static void setAnimationFinishedListener(ItemAnimator itemAnimator,
      final AnimationFinishedListener listener) {
    InvocationHandler animatorListenerInvocationHandler = new InvocationHandler() {
      @Override
      public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getName().equals("onAnimationFinished")) {
          if (args[0] instanceof ViewHolder) {
            listener.onAnimationFinished((ViewHolder) args[0]);
          } else {
            throw new RuntimeException(
                "unknown parameter in method invoked on itemAnimatorListener proxy");
          }
        } else {
          throw new RuntimeException("unknown method invoked on ItemAnimatorListener proxy");
        }

        return null;
      }
    };

    try {
      //get private listener interface
      Class animatorListenerInterface = Class.forName(
          "android.support.v7.widget.RecyclerView$ItemAnimator$ItemAnimatorListener");

      Class<?> animatorClass = Class.forName("android.support.v7.widget.RecyclerView$ItemAnimator");

      //create instance that implements the listener interface
      Object animatorListenerProxy = Proxy.newProxyInstance(
          animatorListenerInterface.getClassLoader(),
          new Class[]{animatorListenerInterface},
          animatorListenerInvocationHandler);

      Method setListenerMethod =
          animatorClass.getDeclaredMethod("setListener", animatorListenerInterface);

      //remove private modifier
      setListenerMethod.setAccessible(true);

      //call setListener method on this ItemAnimator
      setListenerMethod.invoke(itemAnimator, animatorListenerProxy);
    } catch (Exception e) {
      throw new RuntimeException(
          "failed to set AnimationFinishedListener on ItemAnimator: " + e.getMessage());
    }
  }

  @NonNull
  public ItemAnimator getDecoratedAnimator() {
    return mDecorated;
  }

  protected boolean appearAnimationsScheduled() {
    return !mAppearAnims.isEmpty();
  }

  protected boolean changeAnimationsScheduled() {
    return !mChangeAnims.isEmpty();
  }

  boolean disappearAnimationsScheduled() {
    return !mDisappearAnims.isEmpty();
  }

  protected boolean persistAnimationsScheduled() {
    return !mPersistAnims.isEmpty();
  }

  public final boolean animateAppearance(@NonNull ViewHolder viewHolder,
      @Nullable ItemHolderInfo preLayoutInfo, @NonNull ItemHolderInfo postLayoutInfo) {
    mAppearAnims.add(viewHolder);
    return mDecorated.animateAppearance(viewHolder, preLayoutInfo, postLayoutInfo);
  }

  public boolean animateChange(@NonNull ViewHolder oldHolder, @NonNull ViewHolder newHolder,
      @NonNull ItemHolderInfo preLayoutInfo, @NonNull ItemHolderInfo postLayoutInfo) {
    mAppearAnims.add(oldHolder);

    if (newHolder != oldHolder) {
      mAppearAnims.add(newHolder);
    }

    return mDecorated.animateChange(oldHolder, newHolder, preLayoutInfo, postLayoutInfo);
  }

  public final boolean animateDisappearance(@NonNull ViewHolder viewHolder,
      @NonNull ItemHolderInfo preLayoutInfo, @Nullable ItemHolderInfo postLayoutInfo) {
    mDisappearAnims.add(viewHolder);
    return mDecorated.animateDisappearance(viewHolder, preLayoutInfo, postLayoutInfo);
  }

  public final boolean animatePersistence(@NonNull ViewHolder viewHolder,
      @NonNull ItemHolderInfo preLayoutInfo, @NonNull ItemHolderInfo postLayoutInfo) {
    mDisappearAnims.add(viewHolder);
    return mDecorated.animatePersistence(viewHolder, preLayoutInfo, postLayoutInfo);
  }

  public boolean canReuseUpdatedViewHolder(@NonNull ViewHolder viewHolder) {
    return mDecorated.canReuseUpdatedViewHolder(viewHolder);
  }

  public boolean canReuseUpdatedViewHolder(@NonNull ViewHolder holder,
      @NonNull List<Object> payloads) {
    return mDecorated.canReuseUpdatedViewHolder(holder, payloads);
  }

  public void endAnimation(@NonNull ViewHolder item) {
    mDecorated.endAnimation(item);
  }

  @CallSuper
  public void endAnimations() {
    mDecorated.endAnimations();
  }

  @NonNull
  public ItemHolderInfo obtainHolderInfo() {
    return mDecorated.obtainHolderInfo();
  }

  /**
   * will also be called for animations started by the mDecorated class
   */
  public void onAnimationFinished(@NonNull ViewHolder viewHolder) {

  }

  /**
   * attention: will not be called for animations that are started by the mDecorated class
   */
  public void onAnimationStarted(@NonNull ViewHolder viewHolder) {

  }

  @NonNull
  public ItemHolderInfo recordPostLayoutInformation(@NonNull State state,
      @NonNull ViewHolder viewHolder) {
    return mDecorated.recordPostLayoutInformation(state, viewHolder);
  }

  @NonNull
  public ItemHolderInfo recordPreLayoutInformation(@NonNull State state,
      @NonNull ViewHolder viewHolder, int changeFlags, @NonNull List<Object> payloads) {
    return mDecorated.recordPreLayoutInformation(state, viewHolder, changeFlags, payloads);
  }

  @CallSuper
  public void runPendingAnimations() {
    mDecorated.runPendingAnimations();
  }

  @CallSuper
  public boolean isRunning() {
    return mDecorated.isRunning();
  }

  /**
   * must be called by subclass after all change animations are finished, even if there were no
   * animations to run
   */
  void dispatchChangeAnimationsFinished() {
    if (!mDecorated.isRunning()) {
      dispatchAnimationsFinished();
    } else {
      mDecorated.isRunning(new ItemAnimatorFinishedListener() {
        @Override
        public void onAnimationsFinished() {
          dispatchAnimationsFinished();
        }
      });
    }
  }

  public final long getAddDuration() {
    return mDecorated.getAddDuration();
  }

  public final void setAddDuration(long newAddDuration) {
    mDecorated.setAddDuration(newAddDuration);
  }

  public long getChangeDuration() {
    return mDecorated.getChangeDuration();
  }

  public void setChangeDuration(long newChangeDuration) {
    mDecorated.setChangeDuration(newChangeDuration);
  }

  public final long getMoveDuration() {
    return mDecorated.getMoveDuration();
  }

  public final void setMoveDuration(long newMoveDuration) {
    mDecorated.setMoveDuration(newMoveDuration);
  }

  public final long getRemoveDuration() {
    return mDecorated.getRemoveDuration();
  }

  public final void setRemoveDuration(long newRemoveDuration) {
    mDecorated.setRemoveDuration(newRemoveDuration);
  }

  private interface AnimationFinishedListener {
    void onAnimationFinished(ViewHolder holder);
  }
}