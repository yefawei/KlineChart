package com.benben.kchartlib.index.range;

import androidx.annotation.IntDef;

import com.benben.kchartlib.index.IEntity;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;

/**
 * @日期 : 2020/7/9
 * @描述 : 指标基础计算类
 */
public abstract class IndexRange {

    public static final int DOUBLE_SIDE = 0;    // 含最大值最小值
    public static final int UP_SIDE = 1;        // 只含最大值
    public static final int DOWN_SIDE = 2;      // 只含最小值

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({DOUBLE_SIDE, UP_SIDE, DOWN_SIDE})
    public @interface SideMode {
    }

    private boolean mReverse;   // 是否反向，最小值在上，最大值在下
    private int mSideMode;
    private float mPaddingPercent;
    private float mMaxValue = Float.MIN_VALUE;
    private float mMinValue = Float.MAX_VALUE;
    private int mMaxIndex; // 屏幕内最大值的索引
    private int mMinIndex; // 屏幕内最小值的索引

    private ArrayList<IEntity> mExtendedData = new ArrayList<>();

    public IndexRange() {
        this(0);
    }

    public IndexRange(float paddingPercent) {
        this(false, IndexRange.DOUBLE_SIDE, 0, 0);
    }

    /**
     * @param reverse        是否反转
     * @param sideMode       方向
     * @param paddingPercent 预留空间
     * @param startValue     非双向模式的初始化值
     */
    public IndexRange(boolean reverse, @SideMode int sideMode, float paddingPercent, float startValue) {
        mReverse = reverse;
        mSideMode = sideMode;
        if (paddingPercent < 0.0f) {
            paddingPercent = 0.0f;
        } else {
            if (sideMode == DOUBLE_SIDE) {
                if (paddingPercent > 0.45f) {
                    paddingPercent = 0.45f;
                }
            } else {
                if (paddingPercent > 0.9f) {
                    paddingPercent = 0.9f;
                }
            }
        }
        mPaddingPercent = paddingPercent;
        if (mSideMode != DOUBLE_SIDE) {
            mMaxValue = mMinValue = startValue;
        }
    }

    public boolean isReverse() {
        return mReverse;
    }

    public int getSideMode() {
        return mSideMode;
    }

    public void resetValue() {
        if (mSideMode == DOUBLE_SIDE) {
            mMaxValue = Float.MIN_VALUE;
            mMinValue = Float.MAX_VALUE;
        } else if (mSideMode == UP_SIDE) {
            mMaxValue = mMinValue;
        } else {
            mMinValue = mMaxValue;
        }
        mMaxIndex = -1;
        mMinIndex = -1;
    }

    public void addExtendedData(IEntity... entitys) {
        for (IEntity entity : entitys) {
            //一般值比较小，所以采用add方式
            mExtendedData.add(entity);
        }
    }

    public void chearExtendedData() {
        mExtendedData.clear();
    }

    public void calcExtendedData() {
        for (IEntity entity : mExtendedData) {
            if (mSideMode == DOUBLE_SIDE) {
                mMaxValue = calcMaxValue(-1, mMaxValue, entity);
                mMinValue = calcMinValue(-1, mMinValue, entity);
            } else if (mSideMode == UP_SIDE) {
                mMaxValue = calcMaxValue(-1, mMaxValue, entity);
            } else {
                mMinValue = calcMinValue(-1, mMinValue, entity);
            }
        }
    }

    public void calcMinMaxValue(int index, IEntity entity) {
        if (mSideMode == DOUBLE_SIDE) {
            float maxValue = calcMaxValue(index, mMaxValue, entity);
            float minValue = calcMinValue(index, mMinValue, entity);
            if (maxValue != mMaxValue) {
                mMaxValue = maxValue;
                mMaxIndex = index;
            }
            if (minValue != mMinValue) {
                mMinValue = minValue;
                mMinIndex = index;
            }
        } else if (mSideMode == UP_SIDE) {
            float maxValue = calcMaxValue(index, mMaxValue, entity);
            if (maxValue != mMaxValue) {
                mMaxValue = maxValue;
                mMaxIndex = index;
            }
        } else {
            float minValue = calcMinValue(index, mMinValue, entity);
            if (minValue != mMinValue) {
                mMinValue = minValue;
                mMinIndex = index;
            }
        }
    }

    public void calcPaddingValue() {
        if (mSideMode == DOUBLE_SIDE) {
            float paddingValue = Math.abs((mMaxValue - mMinValue) / (1.0f - 2 * mPaddingPercent) * mPaddingPercent);
            mMaxValue += paddingValue;
            mMinValue -= paddingValue;
        } else if (mSideMode == UP_SIDE) {
            float paddingValue = Math.abs((mMaxValue - mMinValue) / (1.0f - mPaddingPercent) * mPaddingPercent);
            mMaxValue += paddingValue;
        } else {
            float paddingValue = Math.abs((mMaxValue - mMinValue) / (1.0f - mPaddingPercent) * mPaddingPercent);
            mMinValue -= paddingValue;
        }
    }

    public float getMaxValue() {
        return mMaxValue;
    }

    public float getMinValue() {
        return mMinValue;
    }

    public int getMaxIndex() {
        return mMaxIndex;
    }

    public int getMinIndex() {
        return mMinIndex;
    }

    /**
     * 将实体与当前最大值进行最大值比较
     *
     * @param index       当前实体的索引，如果索引为-1，则表示为扩展数据
     * @param curMaxValue 当前最大值
     * @param entity      实体
     * @return 新的最大值
     */
    protected float calcMaxValue(int index, float curMaxValue, IEntity entity) {
        return curMaxValue;
    }

    /**
     * 将实体与当前最小值进行最小值比较
     *
     * @param index       当前实体的索引，如果索引为-1，则表示为扩展数据
     * @param curMinValue 当前最小值
     * @param entity      实体
     * @return 新的最小值
     */
    protected float calcMinValue(int index, float curMinValue, IEntity entity) {
        return curMinValue;
    }

    public abstract String getIndexTag();
}
