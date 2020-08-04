package com.benben.kchartlib.render;

import android.graphics.Rect;

import androidx.annotation.CallSuper;

import com.benben.kchartlib.impl.ICanvasPortLayout;
import com.benben.kchartlib.impl.IDataProvider;
import com.benben.kchartlib.impl.IViewPort;

/**
 * @日期 : 2020/7/1
 * @描述 : 渲染类
 */
public abstract class Renderer implements IRenderer, ICanvasPortLayout, IViewPort {

    protected IDataProvider mDataProvider;

    protected Rect mViewPort = new Rect();
    private boolean mInUpdateCanvasPortLayout;

    public Renderer(IDataProvider dataProvider) {
        mDataProvider = dataProvider;
    }

    @Override
    public void setInUpdateCanvasPortLayout(boolean inUpdate) {
        mInUpdateCanvasPortLayout = inUpdate;
    }

    @Override
    public boolean inUpdateCanvasPortLayout() {
        return mInUpdateCanvasPortLayout;
    }

    @Override
    public int getLeft() {
        return mViewPort.left;
    }

    @Override
    public int getTop() {
        return mViewPort.top;
    }

    @Override
    public int getRight() {
        return mViewPort.right;
    }

    @Override
    public int getBottom() {
        return mViewPort.bottom;
    }

    @Override
    public int getWidth() {
        return mViewPort.width();
    }

    @Override
    public int getHeight() {
        return mViewPort.height();
    }

    @CallSuper
    @Override
    public void updateViewPort(int left, int top, int right, int bottom) {
        mViewPort.set(left, top, right, bottom);
    }

    @Override
    public String toViewPortString() {
        return this.getClass().getSimpleName() +
                "--ViewPort: " +
                "[width: " + getWidth() + ", height: " + getHeight() + "]" +
                "[left: " + getLeft() + "]" +
                "[top: " + getTop() + "]" +
                "[right: " + getRight() + "]" +
                "[bottom: " + getBottom() + "]";
    }


}
