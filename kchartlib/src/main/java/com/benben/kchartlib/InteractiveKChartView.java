package com.benben.kchartlib;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;

import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.benben.kchartlib.adapter.BaseKChartAdapter;
import com.benben.kchartlib.animation.AnimationManager;
import com.benben.kchartlib.buffer.IsFullScreenBuffer;
import com.benben.kchartlib.buffer.MaxScrollXBuffer;
import com.benben.kchartlib.buffer.PointWidthBuffer;
import com.benben.kchartlib.buffer.ScalePointWidthBuffer;
import com.benben.kchartlib.canvas.IRendererCanvas;
import com.benben.kchartlib.data.AdapterDataObserver;
import com.benben.kchartlib.data.Transformer;
import com.benben.kchartlib.drawing.IDrawing;
import com.benben.kchartlib.impl.IMainCanvasPort;
import com.benben.kchartlib.render.BackgroundRenderer;
import com.benben.kchartlib.render.ForegroundRenderer;
import com.benben.kchartlib.render.MainRenderer;

/**
 * @日期 : 2020/6/30
 * @描述 :
 */
public class InteractiveKChartView extends ScrollAndScaleView implements AnimationManager.AnimationListener {

    private static final int POINT_FIXED_WIDTH_MODE = 0;    // 固定点宽度，显示范围大显示数量多（默认）
    private static final int POINT_FIXED_SIZE_MODE = 1;     // 固定显示数量，显示范围大单点宽度大

    private int mPointMode = POINT_FIXED_WIDTH_MODE;
    private int mPointModeValue = 6;        // 固定数量：值为数量值 固定点宽度：值为单点宽度
    private PointWidthBuffer mPointWidthBuffer = new PointWidthBuffer();
    private ScalePointWidthBuffer mScalePointWidthBuffer = new ScalePointWidthBuffer();
    private IsFullScreenBuffer mIsFullScreenBuffer = new IsFullScreenBuffer();
    private MaxScrollXBuffer mMaxScrollXBuffer = new MaxScrollXBuffer();

    private int mDataLength;                // 视图总长度
    private BaseKChartAdapter mAdapter;     // 数据适配器

    private Transformer mTransformer;
    private AnimationManager mAnimationManager;

    private boolean mCanUpdateLayout;
    private Rect mViewPort = new Rect();    // 视图可绘制区域
    private boolean mIsRenderBackground = false;
    private BackgroundRenderer mBackgroundRenderer;
    private MainRenderer mMainRenderer;
    private boolean mIsRenderForeground = false;
    private ForegroundRenderer mForegroundRenderer;

    public InteractiveKChartView(@NonNull Context context) {
        this(context, null);
    }

    public InteractiveKChartView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public InteractiveKChartView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mMainRenderer = new MainRenderer(this);
        mTransformer = new Transformer(this);
        mAnimationManager = new AnimationManager(this);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mCanUpdateLayout = true;
        mViewPort.left = getPaddingLeft();
        mViewPort.top = getPaddingTop();
        mViewPort.right = w - getPaddingRight();
        mViewPort.bottom = h - getPaddingBottom();
        updateRenderPortLayout();
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        super.setPadding(left, top, right, bottom);
        mViewPort.left = left;
        mViewPort.top = top;
        mViewPort.right = getWidth() - right;
        mViewPort.bottom = getHeight() - bottom;
        updateRenderPortLayout();
    }

    @Override
    void preInvalidate() {
        if (!mMainRenderer.mainCanvasValid()) {
            return;
        }
        mTransformer.updateBounds();
        if (mIsRenderBackground) {
            mBackgroundRenderer.preCalcDataValue();
        }
        mMainRenderer.preCalcDataValue();
        if (mIsRenderForeground) {
            mForegroundRenderer.preCalcDataValue();
        }
    }

    @Override
    int getMinScrollX() {
        return 0;
    }

    /**
     * 最大可滚动值受数据量/缩放值/主视图宽度所影响
     */
    @Override
    int getMaxScrollX() {
        if (isFullScreen()) {
            if (mMaxScrollXBuffer.mScaleX == getScaleX()) {
                return mMaxScrollXBuffer.mMaxScrollX;
            }
            mMaxScrollXBuffer.mScaleX = getScaleX();
            mMaxScrollXBuffer.mMaxScrollX = Math.round(mDataLength * getScaleX() - mMainRenderer.getMainCanvasWidth());
            return mMaxScrollXBuffer.mMaxScrollX;
        }
        return 0;
    }

    @Override
    void onScrollChanged(int newScrollX, int oldScrollX) {

    }

    @Override
    int onScaleChanged(float scale, float oldScale, float focusX, float focusY) {
        float width = mMainRenderer.getMainCanvasWidth();
        float left = mMainRenderer.getMainCanvasLeft();
        float right = mMainRenderer.getMainCanvasRight();
        if (focusX < 0) {
            // 小于0说明无焦点，则让焦点落在视图右边界上
            focusX = right;
        }
        float x;
        if (left > focusX) {
            x = 0;
        } else if (right < focusX) {
            x = width;
        } else {
            x = focusX - left;
        }
        // 使得以缩放手势中心点进行缩放
        float changeSpace = (width - x) * scale / oldScale - (width - x);
        return Math.round(mScrollX * scale / oldScale + changeSpace);
    }

    // for performance tracking
    private long totalTime = 0;
    private long drawCycles = 0;

    @Override
    protected void onDraw(Canvas canvas) {
        long startTime = System.nanoTime();
        if (!mMainRenderer.mainCanvasValid()) {
            return;
        }
        canvas.clipRect(mViewPort);
        if (mIsRenderBackground) {
            mBackgroundRenderer.render(canvas);
        }
        mMainRenderer.render(canvas);
        if (mIsRenderForeground) {
            mForegroundRenderer.render(canvas);
        }
        long drawtime = System.nanoTime() - startTime;
        totalTime += drawtime;
        drawCycles++;
        long average = totalTime / drawCycles;
        Log.i("Drawtime", "Drawtime: " + drawtime + " ms, average: " + average + " ms, cycles: " + drawCycles);
        mAnimationManager.animUpdate();
    }

    @Override
    public void onAnimation() {
        invalidate();
    }

    @Override
    public Transformer getTransformer() {
        return mTransformer;
    }

    @Override
    public AnimationManager.ChartAnimtion getChartAnimation() {
        return mAnimationManager.getChartAnimtion();
    }

    @Override
    public IMainCanvasPort getMainCanvasPort() {
        return mMainRenderer;
    }

    public void setRenderBackgroud(boolean render) {
        if (mIsRenderBackground == render) return;
        if (render && mBackgroundRenderer == null) {
            mBackgroundRenderer = new BackgroundRenderer(this);
        }
        mIsRenderBackground = render;
        if (render) {
            mBackgroundRenderer.updateViewPort(mViewPort.left, mViewPort.top, mViewPort.right, mViewPort.bottom);
            mBackgroundRenderer.updateChildLayout();
        }
    }

    public BackgroundRenderer getBackgroundRenderer() {
        if (mBackgroundRenderer == null) {
            mBackgroundRenderer = new BackgroundRenderer(this);
        }
        return mBackgroundRenderer;
    }

    public MainRenderer getMainRenderer() {
        return mMainRenderer;
    }

    public void setRenderForeground(boolean render) {
        if (mIsRenderForeground == render) return;
        if (render && mForegroundRenderer == null) {
            mForegroundRenderer = new ForegroundRenderer(this);
        }
        mIsRenderForeground = render;
        if (render) {
            mForegroundRenderer.updateViewPort(mViewPort.left, mViewPort.top, mViewPort.right, mViewPort.bottom);
            mForegroundRenderer.updateChildLayout();
        }
    }

    public ForegroundRenderer getForegroundRenderer() {
        if (mForegroundRenderer == null) {
            mForegroundRenderer = new ForegroundRenderer(this);
        }
        return mForegroundRenderer;
    }

    /**
     * 在添加或更新{@link IRendererCanvas}和{@link IDrawing}时
     * 使布局参数生效
     */
    public void updateRenderPortLayout() {
        if (!mCanUpdateLayout) return;
        if (mBackgroundRenderer != null) {
            mBackgroundRenderer.updateViewPort(mViewPort.left, mViewPort.top, mViewPort.right, mViewPort.bottom);
            mBackgroundRenderer.updateChildLayout();
        }
        mMainRenderer.updateViewPort(mViewPort.left, mViewPort.top, mViewPort.right, mViewPort.bottom);
        mMainRenderer.updateChildLayout();
        if (mForegroundRenderer != null) {
            mForegroundRenderer.updateViewPort(mViewPort.left, mViewPort.top, mViewPort.right, mViewPort.bottom);
            mForegroundRenderer.updateChildLayout();
        }
        notifyChange();
    }

    @Override
    public boolean isFullScreen() {
        if (mIsFullScreenBuffer.mScaleX == getScaleX()) {
            return mIsFullScreenBuffer.mIsFullScreen;
        }
        mIsFullScreenBuffer.mScaleX = getScaleX();
        mIsFullScreenBuffer.mIsFullScreen = mDataLength * getScaleX() >= mMainRenderer.getMainCanvasWidth();
        return mIsFullScreenBuffer.mIsFullScreen;
    }

    /**
     * 设置屏幕显示的item数量，该值为推荐数量，实际以最终计算为准
     * 限制最小值为2，且计算出的单点宽度最小值为3
     */
    public void setPointShowSize(int size) {
        mPointModeValue = Math.max(size, 2);
        mPointMode = POINT_FIXED_SIZE_MODE;
        notifyChange();
    }

    /**
     * 设置单个Item宽度，最小值为3个像素点
     */
    public void setPointWidth(int pointWidth) {
        mPointModeValue = Math.max(pointWidth, 3);
        mPointMode = POINT_FIXED_WIDTH_MODE;
        notifyChange();
    }

    /**
     * 获取经过缩放后的单点宽度
     */
    @Override
    public float getScalePointWidth() {
        if (mScalePointWidthBuffer.mScaleX == getScaleX()) {
            return mScalePointWidthBuffer.mScalePointWidth;
        }
        mScalePointWidthBuffer.mScalePointWidth = getPointWidth() * getScaleX();
        mScalePointWidthBuffer.mScaleX = getScaleX();
        return mScalePointWidthBuffer.mScalePointWidth;
    }

    /**
     * 获取单点宽度
     */
    @Override
    public int getPointWidth() {
        if (!mPointWidthBuffer.mHasBuffer) {
            if (mPointMode == POINT_FIXED_WIDTH_MODE) {
                mPointWidthBuffer.mPointWidth = mPointModeValue;
            } else {
                int canvasWidth = mMainRenderer.getMainCanvasWidth();
                mPointWidthBuffer.mPointWidth = Math.max(Math.round(canvasWidth * 1.0f / mPointModeValue), 3);
            }
            mPointWidthBuffer.mHasBuffer = true;
        }
        return mPointWidthBuffer.mPointWidth;
    }

    /**
     * 指定屏幕位置到达指定索引
     *
     * @param inScreenPercent X轴方向主视窗{@link IMainCanvasPort#getMainCanvasWidth()}所在百分比位置，
     *                        视窗左侧，inScreenPercent = 0.0f
     *                        视窗中间，inScreenPercent = 0.5f
     *                        视窗右侧，inScreenPercent = 1.0f
     */
    public void scrollToIndex(int index, @FloatRange(from = 0, to = 1.0) float inScreenPercent, boolean anim) {
        if (index < 0 || mAdapter == null) {
            index = 0;
        } else if (index >= mAdapter.getCount()) {
            index = mAdapter.getCount() - 1;
        }
        if (inScreenPercent < 0) {
            inScreenPercent = 0;
        } else if (inScreenPercent > 1.0f) {
            inScreenPercent = 1.0f;
        }
        int targetScrollX = mTransformer.getScrollXForIndex(index, inScreenPercent);
        int maxScrollX = getMaxScrollX();
        int targetScroll = Math.min(targetScrollX, maxScrollX);
        if (anim) {
            animScroll(targetScrollX);
        } else {
            setScroll(targetScroll);
        }
    }

    public void setAdapter(@Nullable BaseKChartAdapter adapter) {
        if (mAdapter != null) {
            mAdapter.unregisterDataSetObserver(mDataObserver);
        }
        mAdapter = adapter;
        if (adapter != null) {
            adapter.registerDataSetObserver(mDataObserver);
        }
        notifyChange();
    }

    @Override
    public BaseKChartAdapter getAdapter() {
        return mAdapter;
    }

    private void notifyChange() {
        resetBuffer();
        if (mAdapter == null) {
            mDataLength = 0;
        } else {
            mDataLength = getPointWidth() * mAdapter.getCount();
        }
        int oldScrollX = mScrollX;
        int fixScrollX = getFixScrollX(oldScrollX);
        if (fixScrollX == oldScrollX) {
            invalidate();
        } else {
            setScroll(fixScrollX);
        }
    }

    private void notifyLastInserted(int itemCount) {
        mDataLength = getPointWidth() * mAdapter.getCount();
        resetBuffer();
        if (!isFullScreen()) {
            invalidate();
            return;
        }
        int oldScrollX = mScrollX;
        int scrollRange = Math.round(getScalePointWidth() * itemCount);
        setScrollerThenAnimScroll(oldScrollX + scrollRange, oldScrollX);
    }

    private void resetBuffer() {
        mPointWidthBuffer.mHasBuffer = false;
        mScalePointWidthBuffer.mScaleX = 0;
        mIsFullScreenBuffer.mScaleX = 0;
        mMaxScrollXBuffer.mScaleX = 0;
    }

    private AdapterDataObserver mDataObserver = new AdapterDataObserver() {

        @Override
        public void onChanged() {
            notifyChange();
        }

        @Override
        public void onFirstInserted(int itemCount) {
            notifyChange();
        }

        @Override
        public void notifyLastUpdated() {
            notifyChange();
        }

        @Override
        public void onLastInserted(int itemCount) {
            notifyLastInserted(itemCount);
        }
    };
}
