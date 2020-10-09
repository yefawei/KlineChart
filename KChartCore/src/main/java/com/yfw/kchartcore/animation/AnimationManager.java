package com.yfw.kchartcore.animation;

import androidx.annotation.NonNull;

import java.util.ArrayList;

/**
 * @日期 : 2020/7/10
 * @描述 : 动画管理者
 */
public class AnimationManager {

    private AnimationListener mListener;
    private ChartAnimtion mChartAnimtion;
    private boolean inAnim;

    public AnimationManager(@NonNull AnimationListener listener) {
        mListener = listener;
        mChartAnimtion = new ChartAnimtion(this);
    }

    private void postAnim() {
        if (inAnim) return;
        animCheck();
    }

    /**
     * 更新动画时间
     */
    public void animUpdate() {
        long currTime = System.currentTimeMillis();
        Animation animation;
        for (int i = 0; i < mChartAnimtion.mAnimations.size(); i++) {
            animation = mChartAnimtion.mAnimations.get(i);
            animation.updateAnimProcessTime(currTime);
            if (animation.isAutoAnim()) {
                animation.setInAnim(true);
            } else if (animation.getAnimEndTime() > currTime) {
                animation.setInAnim(true);
            } else {
                animation.setInAnim(false);
                animation.callInAnimation(false);
                mChartAnimtion.mAnimations.remove(i);
                i--;
            }
        }
        inAnim = !mChartAnimtion.mAnimations.isEmpty();
    }

    /**
     * 检测是否处于动画中并通知回调
     */
    public void animCheck() {
        if (inAnim) {
            mListener.onAnimation();
        }
    }

    public AnimationManager.ChartAnimtion getChartAnimtion() {
        return mChartAnimtion;
    }

    /**
     * 图表动画
     */
    public static class ChartAnimtion {

        private ArrayList<Animation> mAnimations = new ArrayList<>();

        private AnimationManager mManager;

        public ChartAnimtion(AnimationManager manager) {
            mManager = manager;
        }

        /**
         * 添加需要执行的动画
         */
        public void addAnim(Animation anim) {
            if (mAnimations.contains(anim)) return;

            mAnimations.add(anim);
            anim.callInAnimation(true);
            mManager.postAnim();
        }

        /**
         * 移除动画
         */
        public void removeAnim(Animation anim) {
            if (mAnimations.remove(anim)) {
                anim.setInAnim(false);
                anim.callInAnimation(false);
            }
        }
    }

    public interface AnimationListener {
        void onAnimation();
    }
}
