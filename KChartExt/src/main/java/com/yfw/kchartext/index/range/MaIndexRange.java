package com.yfw.kchartext.index.range;

import com.yfw.kchartcore.index.IEntity;
import com.yfw.kchartcore.index.range.IndexRange;
import com.yfw.kchartext.index.IMA;

/**
 * @日期 : 2020/7/10
 * @描述 : MA 指标计算类
 */
public class MaIndexRange extends IndexRange {

    public MaIndexRange() {
        super( 0.1f);
    }

    @Override
    public String getIndexTag() {
        return "MA";
    }

    @Override
    protected float calcMaxValue(int index, float curMaxValue, IEntity entity) {
        IMA ma = (IMA) entity;
        if (index > 28) {
            return Math.max(curMaxValue, Math.max(ma.getMA10(), ma.getMA30()));
        }
        if (index > 5) {
            return Math.max(curMaxValue, ma.getMA10());
        }
        return curMaxValue;
    }

    @Override
    protected float calcMinValue(int index, float curMinValue, IEntity entity) {
        IMA ma = (IMA) entity;
        if (index > 28) {
            return Math.min(curMinValue, Math.min(ma.getMA10(), ma.getMA30()));
        }
        if (index > 5) {
            return Math.min(curMinValue, ma.getMA10());
        }
        return curMinValue;
    }
}
