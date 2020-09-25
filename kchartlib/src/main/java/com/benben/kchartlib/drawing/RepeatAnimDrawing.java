package com.benben.kchartlib.drawing;

import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;

import com.benben.kchartlib.canvas.RendererCanvas;
import com.benben.kchartlib.impl.IDataProvider;
import com.benben.kchartlib.impl.IParentPortLayout;
import com.benben.kchartlib.index.range.IndexRange;

/**
 * @日期 : 2020/9/25
 * @描述 : 重复动画的绘制,适合重复持续性动画
 */
public class RepeatAnimDrawing extends AutoAnimDrawing {

    public static final int RESTART = 1;    // 重新开始
    public static final int REVERSE = 2;    // 逆转

    private int mRepeatMode = RESTART;
    boolean mCycleFlip = false;

    private long mCycle = 1000;
    private long mStartTime = 0;

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
    public void attachedParentPortLayout(IParentPortLayout portLayout, IDataProvider dataProvider) {
        super.attachedParentPortLayout(portLayout, dataProvider);
        mCycleFlip = false;
    }

    /**
     * @param repeatMode {@link #RESTART} or {@link #REVERSE}
     */
    public void setRepeatMode(int repeatMode) {
        mRepeatMode = repeatMode;
    }

    public void setRepeatCycle(long cycle) {
        if (cycle < 0) {
            mCycle = 0;
        } else {
            mCycle = cycle;
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
        if (mCycle == 0) return 1.0f;
        ensureInterpolator();
        final long time;
        if (mStartTime == 0) {
            mStartTime = System.currentTimeMillis();
            time = 0;
        } else {
            time = System.currentTimeMillis() - mStartTime;
        }
        if (mRepeatMode == RESTART) {
            float fraction = time % mCycle / (float) mCycle;
            return mInterpolator.getInterpolation(Math.max(Math.min(fraction, 1.0f), 0.0f));
        } else {
            long l = time / mCycle / 2;
            if (l == 0) {
                // 正向
                float fraction = time % mCycle / (float) mCycle;
                return mInterpolator.getInterpolation(Math.max(Math.min(fraction, 1.0f), 0.0f));
            } else {
                // 逆向
                float fraction = (mCycle - time % mCycle) / (float) mCycle;
                return mInterpolator.getInterpolation(Math.max(Math.min(fraction, 1.0f), 0.0f));
            }
        }
    }
}
