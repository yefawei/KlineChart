package com.benben.kchartlib.adapter;

import androidx.annotation.Nullable;

import com.benben.kchartlib.index.IEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * @日期 : 2020/8/21
 * @描述 : 默认K线适配器，内部已实现添加数据的逻辑
 */
public class DefaultKChartAdapter extends BaseKChartAdapter<IEntity> {

    private OnPrepareIndexDataListener mListener;

    private List<IEntity> mKlineInfos;
    private long mStartTime;
    private long mEndTime;

    public DefaultKChartAdapter(@Nullable OnPrepareIndexDataListener listener) {
        mListener = listener;
    }

    @Override
    public int getCount() {
        return mKlineInfos == null ? 0 : mKlineInfos.size();
    }

    @Override
    public IEntity getItem(int position) {
        return mKlineInfos.get(position);
    }

    @Override
    public void prepareIndexData(Set<String> indexTags) {
        if (mListener != null) {
            mListener.prepareIndexData(indexTags);
        }
    }

    public void clear() {
        mKlineInfos = null;
        mStartTime = -1;
        mEndTime = -1;
        notifyDataSetChanged();
    }

    public void addData(IEntity data) {
        if (data == null) return;
        if (mKlineInfos == null) {
            initAndAddSingleData(data);
        } else {
            addSingleData(data);
        }
    }

    public void addDatas(IEntity... datas) {
        if (datas.length == 0) return;
        if (datas.length == 1) {
            addData(datas[0]);
            return;
        }
        if (mKlineInfos == null) {
            initAndAddMultiData(datas);
        } else {
            addMultiData(datas);
        }
    }

    public void addDatas(ArrayList<? extends IEntity> datas) {
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
    private void initAndAddSingleData(IEntity data) {
        mKlineInfos = new ArrayList<>();
        mKlineInfos.add(data);
        mStartTime = data.getDatatime();
        mEndTime = data.getDatatime();
        notifyDataSetChanged();
    }

    /**
     * 初始化列表并添加多个数据
     */
    private void initAndAddMultiData(IEntity... datas) {
        mKlineInfos = new ArrayList<>();
        Collections.addAll(mKlineInfos, datas);
        mStartTime = datas[0].getDatatime();
        mEndTime = datas[datas.length - 1].getDatatime();
        notifyDataSetChanged();
    }

    @SuppressWarnings("unchecked")
    private void initAndAddMultiData(List<? extends IEntity> datas) {
        mKlineInfos = (List<IEntity>) datas;
        mStartTime = datas.get(0).getDatatime();
        mEndTime = datas.get(datas.size() - 1).getDatatime();
        notifyDataSetChanged();
    }

    /**
     * 添加单个数据
     * 数据时间小于开始时间，则添加到头部
     * 数据时间等于开始时间，则更新尾部
     * 数据时间大于开始时间，则添加到尾部
     * 否则,不更新数据
     */
    private void addSingleData(IEntity data) {
        long datatime = data.getDatatime();
        if (datatime < mStartTime) {
            mKlineInfos.add(0, data);
            mStartTime = datatime;
            notifyFirstInserted(1);
        } else if (datatime == mEndTime) {
            mKlineInfos.set(mKlineInfos.size() - 1, data);
            notifyLastUpdated();
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
    private void addMultiData(IEntity... datas) {
        long startTime = datas[0].getDatatime();
        long endTime = datas[datas.length - 1].getDatatime();
        if (endTime < mStartTime) {
            mKlineInfos.addAll(0, Arrays.asList(datas));
            mStartTime = startTime;
            notifyFirstInserted(datas.length);
        } else if (startTime < mStartTime && endTime <= mEndTime) {
            int endIndex = 0;
            for (int i = 0; i < datas.length; i++) {
                if (datas[i].getDatatime() >= mStartTime) {
                    break;
                }
                endIndex = i;
            }
            IEntity[] newDatas = Arrays.copyOf(datas, endIndex + 1);
            mKlineInfos.addAll(0, Arrays.asList(newDatas));
            mStartTime = startTime;
            notifyFirstInserted(newDatas.length);
        } else if (startTime >= mStartTime && endTime > mEndTime) {
            int startIndex = 0;
            for (int i = 0; i < datas.length; i++) {
                if (datas[i].getDatatime() > mEndTime) {
                    startIndex = i;
                    break;
                }
            }
            IEntity[] newDatas = Arrays.copyOfRange(datas, startIndex, datas.length);
            mKlineInfos.addAll(Arrays.asList(newDatas));
            mEndTime = endTime;
            notifyLastInserted(newDatas.length);
        } else if (startTime > mEndTime) {
            mKlineInfos.addAll(Arrays.asList(datas));
            mEndTime = endTime;
            notifyLastInserted(datas.length);
        } else if (startTime < mStartTime && endTime > mEndTime) {
            initAndAddMultiData(datas);
        }
    }

    private void addMultiData(ArrayList<? extends IEntity> datas) {
        long startTime = datas.get(0).getDatatime();
        long endTime = datas.get(datas.size() - 1).getDatatime();
        if (endTime < mStartTime) {
            mKlineInfos.addAll(0, datas);
            mStartTime = startTime;
            notifyFirstInserted(datas.size());
        } else if (startTime < mStartTime && endTime <= mEndTime) {
            int endIndex = 0;
            for (int i = 0; i < datas.size(); i++) {
                if (datas.get(i).getDatatime() >= mStartTime) {
                    break;
                }
                endIndex = i;
            }
            List<? extends IEntity> newDatas = datas.subList(0, endIndex + 1);
            mKlineInfos.addAll(0, newDatas);
            mStartTime = startTime;
            notifyFirstInserted(newDatas.size());
        } else if (startTime >= mStartTime && endTime > mEndTime) {
            int startIndex = 0;
            for (int i = 0; i < datas.size(); i++) {
                if (datas.get(i).getDatatime() > mEndTime) {
                    startIndex = i;
                    break;
                }
            }
            List<? extends IEntity> newDatas = datas.subList(startIndex, datas.size());
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

    public interface OnPrepareIndexDataListener {
        void prepareIndexData(Set<String> indexTags);
    }
}
