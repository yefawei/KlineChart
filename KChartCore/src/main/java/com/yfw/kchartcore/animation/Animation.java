package com.yfw.kchartcore.animation;

/**
 * @日期 : 2020/7/10
 * @描述 : 动画接口
 */
public interface Animation {

    /**
     * 是否在{@link AnimationManager}里
     */
    boolean inAnimationManager();

    /**
     * {@link AnimationManager}添加和移除会回调该函数
     * @param in true 被添加 false 被移除
     */
    void callInAnimation(boolean in);

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
     * 由{@link AnimationManager#animUpdate()}统一更新动画时间
     */
    void updateAnimProcessTime(long time);
}
