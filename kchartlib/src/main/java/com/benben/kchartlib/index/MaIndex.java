package com.benben.kchartlib.index;

/**
 * @日期 : 2020/7/10
 * @描述 : MA计算类
 */
public class MaIndex extends Index {

    public MaIndex() {
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
            return Math.max(curMaxValue, Math.max(ma.getMA7(), ma.getMA30()));
        }
        if (index > 5) {
            return Math.max(curMaxValue, ma.getMA7());
        }
        return curMaxValue;
    }

    @Override
    protected float calcMinValue(int index, float curMinValue, IEntity entity) {
        IMA ma = (IMA) entity;
        if (index > 28) {
            return Math.min(curMinValue, Math.min(ma.getMA7(), ma.getMA30()));
        }
        if (index > 5) {
            return Math.min(curMinValue, ma.getMA7());
        }
        return curMinValue;
    }
}
