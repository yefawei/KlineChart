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
        view.setScrollThenAnimScroll(currScroll + scrollRange, finalScroll, getDuration(itemCount));
        return true;
    }

    private int getDuration(int itemCount) {
        if (itemCount == 1) {
            return 400;
        } else if (itemCount == 2) {
            return 600;
        } else if (itemCount == 3) {
            return 700;
        } else {
            return 800;
        }
    }
}
