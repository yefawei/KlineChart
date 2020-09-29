package com.benben.kchartlib.index.range;

import androidx.annotation.FloatRange;

import com.benben.kchartlib.data.Transformer;

/**
 * @日期 : 2020/9/27
 * @描述 : 最大最小值过渡计算辅助类类
 * 注意：该类不会被{@link Transformer}所计算
 */
public final class TransitionIndexRange extends IndexRange implements IndexRangeContainer, IndexRange.OnCalcValueListener {

    private IndexRange mIndexRange;

    private float mLastMaxValue = Float.NaN;
    private float mLastMinValue = Float.NaN;
    private float mTransitionMaxValue;
    private float mTransitionMinValue;
    private float mTargetMaxValue;
    private float mTargetMinValue;

    private boolean mIsLockChange;

    private float mFraction = 1.0f;

    public TransitionIndexRange(IndexRange indexRange) {
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
        if (!mIsLockChange || getSideMode() == IndexRange.DOWN_SIDE) {
            return mIndexRange.getMaxValue();
        }
        if (mFraction == 1.0f) {
            return mIndexRange.getMaxValue();
        }
        return mTransitionMaxValue * mFraction + mLastMaxValue;
    }

    @Override
    public float getMinValue() {
        if (!mIsLockChange || getSideMode() == IndexRange.UP_SIDE) {
            return mIndexRange.getMinValue();
        }
        if (mFraction == 1.0f) {
            return mIndexRange.getMinValue();
        }
        return mTransitionMinValue * mFraction + mLastMinValue;
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
    public void onResetValue(boolean isEmptyData) {
        if (!isEmptyData) return;
        mLastMaxValue = Float.NaN;
        mLastMinValue = Float.NaN;
        mTransitionMaxValue = 0;
        mTransitionMinValue = 0;
        mTargetMaxValue = 0;
        mTargetMinValue = 0;
        unlockChange();
        dispatchResetValue(isEmptyData);
    }

    @Override
    public void onCalcValueEnd() {
        if (Float.isNaN(mLastMaxValue) || Float.isNaN(mLastMinValue)) {
            mLastMaxValue = mTargetMaxValue = mIndexRange.getMaxValue();
            mLastMinValue = mTargetMinValue = mIndexRange.getMinValue();
            dispatchCalcValueEnd();
            return;
        }
        float currMaxValue = mIndexRange.getMaxValue();
        float currMinValue = mIndexRange.getMinValue();
        if (mTargetMaxValue != currMaxValue || mTargetMinValue != currMinValue) {
            if (mIsLockChange) {
                mLastMaxValue = getMaxValue();
                mLastMinValue = getMinValue();
            } else {
                mLastMaxValue = mTargetMaxValue;
                mLastMinValue = mTargetMinValue;
            }
            mTargetMaxValue = currMaxValue;
            mTargetMinValue = currMinValue;
            mTransitionMaxValue = mTargetMaxValue - mLastMaxValue;
            mTransitionMinValue = mTargetMinValue - mLastMinValue;
            unlockChange();
        }
        dispatchCalcValueEnd();
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

    /**
     * 锁住变更，{@link #mLastMaxValue}、{@link #mLastMinValue}、
     * {@link #mTargetMaxValue}和{@link #mTargetMinValue}有变化会自动去锁
     */
    public void lockChange() {
        mIsLockChange = true;
        mFraction = 0;
    }

    /**
     * 去锁
     */
    public void unlockChange() {
        mIsLockChange = false;
        mFraction = 1.0f;
    }

    public boolean isLockChange() {
        return mIsLockChange;
    }

    /**
     * 更新当前进度时间
     */
    public void updateProcess(@FloatRange(from = 0.0, to = 1.0) float fraction) {
        mFraction = fraction;
        if (fraction == 1.0f) {
            mLastMaxValue = mTargetMaxValue;
            mLastMinValue = mTargetMinValue;
            unlockChange();
        }
    }
}
