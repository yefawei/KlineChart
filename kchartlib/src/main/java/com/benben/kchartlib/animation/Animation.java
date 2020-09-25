package com.benben.kchartlib.animation;

/**
 * @日期 : 2020/7/10
 * @描述 :
 */
public interface Animation {

    /**
     * 是否是自动动画
     */
    boolean isAutoAnim();

    /**
     * 非自动动画开始时间
     */
    long AnimStartTime();

    /**
     * 非自动动画结束时间
     */
    long AnimEndTime();

    /**
     * 设置是否在动画中
     */
    void setInAnim(boolean inAnim);

    /**
     * 是否在动画中
     */
    boolean inAnim();
}
