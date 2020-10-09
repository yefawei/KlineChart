package com.yfw.kchartcore.layout;

/**
 * @日期 : 2020/7/7
 * @描述 : 主视图范围
 */
public interface IMainCanvasPort {

    /**
     * 获取主视图左侧位置
     */
    int getMainCanvasLeft();

    /**
     * 获取主视图顶部位置
     */
    int getMainCanvasTop();

    /**
     * 获取主视图右侧位置
     */
    int getMainCanvasRight();

    /**
     * 获取主视图底部位置
     */
    int getMainCanvasBottom();

    /**
     * 获取主视图宽度
     */
    int getMainCanvasWidth();

    /**
     * 获取主视图高度
     */
    int getMainCanvasHeight();

    /**
     * 主视图是否有效
     */
    boolean mainCanvasValid();
}
