package com.airbnb.epoxy;

import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;

import java.util.List;

/**
 * Created by thilo on 18.04.17.
 */
public class EpoxyDefaultItemAnimator extends DefaultItemAnimator {
    @Override
    public void setSupportsChangeAnimations(boolean supportsChangeAnimations) {
        throw new UnsupportedOperationException(
                "EpoxyDefaultItemAnimator always supports change animations. You cant change this");
    }

    @Override
    public boolean getSupportsChangeAnimations() {
        return true;
    }

    @Override
    public long getChangeDuration() {
        return super.getChangeDuration();
    }

    @Override
    public boolean canReuseUpdatedViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, @NonNull List<Object> payloads) {
        return super.canReuseUpdatedViewHolder(viewHolder, payloads);
    }

    @Override
    public boolean animateChange(RecyclerView.ViewHolder oldHolder, RecyclerView.ViewHolder newHolder, int fromX, int fromY, int toX, int toY) {
        return super.animateChange(oldHolder, newHolder, fromX, fromY, toX, toY);
    }

    @Override
    public void runPendingAnimations() {
        super.runPendingAnimations();
    }

    @Override
    public void endAnimation(RecyclerView.ViewHolder item) {
        super.endAnimation(item);
    }

    @Override
    public void endAnimations() {
        super.endAnimations();
    }

    @Override
    public boolean isRunning() {
        return super.isRunning();
    }

    @NonNull
    @Override
    public ItemHolderInfo recordPreLayoutInformation(@NonNull RecyclerView.State state, @NonNull RecyclerView.ViewHolder viewHolder, int changeFlags, @NonNull List<Object> payloads) {
        return super.recordPreLayoutInformation(state, viewHolder, changeFlags, payloads);
    }

    @NonNull
    @Override
    public ItemHolderInfo recordPostLayoutInformation(@NonNull RecyclerView.State state, @NonNull RecyclerView.ViewHolder viewHolder) {
        return super.recordPostLayoutInformation(state, viewHolder);
    }
}
