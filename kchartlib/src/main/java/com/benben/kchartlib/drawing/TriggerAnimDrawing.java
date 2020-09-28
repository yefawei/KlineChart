package com.benben.kchartlib.drawing;

import androidx.annotation.Nullable;

import com.benben.kchartlib.canvas.RendererCanvas;
import com.benben.kchartlib.impl.IDataProvider;
import com.benben.kchartlib.impl.IParentPortLayout;
import com.benben.kchartlib.index.range.IndexRange;

/**
 * @日期 : 2020/7/10
 * @描述 : 主动触发动画的绘制
 */
public abstract class TriggerAnimDrawing<T extends IndexRange> extends AbstractAnimDrawing<T>{

    private long mAnimStartTime;
    private long mDuration;
    private long mAnimEndTime;

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

    public void stopAnim() {
        mAnimStartTime = 0;
        mAnimEndTime = 0;
        if (mInAnimationManager) {
            mDataProvider.getChartAnimation().removeAnim(this);
        }
    }

    public float getAnimProcess() {
        if (!inAnimTime()) return 1.0f;
        float fraction = (mAnimProcessTime - mAnimStartTime) / (float) mDuration;
        return mInterpolator.getInterpolation(Math.max(Math.min(fraction, 1.0f), 0.0f));
    }
}