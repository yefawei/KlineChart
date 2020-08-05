package com.benben.kchartlib.impl;

import androidx.annotation.Nullable;

import com.benben.kchartlib.adapter.IAdapter;
import com.benben.kchartlib.animation.AnimationManager;
import com.benben.kchartlib.data.Transformer;

/**
 * @日期 : 2020/7/7
 * @描述 :
 */
public interface IDataProvider {

    boolean isOnTouch();

    boolean isOnMultipleTouch();

    boolean isOnLongPress();

    int getScroll();

    float getScaleX();

    float getTouchX();

    float getTouchY();

    @Nullable
    IAdapter getAdapter();

    int getPointWidth();

    float getScalePointWidth();

    boolean isFullScreen();

    Transformer getTransformer();

    AnimationManager.ChartAnimtion getChartAnimation();
}
