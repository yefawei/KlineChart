package com.benben.kchartlib.render;

import android.graphics.Canvas;

import com.benben.kchartlib.canvas.RendererCanvas;
import com.benben.kchartlib.impl.IDataProvider;

/**
 * @日期 : 2020/7/13
 * @描述 : 背景层
 */
public class BackgroundRenderer extends Renderer {

    private RendererCanvas mRenderCanvas;

    public BackgroundRenderer(IDataProvider dataProvider) {
        super(dataProvider);
    }

    @Override
    public void updatePortLayout() {
        if (mRenderCanvas == null) return;
        setInUpdateCanvasPortLayout(true);
        if (mViewPort.isEmpty()) {
            mRenderCanvas.setWidth(0);
            mRenderCanvas.setHeight(0);
            mRenderCanvas.updateViewPort(0, 0, 0, 0);
        } else {
            mRenderCanvas.setWidth(mViewPort.width());
            mRenderCanvas.setHeight(mViewPort.height());
            mRenderCanvas.updateViewPort(mViewPort.left, mViewPort.top, mViewPort.right, mViewPort.bottom);
        }
        setInUpdateCanvasPortLayout(false);
        mRenderCanvas.updatePortLayout();
    }

    @Override
    public void preCalcDataValue() {

    }

    @Override
    public void render(Canvas canvas) {
        if (mRenderCanvas == null || !mRenderCanvas.isValid()) return;
        canvas.save();
        mRenderCanvas.render(canvas);
        canvas.restore();
    }

    public void setRenderCanvas(RendererCanvas canvas) {
        if (mRenderCanvas != null) {
            mRenderCanvas.detachedCanvasPortLayout();
        }
        if (canvas != null) {
            canvas.attachedCanvasPortLayout(this, mDataProvider);
            if (canvas.getLayoutParams() == null) {
                canvas.setLayoutParams(new CanvasLayoutParams(0, 0));
            }
        }
        mRenderCanvas = canvas;
    }
}
