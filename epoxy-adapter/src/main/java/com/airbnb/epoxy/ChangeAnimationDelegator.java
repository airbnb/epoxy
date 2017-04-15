package com.airbnb.epoxy;

import android.animation.AnimatorListenerAdapter;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView.ItemAnimator;
import android.support.v7.widget.RecyclerView.State;
import android.support.v7.widget.RecyclerView.ViewHolder;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ItemAnimator that wraps any other ItemAnimator and delegates change animations to
 * {@link EpoxyModel}
 */
public final class ChangeAnimationDelegator extends ItemAnimatorDecorator
    implements EpoxyItemAnimation.Listener {

  @NonNull private final Map<ViewHolder, EpoxyItemAnimation> mChangeAnimations = new HashMap<>();

  public ChangeAnimationDelegator(@NonNull ItemAnimator decoratedAnimator) {
    super(decoratedAnimator);
  }

  @Override
  public long getChangeDuration() {
    long maxDuration = super.getChangeDuration();

    for (EpoxyItemAnimation anim : mChangeAnimations.values()) {
      maxDuration = Math.max(maxDuration, anim.getDuration());
    }

    return maxDuration;
  }

  public void onAnimationEnd(EpoxyItemAnimation anim) {
    dispatchAnimationFinished(mChangeAnimations.remove(anim.holder));

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

  @Override
  public boolean animateAppearance(@NonNull ViewHolder viewHolder,
      @Nullable ItemHolderInfo preLayoutInfo, @NonNull ItemHolderInfo postLayoutInfo) {

    return super.animateAppearance(viewHolder,
        preLayoutInfo != null
            ? ((DecoratedItemHolderInfo) preLayoutInfo).decoratedHolderInfo
            : null,
        ((DecoratedItemHolderInfo) postLayoutInfo).decoratedHolderInfo);
  }

  @SuppressWarnings("unchecked")
  @Override
  public boolean animateChange(
      @NonNull final ViewHolder oldHolder, @NonNull final ViewHolder newHolder,
      @NonNull final ItemHolderInfo preLayoutInfo, @NonNull final ItemHolderInfo postLayoutInfo) {

    final EpoxyViewHolder holder = ((EpoxyViewHolder) oldHolder);
    final EpoxyModel previousModel = ((PreviousModelHolderInfo) preLayoutInfo).previousModel;

    if (oldHolder == newHolder && previousModel != null && holder.canBindChanges(previousModel)) {

      final EpoxyModel newModel = holder.getModel();
      final Object viewToBind = holder.objectToBind();

      final EpoxyItemAnimation oldAnim = mChangeAnimations.get(oldHolder);

      if (oldAnim == null || !oldAnim.isRunning()) {
        EpoxyItemAnimation newAnim = newModel.bindChangesAnimated(viewToBind, previousModel, null);
        checkAnimNotStarted(newAnim);
        newAnim.addListener(this);
        mChangeAnimations.put(holder, newAnim);
      } else {
        EpoxyItemAnimation continuedAnim =
            newModel.bindChangesAnimated(viewToBind, previousModel, oldAnim);

        if (continuedAnim != null) {
          //TODO: if continuedAnim is started again, dispatchAnimationStarted will be called in the
          // listener but we already did that for the interruptedAnim. Bug?
          continuedAnim.addListener(this);

          //cancel old anim if user decided to create a new animator instead of reusing the old one
          if (continuedAnim != oldAnim) {
            oldAnim.cancel();
            continuedAnim.start();
          }

          mChangeAnimations.put(holder, continuedAnim);
        } else {
          final EpoxyItemAnimation newAnim =
              newModel.bindChangesAnimated(viewToBind, previousModel, null);
          checkAnimNotStarted(newAnim);
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
      return super.animateChange(oldHolder, newHolder,
          ((DecoratedItemHolderInfo) preLayoutInfo).decoratedHolderInfo,
          ((DecoratedItemHolderInfo) postLayoutInfo).decoratedHolderInfo);
    }
  }

  @Override
  public boolean animateDisappearance(@NonNull ViewHolder viewHolder,
      @NonNull ItemHolderInfo preLayoutInfo, @Nullable ItemHolderInfo postLayoutInfo) {

    return super.animateDisappearance(viewHolder,
        ((DecoratedItemHolderInfo) preLayoutInfo).decoratedHolderInfo,
        postLayoutInfo != null
            ? ((DecoratedItemHolderInfo) postLayoutInfo).decoratedHolderInfo
            : null);
  }

  @Override
  public boolean animatePersistence(@NonNull ViewHolder viewHolder,
      @NonNull ItemHolderInfo preLayoutInfo, @NonNull ItemHolderInfo postLayoutInfo) {

    return super.animatePersistence(viewHolder,
        ((DecoratedItemHolderInfo) preLayoutInfo).decoratedHolderInfo,
        ((DecoratedItemHolderInfo) postLayoutInfo).decoratedHolderInfo);
  }

  private void checkAnimNotStarted(EpoxyItemAnimation anim) {
    if (anim.isRunning()) {
      throw new IllegalStateException(
          "EpoxyItemAnimation already started. Do not start animations yourself!");
    }
  }

  @Override
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

  @Override
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

  @Override
  public boolean isRunning() {
    boolean changesRunning = false;

    for (EpoxyItemAnimation anim : mChangeAnimations.values()) {
      changesRunning = changesRunning || anim.isRunning();
    }

    return changesRunning || super.isRunning();
  }

  @Override
  public boolean canReuseUpdatedViewHolder(@NonNull ViewHolder holder,
      @NonNull List<Object> payloads) {
    // pass empty list to super because it contains only the previous model and DefaultItemAnimator
    // would decide not to cross fade
    return (holder instanceof EpoxyViewHolder && !payloads.isEmpty())
        || super.canReuseUpdatedViewHolder(holder,
        Collections.emptyList()); //TODO: only remove our payloads
  }

  @NonNull
  @Override
  public ItemHolderInfo recordPreLayoutInformation(@NonNull State state,
      @NonNull ViewHolder viewHolder, int changeFlags, @NonNull List<Object> payloads) {

    EpoxyModel previousModel = DiffPayload.getModelFromPayload(payloads, viewHolder.getItemId());

    if (previousModel == null) {
      throw new IllegalStateException(
          "recordPreLayoutInfo: could not get previousModel from change payloads");
    }

    return new PreviousModelHolderInfo(
        previousModel,
        super.recordPreLayoutInformation(state, viewHolder, changeFlags, payloads));

  }

  @NonNull
  @Override
  public ItemHolderInfo recordPostLayoutInformation(@NonNull State state,
      @NonNull ViewHolder viewHolder) {

    return new DecoratedItemHolderInfo(super.recordPostLayoutInformation(state, viewHolder));
  }

  private class DecoratedItemHolderInfo extends ItemHolderInfo {
    @NonNull final ItemHolderInfo decoratedHolderInfo;

    DecoratedItemHolderInfo(@NonNull ItemHolderInfo decoratedHolderInfo) {
      this.decoratedHolderInfo = decoratedHolderInfo;
    }
  }

  private class PreviousModelHolderInfo extends DecoratedItemHolderInfo {
    @NonNull final EpoxyModel previousModel;

    PreviousModelHolderInfo(@NonNull EpoxyModel previousModel,
        @NonNull ItemHolderInfo decoratedHolderInfo) {
      super(decoratedHolderInfo);
      this.previousModel = previousModel;
    }
  }

}