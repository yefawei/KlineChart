package com.benben.kchartlib.index;

/**
 * @日期 : 2020/7/10
 * @描述 :
 */
public class KdjIndex extends Index {

    public KdjIndex() {
        super(0.1f);
    }

    @Override
    public String getIndexTag() {
        return "KDJ";
    }

    @Override
    protected float calcMaxValue(int index, float curMaxValue, IEntity entity) {
        if (index > 7) {
            IKDJ kdj = (IKDJ) entity;
            return Math.max(curMaxValue, Math.max(kdj.getK(), Math.max(kdj.getD(), kdj.getJ())));
        }
        return curMaxValue;
    }

    @Override
    protected float calcMinValue(int index, float curMinValue, IEntity entity) {
        if (index > 7) {
            IKDJ kdj = (IKDJ) entity;
            return Math.min(curMinValue, Math.min(kdj.getK(), Math.min(kdj.getD(), kdj.getJ())));
        }
        return curMinValue;
    }
}
