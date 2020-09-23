package com.benben.kchartlib.overlay;

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

    private TapMarkerOptions mSingleTapOptions = new TapMarkerOptions();    // 单次点击
    private TapMarkerOptions mDoubleTapOptions = new TapMarkerOptions();    // 双击
    private TapMarkerOptions mLongTapOptions = new TapMarkerOptions();      // 长按

    public TouchTapManager(IDataProvider dataProvider) {
        mDataProvider = dataProvider;
    }

    public void onSingleTapInfo(float x, float y, int index, IEntity entity) {
        mSingleTapOptions.setX(x);
        mSingleTapOptions.setY(y);
        mSingleTapOptions.setIndex(index);
        mSingleTapOptions.setEntity(entity);

        mSingleTapOptions.setClick(true);
        mDoubleTapOptions.setClick(false);
        mLongTapOptions.setClick(false);
    }

    public void onDoubleTapInfo(float x, float y, int index, IEntity entity) {
        mDoubleTapOptions.setX(x);
        mDoubleTapOptions.setY(y);
        mDoubleTapOptions.setIndex(index);
        mDoubleTapOptions.setEntity(entity);

        mSingleTapOptions.setClick(false);
        mDoubleTapOptions.setClick(true);
        mLongTapOptions.setClick(false);
    }

    public void onLongTapInfo(float x, float y, int index, IEntity entity) {
        mLongTapOptions.setX(x);
        mLongTapOptions.setY(y);
        mLongTapOptions.setIndex(index);
        mLongTapOptions.setEntity(entity);

        mSingleTapOptions.setClick(false);
        mDoubleTapOptions.setClick(false);
        mLongTapOptions.setClick(true);
    }

    /**
     * 移除所有点击信息
     */
    public void removeAllTapInfo() {
        mSingleTapOptions.reset();
        mDoubleTapOptions.reset();
        mLongTapOptions.reset();
    }

    /**
     * 更新点击信息
     */
    public void updateClickTapInfo() {
        if (mSingleTapOptions.isClick()) {
            updateClickTapInfo(mSingleTapOptions);
        }
        if (mDoubleTapOptions.isClick()) {
            updateClickTapInfo(mDoubleTapOptions);
        }
        if (mLongTapOptions.isClick()) {
            updateClickTapInfo(mLongTapOptions);
        }
    }

    /**
     * 更新点击信息索引偏移量
     */
    public void updateTapIndexOffset(int offset) {
        if (mSingleTapOptions.isClick()) {
            mSingleTapOptions.setIndex(mSingleTapOptions.getIndex() + offset);
        }
        if (mDoubleTapOptions.isClick()) {
            mDoubleTapOptions.setIndex(mDoubleTapOptions.getIndex() + offset);
        }
        if (mLongTapOptions.isClick()) {
            mLongTapOptions.setIndex(mLongTapOptions.getIndex() + offset);
        }
    }

    private void updateClickTapInfo(TapMarkerOptions options) {
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


    public boolean hasSingleTap() {
        return mSingleTapOptions.isClick();
    }

    /**
     * 获取单次点击信息
     * @return 如果没点击信息会返回空
     */
    @Nullable
    public TapMarkerOptions getSingleTapMarker() {
        if (mSingleTapOptions.isClick()) {
            return mSingleTapOptions;
        }
        return null;
    }

    public boolean hasDoubleTap() {
        return mDoubleTapOptions.isClick();
    }

    /**
     * 获取双击信息
     * @return 如果没点击信息会返回空
     */
    @Nullable
    public TapMarkerOptions getDoubleTapMarker() {
        if (mDoubleTapOptions.isClick()) {
            return mDoubleTapOptions;
        }
        return null;
    }

    /**
     *
     */
    public boolean hasLongTap() {
        return mLongTapOptions.isClick();
    }

    /**
     * 获取长按信息
     * @return 如果没点击信息会返回空
     */
    @Nullable
    public TapMarkerOptions getLongTapMarker() {
        if (mLongTapOptions.isClick()) {
            return mLongTapOptions;
        }
        return null;
    }
}
