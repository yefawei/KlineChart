package com.yfw.kchartcore.drawing;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;

import androidx.annotation.CallSuper;
import androidx.annotation.Nullable;

import com.yfw.kchartcore.canvas.RendererCanvas;
import com.yfw.kchartcore.impl.IDataProvider;
import com.yfw.kchartcore.index.IEntity;
import com.yfw.kchartcore.index.range.IndexRange;
import com.yfw.kchartcore.layout.CenterPoint;
import com.yfw.kchartcore.layout.IDispatchSingleTapChild;
import com.yfw.kchartcore.layout.IParentPortLayout;
import com.yfw.kchartcore.layout.IViewPort;

/**
 * @日期 : 2020/7/10
 * @描述 : 普通绘制
 */
public abstract class Drawing<T extends IndexRange, S extends IEntity> implements IDrawing<S>, IViewPort, IDispatchSingleTapChild {

    protected T mIndexRange;
    private RendererCanvas.DrawingLayoutParams mLayoutParams;
    private int mWidth;
    private int mHeight;
    protected final Rect mViewPort = new Rect();
    private final CenterPoint mCenterPoint = new CenterPoint();
    private float mScaleValueY;         // Y轴缩放值
    private boolean mDrawInViewPort = true;
    private IParentPortLayout mDrawingPortLayout;

    protected IDataProvider<S> mDataProvider;

    public Drawing() {
    }

    public Drawing(RendererCanvas.DrawingLayoutParams params) {
        if (params == null) {
            throw new NullPointerException("DrawingLayoutParams cannot be null!");
        }
        mLayoutParams = params;
    }

    public Drawing(@Nullable T indexRange) {
        mIndexRange = indexRange;
    }

    public Drawing(@Nullable T indexRange, RendererCanvas.DrawingLayoutParams params) {
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
    public void attachedParentPortLayout(IParentPortLayout portLayout, IDataProvider<S> dataProvider) {
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
    public boolean isAttachedParentPortLayout() {
        return mDataProvider != null;
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
    public CenterPoint getCenter() {
        return mCenterPoint;
    }

    @Override
    @CallSuper
    public void updateViewPort(int left, int top, int right, int bottom) {
        if (mDrawingPortLayout != null && mDrawingPortLayout.inUpdateChildLayout()) {
            mViewPort.set(left, top, right, bottom);
            mCenterPoint.setCenterX(mViewPort.exactCenterX());
            mCenterPoint.setCenterY(mViewPort.exactCenterY());
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

    @Override
    public void preCalcDataValue(boolean emptyBounds) {
        if (!emptyBounds && mIndexRange != null) {
            mScaleValueY = mHeight / (mIndexRange.getMaxValue() - mIndexRange.getMinValue());
        }
    }

    /**
     * 根据数值获取当前所在Y坐标
     */
    protected final float getCoordinateY(float value) {
        if (mIndexRange == null) return -1;
        if (mIndexRange.isReverse()) {
            return mViewPort.top + (value - mIndexRange.getMinValue()) * mScaleValueY;
        } else {
            return mViewPort.bottom - (value - mIndexRange.getMinValue()) * mScaleValueY;
        }
    }

    /**
     * 根据Y坐标获取当前数值
     */
    protected final float getValueByCoordinateY(float coordinateY) {
        if (mIndexRange == null || mHeight == 0) return -1;
        final float p;
        if (mIndexRange.isReverse()) {
             p = (coordinateY - mViewPort.top) / mHeight;
        } else {
             p = (mViewPort.bottom - coordinateY) / mHeight;
        }
        return p * (mIndexRange.getMaxValue() - mIndexRange.getMinValue()) + mIndexRange.getMinValue();
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
