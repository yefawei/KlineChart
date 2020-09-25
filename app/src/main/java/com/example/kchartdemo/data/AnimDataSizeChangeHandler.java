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
        int scrollRange = Math.round(view.getScalePointWidth() * itemCount);
        view.setScrollThenAnimScroll(currScroll + scrollRange, finalScroll, getDuration(itemCount), true);
        return true;
    }

    private int getDuration(int itemCount) {
        if (itemCount == 1) {
            return 4000;
        } else if (itemCount == 2) {
            return 6000;
        } else if (itemCount == 3) {
            return 7000;
        } else {
            return 8000;
        }
    }
}
