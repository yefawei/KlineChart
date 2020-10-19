package com.yfw.kchartcore.index.range;

import com.yfw.kchartcore.index.IEntity;

/**
 * @日期 2020/10/19
 * @描述 分时线计算类
 */
public class TimeLineIndexRange extends IndexRange {
    public TimeLineIndexRange() {
        super(0.1f);
    }

    @Override
    public String getIndexTag() {
        return "TimeLine";
    }

    @Override
    protected float calcMaxValue(int index, float curMaxValue, IEntity entity) {
        return Math.max(curMaxValue, entity.getClosePrice());
    }

    @Override
    protected float calcMinValue(int index, float curMinValue, IEntity entity) {
        return Math.min(curMinValue, entity.getClosePrice());
    }
}
