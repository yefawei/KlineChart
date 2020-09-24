package com.benben.kchartlib.touch;

import androidx.annotation.Nullable;

import com.benben.kchartlib.adapter.BaseKChartAdapter;
import com.benben.kchartlib.impl.IDataProvider;
import com.benben.kchartlib.index.IEntity;

/**
 * @日期 : 2020/8/31
 * @描述 : 点击管理者
 * 保存着【单次点击】【双击】【长按】的对应item信息
 * 注意，三者同一时刻只有一个有信息
 */
public class TouchTapManager {

    private IDataProvider mDataProvider;

    private TapMarkerOption mSingleTapOption = new TapMarkerOption();    // 单次点击
    private TapMarkerOption mDoubleTapOption = new TapMarkerOption();    // 双击
    private TapMarkerOption mLongTapOption = new TapMarkerOption();      // 长按

    private OnSingleSelectedChangeListener mOnSingleSelectedChangeListener;
    private OnDoubleSelectedChangeListener mOnDoubleSelectedChangeListener;
    private OnLongSelectedChangeListener mOnLongSelectedChangeListener;

    public TouchTapManager(IDataProvider dataProvider) {
        mDataProvider = dataProvider;
    }

    public void onSingleTapInfo(float x, float y, int index, IEntity entity) {
        mSingleTapOption.setX(x);
        mSingleTapOption.setY(y);
        mSingleTapOption.setIndex(index);
        mSingleTapOption.setEntity(entity);
        updateClick(1);
        callSingleSelectedChange();
    }

    public void onDoubleTapInfo(float x, float y, int index, IEntity entity) {
        mDoubleTapOption.setX(x);
        mDoubleTapOption.setY(y);
        mDoubleTapOption.setIndex(index);
        mDoubleTapOption.setEntity(entity);
        updateClick(2);
        callDoubleSelectedChange();
    }

    public void onLongTapInfo(float x, float y, int index, IEntity entity) {
        mLongTapOption.setX(x);
        mLongTapOption.setY(y);
        mLongTapOption.setIndex(index);
        mLongTapOption.setEntity(entity);
        updateClick(3);
        callLongSelectedChange();
    }

    private void updateClick(int type) {
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
        }
        if (mDoubleTapOption.isClick()) {
            updateClickTapInfo(mDoubleTapOption);
        }
        if (mLongTapOption.isClick()) {
            updateClickTapInfo(mLongTapOption);
        }
    }

    private void updateClickTapInfo(TapMarkerOption options) {
        BaseKChartAdapter adapter = mDataProvider.getAdapter();
        if (adapter == null) return;
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).getDatatime() == options.getEntity().getDatatime()) {
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
        }
        if (mDoubleTapOption.isClick()) {
            mDoubleTapOption.setIndex(mDoubleTapOption.getIndex() + offset);
        }
        if (mLongTapOption.isClick()) {
            mLongTapOption.setIndex(mLongTapOption.getIndex() + offset);
        }
    }

    public boolean hasSingleTap() {
        return mSingleTapOption.isClick();
    }

    /**
     * 获取单次点击信息
     *
     * @return 如果没点击信息会返回空
     */
    @Nullable
    public TapMarkerOption getSingleTapMarker() {
        if (mSingleTapOption.isClick()) {
            return mSingleTapOption;
        }
        return null;
    }

    public boolean hasDoubleTap() {
        return mDoubleTapOption.isClick();
    }

    /**
     * 获取双击信息
     *
     * @return 如果没点击信息会返回空
     */
    @Nullable
    public TapMarkerOption getDoubleTapMarker() {
        if (mDoubleTapOption.isClick()) {
            return mDoubleTapOption;
        }
        return null;
    }

    /**
     *
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
    public TapMarkerOption getLongTapMarker() {
        if (mLongTapOption.isClick()) {
            return mLongTapOption;
        }
        return null;
    }

    private void callSingleSelectedChange() {
        if (mSingleTapOption.canDispatch()) {
            mSingleTapOption.consumeDispatch();
            if (mOnSingleSelectedChangeListener != null) {
                mOnSingleSelectedChangeListener.onSingleSelectedChanged(
                        mSingleTapOption.getIndex(), mSingleTapOption.getEntity());
            }
        }
    }

    private void callDoubleSelectedChange() {
        if (mDoubleTapOption.canDispatch()) {
            mDoubleTapOption.consumeDispatch();
            if (mOnDoubleSelectedChangeListener != null) {
                mOnDoubleSelectedChangeListener.onDoubleSelectedChanged(
                        mDoubleTapOption.getIndex(), mDoubleTapOption.getEntity());
            }
        }
    }

    private void callLongSelectedChange() {
        if (mLongTapOption.canDispatch()) {
            mLongTapOption.consumeDispatch();
            if (mOnLongSelectedChangeListener != null) {
                mOnLongSelectedChangeListener.onLongleSelectedChanged(
                        mLongTapOption.getIndex(), mLongTapOption.getEntity());
            }
        }
    }

    public void setOnSingleSelectedChangeListener(OnSingleSelectedChangeListener listener) {
        mOnSingleSelectedChangeListener = listener;
    }

    public void setOnDoubleSelectedChangeListener(OnDoubleSelectedChangeListener listener) {
        mOnDoubleSelectedChangeListener = listener;
    }

    public void setOnLongSelectedChangeListener(OnLongSelectedChangeListener listener) {
        mOnLongSelectedChangeListener = listener;
    }

    public interface OnSingleSelectedChangeListener {
        void onSingleSelectedChanged(int index, IEntity entity);
    }

    public interface OnDoubleSelectedChangeListener {
        void onDoubleSelectedChanged(int index, IEntity entity);
    }

    public interface OnLongSelectedChangeListener {
        void onLongleSelectedChanged(int index, IEntity entity);
    }
}
