package com.benben.kchartlib.index.range;

import android.view.animation.Interpolator;
import com.benben.kchartlib.data.Transformer;

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

    private boolean mIsLockChange;
    private long mStartTime;
    private long mProcessTime;
    private long mEndTime;

    private Interpolator mInterpolator;

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
        if (!mIsLockChange) {
            return mIndexRange.getMaxValue();
        }
        float fraction = getProcessFraction();
        if (fraction == 1.0f) {
            return mIndexRange.getMaxValue();
        }
        return (mTargetMaxValue - mLastMaxValue) * fraction + mLastMaxValue;
    }

    @Override
    public float getMinValue() {
        if (!mIsLockChange) {
            return mIndexRange.getMinValue();
        }
        float fraction = getProcessFraction();
        if (fraction == 1.0f) {
            return mIndexRange.getMinValue();
        }
        return (mTargetMinValue - mLastMinValue) * fraction + mLastMinValue;
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
            unlockChange();
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
    }

    /**
     * 去锁
     */
    public void unlockChange() {
        mIsLockChange = false;
        mStartTime = 0;
        mEndTime = 0;
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
        float fraction = (mProcessTime - mStartTime) / (float) (mEndTime - mStartTime);
        if (mInterpolator == null) {
            return Math.max(Math.min(fraction, 1.0f), 0.0f);
        }
        return mInterpolator.getInterpolation(Math.max(Math.min(fraction, 1.0f), 0.0f));
    }

    public void setInterpolator(Interpolator i) {
        mInterpolator = i;
    }

    public Interpolator getInterpolator() {
        return mInterpolator;
    }
}
