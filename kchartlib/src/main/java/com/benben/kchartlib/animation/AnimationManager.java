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
        animCheck();
    }

    public void animUpdate() {
        long currTime = System.currentTimeMillis();
        for (int i = 0; i < mChartAnimtion.mAnimations.size(); i++) {
            Animation animation = mChartAnimtion.mAnimations.get(i);
            animation.updateAnimProcessTime(currTime);
            if (animation.isAutoAnim()) {
                animation.setInAnim(true);
            } else if (animation.getAnimEndTime() > currTime) {
                animation.setInAnim(true);
            } else {
                animation.setInAnim(false);
                animation.inAnimationCall(false);
                mChartAnimtion.mAnimations.remove(i);
                i--;
            }
        }
        inAnim = !mChartAnimtion.mAnimations.isEmpty();
    }

    public void animCheck() {
        if (inAnim) {
            mListener.onAnimation();
        }
    }

    public AnimationManager.ChartAnimtion getChartAnimtion() {
        return mChartAnimtion;
    }

    public static class ChartAnimtion {

        private ArrayList<Animation> mAnimations = new ArrayList<>();

        private AnimationManager mManager;

        public ChartAnimtion(AnimationManager manager) {
            mManager = manager;
        }

        public void addAnim(Animation anim) {
            if (mAnimations.contains(anim)) return;

            mAnimations.add(anim);
            anim.inAnimationCall(true);
            mManager.postAnim();
        }

        public void removeAnim(Animation anim) {
            if (mAnimations.remove(anim)) {
                anim.inAnimationCall(false);
            }
        }
    }

    public interface AnimationListener {
        void onAnimation();
    }
}
