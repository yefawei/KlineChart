package com.benben.kchartlib.drawing;

import androidx.annotation.Nullable;

import com.benben.kchartlib.animation.Animation;
import com.benben.kchartlib.impl.IDataProvider;
import com.benben.kchartlib.impl.IDrawingPortLayout;
import com.benben.kchartlib.index.Index;

/**
 * @日期 : 2020/7/10
 * @描述 : 自动执行动画的绘制
 */
public abstract class AutoAnimDrawing extends Drawing implements Animation {

    public AutoAnimDrawing(@Nullable Index index) {
        super(index);
    }

    public AutoAnimDrawing(@Nullable Index index, IDrawingPortLayout.DrawingLayoutParams params) {
        super(index, params);
    }

    @Override
    public void attachedDrawingPortLayout(IDrawingPortLayout portLayout, IDataProvider dataProvider) {
        super.attachedDrawingPortLayout(portLayout, dataProvider);
        mDataProvider.getChartAnimation().addAnim(this);
    }

    @Override
    public void detachedDrawingPortLayout() {
        mDataProvider.getChartAnimation().removeAnim(this);
        super.detachedDrawingPortLayout();
    }

    @Override
    public final boolean isAutoAnim() {
        return true;
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
