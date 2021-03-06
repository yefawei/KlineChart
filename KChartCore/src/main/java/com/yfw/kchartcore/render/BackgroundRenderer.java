package com.yfw.kchartcore.render;

import android.graphics.Canvas;

import com.yfw.kchartcore.canvas.RendererCanvas;
import com.yfw.kchartcore.impl.IDataProvider;
import com.yfw.kchartcore.index.IEntity;


/**
 * @日期 : 2020/7/13
 * @描述 : 背景层
 */
public class BackgroundRenderer<T extends IEntity> extends Renderer<T> {

    private RendererCanvas<T> mRenderCanvas;

    public BackgroundRenderer(IDataProvider<T> dataProvider) {
        super(dataProvider);
    }

    @Override
    public void updateChildLayout() {
        if (mRenderCanvas == null) return;
        setInUpdateChildLayout(true);
        if (mViewPort.isEmpty()) {
            mRenderCanvas.setWidth(0);
            mRenderCanvas.setHeight(0);
            mRenderCanvas.updateViewPort(0, 0, 0, 0);
        } else {
            mRenderCanvas.setWidth(mViewPort.width());
            mRenderCanvas.setHeight(mViewPort.height());
            mRenderCanvas.updateViewPort(mViewPort.left, mViewPort.top, mViewPort.right, mViewPort.bottom);
        }
        setInUpdateChildLayout(false);
        mRenderCanvas.updateChildLayout();
    }

    @Override
    public boolean inDispatchRange(int x, int y) {
        return mViewPort.contains(x, y);
    }

    @Override
    public boolean dispatchSingleTap(int x, int y) {
        return mRenderCanvas.inDispatchRange(x, y) && mRenderCanvas.dispatchSingleTap(x, y);
    }

    @Override
    public void preCalcDataValue(boolean emptyBounds) {
        mRenderCanvas.preCalcDataValue(emptyBounds);
    }

    @Override
    public void render(Canvas canvas) {
        if (mRenderCanvas == null || !mRenderCanvas.isValid()) return;
        canvas.save();
        mRenderCanvas.render(canvas);
        canvas.restore();
    }

    @Override
    public String toViewPortString() {
        return super.toViewPortString() +
                "\n--------" + mRenderCanvas.toViewPortString() + "\n";
    }

    public void setRenderCanvas(RendererCanvas<T> canvas) {
        if (mRenderCanvas != null) {
            mRenderCanvas.detachedParentPortLayout();
        }
        if (canvas != null) {
            canvas.attachedParentPortLayout(this, mDataProvider);
        }
        mRenderCanvas = canvas;
    }

    public RendererCanvas<T> getRenderCanvas() {
        return mRenderCanvas;
    }
}
