package com.benben.kchartlib.index.range;

import com.benben.kchartlib.data.Transformer;
import com.benben.kchartlib.index.IEntity;

/**
 * @日期 : 2020/9/27
 * @描述 : 最大最小值过渡计算类
 * 注意：该类不会被{@link Transformer}所计算
 */
public class TransitionIndexRange extends IndexRange implements IndexRangeContainer {

    private IndexRange mIndexRange;

    public TransitionIndexRange(IndexRange indexRange) {
        super();
        if (indexRange == null) {
            throw new IllegalArgumentException("ReverseIndexRange: IndexRange cannot be empty.");
        }
        mIndexRange = indexRange;
    }

    @Override
    public String getIndexTag() {
        return mIndexRange.getIndexTag();
    }

    @Override
    public boolean isReverse() {
        return mIndexRange.isReverse();
    }

    @Override
    public int getMaxIndex() {
        return mIndexRange.getMaxIndex();
    }

    @Override
    public int getMinIndex() {
        return mIndexRange.getMinIndex();
    }

    @Override
    public float getMaxValue() {
        return mIndexRange.getMaxValue();
    }

    @Override
    public float getMinValue() {
        return mIndexRange.getMinValue();
    }

    @Override
    public int getSideMode() {
        return mIndexRange.getSideMode();
    }

    @Override
    public IndexRange getRealIndexRange() {
        return mIndexRange;
    }
}
