package com.benben.kchartlib;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.OverScroller;

import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GestureDetectorCompat;

import com.benben.kchartlib.compat.GestureMoveActionCompat;
import com.benben.kchartlib.compat.ScaleGestureDetectorCompat;
import com.benben.kchartlib.impl.IDataProvider;

/**
 * @日期 : 2020/6/30
 * @描述 :
 */
public abstract class ScrollAndScaleView extends View implements GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener,
        ScaleGestureDetectorCompat.OnScaleGestureListener, IDataProvider {

    private boolean mIsAttachedToWindow;

    private boolean mLongEnable = true;         // 是否支持长按
    private boolean mScrollEnable = true;       // 是否支持滑动
    private boolean mScaleEnable = true;        // 是否支持缩放

    private boolean mOnTouch = false;           // 是否在触摸中
    private boolean mOnMultipleTouch = false;   // 是否在多点触控
    private boolean mOnLongPress = false;       // 是否在长按

    private float mMaxScaleX = 2f;              // 缩放的最大值
    private float mMinScaleX = 0.5f;            // 缩放的最小值
    protected float mScaleX = 1.0f;             // 当前缩放值
    protected int mScrollX = 0;                 // 当前滚动值
    private int mPreviousScrollX = 0;           // 备份上一次滚动值
    protected float mLongTouchX;                // 当前点击的X坐标
    protected float mLongTouchY;                // 当前点击的Y坐标

    private GestureMoveActionCompat mGestureMoveActionCompat;
    private GestureDetectorCompat mGestureDetectorCompat;
    private boolean mIsForceScroller;
    private OverScroller mScroller;
    private ScaleGestureDetectorCompat mScaleGestureDetector;

    public static final int SCROLL_STATE_IDLE = 0;      // 闲置
    public static final int SCROLL_STATE_DRAGGING = 1;  // 拖拽中
    public static final int SCROLL_STATE_SETTLING = 2;  // 自行滑动中

    private boolean mIsScrollerStarted;
    private int mScrollState = SCROLL_STATE_IDLE;

    private OnDoubleClickListener mDoubleClickListener;
    private OnPressChangeListener mPressChangeListener;
    private OnScrollChangeListener mOnScrollChangeListener;

    public ScrollAndScaleView(@NonNull Context context) {
        this(context, null);
    }

    public ScrollAndScaleView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScrollAndScaleView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setClickable(true);
        mGestureMoveActionCompat = new GestureMoveActionCompat(context);
        mGestureDetectorCompat = new GestureDetectorCompat(context, this);
        mScroller = new OverScroller(context, new DecelerateInterpolator());
        mScaleGestureDetector = new ScaleGestureDetectorCompat(context, this);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mIsAttachedToWindow = true;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mIsAttachedToWindow = false;
        mScroller.forceFinished(true);
    }

    @Override
    public void invalidate() {
        if (!mIsAttachedToWindow) return;
        preInvalidate();
        super.invalidate();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (mOnLongPress) {
            getParent().requestDisallowInterceptTouchEvent(true);
        } else if (mScrollEnable) {
            boolean onHorizontalMove = mGestureMoveActionCompat.onTouchEvent(event);
            if (onHorizontalMove && event.getActionMasked() == MotionEvent.ACTION_MOVE) {
                getParent().requestDisallowInterceptTouchEvent(true);
            } else {
                getParent().requestDisallowInterceptTouchEvent(false);
            }
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        if (!mLongEnable && !mScrollEnable && !mScaleEnable) {
//            return super.onTouchEvent(event);
//        }
        mOnMultipleTouch = event.getPointerCount() > 1;
        int action = event.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mOnTouch = true;
                removeTap();
                break;
            case MotionEvent.ACTION_MOVE:
                if (mOnLongPress && !mOnMultipleTouch) {
                    onLongActionMove(event);
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mOnTouch = false;
                cancelLongPress();
                mOnMultipleTouch = false;
                break;
        }
        mScaleGestureDetector.onTouchEvent(event);
        mGestureDetectorCompat.onTouchEvent(event);
        if (!mIsScrollerStarted && (action == MotionEvent.ACTION_UP
                || action == MotionEvent.ACTION_CANCEL)) {
            setScrollState(SCROLL_STATE_IDLE);
        }
        return true;
    }

    @Override
    public void cancelLongPress() {
        if (mOnLongPress && mPressChangeListener != null) {
            mPressChangeListener.onPressChange(this, false);
        }
        mOnLongPress = false;
    }

    @Override
    public void computeScroll() {
        if (!mIsScrollerStarted) {
            return;
        }
        if (mScroller.computeScrollOffset()) {
            if ((mOnTouch && !mIsForceScroller) || !mScrollEnable) {
                mIsScrollerStarted = false;
                mScroller.forceFinished(true);
                return;
            }
            scrollTo(mScroller.getCurrX(), 0);

            if (mScroller.isFinished()) {
                mIsForceScroller = false;
                mIsScrollerStarted = false;
                if (!mOnTouch || mOnLongPress) {
                    /**
                     * 之所有加{@link #mOnTouch}和{@link #mOnLongPress}的判断
                     * 是因为如果是强制滚动状态结束后还在触发滚动，交由触碰逻辑走{@link #onScroll}
                     */
                    setScrollState(SCROLL_STATE_IDLE);
                }
            }
        }
    }

    @Override
    public void scrollBy(int x, int y) {
        scrollTo(mScrollX - x, 0);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (mOnScrollChangeListener != null) {
            mOnScrollChangeListener.onScrollXChange(l, oldl);
        }
    }

    @Override
    public void scrollTo(int x, int y) {
        if (x < getMinScrollX()) {
            x = getMinScrollX();
            mScroller.forceFinished(true);
            onRightMargin();
        } else if (x > getMaxScrollX()) {
            x = getMaxScrollX();
            mScroller.forceFinished(true);
            onLeftMargin();
        }
        mPreviousScrollX = mScrollX;
        mScrollX = x;
        if (!mScroller.isFinished()) {
            if (mScrollX != mPreviousScrollX) {
                setScrollState(SCROLL_STATE_SETTLING);
                onScrollChanged(mScrollX, 0, mPreviousScrollX, 0);
            }
            postInvalidateOnAnimation();
            return;
        }
        if (mScrollX != mPreviousScrollX) {
            if (mOnTouch && !mOnLongPress) {
                /**
                 * 之所以加{@link #mOnTouch}判断，
                 * 是因为在滚动状态下触发{@link OverScroller#forceFinished(boolean)}进入到该逻辑
                 *
                 * 之所以加{@link #mOnLongPress}判断，是因为如果是强制滚动，
                 * 且触发了长按，此时触发{@link OverScroller#forceFinished(boolean)}进入该逻辑，会产生错误的回调
                 */
                setScrollState(SCROLL_STATE_DRAGGING);
            }
            onScrollChanged(mScrollX, 0, mPreviousScrollX, 0);
            invalidate();
        }
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if (mScrollEnable && !mOnMultipleTouch && !mIsForceScroller) {
            scrollBy(Math.round(distanceX), 0);
            return true;
        }
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        if (!mIsForceScroller) {
            /**
             * 在滚动中触碰了屏幕，会触发{@link #computeScroll()}里的强制结束滚动
             * 如果用户通过手势滚动则状态顺势从{@link #SCROLL_STATE_SETTLING}进入{@link #SCROLL_STATE_DRAGGING}
             * 如果用户触发该函数了则将状态变更为{@link #SCROLL_STATE_IDLE}
             */
            setScrollState(SCROLL_STATE_IDLE);
        }
        if (mLongEnable) {
            mOnLongPress = true;
            mLongTouchX = e.getX();
            mLongTouchY = e.getY();
            onLongTap(mLongTouchX, mLongTouchY);
            performLongClick();
            if (mPressChangeListener != null) {
                mPressChangeListener.onPressChange(this, true);
            }
            invalidate();
        }
    }

    /**
     * 长按且移动
     */
    private void onLongActionMove(MotionEvent e) {
        mLongTouchX = e.getX();
        mLongTouchY = e.getY();
        onLongTap(mLongTouchX, mLongTouchY);
        invalidate();
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (mScrollEnable) {
            mScroller.fling(mScrollX, 0, (int) velocityX, 0,
                    Integer.MIN_VALUE, Integer.MAX_VALUE, 0, 0);
            mIsScrollerStarted = true;
            invalidate();
            return true;
        }
        return false;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        if (isClickable()) {
            if (!dispatchSingleTap(Math.round(e.getX()), Math.round(e.getY()))) {
                onSingleTap(e.getX(), e.getY());
                performClick();
            }
            invalidate();
            return true;
        }
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        onDoubleTap(e.getX(), e.getY());
        invalidate();
        if (mDoubleClickListener != null) {
            mDoubleClickListener.onDoubleClick(this);
            return true;
        }
        return false;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetectorCompat detector) {
        return mScaleEnable;
    }

    @Override
    public boolean onScale(ScaleGestureDetectorCompat detector) {
        if (mScaleEnable) {
            float scaleX = mScaleX * detector.getScaleFactor();
            if (mIsForceScroller) return false;
            setScaleX(scaleX, detector.getFocusX(), detector.getFocusY());
            return true;
        }
        return false;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetectorCompat detector) {

    }

    @Override
    public int getScrollState() {
        return mScrollState;
    }

    private void setScrollState(int newState) {
        if (mScrollState == newState) {
            return;
        }
        mScrollState = newState;
        if (mOnScrollChangeListener != null) {
            mOnScrollChangeListener.onScrollStateChanged(newState);
        }
    }

    public void reset(boolean invalidate) {
        mScaleX = 1;
        mPreviousScrollX = mScrollX;
        mScrollX = getMinScrollX();
        if (mScrollX != mPreviousScrollX) {
            onScrollChanged(mScrollX, 0, mPreviousScrollX, 0);
        }
        if (invalidate) {
            invalidate();
        }
    }

    /**
     * 设置是否支持长按
     */
    public void setLongEnable(boolean enable) {
        mLongEnable = enable;
        if (!enable) {
            cancelLongPress();
        }
    }

    /**
     * 是否支持长按
     */
    public boolean isLongEnable() {
        return mLongEnable;
    }

    /**
     * 设置是否支持滑动
     */
    public void setScrollEnable(boolean enable) {
        mScrollEnable = enable;
    }

    /**
     * 是否支持滑动
     */
    public boolean isScrollEnable() {
        return mScrollEnable;
    }

    /**
     * 设置是否支持缩放
     */
    public void setScaleEnable(boolean enable) {
        mScaleEnable = enable;
    }

    /**
     * 是否支持缩放
     */
    public boolean isScaleEnable() {
        return mScaleEnable;
    }

    /**
     * 设置当前缩放值
     */
    public void setScaleX(float scaleX) {
        setScaleX(scaleX, 0.5f);
    }

    /**
     * @param inScreenPercent 缩放焦点所在的屏幕位置百分比
     */
    public void setScaleX(float scaleX, @FloatRange(from = 0, to = 1.0) float inScreenPercent) {
        setScaleX(scaleX, getWidth() / inScreenPercent, -1);
    }

    private void setScaleX(float scaleX, float focusX, float focusY) {
        scaleX = getFixScaleX(scaleX);
        if (scaleX != mScaleX) {
            float oldScale = mScaleX;
            mScaleX = scaleX;
            mPreviousScrollX = mScrollX;
            mScrollX = onScaleChanged(mScaleX, oldScale, focusX, focusY);
            if (mScrollX != mPreviousScrollX) {
                onScrollChanged(mScrollX, 0, mPreviousScrollX, 0);
            }
            invalidate();
        }
    }

    /**
     * 获取修正后的缩放值
     */
    protected float getFixScaleX(float scaleX) {
        if (scaleX < mMinScaleX) {
            return mMinScaleX;
        } else if (scaleX > mMaxScaleX) {
            return mMaxScaleX;
        }
        return scaleX;
    }

    /**
     * 获取当前缩放值
     */
    @Override
    public float getScaleX() {
        return mScaleX;
    }

    /**
     * 设置允许缩小的值
     */
    public void setMinScaleX(float minScaleX) {
        if (minScaleX >= 1.0f || minScaleX <= 0.0f) {
            return;
        }
        mMinScaleX = minScaleX;
        if (mScaleX < mMinScaleX) {
            setScaleX(mMinScaleX);
        }
    }

    /**
     * 获取允许缩小的值
     */
    public float getMinScaleX() {
        return mMinScaleX;
    }

    /**
     * 设置允许放大的值
     */
    public void setMaxScaleX(float maxScaleX) {
        if (maxScaleX <= 1.0f) {
            return;
        }
        mMaxScaleX = maxScaleX;
        if (mScaleX > mMaxScaleX) {
            setScaleX(mMaxScaleX);
        }
    }

    /**
     * 获取允许放大的值
     */
    public float getMaxScaleX() {
        return mMaxScaleX;
    }

    /**
     * 设置滚动值
     */
    public void setScroll(int scrollX) {
        scrollTo(scrollX, 0);
    }

    public void animScroll(int targetScrollX) {
        animScroll(targetScrollX, 400);
    }

    public void animScroll(int targetScrollX, int duration) {
        animScroll(targetScrollX, duration, false);
    }

    /**
     * 从当前滚动值以动画的形式滚动到目标滚动值
     * 如果处于触控状态{@link #mOnTouch}=true,则无滚动效果
     *
     * @param targetScrollX 目标滚动值
     * @param force         是否强制滚动，如果为true，将无视手势进行滚动
     */
    public void animScroll(int targetScrollX, int duration, boolean force) {
        targetScrollX = getFixScrollX(targetScrollX);
        if (targetScrollX == mScrollX || (mOnTouch && !force)) return;
        mIsForceScroller = force;
        mScroller.startScroll(mScrollX, 0, targetScrollX - mScrollX, 0, duration);
        mIsScrollerStarted = true;
        invalidate();
    }

    public void setScrollThenAnimScroll(int newScrollX, int targetScrollX) {
        setScrollThenAnimScroll(newScrollX, targetScrollX, 400);
    }

    public void setScrollThenAnimScroll(int newScrollX, int targetScrollX, int duration) {
        setScrollThenAnimScroll(newScrollX, targetScrollX, duration, false);
    }

    /**
     * 更新当前滚动值并滑动到目标滚动值
     * 如果处于触控状态{@link #mOnTouch}=true,则无滚动效果
     *
     * @param newScrollX    新滚动值
     * @param targetScrollX 目标滚动值
     * @param force         是否强制滚动，如果为true，将无视手势进行滚动
     */
    public void setScrollThenAnimScroll(int newScrollX, int targetScrollX, int duration, boolean force) {
        if (newScrollX == targetScrollX || (mOnTouch && !force)) {
            if (newScrollX == mScrollX) return;
            setScroll(newScrollX);
        } else {
            mPreviousScrollX = mScrollX;
            mScrollX = getFixScrollX(newScrollX);
            if (mScrollX != mPreviousScrollX) {
                onScrollChanged(mScrollX, 0, mPreviousScrollX, 0);
            }
            animScroll(targetScrollX, duration, force);
        }
    }

    /**
     * 获取修正后的滚动值
     */
    protected int getFixScrollX(int scrollX) {
        if (scrollX < getMinScrollX()) {
            return getMinScrollX();
        } else if (scrollX > getMaxScrollX()) {
            return getMaxScrollX();
        }
        return scrollX;
    }

    /**
     * 滚动辅助函数，如果处于动画滚动状态，返回目标滚动值，否则返回当前滚动值
     */
    protected final int getFinalScroll() {
        if (mScroller.isFinished()) {
            return mScrollX;
        }
        return mScroller.getFinalX();
    }

    /**
     * 获取当前滚动值
     */
    @Override
    public float getScroll() {
        return mScrollX;
    }

    @Override
    public float getLongTouchY() {
        return mLongTouchY;
    }

    @Override
    public float getLongTouchX() {
        return mLongTouchX;
    }

    /**
     * 是否处在触碰状态
     */
    @Override
    public boolean isOnTouch() {
        return mOnTouch;
    }

    /**
     * 是否处在多点触碰状态
     */
    @Override
    public boolean isOnMultipleTouch() {
        return mOnMultipleTouch;
    }

    /**
     * 是否处于长按状态
     */
    @Override
    public boolean isOnLongPress() {
        return mOnLongPress;
    }

    /**
     * 双击监听
     */
    public void setOnDoubleClickListener(OnDoubleClickListener listener) {
        mDoubleClickListener = listener;
    }

    /**
     * 长按状态变更监听
     */
    public void setOnPressChangeListener(OnPressChangeListener listener) {
        mPressChangeListener = listener;
    }

    /**
     * 设置滚动监听
     */
    public void setOnScrollChangeListener(OnScrollChangeListener listener) {
        mOnScrollChangeListener = listener;
    }

    /**
     * 正在调用Invalidate
     */
    abstract void preInvalidate();

    /**
     * 缩放值有变化
     *
     * @return 缩放后的滚动值
     */
    abstract int onScaleChanged(float scale, float oldScale, float focusX, float focusY);

    /**
     * 滑到了最右边
     */
    abstract public void onRightMargin();

    /**
     * 滑到了最左边
     */
    abstract public void onLeftMargin();

    /**
     * 分发单次点击
     *
     * @return 点击事件由绘制处理则为true
     */
    abstract boolean dispatchSingleTap(int x, int y);

    /**
     * 单次点击
     */
    abstract void onSingleTap(float x, float y);

    /**
     * 双击
     */
    abstract void onDoubleTap(float x, float y);

    /**
     * 长按
     */
    abstract void onLongTap(float x, float y);

    /**
     * 按下的那一刻
     */
    abstract void removeTap();

    /**
     * 双击监听
     */
    public interface OnDoubleClickListener {

        void onDoubleClick(View v);
    }

    /**
     * 长按状态变更监听
     */
    public interface OnPressChangeListener {

        void onPressChange(View v, boolean onLongpress);
    }

    public interface OnScrollChangeListener {

        void onScrollXChange(int scrollX, int oldScrollX);

        /**
         * @param state 当前滚动状态
         * @see ScrollAndScaleView#SCROLL_STATE_IDLE
         * @see ScrollAndScaleView#SCROLL_STATE_DRAGGING
         * @see ScrollAndScaleView#SCROLL_STATE_SETTLING
         */
        void onScrollStateChanged(int state);
    }
}
