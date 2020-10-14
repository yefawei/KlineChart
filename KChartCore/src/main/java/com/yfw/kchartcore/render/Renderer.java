package com.yfw.kchartcore.render;

import android.graphics.Rect;

import androidx.annotation.CallSuper;

import com.yfw.kchartcore.impl.IDataProvider;
import com.yfw.kchartcore.index.IEntity;
import com.yfw.kchartcore.layout.CenterPoint;
import com.yfw.kchartcore.layout.IDispatchSingleTapParent;
import com.yfw.kchartcore.layout.IParentPortLayout;
import com.yfw.kchartcore.layout.IViewPort;


/**
 * @日期 : 2020/7/1
 * @描述 : 渲染类
 */
public abstract class Renderer<T extends IEntity> implements IRenderer, IParentPortLayout, IViewPort, IDispatchSingleTapParent {

    protected final IDataProvider<T> mDataProvider;

    protected final Rect mViewPort = new Rect();
    private final CenterPoint mCenterPoint = new CenterPoint();
    private boolean mInUpdateChildLayout;

    public Renderer(IDataProvider<T> dataProvider) {
        mDataProvider = dataProvider;
    }

    @Override
    public void setInUpdateChildLayout(boolean inUpdate) {
        mInUpdateChildLayout = inUpdate;
    }

    @Override
    public boolean inUpdateChildLayout() {
        return mInUpdateChildLayout;
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

    @Override
    public CenterPoint getCenter() {
        return mCenterPoint;
    }

    @CallSuper
    @Override
    public void updateViewPort(int left, int top, int right, int bottom) {
        mViewPort.set(left, top, right, bottom);
        mCenterPoint.setCenterX(mViewPort.exactCenterX());
        mCenterPoint.setCenterY(mViewPort.exactCenterY());
    }

    @Override
    public String toViewPortString() {
        return "ViewPortString\n" + this.getClass().getSimpleName() +
                "--ViewPort: " +
                "[width: " + getWidth() + ", height: " + getHeight() + "]" +
                "[left: " + getLeft() + "]" +
                "[top: " + getTop() + "]" +
                "[right: " + getRight() + "]" +
                "[bottom: " + getBottom() + "]";
    }


}
