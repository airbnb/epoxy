package com.airbnb.epoxy;

import android.animation.AnimatorListenerAdapter;
import android.animation.EpoxyItemAnimation.AnimatorListener;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView.ItemAnimator;
import android.support.v7.widget.RecyclerView.ViewHolder;

import java.util.Collections;
import java.util.List;

/**
 * ItemAnimator that wraps any other ItemAnimator and delegates change animations to
 * {@link EpoxyModel}
 */
public final class ChangeAnimationDelegator extends ItemAnimatorDecorator
    implements AnimatorListener {
  /* use a BiMap so we can use the same listener for every animator and quickly find the holder for
     an animation. We do this to be able to remove only OUR listener from an animator and not those
     that the user might have set */
  @NonNull private final BiHashMap<ViewHolder, EpoxyItemAnimation> mChangeAnimations =
      new BiHashMap<>();

  public ChangeAnimationDelegator(@NonNull ItemAnimator decoratedAnimator) {
    super(decoratedAnimator);
  }

  public long getChangeDuration() {
    long maxDuration = super.getChangeDuration();

    for (EpoxyItemAnimation anim : mChangeAnimations.values()) {
      maxDuration = Math.max(maxDuration, anim.getDuration());
    }

    return maxDuration;
  }

  public void onAnimationEnd(EpoxyItemAnimation anim) {
    dispatchAnimationFinished(mChangeAnimations.inverse().get(anim));
    mChangeAnimations.inverse().remove(anim);

    if (mChangeAnimations.isEmpty()) {
      dispatchChangeAnimationsFinished();
    }
  }

  @Override
  public void onAnimationCancel(EpoxyItemAnimation anim) {
    mChangeAnimations.inverse().remove(anim);
  }

  public void onAnimationStart(EpoxyItemAnimation anim) {
    dispatchAnimationStarted(mChangeAnimations.inverse().get(anim));
  }

  @SuppressWarnings("unchecked")
  public boolean animateChange(
      @NonNull final ViewHolder oldHolder, @NonNull final ViewHolder newHolder,
      @NonNull ItemHolderInfo preLayoutInfo, @NonNull ItemHolderInfo postLayoutInfo) {

    if (oldHolder == newHolder && oldHolder instanceof EpoxyViewHolder
        && canHolderAnimateChanges((EpoxyViewHolder) oldHolder)) {

      final EpoxyViewHolder holder = ((EpoxyViewHolder) oldHolder);
      final EpoxyModel newModel = holder.getModel();
      final Object viewToBind = holder.objectToBind();
      final EpoxyModel oldModel = DiffPayload.getModelFromPayload();

      final EpoxyItemAnimation oldAnim = mChangeAnimations.get(oldHolder);

      if (oldAnim == null || !oldAnim.isRunning()) {
        EpoxyItemAnimation newAnim = newModel.bindChangesAnimated(viewToBind, oldModel);
        newAnim.addListener(this);
        mChangeAnimations.put(holder, newAnim);
      } else {
        EpoxyItemAnimation continuedAnim =
            newModel.bindChangesAnimated(viewToBind, oldModel, oldAnim);

        if (continuedAnim != null) {
          //cancel old anim if user decided to create a new animator instead of reusing the old one
          if (continuedAnim != oldAnim) {
            oldAnim.cancel();
            continuedAnim.start();
          }

          continuedAnim.addListener(this);
          mChangeAnimations.put(holder, continuedAnim);
        } else {
          final EpoxyItemAnimation newAnim = newModel.bindChangesAnimated(viewToBind, oldModel);
          newAnim.addListener(this);

          oldAnim.removeListener(this);

          oldAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(EpoxyItemAnimation animation) {
              mChangeAnimations.put(holder, newAnim);
              newAnim.start();
            }
          });
        }
      }

      return true;
    } else {
      return super.animateChange(oldHolder, newHolder, preLayoutInfo, postLayoutInfo);
    }
  }

  public void runPendingAnimations() {
    super.runPendingAnimations();

    // start each animator in the map
    for (EpoxyItemAnimation anim : mChangeAnimations.values()) {
      if (!anim.isRunning()) {
        // play change animations after removals, in accordance with DefaultItemAnimator
        if (disappearAnimationsScheduled()) {
          anim.setStartDelay(getRemoveDuration());
        }

        anim.start();
        // don't dispatch animation start here because it's already done in the listener
      }
    }
  }

  public void endAnimation(@NonNull ViewHolder holder) {
    super.endAnimation(holder);

    EpoxyItemAnimation anim = mChangeAnimations.get(holder);
    if (anim != null) {
      anim.end();
      // don't dispatch animation end here because it's already done in the listener
    }
  }

  public void endAnimations() {
    super.endAnimations();

    for (EpoxyItemAnimation anim : mChangeAnimations.values()) {
      anim.end();
      // don't dispatch animation end here because it's already done in the listener
    }
  }

  public boolean isRunning() {
    boolean changesRunning = false;

    for (EpoxyItemAnimation anim : mChangeAnimations.values()) {
      changesRunning = changesRunning || anim.isRunning();
    }

    return changesRunning || super.isRunning();
  }

  public boolean canReuseUpdatedViewHolder(@NonNull ViewHolder holder,
      @NonNull List<Object> payloads) {
    // pass empty list to super because it contains only the previous model and DefaultItemAnimator
    // would decide not to cross fade
    return (holder instanceof EpoxyViewHolder && !payloads.isEmpty())
        || super.canReuseUpdatedViewHolder(holder, Collections.emptyList());
  }

  @SuppressWarnings("unchecked")
  private boolean canHolderAnimateChanges(@NonNull EpoxyViewHolder holder) {
    return ((EpoxyModel) holder.getModel()).canReuseViewHolder();
  }
}