package com.benben.kchartlib.drawing;

import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import androidx.annotation.CallSuper;
import androidx.annotation.Nullable;

import com.benben.kchartlib.animation.Animation;
import com.benben.kchartlib.canvas.RendererCanvas;
import com.benben.kchartlib.index.range.IndexRange;

/**
 * @日期 : 2020/9/27
 * @描述 : 抽象动画绘制类，方便子类的实现
 */
public abstract class AbstractAnimDrawing<T extends IndexRange> extends Drawing<T> implements Animation {

    public final static int RESTART = 1;    // 重新开始
    public final static int REVERSE = 2;    // 逆转
    private final static Interpolator sDefaultInterpolator = new LinearInterpolator();

    boolean mInAnimationManager;
    boolean mInAnim;

    protected long mAnimProcessTime;
    protected Interpolator mInterpolator = sDefaultInterpolator;

    protected int mRepeatMode = RESTART;

    public AbstractAnimDrawing() {
    }

    public AbstractAnimDrawing(RendererCanvas.DrawingLayoutParams params) {
        super(params);
    }

    public AbstractAnimDrawing(@Nullable T indexRange) {
        super(indexRange);
    }

    public AbstractAnimDrawing(@Nullable T indexRange, RendererCanvas.DrawingLayoutParams params) {
        super(indexRange, params);
    }

    @Override
    public boolean inAnimationManager() {
        return mInAnimationManager;
    }

    @Override
    @CallSuper
    public void callInAnimation(boolean in) {
        mInAnimationManager = in;
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
    @CallSuper
    public void updateAnimProcessTime(long time) {
        mAnimProcessTime = time;
    }

    public float getAnimProcessTime() {
        return mAnimProcessTime;
    }

    public void setInterpolator(Interpolator i) {
        if (i == null) {
            mInterpolator = sDefaultInterpolator;
        } else {
            mInterpolator = i;
        }
    }

    public Interpolator getInterpolator() {
        return mInterpolator;
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
}
