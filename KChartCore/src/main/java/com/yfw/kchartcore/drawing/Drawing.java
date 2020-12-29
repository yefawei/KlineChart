package com.yfw.kchartcore.drawing;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;

import androidx.annotation.CallSuper;

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
public abstract class Drawing<T extends IEntity> implements IDrawing<T>, IViewPort, IDispatchSingleTapChild {

    String mTag = "";

    private RendererCanvas.DrawingLayoutParams mLayoutParams;
    private int mWidth;
    private int mHeight;
    protected final Rect mViewPort = new Rect();
    private final CenterPoint mCenterPoint = new CenterPoint();
    private float mScaleValueY;         // Y轴缩放值
    private boolean mDrawInViewPort = true;
    private IParentPortLayout mDrawingPortLayout;

    protected IDataProvider<T> mDataProvider;

    public Drawing() {
    }

    public Drawing(RendererCanvas.DrawingLayoutParams params) {
        if (params == null) {
            throw new NullPointerException("DrawingLayoutParams cannot be null!");
        }
        mLayoutParams = params;
    }

    public final void setLayoutParams(RendererCanvas.DrawingLayoutParams params) {
        if (params == null) {
            throw new NullPointerException("DrawingLayoutParams cannot be null!");
        }
        mLayoutParams = params;
    }

    public final RendererCanvas.DrawingLayoutParams getLayoutParams() {
        return mLayoutParams;
    }

    @CallSuper
    @Override
    public void attachedParentPortLayout(IParentPortLayout portLayout, IDataProvider<T> dataProvider) {
        mDrawingPortLayout = portLayout;
        mDataProvider = dataProvider;
        IndexRange indexRange = getIndexRange();
        if (indexRange != null) {
            mDataProvider.getTransformer().addIndexRange(getIndexRangeGroupId(), indexRange);
        }
    }

    @CallSuper
    @Override
    public void detachedParentPortLayout() {
        IndexRange indexRange = getIndexRange();
        if (indexRange != null) {
            mDataProvider.getTransformer().removeIndexRange(getIndexRangeGroupId(), indexRange);
        }
        mDrawingPortLayout = null;
        mDataProvider = null;
        mWidth = 0;
        mHeight = 0;
        mViewPort.setEmpty();
    }

    @Override
    public final boolean isAttachedParentPortLayout() {
        return mDataProvider != null;
    }

    @Override
    public void setTag(String tag) {
        mTag = tag;
    }

    @Override
    public String getTag() {
        return mTag == null ? "" : mTag;
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
    public int getIndexRangeGroupId() {
        return IndexRange.NO_GROUP;
    }

    @Override
    public IndexRange getIndexRange() {
        return null;
    }

    @Override
    public void preCalcDataValue(boolean emptyBounds) {
        IndexRange indexRange = getIndexRange();
        if (!emptyBounds && indexRange != null) {
            mScaleValueY = mHeight / (indexRange.getMaxValue() - indexRange.getMinValue());
        }
    }

    /**
     * 根据数值获取当前所在Y坐标
     */
    protected final float getCoordinateY(float value) {
        IndexRange indexRange = getIndexRange();
        if (indexRange == null) return -1;
        if (indexRange.isReverse()) {
            return mViewPort.top + (value - indexRange.getMinValue()) * mScaleValueY;
        } else {
            return mViewPort.bottom - (value - indexRange.getMinValue()) * mScaleValueY;
        }
    }

    /**
     * 根据Y坐标获取当前数值
     */
    protected final float getValueByCoordinateY(float coordinateY) {
        IndexRange indexRange = getIndexRange();
        if (indexRange == null || mHeight == 0) return -1;
        final float p;
        if (indexRange.isReverse()) {
            p = (coordinateY - mViewPort.top) / mHeight;
        } else {
            p = (mViewPort.bottom - coordinateY) / mHeight;
        }
        return p * (indexRange.getMaxValue() - indexRange.getMinValue()) + indexRange.getMinValue();
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
