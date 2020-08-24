package com.benben.kchartlib.data;

import com.benben.kchartlib.InteractiveKChartView;

/**
 * @日期 : 2020/8/24
 * @描述 :
 */
public class AnimDataSizeChangeHandler implements IDataSizeChangeHandler {

    @Override
    public boolean onFirstInserted(InteractiveKChartView view, int itemCount, int currScroll, int finalScroll) {
        return false;
    }

    @Override
    public boolean onLastInserted(InteractiveKChartView view, int itemCount, int currScroll, int finalScroll) {
        if (!view.isFullScreen()) {
            return false;
        }
        int scrollRange = Math.round(view.getScalePointWidth() * itemCount);
        view.setScrollerThenAnimScroll(currScroll + scrollRange, finalScroll);
        return true;
    }
}
