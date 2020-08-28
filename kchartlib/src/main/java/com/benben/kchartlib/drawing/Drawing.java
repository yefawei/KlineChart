package com.benben.kchartlib.drawing;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;

import androidx.annotation.CallSuper;
import androidx.annotation.Nullable;

import com.benben.kchartlib.canvas.RendererCanvas;
import com.benben.kchartlib.impl.IDataProvider;
import com.benben.kchartlib.impl.IDispatchSingleTapChild;
import com.benben.kchartlib.impl.IParentPortLayout;
import com.benben.kchartlib.impl.IViewPort;
import com.benben.kchartlib.index.range.IndexRange;

/**
 * @日期 : 2020/7/10
 * @描述 : 普通绘制
 */
public abstract class Drawing implements IDrawing, IViewPort, IDispatchSingleTapChild {

    protected IndexRange mIndexRange;
    private RendererCanvas.DrawingLayoutParams mLayoutParams;
    private int mWidth;
    private int mHeight;
    protected Rect mViewPort = new Rect();
    private float mScaleValueY;         // Y轴缩放值
    private boolean mDrawInViewPort = true;
    private IParentPortLayout mDrawingPortLayout;

    protected IDataProvider mDataProvider;

    public Drawing() {
    }

    public Drawing(RendererCanvas.DrawingLayoutParams params) {
        if (params == null) {
            throw new NullPointerException("DrawingLayoutParams cannot be null!");
        }
        mLayoutParams = params;
    }

    public Drawing(@Nullable IndexRange indexRange) {
        mIndexRange = indexRange;
    }

    public Drawing(@Nullable IndexRange indexRange, RendererCanvas.DrawingLayoutParams params) {
        mIndexRange = indexRange;
        if (params == null) {
            throw new NullPointerException("DrawingLayoutParams cannot be null!");
        }
        mLayoutParams = params;
    }

    public void setLayoutParams(RendererCanvas.DrawingLayoutParams params) {
        if (params == null) {
            throw new NullPointerException("DrawingLayoutParams cannot be null!");
        }
        mLayoutParams = params;
    }

    public RendererCanvas.DrawingLayoutParams getLayoutParams() {
        return mLayoutParams;
    }

    @CallSuper
    @Override
    public void attachedParentPortLayout(IParentPortLayout portLayout, IDataProvider dataProvider) {
        mDrawingPortLayout = portLayout;
        mDataProvider = dataProvider;
        if (mIndexRange != null) {
            mDataProvider.getTransformer().addIndexRange(mIndexRange);
        }
    }

    @CallSuper
    @Override
    public void detachedParentPortLayout() {
        if (mIndexRange != null) {
            mDataProvider.getTransformer().removeIndexRange(mIndexRange);
        }
        mDrawingPortLayout = null;
        mDataProvider = null;
        mWidth = 0;
        mHeight = 0;
        mViewPort.setEmpty();
    }

    @Override
    public void setWidth(int width) {
        if (mDrawingPortLayout != null && mDrawingPortLayout.inUpdateChildLayout()) {
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
        if (mDrawingPortLayout != null && mDrawingPortLayout.inUpdateChildLayout()) {
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
        if (mDrawingPortLayout != null && mDrawingPortLayout.inUpdateChildLayout()) {
            mViewPort.set(left, top, right, bottom);
        } else {
            Log.w("Drawing", "Setting port is not allowed.");
        }
    }

    @Override
    public String toViewPortString() {
        return "----------------" + this.getClass().getSimpleName() +
                "--ViewPort: " +
                "[width: " + getWidth() + ", height: " + getHeight() + "]" +
                "[left: " + getLeft() + "]" +
                "[top: " + getTop() + "]" +
                "[right: " + getRight() + "]" +
                "[bottom: " + getBottom() + "]";
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
    public boolean isValid() {
        return !mViewPort.isEmpty();
    }

    @Override
    public IndexRange getIndexRange() {
        return mIndexRange;
    }

    @CallSuper
    @Override
    public void preCalcDataValue() {
        if (mIndexRange != null) {
            float maxValue = mIndexRange.getMaxValue();
            float minValue = mIndexRange.getMinValue();
            mScaleValueY = mHeight * 1.0f / (maxValue - minValue);
        }
    }

    protected final float getCoordinateY(float value) {
        if (mIndexRange == null) return -1;
        if (mIndexRange.isReverse()) {
            return mViewPort.top + (value - mIndexRange.getMinValue()) * mScaleValueY;
        } else {
            return mViewPort.bottom - (value - mIndexRange.getMinValue()) * mScaleValueY;
        }
    }

    @Override
    public boolean canSingleTap() {
        return false;
    }

    @Override
    public boolean inDispatchRange(int x, int y) {
        return mViewPort.contains(x, y);
    }

    @Override
    public boolean onSingleTap(int x, int y) {
        return false;
    }

    @Override
    public void drawEmpty(Canvas canvas) {

    }

    @Override
    public void drawData(Canvas canvas) {

    }
}
