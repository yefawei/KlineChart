package com.benben.kchartlib.index.range;

import com.benben.kchartlib.index.IEntity;
import com.benben.kchartlib.index.IVolume;

/**
 * @日期 : 2020/7/9
 * @描述 : 成交量计算类
 */
public class VolumeIndexRange extends IndexRange {

    public VolumeIndexRange() {
        super(false, IndexRange.UP_SIDE, 0.1f, 0.0f);
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
