package com.benben.kchartlib.drawing;

import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import androidx.annotation.CallSuper;
import androidx.annotation.Nullable;

import com.benben.kchartlib.animation.Animation;
import com.benben.kchartlib.canvas.RendererCanvas;
import com.benben.kchartlib.impl.IDataProvider;
import com.benben.kchartlib.impl.IParentPortLayout;
import com.benben.kchartlib.index.range.IndexRange;

/**
 * @日期 : 2020/7/10
 * @描述 : 主动触发动画的绘制
 */
public abstract class TriggerAnimDrawing extends Drawing implements Animation {

    private boolean mInAnimationManager;
    private boolean mInAnim;

    private long mAnimStartTime;
    private long mDuration;
    private long mAnimEndTime;

    private Interpolator mInterpolator;

    public TriggerAnimDrawing() {
    }

    public TriggerAnimDrawing(RendererCanvas.DrawingLayoutParams params) {
        super(params);
    }

    public TriggerAnimDrawing(@Nullable IndexRange indexRange) {
        super(indexRange);
    }

    public TriggerAnimDrawing(@Nullable IndexRange indexRange, RendererCanvas.DrawingLayoutParams params) {
        super(indexRange, params);
    }

    @Override
    public void attachedParentPortLayout(IParentPortLayout portLayout, IDataProvider dataProvider) {
        super.attachedParentPortLayout(portLayout, dataProvider);
        if (inAnimTime() && !mInAnimationManager) {
            mDataProvider.getChartAnimation().addAnim(this);
        }
    }

    @Override
    public void detachedParentPortLayout() {
        if (mInAnimationManager) {
            mDataProvider.getChartAnimation().removeAnim(this);
        }
        super.detachedParentPortLayout();
    }

    @Override
    public boolean inAnimationManager() {
        return mInAnimationManager;
    }

    @Override
    @CallSuper
    public void inAnimationCall(boolean in) {
        mInAnimationManager = in;
    }


    @Override
    public final boolean isAutoAnim() {
        return false;
    }

    @Override
    public long getAnimStartTime() {
        return mAnimStartTime;
    }

    @Override
    public long getAnimEndTime() {
        return mAnimEndTime;
    }

    @Override
    @CallSuper
    public void setInAnim(boolean inAnim) {
        mInAnim = inAnim;
    }

    @Override
    public boolean inAnim() {
        return mInAnim;
    }

    @Override
    public boolean inAnimTime() {
        return mAnimEndTime > System.currentTimeMillis();
    }

    /**
     * 开始执行动画
     * 上一个动画没有结束的情况下调用该函数将以新的动画周期开始
     * @param duration 执行时长
     */
    public void startAnim(long duration) {
        if (duration <= 0) {
            return;
        }
        mAnimStartTime = System.currentTimeMillis();
        mDuration = duration;
        mAnimEndTime = mAnimStartTime + duration;
        if (isAttachedParentPortLayout() && !mInAnimationManager) {
            mDataProvider.getChartAnimation().addAnim(this);
        }
    }

    public void setInterpolator(Interpolator i) {
        mInterpolator = i;
    }

    public Interpolator getInterpolator() {
        ensureInterpolator();
        return mInterpolator;
    }

    private void ensureInterpolator() {
        if (mInterpolator == null) {
            mInterpolator = new LinearInterpolator();
        }
    }

    public float getAnimProcess() {
        if (!mInAnim) return 1.0f;
        float fraction = (System.currentTimeMillis() - mAnimStartTime) / (float) mDuration;
        ensureInterpolator();
        return mInterpolator.getInterpolation(Math.max(Math.min(fraction, 1.0f), 0.0f));
    }
}