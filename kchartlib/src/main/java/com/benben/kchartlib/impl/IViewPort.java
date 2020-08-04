package com.benben.kchartlib.impl;

/**
 * @日期 : 2020/7/3
 * @描述 :
 */
public interface IViewPort {

    int getLeft();

    int getTop();

    int getRight();

    int getBottom();

    int getWidth();

    int getHeight();

    /**
     * 更新视图可绘制区域
     */
    void updateViewPort(int left, int top, int right, int bottom);

    String toViewPortString();
}
