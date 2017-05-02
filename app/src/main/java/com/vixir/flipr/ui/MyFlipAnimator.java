package com.vixir.flipr.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import java.util.List;

/*
from : https://www.thedroidsonroids.com/blog/android/android-flipa-card-animation-exlpained/ */

public class MyFlipAnimator extends DefaultItemAnimator {
    private AccelerateInterpolator mAccelerateInterpolator = new AccelerateInterpolator();
    private DecelerateInterpolator mDecelerateInterpolator = new DecelerateInterpolator();
    private ArrayMap<RecyclerView.ViewHolder, AnimatorInfo> mAnimatorMap = new ArrayMap<>();

    @Override
    public boolean canReuseUpdatedViewHolder(RecyclerView.ViewHolder viewHolder) {
        return true;
    }


    @NonNull
    @Override
    public ItemHolderInfo recordPreLayoutInformation(@NonNull RecyclerView.State state, @NonNull RecyclerView.ViewHolder viewHolder, int changeFlags, @NonNull List<Object> payloads) {
        ColorTextInfo info = (ColorTextInfo) super.recordPreLayoutInformation(state, viewHolder,
                changeFlags, payloads);
        return getItemHolderInfo((FeedAdapter.FliprShotViewHolder) viewHolder, info);
    }

    @NonNull
    @Override
    public ItemHolderInfo recordPostLayoutInformation(@NonNull RecyclerView.State state, @NonNull RecyclerView.ViewHolder viewHolder) {
        ColorTextInfo info = (ColorTextInfo) super.recordPostLayoutInformation(state, viewHolder);
        return getItemHolderInfo((FeedAdapter.FliprShotViewHolder) viewHolder, info);
    }

    @Override
    public ItemHolderInfo obtainHolderInfo() {
        return new ColorTextInfo();
    }


    @NonNull
    private ItemHolderInfo getItemHolderInfo(FeedAdapter.FliprShotViewHolder viewHolder, ColorTextInfo info) {
        final FeedAdapter.FliprShotViewHolder myHolder = viewHolder;
        final int bgColor = Color.GRAY;
        info.color = bgColor;
        info.image = myHolder.image;
        info.text = "TA DA";
        return info;
    }

    private class AnimatorInfo {
        Animator overallAnim;
        ObjectAnimator oldViewRotate, newViewRotate;

        public AnimatorInfo(Animator overallAnim, ObjectAnimator oldViewRotate, ObjectAnimator newViewRotate) {
            this.overallAnim = overallAnim;
            this.newViewRotate = newViewRotate;
            this.oldViewRotate = oldViewRotate;
        }
    }

    @Override
    public boolean animateChange(RecyclerView.ViewHolder oldHolder, final RecyclerView.ViewHolder newHolder, @NonNull ItemHolderInfo preInfo, @NonNull ItemHolderInfo postInfo) {
        if (oldHolder != newHolder) {
            return super.animateChange(oldHolder, newHolder, preInfo, postInfo);
        }
        final FeedAdapter.FliprShotViewHolder newViewHolder = (FeedAdapter.FliprShotViewHolder) newHolder;
        AnimatorInfo runningInfo = mAnimatorMap.get(newHolder);
        long prevAnimPlayTime = 0;
        boolean firstHalf = false;
        if (runningInfo != null) {
            firstHalf = runningInfo.oldViewRotate != null &&
                    runningInfo.oldViewRotate.isRunning();
            prevAnimPlayTime = firstHalf ?
                    runningInfo.oldViewRotate.getCurrentPlayTime() :
                    runningInfo.newViewRotate.getCurrentPlayTime();
            runningInfo.overallAnim.cancel();
        }
        ObjectAnimator oldViewRotate = null, newViewRotate;
        if (runningInfo == null || firstHalf) {
            oldViewRotate = ObjectAnimator.ofFloat(newViewHolder.itemView, View.ROTATION_X, 0, 90);
            oldViewRotate.setInterpolator(mAccelerateInterpolator);
            if (runningInfo != null) {
                oldViewRotate.setCurrentPlayTime(prevAnimPlayTime);
            }
            oldViewRotate.addListener(new AnimatorListenerAdapter() {

                boolean mCanceled = false;

                @Override
                public void onAnimationCancel(Animator animation) {
                    mCanceled = true;
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if (!mCanceled) {
                        if (newViewHolder.isBackVisible == false) {
                            newViewHolder.isBackVisible = true;
                            newViewHolder.mCardBackLayout.setVisibility(View.VISIBLE);
                            newViewHolder.mCardFrontLayout.setVisibility(View.GONE);
                        } else {
                            newViewHolder.isBackVisible = false;
                            newViewHolder.mCardBackLayout.setVisibility(View.GONE);
                            newViewHolder.mCardFrontLayout.setVisibility(View.VISIBLE);
                        }

                    }
                }

                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                }
            });

        }
        newViewRotate = ObjectAnimator.ofFloat(newViewHolder.itemView, View.ROTATION_X, -90, 0);
        newViewRotate.setInterpolator(mDecelerateInterpolator);
        if (runningInfo != null && !firstHalf) {
            // If we're interrupting a previous second-phase animation, seek to that time
            newViewRotate.setCurrentPlayTime(prevAnimPlayTime);
        }

        // Choreograph first and second half. First half may be null if we interrupted
        // a second-phase animation
        AnimatorSet newAnim = new AnimatorSet();
        if (oldViewRotate != null) {
            newAnim.playSequentially(oldViewRotate, newViewRotate);
        } else {
            newAnim.play(newViewRotate);
        }

        AnimatorSet changeAnim = new AnimatorSet();
        changeAnim.play(newAnim);
        changeAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                dispatchAnimationFinished(newHolder);
                mAnimatorMap.remove(newHolder);
            }
        });
        changeAnim.start();
        AnimatorInfo runningAnimInfo = new AnimatorInfo(changeAnim, oldViewRotate, newViewRotate);
        mAnimatorMap.put(newHolder, runningAnimInfo);
        return true;
    }

    @Override
    public void endAnimation(RecyclerView.ViewHolder item) {
        super.endAnimation(item);
        if (!mAnimatorMap.isEmpty()) {
            final int numRunning = mAnimatorMap.size();
            for (int i = numRunning; i >= 0; i--) {
                if (item == mAnimatorMap.keyAt(i)) {
                    mAnimatorMap.valueAt(i).overallAnim.cancel();
                }
            }
        }
    }

    @Override
    public boolean isRunning() {
        return super.isRunning() || !mAnimatorMap.isEmpty();
    }

    @Override
    public void endAnimations() {
        super.endAnimations();
        if (!mAnimatorMap.isEmpty()) {
            final int numRunning = mAnimatorMap.size();
            for (int i = numRunning; i >= 0; i--) {
                mAnimatorMap.valueAt(i).overallAnim.cancel();
            }
        }
    }

    private class ColorTextInfo extends ItemHolderInfo {
        int color;
        ImageView image;
        String text;
    }
}
