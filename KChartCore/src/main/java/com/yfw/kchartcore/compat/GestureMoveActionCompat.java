package com.yfw.kchartcore.compat;

import android.content.Context;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

/**
 * @日期 : 2020/6/30
 * @描述 : 手势滑动判断兼容类
 */
public class GestureMoveActionCompat {

    private int mTouchSlop;             //避免程序识别错误的一个阀值。只有触摸移动的距离大于这个阀值时,才认为是一个有效的移动。

    private int mInterceptStatus = 0;   // 当前滑动的方向。0 无滑动（视为点击）；1 垂直滑动；2 横向滑动

    private float mLastMotionX;         // ACTION_DOWN 事件的坐标 X

    private float mLastMotionY;         // ACTION_DOWN 事件的坐标 Y

    private boolean mDragging = false;

    private OnGestureMoveListener mGestureMoveListener;

    public GestureMoveActionCompat(Context context){
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    public boolean isDragging() {
        return mDragging;
    }

    /**
     * @return 事件是否是横向滑动
     */
    public boolean onTouchEvent(MotionEvent e) {
        float x = e.getX();
        float y = e.getY();
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastMotionY = y;
                mLastMotionX = x;
                mInterceptStatus = 0;
                mDragging = false;
                break;

            case MotionEvent.ACTION_MOVE:
                float deltaY = Math.abs(y - mLastMotionY);
                float deltaX = Math.abs(x - mLastMotionX);

                /*
                 * 如果之前是垂直滑动,即使现在是横向滑动,仍然认为它是垂直滑动的
                 * 如果之前是横向滑动,即使现在是垂直滑动,仍然认为它是横向滑动的
                 * 防止在一个方向上来回滑动时,发生垂直滑动和横向滑动的频繁切换,造成识别错误
                 */
                if (mInterceptStatus != 1 &&
                        (mDragging || deltaX > deltaY && deltaX > mTouchSlop)) {
                    mInterceptStatus = 2;
                    mDragging = true;

                    if (mGestureMoveListener != null) {
                        mGestureMoveListener.onHorizontalMove(e, x, y);
                    }
                } else if (mInterceptStatus != 2 &&
                        (mDragging || deltaX < deltaY && deltaY > mTouchSlop)) {
                    mInterceptStatus = 1;
                    mDragging = true;

                    if (mGestureMoveListener != null) {
                        mGestureMoveListener.onVerticalMove(e, x, y);
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
                mInterceptStatus = 0;
                mDragging = false;
                break;
        }
        return mInterceptStatus == 2;
    }

    public void setGestureMoveListener(OnGestureMoveListener listener){
        mGestureMoveListener = listener;
    }

    public interface OnGestureMoveListener {

        /**
         * 横向移动
         */
        void onHorizontalMove(MotionEvent e, float x, float y);

        /**
         * 垂直移动
         */
        void onVerticalMove(MotionEvent e, float x, float y);
    }
}
