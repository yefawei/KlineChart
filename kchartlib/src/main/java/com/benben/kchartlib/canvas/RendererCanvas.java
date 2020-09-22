package com.benben.kchartlib.canvas;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;

import androidx.annotation.CallSuper;
import androidx.annotation.IntDef;

import com.benben.kchartlib.adapter.BaseKChartAdapter;
import com.benben.kchartlib.drawing.Drawing;
import com.benben.kchartlib.impl.IDataProvider;
import com.benben.kchartlib.impl.IDispatchSingleTapParent;
import com.benben.kchartlib.impl.IParentPortLayout;
import com.benben.kchartlib.impl.IViewPort;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;

/**
 * @日期 : 2020/7/1
 * @描述 : 画板
 */
public class RendererCanvas implements IRendererCanvas, IParentPortLayout, IViewPort, IDispatchSingleTapParent {

    private int mWidth;
    private int mHeight;
    protected Rect mViewPort = new Rect();
    private boolean mInUpdateChildLayout;
    private IParentPortLayout mParentPortLayout;

    protected IDataProvider mDataProvider;

    private ArrayList<Drawing> mDrawings = new ArrayList<>();
    private ArrayList<Drawing> mHorizontalLinearDrawings = new ArrayList<>();
    private ArrayList<Drawing> mVerticalLinearDrawings = new ArrayList<>();

    public RendererCanvas() {
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
    public void updateChildLayout() {
        setInUpdateChildLayout(true);
        if (mViewPort.isEmpty()) {
            for (Drawing drawing : mDrawings) {
                drawing.setWidth(0);
                drawing.setHeight(0);
                drawing.updateViewPort(0, 0, 0, 0);
            }
            setInUpdateChildLayout(false);
            return;
        }

        mHorizontalLinearDrawings.clear();
        mVerticalLinearDrawings.clear();
        for (Drawing drawing : mDrawings) {
            if (drawing.getLayoutParams().mIsHorizontalLinear) {
                mHorizontalLinearDrawings.add(drawing);
            }
            if (drawing.getLayoutParams().mIsVerticalLinear) {
                mVerticalLinearDrawings.add(drawing);
            }
        }

        final int w = this.getWidth();
        final int h = this.getHeight();
        int width = this.getWidth();
        int height = this.getHeight();

        int weightUnitWidth = 0;
        int[] horizontalWeightAndWidth = getHorizontalWeightAndWidth();
        if (horizontalWeightAndWidth[0] != 0) {
            int remainingWidth = width - horizontalWeightAndWidth[1];
            if (remainingWidth > 0) {
                // 说明固定宽度没有超出总宽度
                weightUnitWidth = (int) (remainingWidth * 1.0f / horizontalWeightAndWidth[0]);
            }
        }
        int weightUnitHeight = 0;
        int[] verticalWeightAndHeight = getVerticalWeightAndHeight();
        if (verticalWeightAndHeight[0] != 0) {
            int remainingHeight = height - verticalWeightAndHeight[1];
            if (remainingHeight > 0) {
                // 说明固定高度没有超出总宽度
                weightUnitHeight = (int) (remainingHeight * 1.0f / verticalWeightAndHeight[0]);
            }
        }
        int tempValue;
        DrawingLayoutParams params;
        // 计算出水平方向线性布局宽度
        for (Drawing drawing : mHorizontalLinearDrawings) {
            params = drawing.getLayoutParams();
            if (params.mWidth > 0) {
                tempValue = params.mWidth;
            } else if (params.mHorizontalPercent > 0) {
                tempValue = Math.round(w * params.mHorizontalPercent);
            } else if (params.mWeight > 0) {
                tempValue = params.mWeight * weightUnitWidth;
            } else {
                tempValue = 0;
            }
            if (tempValue > 0) {
                if (tempValue > width) {
                    drawing.setWidth(width);
                    width = 0;
                } else {
                    drawing.setWidth(tempValue);
                    width -= tempValue;
                }
            }
        }
        if (width > 0 && weightUnitWidth > 0) {
            int tw = 0;
            // 说明按比例宽度没有填满视图
            for (Drawing drawing : mHorizontalLinearDrawings) {
                params = drawing.getLayoutParams();
                if (params.mWidth > 0 || params.mHorizontalPercent > 0) continue;
                if (params.mWeight > 0) {
                    tempValue = Math.round(width * 1.0f / horizontalWeightAndWidth[0] * params.mWeight + 0.5f);
                    if (tempValue + tw > width) {
                        tempValue = width - tw;
                    }
                    tw += tempValue;
                    Log.w("RendererCanvas", "Horizontal layout with gaps. " + drawing.getClass().getSimpleName() + " ext width: " + tempValue);
                    drawing.setWidth(drawing.getWidth() + tempValue);
                }
                if (width == tw) break;
            }
        }
        // 计算出垂直方向线性布局高度
        for (Drawing drawing : mVerticalLinearDrawings) {
            params = drawing.getLayoutParams();
            if (params.mHeight > 0) {
                tempValue = params.mHeight;
            } else if (params.mVerticalPercent > 0) {
                tempValue = Math.round(h * params.mVerticalPercent);
            } else if (params.mWeight > 0) {
                tempValue = params.mWeight * weightUnitHeight;
            } else {
                tempValue = 0;
            }
            if (tempValue > 0) {
                if (tempValue > height) {
                    drawing.setHeight(height);
                    height = 0;
                } else {
                    drawing.setHeight(tempValue);
                    height -= tempValue;
                }
            }
        }
        if (height > 0 && weightUnitHeight > 0) {
            int th = 0;
            // 说明按比例高度没有填满视图
            for (Drawing drawing : mVerticalLinearDrawings) {
                params = drawing.getLayoutParams();
                if (params.mHeight > 0 || params.mVerticalPercent > 0) continue;
                if (params.mWeight > 0) {
                    tempValue = Math.round(width * 1.0f / horizontalWeightAndWidth[0] * params.mWeight + 0.5f);
                    if (tempValue + th > height) {
                        tempValue = height - th;
                    }
                    th += tempValue;
                    Log.w("RendererCanvas", "Vertical layout with gaps. " + drawing.getClass().getSimpleName() + " ext height: " + tempValue);
                    drawing.setHeight(drawing.getHeight() + tempValue);
                }
                if (height == th) break;
            }
        }

        //计算非线性布局宽高
        for (Drawing drawing : mDrawings) {
            params = drawing.getLayoutParams();
            if (!params.mIsHorizontalLinear) {
                if (params.mWidth > 0) {
                    tempValue = params.mWidth;
                } else if (params.mHorizontalPercent > 0) {
                    tempValue = Math.round(w * params.mHorizontalPercent);
                } else if (params.mWeight > 0) {
                    tempValue = w;
                } else {
                    tempValue = 0;
                }
                if (tempValue > 0) {
                    drawing.setWidth(Math.min(tempValue, w));
                }
            }
            if (!params.mIsVerticalLinear) {
                if (params.mHeight > 0) {
                    tempValue = params.mHeight;
                } else if (params.mVerticalPercent > 0) {
                    tempValue = Math.round(h * params.mVerticalPercent);
                } else if (params.mWeight > 0) {
                    tempValue = h;
                } else {
                    tempValue = 0;
                }
                if (tempValue > 0) {
                    drawing.setHeight(Math.min(tempValue, h));
                }
            }
        }

        int startX = getLeft();
        int startY = getTop();
        int x;
        int y;
        // 最终确认布局
        for (Drawing drawing : mDrawings) {
            params = drawing.getLayoutParams();
            if (params.mIsHorizontalLinear) {
                x = startX;
                startX += drawing.getWidth();
            } else if (params.mHorizontalPosition == DrawingLayoutParams.POSITION_CENTER) {
                x = getLeft() + (getWidth() - drawing.getWidth()) / 2;
            } else if (params.mHorizontalPosition == DrawingLayoutParams.POSITION_RIGHT) {
                x = getRight() - drawing.getWidth();
            } else {
                x = getLeft();
            }
            if (params.mIsVerticalLinear) {
                y = startY;
                startY += drawing.getHeight();
            } else if (params.mVerticalPosition == DrawingLayoutParams.POSITION_CENTER) {
                y = getTop() + (getHeight() - drawing.getHeight()) / 2;
            } else if (params.mVerticalPosition == DrawingLayoutParams.POSITION_BOTTOM) {
                y = getBottom() - drawing.getHeight();
            } else {
                y = getTop();
            }
            drawing.updateViewPort(x, y, x + drawing.getWidth(), y + drawing.getHeight());
        }

        setInUpdateChildLayout(false);
    }

    /**
     * @return [0]:all weight [1]:all width
     */
    private int[] getHorizontalWeightAndWidth() {
        int[] i = new int[2];
        for (Drawing drawing : mHorizontalLinearDrawings) {
            DrawingLayoutParams params = drawing.getLayoutParams();
            if (params.mWidth > 0) {
                i[1] += params.mWidth;
            } else if (params.mHorizontalPercent > 0) {
                i[1] += Math.round(this.getWidth() * params.mHorizontalPercent);
            } else if (params.mWeight > 0) {
                i[0] += params.mWeight;
            }
        }
        return i;
    }

    /**
     * @return [0]:all weight [1]:all height
     */
    private int[] getVerticalWeightAndHeight() {
        int[] i = new int[2];
        for (Drawing drawing : mVerticalLinearDrawings) {
            DrawingLayoutParams params = drawing.getLayoutParams();
            if (params.mHeight > 0) {
                i[1] += params.mHeight;
            } else if (params.mVerticalPercent > 0) {
                i[1] += Math.round(this.getHeight() * params.mVerticalPercent);
            } else if (params.mWeight > 0) {
                i[0] += params.mWeight;
            }
        }
        return i;
    }

    @CallSuper
    @Override
    public void attachedParentPortLayout(IParentPortLayout portLayout, IDataProvider dataProvider) {
        mParentPortLayout = portLayout;
        mDataProvider = dataProvider;
        for (Drawing drawing : mDrawings) {
            drawing.attachedParentPortLayout(this, mDataProvider);
        }
    }

    @CallSuper
    @Override
    public void detachedParentPortLayout() {
        mParentPortLayout = null;
        mDataProvider = null;
        mWidth = 0;
        mHeight = 0;
        mViewPort.setEmpty();
        for (Drawing drawing : mDrawings) {
            drawing.detachedParentPortLayout();
        }
    }

    @Override
    public void setWidth(int width) {
        if (mParentPortLayout != null && mParentPortLayout.inUpdateChildLayout()) {
            mWidth = width;
        } else {
            Log.w("RendererCanvas", "Setting width is not allowed.");
        }
    }

    @Override
    public int getWidth() {
        return mWidth;
    }

    @Override
    public void setHeight(int height) {
        if (mParentPortLayout != null && mParentPortLayout.inUpdateChildLayout()) {
            mHeight = height;
        } else {
            Log.w("RendererCanvas", "Setting width is not allowed.");
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
        if (mParentPortLayout != null && mParentPortLayout.inUpdateChildLayout()) {
            mViewPort.set(left, top, right, bottom);
        } else {
            Log.w("RendererCanvas", "Setting port is not allowed.");
        }
    }

    @Override
    public String toViewPortString() {
        StringBuilder vps = new StringBuilder(this.getClass().getSimpleName() +
                "--ViewPort: " +
                "[width: " + getWidth() + ", height: " + getHeight() + "]" +
                "[left: " + getLeft() + "]" +
                "[top: " + getTop() + "]" +
                "[right: " + getRight() + "]" +
                "[bottom: " + getBottom() + "]");
        vps.append("\n");
        for (Drawing drawing : mDrawings) {
            vps.append(drawing.toViewPortString());
            vps.append("\n");
        }
        return vps.toString();
    }

    @Override
    public boolean isValid() {
        return !mViewPort.isEmpty();
    }


    @Override
    public void addDrawing(Drawing drawing) {
        addDrawing(drawing, false);
    }

    @Override
    public void addDrawing(Drawing drawing, boolean isMainIndexDrawing) {
        if (drawing == null) return;

        if (drawing.getLayoutParams() == null) {
            drawing.setLayoutParams(new DrawingLayoutParams(true));
        }
        if (isMainIndexDrawing) {
            DrawingLayoutParams layoutParams = drawing.getLayoutParams();
            if (!layoutParams.mIsDefault) {
                layoutParams.mWidth = 0;
                layoutParams.mHorizontalPercent = 0;
                layoutParams.mWeight = 1;
                layoutParams.mIsHorizontalLinear = false;
            }
        }

        mDrawings.add(drawing);
        if (mParentPortLayout != null) {
            drawing.attachedParentPortLayout(this, mDataProvider);
        }
    }

    @Override
    public void removeDrawing(Drawing drawing) {
        if (drawing == null) return;

        mDrawings.remove(drawing);
        if (mParentPortLayout != null) {
            drawing.detachedParentPortLayout();
        }
    }

    @Override
    public boolean inDispatchRange(int x, int y) {
        return mViewPort.contains(x, y);
    }

    @Override
    public boolean dispatchSingleTap(int x, int y) {
        for (int i = mDrawings.size() - 1; i >= 0; i--) {
            Drawing drawing = mDrawings.get(i);
            if (drawing.canSingleTap() && drawing.inDispatchRange(x, y) && drawing.onSingleTap(x, y)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void preCalcDataValue() {
        for (Drawing drawing : mDrawings) {
            drawing.preCalcDataValue();
        }
    }

    @Override
    public void render(Canvas canvas) {
        BaseKChartAdapter adapter = mDataProvider.getAdapter();
        if (adapter == null || adapter.getCount() == 0) {
            renderEmpty(canvas);
        } else {
            renderData(canvas);
        }
    }

    private void renderEmpty(Canvas canvas) {
        for (Drawing drawing : mDrawings) {
            if (!drawing.isValid()) continue;
            canvas.save();
            if (drawing.drawInViewPort()) {
                canvas.clipRect(drawing.getLeft(), drawing.getTop(), drawing.getRight(), drawing.getBottom());
            }
            drawing.drawEmpty(canvas);
            canvas.restore();
        }
    }

    private void renderData(Canvas canvas) {
        for (Drawing drawing : mDrawings) {
            if (!drawing.isValid()) continue;
            canvas.save();
            if (drawing.drawInViewPort()) {
                canvas.clipRect(drawing.getLeft(), drawing.getTop(), drawing.getRight(), drawing.getBottom());
            }
            drawing.drawData(canvas);
            canvas.restore();
        }
    }

    /**
     * 绘制预期布局参数
     */
    public static class DrawingLayoutParams {
        public static final int NO_POSITION = -1;
        public static final int POSITION_LEFT = 0;
        public static final int POSITION_TOP = 2;
        public static final int POSITION_CENTER = 1;
        public static final int POSITION_RIGHT = 3;
        public static final int POSITION_BOTTOM = 4;

        @Retention(RetentionPolicy.SOURCE)
        @IntDef({NO_POSITION, POSITION_LEFT, POSITION_CENTER, POSITION_RIGHT})
        public @interface HorizontalPosition {

        }

        @Retention(RetentionPolicy.SOURCE)
        @IntDef({NO_POSITION, POSITION_TOP, POSITION_CENTER, POSITION_BOTTOM})
        public @interface VerticalPosition {

        }

        // 优先级：【固定宽度/固定高度】>【百分比】>【自适应】
        private int mWidth;             // 固定宽度
        private int mHeight;            // 固定高度
        private float mHorizontalPercent;       // 水平百分比
        private float mVerticalPercent;         // 垂直百分比
        private int mWeight;            // 自适应 注意：如果非垂直布局和非水平布局将会铺满

        // 优先级: 【水平布局/垂直布局】>【相对父布局】
        private boolean mIsHorizontalLinear;    // 是否是水平布局
        private boolean mIsVerticalLinear;      // 是否是垂直布局

        private int mHorizontalPosition = NO_POSITION;        // 相对父布局位置：左 中 右
        private int mVerticalPosition = NO_POSITION;          // 相对父布局位置：上 中 下

        private boolean mIsDefault;

        public DrawingLayoutParams() {
        }

        private DrawingLayoutParams(boolean defaultParams) {
            mIsDefault = defaultParams;
        }

        public int getWidth() {
            return mWidth;
        }

        public void setWidth(int width) {
            if (width < 0) width = 0;
            mWidth = width;
        }

        public int getHeight() {
            return mHeight;
        }

        public void setHeight(int height) {
            if (height < 0) height = 0;
            mHeight = height;
        }

        public float getHorizontalPercent() {
            return mHorizontalPercent;
        }

        public void setHorizontalPercent(float percent) {
            if (percent > 1.0f) {
                percent = 1.0f;
            } else if (percent < 0) {
                percent = 0;
            }
            mHorizontalPercent = percent;
        }

        public float getVerticalPercent() {
            return mVerticalPercent;
        }

        public void setVerticalPercent(float percent) {
            if (percent > 1.0f) {
                percent = 1.0f;
            } else if (percent < 0) {
                percent = 0;
            }
            mVerticalPercent = percent;
        }

        public int getWeight() {
            return mWeight;
        }

        public void setWeight(int weight) {
            if (weight < 0) weight = 0;
            mWeight = weight;
        }

        public boolean isHorizontalLinear() {
            return mIsHorizontalLinear;
        }

        public void setHorizontalLinear(boolean isHorizontalLinear) {
            mIsHorizontalLinear = isHorizontalLinear;
        }

        public boolean isVerticalLinear() {
            return mIsVerticalLinear;
        }

        public void setVerticalLinear(boolean isVerticalLinear) {
            mIsVerticalLinear = isVerticalLinear;
        }

        public int getHorizontalPosition() {
            return mHorizontalPosition;
        }

        public void setHorizontalPosition(@HorizontalPosition int horizontalPosition) {
            mHorizontalPosition = horizontalPosition;
        }

        public int getVerticalPosition() {
            return mVerticalPosition;
        }

        public void setVerticalPosition(@VerticalPosition int verticalPosition) {
            mVerticalPosition = verticalPosition;
        }
    }
}
