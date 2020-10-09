package com.yfw.kchartcore.layout;

/**
 * @日期 : 2020/7/3
 * @描述 : 视图范围
 */
public interface IViewPort {

    /**
     * 获取视图左侧位置
     */
    int getLeft();

    /**
     * 获取视图顶部位置
     */
    int getTop();

    /**
     * 获取视图右侧位置
     */
    int getRight();

    /**
     * 获取视图底部位置
     */
    int getBottom();

    /**
     * 获取视图宽度
     */
    int getWidth();

    /**
     * 获取视图高度
     */
    int getHeight();

    /**
     * 更新视图可绘制区域
     */
    void updateViewPort(int left, int top, int right, int bottom);

    /**
     * 返回视图范围信息
     */
    String toViewPortString();
}
