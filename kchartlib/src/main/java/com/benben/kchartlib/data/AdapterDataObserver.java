package com.benben.kchartlib.data;

/**
 * @日期 : 2020/8/21
 * @描述 : 数据更新观察者
 */
public class AdapterDataObserver {

    /**
     * 更新所有数据
     * @param allCount 总数量
     */
    public void onChanged(int allCount) {

    }

    /**
     * 头部有插入新数据
     * @param insertedCount 前端插入数量
     */
    public void onFirstInserted(int insertedCount) {

    }

    /**
     * 最后一个数据有更新
     * @param index 最后的索引
     */
    public void onLastUpdated(int index) {

    }

    /**
     * 尾部有插入新数据
     * @param insertedCount 末尾插入数量
     */
    public void onLastInserted(int insertedCount) {

    }
}
