package com.benben.kchartlib.animation;

import java.util.ArrayList;

/**
 * @日期 : 2020/7/10
 * @描述 :
 */
public class AnimationManager {

    private AnimationListener mListener;
    private ChartAnimtion mChartAnimtion;
    private boolean inAnim;

    public AnimationManager(AnimationListener listener) {
        mListener = listener;
        mChartAnimtion = new ChartAnimtion(this);
    }

    private void postAnim() {
        if (inAnim) return;
        animUpdate();
    }

    public void animUpdate() {
        if (mChartAnimtion.mAnimations.isEmpty()) {
            inAnim = false;
            return;
        }
        long currTime = System.currentTimeMillis();
        for (int i = 0; i < mChartAnimtion.mAnimations.size(); i++) {
            Animation animation = mChartAnimtion.mAnimations.get(i);
            if (animation.isAutoAnim()) {
                animation.setInAnim(true);
            } else if (animation.AnimEndTime() > currTime) {
                animation.setInAnim(true);
            } else {
                animation.setInAnim(false);
                mChartAnimtion.mAnimations.remove(i);
                i--;
            }
        }
        inAnim = true;
        mListener.onAnimation();
    }

    public AnimationManager.ChartAnimtion getChartAnimtion() {
        return mChartAnimtion;
    }

    public static class ChartAnimtion {

        private ArrayList<Animation> mAnimations = new ArrayList<>(8);

        private AnimationManager mManager;

        public ChartAnimtion(AnimationManager manager) {
            mManager = manager;
        }

        public void addAnim(Animation anim) {
            mAnimations.add(anim);
            mManager.postAnim();
        }

        public void removeAnim(Animation anim) {
            mAnimations.remove(anim);
        }
    }

    public interface AnimationListener {
        void onAnimation();
    }
}
