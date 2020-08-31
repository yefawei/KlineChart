package com.benben.kchartlib.impl;

import androidx.annotation.Nullable;

import com.benben.kchartlib.adapter.BaseKChartAdapter;
import com.benben.kchartlib.animation.AnimationManager;
import com.benben.kchartlib.data.PaddingHelper;
import com.benben.kchartlib.data.Transformer;
import com.benben.kchartlib.overlay.OverlayManager;

/**
 * @日期 : 2020/7/7
 * @描述 :
 */
public interface IDataProvider {

    boolean isOnTouch();

    boolean isOnMultipleTouch();

    boolean isOnLongPress();

    float getScroll();

    float getScaleX();

    float getLongTouchX();

    float getLongTouchY();

    PaddingHelper getPaddingHelper();

    @Nullable
    BaseKChartAdapter getAdapter();

    int getPointWidth();

    float getScalePointWidth();

    boolean isFullScreen();

    OverlayManager getOverlayManager();

    Transformer getTransformer();

    AnimationManager.ChartAnimtion getChartAnimation();

    IMainCanvasPort getMainCanvasPort();
}
