package com.benben.kchartlib;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.OverScroller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GestureDetectorCompat;

import com.benben.kchartlib.compat.GestureMoveActionCompat;
import com.benben.kchartlib.compat.ScaleGestureDetectorCompat;
import com.benben.kchartlib.data.PaddingHelper;
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

    private float mScaleXMax = 2f;              // 缩放的最大值
    private float mScaleXMin = 0.5f;            // 缩放的最小值
    protected float mScaleX = 1.0f;             // 当前缩放值
    protected int mScrollX = 0;                 // 当前滚动值
    protected float mTouchX;                    // 当前点击的X坐标
    protected float mTouchY;                    // 当前点击的Y坐标
    protected PaddingHelper mPaddingHelper;     // 边界辅助类

    private GestureMoveActionCompat mGestureMoveActionCompat;
    private GestureDetectorCompat mGestureDetectorCompat;
    private OverScroller mScroller;
    private ScaleGestureDetectorCompat mScaleGestureDetector;

    public ScrollAndScaleView(@NonNull Context context) {
        this(context, null);
    }

    public ScrollAndScaleView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScrollAndScaleView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setClickable(true);
        mPaddingHelper = new PaddingHelper();
        mGestureMoveActionCompat = new GestureMoveActionCompat(context);
        mGestureDetectorCompat = new GestureDetectorCompat(context, this);
        mScroller = new OverScroller(context);
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
        if (!mLongEnable && !mScrollEnable && !mScaleEnable) {
            return super.onTouchEvent(event);
        }
        mOnMultipleTouch = event.getPointerCount() > 1;
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mOnTouch = true;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mOnLongPress && !mOnMultipleTouch) {
                    onLongPress(event);
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mOnTouch = false;
                mOnLongPress = false;
                mOnMultipleTouch = false;
                break;
        }
        mScaleGestureDetector.onTouchEvent(event);
        mGestureDetectorCompat.onTouchEvent(event);
        return true;
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            if (mOnTouch || !mScrollEnable) {
                mScroller.forceFinished(true);
            } else {
                scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            }
        }
    }

    @Override
    public void scrollBy(int x, int y) {
        scrollTo(mScrollX - x, 0);
    }

    @Override
    public void scrollTo(int x, int y) {
        int oldX = mScrollX;
        mScrollX = x;
        if (mScrollX < getMinScrollX()) {
            mScrollX = getMinScrollX();
            //TODO 到达最小值,即最右边
            mScroller.forceFinished(true);
        } else if (mScrollX > getMaxScrollX()) {
            mScrollX = getMaxScrollX();
            //TODO 到达最大值,即最左边
            mScroller.forceFinished(true);
        }
        if (!mScroller.isFinished()) {
            onScrollChanged(mScrollX, 0, oldX, 0);
            postInvalidateOnAnimation();
            return;
        }
        if (mScrollX != oldX) {
            onScrollChanged(mScrollX, 0, oldX, 0);
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
        if (mScrollEnable && !mOnMultipleTouch) {
            scrollBy(Math.round(distanceX), 0);
            return true;
        }
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        if (mLongEnable) {
            mOnLongPress = true;
            mTouchX = e.getX();
            mTouchY = e.getY();
            invalidate();
        }
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (mScrollEnable) {
            mScroller.fling(mScrollX, 0, (int) velocityX, 0,
                    Integer.MIN_VALUE, Integer.MAX_VALUE, 0, 0);
            invalidate();
            return true;
        }
        return false;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        if (isClickable()) {
            //TODO 这里要判断是否会被自己消费掉
            performClick();
            return true;
        }
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        boolean result = false;
        if (mScaleGestureDetector.isQuickScaleEnabled()) {
            mScaleGestureDetector.setOnDoubleTapEvent(e);
            result = true;
        }
        //TODO 双击回调
        return result;
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
            float oldScale = mScaleX;
            mScaleX *= detector.getScaleFactor();
            if (mScaleX < mScaleXMin) {
                mScaleX = mScaleXMin;
            } else if (mScaleX > mScaleXMax) {
                mScaleX = mScaleXMax;
            }
            if (oldScale != mScaleX) {
                float focusX = detector.getFocusX();
                float focusY = detector.getFocusY();
                int scrollX = onScaleChanged(mScaleX, oldScale, focusX, focusY);
                mScrollX = getFixScrollX(scrollX);
                invalidate();
            }
            return true;
        }
        return false;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetectorCompat detector) {

    }

    public void reset(boolean invalidate) {
        mScaleX = 1;
        mScrollX = 0;
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
            mOnLongPress = false;
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
    public void setScaleX(float scale) {
        float oldScale = mScaleX;
        mScaleX = scale;
        if (mScaleX < mScaleXMin) {
            mScaleX = mScaleXMin;
        } else if (mScaleX > mScaleXMax) {
            mScaleX = mScaleXMax;
        }
        if (mScaleX != oldScale) {
            int scrollX = onScaleChanged(mScaleX, oldScale, -1, -1);
            mScrollX = getFixScrollX(scrollX);
            invalidate();
        }
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
    public void setScaleXMin(float scaleXMin) {
        if (scaleXMin >= 1.0f || scaleXMin <= 0.0f) {
            return;
        }
        mScaleXMin = scaleXMin;
        if (mScaleX < mScaleXMin) {
            setScaleX(mScaleXMin);
        }
    }

    /**
     * 获取允许缩小的值
     */
    public float getScaleXMin() {
        return mScaleXMin;
    }

    /**
     * 设置允许放大的值
     */
    public void setScaleXMax(float scaleXMax) {
        if (scaleXMax <= 1.0f) {
            return;
        }
        mScaleXMax = scaleXMax;
        if (mScaleX > mScaleXMax) {
            setScaleX(mScaleXMax);
        }
    }

    /**
     * 获取允许放大的值
     */
    public float getScaleXMax() {
        return mScaleXMax;
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

    /**
     * 从当前滚动值以动画的形式滚动到目标滚动值
     * 如果处于触控状态{@link #mOnTouch}=true,则无滚动效果
     *
     * @param targetScrollX 目标滚动值
     */
    public void animScroll(int targetScrollX, int duration) {
        if (mOnTouch || targetScrollX == mScrollX) return;
        targetScrollX = getFixScrollX(targetScrollX);
        mScroller.startScroll(mScrollX, 0, targetScrollX - mScrollX, 0, duration);
        invalidate();
    }


    public void setScrollerThenAnimScroll(int newScrollX, int targetScrollX) {
        setScrollerThenAnimScroll(newScrollX, targetScrollX, 400);
    }

    /**
     * 更新当前滚动值并滑动到目标滚动值
     * 如果处于触控状态{@link #mOnTouch}=true,则无滚动效果
     *
     * @param newScrollX    新滚动值
     * @param targetScrollX 目标滚动值
     */
    public void setScrollerThenAnimScroll(int newScrollX, int targetScrollX, int duration) {
        newScrollX = getFixScrollX(newScrollX);
        targetScrollX = getFixScrollX(targetScrollX);
        if (mOnTouch || newScrollX == targetScrollX) {
            if (newScrollX == mScrollX) return;
            mScrollX = newScrollX;
            invalidate();
            return;
        }
        mScrollX = newScrollX;
        animScroll(targetScrollX, duration);
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
     * 滚动辅助函数，如果处于动画滚动状态的目标滚动值，否则返回当前滚动值
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
    public float getTouchY() {
        return mTouchY;
    }

    @Override
    public float getTouchX() {
        return mTouchX;
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

    @Override
    public PaddingHelper getPaddingHelper() {
        return mPaddingHelper;
    }

    /**
     * 正在调用Invalidate
     */
    abstract void preInvalidate();

    /**
     * 获取内容可滚动的最小值
     */
    abstract int getMinScrollX();

    /**
     * 获取内容可滚动的最大值
     */
    abstract int getMaxScrollX();

    /**
     * 缩放值有变化
     *
     * @return 缩放后的滚动值
     */
    abstract int onScaleChanged(float scale, float oldScale, float focusX, float focusY);
}
