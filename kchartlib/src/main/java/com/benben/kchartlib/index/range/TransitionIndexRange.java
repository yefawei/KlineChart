package com.benben.kchartlib.index.range;

import android.view.animation.Interpolator;

import com.benben.kchartlib.data.Transformer;

/**
 * @日期 : 2020/9/27
 * @描述 : 最大最小值过渡计算类
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
    private long mDuration;
    private long mStartTime;
    private long mEndTime;
    private long mProcessTime;

    private long mLastProcessTime;
    private float mBufferFraction;

    private Interpolator mInterpolator;

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
        float fraction = getProcessFraction();
        if (fraction == 1.0f) {
            return mIndexRange.getMaxValue();
        }
        return mTransitionMaxValue * fraction + mLastMaxValue;
    }

    @Override
    public float getMinValue() {
        if (!mIsLockChange || getSideMode() == IndexRange.UP_SIDE) {
            return mIndexRange.getMinValue();
        }
        float fraction = getProcessFraction();
        if (fraction == 1.0f) {
            return mIndexRange.getMinValue();
        }
        return mTransitionMinValue * fraction + mLastMinValue;
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
     *
     * @param startTime 锁起始时间
     * @param endTime   锁结束时间
     */
    public void lockChange(long startTime, long endTime) {
        mIsLockChange = true;
        mStartTime = mProcessTime = startTime;
        mEndTime = endTime;
        mDuration = endTime - startTime;
    }

    /**
     * 去锁
     */
    public void unlockChange() {
        mIsLockChange = false;
        mStartTime = 0;
        mDuration = 0;
        mEndTime = 0;
        mProcessTime = 0;
        mLastProcessTime = 0;
    }

    public boolean isLockChange() {
        return mIsLockChange;
    }

    /**
     * 更新当前进度时间
     */
    public void updateProcessTime(long time) {
        mProcessTime = time;
        if (mProcessTime >= mEndTime) {
            mLastMaxValue = mTargetMaxValue;
            mLastMinValue = mTargetMinValue;
            unlockChange();
        }
    }

    /**
     * 获取进度值
     */
    public float getProcessFraction() {
        if (!mIsLockChange) {
            return 1.0f;
        }
        if (mLastProcessTime == mProcessTime) {
            return mBufferFraction;
        }
        mLastProcessTime = mProcessTime;
        float fraction = (mProcessTime - mStartTime) / (float) mDuration;
        if (mInterpolator == null) {
            return mBufferFraction = Math.max(Math.min(fraction, 1.0f), 0.0f);
        }
        return mBufferFraction = mInterpolator.getInterpolation(Math.max(Math.min(fraction, 1.0f), 0.0f));
    }

    public void setInterpolator(Interpolator i) {
        mInterpolator = i;
    }

    public Interpolator getInterpolator() {
        return mInterpolator;
    }
}
