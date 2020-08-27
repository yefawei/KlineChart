package com.benben.kchartlib.data;

import com.benben.kchartlib.InteractiveKChartView;

/**
 * @日期 : 2020/8/24
 * @描述 :
 */
public class AnimDataSizeChangeHandler implements IDataSizeChangeHandler {

    @Override
    public boolean onFullFirstInserted(InteractiveKChartView view, int itemCount, int currScroll, int finalScroll) {
        return false;
    }

    @Override
    public boolean onFullLastInserted(InteractiveKChartView view, int itemCount, int currScroll, int finalScroll) {
        int scrollRange = Math.round(view.getScalePointWidth() * itemCount);
        view.setScrollerThenAnimScroll(currScroll + scrollRange, finalScroll);
        return true;
    }
}
