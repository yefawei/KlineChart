package com.yfw.kchartcore.drawing;

import androidx.annotation.Nullable;

import com.yfw.kchartcore.canvas.RendererCanvas;
import com.yfw.kchartcore.impl.IDataProvider;
import com.yfw.kchartcore.index.range.IndexRange;
import com.yfw.kchartcore.layout.IParentPortLayout;

/**
 * @日期 : 2020/7/10
 * @描述 : 自动执行动画的绘制，适合持续性的动画
 */
public abstract class AutoAnimDrawing<T extends IndexRange> extends AbstractAnimDrawing<T>{

    public AutoAnimDrawing() {
    }

    public AutoAnimDrawing(RendererCanvas.DrawingLayoutParams params) {
        super(params);
    }

    public AutoAnimDrawing(@Nullable T indexRange) {
        super(indexRange);
    }

    public AutoAnimDrawing(@Nullable T indexRange, RendererCanvas.DrawingLayoutParams params) {
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
    public final boolean inAnimTime() {
        return true;
    }
}
