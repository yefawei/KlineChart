package com.example.kchartdemo.data;

import com.benben.kchartlib.InteractiveKChartView;
import com.benben.kchartlib.data.IDataSizeChangeHandler;

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
        if ((currScroll != view.getMinScrollX() && finalScroll != view.getMinScrollX()) || itemCount != 1) {
            int scrollRange = Math.round(view.getScalePointWidth() * itemCount);
            view.setScroll(currScroll + scrollRange);
        } else {
            int scrollRange = Math.round(view.getScalePointWidth() * itemCount);
            view.setScrollThenAnimScroll(currScroll + scrollRange, finalScroll, 4000, true);
        }
        return true;
    }
}
