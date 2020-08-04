package com.benben.kchartlib.drawing;

import com.benben.kchartlib.animation.Animation;

/**
 * @日期 : 2020/7/10
 * @描述 : 自动执行动画的绘制
 */
public abstract class AutoAnimDrawing extends Drawing implements Animation {

    @Override
    public final boolean isAutoAnim() {
        return true;
    }

    @Override
    public long AnimEndTime() {
        return 0;
    }

    @Override
    public final void setInAnim(boolean inAnim) {

    }

    @Override
    public final boolean inAnim() {
        return false;
    }
}
