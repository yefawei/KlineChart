package com.benben.kchartlib.drawing;

import androidx.annotation.CallSuper;
import androidx.annotation.Nullable;

import com.benben.kchartlib.animation.Animation;
import com.benben.kchartlib.canvas.RendererCanvas;
import com.benben.kchartlib.impl.IDataProvider;
import com.benben.kchartlib.impl.IParentPortLayout;
import com.benben.kchartlib.index.range.IndexRange;

/**
 * @日期 : 2020/7/10
 * @描述 : 自动执行动画的绘制，适合持续性的动画
 */
public abstract class AutoAnimDrawing extends Drawing implements Animation {

    private boolean mInAnimationManager;
    boolean mInAnim;
    long mAnimTime;

    public AutoAnimDrawing() {
    }

    public AutoAnimDrawing(RendererCanvas.DrawingLayoutParams params) {
        super(params);
    }

    public AutoAnimDrawing(@Nullable IndexRange indexRange) {
        super(indexRange);
    }

    public AutoAnimDrawing(@Nullable IndexRange indexRange, RendererCanvas.DrawingLayoutParams params) {
        super(indexRange, params);
    }

    @Override
    public void attachedParentPortLayout(IParentPortLayout portLayout, IDataProvider dataProvider) {
        super.attachedParentPortLayout(portLayout, dataProvider);
        mDataProvider.getChartAnimation().addAnim(this);
    }

    @Override
    public void detachedParentPortLayout() {
        mDataProvider.getChartAnimation().removeAnim(this);
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
        return true;
    }

    @Override
    public final long getAnimStartTime() {
        return 0;
    }

    @Override
    public final long getAnimEndTime() {
        return Long.MAX_VALUE;
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
        return true;
    }

    @Override
    public final void updateAnimProcessTime(long time) {
        mAnimTime = time;
    }
}
