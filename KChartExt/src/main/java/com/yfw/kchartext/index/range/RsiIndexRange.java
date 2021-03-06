package com.yfw.kchartext.index.range;

import com.yfw.kchartcore.index.IEntity;
import com.yfw.kchartcore.index.range.IndexRange;
import com.yfw.kchartext.index.IRSI;

/**
 * @日期 : 2020/7/10
 * @描述 : RIS 指标计算类
 */
public class RsiIndexRange extends IndexRange {

    public RsiIndexRange() {
        super(0.1f);
    }

    @Override
    public String getIndexTag() {
        return "RSI";
    }

    @Override
    protected float calcMaxValue(int index, float curMaxValue, IEntity entity) {
        if (index > 13) {
            IRSI rsi = (IRSI) entity;
            return Math.max(curMaxValue, Math.max(rsi.getRSI7(), Math.max(rsi.getRSI14(), rsi.getRSI28())));
        }
        return curMaxValue;
    }

    @Override
    protected float calcMinValue(int index, float curMinValue, IEntity entity) {
        if (index > 13) {
            IRSI rsi = (IRSI) entity;
            return Math.min(curMinValue, Math.min(rsi.getRSI7(), Math.min(rsi.getRSI14(), rsi.getRSI28())));
        }
        return curMinValue;
    }
}
