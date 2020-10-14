package com.yfw.kchartcore.touch;

import androidx.annotation.IntRange;
import androidx.annotation.Nullable;

import com.yfw.kchartcore.ScrollAndScaleView;
import com.yfw.kchartcore.adapter.BaseKChartAdapter;
import com.yfw.kchartcore.impl.IDataProvider;
import com.yfw.kchartcore.index.IEntity;

/**
 * @日期 : 2020/8/31
 * @描述 : 触碰事件管理者
 * 在{@link ScrollAndScaleView}无touch事件仍保留touch信息，以便实现非触碰状态下仍能获取之间的信息
 * 保存着【单次点击】【双击】【长按】的对应item信息
 * 注意，三者同一时刻只有一个有信息
 */
public class TouchTapManager<T extends IEntity> {

    private final IDataProvider mDataProvider;

    private final TapMarkerOption<T> mSingleTapOption = new TapMarkerOption<>();    // 单次点击
    private final TapMarkerOption<T> mDoubleTapOption = new TapMarkerOption<>();    // 双击
    private final TapMarkerOption<T> mLongTapOption = new TapMarkerOption<>();      // 长按

    private OnSingleSelectedChangeListener<T> mOnSingleSelectedChangeListener;
    private OnDoubleSelectedChangeListener<T> mOnDoubleSelectedChangeListener;
    private OnLongSelectedChangeListener<T> mOnLongSelectedChangeListener;

    public TouchTapManager(IDataProvider dataProvider) {
        mDataProvider = dataProvider;
    }

    /**
     * 设置单点信息
     */
    public void setSingleTapInfo(float x, float y, int index, T entity) {
        mSingleTapOption.setX(x);
        mSingleTapOption.setY(y);
        mSingleTapOption.setIndex(index);
        mSingleTapOption.setEntity(entity);
        updateClick(1);
        callSingleSelectedChange();
    }

    /**
     * 设置双击信息
     */
    public void setDoubleTapInfo(float x, float y, int index, T entity) {
        mDoubleTapOption.setX(x);
        mDoubleTapOption.setY(y);
        mDoubleTapOption.setIndex(index);
        mDoubleTapOption.setEntity(entity);
        updateClick(2);
        callDoubleSelectedChange();
    }

    /**
     * 设置长按信息
     */
    public void setLongTapInfo(float x, float y, int index, T entity) {
        mLongTapOption.setX(x);
        mLongTapOption.setY(y);
        mLongTapOption.setIndex(index);
        mLongTapOption.setEntity(entity);
        updateClick(3);
        callLongSelectedChange();
    }

    /**
     * 更新此时点击的对象，使同一时间只有一个类型的点击信息
     */
    private void updateClick(@IntRange(from = 1, to = 3) int type) {
        mSingleTapOption.setClick(type == 1);
        mDoubleTapOption.setClick(type == 2);
        mLongTapOption.setClick(type == 3);
    }

    /**
     * 移除所有点击信息
     */
    public void removeAllTapInfo() {
        mSingleTapOption.reset();
        mDoubleTapOption.reset();
        mLongTapOption.reset();
    }

    /**
     * 更新点击信息
     */
    public void updateClickTapInfo() {
        if (mSingleTapOption.isClick()) {
            updateClickTapInfo(mSingleTapOption);
            callSingleSelectedChange();
        }
        if (mDoubleTapOption.isClick()) {
            updateClickTapInfo(mDoubleTapOption);
            callDoubleSelectedChange();
        }
        if (mLongTapOption.isClick()) {
            updateClickTapInfo(mLongTapOption);
            callLongSelectedChange();
        }
    }

    /**
     * 通过遍历的方式更新此时点击信息所在的索引
     */
    private void updateClickTapInfo(TapMarkerOption<T> options) {
        BaseKChartAdapter<?> adapter = mDataProvider.getAdapter();
        if (adapter == null) return;
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).getTimeStamp() == options.getEntity().getTimeStamp()) {
                options.setIndex(i);
                return;
            }
        }
        options.reset();
    }

    /**
     * 更新点击信息索引偏移量
     */
    public void updateTapIndexOffset(int offset) {
        if (mSingleTapOption.isClick()) {
            mSingleTapOption.setIndex(mSingleTapOption.getIndex() + offset);
            callSingleSelectedChange();
        }
        if (mDoubleTapOption.isClick()) {
            mDoubleTapOption.setIndex(mDoubleTapOption.getIndex() + offset);
            callDoubleSelectedChange();
        }
        if (mLongTapOption.isClick()) {
            mLongTapOption.setIndex(mLongTapOption.getIndex() + offset);
            callLongSelectedChange();
        }
    }

    /**
     * 是否有单次点击事件
     */
    public boolean hasSingleTap() {
        return mSingleTapOption.isClick();
    }

    /**
     * 获取单次点击信息
     *
     * @return 如果没点击信息会返回空
     */
    @Nullable
    public TapMarkerOption<T> getSingleTapMarker() {
        if (mSingleTapOption.isClick()) {
            return mSingleTapOption;
        }
        return null;
    }

    /**
     * 是否有双击事件
     */
    public boolean hasDoubleTap() {
        return mDoubleTapOption.isClick();
    }

    /**
     * 获取双击信息
     *
     * @return 如果没点击信息会返回空
     */
    @Nullable
    public TapMarkerOption<T> getDoubleTapMarker() {
        if (mDoubleTapOption.isClick()) {
            return mDoubleTapOption;
        }
        return null;
    }

    /**
     * 是否有长按事件
     */
    public boolean hasLongTap() {
        return mLongTapOption.isClick();
    }

    /**
     * 获取长按信息
     *
     * @return 如果没点击信息会返回空
     */
    @Nullable
    public TapMarkerOption<T> getLongTapMarker() {
        if (mLongTapOption.isClick()) {
            return mLongTapOption;
        }
        return null;
    }

    /**
     * 如果{@link TapMarkerOption#canDispatch}返回true，则回调单次点击信息
     * 并调用{{@link TapMarkerOption#consumeDispatch}消费该回调
     */
    private void callSingleSelectedChange() {
        if (mSingleTapOption.canDispatch()) {
            mSingleTapOption.consumeDispatch();
            if (mOnSingleSelectedChangeListener != null) {
                mOnSingleSelectedChangeListener.onSingleSelectedChanged(
                        mSingleTapOption.getIndex(), mSingleTapOption.getEntity());
            }
        }
    }

    /**
     * 如果{@link TapMarkerOption#canDispatch}返回true，则回调双击信息
     * 并调用{{@link TapMarkerOption#consumeDispatch}消费该回调
     */
    private void callDoubleSelectedChange() {
        if (mDoubleTapOption.canDispatch()) {
            mDoubleTapOption.consumeDispatch();
            if (mOnDoubleSelectedChangeListener != null) {
                mOnDoubleSelectedChangeListener.onDoubleSelectedChanged(
                        mDoubleTapOption.getIndex(), mDoubleTapOption.getEntity());
            }
        }
    }

    /**
     * 如果{@link TapMarkerOption#canDispatch}返回true，则回调长按信息
     * 并调用{{@link TapMarkerOption#consumeDispatch}消费该回调
     */
    private void callLongSelectedChange() {
        if (mLongTapOption.canDispatch()) {
            mLongTapOption.consumeDispatch();
            if (mOnLongSelectedChangeListener != null) {
                mOnLongSelectedChangeListener.onLongleSelectedChanged(
                        mLongTapOption.getIndex(), mLongTapOption.getEntity());
            }
        }
    }

    /**
     * 设置单次点击监听
     */
    public void setOnSingleSelectedChangeListener(OnSingleSelectedChangeListener<T> listener) {
        mOnSingleSelectedChangeListener = listener;
    }

    /**
     * 设置双击监听
     */
    public void setOnDoubleSelectedChangeListener(OnDoubleSelectedChangeListener<T> listener) {
        mOnDoubleSelectedChangeListener = listener;
    }

    /**
     * 设置长按监听
     */
    public void setOnLongSelectedChangeListener(OnLongSelectedChangeListener<T> listener) {
        mOnLongSelectedChangeListener = listener;
    }

    public interface OnSingleSelectedChangeListener<T> {
        /**
         * 在单次点击或处于点击状态且索引有变化会触发该回调
         */
        void onSingleSelectedChanged(int index, T entity);
    }

    public interface OnDoubleSelectedChangeListener<T> {
        /**
         * 在双击或处于双击状态且索引有变化会触发该回调
         */
        void onDoubleSelectedChanged(int index, T entity);
    }

    public interface OnLongSelectedChangeListener<T> {
        /**
         * 在长按或处于长按状态且索引有变化会触发该回调
         */
        void onLongleSelectedChanged(int index, T entity);
    }
}
