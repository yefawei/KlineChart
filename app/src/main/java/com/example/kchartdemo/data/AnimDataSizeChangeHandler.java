package com.example.kchartdemo.data;


import com.yfw.kchartcore.InteractiveKChartView;
import com.yfw.kchartcore.data.IDataInsertedHandler;

/**
 * @日期 : 2020/8/24
 * @描述 :
 */
public class AnimDataSizeChangeHandler implements IDataInsertedHandler<KlineInfo> {

    @Override
    public boolean onFullFirstInserted(InteractiveKChartView<KlineInfo> view, int itemCount, int currScroll, int finalScroll) {
        return false;
    }

    @Override
    public boolean onFullLastInserted(InteractiveKChartView<KlineInfo> view, int itemCount, int currScroll, int finalScroll) {
        if (itemCount != 1 || (currScroll != view.getMinScrollX() && finalScroll != view.getMinScrollX())) {
            if (itemCount == 1) {
                int scrollRange = Math.round(view.getScalePointWidth() * itemCount);
                view.setScroll(currScroll + scrollRange);
            } else {
                view.invalidate();
            }
        } else {
            int scrollRange = Math.round(view.getScalePointWidth() * itemCount);
            view.setScrollThenAnimScroll(currScroll + scrollRange, finalScroll, 4000, true);
        }
        return true;
    }
}
