package com.benben.kchartlib.index;

/**
 * @日期 : 2020/7/10
 * @描述 : 蜡烛图计算类
 */
public class CandleIndex extends Index {


    public CandleIndex() {
        super(0.1f);
    }

    @Override
    public String getIndexTag() {
        return "Candle";
    }

    @Override
    protected float calcMaxValue(int index, float curMaxValue, IEntity entity) {
        return Math.max(curMaxValue, entity.getHighPrice());
    }

    @Override
    protected float calcMinValue(int index, float curMinValue, IEntity entity) {
        return Math.min(curMinValue, entity.getLowPrice());
    }
}
