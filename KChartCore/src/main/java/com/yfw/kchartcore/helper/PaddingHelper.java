package com.yfw.kchartcore.helper;

/**
 * @日期 : 2020/8/24
 * @描述 : 边界辅助类
 */
public class PaddingHelper {

    private float mBufferRightExtPaddingScale;
    private int mBufferRightExtPadding = 0;
    private boolean mRightExtPaddingFollowScale = false;    // 右扩展边界是否跟随缩放
    private int mRightExtPadding = 0;                       // 右扩展边界

    private float mBufferLeftExtPaddingScale;
    private int mBufferLeftExtPadding = 0;
    private boolean mLeftExtPaddingFollowScale = false;    // 左扩展边界是否跟随缩放
    private int mLeftExtPadding = 0;                       // 左扩展边界

    /**
     * 右扩展边界是否跟随缩放
     */
    public boolean isRightExtPaddingFollowScale() {
        return mRightExtPaddingFollowScale;
    }

    /**
     * 是否有右扩展边界
     */
    public boolean hasRightExtPadding() {
        return mRightExtPadding != 0;
    }

    /**
     * 设置右侧扩展空间
     * @param rightExtPadding 扩展空间
     * @param rightExtPaddingFollowScale 如果为true，则扩展空间随缩放值一并缩放
     */
    public void setRightExtPadding(int rightExtPadding, boolean rightExtPaddingFollowScale) {
        if (rightExtPadding < 0) {
            rightExtPadding = 0;
        }
        mRightExtPadding = rightExtPadding;
        mRightExtPaddingFollowScale = rightExtPaddingFollowScale;
    }

    /**
     * 获取右侧扩展空间
     * @param scale 当前缩放值
     */
    public int getRightExtPadding(float scale) {
        if (mRightExtPaddingFollowScale) {
            if (mBufferRightExtPaddingScale == scale) {
                return mBufferRightExtPadding;
            }
            mBufferRightExtPaddingScale = scale;
            return mBufferRightExtPadding = Math.round(mRightExtPadding * scale);
        }
        return mRightExtPadding;
    }

    /**
     * 左扩展边界是否跟随缩放
     */
    public boolean isLeftExtPaddingFollowScale() {
        return mLeftExtPaddingFollowScale;
    }

    /**
     * 是否有左扩展边界
     */
    public boolean hasLeftExtPadding() {
        return mLeftExtPadding != 0;
    }

    /**
     * 设置左侧扩展空间
     * @param leftExtPadding 扩展空间
     * @param leftExtPaddingFollowScale 如果为true，则扩展空间随缩放值一并缩放
     */
    public void setLeftExtPadding(int leftExtPadding, boolean leftExtPaddingFollowScale) {
        if (leftExtPadding < 0) {
            leftExtPadding = 0;
        }
        mLeftExtPadding = leftExtPadding;
        mLeftExtPaddingFollowScale = leftExtPaddingFollowScale;
    }

    /**
     * 获取左侧扩展空间
     * @param scale 当前缩放值
     */
    public int getLeftExtPadding(float scale) {
        if (mLeftExtPaddingFollowScale) {
            if (mBufferLeftExtPaddingScale == scale) {
                return mBufferLeftExtPadding;
            }
            mBufferLeftExtPaddingScale = scale;
            return mBufferLeftExtPadding = Math.round(mLeftExtPadding * scale);
        }
        return mLeftExtPadding;
    }
}
