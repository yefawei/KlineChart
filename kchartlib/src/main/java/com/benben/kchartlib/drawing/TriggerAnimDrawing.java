package com.benben.kchartlib.drawing;

import androidx.annotation.Nullable;

import com.benben.kchartlib.animation.Animation;
import com.benben.kchartlib.impl.IDrawingPortLayout;
import com.benben.kchartlib.index.Index;

/**
 * @日期 : 2020/7/10
 * @描述 : 主动触发动画的绘制
 */
public abstract class TriggerAnimDrawing extends Drawing implements Animation {

    public TriggerAnimDrawing(@Nullable Index index) {
        super(index);
    }

    public TriggerAnimDrawing(@Nullable Index index, IDrawingPortLayout.DrawingLayoutParams params) {
        super(index, params);
    }

    @Override
    public final boolean isAutoAnim() {
        return false;
    }

    @Override
    public long AnimEndTime() {
        return 0;
    }

    @Override
    public final void setInAnim(boolean inAnim) {

    }

    @Override
    public final boolean inAnim() {
        return false;
    }
}