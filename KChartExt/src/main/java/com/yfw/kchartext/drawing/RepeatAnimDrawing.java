package com.yfw.kchartext.drawing;

import android.view.animation.Interpolator;

import androidx.annotation.Nullable;

import com.yfw.kchartcore.canvas.RendererCanvas;
import com.yfw.kchartcore.drawing.AutoAnimDrawing;
import com.yfw.kchartcore.index.IEntity;
import com.yfw.kchartcore.index.range.IndexRange;


/**
 * @日期 : 2020/9/25
 * @描述 : 重复动画的绘制,适合重复持续性动画
 */
public abstract class RepeatAnimDrawing<T extends IndexRange, S extends IEntity> extends AutoAnimDrawing<T, S> {

    public final static int RESTART = 1;    // 重新开始
    public final static int REVERSE = 2;    // 逆转

    private long mCycleTime = 1000;     // 单个周期时间
    private long mCycleStartTime = 0;   // 周期开始时间

    protected int mRepeatMode = RESTART;

    private Interpolator mInterpolator;

    public RepeatAnimDrawing() {
        super();
    }

    public RepeatAnimDrawing(RendererCanvas.DrawingLayoutParams params) {
        super(params);
    }

    public RepeatAnimDrawing(@Nullable T indexRange) {
        super(indexRange);
    }

    public RepeatAnimDrawing(@Nullable T indexRange, RendererCanvas.DrawingLayoutParams params) {
        super(indexRange, params);
    }

    @Override
    public void updateAnimProcessTime(long time) {
        super.updateAnimProcessTime(time);
        if (mCycleStartTime == 0) {
            mCycleStartTime = mAnimProcessTime;
        }
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
        if (mCycleTime == 0) return 1.0f;
        final long time = mAnimProcessTime - mCycleStartTime;
        final float fraction;
        if (mRepeatMode == RESTART) {
            fraction = time % mCycleTime / (float) mCycleTime;
        } else {
            long l = time / mCycleTime % 2;
            if (l == 0) {
                // 正向
                fraction = time % mCycleTime / (float) mCycleTime;
            } else {
                // 逆向
                fraction = (mCycleTime - time % mCycleTime) / (float) mCycleTime;
            }
        }
        if (mInterpolator == null) {
            return Math.max(Math.min(fraction, 1.0f), 0.0f);
        }
        return mInterpolator.getInterpolation(Math.max(Math.min(fraction, 1.0f), 0.0f));
    }

    /**
     * @param repeatMode {@link #RESTART} or {@link #REVERSE}
     */
    public void setRepeatMode(int repeatMode) {
        mRepeatMode = repeatMode;
    }

    public int getRepeatMode() {
        return mRepeatMode;
    }

    public void setRepeatCycle(long cycleTime) {
        if (cycleTime < 0) {
            mCycleTime = 0;
        } else {
            mCycleTime = cycleTime;
        }
    }

    /**
     * 重新开始周期
     */
    public void restartCycle() {
        mCycleStartTime = 0;
    }
}
