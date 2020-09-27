package com.benben.kchartlib.drawing;

import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;

import com.benben.kchartlib.canvas.RendererCanvas;
import com.benben.kchartlib.index.range.IndexRange;

/**
 * @日期 : 2020/9/25
 * @描述 : 重复动画的绘制,适合重复持续性动画
 */
public abstract class RepeatAnimDrawing extends AutoAnimDrawing {

    private int mRepeatMode = RESTART;

    private long mCycleTime = 1000;     // 单个周期时间
    private long mCycleStartTime = 0;   // 周期开始时间

    private Interpolator mInterpolator;

    public RepeatAnimDrawing() {
        super();
    }

    public RepeatAnimDrawing(RendererCanvas.DrawingLayoutParams params) {
        super(params);
    }

    public RepeatAnimDrawing(@Nullable IndexRange indexRange) {
        super(indexRange);
    }

    public RepeatAnimDrawing(@Nullable IndexRange indexRange, RendererCanvas.DrawingLayoutParams params) {
        super(indexRange, params);
    }

    @Override
    public void updateAnimProcessTime(long time) {
        super.updateAnimProcessTime(time);
        if (mCycleStartTime == 0) {
            mCycleStartTime = mAnimProcessTime;
        }
    }

    /**
     * @param repeatMode {@link #RESTART} or {@link #REVERSE}
     */
    public void setRepeatMode(int repeatMode) {
        mRepeatMode = repeatMode;
    }

    public void setRepeatCycle(long cycleTime) {
        if (cycleTime < 0) {
            mCycleTime = 0;
        } else {
            mCycleTime = cycleTime;
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

    /**
     * 重新开始周期
     */
    public void restartCycle() {
        mCycleStartTime = 0;
    }

    public float getAnimProcess() {
        if (mCycleTime == 0) return 1.0f;
        ensureInterpolator();
        final long time = mAnimProcessTime - mCycleStartTime;
        if (mRepeatMode == RESTART) {
            float fraction = time % mCycleTime / (float) mCycleTime;
            return mInterpolator.getInterpolation(Math.max(Math.min(fraction, 1.0f), 0.0f));
        } else {
            long l = time / mCycleTime / 2;
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
