package com.yfw.kchartcore.index.range;


import com.yfw.kchartcore.data.Transformer;

/**
 * @日期 : 2020/9/24
 * @描述 :指标计算反转，用于实现同一指标{@link IndexRange#isReverse}返回值不同
 * 注意：该类不会被{@link Transformer}所计算
 */
public final class ReverseIndexRange extends IndexRange implements IndexRangeContainer, IndexRange.OnCalcValueListener {

    private final IndexRange mIndexRange;

    public ReverseIndexRange(IndexRange indexRange) {
        super();
        if (indexRange == null) {
            throw new IllegalArgumentException("ReverseIndexRange: IndexRange cannot be empty.");
        }
        mIndexRange = indexRange;
        mIndexRange.addOnCalcValueListener(this);
    }

    @Override
    public String getIndexTag() {
        return mIndexRange.getIndexTag();
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

    @Override
    public IndexRange getRealIndexRange() {
        if (mIndexRange instanceof IndexRangeContainer) {
            return ((IndexRangeContainer) mIndexRange).getRealIndexRange();
        }
        return mIndexRange;
    }

    @Override
    public void onResetValue(boolean isEmptyData) {
        dispatchResetValue(isEmptyData);
    }

    @Override
    public void onCalcValueEnd(boolean isEmptyData) {
        dispatchCalcValueEnd(isEmptyData);
    }
}
