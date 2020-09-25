package com.benben.kchartlib.drawing;

import androidx.annotation.Nullable;

import com.benben.kchartlib.animation.Animation;
import com.benben.kchartlib.canvas.RendererCanvas;
import com.benben.kchartlib.index.range.IndexRange;

/**
 * @日期 : 2020/7/10
 * @描述 : 主动触发动画的绘制
 */
public abstract class TriggerAnimDrawing extends Drawing implements Animation {

    private boolean mInAnim;

    public TriggerAnimDrawing() {
    }

    public TriggerAnimDrawing(@Nullable IndexRange indexRange) {
        super(indexRange);
    }

    public TriggerAnimDrawing(@Nullable IndexRange indexRange, RendererCanvas.DrawingLayoutParams params) {
        super(indexRange, params);
    }

    @Override
    public void detachedParentPortLayout() {
        mDataProvider.getChartAnimation().removeAnim(this);
        super.detachedParentPortLayout();
    }

    @Override
    public final boolean isAutoAnim() {
        return false;
    }

    @Override
    public final long AnimStartTime() {
        return 0;
    }

    @Override
    public final long AnimEndTime() {
        return 0;
    }

    @Override
    public final void setInAnim(boolean inAnim) {
        mInAnim = inAnim;
    }

    @Override
    public final boolean inAnim() {
        return mInAnim;
    }
}