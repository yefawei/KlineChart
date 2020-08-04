package com.benben.kchartlib.drawing;

import android.graphics.Rect;
import android.util.Log;

import com.benben.kchartlib.impl.IDataProvider;
import com.benben.kchartlib.impl.IDrawingPortLayout;
import com.benben.kchartlib.impl.IViewPort;

/**
 * @日期 : 2020/7/10
 * @描述 : 普通绘制
 */
public abstract class Drawing implements IDrawing, IViewPort {

    private IDrawingPortLayout.DrawingLayoutParams mLayoutParams;
    private int mWidth;
    private int mHeight;
    protected Rect mViewPort = new Rect();
    private boolean mDrawInViewPort = true;
    private IDrawingPortLayout mDrawingPortLayout;

    protected IDataProvider mDataProvider;

    public Drawing() {
    }

    public Drawing(IDrawingPortLayout.DrawingLayoutParams params) {
        if (params == null) {
            throw new NullPointerException("DrawingLayoutParams cannot be null!");
        }
        mLayoutParams = params;
    }

    public void setLayoutParams(IDrawingPortLayout.DrawingLayoutParams params) {
        if (params == null) {
            throw new NullPointerException("DrawingLayoutParams cannot be null!");
        }
        mLayoutParams = params;
    }

    public IDrawingPortLayout.DrawingLayoutParams getLayoutParams() {
        return mLayoutParams;
    }

    @Override
    public void attachedDrawingPortLayout(IDrawingPortLayout portLayout, IDataProvider dataProvider) {
        mDrawingPortLayout = portLayout;
        mDataProvider = dataProvider;
    }

    @Override
    public void detachedDrawingPortLayout() {
        mDrawingPortLayout = null;
        mDataProvider = null;
        mWidth = 0;
        mHeight = 0;
        mViewPort.setEmpty();
    }

    @Override
    public void setWidth(int width) {
        if (mDrawingPortLayout != null && mDrawingPortLayout.inUpdateDrawingPortLayout()) {
            mWidth = width;
        } else {
            Log.w("Drawing", "Setting width is not allowed.");
        }
    }

    @Override
    public int getWidth() {
        return mWidth;
    }

    @Override
    public void setHeight(int height) {
        if (mDrawingPortLayout != null && mDrawingPortLayout.inUpdateDrawingPortLayout()) {
            mHeight = height;
        } else {
            Log.w("Drawing", "Setting width is not allowed.");
        }
    }

    @Override
    public int getHeight() {
        return mHeight;
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
    public void updateViewPort(int left, int top, int right, int bottom) {
        if (mDrawingPortLayout != null && mDrawingPortLayout.inUpdateDrawingPortLayout()) {
            mViewPort.set(left, top, right, bottom);
        } else {
            Log.w("Drawing", "Setting port is not allowed.");
        }
    }

    @Override
    public boolean drawInViewPort() {
        return mDrawInViewPort;
    }

    @Override
    public void setDrawInViewPort(boolean in) {
        mDrawInViewPort = in;
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

    @Override
    public boolean isValid() {
        return !mViewPort.isEmpty();
    }
}
