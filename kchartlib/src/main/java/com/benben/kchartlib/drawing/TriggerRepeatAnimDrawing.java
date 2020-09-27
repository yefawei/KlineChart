package com.benben.kchartlib.drawing;

import androidx.annotation.CallSuper;
import androidx.annotation.Nullable;

import com.benben.kchartlib.canvas.RendererCanvas;
import com.benben.kchartlib.impl.IDataProvider;
import com.benben.kchartlib.impl.IParentPortLayout;
import com.benben.kchartlib.index.range.IndexRange;

/**
 * @日期 : 2020/7/10
 * @描述 : 主动触发重复动画的绘制
 */
public abstract class TriggerRepeatAnimDrawing extends AbstractAnimDrawing{

    private long mAnimStartTime;
    private long mAnimEndTime;

    private long mCycleTime = 1000;     // 单个周期时间
    private long mCycleStartTime = 0;   // 周期开始时间
    private boolean mPause;

    public TriggerRepeatAnimDrawing() {
    }

    public TriggerRepeatAnimDrawing(RendererCanvas.DrawingLayoutParams params) {
        super(params);
    }

    public TriggerRepeatAnimDrawing(@Nullable IndexRange indexRange) {
        super(indexRange);
    }

    public TriggerRepeatAnimDrawing(@Nullable IndexRange indexRange, RendererCanvas.DrawingLayoutParams params) {
        super(indexRange, params);
    }

    @Override
    public void attachedParentPortLayout(IParentPortLayout portLayout, IDataProvider dataProvider) {
        super.attachedParentPortLayout(portLayout, dataProvider);
        if (inAnimTime() && !mInAnimationManager && !mPause) {
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

    @Override
    @CallSuper
    public void updateAnimProcessTime(long time) {
        super.updateAnimProcessTime(time);
        if (mCycleStartTime == 0) {
            mCycleStartTime = mAnimProcessTime;
        }
    }

    /**
     * 重新开始周期
     */
    public void restartCycle() {
        mCycleStartTime = 0;
    }

    /**
     * 开始重复动画
     *
     * @param repeatCount 重复次数
     * @param cycleTime   单个周期时长
     */
    public void startRepeatAnim(int repeatCount, long cycleTime) {
        if (repeatCount <= 0 || cycleTime <= 0) {
            stopRepeatAnim();
            return;
        }
        mAnimStartTime = System.currentTimeMillis();
        if (repeatCount == Integer.MAX_VALUE) {
            mAnimEndTime = Long.MAX_VALUE;
        } else {
            mAnimEndTime = mAnimStartTime + repeatCount * cycleTime;
        }
        mCycleTime = cycleTime;
        mPause = false;
        if (isAttachedParentPortLayout() && !mInAnimationManager) {
            mDataProvider.getChartAnimation().addAnim(this);
        }
    }

    public void resume() {
        if (!mPause) {
            return;
        }
        mPause = false;
        if (!inAnimTime()) {
            return;
        }
        if (isAttachedParentPortLayout() && !mInAnimationManager) {
            mDataProvider.getChartAnimation().addAnim(this);
        }
    }

    public void pause() {
        if (!inAnimTime()) {
            return;
        }
        mPause = true;
        if (mInAnimationManager) {
            mDataProvider.getChartAnimation().removeAnim(this);
        }
    }

    public void stopRepeatAnim() {
        mAnimStartTime = 0;
        mAnimEndTime = 0;
        mCycleTime = 0;
        mPause = false;
        if (mInAnimationManager) {
            mDataProvider.getChartAnimation().removeAnim(this);
        }
    }

    public boolean isPause() {
        return mPause;
    }

    public float getAnimProcess() {
        if (mCycleTime == 0 || !inAnimTime()) return 1.0f;
        final long time = mAnimProcessTime - mCycleStartTime;
        if (mRepeatMode == RESTART) {
            float fraction = time % mCycleTime / (float) mCycleTime;
            return mInterpolator.getInterpolation(Math.max(Math.min(fraction, 1.0f), 0.0f));
        } else {
            long l = time / mCycleTime % 2;
            if (l == 0) {
                // 正向
                float fraction = time % mCycleTime / (float) mCycleTime;
                return mInterpolator.getInterpolation(Math.max(Math.min(fraction, 1.0f), 0.0f));
            } else {
                // 逆向
                float fraction = (mCycleTime - time % mCycleTime) / (float) mCycleTime;
                return mInterpolator.getInterpolation(Math.max(Math.min(fraction, 1.0f), 0.0f));
            }
        }
    }
}