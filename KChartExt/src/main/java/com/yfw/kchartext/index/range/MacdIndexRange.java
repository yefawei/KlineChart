package com.yfw.kchartext.index.range;

import com.yfw.kchartcore.index.IEntity;
import com.yfw.kchartcore.index.range.IndexRange;
import com.yfw.kchartext.index.IMACD;

/**
 * @日期 : 2020/7/10
 * @描述 : MACD 指标计算类
 */
public class MacdIndexRange extends IndexRange {

    public MacdIndexRange() {
        super(0.1f);
    }

    @Override
    public String getIndexTag() {
        return "Macd";
    }

    @Override
    protected float calcMaxValue(int index, float curMaxValue, IEntity entity) {
        IMACD macd = (IMACD) entity;
        if (index > 32) {
            return Math.max(curMaxValue, Math.max(macd.getDIF(), Math.max(macd.getDEA(), macd.getMACD())));
        }
        if (index > 24) {
            return Math.max(curMaxValue, macd.getDIF());
        }
        return curMaxValue;
    }

    @Override
    protected float calcMinValue(int index, float curMinValue, IEntity entity) {
        IMACD macd = (IMACD) entity;
        if (index > 32) {
            return Math.min(curMinValue, Math.min(macd.getDIF(), Math.min(macd.getDEA(), macd.getMACD())));
        }
        if (index > 24) {
            return Math.min(curMinValue, macd.getDIF());
        }
        return curMinValue;
    }
}
