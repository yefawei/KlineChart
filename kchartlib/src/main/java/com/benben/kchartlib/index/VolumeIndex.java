package com.benben.kchartlib.index;

/**
 * @日期 : 2020/7/9
 * @描述 : 成交量计算类
 */
public class VolumeIndex extends Index {

    public VolumeIndex() {
        super(false, Index.UP_SIDE, 0.1f, 0.0f);
    }

    @Override
    public String getIndexTag() {
        return "Volume";
    }

    @Override
    protected float calcMaxValue(int index, float curMaxValue, IEntity entity) {
        return Math.max(((IVolume) entity).getVolume(), curMaxValue);
    }
}
