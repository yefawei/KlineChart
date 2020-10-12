package com.yfw.kchartcore.layout;

/**
 * @日期 2020/10/12
 * @描述 视图中心点
 */
public class CenterPoint {

    private float mCenterX;
    private float mCenterY;

    public float getCenterX() {
        return mCenterX;
    }

    public void setCenterX(float centerX) {
        mCenterX = centerX;
    }

    public float getCenterY() {
        return mCenterY;
    }

    public void setCenterY(float centerY) {
        mCenterY = centerY;
    }

    /**
     * @param x 需要判断的x坐标
     * @return true: x坐标在视图左边 false: x坐标在视图右边
     */
    public boolean inLeft(float x) {
        return x < mCenterX;
    }

    /**
     * @param y 需要判断的y坐标
     * @return true: y坐标在视图上边 false: y坐标在视图下边
     */
    public boolean inTop(float y) {
        return y < mCenterY;
    }
}
