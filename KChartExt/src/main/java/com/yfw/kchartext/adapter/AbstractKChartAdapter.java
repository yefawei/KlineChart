package com.yfw.kchartext.adapter;

import com.yfw.kchartcore.adapter.BaseKChartAdapter;
import com.yfw.kchartcore.index.IEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * @日期 : 2020/8/21
 * @描述 : 内部已实现添加数据逻辑的适配器
 */
public abstract class AbstractKChartAdapter<T extends IEntity> extends BaseKChartAdapter<T> {

    protected List<T> mKlineInfos;
    private long mStartTime;
    private long mEndTime;

    @Override
    public int getCount() {
        return mKlineInfos == null ? 0 : mKlineInfos.size();
    }

    @Override
    public T getItem(int position) {
        return mKlineInfos.get(position);
    }

    public void clear() {
        int oldCount = getCount();
        mKlineInfos = null;
        mStartTime = -1;
        mEndTime = -1;
        if (oldCount > 0) {
            notifyDataSetChanged();
        }
    }

    public void addData(T data) {
        if (data == null) return;
        if (mKlineInfos == null) {
            initAndAddSingleData(data);
        } else {
            addSingleData(data);
        }
    }

    public void addDatas(List<T> datas) {
        if (datas == null || datas.isEmpty()) return;
        if (mKlineInfos == null) {
            initAndAddMultiData(datas);
        } else {
            addMultiData(datas);
        }
    }

    /**
     * 初始化列表并添加单个数据
     */
    private void initAndAddSingleData(T data) {
        mKlineInfos = new ArrayList<>();
        mKlineInfos.add(data);
        mStartTime = data.getTimeStamp();
        mEndTime = data.getTimeStamp();
        notifyDataSetChanged();
    }

    private void initAndAddMultiData(List<T> datas) {
        mKlineInfos = datas;
        mStartTime = datas.get(0).getTimeStamp();
        mEndTime = datas.get(datas.size() - 1).getTimeStamp();
        notifyDataSetChanged();
    }

    /**
     * 添加单个数据
     * 数据时间小于开始时间，则添加到头部
     * 数据时间等于开始时间，则更新尾部
     * 数据时间大于开始时间，则添加到尾部
     * 否则,不更新数据
     */
    private void addSingleData(T data) {
        long datatime = data.getTimeStamp();
        if (datatime < mStartTime) {
            mKlineInfos.add(0, data);
            mStartTime = datatime;
            notifyFirstInserted(1);
        } else if (datatime == mEndTime) {
            mKlineInfos.set(mKlineInfos.size() - 1, data);
            notifyLastUpdated(mKlineInfos.size() - 1);
        } else if (datatime > mEndTime) {
            mKlineInfos.add(data);
            mEndTime = datatime;
            notifyLastInserted(1);
        }
    }

    /**
     * 添加多个数据
     * 结束时间小于现有开始时间，将所有数据添加到头部
     * 开始时间小于现有开始时间且结束时间小于等于现有结束时间，将截取前段数据并添加到头部
     * 开始时间大于等于现有开始时间且结束时间大于现有结束时间，将截取后段数据并添加到尾部
     * 开始时间大于现有结束时间，将所有数据添加到尾部
     * 开始时间小于现有开始时间，结束时间大于现有结束时间，将重新更新数据（不推荐该方案，传数据时留意）
     * 否者，传入的数据将不起作用
     */
    private void addMultiData(List<T> datas) {
        long startTime = datas.get(0).getTimeStamp();
        long endTime = datas.get(datas.size() - 1).getTimeStamp();
        if (endTime < mStartTime) {
            mKlineInfos.addAll(0, datas);
            mStartTime = startTime;
            notifyFirstInserted(datas.size());
        } else if (startTime < mStartTime && endTime <= mEndTime) {
            int endIndex = 0;
            for (int i = 0; i < datas.size(); i++) {
                if (datas.get(i).getTimeStamp() >= mStartTime) {
                    break;
                }
                endIndex = i;
            }
            List<T> newDatas = datas.subList(0, endIndex + 1);
            mKlineInfos.addAll(0, newDatas);
            mStartTime = startTime;
            notifyFirstInserted(newDatas.size());
        } else if (startTime >= mStartTime && endTime > mEndTime) {
            int startIndex = 0;
            for (int i = 0; i < datas.size(); i++) {
                if (datas.get(i).getTimeStamp() > mEndTime) {
                    startIndex = i;
                    break;
                }
            }
            List<T> newDatas = datas.subList(startIndex, datas.size());
            mKlineInfos.addAll(newDatas);
            mEndTime = endTime;
            notifyLastInserted(newDatas.size());
        } else if (startTime > mEndTime) {
            mKlineInfos.addAll(datas);
            mEndTime = endTime;
            notifyLastInserted(datas.size());
        } else if (startTime < mStartTime && endTime > mEndTime) {
            initAndAddMultiData(datas);
        }
    }
}
