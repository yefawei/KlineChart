package com.benben.kchartlib.index.range;

import com.benben.kchartlib.data.Transformer;
import com.benben.kchartlib.index.IEntity;

/**
 * @日期 : 2020/9/27
 * @描述 : 最大最小值过渡计算类
 * 注意：该类不会被{@link Transformer}所计算
 */
public final class TransitionIndexRange extends IndexRange implements IndexRangeContainer, IndexRange.OnCalcValueEndListener {

    private IndexRange mIndexRange;

    private float mLastMaxValue = Float.NaN;
    private float mLastMinValue = Float.NaN;
    private float mTargetMaxValue;
    private float mTargetMinValue;

    public TransitionIndexRange(IndexRange indexRange) {
        super();
        if (indexRange == null) {
            throw new IllegalArgumentException("ReverseIndexRange: IndexRange cannot be empty.");
        }
        mIndexRange = indexRange;
        mIndexRange.setOnCalcValueEndListener(this);
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

    @Override
    public void onCalcValueEnd() {
        if (Float.isNaN(mLastMaxValue) || Float.isNaN(mLastMinValue)) {
            mLastMaxValue = mTargetMaxValue = mIndexRange.getMaxValue();
            mLastMinValue = mTargetMinValue = mIndexRange.getMinValue();
            if (mOnCalcValueEndListener != null) mOnCalcValueEndListener.onCalcValueEnd();
            return;
        }
        float currMaxValue = mIndexRange.getMaxValue();
        float currMinValue = mIndexRange.getMinValue();
        if (mLastMaxValue != currMaxValue || mLastMinValue != currMinValue) {
            mLastMaxValue = mTargetMaxValue;
            mLastMinValue = mTargetMinValue;
            mTargetMaxValue = currMaxValue;
            mTargetMinValue = currMinValue;
        }
        if (mOnCalcValueEndListener != null) mOnCalcValueEndListener.onCalcValueEnd();
    }

    /**
     * 判断最大最小值是否有变化
     */
    public boolean valueHasChange() {
        if (Float.isNaN(mLastMaxValue) || Float.isNaN(mLastMinValue)) {
            return false;
        }
        return mLastMaxValue != mTargetMaxValue || mLastMinValue != mTargetMinValue;
    }

    public void print() {

    }
}
