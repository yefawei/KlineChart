package com.yfw.kchartcore.index.range;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;

import com.yfw.kchartcore.data.Transformer;
import com.yfw.kchartcore.index.IEntity;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

/**
 * @日期 : 2020/7/9
 * @描述 : 指标基础计算类,计算{@link Transformer}所得的视图范围内最大最小值
 */
public abstract class IndexRange {

    public static final int NO_GROUP = -1;  // 指标计算类没有组标记

    public static final int DOUBLE_SIDE = 0;    // 含最大值最小值
    public static final int UP_SIDE = 1;        // 只含最大值
    public static final int DOWN_SIDE = 2;      // 只含最小值

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({DOUBLE_SIDE, UP_SIDE, DOWN_SIDE})
    public @interface SideMode {
    }

    private final boolean mReverse;   // 是否反向，最小值在上，最大值在下
    private final int mSideMode;
    private final float mPaddingPercent;
    private float mMaxValue = Float.MIN_VALUE;
    private float mMinValue = Float.MAX_VALUE;
    private int mMaxIndex; // 屏幕内最大值的索引
    private int mMinIndex; // 屏幕内最小值的索引

    private List<OnCalcValueListener> mOnCalcValueListeners;

    private final List<String> mExtendedKeys = new ArrayList<>();
    private final List<Object> mExtendedValues = new ArrayList<>();

    public IndexRange() {
        this(0);
    }

    public IndexRange(float paddingPercent) {
        this(false, IndexRange.DOUBLE_SIDE, paddingPercent, 0);
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

    /**
     * 重置数据
     */
    public final void resetValue(boolean isEmptyData) {
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
        dispatchResetValue(isEmptyData);
    }

    protected final void dispatchResetValue(boolean isEmptyData) {
        if (mOnCalcValueListeners != null) {
            for (int i = 0; i < mOnCalcValueListeners.size(); i++) {
                mOnCalcValueListeners.get(i).onResetValue(isEmptyData);
            }
        }
    }

    /**
     * 添加需要扩展计算的数据
     */
    public final void addExtendedCalcData(String key, Object obj) {
        mExtendedKeys.add(key);
        mExtendedValues.add(obj);
    }

    /**
     * 移除指定扩展计算数据
     */
    public final void removeExtendedCalcData(String key) {
        for (int i = 0; i < mExtendedKeys.size(); i++) {
            if (mExtendedKeys.get(i).equals(key)) {
                mExtendedKeys.remove(i);
                mExtendedValues.remove(i);
                return;
            }
        }
    }

    /**
     * 移除所有扩展计算数据
     */
    public final void clearExtendedCalcData() {
        mExtendedKeys.clear();
        mExtendedValues.clear();
    }

    /**
     * 计算扩展数据
     */
    public final void calcExtendedData() {
        for (int i = 0; i < mExtendedValues.size(); i++) {
            if (mSideMode == DOUBLE_SIDE) {
                mMaxValue = calcExtendedMaxValue(mMaxValue, mExtendedKeys.get(i), mExtendedValues.get(i));
                mMinValue = calcExtendedMinValue(mMinValue, mExtendedKeys.get(i), mExtendedValues.get(i));
            } else if (mSideMode == UP_SIDE) {
                mMaxValue = calcExtendedMaxValue(mMaxValue, mExtendedKeys.get(i), mExtendedValues.get(i));
            } else {
                mMinValue = calcExtendedMinValue(mMinValue, mExtendedKeys.get(i), mExtendedValues.get(i));
            }
        }
    }

    public final void calcMinMaxValue(int index, IEntity entity) {
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

    /**
     * 计算上下边界预留的空间
     */
    public final void calcPaddingValue() {
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

    /**
     * 纠正值一致性问题
     */
    public final void correctedValueConsistency() {
        if (mMaxValue == mMinValue) {
            handleValueConsistency();
        }
    }

    /**
     * 自定义最大最小值
     */
    public final void customMaxMinValue(float maxValue, float minValue) {
        mMaxValue = maxValue;
        mMinValue = minValue;
    }

    /**
     * 计算结束
     */
    public final void calcValueEnd(boolean isEmptyData) {
        dispatchCalcValueEnd(isEmptyData);
    }

    protected final void dispatchCalcValueEnd(boolean isEmptyData) {
        if (mOnCalcValueListeners != null) {
            for (int i = 0; i < mOnCalcValueListeners.size(); i++) {
                mOnCalcValueListeners.get(i).onCalcValueEnd(isEmptyData);
            }
        }
    }

    /**
     * 处理值一致性问题
     */
    public void handleValueConsistency() {
        if (mMaxValue == 0) {
            mMaxValue = 100.0f;
            mMinValue = -100.0f;
        } else if (mMaxValue > 0) {
            mMaxValue = mMaxValue * 2.0f;
            mMinValue = 0;
        } else {
            mMaxValue = 0;
            mMinValue = mMinValue * 2.0f;
        }
    }

    /**
     * 当前页面的最大值
     * 如果是{@link IndexRange#DOUBLE_SIDE}和{@link IndexRange#UP_SIDE}则已包含预留空间
     */
    public float getMaxValue() {
        return mMaxValue;
    }

    /**
     * 当前页面的最小值
     * 如果是{@link IndexRange#DOUBLE_SIDE}和{@link IndexRange#DOWN_SIDE}则已包含预留空间
     */
    public float getMinValue() {
        return mMinValue;
    }

    /**
     * 当前页面的最大值索引
     */
    public int getMaxIndex() {
        return mMaxIndex;
    }

    /**
     * 当前页面的最小值索引
     */
    public int getMinIndex() {
        return mMinIndex;
    }

    /**
     * 计算扩展数据最大值
     */
    protected float calcExtendedMaxValue(float curMaxValue, String key, Object obj) {
        return curMaxValue;
    }

    /**
     * 计算扩展数据最小值
     */
    protected float calcExtendedMinValue(float curMinValue, String key, Object obj) {
        return curMinValue;
    }

    /**
     * 将实体与当前最大值进行最大值比较
     *
     * @param index       当前实体的索引
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
     * @param index       当前实体的索引
     * @param curMinValue 当前最小值
     * @param entity      实体
     * @return 新的最小值
     */
    protected float calcMinValue(int index, float curMinValue, IEntity entity) {
        return curMinValue;
    }

    public void addOnCalcValueListener(@NonNull OnCalcValueListener listener) {
        if (mOnCalcValueListeners == null) {
            mOnCalcValueListeners = new ArrayList<>();
        }
        mOnCalcValueListeners.add(listener);
    }

    public void removerOnCalcValueListener(@NonNull OnCalcValueListener listener) {
        if (mOnCalcValueListeners != null) {
            mOnCalcValueListeners.remove(listener);
        }
    }

    public void clearOnCalcValueListener() {
        if (mOnCalcValueListeners != null) {
            mOnCalcValueListeners.clear();
        }
    }

    /**
     * 获取tag标记，区分不同的指标
     * 方便{@link Transformer#addIndexRange(int, IndexRange)}和{@link Transformer#removeIndexRange(int, IndexRange)}的使用
     * 返回空或者null将不会被{@link Transformer#addIndexRange(int, IndexRange)}所添加
     */
    public abstract String getIndexTag();

    public interface OnCalcValueListener {
        /**
         * 重置数据
         *
         * @param isEmptyData true:为空数据重置数据回调 false:为数据有变更，计算前的重置
         */
        void onResetValue(boolean isEmptyData);

        /**
         * 计算结束
         *
         * @param isEmptyData true:为空数据计算结束回调 false:为数据有变更，计算前结束的回调
         */
        void onCalcValueEnd(boolean isEmptyData);
    }
}
