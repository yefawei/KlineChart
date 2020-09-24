package com.benben.kchartlib.index.range;

/**
 * @日期 : 2020/9/24
 * @描述 :指标计算反转，用于实现同一指标不同方向的显示
 */
public class IndexReverse extends IndexRange {

    private IndexRange mIndexRange;

    public IndexReverse(IndexRange indexRange) {
        super();
        if (indexRange == null) {
            throw new IllegalArgumentException("IndexReverse: IndexRange cannot be empty.");
        }
        mIndexRange = indexRange;
    }

    @Override
    public String getIndexTag() {
        return "";
    }

    @Override
    public boolean isReverse() {
        return !mIndexRange.isReverse();
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

    public IndexRange getIndexRange() {
        return mIndexRange;
    }
}
