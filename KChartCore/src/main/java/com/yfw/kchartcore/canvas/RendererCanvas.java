package com.yfw.kchartcore.canvas;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;

import androidx.annotation.CallSuper;
import androidx.annotation.IntDef;

import com.yfw.kchartcore.adapter.BaseKChartAdapter;
import com.yfw.kchartcore.drawing.Drawing;
import com.yfw.kchartcore.impl.IDataProvider;
import com.yfw.kchartcore.index.IEntity;
import com.yfw.kchartcore.layout.CenterPoint;
import com.yfw.kchartcore.layout.IDispatchSingleTapParent;
import com.yfw.kchartcore.layout.IParentPortLayout;
import com.yfw.kchartcore.layout.IViewPort;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @日期 : 2020/7/1
 * @描述 : 画板
 */
public class RendererCanvas<T extends IEntity> implements IRendererCanvas<T>, IParentPortLayout, IViewPort, IDispatchSingleTapParent {

    private int mWidth;
    private int mHeight;
    protected final Rect mViewPort = new Rect();
    private final CenterPoint mCenterPoint = new CenterPoint();
    private boolean mInUpdateChildLayout;
    private IParentPortLayout mParentPortLayout;

    protected IDataProvider<T> mDataProvider;

    private final ArrayList<Drawing<T>> mDrawings = new ArrayList<>();
    private final LinkedHashMap<Integer, List<Drawing<T>>> mHorizontalLinearGroup = new LinkedHashMap<>();
    private final LinkedHashMap<Integer, List<Drawing<T>>> mVerticalLinearGroup = new LinkedHashMap<>();
    private final HashMap<Integer, Integer> mStartXValueMap = new HashMap<>();
    private final HashMap<Integer, Integer> mStartYValueMap = new HashMap<>();

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
            Drawing<T> drawing;
            for (int i = 0; i < mDrawings.size(); i++) {
                drawing = mDrawings.get(i);
                drawing.setWidth(0);
                drawing.setHeight(0);
                drawing.updateViewPort(0, 0, 0, 0);
            }
            setInUpdateChildLayout(false);
            return;
        }

        DrawingLayoutParams params;
        List<Drawing<T>> drawings;
        mStartXValueMap.clear();
        mStartYValueMap.clear();
        mHorizontalLinearGroup.clear();
        mVerticalLinearGroup.clear();
        Drawing<T> drawing;
        // 将所有线性布局分组
        for (int i = 0; i < mDrawings.size(); i++) {
            drawing = mDrawings.get(i);
            params = drawing.getLayoutParams();
            if (params.mAttachDrawing == null && params.mIsHorizontalLinear) {
                drawings = mHorizontalLinearGroup.get(params.mHorizontalLinearGroupId);
                if (drawings == null) {
                    mStartXValueMap.put(params.mHorizontalLinearGroupId, getLeft());
                    drawings = new ArrayList<>();
                    drawings.add(drawing);
                    mHorizontalLinearGroup.put(params.mHorizontalLinearGroupId, drawings);
                } else {
                    drawings.add(drawing);
                }
            }
            if (params.mAttachDrawing == null && params.mIsVerticalLinear) {
                drawings = mVerticalLinearGroup.get(params.mVerticalLinearGroupId);
                if (drawings == null) {
                    mStartYValueMap.put(params.mVerticalLinearGroupId, getTop());
                    drawings = new ArrayList<>();
                    drawings.add(drawing);
                    mVerticalLinearGroup.put(params.mVerticalLinearGroupId, drawings);
                } else {
                    drawings.add(drawing);
                }
            }
        }

        // 确认所有布局的宽高
        for (Map.Entry<Integer, List<Drawing<T>>> entry : mHorizontalLinearGroup.entrySet()) {
            // 水平线性布局
            calcHorizontalLinearDrawingsWidth(entry.getValue());
        }
        for (Map.Entry<Integer, List<Drawing<T>>> entry : mVerticalLinearGroup.entrySet()) {
            // 垂直线性布局
            calcVerticalLinearDrawingsHeight(entry.getValue());
        }
        // 非线性布局
        calcNotLinearDrawingsWidthAndHeight(mDrawings);

        int x;
        int y;
        // 最终确认布局【附着Drawing除外】
        for (int i = 0; i < mDrawings.size(); i++) {
            drawing = mDrawings.get(i);
            params = drawing.getLayoutParams();
            if (params.mAttachDrawing != null) continue;

            if (params.mIsHorizontalLinear) {
                x = mStartXValueMap.get(params.mHorizontalLinearGroupId);
                mStartXValueMap.put(params.mHorizontalLinearGroupId, x + drawing.getWidth());
            } else if (params.mHorizontalPosition == DrawingLayoutParams.POSITION_CENTER) {
                x = getLeft() + (getWidth() - drawing.getWidth()) / 2;
            } else if (params.mHorizontalPosition == DrawingLayoutParams.POSITION_RIGHT) {
                x = getRight() - drawing.getWidth();
            } else {
                x = getLeft();
            }
            if (params.mIsVerticalLinear) {
                y = mStartYValueMap.get(params.mVerticalLinearGroupId);
                mStartYValueMap.put(params.mVerticalLinearGroupId, y + drawing.getHeight());
            } else if (params.mVerticalPosition == DrawingLayoutParams.POSITION_CENTER) {
                y = getTop() + (getHeight() - drawing.getHeight()) / 2;
            } else if (params.mVerticalPosition == DrawingLayoutParams.POSITION_BOTTOM) {
                y = getBottom() - drawing.getHeight();
            } else {
                y = getTop();
            }
            drawing.updateViewPort(x, y, x + drawing.getWidth(), y + drawing.getHeight());
        }

        // 计算附着Drawing的布局
        for (int i = 0; i < mDrawings.size(); i++) {
            calcAttachDrawingLayout(mDrawings.get(i));
        }

        setInUpdateChildLayout(false);
    }

    /**
     * 计算出水平方向线性布局宽度
     *
     * @param hDrawings 所有水平线性布局
     */
    private void calcHorizontalLinearDrawingsWidth(List<Drawing<T>> hDrawings) {
        final int w = this.getWidth();
        int width = w;

        int weightUnitWidth = 0;
        int[] horizontalWeightAndWidth = getHorizontalWeightAndWidth(hDrawings);
        if (horizontalWeightAndWidth[0] != 0) {
            int remainingWidth = width - horizontalWeightAndWidth[1];
            if (remainingWidth > 0) {
                // 说明固定宽度没有超出总宽度
                weightUnitWidth = (int) (remainingWidth * 1.0f / horizontalWeightAndWidth[0]);
            }
        }

        int tempValue;
        DrawingLayoutParams params;
        // 计算出水平方向线性布局宽度
        Drawing<T> drawing;
        for (int i = 0; i < hDrawings.size(); i++) {
            drawing = hDrawings.get(i);
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
            for (int i = 0; i < hDrawings.size(); i++) {
                drawing = hDrawings.get(i);
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
    }

    /**
     * 计算出垂直方向线性布局高度度
     *
     * @param vDrawings 所有垂直线性布局
     */
    private void calcVerticalLinearDrawingsHeight(List<Drawing<T>> vDrawings) {
        final int h = this.getHeight();
        int height = h;
        int weightUnitHeight = 0;
        int[] verticalWeightAndHeight = getVerticalWeightAndHeight(vDrawings);
        if (verticalWeightAndHeight[0] != 0) {
            int remainingHeight = height - verticalWeightAndHeight[1];
            if (remainingHeight > 0) {
                // 说明固定高度没有超出总宽度
                weightUnitHeight = (int) (remainingHeight * 1.0f / verticalWeightAndHeight[0]);
            }
        }

        int tempValue;
        DrawingLayoutParams params;
        // 计算出垂直方向线性布局高度
        Drawing<T> drawing;
        for (int i = 0; i < vDrawings.size(); i++) {
            drawing = vDrawings.get(i);
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
            for (int i = 0; i < vDrawings.size(); i++) {
                drawing = vDrawings.get(i);
                params = drawing.getLayoutParams();
                if (params.mHeight > 0 || params.mVerticalPercent > 0) continue;
                if (params.mWeight > 0) {
                    tempValue = Math.round(height * 1.0f / verticalWeightAndHeight[0] * params.mWeight + 0.5f);
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
    }

    /**
     * 计算非线性布局宽高
     *
     * @param drawings 所有布局，内部会把线性布局给去除
     */
    private void calcNotLinearDrawingsWidthAndHeight(List<Drawing<T>> drawings) {
        final int w = this.getWidth();
        final int h = this.getHeight();
        int tempValue;
        DrawingLayoutParams params;
        Drawing<T> drawing;
        for (int i = 0; i < drawings.size(); i++) {
            drawing = drawings.get(i);
            params = drawing.getLayoutParams();
            if (params.mAttachDrawing != null) continue;

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
    }

    /**
     * 计算附着Drawing的布局
     */
    private void calcAttachDrawingLayout(Drawing<T> drawing) {
        DrawingLayoutParams layoutParams = drawing.getLayoutParams();
        if (layoutParams.mAttachDrawing == null) return;
        // 依赖中的依赖
        calcAttachDrawingLayout(layoutParams.mAttachDrawing);

        drawing.setWidth(layoutParams.mAttachDrawing.getWidth());
        drawing.setHeight(layoutParams.mAttachDrawing.getHeight());
        drawing.updateViewPort(
                layoutParams.mAttachDrawing.getLeft(),
                layoutParams.mAttachDrawing.getTop(),
                layoutParams.mAttachDrawing.getRight(),
                layoutParams.mAttachDrawing.getBottom());
    }

    /**
     * @param hDrawings 所有水平线性布局
     * @return [0]:all weight [1]:all width
     */
    private int[] getHorizontalWeightAndWidth(List<Drawing<T>> hDrawings) {
        int[] value = new int[2];
        Drawing<T> drawing;
        for (int i = 0; i < hDrawings.size(); i++) {
            drawing = hDrawings.get(i);
            DrawingLayoutParams params = drawing.getLayoutParams();
            if (params.mWidth > 0) {
                value[1] += params.mWidth;
            } else if (params.mHorizontalPercent > 0) {
                value[1] += Math.round(this.getWidth() * params.mHorizontalPercent);
            } else if (params.mWeight > 0) {
                value[0] += params.mWeight;
            }
        }
        return value;
    }

    /**
     * @param vDrawings 所有垂直线性布局
     * @return [0]:all weight [1]:all height
     */
    private int[] getVerticalWeightAndHeight(List<Drawing<T>> vDrawings) {
        int[] value = new int[2];
        Drawing<T> drawing;
        for (int i = 0; i < vDrawings.size(); i++) {
            drawing = vDrawings.get(i);
            DrawingLayoutParams params = drawing.getLayoutParams();
            if (params.mHeight > 0) {
                value[1] += params.mHeight;
            } else if (params.mVerticalPercent > 0) {
                value[1] += Math.round(this.getHeight() * params.mVerticalPercent);
            } else if (params.mWeight > 0) {
                value[0] += params.mWeight;
            }
        }
        return value;
    }

    @CallSuper
    @Override
    public void attachedParentPortLayout(IParentPortLayout portLayout, IDataProvider<T> dataProvider) {
        mParentPortLayout = portLayout;
        mDataProvider = dataProvider;
        for (int i = 0; i < mDrawings.size(); i++) {
            mDrawings.get(i).attachedParentPortLayout(this, mDataProvider);
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
        for (int i = 0; i < mDrawings.size(); i++) {
            mDrawings.get(i).detachedParentPortLayout();
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
    public CenterPoint getCenter() {
        return mCenterPoint;
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
            mCenterPoint.setCenterX(mViewPort.exactCenterX());
            mCenterPoint.setCenterY(mViewPort.exactCenterY());
        } else {
            Log.w("RendererCanvas", "Setting port is not allowed.");
        }
    }

    @Override
    public String toViewPortString() {
        StringBuilder vps = new StringBuilder(this.getClass().getSimpleName());
        vps.append("--ViewPort: ");
        vps.append("[width: ").append(getWidth()).append(", height: ").append(getHeight()).append("]");
        vps.append("[left: ").append(getLeft()).append("]");
        vps.append("[top: ").append(getTop()).append("]");
        vps.append("[right: ").append(getRight()).append("]");
        vps.append("[bottom: ").append(getBottom()).append("]");
        vps.append("\n");
        Drawing<T> drawing;
        for (int i = 0; i < mDrawings.size(); i++) {
            drawing = mDrawings.get(i);
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
    public int drawingSize() {
        return mDrawings.size();
    }

    @Override
    public int drawingIndexOf(Drawing<T> drawing) {
        return mDrawings.indexOf(drawing);
    }

    @Override
    public int drawingIndexTag(String tag) {
        for (int i = 0; i < mDrawings.size(); i++) {
            if (mDrawings.get(i).getTag().equals(tag)) return i;
        }
        return -1;
    }

    @Override
    public void addDrawing(Drawing<T> drawing) {
        addDrawing(mDrawings.size(), drawing, false);
    }

    @Override
    public void addDrawing(int index, Drawing<T> drawing) {
        addDrawing(index, drawing, false);
    }

    @Override
    public void addDrawing(Drawing<T> drawing, boolean isMainIndexDrawing) {
        addDrawing(mDrawings.size(), drawing, isMainIndexDrawing);
    }

    @Override
    public void addDrawing(int index, Drawing<T> drawing, boolean isMainIndexDrawing) {
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

        mDrawings.add(index, drawing);
        if (mParentPortLayout != null && !drawing.isAttachedParentPortLayout()) {
            drawing.attachedParentPortLayout(this, mDataProvider);
        }
    }

    @Override
    public Drawing<T> getDrawing(int index) {
        return mDrawings.get(index);
    }

    @Override
    public Drawing<T> getDrawingByTag(String tag) {
        Iterator<Drawing<T>> iterator = mDrawings.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().getTag().endsWith(tag)) {
                return iterator.next();
            }
        }
        return null;
    }

    @Override
    public void replaceDrawing(int index, Drawing<T> drawing) {
        if (drawing == null) return;

        Drawing<T> old = mDrawings.set(index, drawing);
        if (old != null && old.isAttachedParentPortLayout()) {
            old.detachedParentPortLayout();
        }
        if (mParentPortLayout != null && !drawing.isAttachedParentPortLayout()) {
            drawing.attachedParentPortLayout(this, mDataProvider);
        }
    }

    @Override
    public void removeDrawing(int index) {
        Drawing<T> drawing = mDrawings.remove(index);
        if (drawing != null && drawing.isAttachedParentPortLayout()) {
            drawing.detachedParentPortLayout();
        }
    }

    @Override
    public void removeDrawing(Drawing<T> drawing) {
        if (drawing == null) return;

        mDrawings.remove(drawing);
        if (drawing.isAttachedParentPortLayout()) {
            drawing.detachedParentPortLayout();
        }
    }

    @Override
    public void removeDrawingByTag(String tag) {
        Iterator<Drawing<T>> iterator = mDrawings.iterator();
        while (iterator.hasNext()) {
            Drawing<T> next = iterator.next();
            if (next.getTag().endsWith(tag)) {
                iterator.remove();
                if (next.isAttachedParentPortLayout()) {
                    next.detachedParentPortLayout();
                }
            }
        }
    }

    @Override
    public boolean inDispatchRange(int x, int y) {
        return mViewPort.contains(x, y);
    }

    @Override
    public boolean dispatchSingleTap(int x, int y) {
        for (int i = mDrawings.size() - 1; i >= 0; i--) {
            Drawing<T> drawing = mDrawings.get(i);
            if (drawing.canSingleTap() && drawing.inDispatchRange(x, y) && drawing.onSingleTap(x, y)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void preCalcDataValue(boolean emptyBounds) {
        for (int i = 0; i < mDrawings.size(); i++) {
            mDrawings.get(i).preCalcDataValue(emptyBounds);
        }
    }

    @Override
    public void render(Canvas canvas) {
        BaseKChartAdapter<T> adapter = mDataProvider.getAdapter();
        if (adapter == null || adapter.getCount() == 0) {
            renderEmpty(canvas);
        } else {
            renderData(canvas);
        }
    }

    private void renderEmpty(Canvas canvas) {
        Drawing<T> drawing;
        for (int i = 0; i < mDrawings.size(); i++) {
            drawing = mDrawings.get(i);
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
        Drawing<T> drawing;
        for (int i = 0; i < mDrawings.size(); i++) {
            drawing = mDrawings.get(i);
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

        // 优先级：【附着Drawing】>【固定宽度/固定高度】>【百分比】>【自适应】
        private Drawing mAttachDrawing; // 附着于某个Drawing，宽高与该Drawing一致

        private int mWidth;             // 固定宽度
        private int mHeight;            // 固定高度
        private float mHorizontalPercent;       // 水平百分比
        private float mVerticalPercent;         // 垂直百分比
        private int mWeight;            // 自适应 注意：如果非垂直布局和非水平布局将会铺满

        // 优先级: 【附着Drawing】>【水平布局/垂直布局】>【相对父布局】
        private int mHorizontalLinearGroupId;   // 水平布局组id
        private boolean mIsHorizontalLinear;    // 是否是水平布局
        private int mVerticalLinearGroupId;     // 垂直布局组id
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

        /**
         * 设置是否是水平布局
         *
         * @param groupId 组id，不同组id会单独线性布局
         */
        public void setHorizontalLinear(boolean isHorizontalLinear, int groupId) {
            mIsHorizontalLinear = isHorizontalLinear;
            mHorizontalLinearGroupId = groupId;
        }

        public boolean isVerticalLinear() {
            return mIsVerticalLinear;
        }

        /**
         * 设置是否是垂直布局
         *
         * @param groupId 组id，不同组id会单独线性布局
         */
        public void setVerticalLinear(boolean isVerticalLinear, int groupId) {
            mIsVerticalLinear = isVerticalLinear;
            mVerticalLinearGroupId = groupId;
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

        public Drawing getAttachDrawing() {
            return mAttachDrawing;
        }

        public void setAttachDrawing(Drawing drawing) {
            mAttachDrawing = drawing;
        }
    }
}
