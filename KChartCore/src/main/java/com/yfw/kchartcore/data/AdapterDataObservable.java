package com.yfw.kchartcore.data;

import android.database.Observable;

/**
 * @日期 : 2020/8/21
 * @描述 : 适配器数据变化观察者维护类
 */
public class AdapterDataObservable extends Observable<AdapterDataObserver> {

    /**
     * 通知更新所有数据
     */
    public void notifyChanged(int count) {
        for (AdapterDataObserver observer : mObservers) {
            observer.onChanged(count);
        }
    }

    /**
     * 通知头部插入新数据
     */
    public void notifyFirstInserted(int itemCount) {
        for (AdapterDataObserver observer : mObservers) {
            observer.onFirstInserted(itemCount);
        }
    }

    /**
     * 通知最后一个数据有更新
     */
    public void notifyLastUpdated(int index) {
        for (AdapterDataObserver observer : mObservers) {
            observer.onLastUpdated(index);
        }
    }

    /**
     * 通知尾部有插入新数据
     */
    public void notifyLastInserted(int itemCount) {
        for (AdapterDataObserver observer : mObservers) {
            observer.onLastInserted(itemCount);
        }
    }
}
