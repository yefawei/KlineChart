package com.benben.kchartlib.animation;

/**
 * @日期 : 2020/7/10
 * @描述 :
 */
public interface Animation {

    /**
     * 动画管理者添加和移除会回调该函数
     * @param in true 被添加 false 被移除
     */
    void inAnimationCall(boolean in);

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
