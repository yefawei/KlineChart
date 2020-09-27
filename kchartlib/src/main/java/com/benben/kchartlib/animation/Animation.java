package com.benben.kchartlib.animation;

/**
 * @日期 : 2020/7/10
 * @描述 :
 */
public interface Animation {


    boolean inAnimationManager();

    /**
     * {@link AnimationManager}添加和移除会回调该函数
     * @param in true 被添加 false 被移除
     */
    void inAnimationCall(boolean in);

    /**
     * 是否是自动动画
     */
    boolean isAutoAnim();

    /**
     * 动画开始时间
     */
    long getAnimStartTime();

    /**
     * 动画结束时间
     */
    long getAnimEndTime();

    /**
     * 设置是否在动画中
     */
    void setInAnim(boolean inAnim);

    /**
     * 是否在动画中
     */
    boolean inAnim();

    /**
     * 是否在动画时间内
     */
    boolean inAnimTime();

    /**
     * 更新动画时间
     */
    void updateAnimProcessTime(long time);
}
