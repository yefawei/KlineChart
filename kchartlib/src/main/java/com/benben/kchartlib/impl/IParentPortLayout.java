package com.benben.kchartlib.impl;

/**
 * @日期 : 2020/7/13
 * @描述 : 父视图布局
 */
public interface IParentPortLayout {

    /**
     * 设置是否是在更新子布局
     */
    void setInUpdateChildLayout(boolean inUpdate);

    /**
     * 是否在更新子布局
     */
    boolean inUpdateChildLayout();

    /**
     * 更新子布局
     */
    void updateChildLayout();
}
