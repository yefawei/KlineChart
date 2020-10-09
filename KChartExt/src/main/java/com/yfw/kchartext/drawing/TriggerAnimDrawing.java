package com.yfw.kchartext.drawing;

import android.view.animation.Interpolator;

import androidx.annotation.Nullable;

import com.yfw.kchartcore.canvas.RendererCanvas;
import com.yfw.kchartcore.drawing.AbstractAnimDrawing;
import com.yfw.kchartcore.impl.IDataProvider;
import com.yfw.kchartcore.index.range.IndexRange;
import com.yfw.kchartcore.layout.IParentPortLayout;

/**
 * @日期 : 2020/7/10
 * @描述 : 主动触发动画的绘制
 */
public abstract class TriggerAnimDrawing<T extends IndexRange> extends AbstractAnimDrawing<T> {

    private long mAnimStartTime;
    private long mDuration;
    private long mAnimEndTime;

    private Interpolator mInterpolator;

    public TriggerAnimDrawing() {
    }

    public TriggerAnimDrawing(RendererCanvas.DrawingLayoutParams params) {
        super(params);
    }

    public TriggerAnimDrawing(@Nullable T indexRange) {
        super(indexRange);
    }

    public TriggerAnimDrawing(@Nullable T indexRange, RendererCanvas.DrawingLayoutParams params) {
        super(indexRange, params);
    }

    @Override
    public void attachedParentPortLayout(IParentPortLayout portLayout, IDataProvider dataProvider) {
        super.attachedParentPortLayout(portLayout, dataProvider);
        if (inAnimTime() && !inAnimationManager()) {
            mDataProvider.getChartAnimation().addAnim(this);
        }
    }

    @Override
    public void detachedParentPortLayout() {
        if (inAnimationManager()) {
            mDataProvider.getChartAnimation().removeAnim(this);
        }
        super.detachedParentPortLayout();
    }

    @Override
    public final boolean isAutoAnim() {
        return false;
    }

    @Override
    public final long getAnimStartTime() {
        return mAnimStartTime;
    }

    @Override
    public final long getAnimEndTime() {
        return mAnimEndTime;
    }

    @Override
    public final boolean inAnimTime() {
        return mAnimEndTime > System.currentTimeMillis();
    }

    @Override
    public void setInterpolator(Interpolator i) {
        mInterpolator = i;
    }

    @Override
    public Interpolator getInterpolator() {
        return mInterpolator;
    }

    @Override
    public float getAnimProcess() {
        if (!inAnimTime()) return 1.0f;
        float fraction = (mAnimProcessTime - mAnimStartTime) / (float) mDuration;
        if (mInterpolator == null) {
            return Math.max(Math.min(fraction, 1.0f), 0.0f);
        }
        return mInterpolator.getInterpolation(Math.max(Math.min(fraction, 1.0f), 0.0f));
    }

    /**
     * 开始执行动画
     * 上一个动画没有结束的情况下调用该函数将以新的动画周期开始
     *
     * @param duration 执行时长
     */
    public void startAnim(long duration) {
        if (duration <= 0) {
            return;
        }
        mAnimStartTime = System.currentTimeMillis();
        mDuration = duration;
        mAnimEndTime = mAnimStartTime + duration;
        if (isAttachedParentPortLayout() && !inAnimationManager()) {
            mDataProvider.getChartAnimation().addAnim(this);
        }
    }

    public void stopAnim() {
        mAnimStartTime = 0;
        mAnimEndTime = 0;
        if (inAnimationManager()) {
            mDataProvider.getChartAnimation().removeAnim(this);
        }
    }
}