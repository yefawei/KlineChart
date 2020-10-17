package com.yfw.kchartcore.impl;

import androidx.annotation.Nullable;

import com.yfw.kchartcore.adapter.BaseKChartAdapter;
import com.yfw.kchartcore.animation.AnimationManager;
import com.yfw.kchartcore.data.Transformer;
import com.yfw.kchartcore.helper.PaddingHelper;
import com.yfw.kchartcore.index.IEntity;
import com.yfw.kchartcore.layout.IMainCanvasPort;
import com.yfw.kchartcore.touch.TouchTapManager;

/**
 * @日期 : 2020/7/7
 * @描述 :
 */
public interface IDataProvider<T extends IEntity> {

    /**
     * 是否处在触碰状态
     */
    boolean isOnTouch();

    /**
     * 是否处在多点触碰状态
     */
    boolean isOnMultipleTouch();

    /**
     * 是否处于长按状态
     */
    boolean isOnLongPress();

    /**
     * 获取当前滚动状态
     */
    int getScrollState();

    /**
     * 获取当前滚动值
     */
    int getScroll();

    /**
     * 获取当前缩放值
     */
    float getScaleX();

    PaddingHelper getPaddingHelper();

    @Nullable
    BaseKChartAdapter<T> getAdapter();

    /**
     * 获取单点宽度
     */
    int getPointWidth();

    /**
     * 获取经过缩放后的单点宽度
     */
    float getScalePointWidth();

    /**
     * 数据是否满屏
     */
    boolean isFullScreen();

    /**
     * 获取内容可滚动的最小值
     */
    int getMinScrollX();

    /**
     * 获取内容可滚动的最大值
     */
    int getMaxScrollX();

    /**
     * 获取触碰事件管理者
     */
    TouchTapManager<T> getTouchTapManager();

    /**
     * 获取坐标计算类
     */
    Transformer<T> getTransformer();

    /**
     * 获取动画管理者
     */
    AnimationManager.ChartAnimtion getChartAnimation();

    /**
     * 获取主视图视窗范围
     */
    IMainCanvasPort getMainCanvasPort();
}
