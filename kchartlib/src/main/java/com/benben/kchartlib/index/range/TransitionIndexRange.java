package com.benben.kchartlib.index.range;

import com.benben.kchartlib.index.IEntity;

/**
 * @日期 : 2020/9/27
 * @描述 : 最大最小值过渡计算类
 */
public abstract class TransitionIndexRange extends IndexRange {

    private IndexRange mIndexRange;

    public TransitionIndexRange(IndexRange indexRange) {
        super();
        if (indexRange == null) {
            throw new IllegalArgumentException("IndexReverse: IndexRange cannot be empty.");
        }
        mIndexRange = indexRange;
    }

    public TransitionIndexRange(IndexRange indexRange, float paddingPercent) {
        super(paddingPercent);
        if (indexRange == null) {
            throw new IllegalArgumentException("IndexReverse: IndexRange cannot be empty.");
        }
        mIndexRange = indexRange;
    }

    public TransitionIndexRange(IndexRange indexRange, boolean reverse, int sideMode, float paddingPercent, float startValue) {
        super(reverse, sideMode, paddingPercent, startValue);
        if (indexRange == null) {
            throw new IllegalArgumentException("IndexReverse: IndexRange cannot be empty.");
        }
        mIndexRange = indexRange;
    }

    @Override
    public boolean isReverse() {
        return mIndexRange.isReverse();
    }

    @Override
    public int getSideMode() {
        return mIndexRange.getSideMode();
    }

    @Override
    public void resetValue() {
        mIndexRange.resetValue();
    }

    @Override
    public void addExtendedCalcData(String key, Object obj) {
        mIndexRange.addExtendedCalcData(key, obj);
    }

    @Override
    public void chearExtendedCalcData() {
        mIndexRange.chearExtendedCalcData();
    }

    @Override
    public void calcExtendedData() {
        mIndexRange.calcExtendedData();
    }

    @Override
    public void calcMinMaxValue(int index, IEntity entity) {
        mIndexRange.calcMinMaxValue(index, entity);
    }

    @Override
    public void calcPaddingValue() {
        mIndexRange.calcPaddingValue();
    }

    @Override
    public void calcValueEnd() {
        mIndexRange.calcValueEnd();
        //TODO 这里备份
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
    public int getMaxIndex() {
        return mIndexRange.getMaxIndex();
    }

    @Override
    public int getMinIndex() {
        return mIndexRange.getMinIndex();
    }

    @Override
    protected float calcExtendedMaxValue(float curMaxValue, String key, Object obj) {
        return mIndexRange.calcExtendedMaxValue(curMaxValue, key, obj);
    }

    @Override
    protected float calcExtendedMinValue(float curMinValue, String key, Object obj) {
        return mIndexRange.calcExtendedMinValue(curMinValue, key, obj);
    }

    @Override
    protected float calcMaxValue(int index, float curMaxValue, IEntity entity) {
        return mIndexRange.calcMaxValue(index, curMaxValue, entity);
    }

    @Override
    protected float calcMinValue(int index, float curMinValue, IEntity entity) {
        return mIndexRange.calcMinValue(index, curMinValue, entity);
    }

    @Override
    public String getIndexTag() {
        return mIndexRange.getIndexTag();
    }

    public IndexRange getIndexRange() {
        return mIndexRange;
    }
}
