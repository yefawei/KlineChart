package com.yfw.kchartcore;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;

import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.yfw.kchartcore.adapter.BaseKChartAdapter;
import com.yfw.kchartcore.animation.AnimationManager;
import com.yfw.kchartcore.buffer.IsFullScreenBuffer;
import com.yfw.kchartcore.buffer.MaxScrollXBuffer;
import com.yfw.kchartcore.buffer.PointWidthBuffer;
import com.yfw.kchartcore.buffer.ScalePointWidthBuffer;
import com.yfw.kchartcore.canvas.IRendererCanvas;
import com.yfw.kchartcore.data.AdapterDataObserver;
import com.yfw.kchartcore.data.IDataInsertedHandler;
import com.yfw.kchartcore.data.Transformer;
import com.yfw.kchartcore.drawing.IDrawing;
import com.yfw.kchartcore.helper.PaddingHelper;
import com.yfw.kchartcore.index.IEntity;
import com.yfw.kchartcore.layout.IMainCanvasPort;
import com.yfw.kchartcore.render.BackgroundRenderer;
import com.yfw.kchartcore.render.ForegroundRenderer;
import com.yfw.kchartcore.render.MainRenderer;
import com.yfw.kchartcore.touch.TouchTapManager;

import java.util.ArrayList;
import java.util.List;

/**
 * @日期 : 2020/6/30
 * @描述 : 可交互K线
 */
public class InteractiveKChartView<T extends IEntity> extends ScrollAndScaleView<T> implements AnimationManager.AnimationListener {

    private static final int POINT_FIXED_WIDTH_MODE = 0;    // 固定点宽度，显示范围大显示数量多（默认）
    private static final int POINT_FIXED_SIZE_MODE = 1;     // 固定显示数量，显示范围大单点宽度大

    private int mPointMode = POINT_FIXED_WIDTH_MODE;
    private int mPointModeValue = 6;        // 固定数量：值为数量值 固定点宽度：值为单点宽度
    private final PointWidthBuffer mPointWidthBuffer = new PointWidthBuffer();
    private final ScalePointWidthBuffer mScalePointWidthBuffer = new ScalePointWidthBuffer();
    private final MaxScrollXBuffer mMaxScrollXBuffer = new MaxScrollXBuffer();
    private final IsFullScreenBuffer mIsFullScreenBuffer = new IsFullScreenBuffer();

    private boolean mPreviousIsFullScreen;  // 备份上一次是否满屏
    private int mPreviousDataLength;        // 备份上一次数据长度
    private int mDataLength;                // 视图总长度
    private List<OnAdapterChangeListener<T>> mAdapterChangeListeners;
    private BaseKChartAdapter<T> mAdapter;     // 数据适配器
    private IDataInsertedHandler<T> mDataSizeChangeHandler;

    private final PaddingHelper mPaddingHelper;     // 边界辅助类
    private final Transformer<T> mTransformer;
    private final AnimationManager mAnimationManager;
    private final TouchTapManager<T> mTouchTapManager;

    private boolean mCanUpdateLayout;
    private final Rect mViewPort = new Rect();    // 视图可绘制区域
    private boolean mIsRenderBackground = false;
    private BackgroundRenderer<T> mBackgroundRenderer;
    private final MainRenderer<T> mMainRenderer;
    private boolean mIsRenderForeground = false;
    private ForegroundRenderer<T> mForegroundRenderer;

    private boolean mInRightPadding;
    private boolean mInLeftPadding;
    private OnPaddingListener mOnPaddingListener;
    private boolean mInRightMargin;
    private boolean mInLeftMargin;
    private OnMarginListener mOnMarginListener;

    public InteractiveKChartView(@NonNull Context context) {
        this(context, null);
    }

    public InteractiveKChartView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public InteractiveKChartView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        limitBackgroundAndForeground(context, attrs, defStyleAttr);
        mMainRenderer = new MainRenderer<>(this);
        mPaddingHelper = new PaddingHelper();
        mTransformer = new Transformer<>(this);
        mAnimationManager = new AnimationManager(this);
        mTouchTapManager = new TouchTapManager<>(this);
    }

    /**
     * 出于性能考虑,不要在此View设置背景和前景
     * 由于此View刷新频繁，会连同背景和前景也一并重绘
     * 将背景和前景放置在其它View上，在硬件加速下不会触发背景和前景的重绘
     */
    private void limitBackgroundAndForeground(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.InteractiveKChartView, defStyleAttr, 0);
        int indexCount = typedArray.getIndexCount();
        for (int i = 0; i < indexCount; i++) {
            int index = typedArray.getIndex(i);
            if (index == R.styleable.InteractiveKChartView_android_background) {
                throw new IllegalArgumentException("For performance reasons, do not set the background in KChartView.");
            }
            if (index == R.styleable.InteractiveKChartView_android_foreground) {
                throw new IllegalArgumentException("For performance reasons, do not set the foreground in KChartView.");
            }
        }
        typedArray.recycle();
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
        if (mAdapter == null || mAdapter.getCount() == 0 || !mMainRenderer.mainCanvasIsValid()) {
            mTransformer.emptyBounds();
        } else {
            mTransformer.calcBounds();
        }
        mAnimationManager.animUpdate();
        if (mIsRenderBackground) {
            mBackgroundRenderer.preCalcDataValue(mTransformer.isEmptyBounds());
        }
        mMainRenderer.preCalcDataValue(mTransformer.isEmptyBounds());
        if (mIsRenderForeground) {
            mForegroundRenderer.preCalcDataValue(mTransformer.isEmptyBounds());
        }
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (mInRightMargin) {
            // 到达了右边界再滑动的
            mInRightMargin = false;
        }
        if (mInLeftMargin) {
            // 到达了左边界再滑动的
            mInLeftMargin = false;
        }
        if (!isFullScreen()) {
            if (mOnPaddingListener != null) {
                if (mInRightPadding) mOnPaddingListener.rightPadding(false);
                if (mInLeftPadding) mOnPaddingListener.leftPadding(false);
            }
            mInRightPadding = false;
            mInLeftPadding = false;
            return;
        }
        if (l < 0) {
            if (!mInRightPadding && mOnPaddingListener != null) mOnPaddingListener.rightPadding(true);
            mInRightPadding = true;
        } else {
            if (mInRightPadding && mOnPaddingListener != null) mOnPaddingListener.rightPadding(false);
            mInRightPadding = false;
        }
        if (mPaddingHelper.hasLeftExtPadding()) {
            if (l > getMaxScrollX() - mPaddingHelper.getLeftExtPadding(mScaleX)) {
                if (!mInLeftPadding && mOnPaddingListener != null) mOnPaddingListener.leftPadding(true);
                mInLeftPadding = true;
            } else {
                if (mInLeftPadding && mOnPaddingListener != null) mOnPaddingListener.leftPadding(false);
                mInLeftPadding = false;
            }
        }
    }

    @Override
    int onScaleChanged(float scale, float oldScale, float focusX, float focusY) {
        boolean isFullScreen = mPreviousIsFullScreen;
        mPreviousIsFullScreen = isFullScreen();
        if (!mPreviousIsFullScreen) {
            // 非满屏
            return 0;
        }
        if (!isFullScreen && mScrollX == 0) {
            // 缩放非满屏 到 满屏
            // 判断mScrollX == 0是因为从无数据到到有数据且是满屏数据，则用户有滑动过不进入该代码块
            return getMinScrollX();
        }
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
        return getFixScrollX(Math.round(mScrollX * scale / oldScale + changeSpace));
    }

    @Override
    public void onRightMargin() {
        if (!mInRightMargin && mOnMarginListener != null) {
            mOnMarginListener.onRightMargin();
        }
        mInRightMargin = true;
    }

    @Override
    public void onLeftMargin() {
        if (!mInLeftMargin && mOnMarginListener != null) {
            mOnMarginListener.onLeftMargin();
        }
        mInLeftMargin = true;
    }

    @Override
    boolean dispatchSingleTap(int x, int y) {
        boolean handled = false;
        if (mIsRenderForeground && mForegroundRenderer.inDispatchRange(x, y)) {
            handled = mForegroundRenderer.dispatchSingleTap(x, y);
        }
        if (!handled && mMainRenderer.inDispatchRange(x, y)) {
            handled = mMainRenderer.dispatchSingleTap(x, y);
        }
        if (!handled && mIsRenderBackground && mBackgroundRenderer.inDispatchRange(x, y)) {
            handled = mBackgroundRenderer.dispatchSingleTap(x, y);
        }
        return handled;
    }

    @Override
    void onSingleTap(float x, float y) {
        int index = mTransformer.getIndexByScreenX(x);
        if (index == -1) {
            removeTap();
            return;
        }
        mTouchTapManager.setSingleTapInfo(x, y, index, mAdapter.getItem(index));
    }

    @Override
    void onDoubleTap(float x, float y) {
        int index = mTransformer.getIndexByScreenX(x);
        if (index == -1) {
            removeTap();
            return;
        }
        mTouchTapManager.setDoubleTapInfo(x, y, index, mAdapter.getItem(index));
    }

    @Override
    void onLongTap(float x, float y) {
        int index = mTransformer.getIndexByScreenX(x);
        if (index == -1) {
            removeTap();
            return;
        }
        mTouchTapManager.setLongTapInfo(x, y, index, mAdapter.getItem(index));
    }

    @Override
    void removeTap() {
        mTouchTapManager.removeAllTapInfo();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!mMainRenderer.mainCanvasIsValid()) {
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
        mAnimationManager.animCheck();
    }

    @Override
    public void onAnimation() {
        invalidate();
    }

    @Override
    public Transformer<T> getTransformer() {
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
            mBackgroundRenderer = new BackgroundRenderer<>(this);
        }
        mIsRenderBackground = render;
        if (render) {
            mBackgroundRenderer.updateViewPort(mViewPort.left, mViewPort.top, mViewPort.right, mViewPort.bottom);
            mBackgroundRenderer.updateChildLayout();
        }
    }

    public BackgroundRenderer<T> getBackgroundRenderer() {
        if (mBackgroundRenderer == null) {
            mBackgroundRenderer = new BackgroundRenderer<>(this);
        }
        return mBackgroundRenderer;
    }

    public MainRenderer<T> getMainRenderer() {
        return mMainRenderer;
    }

    public void setRenderForeground(boolean render) {
        if (mIsRenderForeground == render) return;
        if (render && mForegroundRenderer == null) {
            mForegroundRenderer = new ForegroundRenderer<>(this);
        }
        mIsRenderForeground = render;
        if (render) {
            mForegroundRenderer.updateViewPort(mViewPort.left, mViewPort.top, mViewPort.right, mViewPort.bottom);
            mForegroundRenderer.updateChildLayout();
        }
    }

    public ForegroundRenderer<T> getForegroundRenderer() {
        if (mForegroundRenderer == null) {
            mForegroundRenderer = new ForegroundRenderer<>(this);
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
        requestDraw();
    }

    /**
     * 数据是否满屏
     */
    @Override
    public boolean isFullScreen() {
        if (mIsFullScreenBuffer.mScaleX == mScaleX) {
            return mIsFullScreenBuffer.mIsFullScreen;
        }
        mIsFullScreenBuffer.mScaleX = mScaleX;
        mIsFullScreenBuffer.mIsFullScreen = mDataLength * mScaleX > (mMainRenderer.getMainCanvasWidth()
                - mPaddingHelper.getRightExtPadding(mScaleX));
        return mIsFullScreenBuffer.mIsFullScreen;
    }

    @Override
    public int getMinScrollX() {
        if (isFullScreen()) {
            return -mPaddingHelper.getRightExtPadding(mScaleX);
        }
        return 0;
    }

    /**
     * 最大可滚动值受数据量/缩放值/主视图宽度所影响
     */
    @Override
    public int getMaxScrollX() {
        if (isFullScreen()) {
            if (mMaxScrollXBuffer.mScaleX == mScaleX) {
                return mMaxScrollXBuffer.mMaxScrollX;
            }
            mMaxScrollXBuffer.mScaleX = mScaleX;
            mMaxScrollXBuffer.mMaxScrollX = Math.round(mDataLength * mScaleX - mMainRenderer.getMainCanvasWidth())
                    + mPaddingHelper.getLeftExtPadding(mScaleX);
            return mMaxScrollXBuffer.mMaxScrollX;
        }
        return 0;
    }

    @Override
    public TouchTapManager<T> getTouchTapManager() {
        return mTouchTapManager;
    }

    /**
     * 设置屏幕显示的item数量，该值为推荐数量，实际以最终计算为准
     * 限制最小值为2，且计算出的单点宽度最小值为3
     */
    public void setPointShowSize(int size) {
        mPointModeValue = Math.max(size, 2);
        mPointMode = POINT_FIXED_SIZE_MODE;
        requestDraw();
    }

    /**
     * 设置单个Item宽度，最小值为3个像素点
     */
    public void setPointWidth(int pointWidth) {
        mPointModeValue = Math.max(pointWidth, 3);
        mPointMode = POINT_FIXED_WIDTH_MODE;
        requestDraw();
    }

    /**
     * 获取经过缩放后的单点宽度
     */
    @Override
    public float getScalePointWidth() {
        if (mScalePointWidthBuffer.mScaleX == mScaleX) {
            return mScalePointWidthBuffer.mScalePointWidth;
        }
        mScalePointWidthBuffer.mScalePointWidth = getPointWidth() * mScaleX;
        mScalePointWidthBuffer.mScaleX = mScaleX;
        return mScalePointWidthBuffer.mScalePointWidth;
    }

    /**
     * 获取单点宽度【注意：非缩放后的值】
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
     * @param index              需要滚动的索引
     * @param inItemWidthPercent 在item的什么位置计算滚动值
     *                           以item的左侧为锚点，inItemWidthPercent = 0.0f
     *                           以item的中间为锚点，inItemWidthPercent = 0.5f
     *                           以item的右侧为锚点，inItemWidthPercent = 1.0f
     * @param inScreenPercent    X轴方向主视窗{@link IMainCanvasPort#getMainCanvasWidth()}所在百分比位置，
     *                           需要滚动到视窗左侧，inScreenPercent = 0.0f
     *                           需要滚动到视窗中间，inScreenPercent = 0.5f
     *                           需要滚动到视窗右侧，inScreenPercent = 1.0f
     */
    public void scrollToIndex(int index, @FloatRange(from = 0, to = 1.0) float inItemWidthPercent,
                              @FloatRange(from = 0, to = 1.0) float inScreenPercent, boolean anim) {
        if (index < 0 || mAdapter == null) {
            index = 0;
        } else if (index >= mAdapter.getCount()) {
            index = mAdapter.getCount() - 1;
        }
        int targetScrollX = mTransformer.getGoToIndexScrollX(index, inItemWidthPercent, inScreenPercent);
        if (anim) {
            animScroll(targetScrollX);
        } else {
            setScroll(targetScrollX);
        }
    }

    public void setAdapter(@Nullable BaseKChartAdapter<T> adapter) {
        if (mAdapter != null) {
            mAdapter.unregisterDataSetObserver(mDataObserver);
        }
        dispatchDetachAdapter();
        mAdapter = adapter;
        if (adapter != null) {
            adapter.registerDataSetObserver(mDataObserver);
        }
        dispatchAttachAdapter();
        requestDraw();
    }

    private void dispatchAttachAdapter() {
        if (mAdapterChangeListeners != null) {
            for (int i = 0; i < mAdapterChangeListeners.size(); i++) {
                mAdapterChangeListeners.get(i).onAttachAdapter(mAdapter);
            }
        }
    }

    private void dispatchDetachAdapter() {
        if (mAdapterChangeListeners != null) {
            for (int i = 0; i < mAdapterChangeListeners.size(); i++) {
                mAdapterChangeListeners.get(i).onDetachAdapter(mAdapter);
            }
        }
    }

    /**
     * 数据数量变更时扩展操作类
     */
    public void setDataSizeChangeHandler(@Nullable IDataInsertedHandler<T> handler) {
        mDataSizeChangeHandler = handler;
    }

    @Override
    public PaddingHelper getPaddingHelper() {
        return mPaddingHelper;
    }

    @Nullable
    @Override
    public BaseKChartAdapter<T> getAdapter() {
        return mAdapter;
    }

    public void prepareIndexData() {
        prepareIndexData(mTransformer.getIndexTags());
    }

    public void prepareIndexData(List<String> indexTags) {
        if (mAdapter.getCount() > 0) {
            mAdapter.prepareIndexData(indexTags);
        }
    }

    private void requestDraw() {
        // 备份上一次数据
        mPreviousIsFullScreen = isFullScreen();
        mPreviousDataLength = mDataLength;

        resetBuffer();
        if (mAdapter == null) {
            mDataLength = 0;
        } else {
            mDataLength = getPointWidth() * mAdapter.getCount();
        }
        if (mPreviousDataLength == 0 && mDataLength == 0) {
            invalidate();
        } else if (mPreviousDataLength == 0) {
            // 从无数据到有数据
            mInRightMargin = false;
            mInLeftMargin = false;
            if (isFullScreen() && getMinScrollX() != mScrollX) {
                setScroll(getMinScrollX());
            } else {
                invalidate();
            }
        } else if (mDataLength == 0) {
            // 从有数据到无数据
            removeTap();
            if (mScrollX == 0) {
                invalidate();
            } else {
                setScroll(0);
            }
        } else {
            // 更新数据
            boolean fullScreen = isFullScreen();
            if (!fullScreen) {
                mInRightMargin = false;
                mInLeftMargin = false;
            }
            mTouchTapManager.updateClickTapInfo();
            if (!mPreviousIsFullScreen && fullScreen) {
                // 非满屏 到 满屏
                setScroll(getMinScrollX());
            } else {
                int oldScrollX = mScrollX;
                int fixScrollX = getFixScrollX(oldScrollX);
                if (fixScrollX == oldScrollX) {
                    invalidate();
                } else {
                    setScroll(fixScrollX);
                }
            }
        }
    }

    private void resetBuffer() {
        mPointWidthBuffer.mHasBuffer = false;
        mScalePointWidthBuffer.mScaleX = 0;
        mIsFullScreenBuffer.mScaleX = 0;
        mMaxScrollXBuffer.mScaleX = 0;
    }

    public void addOnAdapterChangeListener(OnAdapterChangeListener<T> listener) {
        if (listener == null) return;
        if (mAdapterChangeListeners == null) {
            mAdapterChangeListeners = new ArrayList<>();
        }
        mAdapterChangeListeners.add(listener);
    }

    public void removeOnAdapterChangeListener(OnAdapterChangeListener<T> listener) {
        if (listener == null || mAdapterChangeListeners == null) return;
        mAdapterChangeListeners.remove(listener);
    }

    public void setOnPaddingListener(OnPaddingListener listener) {
        mOnPaddingListener = listener;
    }

    public void setOnMarginListener(OnMarginListener listener) {
        mOnMarginListener = listener;
    }

    private final AdapterDataObserver mDataObserver = new AdapterDataObserver() {

        @Override
        public void onChanged(int allCount) {
            prepareIndexData();
            requestDraw();
        }

        @Override
        public void onFirstInserted(int insertedCount) {
            prepareIndexData();
            if (!isFullScreen()) {
                // 原有数据并没有铺满屏幕
                requestDraw();
                return;
            }
            resetBuffer();
            mDataLength = getPointWidth() * mAdapter.getCount();
            mTouchTapManager.updateTapIndexOffset(insertedCount);
            if (mDataSizeChangeHandler == null) {
                invalidate();
                return;
            }
            if (!mDataSizeChangeHandler.onFullFirstInserted(InteractiveKChartView.this,
                    insertedCount, mScrollX, getFinalScroll())) {
                invalidate();
            }
        }

        @Override
        public void onLastUpdated(int index) {
            prepareIndexData();
            invalidate();
        }

        @Override
        public void onLastInserted(int insertedCount) {
            prepareIndexData();
            if (!isFullScreen()) {
                // 原有数据并没有铺满屏幕
                requestDraw();
                return;
            }
            resetBuffer();
            mDataLength = getPointWidth() * mAdapter.getCount();
            if (mDataSizeChangeHandler == null) {
                invalidate();
                return;
            }
            if (!mDataSizeChangeHandler.onFullLastInserted(InteractiveKChartView.this,
                    insertedCount, mScrollX, getFinalScroll())) {
                invalidate();
            }
        }
    };

    public interface OnAdapterChangeListener<T extends IEntity> {

        void onAttachAdapter(@Nullable BaseKChartAdapter<T> adapter);

        void onDetachAdapter(@Nullable BaseKChartAdapter<T> adapter);
    }

    public interface OnPaddingListener {
        /**
         * 到达和离开右padding会触发该函数
         */
        void rightPadding(boolean inRightPadding);

        /**
         * 到达和离开左padding会触发该函数
         */
        void leftPadding(boolean inLeftPadding);
    }

    public interface OnMarginListener {
        /**
         * 到达右边界会触发该函数，
         */
        void onRightMargin();

        /**
         * 到达左边界会触发该函数，
         */
        void onLeftMargin();
    }
}
