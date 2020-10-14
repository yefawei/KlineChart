package com.yfw.kchartcore.data;


import com.yfw.kchartcore.InteractiveKChartView;
import com.yfw.kchartcore.index.IEntity;

/**
 * @日期 : 2020/8/24
 * @描述 : 满屏数据插入处理者
 */
public interface IDataInsertedHandler<T extends IEntity> {

    /**
     * 满屏状态下头部有插入新数据
     *
     * @param itemCount   变更数量
     * @param currScroll  当前滚动值
     * @param finalScroll 如果处于动画滚动状态的目标滚动值，否则与当前滚动值一致
     * @return true: 自行处理 false:交有库默认处理
     */
    boolean onFullFirstInserted(InteractiveKChartView<T> view, int itemCount, int currScroll, int finalScroll);

    /**
     * 满屏状态下尾部有插入新数据
     *
     * @param itemCount   变更数量
     * @param currScroll  当前滚动值
     * @param finalScroll 如果处于动画滚动状态的目标滚动值，否则与当前滚动值一致
     * @return true: 自行处理 false:交有库默认处理
     */
    boolean onFullLastInserted(InteractiveKChartView<T> view, int itemCount, int currScroll, int finalScroll);
}
