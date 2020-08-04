package com.benben.kchartlib.impl;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @日期 : 2020/7/10
 * @描述 :
 */
public interface IDrawingPortLayout extends IPortLayout {

    /**
     * 设置是否在更新绘制范围
     */
    void setInUpdateDrawingPortLayout(boolean inUpdate);

    /**
     * 是否在更新绘制范围
     */
    boolean inUpdateDrawingPortLayout();

    /**
     * 绘制预期布局参数
     */
    class DrawingLayoutParams {
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
        private float mPercent;         // 百分比
        private int mWeight;            // 自适应 注意：如果非垂直布局和非水平布局将会铺满

        // 优先级: 【水平布局/垂直布局】>【相对父布局】
        private boolean mIsHorizontalLinear;    // 是否是水平布局
        private boolean mIsVerticalLinear;      // 是否是垂直布局

        private int mHorizontalPosition = NO_POSITION;        // 相对父布局位置：左 中 右
        private int mVerticalPosition = NO_POSITION;          // 相对父布局位置：上 中 下

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

        public float getPercent() {
            return mPercent;
        }

        public void setPercent(float percent) {
            if (percent > 1.0f) {
                percent = 1.0f;
            } else if (percent < 0) {
                percent = 0;
            }
            mPercent = percent;
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
